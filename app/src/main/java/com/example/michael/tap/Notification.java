package com.example.michael.tap;

/**
 * Created by Nick Hauser on 2/9/2015.
 */
//TODO - this class is a stub used for testing; it likely needs improvement
public class Notification {
    public static final String FRIEND_REQUEST = "You have been invited to be friends with";
    public static final String GROUP_INVITE = "You have been invited to join the group";

    private String type;
    private String name;

    public Notification(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return type + "\n" + name;
    }
}