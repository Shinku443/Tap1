package com.example.michael.tap;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

//TODO (NGH) - this should include optionality to activate a group for receiving vibrations
public class GroupActivity extends ActionBarActivity {
    Context tempContext;
    private String groupSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        //store the name of the group whose page is being viewed
        //and update the page title to reflect that data
        groupSelected = getIntent().getStringExtra(GroupsActivity.MESSAGE_KEY);
        TextView titleTxt = (TextView) findViewById(R.id.title_label);
        titleTxt.setText(groupSelected);
    }

    //Perform leave group action in response to button press
    public void buttonLeave(View view) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        tempContext = this;
        LeaveGroup lg = new LeaveGroup();
        lg.execute();
        finish();
    }

    //Perform tap action in response to button press
    //record the length of the button press and
    //send it as a vibration of that length to
    //all members of the current group
    public void buttonTap(View view) {
        //TODO - this should actually be implemented by a call to the server; this method is a stub
        finish();
    }






    /* Server Calls */


    public class LeaveGroup extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            //set up variables
            //Check urlRead
            String urlRead = String.format("%s/groups/%s/members", Global.globalURL, Global.groupNameDict.get(groupSelected).getID());
            //Global.groupID);
            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpdelete = new HttpDelete(urlRead);
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
                httpdelete.setHeader("X-Tap-Auth", Global.auth); //Need the user auth


                //query the server
                response = httpclient.execute(httpdelete);
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
                }else if (result.get("status").toString().equals("200")) {
                    //Might be wrong, can't test lol
                    Toast.makeText(tempContext, "You have left the group!", Toast.LENGTH_LONG).show();
                }else {
                    Log.v("V003", result.get("status").toString());
                }
            } catch (JSONException e) {
                Toast.makeText(tempContext, "Creation error; try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}