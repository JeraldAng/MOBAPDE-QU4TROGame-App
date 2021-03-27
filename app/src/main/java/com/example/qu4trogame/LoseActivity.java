package com.example.qu4trogame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoseActivity extends AppCompatActivity {
    Button btnExit;
    MediaPlayer mp, mp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose_activty);

        btnExit = findViewById(R.id.btnExit);
        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);

    }

    public void exit(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        finish();
    }
}
