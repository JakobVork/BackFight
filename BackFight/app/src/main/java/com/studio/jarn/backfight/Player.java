package com.studio.jarn.backfight;



class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    private boolean mSelected = false;

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;
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
