package com.example.michael.tap;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Nick Hauser on 2/9/2015.
 * Class exists to globally track the current user
 * This is needed for requests to the server
 */
public class Global {
    public static String currentUser;
    public static String globalURL = "http://wyvernzora.ninja:3000/api";
    public static String auth;
    public static String id;
    public static Dictionary<String, Group> groupNameDict = new Hashtable<String, Group>();
    public static Dictionary<String, User> userNameDict = new Hashtable<String, User>();

    //Validate a string based on length and content
    //the string should be less than maxLen
    //if spacesAllowed is false, spaces should not be allowed
    public static boolean isStringValid(String toValidate, int maxLen, boolean spacesAllowed) {
        //check the length
        if (toValidate.length() >= maxLen) {
            return false;
        }
        //check for spaces
        if (!spacesAllowed) {
            for (int i = 0; i < toValidate.length(); i++) {
                if (toValidate.charAt(i) == ' ') {
                    return false;
                }
            }
        }
        return true;
    }
}