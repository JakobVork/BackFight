package com.studio.jarn.backfight;

import com.studio.jarn.backfight.monster.Monster;

import java.util.ArrayList;


interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monsterList);
}
