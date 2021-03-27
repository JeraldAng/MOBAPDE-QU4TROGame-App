package com.example.qu4trogame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {
    public static final String USER_ID = "USER_ID";
    TextView txtWelcome;
    Button btnLogout, btnNewGame, btnQuitGame, btnGameRecords, btnHowToPlay, btnSettings;
    private FirebaseAuth auth;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
    DatabaseReference databaseUsers;
    List<User> users;
    MediaPlayer mp, mp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        txtWelcome = findViewById(R.id.txtWelcome);
        auth = FirebaseAuth.getInstance();
        //Getting "users". If it does not exist, creates it//
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        users = new ArrayList<>();

        btnLogout = findViewById(R.id.btnLogout);
        btnQuitGame = findViewById(R.id.btnQuitGame);
        btnNewGame = findViewById(R.id.btnNewGame);
        btnGameRecords = findViewById(R.id.btnGameRecords);
        btnHowToPlay = findViewById(R.id.btnHowToPlay);
        btnSettings = findViewById(R.id.btnSettings);

        loadUserInformation();

        if(auth.getCurrentUser().getDisplayName().isEmpty() || auth.getCurrentUser().getDisplayName()==null){
            btnGameRecords.setVisibility(View.GONE);
        }
        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth == null){ //means user is not logged in
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        //attaching value event listener to our DB
        databaseUsers.addValueEventListener(new ValueEventListener() {
            // Database entries are all DataSnapshots
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                users.clear();
                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting user
                    User user = postSnapshot.getValue(User.class);
                    //adding user to the list
                    users.add(user);
                    loadUserInformation();
                    //currId = user.getUserId();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInformation(){
        FirebaseUser user = auth.getCurrentUser();

        if (user != null){
            if (user.getDisplayName() != null){
                txtWelcome.setText("Welcome " + user.getDisplayName() + "!");
            }
            else{
                txtWelcome.setText("Welcome Guest!");
            }
        }
    }

    public void newGame(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        Intent intent = new Intent(this, MainGame.class);
        startActivity(intent);
    }

    public void howToPlay(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        Intent intent = new Intent(this, howToPlay.class);
        startActivity(intent);
    }

    public void gameRecords(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        Intent intent = new Intent(this, GameRecords.class);
        startActivity(intent);
    }

    public void settings(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }

    public void quitGame(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        auth.signOut();
        finishAffinity();
    }

    public void logout(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        auth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
