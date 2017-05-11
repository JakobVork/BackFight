package com.studio.jarn.backfight.Items;

import android.content.Context;

import com.studio.jarn.backfight.R;

public class Weapons {
    private Context mContext;

    Weapons(Context context) {
        mContext = context;
    }

    public GameItem SwordSimple() {
        return new ItemWeapon(1, 2, 2, 4, //(1-2)-(2-4)
                mContext.getResources().getString(R.string.item_sword_simple_title),
                mContext.getResources().getString(R.string.item_sword_simple_description),
                R.drawable.item_sword);
    }

    public GameItem SwordDual() {
        return new ItemWeapon(1, 3, 3, 6, //(1-3)-(3-6)
                mContext.getResources().getString(R.string.item_sword_dual_title),
                mContext.getResources().getString(R.string.item_sword_dual_description),
                R.drawable.item_swords);
    }

    public GameItem SwordScimitar() {
        return new ItemWeapon(2, 3, 4, 5, //(2-3)-(4-5)
                mContext.getResources().getString(R.string.item_sword_scimitar_title),
                mContext.getResources().getString(R.string.item_sword_scimitar_description),
                R.drawable.item_sword_scimitar);
    }

    public GameItem SwordMagical() {
        return new ItemWeapon(2, 4, 4, 6, //(2-4)-(4-6)
                mContext.getResources().getString(R.string.item_sword_magical_title),
                mContext.getResources().getString(R.string.item_sword_magical_description),
                R.drawable.item_sword_magical);
    }

    public GameItem Scepter() {
        return new ItemWeapon(1, 3, 3, 4, //(1-3)-(3-4)
                mContext.getResources().getString(R.string.item_scepter_title),
                mContext.getResources().getString(R.string.item_scepter_description),
                R.drawable.item_scepter);
    }

    public GameItem Axe() {
        return new ItemWeapon(3, 4, 4, 5, //(3-4)-(4-5)
                mContext.getResources().getString(R.string.item_axe_title),
                mContext.getResources().getString(R.string.item_axe_description),
                R.drawable.item_axe);
    }

    public GameItem AxeMajor() {
        return new ItemWeapon(3, 4, 5, 7, //(3-4)-(5-7)
                mContext.getResources().getString(R.string.item_axe_major_title),
                mContext.getResources().getString(R.string.item_axe_major_description),
                R.drawable.item_axe_major);
    }

    public GameItem SwordFlame() {
        return new ItemWeapon(4, 6, 7, 8, //(4-6)-(7-8)
                mContext.getResources().getString(R.string.item_sword_flame_title),
                mContext.getResources().getString(R.string.item_sword_flame_description),
                R.drawable.item_flames_edge);
    }
}
