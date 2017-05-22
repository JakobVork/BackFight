package com.studio.jarn.backfight;

import com.studio.jarn.backfight.monster.Monster;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;


import java.util.ArrayList;


interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monsterList);

    void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList);

}
