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
    private final Tile wallTile = new Tile(Tile.Types.Wall, null, 0);
    private final Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
    private final List<Integer> checkedList = new ArrayList<>();
    private Tile[][] mGrid;
    private int visitCounter = 0;

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


        Tile floorTileWithPlayers = new Tile(Tile.Types.WoodenFloor, players, 0);
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
                    if (mGrid[r][c].Type == Tile.Types.WoodenFloor && checkIfVisited(mGrid[r][c].Visit)) {
                        changeWallToFloor(r, c);
                    }
                }
            }
            mGrid[1][1].Visit = 0;
            visitCounter = 0;
            checkedList.clear();
        }
    }

    private void changeWallToFloor(int r, int c) {
        //check if it is first row and then change the first wall it meets going to the right to a floor
        if (r == 0) {
            for (int counter = 0; counter < GridSizeWidthAndHeight; counter++) {
                if (mGrid[r + counter][c].Type == Tile.Types.Wall) {
                    mGrid[r + counter][c] = floorTile;
                    break;
                }
            }
        } else {
            outerLoop:
            for (int counter = 0; counter <= r; counter++) {
                if (mGrid[r - counter][c].Type == Tile.Types.Wall) { //Try to change wall tile to floor, to the left side
                    mGrid[r - counter][c] = floorTile;
                    break;
                }
                if (counter == r) { //if there is no wall tiles to the left all the way to the edge, try to change to the right
                    for (int counter2 = 0; counter2 <= GridSizeWidthAndHeight - 1 - r; counter2++) {
                        if (mGrid[r + counter2][c].Type == Tile.Types.Wall) {
                            mGrid[r + counter2][c] = floorTile;
                            break outerLoop;
                        }
                    }
                }
            }
        }
    }

    // Check if the tile group has already been visited (part of the checkedList)
    private boolean checkIfVisited(int visit) {

        for (Integer checkedInts : checkedList) {
            if (checkedInts == visit) {
                return false;
            }
        }
        checkedList.add(visit);
        return true;
    }

    private int checkConnectivity() {
        //reset the visit count for all spaces in the Grid
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
                mGrid[i][j].Visit = 0;
            }
        }

        //for each floor tile go and visit all their neighbors, when no more neighbors count
        // visitCounter one up and find the next floor tile that has not been visited.
        for (int i = 0; i < GridSizeWidthAndHeight; i++) {
            for (int j = 0; j < GridSizeWidthAndHeight; j++) {
                if (mGrid[i][j].Type == Tile.Types.WoodenFloor && mGrid[i][j].Visit == 0) {
                    visitAllConnectedNeighbors(++visitCounter, i, j);
                }
            }
        }
        return visitCounter;
    }

    //recursive logic to take a floor tile and visit all the connecting neighbors
    private void visitAllConnectedNeighbors(int visitCollectionNr, int i, int j) {
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, visitCollectionNr);
        mGrid[i][j] = floorTile;
        if (i > 0 && mGrid[i - 1][j].Type == Tile.Types.WoodenFloor && mGrid[i - 1][j].Visit != visitCollectionNr)
            visitAllConnectedNeighbors(visitCollectionNr, i - 1, j);
        if (i < GridSizeWidthAndHeight - 1 && mGrid[i + 1][j].Type == Tile.Types.WoodenFloor && mGrid[i + 1][j].Visit != visitCollectionNr)
            visitAllConnectedNeighbors(visitCollectionNr, i + 1, j);
        if (j > 0 && mGrid[i][j - 1].Type == Tile.Types.WoodenFloor && mGrid[i][j - 1].Visit != visitCollectionNr)
            visitAllConnectedNeighbors(visitCollectionNr, i, j - 1);
        if (j < GridSizeWidthAndHeight - 1 && mGrid[i][j + 1].Type == Tile.Types.WoodenFloor && mGrid[i][j + 1].Visit != visitCollectionNr)
            visitAllConnectedNeighbors(visitCollectionNr, i, j + 1);
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
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, null, 0);
        // Examine each direction
        for (int randDir : randDirs) {

            switch (randDir) {
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
