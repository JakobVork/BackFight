package com.studio.jarn.backfight;


import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

interface ILoadRules {
    ArrayList<Rules> getRules();
}

class LoadRules implements ILoadRules {

    private ArrayList<Rules> mRules = new ArrayList<>();
    private Activity activity;

    LoadRules(Activity a) {
        activity = a;
    }

    public ArrayList<Rules> getRules() {

        getSingleRule("credits");
        getSingleRule("movement");
        return mRules;
    }


    private void getSingleRule(String Rule) {

        String ruleTitle = "raw/" + Rule;
        ArrayList<String> ruleDescription = new ArrayList<>();
        Rules temp;
        InputStream is = activity.getResources().openRawResource(activity.getResources().getIdentifier(ruleTitle,
                "raw", activity.getPackageName()));
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
            temp = new Rules(ruleTitle, ruleDescription);
            mRules.add(temp);
        } catch (Exception ex) {
            Log.e("ERROR", "Something wrong during CSV file read", ex);
        }
    }
}