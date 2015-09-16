package com.example.michael.tap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class MenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //set the text box containing the current user's name
        TextView currentUserText = (TextView) findViewById(R.id.current_user_text);
        currentUserText.setText(Global.currentUser);
    }

    //Perform view notifications action in response to button press
    //move into the notifications activity
    public void buttonNotifications(View view) {
        startActivity(new Intent(this, NotificationsActivity.class));
    }

    //Perform view people action in response to button press
    //move into the people activity
    public void buttonPeople(View view) {
        startActivity(new Intent(this, PeopleActivity.class));
    }

    //Perform view groups action in response to button press
    //move into the groups activity
    public void buttonGroups(View view) {
        startActivity(new Intent(this, GroupsActivity.class));
    }

    //Perform log out action in response to button press
    //move back into the parent activity
    //and reset the current user
    public void buttonLogOut(View view) {
        Global.currentUser = "";
        Global.auth = "";
        Global.id = "";
        finish();
    }
}