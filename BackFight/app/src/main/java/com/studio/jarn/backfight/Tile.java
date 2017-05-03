package com.studio.jarn.backfight;

import java.util.List;


class Tile {
    Types Type;
    List<Player> Players;
    int TileConnectivityCollectionNr;
    boolean CanBePassed;


    Tile(Types type, List<Player> players, int tileConnectivityCollectionNr) {
        Type = type;
        Players = players;

        //Used for calculating connectivity for the CanBePassed tiles
        TileConnectivityCollectionNr = tileConnectivityCollectionNr;


        canBePassedSwitch(type);
    }

    Tile(Types type, List<Player> players) {
        this(type, players, 0);
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

    enum Types {
        WoodenFloor, Wall
    }
}

