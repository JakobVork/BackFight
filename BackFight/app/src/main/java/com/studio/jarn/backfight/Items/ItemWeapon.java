package com.studio.jarn.backfight.Items;

import java.util.Random;

public class ItemWeapon extends GameItem {

    private int DmgMin;
    private int DmgMax;

    // Firebase require a default constructor
    public ItemWeapon() {
    }

    public ItemWeapon(int DmgMin_MinRoll,
                      int DmgMin_MaxRoll,
                      int DmgMax_MinRoll,
                      int DmgMax_MaxRoll,
                      String title,
                      String description,
                      int image
    ) {
        super(title, description, image);

        Random rnd = new Random();

        //nextInt is inclusive 0 and exclusive Value. Therefore + 1 is added as parameter for
        // securing MaxValue, and +MinRoll roll for securing min value
        DmgMin = rnd.nextInt(DmgMin_MaxRoll - DmgMin_MinRoll + 1) + DmgMin_MinRoll;
        DmgMax = rnd.nextInt(DmgMax_MaxRoll - DmgMax_MinRoll + 1) + DmgMax_MinRoll;
    }

    public int getDmgMin() {
        return this.DmgMin;
    }

    public void setDmgMin(int dmgMin) {
        this.DmgMin = dmgMin;
    }

    public int getDmgMax() {
        return this.DmgMax;
    }

    public void setDmgMax(int dmgMax) {
        this.DmgMax = dmgMax;
    }
}
