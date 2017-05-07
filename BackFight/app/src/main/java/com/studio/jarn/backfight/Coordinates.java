package com.studio.jarn.backfight;

public class Coordinates {
    public float x;
    public float y;
    private float mXEnd;
    private float mYEnd;

    public Coordinates(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(float x, float y, float tileSize){
        this.x = x;
        this.y = y;
        setEndCoordinates(tileSize);
    }

    public void setEndCoordinates(float tileSize){
        mXEnd = x + (tileSize/4);
        mYEnd = y + (tileSize/4);
    }

    public float getXEnd(){
        return mXEnd;
    }

    public float getYEnd(){
        return mYEnd;
    }
}
