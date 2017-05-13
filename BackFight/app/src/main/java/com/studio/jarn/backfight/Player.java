package com.studio.jarn.backfight;


import java.util.UUID;
import android.content.Context;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    int actionsRemaning = 3;
    private boolean mSelected = false;
    String id = UUID.randomUUID().toString();

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;
    }

    //Needed for casting from Firebase
    Player() {
    }

    boolean takeAction(Context context) {
        if (actionsRemaning-- <= 0)
            return false;
        GameActivity gameActivity = (GameActivity) context;
        gameActivity.setActionCounter(actionsRemaning);
        return true;
    }

    void SelectPlayer() {
        mSelected = !mSelected;
    }

    int getFigure() {
        if (mSelected) return mFigureSelected;
        else return mFigure;
    }

    boolean isSelected() {
        return mSelected;
    }

    interface ActionCountListener {
        void onActionCountUpdated(int actionCountLeft);
    }
}
