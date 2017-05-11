package com.studio.jarn.backfight;


import java.util.UUID;

class Player {
    String Name;
    int mFigure = 0;
    int mFigureSelected = 0;
    String id = UUID.randomUUID().toString();

    Player(int Figure, int FigureSelected, String name) {
        mFigure = Figure;
        Name = name;
        mFigureSelected = FigureSelected;
    }

    //Needed for casting from Firebase
    Player() {
    }
}
