package com.studio.jarn.backfight.Firebase;


public interface FirebaseGameActivityListener {
    void setActionCounter(int actionCounter);

    void setRound(int round);

    void sendNotificationNewRound();
}
