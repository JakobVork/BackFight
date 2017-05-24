package com.studio.jarn.backfight.Gameboard;

import java.util.List;

class SimpleCoordinates {
    private int tileX;
    private int tileY;

    SimpleCoordinates(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    boolean existInList(List<SimpleCoordinates> list) {
        for (SimpleCoordinates simpleCoordinates : list) {
            if (simpleCoordinates.tileX == this.tileX && simpleCoordinates.tileY == this.tileY) {
                return true;
            }
        }
        return false;
    }
}
