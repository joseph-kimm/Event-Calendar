package com.example.samplewebapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewEventActivity extends AppCompatActivity {

    JSONObject event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

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
}

