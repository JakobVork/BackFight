package com.example.banders.de;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;


/**
 * GameBoardView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

public class GameBoardView extends PanZoomView
{

    // Variables that control placement and translation of the canvas.
    // Initial values are for debugging on 480 x 320 screen. They are reset in onDrawPz.
    private float mMaxCanvasWidth = 960;
    private float mMaxCanvasHeight = 960;
    private float mHalfMaxCanvasWidth = 480;
    private float mHalfMaxCanvasHeight = 480;
    private float mOriginOffsetX = 320;
    private float mOriginOffsetY = 320;
    private float mSquareWidth = 64;         // use float for more accurate placement
    private float mSquareHeight = 64;

    protected float mFocusX;
    protected float mFocusY;

    private Rect  mDestRect;
    private RectF mDestRectF;

    private int [] [] mGrid;


public GameBoardView (Context context) {
    super (context);
}
 
public GameBoardView (Context context, AttributeSet attrs) {
    super (context, attrs);
}
 
public GameBoardView (Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
}


private int mGridSizeWidthAndHeight;
public void setGridSize(int newValue)
{
   mGridSizeWidthAndHeight = newValue;
}

private int mSquaresViewedAtStartup;
public void setViewSizeAtStartup(int newValue)
{
   mSquaresViewedAtStartup = newValue;
}


//Todo: remove hardcoded players
public void drawOnCanvas (Canvas canvas) {

    Paint paint = new Paint();

    Bitmap b1 = BitmapFactory.decodeResource (mContext.getResources(), R.drawable.no_marker_default);
    Bitmap b2 = BitmapFactory.decodeResource (mContext.getResources(), R.drawable.cart);
    Bitmap b3 = BitmapFactory.decodeResource (mContext.getResources(), R.drawable.point);
    Bitmap b4 = addPlayerToBitmap(b1,b2, b3);

    //
    // Draw squares to fill the grid.
    //
    RectF dest1 = mDestRectF;
    float dx = 0, dy = 0;
    for (int j = 0; j < mGridSizeWidthAndHeight; j++) {
       dx = 0;
       for (int i = 0; i < mGridSizeWidthAndHeight; i++) {
        dest1.offsetTo (dx, dy);

        canvas.drawBitmap (b1, null, dest1, paint);
        if(j == 5 && i == 6)
            canvas.drawBitmap (b4, null, dest1, paint);

        dx = dx + mSquareWidth;
       }
       dy = dy + mSquareHeight;
    }
}

// Inspiration found here: http://stackoverflow.com/questions/10616777/how-to-merge-to-two-bitmap-one-over-another
public static Bitmap addPlayerToBitmap(Bitmap bmp1, Bitmap bmp2, Bitmap bmp3) {
    Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
    Canvas canvas = new Canvas(bmOverlay);
    canvas.drawBitmap(bmp1, new Matrix(), null);
    canvas.drawBitmap(bmp2, 0, 0, null);
    canvas.drawBitmap(bmp3, 0, bmp1.getHeight()/2, null);
    return bmOverlay;
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
    mSquareWidth  = (float) shortestWidth / (float) mSquaresViewedAtStartup;
    mSquareHeight = (float) shortestWidth / (float) mSquaresViewedAtStartup;

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
    mOriginOffsetX = totalOffscreenSquaresX / 2.0f * mSquareWidth;
    mOriginOffsetY = totalOffscreenSquaresY / 2.0f * mSquareHeight;

    // The canvas is translated by the amount we have
    // scrolled and the standard amount to move the origin
    // of the canvas up and left so the region is centered
    // in the view. (Note: mPosX and mPosY are defined in PanZoomView.)
    float x = 0, y = 0;
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
       mGrid = new int [mGridSizeWidthAndHeight] [mGridSizeWidthAndHeight];

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


public void updateGrid (int grid [][]){
   // Set up the grid  and grid selection variables.
   if (mGrid == null) mGrid = new int [mGridSizeWidthAndHeight] [mGridSizeWidthAndHeight];

    mGrid = grid.clone();
}

} // end class
  
