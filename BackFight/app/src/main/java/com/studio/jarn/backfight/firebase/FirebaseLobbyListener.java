package com.studio.jarn.backfight.firebase;


import com.studio.jarn.backfight.Player;

import java.util.ArrayList;

public interface FirebaseLobbyListener {
    void startGameClient();

    void setNumberPickerValue(int value);

    void setRadioGroupButton(int value);

    void setPlayerList(ArrayList<Player> playerList);
}
