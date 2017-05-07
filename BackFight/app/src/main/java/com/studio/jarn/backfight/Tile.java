package com.studio.jarn.backfight;

class Tile {
    Types Type;
    int TileConnectivityCollectionNr;
    boolean CanBePassed;


    Tile(Types type,int tileConnectivityCollectionNr) {
        Type = type;

        //Used for calculating connectivity for the CanBePassed tiles
        TileConnectivityCollectionNr = tileConnectivityCollectionNr;


        canBePassedSwitch(type);
    }

    Tile(Types type) {
        this(type, 0);
    }

    Tile(){

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

