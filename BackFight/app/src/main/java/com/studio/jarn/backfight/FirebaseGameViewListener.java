package com.studio.jarn.backfight;

import java.util.ArrayList;


interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);
}
