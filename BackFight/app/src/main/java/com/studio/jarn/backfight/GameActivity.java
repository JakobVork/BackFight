package com.studio.jarn.backfight;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameActivity extends Activity
{
    static public final int GridSizeWidthAndHeight = 16;
    static public final int SquaresViewedAtStartup = 3;
    Tile wallTile = new Tile(Tile.Types.Wall, null, 0);
    Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
    private Tile[][] mGrid;
    private int visitCounter = 1;
    private List<Integer> checkedList = new ArrayList<>();
    private int counter = 0;

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
        if (mGrid == null) {
            List<Player> players = new ArrayList<>();
            players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Anders"));
            players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));
            players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.cart)), "Pernille"));
            players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Pernille"));
            players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));


            Tile floorTileWithPlayers = new Tile(Tile.Types.WoodenFloor, players, 0);

            Random random = new Random();
            mGrid = new Tile[n][n];

            //generate board
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (random.nextInt(9) > 4) //70% chance of placing a floorTile
                        mGrid[i][j] = floorTile;
                    else
                        mGrid[i][j] = wallTile;
                }
            }
            //find connectivity
            while (findConnectivity() > 1) {

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {

                        if (mGrid[i][j].Type == Tile.Types.WoodenFloor && checkIfVisited(mGrid[i][j].Visit)) {
                            if (i == 0) {
                                counter = 0;
                                for (int counter = 0; counter < GridSizeWidthAndHeight; counter++) {
                                    if (mGrid[i + counter][j].Type == Tile.Types.Wall) {
                                        mGrid[i + counter][j] = floorTile;
                                        break;
                                    }
                                }
                            } else {
                                counter = 0;
                                for (int counter = 0; counter < i; counter++) {
                                    if (mGrid[i - counter][j].Type == Tile.Types.Wall) {
                                        mGrid[i - counter][j] = floorTile;
                                        break;
                                    }
                                }

                            }
                        }
                    }
                }
                mGrid[1][1].Visit = 0;
                visitCounter = 1;
                checkedList.clear();
            }

            Log.d("", "");


            //MAZE
/*            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    mGrid[i][j] = wallTile;
                }
            }
            Random rand = new Random();
            // r for row、c for column
            // Generate random r
            int r = rand.nextInt(GridSizeWidthAndHeight);
            while (r % 2 == 0) {
                r = rand.nextInt(GridSizeWidthAndHeight);
            }
            // Generate random c
            int c = rand.nextInt(GridSizeWidthAndHeight);
            while (c % 2 == 0) {
                c = rand.nextInt(GridSizeWidthAndHeight);
            }
            // Starting cell
            mGrid[r][c] = floorTile;

            //　Allocate the maze with recursive method
            recursionMaze(r, c);*/


            //place starting players
