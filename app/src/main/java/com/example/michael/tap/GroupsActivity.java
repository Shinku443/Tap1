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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GroupsActivity extends ActionBarActivity {
    Context tempContext;
    private String searchString;
    public final static String MESSAGE_KEY = "com.example.michael.tap.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        //set up the list view which will contain search results
        final ListView groupList = (ListView) findViewById(R.id.people_list);
        groupList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        buttonSearch(null);

        //add the action listener for list items
        //which starts the group activity for the group clicked
        final Context parentContext = this;
        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent groupClickedIntent = new Intent(parentContext, GroupActivity.class);
                String groupName = (String) groupList.getAdapter().getItem(position);
                groupClickedIntent.putExtra(MESSAGE_KEY, groupName);
                startActivity(groupClickedIntent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        buttonSearch(null);
    }

    //Perform create group action in response to button press
    public void buttonCreate(View view) {
        //get the group name
        EditText searchTextBox = (EditText) findViewById(R.id.search_text);
        String groupName = searchTextBox.getText().toString();

        //create the group or display an error
        if (Global.isStringValid(groupName, 16, false)) {
            createGroup(groupName);
        } else {
            Toast.makeText(this, "Group names must contain less than 16 letters and must not include spaces.", Toast.LENGTH_LONG).show();
        }
    }

    //Request that the server create a group with the name provided
    private void createGroup(String groupName) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        CreateGroupFromServer grpcreate = new CreateGroupFromServer();
        grpcreate.execute();
    }

    //Perform person search action in response to button press
    //expected behavior is that if the search is empty,
    //a list of the user's groups is returned;
    //if the search has a value,
    //a list of groups pertinent to that value is returned
    public void buttonSearch(View view) {
        //get the search string
        EditText searchTextBox = (EditText) findViewById(R.id.search_text);
        String searchKey = searchTextBox.getText().toString();

        //get the list of results to display
        Group[] results;
        if (searchKey.equals("")) {
            new FetchGroupsFromServer().execute();
        } else {
            searchString = searchKey;
            new SearchGroupsFromServer().execute();
        }

        /*//get the GUI objects
        ListView peopleList = (ListView) findViewById(R.id.people_list);
        ArrayAdapter<String> groups = (ArrayAdapter<String>) peopleList.getAdapter();
        groups.clear();

        //add the results to the GUI
        for (int i = 0; i < results.length; i++) {
            groups.add(results[i].getName());
        }Removed by NGH because search methods should now handle this*/
    }

    //Return a list of the search results from the server
    /*private void fetchSearch(String searchKey) {
        ListView peopleList = (ListView) findViewById(R.id.people_list);
        ArrayAdapter<String> groups = (ArrayAdapter<String>) peopleList.getAdapter();
        int countValids = 0;
        for (int i = 0; i < groups.getCount(); i++) {
            if (groups.getItem(i).contains(searchKey)) {
                countValids = countValids + 1;
            }
        }
        String[] valids = new String[countValids];
        int currValid = 0;
        for (int i = 0; i < groups.getCount(); i++) {
            if (groups.getItem(i).contains(searchKey)) {
                valids[currValid] = groups.getItem(i);
                currValid = currValid + 1;
            }
        }
        groups.clear();
        for (int i = 0; i < valids.length; i++) {
            groups.add(valids[i]);
        }
    }No longer needed, handled by an async task now*/


    public class FetchGroupsFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/groups", Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                //EditText textBox = (EditText) findViewById(R.id.username_text);
                //requestString.put("username", textBox.getText().toString());

                //add the header and json to the http object
                /*StringEntity requestEntity = new StringEntity(requestString.toString());
                httpget.setEntity(requestEntity); NGH Commented this out as a test*/
                //httppost.setHeader("Content-Type", "application/json");
                httpget.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpget);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
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
                    //Might be wrong, can't test lol
                    //Toast.makeText(tempContext, "You and ", selected," are now friends!", , Toast.LENGTH_LONG).show();
                    //Displays name of groups: result.get("result").get("name");


                    //add to the GUI objects
                    ListView peopleList = (ListView) findViewById(R.id.people_list);
                    ArrayAdapter<String> groups = (ArrayAdapter<String>) peopleList.getAdapter();
                    groups.clear();
                    JSONArray arr = result.getJSONArray("result");
                    for(int i = 0; i < arr.length(); i++) {
                        Group newGroup = new Group(arr.getJSONObject(i).get("name").toString(),
                                arr.getJSONObject(i).get("id").toString());
                        if (arr.getJSONObject(i).get("accepted").toString().equals("1")) {
                            Global.groupNameDict.remove(newGroup.getName());
                            Global.groupNameDict.put(newGroup.getName(), newGroup);
                            groups.add(newGroup.getName());
                        }
                    }

                }
            } catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class SearchGroupsFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/groups", Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                //EditText textBox = (EditText) findViewById(R.id.username_text);
                //requestString.put("username", textBox.getText().toString());

                //add the header and json to the http object
                /*StringEntity requestEntity = new StringEntity(requestString.toString());
                httpget.setEntity(requestEntity); NGH Commented this out as a test*/
                //httppost.setHeader("Content-Type", "application/json");
                httpget.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpget);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
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
                    //Might be wrong, can't test lol
                    //Toast.makeText(tempContext, "You and ", selected," are now friends!", , Toast.LENGTH_LONG).show();
                    //Displays name of groups: result.get("result").get("name");


                    //add to the GUI objects
                    ListView peopleList = (ListView) findViewById(R.id.people_list);
                    ArrayAdapter<String> groups = (ArrayAdapter<String>) peopleList.getAdapter();
                    groups.clear();
                    JSONArray arr = result.getJSONArray("result");
                    for(int i = 0; i < arr.length(); i++) {
                        Group newGroup = new Group(arr.getJSONObject(i).get("name").toString(),
                                arr.getJSONObject(i).get("id").toString());
                        if (newGroup.getName().contains(searchString) &&
                                arr.getJSONObject(i).get("accepted").toString().equals("1")) {
                            Global.groupNameDict.remove(newGroup.getName());
                            Global.groupNameDict.put(newGroup.getName(), newGroup);
                            groups.add(newGroup.getName());
                        }
                    }

                }
            } catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class CreateGroupFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/groups", Global.globalURL);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                //Need R.id. GROUP TEXT NAME
                EditText textBox = (EditText) findViewById(R.id.search_text);
                requestString.put("name", textBox.getText().toString());

                //add the header and json to the http object
                StringEntity requestEntity = new StringEntity(requestString.toString());
                //requestEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(requestEntity);
                httppost.setHeader("Content-Type", "application/json");
                httppost.setHeader("X-Tap-Auth", Global.auth);

                //query the server
                response = httpclient.execute(httppost);
                result = new JSONObject(EntityUtils.toString(response.getEntity()));
            } catch (Exception e) {
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
                    Toast.makeText(tempContext, "Authorization denied.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(tempContext, "Group Succesfully created!", Toast.LENGTH_LONG).show();
                    //save groupid for that specific group
                    Group newGroup = new Group(((JSONObject) result.get("result")).get("name").toString(),
                            ((JSONObject) result.get("result")).get("id").toString());
                    Global.groupNameDict.put(newGroup.getName(), newGroup);
                    ListView peopleList = (ListView) findViewById(R.id.people_list);
                    ArrayAdapter<String> groups = (ArrayAdapter<String>) peopleList.getAdapter();
                    groups.add(newGroup.getName());
                    //Global.groupid = (result.get("result").get("id").toString());
                }
            } catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}