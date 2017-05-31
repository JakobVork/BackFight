package com.studio.jarn.backfight.Gameboard;

//inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board

/**
 * Defines the listener interface for touch actions on a GameBoardView.
 * It defines onTouchDown, onTouchUp, and onLongTouchUp.
 * For onTouchUp and onLongTouchUp, the listener receives index numbers of the squares of the grid.
 * No coordinates are included in the call to onTouchDown.
 */

interface GameTouchListener {

    /**
     * This method is called when a touch Down action occurs.
     * <p>
     * <p> Note that the location of the down location is not provided, but it is provided when the touch ends
     * and a call is made to onTouchUp.
     */

    void onTouchDown();

    /**
     * This method is called when a touch Up action occurs.
     * <p>
     * <p>
     * Index values are 0 based.
     * Values are between 0 and NumSquaresAlongCanvas-1.
     *
     * @param tileX int - The tiles X value on the map
     * @param tileY int - The tiles Y value on the map
     * @param placementX   int - The placement X value on the tile
     * @param placementY   int - The placement Y value on the tile
     */

    void onTouchUp(int tileX, int tileY, int placementX, int placementY);
}
