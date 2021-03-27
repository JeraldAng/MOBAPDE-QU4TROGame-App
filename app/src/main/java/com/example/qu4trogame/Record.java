package com.example.qu4trogame;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class Record {
    private String username;
    private int wins;
    private int losses;

    public Record(){
        //this constructor is required
    }

    public Record(String username, int wins, int losses) {
        this.username = username;
        this.wins = wins;
        this.losses = losses;
    }


    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }
    public int getLosses() {
        return losses;
    }

    public void setWins(int wins){
        this.wins += wins;
    }

    public void setLosses(int losses){
        this.losses += losses;
    }

}
