package com.studio.jarn.backfight.Monster;


import com.studio.jarn.backfight.Gameboard.Coordinates;

public class Monster {
    public int Figure;
    public String Name;
    public String Decription;
    public int ActionsRemaining;
    public int ActionsPerTurn;
    public int AttackPower;
    public int HitPoints;
    public Coordinates coordinate;

    Monster(int Figure, String name, String decription, int monsterTurn, int hp, int ap) {
        this(Figure, name, decription, monsterTurn, hp, ap, null);
    }

    Monster(int Figure, String name, String decription, int monsterTurn, int hp, int ap, Coordinates coord) {
        this.Figure = Figure;
        Name = name;
        ActionsPerTurn = monsterTurn;
        ActionsRemaining = monsterTurn;
        HitPoints = hp;
        AttackPower = ap;
        Decription = decription;
        coordinate = coord;
    }

    //Needed for casting from Firebase
    Monster() {
    }

    public boolean canTakeAction() {
        return ActionsRemaining > 0;
    }

    public void takeAction() {
        ActionsRemaining--;
    }

    public void resetActions() {
        ActionsRemaining = ActionsPerTurn;
    }
}
