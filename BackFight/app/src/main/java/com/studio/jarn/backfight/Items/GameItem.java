package com.studio.jarn.backfight.Items;

import java.util.UUID;

public class GameItem {

    public String Title;
    public String Description;
    public int Image;
    public long Id;
    private static long nextId = 0;

    public GameItem(String title, String description, int img) {
        this.Id = nextId;
        nextId++; // TODO : Find a better why. I've tried to use UUID, but when there's a problem with the adapter.
        this.Title = title;
        this.Description = description;
        this.Image = img;
    }
}
