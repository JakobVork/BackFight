package com.studio.jarn.backfight;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    Button mbtnBack;
    Button mbtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideActionBar();
        initButtons();
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mbtnBack = (Button) findViewById(R.id.activity_settings_btn_back);
        mbtnSave = (Button) findViewById(R.id.activity_settings_btn_save);

        setOnClickListener();
    }

    // Make OnClickListener to the buttons
    private void setOnClickListener() {
        mbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        mbtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    // Saves settings and go to main menu
    private void saveSettings() {
        //Save settings, maybe locally on the phone

        backToMainMenu();
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
