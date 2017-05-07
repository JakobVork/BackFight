package com.studio.jarn.backfight;

public class Coordinates {
    public float x;
    public float y;
    public float xEnd;
    public float yEnd;

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Coordinates(float x, float y, float xEnd, float yEnd){
        this.x = x;
        this.y = y;
        this.xEnd = xEnd;
        this.yEnd = yEnd;
    }
}
