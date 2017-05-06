package com.studio.jarn.backfight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LobbyActivity extends AppCompatActivity {

    Button mbtnBack;
    Button mbtnStart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        hideActionBar();
        initButtons();
        getData();
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mbtnBack = (Button) findViewById(R.id.activity_lobby_btn_back);
        mbtnStart = (Button) findViewById(R.id.activity_lobby_btn_start);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToNewGame();
            }
        });

        mbtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame();
            }
        });
    }

    // Starts the game
    private void startGame() {
        Intent StartGameIntent = new Intent(this, GameActivity.class);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), "String");
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), true);
        GridType gridType = GridType.DefaultGrid;
        /*GridType gridType = GridType.Maze;*/
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDTYPE), gridType);

        startActivity(StartGameIntent);
    }

    // Go back to main menu
    private void backToNewGame() {
        finish();
    }

    // Hides the actionbar
    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }

    // Get data from database
    private void getData() {
    }
}
