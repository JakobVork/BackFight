package com.studio.jarn.backfight;

import android.graphics.BitmapFactory;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.studio.jarn.backfight.Items.gameItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GameActivity extends FragmentActivity implements ItemsAndStatsFragment.OnItemSelectedListener
{
    static public final int GridSizeWidthAndHeight = 5;
    static public final int SquaresViewedAtStartup = 3;

    private static boolean isHidden = true;

    private static final int sGridSize = 16;
    private static final int sSquaresViewedAtStartup = 3;
    private final Tile wallTile = new Tile(Tile.Types.Wall, null);
    private final Tile floorTile = new Tile(Tile.Types.WoodenFloor, null);
    private final List<Integer> checkedList = new ArrayList<>();
    private Tile[][] mGrid;

    Fragment itemsAndStatsFragment;
    private int TileConnectivityCollectionNrCounter = 0;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);


        GridType gridType = GridType.DefaultGrid;
        /*GridType gridType = GridType.Maze;*/
        setupMyGrid(gridType);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {

            gv.setGridSize(sGridSize);
            gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
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

    //ToDO Needs implementation
    public void addPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player(R.drawable.point, "Anders"));
        players.add(new Player(R.drawable.player32, "Pernille"));
        players.add(new Player(R.drawable.cart, "Pernille"));
        players.add(new Player(R.drawable.point, "Pernille"));
        players.add(new Player(R.drawable.player32, "Pernille"));


        Tile floorTileWithPlayers = new Tile(Tile.Types.WoodenFloor, players);
    }

    private void setupMyGrid(GridType gridType)
    {
        mGrid = new Tile[sGridSize][sGridSize]; //initialize grid

        switch (gridType) {
            case DefaultGrid: {
                generateDefaultGrid();
                break;
            }
            case Maze: {
                generateMazeGrid();
                break;
            }
        }
    }

    private void generateDefaultGrid() {
        Random random = new Random();

        //generate random board with 50% walls and floor
        for (int row = 0; row < sGridSize; row++) {
            for (int column = 0; column < sGridSize; column++) {

                if (random.nextInt(99) + 1 > 50) //50% chance of placing a floorTile
                    mGrid[row][column] = floorTile;
                else
                    mGrid[row][column] = wallTile;
            }
        }
        connectAllFloorSpacesInMap();
    }

    private void connectAllFloorSpacesInMap() {
        //Do this until all floor spaces is connected together
        while (checkConnectivity() > 1) {

            for (int row = 0; row < sGridSize; row++) {
                for (int column = 0; column < sGridSize; column++) {

                    //check if tile is floor and if it has already been visited
                    if (mGrid[row][column].CanBePassed && checkIfVisited(mGrid[row][column].TileConnectivityCollectionNr)) {
                        changeWallToFloor(row, column);
                    }
                }
            }
            //reset TileConnectivityCollectionNrCounter and clear the checkedList so its ready for the next checkConnectivity() run
            TileConnectivityCollectionNrCounter = 0;
            checkedList.clear();
        }
    }

    private void changeWallToFloor(int row, int column) {
        //check if it is first row and then change the first wall it meets going to the right to a floor
        if (row == 0) {
            for (int spacesToGoRight = 0; spacesToGoRight < sGridSize; spacesToGoRight++) {
                if (!mGrid[row + spacesToGoRight][column].CanBePassed) {
                    mGrid[row + spacesToGoRight][column] = floorTile;
                    break;
                }
            }
        } else {
            outerLoop:
            for (int spacesToGoLeft = 0; spacesToGoLeft <= row; spacesToGoLeft++) {
                if (!mGrid[row - spacesToGoLeft][column].CanBePassed) { //Try to change wall tile to floor, to the left side
                    mGrid[row - spacesToGoLeft][column] = floorTile;
                    break;
                }
                if (spacesToGoLeft == row) { //if there is no wall tiles to the left all the way to the edge, try to change to the right
                    for (int spacesToGoRight = 0; spacesToGoRight <= sGridSize - 1 - row; spacesToGoRight++) {
                        if (!mGrid[row + spacesToGoRight][column].CanBePassed) {
                            mGrid[row + spacesToGoRight][column] = floorTile;
                            break outerLoop;
                        }
                    }
                }
            }
        }
    }

    // Check if the tile group has already been visited (part of the checkedList)
    private boolean checkIfVisited(int tileConnectivityCollectionNr) {

        for (Integer checkedInts : checkedList) {
            if (checkedInts == tileConnectivityCollectionNr) {
                return false;
            }
        }
        checkedList.add(tileConnectivityCollectionNr);
        return true;
    }

    private int checkConnectivity() {
        //reset the visit count for all spaces in the Grid
        for (int row = 0; row < sGridSize; row++) {
            for (int column = 0; column < sGridSize; column++) {
                mGrid[row][column].TileConnectivityCollectionNr = 0;
            }
        }

        //for each floor tile go and visit all their neighbors, when no more neighbors count
        // TileConnectivityCollectionNrCounter one up and find the next floor tile that has not been visited.
        for (int row = 0; row < sGridSize; row++) {
            for (int column = 0; column < sGridSize; column++) {
                if (mGrid[row][column].CanBePassed && mGrid[row][column].TileConnectivityCollectionNr == 0) {
                    visitAllConnectedNeighbors(++TileConnectivityCollectionNrCounter, row, column);
                }
            }
        }
        return TileConnectivityCollectionNrCounter;
    }

    //recursive logic to take a floor tile and visit all the connecting neighbors
    private void visitAllConnectedNeighbors(int tileConnectivityCollectionNr, int row, int column) {
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, tileConnectivityCollectionNr);
        mGrid[row][column] = floorTile;
        if (row > 0 && mGrid[row - 1][column].CanBePassed && mGrid[row - 1][column].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, row - 1, column);
        if (row < sGridSize - 1 && mGrid[row + 1][column].CanBePassed && mGrid[row + 1][column].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, row + 1, column);
        if (column > 0 && mGrid[row][column - 1].CanBePassed && mGrid[row][column - 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, row, column - 1);
        if (column < sGridSize - 1 && mGrid[row][column + 1].CanBePassed && mGrid[row][column + 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, row, column + 1);
    }


    private void generateMazeGrid() {
        for (int row = 0; row < sGridSize; row++) {
            for (int column = 0; column < sGridSize; column++) {
                mGrid[row][column] = wallTile;
            }
        }
        Random rand = new Random();
        // Generate random row
        int row = rand.nextInt(sGridSize);
        while (row % 2 == 0) {
            row = rand.nextInt(sGridSize);
        }
        // Generate random column
        int column = rand.nextInt(sGridSize);
        while (column % 2 == 0) {
            column = rand.nextInt(sGridSize);
        }
        // Starting cell
        mGrid[row][column] = floorTile;

        //　Allocate the maze with recursive method
        recursionMaze(row, column);

    }

    //inspiration for the maze function have been taken from http://www.migapro.com/depth-first-search/
    //There is used depth first search to generate the maze
    private void recursionMaze(int row, int column) {
        // 4 random directions
        int[] randDirs = generateRandomDirections();
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null);
        // Examine each direction
        for (int randDir : randDirs) {

            switch (randDir) {
                case 1: // Up
                    //　Whether 2 cells up is out or not
                    if (row - 2 <= 0)
                        continue;
                    if (!mGrid[row - 2][column].CanBePassed) {
                        mGrid[row - 2][column] = floorTile;
                        mGrid[row - 1][column] = floorTile;
                        recursionMaze(row - 2, column);
                    }
                    break;
                case 2: // Right
                    // Whether 2 cells to the right is out or not
                    if (column + 2 >= sGridSize - 1)
                        continue;
                    if (!mGrid[row][column + 2].CanBePassed) {
                        mGrid[row][column + 2] = floorTile;
                        mGrid[row][column + 1] = floorTile;
                        recursionMaze(row, column + 2);
                    }
                    break;
                case 3: // Down
                    // Whether 2 cells down is out or not
                    if (row + 2 >= sGridSize - 1)
                        continue;
                    if (!mGrid[row + 2][column].CanBePassed) {
                        mGrid[row + 2][column] = floorTile;
                        mGrid[row + 1][column] = floorTile;
                        recursionMaze(row + 2, column);
                    }
                    break;
                case 4: // Left
                    // Whether 2 cells to the left is out or not
                    if (column - 2 <= 0)
                        continue;
                    if (!mGrid[row][column - 2].CanBePassed) {
                        mGrid[row][column - 2] = floorTile;
                        mGrid[row][column - 1] = floorTile;
                        recursionMaze(row, column - 2);
                    }
                    break;
            }
        }
    }

    //inspiration for the maze function have been taken from http://www.migapro.com/depth-first-search/
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

    @Override
    public void onItemSelected(gameItem item) {
        Log.d("Item", "onItemSelected: Clicked!");
    }
}
