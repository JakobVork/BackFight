package com.studio.jarn.backfight.Items;

import java.util.Random;

public class ItemWeapon extends GameItem {

    public int DmgMin;
    public int DmgMax;

    public ItemWeapon() {};

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
}
