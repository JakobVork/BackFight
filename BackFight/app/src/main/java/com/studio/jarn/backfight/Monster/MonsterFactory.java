package com.studio.jarn.backfight.Monster;

import android.content.Context;

import com.studio.jarn.backfight.R;

import java.util.Random;

public class MonsterFactory {

    private final Context mContext;

    public MonsterFactory(Context context) {
        mContext = context;
    }

    public Monster getRandomNormalMonster(int numberOfPlayers, int numberOfRounds) {
        switch (new Random().nextInt(5)) {
            case 0:
                return Zombie(numberOfPlayers, numberOfRounds);
            case 1:
                return Undead(numberOfPlayers, numberOfRounds);
            case 2:
                return Mauler(numberOfPlayers, numberOfRounds);
            case 3:
                return Muler(numberOfPlayers, numberOfRounds);
            default:
                return Hiss(numberOfPlayers, numberOfRounds);
        }
    }

    public Monster getRandomEliteMonster(int numberOfPlayers, int numberOfRounds) {
        switch (new Random().nextInt(2)) {
            case 0:
                return Tauler(numberOfPlayers, numberOfRounds);
            default:
                return GhostHorse(numberOfPlayers, numberOfRounds);
        }
    }

    public Monster getRandomBossMonster(int numberOfPlayers, int numberOfRounds) {
        switch (new Random().nextInt(1)) {
            default:
                return ThreeHeaded(numberOfPlayers, numberOfRounds);
        }
    }

    private Monster Zombie(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 6 * numberOfPlayers + numberOfRounds;
        int ap = 4;
        int monsterTurn = 2;

        return new Monster(
                R.drawable.monster_zombieman,
                mContext.getResources().getString(R.string.monster_zombie_name),
                mContext.getResources().getString(R.string.monster_zombie_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster Undead(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 7 * numberOfPlayers + numberOfRounds;
        int ap = 3;
        int monsterTurn = 2;

        return new Monster(
                R.drawable.monster_undead,
                mContext.getResources().getString(R.string.monster_undead_name),
                mContext.getResources().getString(R.string.monster_undead_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster Muler(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 5 * numberOfPlayers + numberOfRounds;
        int ap = 3;
        int monsterTurn = 3;

        return new Monster(
                R.drawable.monster_muler,
                mContext.getResources().getString(R.string.monster_muler_name),
                mContext.getResources().getString(R.string.monster_muler_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster Tauler(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 8 * numberOfPlayers + numberOfRounds;
        int ap = 3;
        int monsterTurn = 4;

        return new Monster(
                R.drawable.monster_tauler,
                mContext.getResources().getString(R.string.monster_tauler_name),
                mContext.getResources().getString(R.string.monster_tauler_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster Mauler(int numberOfPlayers, int numberOfRounds) {
        // used for scaling6
        int hp = 5 * numberOfPlayers + numberOfRounds;
        int ap = 2;
        int monsterTurn = 3;

        return new Monster(
                R.drawable.monster_mauler,
                mContext.getResources().getString(R.string.monster_mauler_name),
                mContext.getResources().getString(R.string.monster_mauler_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster Hiss(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 4 * numberOfPlayers;
        int ap = 1;
        int monsterTurn = 6;

        return new Monster(
                R.drawable.monster_hiss,
                mContext.getResources().getString(R.string.monster_hiss_name),
                mContext.getResources().getString(R.string.monster_hiss_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster GhostHorse(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 10 * numberOfPlayers + numberOfRounds;
        int ap = 4;
        int monsterTurn = 3;

        return new Monster(
                R.drawable.monster_ghost_horse,
                mContext.getResources().getString(R.string.monster_ghostHorse_name),
                mContext.getResources().getString(R.string.monster_ghostHorse_description),
                monsterTurn,
                hp,
                ap);
    }

    private Monster ThreeHeaded(int numberOfPlayers, int numberOfRounds) {
        // used for scaling
        int hp = 25 * numberOfPlayers;
        int ap = 4;
        int monsterTurn = 3;

        return new Monster(
                R.drawable.monster_3headed,
                mContext.getResources().getString(R.string.monster_threeHeaded_name),
                mContext.getResources().getString(R.string.monster_threeHeaded_description),
                monsterTurn,
                hp,
                ap);
    }
}
