package com.studio.jarn.backfight.Items;


import android.content.Context;

public class ItemFactory {
    public final Weapons Weapons;

    public ItemFactory(Context context) {
        Weapons = new Weapons(context);
    }
}

class Armor {
    // TODO: Add diffrent kind of armor.
}