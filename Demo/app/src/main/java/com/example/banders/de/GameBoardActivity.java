package com.example.banders.de;

import android.app.Activity;
import android.os.Bundle;

public class GameBoardActivity extends Activity
{
    static public final int GridSizeWidthAndHeight = 15;
    static public final int SquaresViewedAtStartup = 3;

    private int [][] pGrid;


    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);

        setupMyGrid (GridSizeWidthAndHeight);

        GameBoardView gv = (GameBoardView) findViewById (R.id.boardview);
        if (gv != null) {

           gv.setGridSize(GridSizeWidthAndHeight); ;
           gv.setViewSizeAtStartup(SquaresViewedAtStartup);
           gv.updateGrid (pGrid);
        }
    }

    public void setupMyGrid (int n)
    {
        pGrid = new int [n] [n];
    }
}
