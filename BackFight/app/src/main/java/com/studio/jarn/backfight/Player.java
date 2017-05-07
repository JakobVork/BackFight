package com.studio.jarn.backfight;


import java.lang.reflect.Type;

class Player {
    private final int Figure;
    private final int FigureSelected;
    private boolean Selected = false;
    String Name;

    Player(int figure, int figureSelected, String name) {
        Figure = figure;
        Name = name;
        this.FigureSelected = figureSelected;
    }

    void SelectPlayer(){
        if(Selected) Selected = false;
        else Selected = true;
    }

    int getFigure() {
        if(Selected) return FigureSelected;
        else return Figure;
    }

    boolean isSelected(){
        return Selected;
    }

    Player() {
    }
}
