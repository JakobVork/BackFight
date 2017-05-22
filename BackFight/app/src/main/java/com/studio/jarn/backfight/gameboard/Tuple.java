package com.studio.jarn.backfight.gameboard;

public class Tuple<X, Y> {
    public X mGameObject;
    public Y mCoordinates;

    public Tuple(X mGameObject, Y mCoordinates) {
        this.mGameObject = mGameObject;
        this.mCoordinates = mCoordinates;
    }

    public Tuple() {
    }
}