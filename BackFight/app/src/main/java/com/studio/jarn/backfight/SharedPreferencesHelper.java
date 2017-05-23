package com.studio.jarn.backfight;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SharedPreferencesHelper {

    private static String sPrefKeyGameIdList = "Game ID List";
    private Context mContext;

    public SharedPreferencesHelper(Context context) {
        mContext = context;
    }

    public void addGameIdToRecentGameIds(String gameId) {
        List<String> recentGames = getRecentGameIdsList();
        for (String id : recentGames) {
            if (id.equals(gameId)) return;
        }
        if (recentGames.size() > 3) {
            recentGames.remove(0);
        }
        recentGames.add(gameId);

        SharedPreferences settingsSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor settingsSpEditor = settingsSp.edit();
        settingsSpEditor.putString(sPrefKeyGameIdList, TextUtils.join(",", recentGames));
        settingsSpEditor.apply();
    }

    private List<String> getRecentGameIdsList() {

        SharedPreferences settingsSp = PreferenceManager.getDefaultSharedPreferences(mContext);
        String serialized = settingsSp.getString(sPrefKeyGameIdList, null);
        if (serialized == null)
            return new ArrayList<>();


        return new ArrayList<>(Arrays.asList(TextUtils.split(serialized, ",")));
    }

    public CharSequence[] getRecentGameIdsCharSequence() {
        List<String> list = getRecentGameIdsList();
        Collections.reverse(list);
        return list.toArray(new CharSequence[list.size()]);
    }

}
