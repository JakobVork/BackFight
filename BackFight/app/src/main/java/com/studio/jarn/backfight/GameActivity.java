package com.studio.jarn.backfight;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity
{
    static public final int GridSizeWidthAndHeight = 5;
    static public final int SquaresViewedAtStartup = 3;
    List<Player> players = new ArrayList<>();
    private Tile[][] mGrid;

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

    public void setupMyGrid (int n)
    {
        /*mGrid = new Tile [n] [n];*/
        players.add(new Player(R.drawable.player32, "Pernille"));
        players.add(new Player(R.drawable.point, "Anders"));
        players.add(new Player(R.drawable.cart, "Pernille"));
        players.add(new Player(R.drawable.point, "Pernille"));
        players.add(new Player(R.drawable.player32, "Pernille"));

        Tile[] arrayWithPlayers = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, players)};
        Tile[] array1 = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, null)};
        Tile[] array2 = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, null)};
        Tile[] array3 = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, null)};
        Tile[] array4 = new Tile[]{new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.WoodenFloor, null), new Tile(Tile.Types.Wall, null), new Tile(Tile.Types.Wall, null)};


        mGrid = new Tile[][]{array1, array2, array3, array4, arrayWithPlayers};
    }
}
