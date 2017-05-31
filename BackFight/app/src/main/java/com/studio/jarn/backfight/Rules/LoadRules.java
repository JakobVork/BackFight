package com.studio.jarn.backfight.Rules;


import android.app.Activity;
import android.util.Log;

import com.studio.jarn.backfight.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

class LoadRules {

    private final ArrayList<Rules> mRules = new ArrayList<>();
    private final Activity activity;

    LoadRules(Activity a) {
        activity = a;
    }

    ArrayList<Rules> getRules() {

        String language = Locale.getDefault().getDisplayLanguage();
        if(language.equals("English")) LoadEnglishRules();
        else if (language.equals("dansk")) LoadDanishRules();

        return mRules;
    }

    private void LoadEnglishRules() {
        getSingleRule(R.raw.actions_en);
        getSingleRule(R.raw.attack_en);
        getSingleRule(R.raw.round_en);
        getSingleRule(R.raw.hitpoints_en);
        getSingleRule(R.raw.items_en);
        getSingleRule(R.raw.monsters_en);
        getSingleRule(R.raw.movement_en);
        getSingleRule(R.raw.credits_en);
    }

    private void LoadDanishRules() {
        getSingleRule(R.raw.actions_da);
        getSingleRule(R.raw.attack_da);
        getSingleRule(R.raw.round_da);
        getSingleRule(R.raw.hitpoints_da);
        getSingleRule(R.raw.items_da);
        getSingleRule(R.raw.monsters_da);
        getSingleRule(R.raw.movement_da);
        getSingleRule(R.raw.credits_da);
    }


    private void getSingleRule(int Rule) {

        String ruleTitle = "";
        ArrayList<String> ruleDescription = new ArrayList<>();
        Rules rule;

        InputStream is = activity.getResources().openRawResource(Rule);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    ruleTitle = line;
                    first = false;
                } else {
                    ruleDescription.add(line);
                }
            }
            if(!ruleTitle.isEmpty()) {
                rule = new Rules(ruleTitle, ruleDescription);
                mRules.add(rule);
            }
        } catch (Exception ex) {
            Log.e("ERROR", "Something wrong during CSV file read", ex);
        }
    }
}