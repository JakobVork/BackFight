package com.studio.jarn.backfight;


import android.util.Log;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemFactory;

import java.util.ArrayList;
import java.util.List;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    private boolean mSelected = false;

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
}
