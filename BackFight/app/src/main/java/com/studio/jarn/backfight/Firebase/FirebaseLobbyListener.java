package com.studio.jarn.backfight.Firebase;


import com.studio.jarn.backfight.Player.Player;

import java.util.ArrayList;

public interface FirebaseLobbyListener {
    void startGameClient();

    void setNumberPickerValue(int value);

    void setRadioGroupButton(int value);

    void setPlayerList(ArrayList<Player> playerList);
}
