package com.studio.jarn.backfight;


import android.content.Context;

import java.util.UUID;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    int actionsRemaning = 3;
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
        if (context instanceof GameActivityListener) {
            GameActivityListener gameActivityListener = (GameActivityListener) context;
            gameActivityListener.setActionCounter(actionsRemaning);
            return true;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }
}
