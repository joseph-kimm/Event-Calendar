package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    public static final int LOGIN_ACTIVITY_ID = 2;
    public static final int REGISTER_ACTIVITY_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onGoToLoginButtonClick(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, LOGIN_ACTIVITY_ID);
    }

    public void onGoToRegisterButtonClick(View v){
        Intent i = new Intent(this, RegisterActivity.class);
        startActivityForResult(i, REGISTER_ACTIVITY_ID);
    }
}
