package com.studio.jarn.backfight;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity
{
    static public final int GridSizeWidthAndHeight = 16;
    static public final int SquaresViewedAtStartup = 3;

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
        List<Player> players = new ArrayList<>();
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Anders"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.cart)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));


        Tile wallTile = new Tile(Tile.Types.Wall, null);
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null);
        Tile floorTileWithPlayers = new Tile(Tile.Types.WoodenFloor, players);

        Random random = new Random();
        mGrid = new Tile[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                if (random.nextInt(9) > 3)
                    mGrid[i][j] = floorTile;
                else
                    mGrid[i][j] = wallTile;
            }
        }
        mGrid[n / 2][n / 2] = floorTileWithPlayers;
    }
}
