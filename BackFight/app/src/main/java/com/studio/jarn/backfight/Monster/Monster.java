package com.studio.jarn.backfight.Monster;


public class Monster {
    public int mFigure;
    public String mName;
    public String mDecription;
    public int mActionsRemaining;
    public int mActionsPerTurn;
    public int mAttackPower;
    public int mHitPoints;

    Monster(int Figure, String name, String decription, int monsterTurn, int hp, int ap) {
        mFigure = Figure;
        mName = name;
        mActionsPerTurn = monsterTurn;
        mActionsRemaining = monsterTurn;
        mHitPoints = hp;
        mAttackPower = ap;
        mDecription = decription;
    }

    //Needed for casting from Firebase
    Monster() {
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
