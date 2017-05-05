package com.studio.jarn.backfight.Items;

public class gameItem {

    public String Title;
    public String Description;
    public int Image;
    public int Id;

    public gameItem(String title, String description, int img) {
        this.Id = 1; //TODO: Generate random ID, or delegate this to childs, when they are created?
        this.Title = title;
        this.Description = description;
        this.Image = img;
    }
}
