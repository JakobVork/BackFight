package com.studio.jarn.backfight;

class Coordinates {
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
}
