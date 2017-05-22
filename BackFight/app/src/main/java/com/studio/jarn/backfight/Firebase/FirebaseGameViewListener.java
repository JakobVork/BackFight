package com.studio.jarn.backfight.Firebase;

import com.studio.jarn.backfight.Gameboard.Coordinates;
import com.studio.jarn.backfight.Gameboard.Tile;
import com.studio.jarn.backfight.Gameboard.Tuple;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.Player.Player;

import java.util.ArrayList;


public interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList);

    void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monsterList);

    void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList);

}
