package com.example.qu4trogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.os.Bundle;

public class MainGame extends AppCompatActivity {
    Button btnPause;
    TextView txtUsername;
    private FirebaseAuth auth;
    DatabaseReference databaseRecords;
    List<Record> records;
    MediaPlayer mp, mp2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        auth = FirebaseAuth.getInstance();

        txtUsername = findViewById(R.id.txtUsername);
        btnPause = findViewById(R.id.btnPause);

        loadUser();

        mp = MediaPlayer.create(this, R.raw.click);
        databaseRecords = FirebaseDatabase.getInstance().getReference("records");
        records = new ArrayList<>();

        // textViews and button widgets to be updated in the relative layout
        tv_top_pile = (TextView) findViewById(R.id.textView_top_pile);
        tv_cards_rem = (TextView) findViewById(R.id.textView_cards_rem);
        bt_1 = (Button) findViewById(R.id.button_1);
        bt_2 = (Button) findViewById(R.id.button_2);
        bt_3 = (Button) findViewById(R.id.button_3);
        bt_4 = (Button) findViewById(R.id.button_4);
        bt_5 = (Button) findViewById(R.id.button_5);
        bt_6 = (Button) findViewById(R.id.button_6);
        bt_7 = (Button) findViewById(R.id.button_7);
        tv_opponent_cards = (TextView) findViewById(R.id.textView1);
        make_button_arraylst();
        make_all_Buttons_Visible();

        // populate arraylist of cards deck as well as user's and computer's hands of cards
        populate_deckArrayLst();
        userArrayLst = populate_PlayerArrayLst();
        compArrayLst = populate_PlayerArrayLst();

        // set the next card on the deck to the top of pile
        int top_of_deck_index = userArrayLst.size() + compArrayLst.size();

        // check if the first card is wild, then pick a random color
        if (deckArrayLst.get(top_of_deck_index).get_card_color() == Color.parseColor("#A7A7A7"))
            deckArrayLst.get(top_of_deck_index).set_card_color(colors[new Random().nextInt(4)]);

        set_top_of_pile(deckArrayLst.get(top_of_deck_index));

        // update views in the relative layout, including textviews and button widgets
        update_layout();

