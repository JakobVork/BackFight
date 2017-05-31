package com.studio.jarn.backfight.Monster;


import com.studio.jarn.backfight.Gameboard.Coordinates;

public class Monster {
    public int Figure; //For use in Firebase
    public String Name; //For use in Firebase
    public String Description; //For use in Firebase
    public int ActionsRemaining; //For use in Firebase
    public int ActionsPerTurn; //For use in Firebase
    public int AttackPower; //For use in Firebase
    public int HitPoints; //For use in Firebase
    public Coordinates coordinate; //For use in Firebase

    Monster(int Figure, String name, String description, int monsterTurn, int hp, int ap) {
        this(Figure, name, description, monsterTurn, hp, ap, null);
    }

    Monster(int Figure, String name, String description, int monsterTurn, int hp, int ap, Coordinates coord) {
        this.Figure = Figure;
        Name = name;
        ActionsPerTurn = monsterTurn;
        ActionsRemaining = monsterTurn;
        HitPoints = hp;
        AttackPower = ap;
        Description = description;
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
