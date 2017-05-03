package com.studio.jarn.backfight;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class GameActivity extends Activity
{
    private static final int GridSizeWidthAndHeight = 16;
    private static final int SquaresViewedAtStartup = 3;
    private final Tile wallTile = new Tile(Tile.Types.Wall, null);
    private final Tile floorTile = new Tile(Tile.Types.WoodenFloor, null);
    private final List<Integer> checkedList = new ArrayList<>();
    private Tile[][] mGrid;
    private int TileConnectivityCollectionNrCounter = 0;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);


        GridType gridType = GridType.DefaultGrid;
        /*GridType gridType = GridType.Maze;*/
        setupMyGrid(gridType);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {

            gv.setGridSize(GridSizeWidthAndHeight);
            gv.setViewSizeAtStartup(SquaresViewedAtStartup);
            gv.updateGrid(mGrid);
        }
    }

    //ToDO Needs implementation
    public void addPlayers() {
        List<Player> players = new ArrayList<>();
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Anders"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.cart)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.point)), "Pernille"));
        players.add(new Player((BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.player32)), "Pernille"));


        Tile floorTileWithPlayers = new Tile(Tile.Types.WoodenFloor, players);
    }

    private void setupMyGrid(GridType gridType)
    {
        mGrid = new Tile[GridSizeWidthAndHeight][GridSizeWidthAndHeight]; //initialize grid

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
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {

                if (random.nextInt(9) > 4) //50% chance of placing a floorTile
                    mGrid[i][j] = floorTile;
                else
                    mGrid[i][j] = wallTile;
            }
        }
        connectAllFloorSpacesInMap();
    }

    private void connectAllFloorSpacesInMap() {
        //Do this until all floor spaces is connected together
        while (checkConnectivity() > 1) {

            for (int r = 0; r < GridSizeWidthAndHeight; r++) {
                for (int c = 0; c < GridSizeWidthAndHeight; c++) {

                    //check if tile is floor and if it has already been visited
                    if (mGrid[r][c].CanBePassed && checkIfVisited(mGrid[r][c].TileConnectivityCollectionNr)) {
                        changeWallToFloor(r, c);
                    }
                }
            }
            //reset TileConnectivityCollectionNrCounter and clear the checkedList so its ready for the next checkConnectivity() run
            TileConnectivityCollectionNrCounter = 0;
            checkedList.clear();
        }
    }

    private void changeWallToFloor(int r, int c) {
        //check if it is first row and then change the first wall it meets going to the right to a floor
        if (r == 0) {
            for (int spacesToGoRight = 0; spacesToGoRight < GridSizeWidthAndHeight; spacesToGoRight++) {
                if (!mGrid[r + spacesToGoRight][c].CanBePassed) {
                    mGrid[r + spacesToGoRight][c] = floorTile;
                    break;
                }
            }
        } else {
            outerLoop:
            for (int spacesToGoLeft = 0; spacesToGoLeft <= r; spacesToGoLeft++) {
                if (!mGrid[r - spacesToGoLeft][c].CanBePassed) { //Try to change wall tile to floor, to the left side
                    mGrid[r - spacesToGoLeft][c] = floorTile;
                    break;
                }
                if (spacesToGoLeft == r) { //if there is no wall tiles to the left all the way to the edge, try to change to the right
                    for (int spacesToGoRight = 0; spacesToGoRight <= GridSizeWidthAndHeight - 1 - r; spacesToGoRight++) {
                        if (!mGrid[r + spacesToGoRight][c].CanBePassed) {
                            mGrid[r + spacesToGoRight][c] = floorTile;
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
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
                mGrid[i][j].TileConnectivityCollectionNr = 0;
            }
        }

        //for each floor tile go and visit all their neighbors, when no more neighbors count
        // TileConnectivityCollectionNrCounter one up and find the next floor tile that has not been visited.
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
                if (mGrid[i][j].CanBePassed && mGrid[i][j].TileConnectivityCollectionNr == 0) {
                    visitAllConnectedNeighbors(++TileConnectivityCollectionNrCounter, i, j);
                }
            }
        }
        return TileConnectivityCollectionNrCounter;
    }

    //recursive logic to take a floor tile and visit all the connecting neighbors
    private void visitAllConnectedNeighbors(int tileConnectivityCollectionNr, int i, int j) {
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, tileConnectivityCollectionNr);
        mGrid[i][j] = floorTile;
        if (i > 0 && mGrid[i - 1][j].CanBePassed && mGrid[i - 1][j].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, i - 1, j);
        if (i < GridSizeWidthAndHeight - 1 && mGrid[i + 1][j].CanBePassed && mGrid[i + 1][j].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, i + 1, j);
        if (j > 0 && mGrid[i][j - 1].CanBePassed && mGrid[i][j - 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, i, j - 1);
        if (j < GridSizeWidthAndHeight - 1 && mGrid[i][j + 1].CanBePassed && mGrid[i][j + 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(tileConnectivityCollectionNr, i, j + 1);
    }


    private void generateMazeGrid() {
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
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
        recursionMaze(r, c);

    }

    //inspiration for the maze function have been taken from http://www.migapro.com/depth-first-search/
    //There is used depth first search to generate the maze
    private void recursionMaze(int r, int c) {
        // 4 random directions
        int[] randDirs = generateRandomDirections();
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null);
        // Examine each direction
        for (int randDir : randDirs) {

            switch (randDir) {
                case 1: // Up
                    //　Whether 2 cells up is out or not
                    if (r - 2 <= 0)
                        continue;
                    if (!mGrid[r - 2][c].CanBePassed) {
                        mGrid[r - 2][c] = floorTile;
                        mGrid[r - 1][c] = floorTile;
                        recursionMaze(r - 2, c);
                    }
                    break;
                case 2: // Right
                    // Whether 2 cells to the right is out or not
                    if (c + 2 >= GridSizeWidthAndHeight - 1)
                        continue;
                    if (!mGrid[r][c + 2].CanBePassed) {
                        mGrid[r][c + 2] = floorTile;
                        mGrid[r][c + 1] = floorTile;
                        recursionMaze(r, c + 2);
                    }
                    break;
                case 3: // Down
                    // Whether 2 cells down is out or not
                    if (r + 2 >= GridSizeWidthAndHeight - 1)
                        continue;
                    if (!mGrid[r + 2][c].CanBePassed) {
                        mGrid[r + 2][c] = floorTile;
                        mGrid[r + 1][c] = floorTile;
                        recursionMaze(r + 2, c);
                    }
                    break;
                case 4: // Left
                    // Whether 2 cells to the left is out or not
                    if (c - 2 <= 0)
                        continue;
                    if (!mGrid[r][c - 2].CanBePassed) {
                        mGrid[r][c - 2] = floorTile;
                        mGrid[r][c - 1] = floorTile;
                        recursionMaze(r, c - 2);
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
}
