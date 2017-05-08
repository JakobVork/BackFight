package com.studio.jarn.backfight.Items;


import android.content.Context;

import com.studio.jarn.backfight.R;

public class ItemFactory {
    public Weapons Weapons;
    private Context mContext; // Used for getting resources.

    public ItemFactory(Context context) {
        mContext = context;
        Weapons = new Weapons(mContext);
    }
}

class Armor {
    // TODO: Add diffrent kind of armor.
}