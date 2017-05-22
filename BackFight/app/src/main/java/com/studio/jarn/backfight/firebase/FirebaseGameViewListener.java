package com.studio.jarn.backfight.firebase;

import com.studio.jarn.backfight.Coordinates;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Player;
import com.studio.jarn.backfight.Tile;
import com.studio.jarn.backfight.Tuple;
import com.studio.jarn.backfight.monster.Monster;

import java.util.ArrayList;


public interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monsterList);

    void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList);

}
