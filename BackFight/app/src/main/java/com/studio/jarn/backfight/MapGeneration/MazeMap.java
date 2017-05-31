package com.studio.jarn.backfight.MapGeneration;

import com.studio.jarn.backfight.Gameboard.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MazeMap implements IMapGenerator {
    private int sGridSize;

    @Override
    public Tile[][] generateMap(int gridSize) {
        Tile[][] mGrid = new Tile[gridSize][gridSize];
        sGridSize = gridSize;


        for (int row = 0; row < gridSize; row++) {
            for (int column = 0; column < gridSize; column++) {
                mGrid[row][column] = new Tile(Tile.Types.Wall);
            }
        }
        Random rand = new Random();
        // Generate random row
        int row = rand.nextInt(gridSize);
        while (row % 2 == 0) {
            row = rand.nextInt(gridSize);
        }
        // Generate random column
        int column = rand.nextInt(gridSize);
        while (column % 2 == 0) {
            column = rand.nextInt(gridSize);
        }
        // Starting cell
        mGrid[row][column] = new Tile(Tile.Types.WoodenFloor);

        //　Allocate the maze with recursive method
        recursionMaze(mGrid, row, column);

        return mGrid;
    }

    //inspiration for the maze function have been taken from http://www.migapro.com/depth-first-search/
    //There is used depth first search to generate the maze
    private void recursionMaze(Tile[][] mGrid, int row, int column) {
        // 4 random directions
        int[] randDirs = generateRandomDirections();
        Tile floorTile = new Tile(Tile.Types.WoodenFloor);
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
                        recursionMaze(mGrid, row - 2, column);
                    }
                    break;
                case 2: // Right
                    // Whether 2 cells to the right is out or not
                    if (column + 2 >= sGridSize - 1)
                        continue;
                    if (!mGrid[row][column + 2].CanBePassed) {
                        mGrid[row][column + 2] = floorTile;
                        mGrid[row][column + 1] = floorTile;
                        recursionMaze(mGrid, row, column + 2);
                    }
                    break;
                case 3: // Down
                    // Whether 2 cells down is out or not
                    if (row + 2 >= sGridSize - 1)
                        continue;
                    if (!mGrid[row + 2][column].CanBePassed) {
                        mGrid[row + 2][column] = floorTile;
                        mGrid[row + 1][column] = floorTile;
                        recursionMaze(mGrid, row + 2, column);
                    }
                    break;
                case 4: // Left
                    // Whether 2 cells to the left is out or not
                    if (column - 2 <= 0)
                        continue;
                    if (!mGrid[row][column - 2].CanBePassed) {
                        mGrid[row][column - 2] = floorTile;
                        mGrid[row][column - 1] = floorTile;
                        recursionMaze(mGrid, row, column - 2);
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
