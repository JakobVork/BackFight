package com.studio.jarn.backfight.Items;

import com.studio.jarn.backfight.Gameboard.Coordinates;

public class GameItem {

    private static long sNextId = 0;
    public String Title;
    public String Description;
    public int Image;
    public long Id;

    public Coordinates Coordinate;

    public GameItem() {

    }

    public GameItem(String title, String description, int img) {
        this.Id = sNextId;
        sNextId++; // TODO : Find a better why. I've tried to use UUID, but when there's a problem with the adapter.
        this.Title = title;
        this.Description = description;
        this.Image = img;

        Coordinate = null; // Coordinate will be assigned if needed - Default is null.
    }
}
