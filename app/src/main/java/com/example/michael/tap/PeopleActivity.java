package com.example.michael.tap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PeopleActivity extends ActionBarActivity {
    Context tempContext;
    public final static String MESSAGE_KEY = "com.example.nickhauser.tap.MESSAGE";
    public String searchkeyglobal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        //set up the list view which will contain search results
        //TODO (NGH) - this should display whether the user is a friend
        final ListView peopleList = (ListView) findViewById(R.id.people_list);
        peopleList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        buttonReset(null);

        //add the action listener for list items
        //which starts the person activity for the person clicked
        final Context parentContext = this;
        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent personClickedIntent = new Intent(parentContext, PersonActivity.class);
                String personName = (String) peopleList.getAdapter().getItem(position);
                personClickedIntent.putExtra(MESSAGE_KEY, personName);
                startActivity(personClickedIntent);
            }
        });
    }

    //Perform search reset action in response to button press
    //reset the value of the search box and execute the search
    public void buttonReset(View view) {
        EditText searchTextBox = (EditText) findViewById(R.id.search_text);
        searchTextBox.setText("");
        buttonSearch(view);
    }

    //Perform person search action in response to button press
    //expected behavior is that if the search is empty,
    //a list of the user's friends is returned;
    //if the search has a value,
    //a list of people pertinent to that value is returned
    public void buttonSearch(View view) {
        //get the search string
        EditText searchTextBox = (EditText) findViewById(R.id.search_text);
        String searchKey = searchTextBox.getText().toString();

        //get the list of results to display
        //User[] results;
        if (searchKey.equals("")) {
            fetchFriends(Global.currentUser);
        }else {
            fetchSearch(searchKey);
        }

        //get the GUI objects
        /*ListView peopleList = (ListView) findViewById(R.id.people_list);
        ArrayAdapter<String> people = (ArrayAdapter<String>) peopleList.getAdapter();
        people.clear();

        //add the results to the GUI
        for (int i = 0; i < results.length; i++) {
            people.add(results[i].getName());
        }NGH removed this because it should be handled in fetch methods*/
    }

    //Return a list of the user's friends from the server
    private void fetchFriends(String currentUser) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        ListFriends lf = new ListFriends();
        lf.execute();

    }



    //Return a list of the search results from the server
    private void fetchSearch(String searchKey) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        searchkeyglobal = searchKey;
        tempContext = this;
        SearchUsers su = new SearchUsers();
        su.execute();

    }







    /* * * * * * * * * * * * * * */
    /* Classes for Server Calls  */
    /* * * * * * * * * * * * * * */

    public class ListFriends extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/friends", Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlRead);
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                //EditText textBox = (EditText) findViewById(R.id.username_text);
                //requestString.put("username", textBox.getText().toString());
                //requestString.put("X-Tap-Auth", GLOBAL.Auth);

                //add the header and json to the http object
                //StringEntity requestEntity = new StringEntity(requestString.toString());
                //httppost.setEntity(requestEntity);
                //httppost.setHeader("Content-Type", "application/json");
                httpget.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpget);
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
                if (result == null) {
                    //do nothing
                }else if (result.get("status").toString().equals("401")) {
                    Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                } else {
                    //Successful listing of friend list
                    ListView peopleList = (ListView) findViewById(R.id.people_list);
                    ArrayAdapter<String> people = (ArrayAdapter<String>) peopleList.getAdapter();
                    people.clear();

                    JSONArray js = result.getJSONArray("result");
                    //add the results to the GUI
                    for (int i = 0; i < js.length(); i++) {
                        if (js.getJSONObject(i).get("accepted").equals("1")) {
                            User newUser = new User(js.getJSONObject(i).get("uname").toString(),
                                    js.getJSONObject(i).get("id").toString());
                            people.add(newUser.getName());
                            Global.userNameDict.remove(newUser.getName());
                            Global.userNameDict.put(newUser.getName(), newUser);
                        }
                    }
                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }





    public class SearchUsers extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/users/%s", Global.globalURL, searchkeyglobal);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                response = httpclient.execute(httpget);
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
                    Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                } else {
                    //Successful listing of friend list
                    ListView peopleList = (ListView) findViewById(R.id.people_list);
                    ArrayAdapter<String> people = (ArrayAdapter<String>) peopleList.getAdapter();
                    people.clear();

                    JSONArray js = result.getJSONArray("result");
                    //add the results to the GUI
                    for (int i = 0; i < js.length(); i++) {
                        User newUser = new User(js.getJSONObject(i).get("uname").toString(),
                                js.getJSONObject(i).get("id").toString());
                        people.add(newUser.getName());
                        Global.userNameDict.remove(newUser.getName());
                        Global.userNameDict.put(newUser.getName(), newUser);
                    }

                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

}