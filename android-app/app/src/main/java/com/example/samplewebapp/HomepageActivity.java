package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;



public class HomepageActivity extends AppCompatActivity {

    public static final int ALL_EVENTS_ACTIVITY_ID = 1;
    public static final int POST_EVENT_ACTIVITY_ID = 2;
    public static final int CHANGE_EVENT_ACTIVITY_ID = 3;
    public static final int CHANGE_PASSWORD_EVENT_ID = 4;
    public static final int LOGIN_ACTIVITY_ID = 5;

    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
    }

    public void onMyEventsButtonClick(View v) {
        Intent i = new Intent(this, MyeventsActivity.class);
        i.putExtra("username", username);
        startActivity(i);
    }

    public void onViewButtonClick(View v) {
        Intent i = new Intent(this, ViewActivity.class);
        startActivity(i);
    }

    public void onPostButtonClick(View v) {
        Intent i = new Intent(this, PostEventActivity.class);
        i.putExtra("username", username);
        startActivity(i);
    }
    public void onChangePasswordButtonClick(View v){
        Intent i = new Intent(this, ChangePasswordActivity.class);
        i.putExtra("username", username);
        i.putExtra("password", password);
        startActivity(i);
    }

    public void onSearchButtonClick(View v){
        Intent i = new Intent(this, SearchEventActivity.class);
        startActivity(i);
    }

    public void onLogoutButtonClick(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}
