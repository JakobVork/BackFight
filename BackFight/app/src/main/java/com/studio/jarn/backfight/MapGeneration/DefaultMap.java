package com.studio.jarn.backfight.MapGeneration;

import com.studio.jarn.backfight.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DefaultMap implements IMapGenerator {
    private final List<Integer> checkedList = new ArrayList<>();
    private int TileConnectivityCollectionNrCounter = 0;
    private int sGridSize;

    @Override
    public Tile[][] generateMap(int gridSize) {
        Random random = new Random();
        Tile[][] mGrid = new Tile[gridSize][gridSize];
        sGridSize = gridSize;

        //generate random board with 50% walls and floor
        for (int row = 0; row < gridSize; row++) {
            for (int column = 0; column < gridSize; column++) {

                if (random.nextInt(99) + 1 > 50) //50% chance of placing a floorTile
                    mGrid[row][column] = new Tile(Tile.Types.WoodenFloor);
                else
                    mGrid[row][column] = new Tile(Tile.Types.Wall);
            }
        }
        mGrid = connectAllFloorSpacesInMap(mGrid);

        return mGrid;
    }

    private Tile[][] connectAllFloorSpacesInMap(Tile[][] mGrid) {
        mGrid = checkConnectivity(mGrid); // Check once before while-loop


        //Do this until all floor spaces is connected together
        while (TileConnectivityCollectionNrCounter > 1) {

            for (int row = 0; row < sGridSize; row++) {
                for (int column = 0; column < sGridSize; column++) {

                    //check if tile is floor and if it has already been visited
                    if (mGrid[row][column].CanBePassed && checkIfVisited(mGrid[row][column].TileConnectivityCollectionNr)) {
                        mGrid = changeWallToFloor(mGrid, row, column);
                    }
                }
            }
            //reset TileConnectivityCollectionNrCounter and clear the checkedList so its ready for the next checkConnectivity() run
            TileConnectivityCollectionNrCounter = 0;
            checkedList.clear();

            mGrid = checkConnectivity(mGrid);
        }

        return mGrid;
    }

    private Tile[][] changeWallToFloor(Tile[][] mGrid, int row, int column) {
        //check if it is first row and then change the first wall it meets going to the right to a floor
        if (row == 0) {
            for (int spacesToGoRight = 0; spacesToGoRight < sGridSize; spacesToGoRight++) {
                if (!mGrid[row + spacesToGoRight][column].CanBePassed) {
                    mGrid[row + spacesToGoRight][column] = new Tile(Tile.Types.WoodenFloor);
                    break;
                }
                //not possible to to connect tiles by going horizontal, now try to go vertical
                else if (spacesToGoRight == sGridSize - 1) {
                    for (int spacesToGoDown = 0; spacesToGoDown < sGridSize - column; spacesToGoDown++) {
                        if (!mGrid[row][column + spacesToGoDown].CanBePassed) {
                            mGrid[row][column + spacesToGoDown] = new Tile(Tile.Types.WoodenFloor);
                            break;
                        } else if (spacesToGoDown == column) {
                            for (int spacesToGoUp = 0; spacesToGoUp < column; spacesToGoUp++) {
                                if (!mGrid[row][column - spacesToGoUp].CanBePassed) {
                                    mGrid[row][column - spacesToGoUp] = new Tile(Tile.Types.WoodenFloor);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            outerLoop:
            for (int spacesToGoLeft = 0; spacesToGoLeft <= row; spacesToGoLeft++) {
                if (!mGrid[row - spacesToGoLeft][column].CanBePassed) { //Try to change wall tile to floor, to the left side
                    mGrid[row - spacesToGoLeft][column] = new Tile(Tile.Types.WoodenFloor);
                    break;
                }
                if (spacesToGoLeft == row) { //if there is no wall tiles to the left all the way to the edge, try to change to the right
                    for (int spacesToGoRight = 0; spacesToGoRight < sGridSize - row; spacesToGoRight++) {
                        if (!mGrid[row + spacesToGoRight][column].CanBePassed) {
                            mGrid[row + spacesToGoRight][column] = new Tile(Tile.Types.WoodenFloor);
                            break outerLoop;
                        }
                    }
                }
            }
        }

        return mGrid;
    }

    private Tile[][] checkConnectivity(Tile[][] mGrid) { //returned int before
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
                    visitAllConnectedNeighbors(mGrid, ++TileConnectivityCollectionNrCounter, row, column);
                }
            }
        }

        return mGrid;
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

    //recursive logic to take a floor tile and visit all the connecting neighbors
    private void visitAllConnectedNeighbors(Tile[][] mGrid, int tileConnectivityCollectionNr, int row, int column) {
        Tile floorTile = new Tile(Tile.Types.WoodenFloor, tileConnectivityCollectionNr);
        mGrid[row][column] = floorTile;
        if (row > 0 && mGrid[row - 1][column].CanBePassed && mGrid[row - 1][column].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(mGrid, tileConnectivityCollectionNr, row - 1, column);
        if (row < sGridSize - 1 && mGrid[row + 1][column].CanBePassed && mGrid[row + 1][column].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(mGrid, tileConnectivityCollectionNr, row + 1, column);
        if (column > 0 && mGrid[row][column - 1].CanBePassed && mGrid[row][column - 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(mGrid, tileConnectivityCollectionNr, row, column - 1);
        if (column < sGridSize - 1 && mGrid[row][column + 1].CanBePassed && mGrid[row][column + 1].TileConnectivityCollectionNr != tileConnectivityCollectionNr)
            visitAllConnectedNeighbors(mGrid, tileConnectivityCollectionNr, row, column + 1);
    }
}
