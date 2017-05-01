package com.studio.jarn.backfight;

import java.util.List;


class Tile {
    Types Type;
    List<Player> Players;


    Tile(Types type, List<Player> players) {
        Type = type;
        Players = players;
    }

    enum Types {
        WoodenFloor, Wall
    }
}

