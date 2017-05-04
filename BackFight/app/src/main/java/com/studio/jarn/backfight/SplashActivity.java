package com.studio.jarn.backfight;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    // Welcome screen is there in 4000 milliseconds = 4 seconds
    private static int SPLASH_TIME_OUT = 6000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideActionBar();
        makeDelayBeforeMainStarts();
        StartAnimation();

    }

    private void StartAnimation() {
        TextView mBackFight = (TextView) findViewById(R.id.tvwBackfight);
        Animation StartAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mBackFight.startAnimation(StartAnimation);
    }

    private void makeDelayBeforeMainStarts() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainMenuIntent = new Intent(SplashActivity.this, MainMenuActivity.class);
                startActivity(MainMenuIntent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }

    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }
}
