package com.studio.jarn.backfight;

//inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board

/**
 * Defines the listener interface for touch actions on a GameBoardView.
 * It defines onTouchDown, onTouchUp, and onLongTouchUp.
 * For onTouchUp and onLongTouchUp, the listener receives index numbers of the squares of the grid.
 * No coordinates are included in the call to onTouchDown.
 */

public interface GameBoardTouchListener {

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
     * @param downX int - x value of the down action square
     * @param downY int - y value of the down action square
     * @param upX   int - x value of the up action square
     * @param upY   int - y value of the up action square
     * @return void
     */

    void onTouchUp(int downX, int downY, int upX, int upY);

    /**
     * This method is called when a touch Up action occurs and the time between down and up
     * exceeds the Android long press timeout value.
     * <p>
     * <p>
     * Index values are 0 based.
     * Values are between 0 and NumSquaresAlongCanvas-1.
     *
     * @param downX int - x value of the down action square
     * @param downY int - y value of the down action square
     * @param upX   int - x value of the up action square
     * @param upY   int - y value of the up action square
     * @return void
     */

    void onLongTouchUp(int downX, int downY, int upX, int upY);

}