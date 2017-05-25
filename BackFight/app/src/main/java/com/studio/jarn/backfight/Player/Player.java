package com.studio.jarn.backfight.Player;


import android.content.Context;
import android.view.View;

import com.studio.jarn.backfight.Gameboard.Coordinates;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    public List<GameItem> PlayerItems;
    public String Name;
    public int Figure = 0;
    public int FigureSelected = 0;
    public int ActionsRemaining = 3;
    public String Id;
    public int Health = 20;
    public int LineOfSight = 1;
    public Coordinates Coordinate;

    private int mActionsPerTurn = 3;

    public Player(int Figure, int FigureSelected, String name, String uuid) {
        this(Figure, FigureSelected, name, uuid, null);
    }

    public Player(int Figure, int FigureSelected, String name, String uuid, Coordinates coord) {
        this.Figure = Figure;
        Name = name;
        this.FigureSelected = FigureSelected;
        Id = uuid;

        PlayerItems = new ArrayList<>();
        Coordinate = coord;
    }

    //Needed for casting from Firebase
    public Player() {
    }

    public boolean canTakeAction() {
        return ActionsRemaining > 0;
    }

    public void takeAction(Context context, View view) {
        PlayerGameActivityListener playerGameActivityListener;
        PlayerGameViewListener playerGameViewListener;
        ActionsRemaining--;

        playerGameActivityListener = (PlayerGameActivityListener) context;
        playerGameViewListener = (PlayerGameViewListener) view;
        playerGameActivityListener.setActionCounter(ActionsRemaining);
        if (ActionsRemaining <= 0)
            playerGameViewListener.actionTaken();
    }

    public void resetActions() {
        ActionsRemaining = mActionsPerTurn;
    }

    public int rollAttack(){
        int min = 1;
        int max = 1;
        for (GameItem item:PlayerItems) {
            // Check if item is a weapon
            if(item instanceof ItemWeapon) {
                min += ((ItemWeapon)item).getDmgMin();
                max += ((ItemWeapon)item).getDmgMax();
            }
        }

        Random rnd = new Random();
        return rnd.nextInt(max - min + 1) + min; // return a number between min and max (both inc.)
    }
}
