package com.example.michael.tap;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class NotificationsActivity extends ActionBarActivity {
    Context tempContext;
    private static final String DEFAULT_TYPE = "You do not have any notifications";
    private static final String DEFAULT_NAME = "at this time.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        //set up the list view which will contain notifications
        final ListView notificationList = (ListView) findViewById(R.id.notification_list);
        notificationList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>()));
        fetchNotifications(Global.currentUser);
        //set the currently selected notification
        TextView selectionTypeText = (TextView) findViewById(R.id.selection_type_text);
        TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
        ArrayAdapter<String> listAdapter = (ArrayAdapter<String>)notificationList.getAdapter();
        if (notificationList.getAdapter().getCount() == 0) {
            selectionTypeText.setText("TODO put a default value here");
            selectionNameText.setText("TODO put a default value here");
        }else {
            selectionTypeText.setText(listAdapter.getItem(0).split("\n")[0]);
            selectionNameText.setText(listAdapter.getItem(0).split("\n")[1]);
        }

        //add the action listener for list items
        //which which set the current selection when clicked
        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String notificationName = (String) notificationList.getAdapter().getItem(position);
                TextView selectionTypeText = (TextView) findViewById(R.id.selection_type_text);
                TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
                selectionTypeText.setText(notificationName.split("\n")[0]);
                selectionNameText.setText(notificationName.split("\n")[1]);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ListView notificationList = (ListView) findViewById(R.id.notification_list);
        ArrayAdapter<String> listAdapter = (ArrayAdapter<String>)notificationList.getAdapter();
        listAdapter.clear();
        fetchNotifications(Global.currentUser);
        TextView selectionTypeText = (TextView) findViewById(R.id.selection_type_text);
        TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
        if (notificationList.getAdapter().getCount() == 0) {
            selectionTypeText.setText(DEFAULT_TYPE);
            selectionNameText.setText(DEFAULT_NAME);
        }else {
            selectionTypeText.setText(listAdapter.getItem(0).split("\n")[0]);
            selectionNameText.setText(listAdapter.getItem(0).split("\n")[1]);
        }
    }

    //Repopulate the list of the user's notifications
    /*public Notification[] populateNotifications() {
        //get the list of results to display
        fetchNotifications(Global.currentUser);

        //get the GUI objects
        ListView peopleList = (ListView) findViewById(R.id.notification_list);
        ArrayAdapter<String> notifications = (ArrayAdapter<String>) peopleList.getAdapter();
        notifications.clear();

        //add the results to the GUI
        for (int i = 0; i < results.length; i++) {
            notifications.add(results[i].toString());
        }

        //return the list of notifications
        return results;
    }no longer needed handled in async task*/

    //Return a list of the user's notifications from the server
    private void fetchNotifications(String currentUser) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        ListFriends  lf = new ListFriends();
        lf.execute();
        ListGroup lg = new ListGroup();
        lg.execute();
    }

    //Perform accept notification action in response to button press
    public void buttonAccept(View view) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        //If accept is on friend request:
        if (((TextView) findViewById(R.id.selection_type_text)).getText().equals(Notification.FRIEND_REQUEST)) {
            AddFriendFromServer addFriend = new AddFriendFromServer();
            addFriend.execute();
        }else {
            //else if notification for group then:
            AddGroupFromServer addGroup = new AddGroupFromServer();
            addGroup.execute();
        }
    }

    //Perform decline notification action in response to button press
    public void buttonDecline(View view) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        if (((TextView) findViewById(R.id.selection_type_text)).getText().equals(Notification.FRIEND_REQUEST)) {
            //if its a friend request:
            DeleteFriendFromServer delFriend = new DeleteFriendFromServer();
            delFriend.execute();
        }else {
            //else if its group request:
            new DeleteGroupFromServer().execute();
        }
    }



    /* * * * * * * */
    /*  API CALLS  */
    /* * * * * * * */

    public class AddFriendFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
            String selectionName = selectionNameText.getText().toString();
            String urlRead = String.format("%s/users/%s", Global.globalURL, Global.userNameDict.get(selectionName).getID());
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpput = new HttpPut(urlRead);
            JSONObject requestString = new JSONObject();
            HttpResponse response = null;
            JSONObject result = null;

            //query the server
            try {
                //add the fields to the json object
                EditText textBox = (EditText) findViewById(R.id.username_text);
                requestString.put("username", textBox.getText().toString());
                //requestString.put("X-Tap-Auth", GLOBAL.Auth);

                //add the header and json to the http object
                StringEntity requestEntity = new StringEntity(requestString.toString());
                //Use header as X-Auth: GLOBAL.Auth
                httpput.setEntity(requestEntity);
                httpput.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpput);
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
                    //Might be wrong, can't test lol
                    Toast.makeText(tempContext, "You are now friends!", Toast.LENGTH_LONG).show();


                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }




    public class AddGroupFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
            String selectionName = selectionNameText.getText().toString();
            String urlRead = String.format("%s/groups/%s/members", Global.globalURL, Global.groupNameDict.get(selectionName).getID()); //group id needed
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlRead);
            JSONObject requestString = new JSONObject();
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
                httppost.setHeader("X-Tap-Auth", Global.auth);


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
                if (result == null) {
                    //do nothing
                }else if (result.get("status").toString().equals("401")) {
                    Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                } else if(result.get("status").toString().equals("500")){
                    Toast.makeText(tempContext, "Database failure!", Toast.LENGTH_LONG).show();
                } else {
                    //Selected needs to be a group
                    Toast.makeText(tempContext, "You are now a part of the group.", Toast.LENGTH_LONG).show();


                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }



    /* * * * * * * * * * * * * * * */
    /*  Deletion of Friends/Groups */
    /* * * * * * * * * * * * * * * */
    public class DeleteFriendFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
            String selectionName = selectionNameText.getText().toString();
            String urlRead = String.format("%s/friends/%s", Global.globalURL, Global.userNameDict.get(selectionName).getID());
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpdelete = new HttpDelete(urlRead);
            //JSONObject requestString = new JSONObject();
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
                //Use header as X-Auth: GLOBAL.Auth
                //httpdelete.setEntity(requestEntity);
                httpdelete.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpdelete);
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
                }else if (result.get("status").toString().equals("401")) {
                        Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                    } else {
                    //Might be wrong, can't test lol
                    Toast.makeText(tempContext, "You are no longer friends!", Toast.LENGTH_LONG).show();


                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }



    //Group Deletion
    public class DeleteGroupFromServer extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
            String selectionName = selectionNameText.getText().toString();
            String urlRead = String.format("%s/groups/%s/members", Global.globalURL, Global.groupNameDict.get(selectionName).getID()); //group id needed
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpdelete = new HttpDelete(urlRead);
            //JSONObject requestString = new JSONObject();
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
                //Use header as X-Auth: GLOBAL.Auth
                //httpdelete.setEntity(requestEntity);
                httpdelete.setHeader("X-Tap-Auth", Global.auth);


                //query the server
                response = httpclient.execute(httpdelete);
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
                    //Might be wrong, can't test lol
                    Toast.makeText(tempContext, "You have left the group", Toast.LENGTH_LONG).show();


                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }




    //Listing Friends & Groups to populate notifications

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
                } else if(result.get("status").toString().equals("500")){
                    Toast.makeText(tempContext, "Database failure!", Toast.LENGTH_LONG).show();
                } else {
                    ListView lf = (ListView)findViewById(R.id.notification_list);
                    ArrayAdapter<String> arrad = (ArrayAdapter<String>)lf.getAdapter();
                    JSONArray arr = result.getJSONArray("result");
                    for(int i=0; i<arr.length(); i++){
                        if(arr.getJSONObject(i).get("accepted").toString().equals("0")){
                            //add group to global dictionary
                            String uName = arr.getJSONObject(i).get("uname").toString();
                            String uId = arr.getJSONObject(i).get("userId").toString();
                            User newUser = new User(uName, uId);
                            Global.userNameDict.remove(uName);
                            Global.userNameDict.put(uName, newUser);
                            //add notification to GUI
                            Notification newNot = new Notification(Notification.FRIEND_REQUEST, uName);
                            if (arrad.isEmpty()) {
                                TextView selectionTypeText = (TextView) findViewById(R.id.selection_type_text);
                                TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
                                selectionTypeText.setText(newNot.getType());
                                selectionNameText.setText(newNot.getName());
                            }
                            arrad.add(newNot.toString());
                        }
                    }


                }
            }catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }



    public class ListGroup extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //this crashes program Toast.makeText(tempContext, "About to do...", Toast.LENGTH_LONG).show();
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
                } else if (result.get("status").toString().equals("401")) {
                        Toast.makeText(tempContext, "Authorization Denied. Contact customer support to resolve this issue.", Toast.LENGTH_LONG).show();
                    } else if(result.get("status").toString().equals("500")){
                        Toast.makeText(tempContext, "Database failure!", Toast.LENGTH_LONG).show();
                    } else {
                        //Selected needs to be a group
                        //Toast.makeText(tempContext, "You are now a part of the group.", Toast.LENGTH_LONG).show();
                        ListView lg = (ListView)findViewById(R.id.notification_list);
                        ArrayAdapter<String> arrad = (ArrayAdapter<String>)lg.getAdapter();
                        //Selected needs to be a group
                        //Toast.makeText(tempContext, "You are now a part of the group.", Toast.LENGTH_LONG).show();
                        JSONArray arr = result.getJSONArray("result");
                        for(int i=0; i<arr.length(); i++){
                            if(arr.getJSONObject(i).get("accepted").toString().equals("0")){
                                //add group to global dictionary
                                String gName = arr.getJSONObject(i).get("name").toString();
                                String gId = arr.getJSONObject(i).get("id").toString();
                                Group newGroup = new Group(gName, gId);
                                Global.groupNameDict.remove(gName);
                                Global.groupNameDict.put(gName, newGroup);
                                //add notification to GUI
                                Notification newNot = new Notification(Notification.GROUP_INVITE, gName);
                                if (arrad.isEmpty()) {
                                    TextView selectionTypeText = (TextView) findViewById(R.id.selection_type_text);
                                    TextView selectionNameText = (TextView) findViewById(R.id.selection_name_text);
                                    selectionTypeText.setText(newNot.getType());
                                    selectionNameText.setText(newNot.getName());
                                }
                                arrad.add(newNot.toString());
                            }
                        }
                        //Toast.makeText(tempContext, result.toString(), Toast.LENGTH_LONG).show();

                    }
            }catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }


}