package com.studio.jarn.backfight;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.studio.jarn.backfight.Items.gameItem;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends FragmentActivity implements ItemsAndStatsFragment.OnItemSelectedListener
{
    static public final int GridSizeWidthAndHeight = 5;
    static public final int SquaresViewedAtStartup = 3;

    private static boolean isHidden = true;

    private Tile[][] mGrid;

    Fragment itemsAndStatsFragment;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);

        setupMyGrid (GridSizeWidthAndHeight);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {

            gv.setGridSize(GridSizeWidthAndHeight);
            gv.setViewSizeAtStartup(SquaresViewedAtStartup);
            gv.updateGrid(mGrid);
        }
    }

    public void switchItemListFragment(View view) {
        if(isHidden) {
            showItemListFragment();
            isHidden = false;
        } else {
            hideItemListFragment();
            isHidden = true;
        }
    }

    private void showItemListFragment() {
        // Add ItemsAndStats fragment
        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        if(itemsAndStatsFragment == null) {
            itemsAndStatsFragment = new ItemsAndStatsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0 ,0);
            ft.add(R.id.game_board_activity_items_and_stats_fragment, itemsAndStatsFragment);
            ft.commit();
        }
    }

    private void hideItemListFragment() {
        itemsAndStatsFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        if (itemsAndStatsFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0 ,0);
            ft.remove(itemsAndStatsFragment);
            ft.commit();
        }
    }

    public void setupMyGrid (int n)
    {
        /*mGrid = new Tile [n] [n];*/
        List<Player> players = new ArrayList<>();
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Anders"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.cart)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));

        Tile[] arrayWithPlayers = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, players)};
        Tile[] array = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, null)};

        mGrid = new Tile[][]{array, array, array, array, arrayWithPlayers};
    }

    @Override
    public void onItemSelected(gameItem item) {
        Log.d("Item", "onItemSelected: Clicked!");
    }
}
