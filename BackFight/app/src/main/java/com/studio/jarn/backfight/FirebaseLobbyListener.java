package com.studio.jarn.backfight;


import java.util.ArrayList;

interface FirebaseLobbyListener {
    void startGameClient();

    void setNumberPickerValue(int value);

    void setRadioGroupButton(int value);

    void setPlayerList(ArrayList<Player> playerList);
}
