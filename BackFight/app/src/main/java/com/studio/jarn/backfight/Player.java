package com.studio.jarn.backfight;


import android.content.Context;
import android.view.View;

class Player {
    String mName;
    int mFigure = 0;
    int mFigureSelected = 0;
    int mActionsRemaining = 3;
    int mActionsPerTurn = 3;
    String id;

    Player(int Figure, int FigureSelected, String name, String uuid) {
        mFigure = Figure;
        mName = name;
        mFigureSelected = FigureSelected;
        id = uuid;
    }

    //Needed for casting from Firebase
    Player() {
    }

    boolean canTakeAction() {
        return mActionsRemaining > 0;
    }

    void takeAction(Context context, View view) {
        PlayerGameActivityListener playerGameActivityListener;
        PlayerGameViewListener playerGameViewListener;
        mActionsRemaining--;

        playerGameActivityListener = (PlayerGameActivityListener) context;
        playerGameViewListener = (PlayerGameViewListener) view;
        playerGameActivityListener.setActionCounter(mActionsRemaining);
        if (mActionsRemaining <= 0)
            playerGameViewListener.actionTaken();
    }

    void resetActions() {
        mActionsRemaining = mActionsPerTurn;
    }
}
