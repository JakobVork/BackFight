package com.studio.jarn.backfight.Player;


import android.content.Context;
import android.view.View;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    public List<GameItem> PlayerItems;
    public String mName;
    public int mFigure = 0;
    public int mFigureSelected = 0;
    public int mActionsRemaining = 3;
    public String id;
    public int Health = 20;
    public int LineOfSight = 1;
    public int mActionsPerTurn = 3; //Needed for Firebase

    public Player(int Figure, int FigureSelected, String name, String uuid) {
        mFigure = Figure;
        mName = name;
        mFigureSelected = FigureSelected;
        id = uuid;

        PlayerItems = new ArrayList<>();
    }

    //Needed for casting from Firebase
    public Player() {
    }

    public boolean canTakeAction() {
        return mActionsRemaining > 0;
    }

    public void takeAction(Context context, View view) {
        PlayerGameActivityListener playerGameActivityListener;
        PlayerGameViewListener playerGameViewListener;
        mActionsRemaining--;

        playerGameActivityListener = (PlayerGameActivityListener) context;
        playerGameViewListener = (PlayerGameViewListener) view;
        playerGameActivityListener.setActionCounter(mActionsRemaining);
        if (mActionsRemaining <= 0)
            playerGameViewListener.actionTaken();
    }

    public void resetActions() {
        mActionsRemaining = mActionsPerTurn;
    }

    public int rollAttack() {
        int min = 1;
        int max = 1;
        for (GameItem item : PlayerItems) {
            // Check if item is a weapon
            if (item instanceof ItemWeapon) {
                min += ((ItemWeapon) item).getDmgMin();
                max += ((ItemWeapon) item).getDmgMax();
            }
        }

        Random rnd = new Random();
        return rnd.nextInt(max - min + 1) + min; // return a number between min and max (both inc.)
    }
}
