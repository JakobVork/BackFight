package com.studio.jarn.backfight;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;

import java.util.ArrayList;


interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList);
}
