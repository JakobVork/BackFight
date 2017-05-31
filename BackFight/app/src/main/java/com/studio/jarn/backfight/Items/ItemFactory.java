package com.studio.jarn.backfight.Items;


import android.content.Context;

public class ItemFactory {
    public Weapons Weapons;

    public ItemFactory(Context context) {
        Context mContext = context;
        Weapons = new Weapons(mContext);
    }
}

class Armor {
    // TODO: Add diffrent kind of armor.
}