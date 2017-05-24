package com.studio.jarn.backfight.Items;

public class GameItem {

    private static long sNextId = 0;
    public String Title;
    public String Description;
    public int Image;
    public long Id;

    public GameItem(){

    }

    public GameItem(String title, String description, int img) {
        this.Id = sNextId;
        sNextId++; // TODO : Find a better why. Id've tried to use UUID, but when there's a problem with the adapter.
        this.Title = title;
        this.Description = description;
        this.Image = img;
    }
}
