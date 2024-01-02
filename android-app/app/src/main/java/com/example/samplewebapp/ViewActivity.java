package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ViewActivity extends AppCompatActivity {
//    String words [] = {"bob", "manny", "alice", "alice", "alice", "alice",
//            "alice", "alice", "alice", "alice", "bob", "bob"};
    ListView listView;
    List<String> titles = new LinkedList<>();
    List<String> dates = new LinkedList<>();

    List<String> times = new LinkedList<>();

    JSONArray data;
    HashMap<String, JSONObject> events = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        populate();
        listView = (ListView) findViewById(R.id.eventList);
        ListSetUpBaseAdapter adp = new ListSetUpBaseAdapter(getBaseContext(),titles,dates,times);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
//                (this,R.layout.list_set_up, R.id.eventListTitle, titles);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) parent.getItemAtPosition(position).toString();
                Intent i = new Intent(ViewActivity.this, ViewEventActivity.class);
                JSONObject event = events.get(title);
                i.putExtra("event", event.toString());
                startActivity(i);
            }
        });
    }



    public void populate() {

        TextView tv = findViewById(R.id.statusField);

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            //connection
                            URL url = new URL("http://10.0.2.2:3006/appView");
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

                            // json array of events sadly this is not sending the array of events
                            //its sending an object with nested arrays of event
                            // event:[{"title\":\"bob\"}{"title\":\"cat\"]
                            //JSONArray events = new JSONArray(response);
                            //so we need to get the "events" array from json obj
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
    public void onBackButtonClick(View v) {
//        Intent i = new Intent(this, HomepageActivity.class);
//        startActivity(i);
        finish();
    }


}
