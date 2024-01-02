package com.example.samplewebapp;

import static java.lang.Double.isNaN;
import static java.lang.Double.parseDouble;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class PostEventActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);

        Spinner category = (Spinner) findViewById(R.id.editCat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);

    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

    protected String message;
    boolean filled = true;
    public String getEntry(EditText t, String type) {
        String entry = "";
        if (t != null) {
            entry = t.getText().toString();
        } else {
            Toast.makeText(
                    this, "Error editText: " + type, Toast.LENGTH_LONG).show();
            throw new IllegalArgumentException("EditText is null: " + type);
        }
        if (!entry.equals("")) {
            t.setBackgroundColor(Color.parseColor("#f5f2e1"));
            return entry;
        } else {
            t.setBackgroundColor(Color.parseColor("#fce8f3"));
            filled = false;
            return null;
        }
    }

    public void onBackButtonClick(View v) {
//        Intent i = new Intent(this, HomepageActivity.class);
//        startActivity(i);
        finish();
    }

    public static boolean dateValid(String str){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //non-lenient checks for valid date
        format.setLenient(false);
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

    public void onCreateButtonClick(View v) {
        filled = true;
        EditText title = findViewById(R.id.editTitle);
        Spinner category = (Spinner) findViewById(R.id.editCat);
        EditText host = findViewById(R.id.editHost);
        EditText location = findViewById(R.id.editLoc);
        EditText date = findViewById(R.id.editDate);
        EditText startTime = findViewById(R.id.editStart);
        EditText endTime = findViewById(R.id.editEnd);
        EditText toBring = findViewById(R.id.editBring);
        EditText audience = findViewById(R.id.editAud);
        EditText access = findViewById(R.id.editAcc);
        EditText description = findViewById(R.id.editDes);
        String titleText = getEntry(title, "Title");
        String titleCategory = (String) category.getSelectedItem();
        String titleHost = getEntry(host, "Host");
        String titleLocation = getEntry(location, "Location");
        String titleDate = getEntry(date, "Date");
        String titleStart = getEntry(startTime, "Start Time");
        String titleEnd = getEntry(endTime, "End Time");
        String titleBring = getEntry(toBring, "To Bring");
        String titleAudience = getEntry(audience, "Audience");
        String titleAccess = getEntry(access, "Accessibility");
        String titleDescription = getEntry(description, "Description");
        //Log.v( filled + " ", "testing method");

         if (!filled) {
            Toast.makeText(this,"Missing Required Field/s",Toast.LENGTH_LONG).show();
            return;
        } else if (!dateValid(titleDate)){
            Toast.makeText(this,"Date must be in yyyy-MM-dd form and real",Toast.LENGTH_LONG).show();
            return;
        } else if (!timeValid(titleStart) || !timeValid(titleEnd)){
             Toast.makeText(this,"Time must be in hh:mm format (military time) and real.",Toast.LENGTH_LONG).show();
             return;
         }

         //this will only happen if none of the error contitions didn't return so you don't need an else if
         String username = getIntent().getStringExtra("username");
         String u = "http://10.0.2.2:3006/appPostEvent?title=" + titleText + "&category="
                 + titleCategory + "&host=" + titleHost + "&location=" + titleLocation
                 + "&date=" + titleDate + "&startTime=" + titleStart + "&endTime="
                 + titleEnd + "&toBring=" + titleBring + "&intendedAudience=" + titleAudience
                 + "&accessibilityInfo=" + titleAccess +
                 "&additionalDescription=" + titleDescription + "&username=" + username;
            //Log.v(titleText + filled, "testing method");
            //Log.v( getIntent().getExtras().toString(), "testing method");

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
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
                } catch (Exception e) {
                    e.printStackTrace();
                    message = e.toString();
                }
            });

            executor.awaitTermination(2, TimeUnit.SECONDS);
            finish();

        } catch (Exception e) {
            // uh oh
            e.printStackTrace();
            title.setText(e.toString());
            Toast.makeText(
                    this,"Unexpected Error",Toast.LENGTH_LONG).show();
        }
    }

}
