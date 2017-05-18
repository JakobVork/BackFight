package com.studio.jarn.backfight;

class Tuple<X, Y> {
    X mGameObject;
    Y mCoordinates;

    Tuple(X mGameObject, Y mCoordinates) {
        this.mGameObject = mGameObject;
        this.mCoordinates = mCoordinates;
    }

    Tuple() {
    }
}
