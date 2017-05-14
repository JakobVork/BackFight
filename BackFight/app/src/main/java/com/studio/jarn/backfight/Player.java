package com.studio.jarn.backfight;


import android.content.Context;
import android.view.View;

import java.util.UUID;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    int actionsRemaining = 3;
    int actionsPerTurn = 3;
    String id = UUID.randomUUID().toString();

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;
    }

    //Needed for casting from Firebase
    Player() {
    }

    boolean canTakeAction() {
        return actionsRemaining > 0;
    }

    void takeAction(Context context, View view) {
        PlayerGameActivityListener playerGameActivityListener;
        PlayerGameViewListener playerGameViewListener;
        actionsRemaining--;

        playerGameActivityListener = (PlayerGameActivityListener) context;
        playerGameViewListener = (PlayerGameViewListener) view;
        playerGameActivityListener.setActionCounter(actionsRemaining);
        if (actionsRemaining <= 0)
            playerGameViewListener.actionTaken();
    }

    void resetActions() {
        actionsRemaining = actionsPerTurn;
    }
}
