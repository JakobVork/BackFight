package com.studio.jarn.backfight;

public class Coordinates {
    public int tileX;
    public int tileY;
    public int placementOnTileX;
    public int placementOnTileY;


    public Coordinates(){}

    public Coordinates(int tileX, int tileY, int placementOnTileX, int placementOnTileY){
        this.tileX = tileX;
        this.tileY = tileY;
        this.placementOnTileX = placementOnTileX;
        this.placementOnTileY = placementOnTileY;
    }
}
