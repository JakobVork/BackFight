package com.studio.jarn.backfight;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.studio.jarn.backfight.LoadGame.LoadGameActivity;
import com.studio.jarn.backfight.NewGame.NewGameActivity;
import com.studio.jarn.backfight.Rules.RulesActivity;
import com.studio.jarn.backfight.Settings.SettingsActivity;

import java.util.UUID;

import static com.studio.jarn.backfight.Settings.SettingsActivity.AVATAR_IMAGE_NUMBER_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.AVATAR_IMAGE_SELECTED_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.AVATAR_IMAGE_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.PROFILE_NAME_SP;

public class MainMenuActivity extends AppCompatActivity {

    public static String PHONE_UUID_SP = "Phone UUID";
    private static final int NO_INTERNET_SETTINGS_INTENT = 42;

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

        if (!isConnectedToInternet()) {
            createNetErrorDialog();
        }
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected())
            return true;

        return false;
    }

    // Method copied from https://stackoverflow.com/questions/15456428/ask-user-to-start-wifi-or-3g-on-launching-an-android-app-if-not-connected-to-int
    private void createNetErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You need a network connection to use this application. Please turn on mobile network or Wi-Fi in Settings.")
                .setTitle("Unable to connect")
                .setCancelable(false)
                .setPositiveButton("Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                startActivityForResult(i, NO_INTERNET_SETTINGS_INTENT);
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                    }
                );
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Returned - Check if internet is available now.
        if (!isConnectedToInternet()) {
            createNetErrorDialog();
        }
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
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

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
        settingsSpEditor.putInt(AVATAR_IMAGE_SP, R.drawable.player_crusader);
        settingsSpEditor.putInt(AVATAR_IMAGE_NUMBER_SP, 0);
        settingsSpEditor.putInt(AVATAR_IMAGE_SELECTED_SP, R.drawable.player_crusader_selected);
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
                openNewGameActivity(v);
            }
        });

        mBtnLoadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoadGameActivity(v);
            }
        });

        mBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsActivity(v);
            }
        });

        mBtnRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRulesActivity(v);
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
    private void openRulesActivity(View view) {
        Intent RulesIntent = new Intent(this, RulesActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(RulesIntent, bundle);
        } else
            startActivity(RulesIntent);

    }

    // Open a new activity to Settings
    private void openSettingsActivity(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(settingsIntent, bundle);
        } else
        startActivity(settingsIntent);
    }

    // Open a new activity to Load Game
    private void openLoadGameActivity(View view) {
        Intent LoadGameIntent = new Intent(this, LoadGameActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(LoadGameIntent, bundle);
        } else
        startActivity(LoadGameIntent);
    }

    // Open a new activity to New Game
    private void openNewGameActivity(View view) {
        Intent NewGameIntent = new Intent(this, NewGameActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(NewGameIntent, bundle);
        } else
        startActivity(NewGameIntent);
    }

    // Hide the action bar
    public void hideActionBar(){

        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }
}


