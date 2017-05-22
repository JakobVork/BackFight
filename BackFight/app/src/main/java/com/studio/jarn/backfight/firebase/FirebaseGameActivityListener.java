package com.studio.jarn.backfight.firebase;


public interface FirebaseGameActivityListener {
    void setActionCounter(int actionCounter);

    void setRound(int round);

    void sendNotificationNewRound();
}
