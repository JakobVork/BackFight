package com.studio.jarn.backfight;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends AppCompatActivity {

    Button mBtnNewGame;
    Button mBtnLoadGame;
    Button mBtnRules;
    Button mBtnSettings;
    Button mBtnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        hideActionBar();
        initButtons();

    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mBtnNewGame = (Button) findViewById(R.id.activity_mainMenu_btn_newGame);
        mBtnLoadGame = (Button) findViewById(R.id.activity_mainMenu_btn_loadGame);
        mBtnSettings = (Button) findViewById(R.id.activity_mainMenu_btn_settings);
        mBtnRules = (Button) findViewById(R.id.activity_mainMenu_btn_rules);
        mBtnExit = (Button) findViewById(R.id.activity_mainMenu_btn_exit);

        setOnClickListeners();

    }

    // Make OnClickListener to the buttons
    private void setOnClickListeners() {
        mBtnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewGameActivity();
            }
        });
        
        mBtnLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoadGameActivity();
            }
        });
        
        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity();
            }
        });
        
        mBtnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRulesActivity();
            }
        });
        
        mBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeApp();
            }
        });
    }

    // Closes the app
    private void closeApp() {
        finish();
        System.exit(0);
    }

    // Open a new activity to rules
    private void openRulesActivity() {
        Intent RulesIntent = new Intent(this, RulesActivity.class);
        startActivity(RulesIntent);
    }

    // Open a new activity to Settings
    private void openSettingsActivity() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    // Open a new activity to Load Game
    private void openLoadGameActivity() {
        Intent LoadGameIntent = new Intent(this, LoadGameActivity.class);
        startActivity(LoadGameIntent);
    }

    // Open a new activity to New Game
    private void openNewGameActivity() {
        Intent NewGameIntent = new Intent(this, NewGameActivity.class);
        startActivity(NewGameIntent);
    }

    // Hide the action bar
    public void hideActionBar(){

        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }
}


