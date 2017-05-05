package com.studio.jarn.backfight;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NewGameActivity extends AppCompatActivity {

    Button mBtnBack;
    Button mBtnCreate;
    Button mBtnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        hideActionBar();
        initButtons();
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mBtnBack = (Button) findViewById(R.id.activity_newGame_btn_back);
        mBtnCreate = (Button) findViewById(R.id.activity_newGame_btn_create);
        mBtnJoin = (Button) findViewById(R.id.activity_newGame_btn_join);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLobby();
            }
        });

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinLobby();
            }
        });
    }

    // Join lobby, maybe make a dialog to write Id/password
    private void joinLobby() {
    }

    //Creates a lobby players can join before starting a game
    private void createLobby() {
        Intent LobbyIntent = new Intent(this, LobbyActivity.class);
        startActivity(LobbyIntent);
    }

    // Go back to main menu
    private void backToMainMenu() {
        finish();
    }

    //Hides the actionbar
    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }
}
