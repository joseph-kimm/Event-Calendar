package com.example.samplewebapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    JSONObject event;
    String username;
    boolean error;

    String message;

    Map<String, Integer> categories = new HashMap<String, Integer>()
    {{
        put("other", 0);
        put("social", 1);
        put("affinity group", 2);
        put("club event", 3);
        put("performance", 4);
        put("academic", 5);
        put("post grad", 6);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        username = getIntent().getStringExtra("username");

        try {
            event = new JSONObject(getIntent().getStringExtra("event"));
        } catch (JSONException e) {
        }

        EditText title = findViewById(R.id.titleEdit);
        EditText host = findViewById(R.id.hostEdit);
        EditText location = findViewById(R.id.locationEdit);
        EditText date = findViewById(R.id.dateEdit);
        EditText starTime = findViewById(R.id.startTimeEdit);
        EditText endTime = findViewById(R.id.endTimeEdit);
        EditText bring = findViewById(R.id.bringEdit);
        EditText audience = findViewById(R.id.audienceEdit);
        EditText access = findViewById(R.id.accessibilityEdit);
        EditText description = findViewById(R.id.descriptionEdit);

        Spinner category = (Spinner) findViewById(R.id.categoryEdit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

        try {
            title.setText(event.getString("title"));
            host.setText(event.getString("host"));
            location.setText(event.getString("location"));

            String categoryInfo = event.getString("category");

            if (categories.containsKey(categoryInfo)) {
                category.setSelection(categories.get(categoryInfo));
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date dateInfo = format.parse(event.getString("date"));
            String dateInfoStr = format.format(dateInfo);
            date.setText(dateInfoStr);

            starTime.setText(event.getString("startTime"));
            endTime.setText(event.getString("endTime"));
            bring.setText(event.getString("toBring"));
            audience.setText(event.getString("intendedAudience"));
            access.setText(event.getString("accessibilityInfo"));
            description.setText(event.getString("additionalDescription"));
        } catch (JSONException | ParseException e) {
        }
    }

    public static boolean dateValid(String str){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            format.parse(str.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean timeValid(String str){
        try {
            //check it has colin
            if (!str.contains(":")){
                Log.v("TIME ERROR", "colin");

                return false;
            }
            //check all the characters are numbers, colin, space, or am/pm
            for (int i = 0; i < str.length(); i++){
                char c = str.charAt(i);
                if (!Character.isDigit(c) && c != ':'){
                    Log.v("TIME ERROR", "character error");
                    return false;
                }
            }
            if (str.length() != 5){
                Log.v("TIME ERROR", "first half length");
                return false;
            }
            String[] time = str.split(":");
            return  Integer.parseInt(time[0]) < 24 && Integer.parseInt(time[1].substring(0,2)) < 60;
        } catch (Exception e) {
            return false;
        }
    }

    public String checkEntry(EditText edit) {
        String entry = edit.getText().toString();

        if (entry.equals("")) {
            edit.setBackgroundColor(Color.parseColor("#fce8f3"));
            error = true;
        }

        else {
            edit.setBackgroundColor(Color.parseColor("#f5f2e1"));
        }
        return entry;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

    public void onSaveButtonClick(View v) {

        error = false;

        EditText title = findViewById(R.id.titleEdit);
        EditText host = findViewById(R.id.hostEdit);
        EditText location = findViewById(R.id.locationEdit);
        EditText date = findViewById(R.id.dateEdit);
        EditText starTime = findViewById(R.id.startTimeEdit);
        EditText endTime = findViewById(R.id.endTimeEdit);
        EditText bring = findViewById(R.id.bringEdit);
        EditText audience = findViewById(R.id.audienceEdit);
        EditText access = findViewById(R.id.accessibilityEdit);
        EditText description = findViewById(R.id.descriptionEdit);

        Spinner category = (Spinner) findViewById(R.id.categoryEdit);

        String titleText = checkEntry(title);
        String categoryText = (String) category.getSelectedItem();
        String hostText = checkEntry(host);
        String locationText = checkEntry(location);
        String dateText = checkEntry(date);
        String startTimeText = checkEntry(starTime);
        String endTimeText = checkEntry(endTime);
        String bringText = checkEntry(bring);
        String audienceText = checkEntry(audience);
        String accessText = checkEntry(access);
        String descriptionText = checkEntry(description);

        if (!dateValid(dateText)) {
            Toast.makeText(
                    this,"date must be in yyyy-MM-dd form",Toast.LENGTH_LONG).show();
        } else if (!timeValid(startTimeText) || !timeValid(endTimeText)){
            Toast.makeText(this,"Time must be in hh:mm format (military time) and real.",Toast.LENGTH_LONG).show();
            return;
        }

        else if (error) {
            Toast.makeText(
                    this,"Missing Required Field/s",Toast.LENGTH_LONG).show();
        }

        else {

            String u = "http://10.0.2.2:3006/appEditEvent?title=" + titleText + "&category="
                    + categoryText + "&host=" + hostText + "&location=" + locationText
                    + "&date=" + dateText + "&startTime=" + startTimeText + "&endTime="
                    + endTimeText + "&toBring=" + bringText + "&intendedAudience=" + audienceText
                    + "&accessibilityInfo=" + accessText +
                    "&additionalDescription=" + descriptionText +
                    "&userName=" + username;

            try {
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
                                    message = "Could not edit event";
                                }


                                // need to set the instance variable in the Activity object
                                // because we cannot directly access the TextView from here

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                message = "Could not edit event";
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
            catch (Exception e) {
                // uh oh
                e.printStackTrace();
            }
        }
    }

    public void onGoBackButtonClick(View v) {
        finish();
    }
}
