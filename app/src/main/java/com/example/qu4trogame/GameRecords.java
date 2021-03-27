package com.example.qu4trogame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GameRecords extends AppCompatActivity {
    TextView txtFirst, txtSecond, txtThird;
    ListView listViewRecords;
    DatabaseReference databaseRecords;
    List<Record> records;
    FirebaseAuth auth;
    RecordList recordAdapter;
    MediaPlayer mp, mp2;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_records);

        txtFirst = findViewById(R.id.txtFirst);
        txtSecond = findViewById(R.id.txtSecond);
        txtThird = findViewById(R.id.txtThird);
        listViewRecords = findViewById(R.id.listViewRecords);
        auth = FirebaseAuth.getInstance();
        //Getting "records" table
        databaseRecords = FirebaseDatabase.getInstance().getReference("records");
        records = new ArrayList<>();
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

    @Override
    protected void onStart() {
        super.onStart();

        databaseRecords.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        records.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Record record = postSnapshot.getValue(Record.class);

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.weight = 1.0f;
                            params.gravity = Gravity.TOP;
                            params.setMargins(0,0,90,0);

                            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.quicksand_regular);

                            LinearLayout linearLayout = findViewById(R.id.mainLayout);

                            LinearLayout innerLayout = new LinearLayout(getApplicationContext());
                            innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                            /*TextView text = new TextView(getApplicationContext());
                            text.setText("    "+record.getUsername() + "\n       " + record.getWins() + "      |       " + record.getLosses());
                            innerLayout.setGravity(1);
                            innerLayout.addView(text);*/

                            TextView txtUsername = new TextView(getApplicationContext());
                            txtUsername.setText("    "+record.getUsername());
                            txtUsername.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            txtUsername.setGravity(Gravity.START);
                            txtUsername.setTextColor(Color.BLACK);
                            txtUsername.setTypeface(typeface);
                            innerLayout.addView(txtUsername);

                            TextView txtWinsANDLosses = new TextView(getApplicationContext());
                            txtWinsANDLosses.setText(""+record.getWins() + "  |  "+record.getLosses());
                            txtWinsANDLosses.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                            txtWinsANDLosses.setGravity(Gravity.CENTER);
                            txtWinsANDLosses.setTextColor(Color.BLACK);
                            txtWinsANDLosses.setLayoutParams(params);

                            txtWinsANDLosses.setTypeface(typeface);
                            innerLayout.addView(txtWinsANDLosses);

                            linearLayout.addView(innerLayout);

                            //records.add(record);
                        }
                        recordAdapter = new RecordList(GameRecords.this, records);
                        listViewRecords.setAdapter(recordAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
