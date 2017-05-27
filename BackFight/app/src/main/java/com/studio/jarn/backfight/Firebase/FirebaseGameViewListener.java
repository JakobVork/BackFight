package com.studio.jarn.backfight.Firebase;

import com.studio.jarn.backfight.Gameboard.Tile;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.Player.Player;

import java.util.List;


public interface FirebaseGameViewListener {
    void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid);

    void setPlayerList(List<Player> playerList);

    void setMonsterList(List<Monster> monsterList);

    void setItemList(List<GameItem> itemList);

}
