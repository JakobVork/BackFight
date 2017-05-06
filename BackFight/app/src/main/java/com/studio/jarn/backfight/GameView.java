package com.studio.jarn.backfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * GameBoardView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

public class GameView extends PanZoomView implements GameTouchListener
{

    protected float mFocusX;
    protected float mFocusY;
    protected GameTouchListener mTouchListener;
    // Variables that control placement and translation of the canvas.
    // Initial values are for debugging on 480 x 320 screen. They are reset in onDrawPz.
    private float mMaxCanvasWidth = 960;
    private float mMaxCanvasHeight = 960;
    private float mHalfMaxCanvasWidth = 480;
    private float mHalfMaxCanvasHeight = 480;
    private float mOriginOffsetX = 0;
    private float mOriginOffsetY = 0;
    private float mSquareWidth = 64;         // use float for more accurate placement
    private float mSquareHeight = 64;
    private Rect  mDestRect;
    private RectF mDestRectF;
    private Tile[][] mGrid;
    //TODO Player should be GameObject
    private ArrayList<Tuple<Player, Coordinates>> mGameObjectList = new ArrayList<>();
    private int mGridSizeWidthAndHeight;
    private int mSquaresViewedAtStartup;

    public GameView(Context context) {
        super(context);
        setTouchListener(this);
    }
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTouchListener(this);
    }
    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTouchListener(this);
    }

    public GameTouchListener getTouchListener() {
        return mTouchListener;
    }

    public void setTouchListener(GameTouchListener newListener) {
        mTouchListener = newListener;
    }

    public void addGameObjects(ArrayList<Tuple<Player, Coordinates>> gameObjects){
        mGameObjectList.addAll(gameObjects);
    }

    //TODO Player should be GameObjects
    // Inspiration found here: http://stackoverflow.com/questions/10616777/how-to-merge-to-two-bitmap-one-over-another
    private Bitmap addPlayerToBitmap(Bitmap bmp1, List<Tuple<Player, Coordinates>> players) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        float column = 0;
        float row = 0;
        for (Tuple p : players) {

            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    ((Player)p.x).getFigure()), bmp1.getWidth() * (row / 4), bmp1.getHeight() * (column / 4), null);

            // logic to distribute players over the tile, so each tile can hold 16 players
            if (column < 4)
                row++;
            else {
                Log.d("Error", "More than 16 players added");
                break;
            }
            if (row == 4) {
                column++;
                row = 0;
            }

        }

        return bmOverlay;
    }

public void setGridSize(int newValue)
{
   mGridSizeWidthAndHeight = newValue;
}

public void setViewSizeAtStartup(int newValue)
{
   mSquaresViewedAtStartup = newValue;
}

    //Todo: remove hardcoded Players
public void drawOnCanvas (Canvas canvas) {

    Paint paint = new Paint();

    Bitmap bm_wall = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall128);
    Bitmap bm_floor = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.floor128);

    //
    // Draw squares to fill the grid.
    //
    RectF dest1 = mDestRectF;
    float dx, dy = 0;
    for (int j = 0; j < mGridSizeWidthAndHeight; j++) {
       dx = 0;
       for (int i = 0; i < mGridSizeWidthAndHeight; i++) {
        dest1.offsetTo(dx, dy);

           switch (mGrid[j][i].Type) {
               case Wall: {
                    canvas.drawBitmap(bm_wall, null, dest1, paint);
                    break;
               }
               case WoodenFloor: {
                   canvas.drawBitmap(bm_floor, null, dest1, paint);
                   break;
               }
           }
        dx = dx + mSquareWidth;
       }
       dy = dy + mSquareHeight;
    }
    UpdateMapWithPlayers(bm_floor);
}

    private void UpdateMapWithPlayers(Bitmap tile){
        //TODO Should be implemented
        mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Nicklas"), new Coordinates(0,0)));

        addPlayerToBitmap(tile, mGameObjectList);
    }

/**
 * onDrawPz
 * This method is copied from here http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */
 
