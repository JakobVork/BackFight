package com.studio.jarn.backfight;


import android.util.Log;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.View;

import java.util.UUID;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    int actionsRemaining = 3;
    String id = UUID.randomUUID().toString();

    public List<GameItem> PlayerItems;

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;

        PlayerItems = new ArrayList<GameItem>();
    }

    //Needed for casting from Firebase
    Player() {
    }

    boolean canTakeAction() {
        return actionsRemaining > 0;
    }

    void takeAction(Context context, View view) {
        FirebaseGameActivityListener firebaseGameActivityListener;
        PlayerGameViewListener playerGameViewListener;
        actionsRemaining--;

        firebaseGameActivityListener = (FirebaseGameActivityListener) context;
        playerGameViewListener = (PlayerGameViewListener) view;
        firebaseGameActivityListener.setActionCounter(actionsRemaining);
        if (actionsRemaining <= 0)
            playerGameViewListener.actionTaken();
    }
}
