package com.studio.jarn.backfight;


import android.content.Context;
import android.view.View;

import com.studio.jarn.backfight.Items.GameItem;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public List<GameItem> PlayerItems;
    String mName;
    int mFigure = 0;
    int mFigureSelected = 0;
    int mActionsRemaining = 3;
    String id;
    private int mActionsPerTurn = 3;

    Player(int Figure, int FigureSelected, String name, String uuid) {
        mFigure = Figure;
        mName = name;
        mFigureSelected = FigureSelected;
        id = uuid;

        PlayerItems = new ArrayList<>();
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
