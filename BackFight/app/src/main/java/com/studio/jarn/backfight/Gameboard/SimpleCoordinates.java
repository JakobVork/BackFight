package com.studio.jarn.backfight.Gameboard;

import java.util.List;

public class SimpleCoordinates {
    public int tileX; //For use in Firebase
    public int tileY; //For use in Firebase

    SimpleCoordinates(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    SimpleCoordinates() {
    } //For use in Firebase

    boolean existInList(List<SimpleCoordinates> list) {
        if (list == null)
            return false;
        for (SimpleCoordinates simpleCoordinates : list) {
            if (simpleCoordinates.tileX == this.tileX && simpleCoordinates.tileY == this.tileY) {
                return true;
            }
        }
        return false;
    }
}
