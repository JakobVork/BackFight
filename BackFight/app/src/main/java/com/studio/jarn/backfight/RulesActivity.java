package com.studio.jarn.backfight;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RulesActivity extends AppCompatActivity {

    Button mbtnBack;

    //Maybe make a listview of the rules?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        hideActionBar();
        initButtons();
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mbtnBack = (Button) findViewById(R.id.btnBackRules);

        setOnClickListener();
    }

    private void setOnClickListener() {
        mbtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });
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