        mp = MediaPlayer.create(getApplicationContext(),R.raw.click);
        mp2 = MediaPlayer.create(getApplicationContext(),R.raw.click);
    }

    private void loadUser(){
        FirebaseUser user = auth.getCurrentUser();

        if (user != null){
            if (user.getDisplayName() == null || user.getDisplayName().isEmpty()){
                txtUsername.setText("Guest");
            }
            else{
                txtUsername.setText("" + user.getDisplayName());
            }
        }
    }

    public void pause(View view){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pause_dialog, null);
        dialogBuilder.setView(dialogView);
        final Button btnQuit = (Button) dialogView.findViewById(R.id.btnQuit);
        final Button btnResume = (Button) dialogView.findViewById(R.id.btnResume);

        final AlertDialog b = dialogBuilder.create();
        b.show();


        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                finish();
            }
        });
        btnResume.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // create card class which comprises its numeric value and color
    private class Card{
        private String card_val;
        private int card_color;
        public Card(String card_val, int card_color){
            this.card_val = card_val;
            this.card_color = card_color;
        }
        public String get_card_val(){
            return this.card_val;
        }
        public int get_card_color(){
            return this.card_color;
        }
        public void set_card_color(int color){this.card_color = color;}
    }

    // arraylist of cards in the deck
    ArrayList<Card> deckArrayLst = new ArrayList<>();

    // number of cards in user's hand
    static int player_total = 7;
    // arraylist of cards in user's hand
    ArrayList<Card> userArrayLst = new ArrayList<>(player_total);

    // arraylist of cards in computer's hand
    ArrayList<Card> compArrayLst = new ArrayList<>(player_total);

    public static int ARBITRARY_INT = 19955991;

    // number of card colors available
    private static int num_colors = 4;

    //pastel red, pastel blue, pastel green, pastel yellow
    private static int[] colors = { Color.rgb(255,105,97), Color.rgb(165, 225, 230), Color.rgb(170,233,164), Color.rgb(255,230,99) };


    //textViews and buttons widgets to be updated in the UI
    TextView tv_top_pile, tv_cards_rem, tv_opponent_cards;
    Button bt_1, bt_2, bt_3, bt_4, bt_5, bt_6, bt_7;
    ArrayList<Button> buttonArrayLst = new ArrayList<>(player_total);

    // tracks current top of pile
    Card top_of_pile;

    // arraylist to be traversed when accessing button widgets in this implementation
    public void make_button_arraylst(){
        buttonArrayLst.add(bt_1);
        buttonArrayLst.add(bt_2);
        buttonArrayLst.add(bt_3);
        buttonArrayLst.add(bt_4);
        buttonArrayLst.add(bt_5);
        buttonArrayLst.add(bt_6);
        buttonArrayLst.add(bt_7);
    }

    // populate arraylist of cards randomly on the deck
    // with two each of the numbers 0 to 9 and respective colors
    // number of cards orginally on the deck is 50;
    public void populate_deckArrayLst(){
        for(int i=0; i<8; i++){
            for (int j=0; j<2; j++){
                for (int k = 0; k < num_colors; k++) {
                    Card NewCd = new Card(String.valueOf(i), colors[k]);
                    deckArrayLst.add(NewCd);
                }
            }
        }

        for (int k = 0; k < num_colors; k++) {
            Card NewCd = new Card("+2", colors[k]);
            deckArrayLst.add(NewCd);
        }

        for (int k = 0; k < num_colors; k++) {
            Card NewCd = new Card("+4", colors[k]);
            deckArrayLst.add(NewCd);
        }

        for (int k = 0; k < num_colors; k++) {
            Card NewCd = new Card( "🚫", colors[k]);
            deckArrayLst.add(NewCd);
        }

        for (int k = 0; k < num_colors; k++) {
            Card NewCd = new Card( "W", Color.parseColor("#A7A7A7"));
            deckArrayLst.add(NewCd);
        }

        // shuffle the list
        Collections.shuffle(deckArrayLst);
    }

    /*
     * picks a random value between zero and a given number using the java Random class
     * @param last_val last possible value
     */
    public int getRandomCardIndex(int last_val){
        Random r = new Random();
        return r.nextInt(last_val);
    }

    // populate array of cards randomly on player's array
    public ArrayList<Card> populate_PlayerArrayLst() {
        ArrayList <Card> NewArrLst = new ArrayList<>(player_total);
        for (int i = 0; i < player_total; i++) {

            // pick a random card from the deck if there is more than one card available
            int rand_idx;
            if (deckArrayLst.size() > 1) {
                rand_idx = getRandomCardIndex(deckArrayLst.size() - 1);
            } else {
                rand_idx = 0;
            }

            // place a new card in the player's array and
            Card NewPlayerCard  = new Card( deckArrayLst.get(rand_idx).get_card_val()
                    ,deckArrayLst.get(rand_idx).get_card_color());
            NewArrLst.add(NewPlayerCard);
            if (!deckArrayLst.isEmpty()){
                deckArrayLst.remove(rand_idx);
            } else {
                printDeckEmpty_Restart();
            }
        }
        return NewArrLst;
    }

    // restart the game from scratch
    public void restart_game(){
        finish();
        startActivity(getIntent());
    }

    //update wins
    public static void setWin(final String operation) {
        DatabaseReference recordRef =  FirebaseDatabase.getInstance().getReference("records")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wins");
        recordRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer score = mutableData.getValue(Integer.class);
                if (score == null) {
                    return Transaction.success(mutableData);
                }

                if (operation.equals("increaseScore")) {
                    mutableData.setValue(score + 1);
                } else if (operation.equals("decreaseScore")){
                    mutableData.setValue(score - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
        });
    }

    //update losses
    public static void setLoss(final String operation) {
        DatabaseReference recordRef =  FirebaseDatabase.getInstance().getReference("records")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("losses");
        recordRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer score = mutableData.getValue(Integer.class);
                if (score == null) {
                    return Transaction.success(mutableData);
                }

                if (operation.equals("increaseScore")) {
                    mutableData.setValue(score + 1);
                } else if (operation.equals("decreaseScore")){
                    mutableData.setValue(score - 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {}
        });
    }

    // print toast message indicating a winner, if that is the case
    public void check_winner(){
        if (userArrayLst.size() == 0){
            Toast.makeText(this,  "Congratulations! You won!", Toast.LENGTH_SHORT).show();
            setWin("increaseScore");
            //FirebaseDatabase.getInstance().getReference("records")
              //      .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("wins").setValue(0);
            //restart_game();
            finish();
            startActivity(new Intent(this, WinActivity.class));
            return;

        } else if (compArrayLst.size() == 0) {
            Toast.makeText(this,  "Sorry. You lost! Try again next time!",
                    Toast.LENGTH_SHORT).show();
            setLoss("increaseScore");
            //restart_game();
            finish();
            startActivity(new Intent(this, LoseActivity.class));
            return;
        }
    }

    // set textview showing the card on top of the deck
    public void set_top_of_pile(Card top_card){
        if (top_card != null){
            top_of_pile = new Card(top_card.get_card_val(), top_card.get_card_color());
            tv_top_pile.setText("" + top_card.get_card_val());
            tv_top_pile.setBackgroundColor(top_card.get_card_color());
        }
    }

    /*
     * compares the text and color of a button with that on top of the pile
     * @param player user or computer
     * @param index index of player's arraylist Card element whose values are to be compared with
     *              that of the card on top of the pile
     * @return whether a card is a match, an invalid move, a match+2, or a match_skip
     */
    public String same_as_top_pile(String player, int index) {
        if (player.equals("user")) {
            if ( (userArrayLst.get(index).get_card_val().equals(top_of_pile.get_card_val()) &&
                    !userArrayLst.get(index).get_card_val().equals("W")) ||
                    userArrayLst.get(index).get_card_color() == top_of_pile.get_card_color() ) {
                if (userArrayLst.get(index).get_card_val().equals("+2")){
                    return "match_plus2";
                }
                else if (userArrayLst.get(index).get_card_val().equals("+4")){
                    return "match_plus4";
                }
                else if (userArrayLst.get(index).get_card_val().equals("🚫")){
                    return "match_skip";
                }
                return "match";
            }
            else if (userArrayLst.get(index).get_card_color() == Color.parseColor("#A7A7A7")){
                wild_choose_a_color(index);
                return "wild";
            }
            return "invalid";

        } else if (player.equals("computer")) {
            if ( (compArrayLst.get(index).get_card_val().equals(top_of_pile.get_card_val())) &&
                    !compArrayLst.get(index).get_card_val().equals("W") ||
                    compArrayLst.get(index).get_card_color() == top_of_pile.get_card_color() ) {
                if (compArrayLst.get(index).get_card_val().equals("+2")){
                    return "match_plus2";
                }
                else if (compArrayLst.get(index).get_card_val().equals("+4")){
                    return "match_plus4";
                }
                else if (compArrayLst.get(index).get_card_val().equals("🚫")){
                    return "match_skip";
                }
                return "match";
            }
            else if (compArrayLst.get(index).get_card_color() == Color.parseColor("#A7A7A7")){
                compArrayLst.get(index).set_card_color(colors[new Random().nextInt(4)]);
                update_layout();
                return "match";
            }
        }
        return "invalid";
    }

    // updates the textview showing cards remaining
    public void update_cards_rem_textView(){
        tv_cards_rem.setText(String.valueOf(deckArrayLst.size()) + " cards");
    }

    // for use in case game is restarted: make all buttons visible and
    // enabled
    public void make_all_Buttons_Visible(){
        for (int i=0; i<7; i++){
            buttonArrayLst.get(i).setVisibility(View.VISIBLE);
            buttonArrayLst.get(i).setEnabled(true);
        }
    }

    /*
     * enables and makes the buttons corresponding to cards on user's hand visible
     * disables others
     */
    public void update_buttons(){
        for (int i=0; i<userArrayLst.size(); i++){
            buttonArrayLst.get(i).setEnabled(true);
            buttonArrayLst.get(i).setVisibility(View.VISIBLE);
            buttonArrayLst.get(i).setText(String.valueOf(userArrayLst.get(i).get_card_val()));
            buttonArrayLst.get(i).setBackgroundColor(
                    userArrayLst.get(i).get_card_color() );
        }
        for (int j = userArrayLst.size(); j<player_total; j++){
            buttonArrayLst.get(j).setEnabled(false);
            buttonArrayLst.get(j).setVisibility(View.INVISIBLE);
        }
    }

    /*
     * update views in the relative layout, including textviews and button widgets
     */
    public void update_layout(){

        // updates textview showing the number of cards left in the computer's hand
        update_comp_cards_textView();

        // updates the textview showing number of cards remaining on the deck
        update_cards_rem_textView();

        // updates buttons with the contents of the user's hand
        update_buttons();

        // print toast message indicating a winner, if that is the case
        check_winner();
    }

    /*
     * compares card to that on top of the pile, re-arranges buttons
     * @param index index of button view to be updated
     */
    public void user_play_game(int index){
        // if the card is the same as that on the top pile,
        // remove both from the user's hand and deck respectively
        // TopCard is a placeholder for comparing, because the top of pile changes before checking card type

        update_layout();
        String TopCard = same_as_top_pile("user", index);

        if (TopCard.contains("match")){
            set_top_of_pile(userArrayLst.get(index));
            userArrayLst.remove(index);
            if (!userArrayLst.isEmpty()){
                // the opponent (computer) takes its turn if it is not a special card
                if (TopCard.equals("match")) {
                    computer_play_game();
                }
                // if the card is +2, make the opponent draw twice, then give the turn
                else if (TopCard.equals("match_plus2")){
                    for (int i=0; i<2; i++) {
                        player_pick_cd("computer");
                    }
                    computer_play_game();
                }
                // if the card is +4, make the opponent draw four times, then give the turn
                else if (TopCard.equals("match_plus4")){
                    for (int i=0; i<4; i++) {
                        player_pick_cd("computer");
                    }
                    computer_play_game();
                }
                // if the card is skip, do not let the opponent play a turn
                else if (TopCard.equals("match_skip")){
                    // do nothing
                }
            }
        }
        else if (TopCard.equals("wild")){
            // do nothing, the function does not wait for the alertdialog to close, so execute the cancellation-
            // and wild card play in wild_choose_a_color()
        }
        else {
            Toast.makeText(this, "Select card of same color or value as top of pile" +
                    " or pick card from deck", Toast.LENGTH_SHORT).show();
        }

        update_layout();
    }

    // executes a game move after each click of the user, that is, at the opponent's (computer's)
    // turn
    public void computer_play_game(){
        // after populating its array, the computer is going to traverse the comparraylst
        // to find a card which is of the same value or color as the card on top of the pile
        // if it doesn't find any such card, it picks a card from the deck

        update_layout();
        boolean found_matching_card = false;
        int match_index = ARBITRARY_INT;
        for (int idx = 0; idx<compArrayLst.size(); idx++){
            if (same_as_top_pile("computer", idx).contains("match")){
                found_matching_card = true;
                match_index = idx;
                break;
            }
        }
        if (!found_matching_card){
            // picks a random card from the deck and places it on the computer's hand
            player_pick_cd("computer");
        } else {
            if (match_index != ARBITRARY_INT) {
                if (!compArrayLst.isEmpty()) {
                    // TopCard is a placeholder for comparing, because the top of pile changes before checking card type
                    String TopCard = same_as_top_pile("computer", match_index);
                    set_top_of_pile(compArrayLst.get(match_index));
                    compArrayLst.remove(match_index);

                    // check if the opponent puts down a normal card
                    if (TopCard.equals("match")){
                        // do nothing, end the turn
                    }
                    // if the card is +2, make the user draw twice, then give the turn
                    else if (TopCard.equals("match_plus2")){
                        for (int i=0; i<2; i++) {
                            player_pick_cd("user");
                        }
                    }
                    // if the card is +4, make the user draw four times, then give the turn
                    else if (TopCard.equals("match_plus4")){
                        for (int i=0; i<4; i++) {
                            player_pick_cd("user");
                        }
                    }
                    // if the card is skip, do not let the user play a turn
                    else if (TopCard.equals("match_skip")){
                        Toast.makeText( this, "opponent used skip!", Toast.LENGTH_SHORT ).show();
                        computer_play_game();     // prompts the computer to play another turn
                    }
                }
            }
        }
        update_layout();
    }


    public void button_1_click(View view) {
        // 0 is button's index in the arrayList
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(0);
    }


    public void button_2_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(1);
    }


    public void button_3_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(2);
    }

    public void button_4_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(3);
    }

    public void button_5_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(4);
    }

    public void button_6_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(5);
    }

    public void button_7_click(View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        user_play_game(6);
    }

    // prints out toast message if the card deck becomes empty during the game
    // and restarts accordingly
    public void printDeckEmpty_Restart(){

        if (userArrayLst.size() == compArrayLst.size()){
            Toast.makeText( this, "The card deck is empty so it's a tie! " +
                            "Game will now restart. " + "No ties allowed in Qu4tro!.",
                    Toast.LENGTH_LONG ).show();
        } else if (userArrayLst.size() > compArrayLst.size()) {
            Toast.makeText(this, "The card deck is now empty. You have more cards! " +
                            "So you're as good as a winner! Game will now restart. ",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Sorry. The card deck is now empty. " +
                            "Your opponent has " +
                            String.valueOf(compArrayLst.size() - userArrayLst.size()) +
                            " more cards than you. Game will now restart. ",
                    Toast.LENGTH_LONG).show();
        }
        restart_game();
    }

    /*
     * picks a random card from the deck and places it on the user's hand
     * @param view button widget showing "OR PICK UP CARD"
     * modified! pick card now does not pass the turn to the computer, thus when the user draws, the turn is lost
     */
    public void pick_cd_click (View view) {
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        player_pick_cd("user");
        computer_play_game();
    }

    /*
     * picks a random card from the deck and places it on the player's hand
     * @param player user or computer
     */
    public void player_pick_cd(String player) {
        // return if there is a winner already
        if (userArrayLst.size() <= 0 || compArrayLst.size() <= 0) {
            return;
        }
        if (deckArrayLst.isEmpty()) {
            printDeckEmpty_Restart();
            return;
        } else {
            int rand_num;
            if (deckArrayLst.size() > 1) {
                rand_num = getRandomCardIndex(deckArrayLst.size() - 1);
            } else {
                rand_num = 0;
            }

            // place a new card in the player's array and
            Card NewCard = new Card(deckArrayLst.get(rand_num).get_card_val(),
                    deckArrayLst.get(rand_num).get_card_color());
            if (!deckArrayLst.isEmpty()) {
                deckArrayLst.remove(rand_num);
            }
            update_cards_rem_textView();

            if (player.equals("user")) {
                if (userArrayLst.size() == player_total) {

                    // replace first card if the maximum 7 cards are on the user's hand
                    userArrayLst.set(0, NewCard);
                    Collections.shuffle(userArrayLst);
                    if (userArrayLst.size() == player_total) {
                        // make cards more aesthetically pleasing
                        Toast.makeText(this, "Cards shuffled!", Toast.LENGTH_SHORT).show();
                    }
                    // update button widget with information from new card
                    bt_1.setText(String.valueOf(NewCard.get_card_val()));
                    bt_1.setBackgroundColor(NewCard.get_card_color());
                } else {
                    // ensure that the array is not empty
                    if (!userArrayLst.isEmpty()) {
                        userArrayLst.add(NewCard);
                    }
                }
                // the opponent (computer) takes its turn
                //computer_play_game();
            } else if (player.equals("computer")) {
                if (compArrayLst.size() == player_total) {
                    // replace first card if the maximum 7 cards are on the user's hand
                    compArrayLst.set(0, NewCard);
                } else {
                    // ensure that the array is not empty
                    if (!compArrayLst.isEmpty()) {
                        compArrayLst.add(NewCard);
                    }
                }
            }
        }
        update_layout();
    }

    // updates textview at the top of the screen, informing user of the progress of its
    // opponent (the computer
    public void update_comp_cards_textView(){
        String updated_text = "CPU: " + compArrayLst.size() +
                " cards left";
        tv_opponent_cards.setText(updated_text);
    }

    // prompts the user to choose a color when tapping on the wild card (opens an alertdialog)
    // cancelling will not cause the user to lose a round
    // this function is used only when it is the user's turn
    public void wild_choose_a_color(int card_index){
        mp.start();
        if(mp.isPlaying())
            mp2.start();
        else
            mp.start();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.choose_color_dialog, null);
        dialogBuilder.setView(dialogView);
        final int index = card_index;

        final Button btnRed = (Button) dialogView.findViewById(R.id.btnRed);
        final Button btnBlue = (Button) dialogView.findViewById(R.id.btnBlue);
        final Button btnGreen = (Button) dialogView.findViewById(R.id.btnGreen);
        final Button btnYellow = (Button) dialogView.findViewById(R.id.btnYellow);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        dialogBuilder.setTitle("Play Wild Card");
        final AlertDialog b = dialogBuilder.create();
        b.show();

        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                userArrayLst.get(index).set_card_color(colors[0]);
                b.dismiss();
                set_top_of_pile(userArrayLst.get(index));
                userArrayLst.remove(index);
                computer_play_game();
            }
        });

        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                userArrayLst.get(index).set_card_color(colors[1]);
                b.dismiss();
                set_top_of_pile(userArrayLst.get(index));
                userArrayLst.remove(index);
                computer_play_game();
            }
        });

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                userArrayLst.get(index).set_card_color(colors[2]);
                b.dismiss();
                set_top_of_pile(userArrayLst.get(index));
                userArrayLst.remove(index);
                computer_play_game();
            }
        });

        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if(mp.isPlaying())
                    mp2.start();
                else
                    mp.start();
                userArrayLst.get(index).set_card_color(colors[3]);
                b.dismiss();
                set_top_of_pile(userArrayLst.get(index));
                userArrayLst.remove(index);
                computer_play_game();
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
}

