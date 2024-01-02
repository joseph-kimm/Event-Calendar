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

public class RegisterActivity extends AppCompatActivity {
    private static String userExistsVar = "false";
    private static String userExistsError = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.v("start", "started RegisterActivity");
    }

    public void onRegisterButtonClick(View v){
        EditText usernameET = findViewById(R.id.usernameRegister);
        EditText fNameET = findViewById(R.id.fNameRegister);
        EditText lNameET = findViewById(R.id.lNameRegister);
        EditText orgEmailET = findViewById(R.id.orgEmailRegister);
        EditText passwordET = findViewById(R.id.passwordRegister);
        EditText confirmPasswordET = findViewById(R.id.confirmPasswordRegister);

        String givenUsername = usernameET.getText().toString();
        String givenFName = fNameET.getText().toString();
        String givenLName = lNameET.getText().toString();
        String givenOrgEmail = orgEmailET.getText().toString();
        String givenPassword = passwordET.getText().toString();
        String givenConfirmPassword = confirmPasswordET.getText().toString();

        //check if anything is empty
        if (givenUsername.equals("")){
            Toast.makeText(v.getContext(), "Please provide a username.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenFName.equals("")){
            Toast.makeText(v.getContext(), "Please provide a first name.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenLName.equals("")){
            Toast.makeText(v.getContext(), "Please provide a last name.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenOrgEmail.equals("")){
            Toast.makeText(v.getContext(), "Please provide a tri-co email.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenPassword.equals("")){
            Toast.makeText(v.getContext(), "Please provide a password.", Toast.LENGTH_LONG).show();
            return;
        } else if (givenConfirmPassword.equals("")){
            Toast.makeText(v.getContext(), "Please confirm your password.", Toast.LENGTH_LONG).show();
            return;
        }

        //check if email is in email format/check if there is an @brynmawr or an @ haverford or whatever
         else if (!(givenOrgEmail.contains("@brynmawr.edu") || givenOrgEmail.contains("@haverford.edu") || givenOrgEmail.contains("@swarthmore.edu"))){
            Toast.makeText(v.getContext(), "Please check that you have provided a tri-co email.", Toast.LENGTH_LONG).show();
            return;
        }
        //check if password and confirm password are the same
         else if (!givenPassword.equals(givenConfirmPassword)){
            Toast.makeText(v.getContext(), "Please check that passwords are the same.", Toast.LENGTH_LONG).show();
            return;
        }
        //check if password is 8 characters or longer
         else if (givenPassword.length() < 8){
            Toast.makeText(v.getContext(), "Please make your password 8 or more characters.", Toast.LENGTH_LONG).show();
            return;
        }

         //check if username contains non letter (or _ or .) characters
        boolean usernameContainsDisallowedCharacters = false;
        for (int i = 0; i < givenUsername.length(); i++){
            char c = givenUsername.charAt(i);
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_' && c != '.'){
                usernameContainsDisallowedCharacters = true;
                break;
            }
        }
        if (usernameContainsDisallowedCharacters){
            Toast.makeText(v.getContext(), "Username must only contain letters, numbers, underscores, and periods.", Toast.LENGTH_LONG).show();
            return;
        }

        //check if email is valid
        boolean emailContainsDisallowedCharacters = false;
        int atSymbolIndex = givenOrgEmail.indexOf("@");
        String emailBeforeAtSymbol = givenOrgEmail.substring(0, atSymbolIndex);
        if (emailBeforeAtSymbol.length() == 0){
            emailContainsDisallowedCharacters = true;
        } else {
            for (int i = 0; i < emailBeforeAtSymbol.length(); i++){
                char c = emailBeforeAtSymbol.charAt(i);
                if (!Character.isLetter(c) && !Character.isDigit(c)){
                    emailContainsDisallowedCharacters = true;
                    break;
                }
            }
        }

        if (emailContainsDisallowedCharacters){
            Toast.makeText(v.getContext(), "Email must be valid and must only contain letters and numbers.", Toast.LENGTH_LONG).show();
            return;
        }

        //if ALL OF THAT is fine, THEN send request to server to register
        try {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                        try {
                            // assumes that there is a server running on the AVD's host
                            // and that it has a /test endpoint that returns a JSON object with
                            // a field called "message"

                            URL url = new URL("http://10.0.2.2:3006/registerRequest?username=" + givenUsername + "&firstName=" + givenFName + "&lastName=" + givenLName + "&orgEmail=" + givenOrgEmail + "&password=" + givenPassword );

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            conn.connect();

                            Scanner in = new Scanner(url.openStream());
                            String response = in.nextLine();

                            JSONObject jo = new JSONObject(response);
//                            Log.v("DOES IT EXIST?", jo.getString("result"));
                            userExistsVar = jo.getString("result");
                            userExistsError = jo.getString("error");
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
        }

        if (userExistsVar.equals("true")){
            Toast.makeText(v.getContext(), "An account with this " + userExistsError + " already exists. Please try again.", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(v.getContext(), "Successfully registered.", Toast.LENGTH_LONG).show();
            //if request comes back ok, THEN set intent to login and toast a successful registration
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("givenUsername", givenUsername);
            finish();
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
