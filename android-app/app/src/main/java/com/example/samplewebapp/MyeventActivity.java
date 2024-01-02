package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyeventActivity extends AppCompatActivity {

    JSONObject event;
    String username;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);

        username = getIntent().getStringExtra("username");

        try {
            event = new JSONObject(getIntent().getStringExtra("event"));
        } catch (JSONException e) {
        }

        TextView title = findViewById(R.id.title);
        TextView category = findViewById(R.id.category);
        TextView host = findViewById(R.id.host);
        TextView location = findViewById(R.id.location);
        TextView date = findViewById(R.id.date);
        TextView time = findViewById(R.id.time);
        TextView bring = findViewById(R.id.toBring);
        TextView audience = findViewById(R.id.audience);
        TextView access = findViewById(R.id.accessibility);
        TextView description = findViewById(R.id.description);

        try {
            title.setText(event.getString("title"));
            category.setText(event.getString("category"));
            host.setText(event.getString("host"));
            location.setText(event.getString("location"));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInfo = format.parse(event.getString("date"));
            date.setText(format.format(dateInfo));

            time.setText(event.getString("startTime") + " - " + event.getString("endTime"));
            bring.setText(event.getString("toBring"));
            audience.setText(event.getString("intendedAudience"));
            access.setText(event.getString("accessibilityInfo"));
            description.setText(event.getString("additionalDescription"));

        } catch (JSONException | ParseException e) {
        }


    }

    public void onGoBackButtonClick(View v) {
        finish();
    }

    public void onEditButtonClick(View v) {
        Intent i = new Intent(this, EditActivity.class);
        i.putExtra("event", event.toString());
        i.putExtra("username", username);
        startActivity(i);
    }

    public void onDeleteButtonClick(View v) {
        try {
            String titleText = event.getString("title");
            String u = "http://10.0.2.2:3006/appDeleteEvent?title=" + titleText;

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL(u);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            if (jo.getBoolean("result")) {
                                message = "Success!";
                            }

                            else {
                                message = "Could not delete event";
                            }


                            // need to set the instance variable in the Activity object
                            // because we cannot directly access the TextView from here

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            message = "Could not delete event";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            Toast.makeText(
                    this, message,Toast.LENGTH_LONG).show();



        }
        catch (JSONException e) {}
        catch (Exception e) {}

        finish();
    }
}
