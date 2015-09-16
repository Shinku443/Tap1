package com.example.michael.tap;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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


public class PersonActivity extends ActionBarActivity {
    Context tempContext;
    private final static String ALREADY_FRIENDS_STATUS = "This user is your friend.";
    private final static String REQUEST_SENT_STATUS = "You have sent this user a friend request.";
    private final static String NOT_FRIENDS_STATUS = "TODO";
    private String personSelected;
    private String relationshipStatus;
    private String groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        //store the name of the user whose page is being viewed
        //and update the page title to reflect that data
        personSelected = getIntent().getStringExtra(PeopleActivity.MESSAGE_KEY);
        TextView titleTxt = (TextView) findViewById(R.id.title_label);
        titleTxt.setText(personSelected);

        //update the status text with the person's relationship status
        setPersonRelationship();

        //update the group options with the active user's groups
        setGroupOptions();
    }

    //Update the GUI with the current user's relationship to the user they are viewing
    private void setPersonRelationship() {
        TextView relationshipStatusText = (TextView) findViewById(R.id.friends_label);
        relationshipStatus = NOT_FRIENDS_STATUS;
        relationshipStatusText.setText(relationshipStatus);
    }

    //Update the GUI by adding the active user's groups to the options for group invitations
    private void setGroupOptions() {
        //find the spinner and create its item list
        final Spinner peopleList = (Spinner) findViewById(R.id.groups_spinner);
        peopleList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));

        //get a list of the current user's groups from the server
        fetchGroups(Global.currentUser);

        //add the results to the GUI
        /*ArrayAdapter<String> people = (ArrayAdapter<String>) peopleList.getAdapter();
        for (int i = 0; i < results.length; i++) {
            people.add(results[i].getName());
        }Removed by NGH because fetch groups should handle*/
    }

    //Return a list of the current user's groups from the server
    private void fetchGroups(String currentUser) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        /*Group[] toRet = new Group[4];
        toRet[0] = new Group("Cheaters");
        toRet[1] = new Group("Businessmen");
        toRet[2] = new Group("Secretaries");
        toRet[3] = new Group("Athletes");
        return toRet;*/
        tempContext = this;
        FetchGroupsFromServer grps = new FetchGroupsFromServer();
        grps.execute();
    }

    //Perform friend request action in response to button press
    //change the relationship status text and ask the server to send the request
    //if such a request was not already sent, otherwise, display an error
    public void buttonRequest(View view) {
            TextView relationshipStatusText = (TextView) findViewById(R.id.friends_label);
            relationshipStatus = REQUEST_SENT_STATUS;
            relationshipStatusText.setText(relationshipStatus);
            createRequest(Global.currentUser, personSelected);
    }

    //Ask the server to generate a friend request from the current user to the user being viewed
    private void createRequest(String current, String selected) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        RequestFriend reqF = new RequestFriend();
        reqF.execute();
    }

    //Perform friend request action in response to button press
    //change the relationship status text and ask the server to send the request
    public void buttonInvite(View view) {
        Spinner groupOptions = (Spinner) findViewById(R.id.groups_spinner);
        groupSelected = (String) groupOptions.getSelectedItem();
            createInvite();
    }

    //Ask the server whether the recipient has already been invited to the group
    private boolean groupInviteSent(String group, String recipient) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        return false;
    }

    //Ask the server to generate a group invite to the user being viewed for the selected group
    private void createInvite() {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        RequestToGroup grp = new RequestToGroup();
        grp.execute();
    }








    /* * * * * * * * * * * * * * */
    /* Classes for Server Calls  */
    /* * * * * * * * * * * * * * */





    /* * * * * * * * */
    /*  Fetch Groups */
    /* * * * * * * * */
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
                //StringEntity requestEntity = new StringEntity(requestString.toString());
                //httpget.setEntity(requestEntity);
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
                if (result.get("status").toString().equals("401")) {
                    Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                }  else {
                    //Might be wrong, can't test lol
                    //Toast.makeText(tempContext, "You and ", selected," are now friends!", , Toast.LENGTH_LONG).show();
                    //Displays name of groups: result.get("result").get("name");

                    //add the results to the GUI
                    Spinner peopleList = (Spinner) findViewById(R.id.groups_spinner);
                    ArrayAdapter<String> people = (ArrayAdapter<String>) peopleList.getAdapter();
                    JSONArray arr = result.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        if (arr.getJSONObject(i).get("accepted").toString().equals("1")) {
                            Group newGroup = new Group(arr.getJSONObject(i).get("name").toString(),
                                    arr.getJSONObject(i).get("id").toString());
                            Global.groupNameDict.remove(newGroup.getName());
                            Global.groupNameDict.put(newGroup.getName(), newGroup);
                            people.add(newGroup.getName());
                        }
                    }
                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }


    public class RequestFriend extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = "http://wyvernzora.ninja:3000/api/friends";
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                requestString.put("id", Global.userNameDict.get(personSelected).getID());//Selected shouldn't be a string, but should be an ID from server

                //add the header and json to the http object
                StringEntity requestEntity = new StringEntity(requestString.toString());
                httppost.setEntity(requestEntity);
                httppost.setHeader("Content-Type", "application/json");
                httppost.addHeader("X-Tap-Auth", Global.auth);

                Log.v("L100", httppost.toString());

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
                    Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                } else if(result.get("status").toString().equals("500")){
                    Toast.makeText(tempContext, "Database failure!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(tempContext, "Friend request sent!", Toast.LENGTH_LONG).show();
                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

public class RequestToGroup extends AsyncTask<String, Integer, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... params) {
        //set up variables
        //Check urlRead
        String urlRead = String.format("http://wyvernzora.ninja:3000/api/groups/%s/members", Global.groupNameDict.get(groupSelected).getID());
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(urlRead);
        JSONObject requestString = new JSONObject();
        HttpResponse response = null;
        JSONObject result = null;

        //query the server
        try {
            //add the fields to the json object
            requestString.put("user", personSelected);//Selected shouldn't be a string, but should be an ID from server

            //add the header and json to the http object
            StringEntity requestEntity = new StringEntity(requestString.toString());
            httppost.setEntity(requestEntity);
            httppost.setHeader("Content-Type", "application/json; charset=utf-8");
            httppost.addHeader("X-Tap-Auth", Global.auth);


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
                Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
            } else if (result.get("status").toString().equals("400")) {
                Toast.makeText(tempContext, "User is already a member of this group!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(tempContext, "Group request sent!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
        }
    }
}
}