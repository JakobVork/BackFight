package com.studio.jarn.backfight.monster;


public class Monster {
    private String mName;
    private String mDecription;
    private int mFigure;
    private int mFigureSelected;
    private int mActionsRemaining;
    private int mActionsPerTurn;
    private int mAttackPower;
    private int mHitPoints;

    Monster(int Figure, int FigureSelected, String name, String decription, int monsterTurn, int hp, int ap) {
        mFigure = Figure;
        mName = name;
        mFigureSelected = FigureSelected;
        mActionsPerTurn = monsterTurn;
        mActionsRemaining = monsterTurn;
        mHitPoints = hp;
        mAttackPower = ap;
        mDecription = decription;
    }

    //Needed for casting from Firebase
    Monster() {
    }

    public String getName() {
        return mName;
    }

    public String getDecription() {
        return mDecription;
    }

    public int getFigure() {
        return mFigure;
    }

    public int getFigureSelected() {
        return mFigureSelected;
    }

    public int getAttackPower() {
        return mAttackPower;
    }

    public int getHitPoints() {
        return mHitPoints;
    }

    public boolean canTakeAction() {
        return mActionsRemaining > 0;
    }

    public void takeAction() {
        mActionsRemaining--;
    }

    public void resetActions() {
        mActionsRemaining = mActionsPerTurn;
    }
}
