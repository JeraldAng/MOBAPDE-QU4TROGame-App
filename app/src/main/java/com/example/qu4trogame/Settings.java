package com.example.qu4trogame;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Settings extends AppCompatActivity {
    ImageButton btnChangeUsername, btnChangePassword, btnMusic, btnExit;
    ProgressDialog dialog;
    DatabaseReference databaseUsers;
    List<User> users;
    FirebaseAuth auth;
    MediaPlayer mp, mp2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        btnChangeUsername = findViewById(R.id.btnChangeUsername);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnMusic = findViewById(R.id.btnMusic);
        btnExit = findViewById(R.id.btnExit);
        dialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        //Getting "users"
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        users = new ArrayList<>();
        if (!MainActivity.getmusicSrv().player.isPlaying()){
            btnMusic.setImageResource(R.drawable.duct_tape_music_off);
        }

        if(auth.getCurrentUser().getDisplayName().isEmpty()){
            btnChangeUsername.setVisibility(View.GONE);
            btnChangePassword.setVisibility(View.GONE);
        }

        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);

    }



    @Override
    protected void onStart() {
        super.onStart();

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
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void back(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        startActivity(new Intent(this, MainMenu.class));
        finish();
    }

    public void exit(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        startActivity(new Intent(this, MainMenu.class));
        finish();
    }

    public void changeUsername(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_username_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText txtNewUsername = (EditText) dialogView.findViewById(R.id.txtNewUsername);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        final Button btnUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);

        dialogBuilder.setTitle(auth.getCurrentUser().getDisplayName());
        final AlertDialog b = dialogBuilder.create();
        b.show();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                String username = txtNewUsername.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    dialog.setMessage("Changing username, please wait...");
                    dialog.show();
                    databaseUsers.child(auth.getCurrentUser().getUid()).child("username").setValue(username);
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    if (firebaseUser != null) {
                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)
                                .build();

                        firebaseUser.updateProfile(profile)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Settings.this, "Username has been changed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    dialog.dismiss();
                    finish();
                    b.dismiss();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                b.dismiss();
            }
        });
    }

    public void changePassword(View v){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.change_password_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText txtNewPassword = (EditText) dialogView.findViewById(R.id.txtNewPassword);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        final Button btnUpdate = (Button) dialogView.findViewById(R.id.btnUpdate);

        final AlertDialog b = dialogBuilder.create();
        b.show();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                String password = txtNewPassword.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                databaseUsers.child(auth.getCurrentUser().getUid()).child("password").setValue(password);
                if (user!=null){
                    dialog.setMessage("Changing password, please wait...");
                    dialog.show();
                    user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Password has been changed", Toast.LENGTH_SHORT).show();
                                auth.signOut();
                                finish();
                            }
                            else{
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Password could not be changed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    b.dismiss();
                }
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                b.dismiss();
            }
        });

    }


    public void toggleMusic(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        ImageButton btn = (ImageButton) findViewById(R.id.btnMusic);
        Drawable drawable = btn.getDrawable();
        if (drawable.getConstantState().equals(getResources().getDrawable(R.drawable.duct_tape_music_on).getConstantState())){
            ((ImageButton) view).setImageResource(R.drawable.duct_tape_music_off);
            MainActivity.getmusicSrv().pausePlayer();
        }
        else{
            ((ImageButton) view).setImageResource(R.drawable.duct_tape_music_on);
            MainActivity.getmusicSrv().resumePlayer();
        }

    }
}
