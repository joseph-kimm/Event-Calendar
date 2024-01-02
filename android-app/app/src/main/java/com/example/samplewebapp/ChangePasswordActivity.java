package com.example.samplewebapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class ChangePasswordActivity extends AppCompatActivity{
    private static String newPasswordExistsVar = "false";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        Log.v("start", "started ChangePasswordActivity");
    }

    public void onChangePasswordButtonClick(View v){
        EditText newPasswordText = findViewById(R.id.newPassword);
        EditText confirmNewPasswordText = findViewById(R.id.confirmNewPassword);

        String newPassword = newPasswordText.getText().toString();
        String confirmNewPassword = confirmNewPasswordText.getText().toString();
        String username = getIntent().getStringExtra("username");

        //check if password entered isn't ""
        if(newPassword.equals("")){
            Toast.makeText(v.getContext(), "Please provide a password.", Toast.LENGTH_LONG).show();
            return;
        }
        //check if confirmed password isn't ""
        else if(confirmNewPassword.equals("")){
            Toast.makeText(v.getContext(), "Please confirm your password.", Toast.LENGTH_LONG).show();
            return;
        }
        //check that the password and confirmation password match
        else if(!newPassword.equals(confirmNewPassword)){
            Toast.makeText(v.getContext(), "Please check that passwords are the same.", Toast.LENGTH_LONG).show();
            return;
        }
        //check that the password is at least 8 characters
        else if(newPassword.length()<8){
            Toast.makeText(v.getContext(), "Please make your password 8 or more characters.", Toast.LENGTH_LONG).show();
            return;
        }
        //send request to server

        try{
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try{
                    URL url = new URL("http://10.0.2.2:3006/changePasswordRequest?newPassword=" + newPassword + "&username=" + username);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();

                    //read and parse response
                    Scanner in = new Scanner(url.openStream());
                    String response = in.nextLine();


                    Log.d("ChangePasswordActivity", "Server response: " + response);


                    JSONObject jo = new JSONObject(response);
                    Log.v("DOES IT EXIST?", jo.getString("result"));
                    //check response from server to see if it was changed or not
                    //true if it was, false if it wasn't
                    newPasswordExistsVar = jo.getString("result");
                    //toasts for if it was changed successfully or not
                    runOnUiThread(()->{
                        if(newPasswordExistsVar.equals("true")){
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(ChangePasswordActivity.this, HomepageActivity.class);
                            startActivity(intent);
                            finish(); // Close the current activity
                        }
                        else{
                            Toast.makeText(ChangePasswordActivity.this, "Failed to change password.", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                catch(Exception e){
                    e.printStackTrace();
                    Log.e("ChangePasswordActivity", "Error: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(ChangePasswordActivity.this, "Error changing password.", Toast.LENGTH_LONG).show());
                }
            });
            executor.awaitTermination(2, TimeUnit.SECONDS);

        }
        catch (Exception e){
            e.printStackTrace();
            Log.v("ERROR", "Catch 2 - error");
        }


    }
    public void onGoBackButtonClick(View v){
        Intent i = new Intent();
        setResult(RESULT_CANCELED, i);
        finish();
    }
}
