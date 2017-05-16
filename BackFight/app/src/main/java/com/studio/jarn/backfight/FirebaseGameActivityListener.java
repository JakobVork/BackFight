package com.studio.jarn.backfight;


interface FirebaseGameActivityListener {
    void setActionCounter(int actionCounter);

    void setRound(int round);

    void sendNotificationNewRound();
}
