package com.studio.jarn.backfight;

import java.util.Random;

class Coordinates {
    private final static Random rnd = new Random();

    int tileX;
    int tileY;
    int placementOnTileX;
    int placementOnTileY;

    Coordinates() {
    }

    Coordinates(int tileX, int tileY, int placementOnTileX, int placementOnTileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.placementOnTileX = placementOnTileX;
        this.placementOnTileY = placementOnTileY;
    }

    public static Coordinates getRandom(int GridSize) {
        int x = rnd.nextInt(GridSize);
        int y = rnd.nextInt(GridSize);

        return new Coordinates(x, y, 1, 1);
    }
}
