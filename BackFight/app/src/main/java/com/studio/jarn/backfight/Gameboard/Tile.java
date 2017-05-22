package com.studio.jarn.backfight.Gameboard;

public class Tile {
    public int TileConnectivityCollectionNr;
    public boolean CanBePassed;
    Types Type;


    public Tile(Types type, int tileConnectivityCollectionNr) {
        Type = type;

        //Used for calculating connectivity for the CanBePassed tiles
        TileConnectivityCollectionNr = tileConnectivityCollectionNr;


        canBePassedSwitch(type);
    }

    public Tile(Types type) {
        this(type, 0);
    }

    public Tile() {

    }

    private void canBePassedSwitch(Types type) {
        switch (type) {
            case WoodenFloor: {
                CanBePassed = true;
                break;
            }
            case Wall: {
                CanBePassed = false;
                break;
            }
        }
    }


    public enum Types {
        WoodenFloor, Wall
    }
}

