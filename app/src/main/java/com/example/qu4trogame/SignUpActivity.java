package com.example.qu4trogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import static android.os.Debug.waitForDebugger;

public class SignUpActivity extends AppCompatActivity {
    EditText txtEmail, txtUsername, txtPassword, txtConfirmPassword;
    Button  btnSignUp, btnQuitGame;
    TextView txtLogin;
    ProgressBar progressBar;
    public FirebaseAuth auth;
    public FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference databaseUsers;
    List<User> users;
    MediaPlayer mp, mp2;
    DatabaseReference databaseRecords;
    List<Record> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        txtEmail = findViewById(R.id.txtEmail);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        txtLogin = findViewById(R.id.txtLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnQuitGame = findViewById(R.id.btnQuitGame);
        progressBar = findViewById(R.id.progressBar);

        //Getting "users". If it does not exist, creates it//
        databaseUsers = FirebaseDatabase.getInstance().getReference("users");
        users = new ArrayList<>();

        databaseRecords = FirebaseDatabase.getInstance().getReference("records");
        records = new ArrayList<>();
        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);
    }

    public void login(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        finish();
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
                records.clear();

                //iterating through all the nodes
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //getting user
                    User user = postSnapshot.getValue(User.class);
                    Record record = postSnapshot.getValue(Record.class);
                    //adding user to the list
                    users.add(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signUp(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        String username = txtUsername.getText().toString();
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        //error checking
        if (email.isEmpty()){
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtEmail.setError("Please enter a valid email.");
            txtEmail.requestFocus();
            return;
        }

        if (username.isEmpty()){
            txtUsername.setError("Username is required");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()){
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
            return;
        }

        if (password.length()<6){
            txtPassword.setError("Minimum length of password should be 6");
            txtPassword.requestFocus();
            return;
        }

        if (confirmPassword.isEmpty()){
            txtConfirmPassword.setError("Please confirm your password");
            txtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword) || !confirmPassword.equals(password)){
            txtPassword.setError("Passwords do not match");
            txtPassword.requestFocus();
            txtConfirmPassword.setError("Passwords do not match");
            txtConfirmPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        //user registration
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){ //if registration is successful


                    User user = new User(
                            txtUsername.getText().toString(),
                            txtEmail.getText().toString(),
                            txtPassword.getText().toString()
                    );

                    Record record = new Record( txtUsername.getText().toString(), 0, 0);

                    FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User registration successful!", Toast.LENGTH_LONG).show();
                            } else {
                                //display a failure message
                            }
                        }
                    });

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    if (firebaseUser != null) {
                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                .setDisplayName(txtUsername.getText().toString())
                                .build();

                        firebaseUser.updateProfile(profile)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, "User registration successful!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }

                    FirebaseDatabase.getInstance().getReference("records")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(record).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "User registration successful!", Toast.LENGTH_LONG).show();
                            } else {
                                //display a failure message
                            }
                        }
                    });
                    finish();
                }
                else{ //if failed
                    Toast.makeText(getApplicationContext(), "An error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
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
        finishAffinity();
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
