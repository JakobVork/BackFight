package com.studio.jarn.backfight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.studio.jarn.backfight.LoadGame_Activity.LoadGameActivity;
import com.studio.jarn.backfight.Rules_Activity.RulesActivity;
import com.studio.jarn.backfight.Settings_Activity.SettingsActivity;

import java.util.UUID;

import static com.studio.jarn.backfight.Settings_Activity.SettingsActivity.PROFILE_NAME_SP;

public class MainMenuActivity extends AppCompatActivity {

    public static String PHONE_UUID_SP = "Phone UUID";


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
        isItFirstTime();

    }

    private void isItFirstTime() {
        SharedPreferences sharedPref = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
        if (sharedPref.getString(PHONE_UUID_SP, "").equals("")) {
            DisplayEnterIdDialog();
        }
    }

    // Display Dialog to enter game ID
    private void DisplayEnterIdDialog() {
        //Source: http://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mainMenu_dialogTitle);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        builder.setView(input);
        builder.setMessage(R.string.mainMenu_dialogMessage);
        builder.setPositiveButton(R.string.mainMenu_dialogBtnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")) {
                    DisplayEnterIdDialog();
                } else {
                    generateUuidAndProfileName(input.getText().toString());
                }
            }
        });
        builder.show();

    }

    private void generateUuidAndProfileName(String input) {
        SharedPreferences sp = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsSpEditor = sp.edit();
        settingsSpEditor.putString(PHONE_UUID_SP, UUID.randomUUID().toString());
        settingsSpEditor.putString(PROFILE_NAME_SP, input);
        settingsSpEditor.apply();
    }

    // make a unique key for each phone
    private void makeUuid() {


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


