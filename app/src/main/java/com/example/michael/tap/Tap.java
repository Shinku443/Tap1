package com.example.michael.tap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.*;
import org.json.JSONException;

import java.io.*;
import java.net.*;
import java.lang.Override;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class Tap extends ActionBarActivity {
    Context tempContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap);
    }

    //Perform log in action in response to button press
    //request credentials from the server
    //if they match, move into the app and save the username
    //otherwise, display an error message
    public void buttonLogIn(View view) {
        //start the validation thread
        tempContext = this;
        ValidateCredentials validateAcc = new ValidateCredentials();
        validateAcc.execute();
    }

    //Create a new account in response to button press
    //request the server to validate credentials and check uniqueness
    //read the response denoting whether the server created the account
    //if the request succeeded, move into the app and save the username
    //otherwise, display an error message
    public void buttonCreateAccount(View view) {
        //get the username and password provided
        EditText textBox = (EditText) findViewById(R.id.username_text);
        String username = textBox.getText().toString();
        textBox = (EditText) findViewById(R.id.password_text);
        String password = textBox.getText().toString();

        //check that the provided credentials meet security requirements
        if (username.length() < 6) {
            Toast.makeText(this, "Usernames must contain more than 5 letters.", Toast.LENGTH_LONG).show();
        } else if (!Global.isStringValid(username, 16, false)) {
            Toast.makeText(this, "Usernames must contain less than 16 letters and must not include spaces.", Toast.LENGTH_LONG).show();
        } else if (!Global.isStringValid(password, 128, true)) {
            Toast.makeText(this, "Your password is too long.", Toast.LENGTH_LONG).show();
        } else {
            createAccount(username, password);
        }
    }

    //Send a request to the server to create a new account using the supplied credentials
    private void createAccount(String username, String password) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        CreateCredentials createAcc = new CreateCredentials();
        createAcc.execute();
    }

    //Request a password reset in response to button press
    //display a message notifying the user the request will be processed
    public void buttonForgotPassword(View view) {
        Toast.makeText(this, "Your request will be processed by our admins; you will be emailed shortly.", Toast.LENGTH_LONG).show();
    }





    /* * * * * * * * * * * * * * */
    /* Classes for Server Calls  */
    /* * * * * * * * * * * * * * */



    //Class to validate a username-password pair
    public class ValidateCredentials extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            String urlRead = String.format("%s/auth", Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                EditText textBox = (EditText) findViewById(R.id.username_text);
                requestString.put("username", textBox.getText().toString());
                textBox = (EditText) findViewById(R.id.password_text);
                requestString.put("password", textBox.getText().toString());

                //add the header and json to the http object
                StringEntity requestEntity = new StringEntity(requestString.toString());
                requestEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(requestEntity);

                //query the server
                response = httpclient.execute(httppost);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            }catch (Exception e) {
                e.printStackTrace();
            }

            //return the result
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            //move into the main app if the login succeeded, display error otherwise
            try {
                if (result.get("status").toString().equals("401")) {
                    Toast.makeText(tempContext, "Invalid username or password.", Toast.LENGTH_LONG).show();
                } else if (result.get("status").toString().equals("200")) {
                    Global.currentUser = ((JSONObject)result.get("result")).get("uname").toString();
                    Global.auth = ((JSONObject)result.get("result")).get("token").toString();
                    Global.id = ((JSONObject)result.get("result")).get("id").toString();
                    startActivity(new Intent(tempContext, MenuActivity.class));
                }else {
                    Log.v("V002", result.get("status").toString());
                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Login error; try again.", Toast.LENGTH_LONG).show();
            }
        }

    }

    //Class to create a username-password pair
    //TODO - modify this to look like above using proper header and JSON info
    public class CreateCredentials extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            String urlRead = String.format("%s/users",Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlRead);
            HttpResponse response = null;
            JSONObject result = null;
            JSONObject requestString = new JSONObject();


            try {
                //add the input values
                EditText textBox = (EditText) findViewById(R.id.username_text);
                requestString.put("username", textBox.getText().toString());
                textBox = (EditText) findViewById(R.id.password_text);
                requestString.put("password", textBox.getText().toString());

                //Header
                StringEntity requestEntity = new StringEntity(requestString.toString());
                requestEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(requestEntity);

                //query the server
                response = httpclient.execute(httppost);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));


            }catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            //move into the main app if the login succeeded, display error otherwise
            try {
                if (result.get("status").toString().equals("500")) {
                    Toast.makeText(tempContext, "Database access failed.", Toast.LENGTH_LONG).show();
                }else if (result.get("status").toString().equals("201")) {
                    Toast.makeText(tempContext, "Successful creation! You may now login.", Toast.LENGTH_LONG).show();
                }else {
                    Log.v("V001", result.get("status").toString());
                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}