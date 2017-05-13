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


public class GameActivity extends FragmentActivity implements ItemsAndStatsFragment.OnItemSelectedListener
{
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
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                hideItemListFragment();
            }
        });

        mIvItemFragmentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView gv = (GameView) findViewById(R.id.boardview);
                showItemListFragment(gv.getPlayerItemList(), gv.getPlayerName());
            }
        });
    }

    private void setupGameView(Intent i) {
        String Uuid = i.getStringExtra(getString(R.string.EXTRA_UUID));
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
                gv.setupFirebase(Uuid);

                //addPlayers();
                gv.initHostGrid(mGrid);
                gv.setPlayerListener();
                gv.initAddPlayers(playerList);

                // Spawn items
                gv.spawnItems(10);
            } else {
                gv.setGridSize(sGridSize);
                gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
                gv.setupFirebase(Uuid);
                gv.initClientGrid();

                gv.setPlayerListener();
            }

            gv.setItemListener();
        }
    }

    public void showItemListFragment(List<GameItem> itemList, String name) {
        mIvItemFragmentShow.setVisibility(View.GONE);
        mIvItemFragmentHide.setVisibility(View.VISIBLE);

        // Add ItemsAndStats fragment
        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);

        // Need to create a new every time, since the current fragment might be for another user
        // Need to ensure that this doesn't create a memory leak in some sort? <-- I don't think so
        // Might need to keep it in detailed fragment, if player has selected a item? <-- A lot
        // harder, since it also depends on who it is etc.
        itemsAndStatsFragment = ItemsAndStatsFragment.newInstance(itemList, name);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
        ft.add(R.id.game_board_activity_items_and_stats_fragment, itemsAndStatsFragment);
        ft.commit();
    }

    public void hideItemListFragment() {
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