/*            mGrid[n / 2][n / 2] = floorTileWithPlayers;*/

            //generate board









            /*for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {

                    if (mGrid[i][j] == floorTile) //see if tile is a floor
                    {

                        //HACVHASHJFJHASFJHASHJF
                        //AJKSJKSADJKASJKDJKASD
                        // Corner cases
                        if (i == 0 && j == 0) {
                            if (mGrid[i][j + 1] != floorTile && mGrid[i + 1][j] != floorTile) // check only for 2 spaces
                                mGrid[i + 1][j] = floorTile;
                        } else if (i == 0 && j == n - 1) {
                            if (mGrid[i][j - 1] != floorTile && mGrid[i + 1][j] != floorTile) // check only for 2 spaces
                                mGrid[i + 1][j] = floorTile;
                        } else if (i == n - 1 && j == n - 1) {
                            if (mGrid[i][j - 1] != floorTile && mGrid[i - 1][j] != floorTile) // check only for 2 spaces
                                mGrid[i][j - 1] = floorTile;
                        } else if (i == n - 1 && j == 0) {
                            if (mGrid[i][j + 1] != floorTile && mGrid[i - 1][j] != floorTile) // check only for 2 spaces
                                mGrid[i][j + 1] = floorTile;
                        }

                        // edge cases
                        else if (i == 0) {
                            if (mGrid[i][j + 1] != floorTile && mGrid[i][j - 1] != floorTile && mGrid[i + 1][j] != floorTile) // check all neighbours
                                mGrid[i + 1][j] = floorTile;
                        } else if (i == n - 1) {
                            if (mGrid[i][j + 1] != floorTile && mGrid[i][j - 1] != floorTile && mGrid[i - 1][j] != floorTile) // check all neighbours
                                mGrid[i][j + 1] = floorTile;
                        } else if (j == 0) {
                            if (mGrid[i][j + 1] != floorTile && mGrid[i + 1][j] != floorTile && mGrid[i - 1][j] != floorTile) // check all neighbours
                                mGrid[i][j + 1] = floorTile;
                        } else if (j == n - 1) {
                            if (mGrid[i][j - 1] != floorTile && mGrid[i + 1][j] != floorTile && mGrid[i - 1][j] != floorTile) // check all neighbours
                                mGrid[i + 1][j] = floorTile;
                        }

                        //middle case
                        else {
                            if (mGrid[i][j + 1] != floorTile *//*&& mGrid[i][j - 1] != floorTile*//* && mGrid[i + 1][j] != floorTile && mGrid[i - 1][j] != floorTile) { // check all neighbours
                                mGrid[i + 1][j] = floorTile;
                                mGrid[i][j + 1] = floorTile;
                                mGrid[i - 1][j] = floorTile;
                                mGrid[i][j - 1] = floorTile;
                            }
                        }
                    }
                }
            }*/


        }
    }

    public boolean checkIfVisited(int visit) {

        for (Integer checkedInts : checkedList) {
            if (checkedInts == visit) {
                return false;
            }
        }
        checkedList.add(visit);
        return true;
    }

    public int findConnectivity() {
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
                if (mGrid[i][j] == floorTile && mGrid[i][j].Visit == 0) {
                    visitN(visitCounter++, i, j);
                }

            }
        }
        return visitCounter;
    }

    public void visitN(int visitCounter, int i, int j) {
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, visitCounter);
        mGrid[i][j] = floorTile;
        if (i > 0 && mGrid[i - 1][j].Type == Tile.Types.WoodenFloor && mGrid[i - 1][j].Visit != visitCounter)
            visitN(visitCounter, i - 1, j);
        if (i < GridSizeWidthAndHeight - 1 && mGrid[i + 1][j].Type == Tile.Types.WoodenFloor && mGrid[i + 1][j].Visit != visitCounter)
            visitN(visitCounter, i + 1, j);
        if (j > 0 && mGrid[i][j - 1].Type == Tile.Types.WoodenFloor && mGrid[i][j - 1].Visit != visitCounter)
            visitN(visitCounter, i, j - 1);
        if (j < GridSizeWidthAndHeight - 1 && mGrid[i][j + 1].Type == Tile.Types.WoodenFloor && mGrid[i][j + 1].Visit != visitCounter)
            visitN(visitCounter, i, j + 1);
    }

    private void recurse(int i, int j) {
        Tile wallTile = new Tile(Tile.Types.Wall, null, 0);
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
        Random random = new Random();

        if (counter++ == GridSizeWidthAndHeight * GridSizeWidthAndHeight / 2) {
            counter = 0;
            recurse2(GridSizeWidthAndHeight - 2, 1);
            return;
        }


        if (j == 0)
            /*j = GridSizeWidthAndHeight/2;*/
            j += 1;
        if (j == GridSizeWidthAndHeight - 1)
            j -= 1;
        if (i == 0 || i == GridSizeWidthAndHeight - 1)
            i = GridSizeWidthAndHeight / 2;

        switch (random.nextInt(3)) {
            case 0: {
                mGrid[i][j + 1] = floorTile;
                recurse(i, j + 1);
                break;
            }
            case 1: {
                mGrid[i + 1][j] = floorTile;
                recurse(i + 1, j);
                break;
            }
            case 2: {
                mGrid[i][j - 1] = floorTile;
                recurse(i, j - 1);
                break;
            }
            case 3: {
                mGrid[i - 1][j] = floorTile;
                recurse(i, j + 1);
                break;
            }

        }
    }

    private void recurse2(int i, int j) {
        Tile wallTile = new Tile(Tile.Types.Wall, null, 0);
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
        Random random = new Random();

        if (counter++ == GridSizeWidthAndHeight * GridSizeWidthAndHeight / 3)
            return;

        if (j == 0)
            /*j = GridSizeWidthAndHeight/2;*/
            j += 1;
        if (j == GridSizeWidthAndHeight - 1)
            j -= 1;
        if (i == 0 || i == GridSizeWidthAndHeight - 1)
            i = GridSizeWidthAndHeight / 2;

        switch (random.nextInt(3)) {
            case 0: {
                mGrid[i][j + 1] = floorTile;
                recurse2(i, j + 1);
                break;
            }
            case 1: {
                mGrid[i + 1][j] = floorTile;
                recurse2(i + 1, j);
                break;
            }
            case 2: {
                mGrid[i][j - 1] = floorTile;
                recurse2(i, j - 1);
                break;
            }
            case 3: {
                mGrid[i - 1][j] = floorTile;
                recurse2(i, j + 1);
                break;
            }

        }
    }


    //http://www.migapro.com/depth-first-search/
    public void recursionMaze(int r, int c) {
        // 4 random directions
        int[] randDirs = generateRandomDirections();
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
        // Examine each direction
        for (int i = 0; i < randDirs.length; i++) {

            switch (randDirs[i]) {
                case 1: // Up
                    //　Whether 2 cells up is out or not
                    if (r - 2 <= 0)
                        continue;
                    if (mGrid[r - 2][c].Type != Tile.Types.WoodenFloor) {
                        mGrid[r - 2][c] = floorTile;
                        mGrid[r - 1][c] = floorTile;
                        recursionMaze(r - 2, c);
                    }
                    break;
                case 2: // Right
                    // Whether 2 cells to the right is out or not
                    if (c + 2 >= GridSizeWidthAndHeight - 1)
                        continue;
                    if (mGrid[r][c + 2].Type != Tile.Types.WoodenFloor) {
                        mGrid[r][c + 2] = floorTile;
                        mGrid[r][c + 1] = floorTile;
                        recursionMaze(r, c + 2);
                    }
                    break;
                case 3: // Down
                    // Whether 2 cells down is out or not
                    if (r + 2 >= GridSizeWidthAndHeight - 1)
                        continue;
                    if (mGrid[r + 2][c].Type != Tile.Types.WoodenFloor) {
                        mGrid[r + 2][c] = floorTile;
                        mGrid[r + 1][c] = floorTile;
                        recursionMaze(r + 2, c);
                    }
                    break;
                case 4: // Left
                    // Whether 2 cells to the left is out or not
                    if (c - 2 <= 0)
                        continue;
                    if (mGrid[r][c - 2].Type != Tile.Types.WoodenFloor) {
                        mGrid[r][c - 2] = floorTile;
                        mGrid[r][c - 1] = floorTile;
                        recursionMaze(r, c - 2);
                    }
                    break;
            }
        }

    }

    /**
     * http://www.migapro.com/depth-first-search/
     * Generate an array with random directions 1-4
     *
     * @return Array containing 4 directions in random order
     */
    private int[] generateRandomDirections() {
        ArrayList<Integer> randoms = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            randoms.add(i + 1);
        Collections.shuffle(randoms);

        int[] intArray = new int[4];
        for (int i = 0; i < randoms.size(); i++)
            intArray[i] = randoms.get(i);

        return intArray;
    }
}
