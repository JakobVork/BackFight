package com.studio.jarn.backfight.StartScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.jarn.backfight.MainMenuActivity;
import com.studio.jarn.backfight.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideActionBar();
        makeDelayBeforeMainStarts();
        StartAnimation();

    }


    private void StartAnimation() {
        ImageView mIcon = (ImageView) findViewById(R.id.activity_splash_img_title);
        TextView mBackFight = (TextView) findViewById(R.id.activity_splash_tv_title);
        Animation StartAnimationIcon = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation StartAnimationText = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mBackFight.startAnimation(StartAnimationText);
        mIcon.startAnimation(StartAnimationIcon);
    }

    private void makeDelayBeforeMainStarts() {
        int sSplash_time_out = 6000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent MainMenuIntent = new Intent(SplashActivity.this, MainMenuActivity.class);
                startActivity(MainMenuIntent);
                finish();
            }
        }, sSplash_time_out);
    }

    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }
}
