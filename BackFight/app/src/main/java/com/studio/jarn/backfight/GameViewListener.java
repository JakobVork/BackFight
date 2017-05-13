package com.studio.jarn.backfight;

import com.studio.jarn.backfight.Items.GameItem;

import java.util.ArrayList;


interface GameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList);
}
