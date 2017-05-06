package com.studio.jarn.backfight;



class Player {
    private final int Figure;
    private final int FigureSelected;
    private boolean IsSelected = false;
    String Name;

    Player(int figure, int figureSelected, String name) {
        Figure = figure;
        Name = name;
        this.FigureSelected = figureSelected;
    }

    void SelectPlayer(){
        if(IsSelected) IsSelected = false;
        else IsSelected = true;
    }

    int getFigure() {
        if(IsSelected) return FigureSelected;
        else return Figure;
    }
}
