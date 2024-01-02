package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private static String userExistsVar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.v("start", "started LoginActivity");

        String un = getIntent().getStringExtra("givenUsername");
        if (un != null){
            Log.v("USERNAME", un);
            TextView userAnswerView = findViewById(R.id.usernameLogin);
            userAnswerView.setText(un);
        }

        EditText passwordET = findViewById(R.id.passwordLogin);
        passwordET.getText().clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText passwordET = findViewById(R.id.passwordLogin);
        passwordET.setText("");
    }

    public void onLoginButtonClick(View v){
        EditText usernameET = findViewById(R.id.usernameLogin);
        EditText passwordET = findViewById(R.id.passwordLogin);

        String givenUsername = usernameET.getText().toString();
        String givenPassword = passwordET.getText().toString();

        if (givenUsername.equals("")){
            Toast.makeText(v.getContext(), "Please provide a username.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenPassword.equals("")){
            Toast.makeText(v.getContext(), "Please input a password.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3006/loginRequest?username=" + givenUsername + "&password=" + givenPassword );

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
                            userExistsVar = jo.getString("result");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
//                            message = e.toString();
                        }
                    }
            );

            // this waits for up to 2 seconds
            // it's a bit of a hack because it's not truly asynchronous
            // but it should be okay for our purposes (and is a lot easier)
            executor.awaitTermination(2, TimeUnit.SECONDS);


            // now we can set the status in the TextView
//            tv.setText(message);
        }
        catch (Exception e) {
            // uh oh
            e.printStackTrace();
            Log.v("ERROR", "Catch 2 - error");
//            tv.setText(e.toString());
        }

        Log.v("DOES IT??", userExistsVar);
        if (userExistsVar.equals("failure")){
            Toast.makeText(v.getContext(), "Login failed. Please try again.", Toast.LENGTH_LONG).show();
        } else if (userExistsVar.equals("success")){
            Toast.makeText(v.getContext(), "Login successful.", Toast.LENGTH_LONG).show();
            //if request comes back ok, THEN set intent to login and toast a successful registration
            Intent i = new Intent(this, HomepageActivity.class);
            i.putExtra("username", givenUsername);
            startActivity(i);
            //just use startActivity if no code needs to run when you return to the original activity
        }

    }

    public void onGoBackButtonClick(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();

    }

}
