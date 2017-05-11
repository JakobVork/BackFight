package com.studio.jarn.backfight;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemFactory;
import com.studio.jarn.backfight.Items.ItemWeapon;
import com.studio.jarn.backfight.MapGeneration.DefaultMap;
import com.studio.jarn.backfight.MapGeneration.IMapGenerator;
import com.studio.jarn.backfight.MapGeneration.MazeMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class GameActivity extends FragmentActivity implements ItemsAndStatsFragment.OnItemSelectedListener
{
    static final String DATABASE_POSTFIX_GRID = "Grid";
    static final String DATABASE_POSTFIX_PLAYERS = "PlayerList";
    private static final int sSquaresViewedAtStartup = 3;
    private static final int sDefaultGridSize = 15;
    private static int sGridSize = 16;
    Fragment itemsAndStatsFragment;
    Fragment itemsAndStatsFragmentDetailed;
    private Tile[][] mGrid;

    private ImageView mIvItemFragmentShow;
    private ImageView mIvItemFragmentHide;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);

        Intent i = getIntent();
        if (i != null) {
            setupGameView(i);
        }

        setupItemFragment();
    }

    private void setupItemFragment() {

        mIvItemFragmentHide = (ImageView) findViewById(R.id.game_board_activity_iv_hide_items);
        mIvItemFragmentShow = (ImageView) findViewById(R.id.game_board_activity_iv_show_items);
        hideItemListFragment();

        mIvItemFragmentHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                hideItemListFragment();
            }
        });

        mIvItemFragmentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemListFragment();
            }
        });
    }

    private void setupGameView(Intent i) {
        String UUID = i.getStringExtra(getString(R.string.EXTRA_UUID));
        boolean host = i.getBooleanExtra(getString(R.string.EXTRA_HOST), true);
        sGridSize = i.getIntExtra(getString(R.string.EXTRA_GRIDSIZE), sDefaultGridSize);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {
            if (host) {

                //Casting to List example: http://stackoverflow.com/questions/5813434/trouble-with-gson-serializing-an-arraylist-of-pojos
                Type playerListType = new TypeToken<List<Player>>() {
                }.getType();

                String playerListInJson = i.getStringExtra(getString(R.string.EXTRA_PLAYERLIST));
                List<Player> playerList = new Gson().fromJson(playerListInJson, playerListType);

                //http://stackoverflow.com/questions/2836256/passing-enum-or-object-through-an-intent-the-best-solution
                GridType gridType = (GridType) i.getSerializableExtra(getString(R.string.EXTRA_GRIDTYPE));
                setupMyGrid(gridType);

                gv.setGridSize(sGridSize);
                gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
                gv.setUuidStartup(UUID + DATABASE_POSTFIX_GRID);

                //addPlayers();
                gv.initHostGrid(mGrid);
                gv.setPlayerListener(UUID + DATABASE_POSTFIX_PLAYERS);
                gv.initAddPlayers(playerList);
            } else {
                gv.setGridSize(sGridSize);
                gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
                gv.setUuidStartup(UUID + DATABASE_POSTFIX_GRID);
                gv.initClientGrid();

                gv.setPlayerListener(UUID + DATABASE_POSTFIX_PLAYERS);
            }
        }
    }

    private void showItemListFragment() {
        mIvItemFragmentShow.setVisibility(View.GONE);
        mIvItemFragmentHide.setVisibility(View.VISIBLE);

        ArrayList<GameItem> items = new ArrayList<GameItem>();
        ItemFactory fac = new ItemFactory(getApplicationContext());
        items.add(fac.Weapons.AxeMajor());
        ItemsAndStatsFragment.newInstance(items);

        // Add ItemsAndStats fragment
        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        if (itemsAndStatsFragment == null) {
            // TODO: Use correct items instead of hardcoded.
            ItemFactory itemFac = new ItemFactory(getApplicationContext());
            ArrayList<GameItem> itemList = new ArrayList<>();

            GameItem item = itemFac.Weapons.SwordSimple();
            itemList.add(item);
            item = itemFac.Weapons.SwordFlame();
            itemList.add(item);
            item = itemFac.Weapons.AxeMajor();
            itemList.add(item);
            item = itemFac.Weapons.Scepter();
            itemList.add(item);

            itemsAndStatsFragment = ItemsAndStatsFragment.newInstance(itemList);
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
        ft.add(R.id.game_board_activity_items_and_stats_fragment, itemsAndStatsFragment);
        ft.commit();
    }

    private void hideItemListFragment() {
        mIvItemFragmentShow.setVisibility(View.VISIBLE);
        mIvItemFragmentHide.setVisibility(View.GONE);

        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        if (itemsAndStatsFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
            ft.remove(itemsAndStatsFragment);
            ft.commit();
        }
    }

/*    //ToDO Needs implementation
    public void addPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(R.drawable.player32, "Pernille"));

        Random random = new Random();

        while (true) {
            int random1 = random.nextInt(sGridSize - 1);
            int random2 = random.nextInt(sGridSize - 1);

            if (mGrid[random1][random2].CanBePassed) {
                mGrid[random1][random2].Players = players;
                break;
            }
        }
    }*/

    private void setupMyGrid(GridType gridType)
    {
        IMapGenerator mapGenerator;

        switch (gridType) {
            case DefaultGrid: {
                mapGenerator = new DefaultMap();
                mGrid = mapGenerator.generateMap(sGridSize);
                break;
            }
            case Maze: {
                mapGenerator = new MazeMap();
                mGrid = mapGenerator.generateMap(sGridSize);
                break;
            }
        }
    }

    @Override
    public void onItemSelected(GameItem item) {
        Log.d("Item", "onItemSelected: Clicked!");
        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        itemsAndStatsFragmentDetailed = fragment_item_details.newInstance((ItemWeapon) item);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.game_board_activity_items_and_stats_fragment, itemsAndStatsFragmentDetailed);
        ft.addToBackStack(null);
        ft.commit();
    }
}
