package com.example.qu4trogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.example.qu4trogame.BackgroundSoundService.MusicBinder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static BackgroundSoundService musicSrv;
    EditText txtEmail, txtPassword;
    Button btnLogin, btnSignUp, btnGuest, btnQuitGame;
    ProgressDialog dialog, dialogGuest;
    private Intent playIntent;
    private FirebaseAuth auth;
    DatabaseReference databaseUsers;
    List<User> users;
    MediaPlayer mp, mp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);
        btnQuitGame = findViewById(R.id.btnQuitGame);

        dialog = new ProgressDialog(this);
        dialogGuest = new ProgressDialog(this);
        //get users
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        users = new ArrayList<>();
        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);

    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder)service;
            //get service
            musicSrv = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        // TODO
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, BackgroundSoundService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

        /*if (auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainMenu.class));
        }*/

    }

    public void login(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        final String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        if (email.isEmpty()){
            txtEmail.setError("Please enter your registered email to proceed");
            txtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter a valid email.");
            txtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()){
            txtPassword.setError("Please enter your password to proceed");
            txtPassword.requestFocus();
            return;
        }

        //user login
        dialog.setMessage("Logging in, please wait...");
        dialog.show();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    dialog.dismiss();
                    txtEmail.setText("");
                    txtPassword.setText("");
                    Toast.makeText(getApplicationContext(), "Welcome back to QU4TRO!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //if(auth.getCurrentUser().getEmail().contains(users.get()))
                   //Bundle extras = new Bundle();
                   //extras.putString("USER_ID", );

                   //intent.putExtras(extras);
                    startActivity(intent);
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Invalid credentials. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signUp(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    public void continueAsGuest(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        dialog.setMessage("Continuing as guest...");
        dialog.show();
        auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    FirebaseUser user = auth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), "Welcome to QU4TRO!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainMenu.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void quitGame(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        auth.signOut();
        stopService(playIntent);
        finish();
    }



    public static BackgroundSoundService getmusicSrv(){ return musicSrv;}
}
