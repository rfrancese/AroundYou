package com.example.aroundyou;

import org.json.JSONException;
import org.json.JSONObject;
 

import com.example.aroundyou.library.DatabaseHandler;
import com.example.aroundyou.library.UserFunctions;
 

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
 
public class RegisterActivity extends Activity {
    Button btnRegister;
    Button btnLinkToLogin;
    EditText inputFullName;
    EditText inputEmail;
    EditText inputPassword;
    TextView registerErrorMsg;
     
    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    
  //CLASSE AGGIUNTA SUCCESSIVAMENTE
    private class MyAsyncTask extends AsyncTask<String, Void, JSONObject> {
            
            protected JSONObject doInBackground(String... params) {
                    UserFunctions userFunction = new UserFunctions();
                    if (params.length != 2)
                            return null;
                    JSONObject json = userFunction.loginUser(params[0], params[1]);
                    return json;
            }
           
            protected void onPostExecute(JSONObject json) {
                    try {
                if (json != null && json.getString(KEY_SUCCESS) != null) {
                    //loginErrorMsg.setText("");
                	registerErrorMsg.setText("");
                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){
                        // user successfully logged in
                        // Store user details in SQLite Database
                        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                        JSONObject json_user = json.getJSONObject("user");
                         
                        // Clear all previous data in database
                        UserFunctions userFunction = new UserFunctions();
                        userFunction.logoutUser(getApplicationContext());
                        db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
                         
                        // Launch Dashboard Screen
                        Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                         
                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                         
                        // Close Login Screen
                        finish();
                    }else{
                        // Error in login
                        registerErrorMsg.setText("Email o password errata");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
    }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
 
        // Importing all assets like buttons, text fields
        inputFullName = (EditText) findViewById(R.id.registerName);
        inputEmail = (EditText) findViewById(R.id.registerEmail);
        inputPassword = (EditText) findViewById(R.id.registerPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
         
        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {         
            public void onClick(View view) {
                String name = inputFullName.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                new MyAsyncTask().execute(email, password);
                UserFunctions userFunction = new UserFunctions();
                JSONObject json = userFunction.registerUser(name, email, password);
                 
                // check for login response
                try {
                    if (json.getString(KEY_SUCCESS) != null) {
                        registerErrorMsg.setText("");
                        String res = json.getString(KEY_SUCCESS); 
                        if(Integer.parseInt(res) == 1){
                            // user successfully registred
                            // Store user details in SQLite Database
                            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                            JSONObject json_user = json.getJSONObject("user");
                             
                            // Clear all previous data in database
                            userFunction.logoutUser(getApplicationContext());
                            db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));                        
                            // Launch Dashboard Screen
                            Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                            // Close all views before launching Dashboard
                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(dashboard);
                            // Close Registration Screen
                            finish();
                        }else{
                            // Error in registration
                            registerErrorMsg.setText("Error occured in registration");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
 
        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
 
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                // Close Registration View
                finish();
            }
        });
    }
}