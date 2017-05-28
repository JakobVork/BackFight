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

    private ArrayList<Rules> mRules = new ArrayList<>();
    private Activity activity;

    LoadRules(Activity a) {
        activity = a;
    }

    ArrayList<Rules> getRules() {

        String language = Locale.getDefault().getDisplayLanguage();
        if(language.equals("english")) LoadEnglishRules();
        else if (language.equals("dansk")) LoadDanishRules();

        return mRules;
    }

    public void LoadEnglishRules(){
        getSingleRule(R.raw.actions);
        getSingleRule(R.raw.attack);
        getSingleRule(R.raw.round);
        getSingleRule(R.raw.hitpoints);
        getSingleRule(R.raw.items);
        getSingleRule(R.raw.monsters);
        getSingleRule(R.raw.movement);
        getSingleRule(R.raw.credits);
    }

    public void LoadDanishRules(){
        getSingleRule(R.raw.actions);
        getSingleRule(R.raw.angrib);
        getSingleRule(R.raw.round);
        getSingleRule(R.raw.hitpoints);
        getSingleRule(R.raw.items);
        getSingleRule(R.raw.monsters);
        getSingleRule(R.raw.movement);
        getSingleRule(R.raw.credits);
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