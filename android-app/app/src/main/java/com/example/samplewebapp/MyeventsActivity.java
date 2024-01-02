package com.example.samplewebapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyeventsActivity extends AppCompatActivity {

    JSONArray data = new JSONArray();
    String statusText;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myevents);

        username = getIntent().getStringExtra("username");
//        username = "josephTest";

        TextView status = findViewById(R.id.statusField);

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3006/appMyEvents?username=" + username);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();
                            statusText = "My Events";

                            data = new JSONArray(response);

                            if (data.length() == 0 ) {
                                statusText = "No Event Posted Yet";
                            }

                            // need to set the instance variable in the Activity object
                            // because we cannot directly access the TextView from here

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            statusText ="Not Connected";
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);
            status.setText(statusText);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
        }

        // trying an example
        /*
        JSONObject event1 = new JSONObject();
        JSONObject event2 = new JSONObject();
        try {
            event1.put("title", "Studying");
            event2.put("title", "Partay");
        } catch (JSONException e) {
        }
        data.put(event1);
        data.put(event2);
         */

        HashMap<String, JSONObject> events = new HashMap<>();

        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject event = data.getJSONObject(i);
                String title = event.getString("title");
                events.put(title, event);
            } catch (JSONException e) {
            }
        }

        ArrayList titles = new ArrayList<>(events.keySet());

        ListView eventsList = findViewById(R.id.events);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.listview_row, R.id.textView, titles);
        eventsList.setAdapter(arrayAdapter);

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(MyeventsActivity.this, MyeventActivity.class);
                JSONObject event = events.get(title);
                i.putExtra("event", event.toString());
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

    public void onGoBackButtonClick(View v) {
        finish();
    }


}