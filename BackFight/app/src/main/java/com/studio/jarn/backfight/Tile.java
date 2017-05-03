package com.studio.jarn.backfight;

import java.util.List;


class Tile {
    Types Type;
    List<Player> Players;
    int Visit;


    Tile(Types type, List<Player> players, int visit) {
        Type = type;
        Players = players;
        Visit = visit;
    }

    enum Types {
        WoodenFloor, Wall
    }
}

