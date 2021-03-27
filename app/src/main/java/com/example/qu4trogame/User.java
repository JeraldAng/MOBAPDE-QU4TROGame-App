package com.example.qu4trogame;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class User {
    private String username;
    private String email;
    private String password;

    public User(){
        //this constructor is required
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword(){ return password;}

}
