package com.example.samplewebapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SearchEventActivity extends AppCompatActivity {
    ListView listView;
    List<String> titles = new LinkedList<>();
    List<String> dates = new LinkedList<>();

    List<String> times = new LinkedList<>();

    JSONArray data;
    HashMap<String, JSONObject> events = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_event);

        Spinner category = (Spinner) findViewById(R.id.searchCat);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_array,
                android.R.layout.simple_spinner_dropdown_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
    }

    public void onSearchButtonClick(View v) {
        titles.clear();
        dates.clear();
        times.clear();
        populate();
        listView = (ListView) findViewById(R.id.searchEventList);
        ListSetUpBaseAdapter adp = new ListSetUpBaseAdapter(getBaseContext(),titles,dates,times);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this,R.layout.list_set_up, R.id.eventListTitle, titles);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position).toString();
                Intent i = new Intent(SearchEventActivity.this, ViewEventActivity.class);
                JSONObject event = events.get(title);
                i.putExtra("event", event.toString());
                startActivity(i);
            }
        });
    }


    public void populate() {
        String link = getLink();

        TextView tv = findViewById(R.id.statusField);

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            //connection
                            URL url = new URL(link);
                            Log.v("Anna", "Link: " + link);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            //Scanner to read content from http
                            Scanner in = new Scanner(url.openStream());
                            //reads
                            String response = in.nextLine();
                            //jason object from parsing response containing json data
                            JSONObject jo = new JSONObject(response);
                            //Log.d(jo.getString("events"), "hi");

                            data = jo.getJSONArray("events");
                            //all
                            for (int i = 0; i < data.length(); i++) {
                                //current
                                JSONObject event = data.getJSONObject(i);
                                //extract title
                                String title = event.getString("title");
                                String date = event.getString("date");
                                String dateForm[] = date.split("T");
                                String dateFormat = dateForm[0];
                                //Log.v(dateFormat,"f");
                                String start = event.getString("startTime");
                                String end = event.getString("endTime");
                                titles.add(title);
                                dates.add(dateFormat);
                                times.add(start + " - " + end);

                                events.put(title, event);
                                //Log.v(title,"tit");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            //message = e.toString();
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);

            // now we can set the status in the TextView
            //tv.setText(message);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            tv.setText(e.toString());
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

    public String getEntry(EditText t, String type) {
        String entry = "";
        if (t != null) {
            entry = t.getText().toString();
        }
        return entry;
    }

    public static boolean dateCheck(String str){
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

    private String stringConnect(String url, String type, String value) {
        if (!value.isEmpty()) {
            if (url.charAt(url.length() - 1) == '?') {
                return url + type + "=" + value;
            } else {
                return url + "&" + type + "=" + value;
            }
        }
        return url;
    }

    public String getLink() {
        String link = "";
        EditText title = findViewById(R.id.searchTitle);
        Spinner category = (Spinner) findViewById(R.id.searchCat);
        EditText host = findViewById(R.id.searchHost);
        EditText location = findViewById(R.id.searchLoc);
        EditText date = findViewById(R.id.searchDate);
        EditText startTime = findViewById(R.id.searchStart);
        String titleText = getEntry(title, "Title");
        String titleCategory = (String) category.getSelectedItem();
        String titleHost = getEntry(host, "Host");
        String titleLocation = getEntry(location, "Location");
        String titleDate = getEntry(date, "Date");
        String titleStart = getEntry(startTime, "Start Time");
        //Log.v( date + " ", "testing method");
        if (!titleDate.isEmpty() && !dateCheck(titleDate)){
            Toast.makeText(
                    this,"Date must be in yyyy-MM-dd form and real",Toast.LENGTH_LONG).show();
            //Log.v( "hiiiiiii", "testing method");
        } else if (!titleStart.isEmpty() && !timeValid(titleStart)){
            Toast.makeText(this,"Time must be in hh:mm format (military time) and real.",Toast.LENGTH_LONG).show();
        } else {
            String u = "http://10.0.2.2:3006/appSearch?";
            u = stringConnect(u, "title", titleText);
            u = stringConnect(u, "category", titleCategory);
            u = stringConnect(u, "host", titleHost);
            u = stringConnect(u, "location", titleLocation);
            u = stringConnect(u, "date", titleDate);
            u = stringConnect(u, "startTime", titleStart);
            link = u;
        }
        return link;
    }
    public void onBackButtonClick(View v) {
//        Intent i = new Intent(this, HomepageActivity.class);
//        startActivity(i);
        finish();
    }
}