@Override
public void onDrawPz(Canvas canvas) {
   
    canvas.save();
 
    // Get the width and height of the view.
    int viewH = getHeight (), viewW = getWidth ();

    boolean isLandscape = (viewW > viewH);
    float shortestWidth = isLandscape ? viewH : viewW;
 
    // Set width and height to be used for the squares.
    mSquareWidth = shortestWidth / (float) mSquaresViewedAtStartup;
    mSquareHeight = shortestWidth / (float) mSquaresViewedAtStartup;

    float numSquaresAlongX = isLandscape ? (viewW / mSquareWidth) : mSquaresViewedAtStartup;
    float numSquaresAlongY = isLandscape ? mSquaresViewedAtStartup : (viewH / mSquareHeight);

    // We start out knowing how many squares will be displayed
    // along a side and how many along the whole canvas.
    // The canvas is centered in the view so half of what
    // remains to be displayed can be used to calculate the
    // origin offset values.
    mMaxCanvasWidth  = mGridSizeWidthAndHeight * mSquareWidth;
    mMaxCanvasHeight = mGridSizeWidthAndHeight * mSquareHeight;
    mHalfMaxCanvasWidth  = mMaxCanvasWidth / 2.0f;
    mHalfMaxCanvasHeight = mMaxCanvasHeight / 2.0f;

    float totalOffscreenSquaresX = mGridSizeWidthAndHeight - numSquaresAlongX;
    if (totalOffscreenSquaresX < 0f) totalOffscreenSquaresX = 0f;
    float totalOffscreenSquaresY = mGridSizeWidthAndHeight - numSquaresAlongY;
    if (totalOffscreenSquaresY < 0f) totalOffscreenSquaresY = 0f;
    mOriginOffsetX = 0;//totalOffscreenSquaresX / 2.0f * mSquareWidth;
    mOriginOffsetY = 0;//totalOffscreenSquaresY / 2.0f * mSquareHeight;

    // The canvas is translated by the amount we have
    // scrolled and the standard amount to move the origin
    // of the canvas up and left so the region is centered
    // in the view. (Note: mPosX and mPosY are defined in PanZoomView.)
    float x, y;
    mPosX0 = mOriginOffsetX;
    mPosY0 = mOriginOffsetY;
    mPosY0 = mOriginOffsetY;
    x = mPosX - mPosX0;
    y = mPosY - mPosY0;
    canvas.translate (x, y);

    // The focus point for zooming is the center of the
    // displayable region. That point is defined by half
    // the canvas width and height.
    mFocusX = mHalfMaxCanvasWidth;
    mFocusY = mHalfMaxCanvasHeight;
    canvas.scale (mScaleFactor, mScaleFactor, mFocusX, mFocusY);

    // Set up the grid  and grid selection variables.
    if (mGrid == null)
        mGrid = new Tile[mGridSizeWidthAndHeight][mGridSizeWidthAndHeight];

    // Set up the rectangles we use for drawing, if not done already.
    // Set width and height to be used for the rectangle to be drawn.
    Rect dest = mDestRect;
    if (dest == null) {
       int ih = (int) Math.floor (mSquareHeight);
       int iw = (int) Math.floor (mSquareWidth);
       dest = new Rect (0, 0, iw, ih);
       mDestRect = dest;
    }
    RectF dest1 = mDestRectF;
    if (dest1 == null) {
       dest1 = new RectF ();
       dest1.left = dest.left; dest1.top = dest.top;
       dest1.right = mSquareWidth; dest1.bottom = mSquareHeight;
       mDestRectF = dest1;
    }
    
    // Do the drawing operation for the view.
    drawOnCanvas (canvas);
 
    canvas.restore();
 
}

    @Override
    protected void setupToDraw (Context context, AttributeSet attrs, int defStyle) {
        super.setupToDraw (context, attrs, defStyle);
    }


    public void updateGrid(Tile grid[][]) {
        // Set up the grid  and grid selection variables.
        if (mGrid == null) mGrid = new Tile[mGridSizeWidthAndHeight][mGridSizeWidthAndHeight];

        mGrid = grid.clone();
    }

    public void onTouchDown(float downX, float downY) {
        GameTouchListener listener = getTouchListener();
        if (listener == null) return;
        listener.onTouchDown();
    }

    public void onTouchUp(float downX, float downY, float upX, float upY) {
        //Calculate the coordinates pressed on the map
        float xCoord = (mOriginOffsetX + upX - (mPosX-((mMaxCanvasWidth/2)*mScaleFactor-(mMaxCanvasWidth/2))))/mScaleFactor;
        float yCoord = (mOriginOffsetY + upY - (mPosY-((mMaxCanvasHeight/2)*mScaleFactor-(mMaxCanvasHeight/2))))/mScaleFactor;

        //Coordinates to tile
        int tileX = (int)(xCoord / mSquareWidth);
        int tileY = (int)(yCoord / mSquareHeight);

        GameTouchListener listener = getTouchListener();
        if (listener == null) return;
        listener.onTouchUp(xCoord, yCoord);
    }


    @Override
    public void onTouchDown() {

    }

    @Override
    public void onTouchUp(float upX, float upY) {


        invalidate();
    }

    @Override
    public void onLongTouchUp(int downX, int downY, int upX, int upY) {

    }
} // end class
  
