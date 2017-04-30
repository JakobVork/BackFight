package com.studio.jarn.backfight;

import android.app.Activity;
import android.os.Bundle;

public class GameActivity extends Activity
{
    static public final int GridSizeWidthAndHeight = 15;
    static public final int SquaresViewedAtStartup = 3;

    private int [][] pGrid;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);

        setupMyGrid (GridSizeWidthAndHeight);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {

            gv.setGridSize(GridSizeWidthAndHeight);
            gv.setViewSizeAtStartup(SquaresViewedAtStartup);
           gv.updateGrid (pGrid);
        }
    }

    public void setupMyGrid (int n)
    {
        pGrid = new int [n] [n];
    }
}
