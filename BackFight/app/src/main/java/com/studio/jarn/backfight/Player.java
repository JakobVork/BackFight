package com.studio.jarn.backfight;


class Player {
    private final int mFigure;
    private final int mFigureSelected;
    private boolean mSelected = false;
    String Name;

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;
    }

    void SelectPlayer(){
        if(mSelected) mSelected = false;
        else mSelected = true;
    }

    int getFigure() {
        if(mSelected) return mFigureSelected;
        else return mFigure;
    }

    boolean isSelected(){
        return mSelected;
    }
}
