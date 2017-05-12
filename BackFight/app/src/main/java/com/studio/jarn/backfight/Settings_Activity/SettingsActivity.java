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

import com.studio.jarn.backfight.R;

public class SettingsActivity extends AppCompatActivity {

    private static String sProfileNameSP = "Profile Name";
    private static String sAvatarImgNumberSP = "Avatar Image number";
    private static String sAvatarImgPathSP = "Avatar Image Path";
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
        initViews();
        getSavedSettings();
    }

    private void getSavedSettings() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mProfileName.setText(sharedPref.getString(sProfileNameSP, ""));
        mViewPager.setCurrentItem(sharedPref.getInt(sAvatarImgNumberSP, 0));
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initViews() {
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
        SharedPreferences settingsSP = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsSPEditor = settingsSP.edit();
        int CurrentView = mViewPager.getCurrentItem();

        //saves the position and image path and the profile name
        settingsSPEditor.putString(sProfileNameSP, mProfileName.getText().toString());
        settingsSPEditor.putInt(sAvatarImgNumberSP, CurrentView);
        settingsSPEditor.putString(sAvatarImgPathSP, mCustomPagerAdapter.getResourcePath(CurrentView));
        settingsSPEditor.apply();
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
