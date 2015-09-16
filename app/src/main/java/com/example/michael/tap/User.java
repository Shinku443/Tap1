package com.example.michael.tap;

/**
 * Created by Nick Hauser on 2/9/2015.
 */
//TODO - this class is a stub used for testing; it likely needs improvement
public class User {
    private String name;
    private String id;
    private String auth;

    public User(String name) {
        this.name = name;
    }

    public User(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getID(){
        return id;
    }

    public void setID(String newID){
        id = newID;
    }

    public void setAuth(String authentication){
        auth = authentication;
    }

    public String getAuth(){
        return auth;
    }
}
