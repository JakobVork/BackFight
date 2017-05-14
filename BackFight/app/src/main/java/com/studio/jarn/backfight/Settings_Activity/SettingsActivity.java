package com.studio.jarn.backfight.Settings_Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studio.jarn.backfight.R;

public class SettingsActivity extends AppCompatActivity {

    public static String PROFILE_NAME_SP = "Profile Name";
    public static String AVATAR_IMAGE_SP = "Avatar Image";
    public static String AVATAR_IMAGE_SELECTED_SP = "Avatar Image Selected";
    public static String AVATAR_IMAGE_NUMBER_SP = "Avatar Image Number";
    Button mBtnBack;
    Button mBtnSave;
    EditText mProfileName;
    CustomPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        hideActionBar();
        initWidgets();
        getSavedSettings();
    }

    private void getSavedSettings() {
        SharedPreferences sharedPref = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
        mProfileName.setText(sharedPref.getString(PROFILE_NAME_SP, ""));
        mViewPager.setCurrentItem(sharedPref.getInt(AVATAR_IMAGE_NUMBER_SP, 0));
    }

    // Find the buttons in the layout file and call to make OnClickListener on them
    private void initWidgets() {
        mBtnBack = (Button) findViewById(R.id.activity_settings_btn_back);
        mBtnSave = (Button) findViewById(R.id.activity_settings_btn_save);
        mViewPager = (ViewPager) findViewById(R.id.activity_settings_vp_image);
        mProfileName = (EditText) findViewById(R.id.activity_settings_et_name);

        setOnClickListeners();
        setUpViewPager();
    }

    private void setUpViewPager() {
        mCustomPagerAdapter = new CustomPagerAdapter(this);
        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager.setClipChildren(false);
        mViewPager.setOffscreenPageLimit(mCustomPagerAdapter.getCount());

        //TODO remove hardcoded
        mViewPager.setPageMargin(-200);
    }

    // Make OnClickListener to the buttons
    private void setOnClickListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    // Saves settings and go to main menu
    private void saveSettings() {
        SharedPreferences settingsSp = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsSpEditor = settingsSp.edit();
        int currentView = mViewPager.getCurrentItem();

        //saves the position and image path and the profile name
        String profileName = mProfileName.getText().toString();
        if (!profileName.equals("")) {
            settingsSpEditor.putString(PROFILE_NAME_SP, profileName);
            settingsSpEditor.putInt(AVATAR_IMAGE_NUMBER_SP, currentView);
            settingsSpEditor.putInt(AVATAR_IMAGE_SELECTED_SP, mCustomPagerAdapter.getResourceSelected(currentView));
            settingsSpEditor.putInt(AVATAR_IMAGE_SP, mCustomPagerAdapter.getResource(currentView));
            settingsSpEditor.apply();
            backToMainMenu();
        } else {
            Toast.makeText(this, R.string.settings_invalidProfileName, Toast.LENGTH_SHORT).show();
        }
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
