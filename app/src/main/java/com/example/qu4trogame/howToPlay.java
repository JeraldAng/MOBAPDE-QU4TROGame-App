package com.example.qu4trogame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class howToPlay extends AppCompatActivity {
    MediaPlayer mp, mp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);
    }

    public void back(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        finish();
    }


}
