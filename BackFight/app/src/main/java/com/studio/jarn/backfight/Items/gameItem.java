package com.studio.jarn.backfight.Items;

public class gameItem {

    public String Title;
    public String Description;
    public int image;
    public int id;

    public gameItem(String title, String description, int img) {
        this.id = 1;
        this.Title = title;
        this.Description = description;
        this.image = img;
    }
}
