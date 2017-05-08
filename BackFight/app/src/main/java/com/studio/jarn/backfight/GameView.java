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

import com.google.common.collect.Iterables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * GameBoardView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

public class GameView extends PanZoomView implements GameBoardTouchListener
{

    protected float mFocusX;
    protected float mFocusY;
    protected GameBoardTouchListener mTouchListener;
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
    private Rect  mDestRect;
    private RectF mDestRectF;
    private Tile[][] mGrid;
    private int mGridSizeWidthAndHeight;
    private int mSquaresViewedAtStartup;
    private String mUuid;
    private DatabaseReference databaseReference;

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

    public GameBoardTouchListener getTouchListener() {
        return mTouchListener;
    }

    public void setTouchListener(GameBoardTouchListener newListener) {
        mTouchListener = newListener;
    }

    // Inspiration found here: http://stackoverflow.com/questions/10616777/how-to-merge-to-two-bitmap-one-over-another
    private Bitmap addPlayerToBitmap(Bitmap bmp1, List<Player> players) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);

        float column = 0;
        float row = 0;
        for (Player p : players) {

            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
                    p.Icon), bmp1.getWidth() * (row / 4), bmp1.getHeight() * (column / 4), null);

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

    public void setUuidStartup(String uuid) {
        mUuid = uuid;
    }



    //Todo: remove hardcoded Players
public void drawOnCanvas (Canvas canvas) {

    Paint paint = new Paint();

    Bitmap bm_wall = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall128);
    Bitmap bm_floor = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.floor128);

    //
    // Draw squares to fill the grid.
    //

    //Check to see if there has been added an array to mGrid
    if (mGrid[0][0] == null) {
        return;
    }

    RectF dest1 = mDestRectF;
    float dx, dy = 0;
    for (int j = 0; j < mGridSizeWidthAndHeight; j++) {
       dx = 0;
       for (int i = 0; i < mGridSizeWidthAndHeight; i++) {
        dest1.offsetTo (dx, dy);

           switch (mGrid[j][i].Type) {
               case Wall: {
                   if (mGrid[j][i].Players != null) {
                       canvas.drawBitmap(addPlayerToBitmap(bm_wall, mGrid[j][i].Players), null, dest1, paint);
                   } else
                       canvas.drawBitmap(bm_wall, null, dest1, paint);
                   break;
               }
               case WoodenFloor: {
                   if (mGrid[j][i].Players != null) {
                       canvas.drawBitmap(addPlayerToBitmap(bm_floor, mGrid[j][i].Players), null, dest1, paint);
                   } else
                       canvas.drawBitmap(bm_floor, null, dest1, paint);
                   break;
               }
           }
        dx = dx + mSquareWidth;
       }
       dy = dy + mSquareHeight;
    }
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
    mOriginOffsetX = totalOffscreenSquaresX / 2.0f * mSquareWidth;
    mOriginOffsetY = totalOffscreenSquaresY / 2.0f * mSquareHeight;

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


    public void initHostGrid(Tile grid[][]) {
        // Set up the grid  and grid selection variables.
        if (mGrid == null)
            mGrid = grid;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(mUuid);
        setupOnDataChange();
        updateGrid(grid);
    }


    public void initClientGrid() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(mUuid);
        setupOnDataChange();
    }

    private void setupOnDataChange() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int row = -1;
                int column = -1;

                int sizeOfArrayOnFirebase = Iterables.size(dataSnapshot.getChildren());
                if (mGridSizeWidthAndHeight != sizeOfArrayOnFirebase) {
                    mGridSizeWidthAndHeight = sizeOfArrayOnFirebase;
                    mGrid = new Tile[mGridSizeWidthAndHeight][mGridSizeWidthAndHeight];
                }


                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    row++;
                    for (DataSnapshot postSnapshot1 : postSnapshot.getChildren()) {
                        column++;
                        mGrid[row][column] = postSnapshot1.getValue(Tile.class);
                    }
                    column = -1;
                }
                invalidate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }


    public void updateGrid(Tile grid[][]) {

        List<List<Tile>> list = new ArrayList<>();
        for (Tile[] aMGrid : grid) {
            list.add(Arrays.asList(aMGrid));
        }
        databaseReference.setValue(list);
    }


    public void onTouchDown(float downX, float downY) {
        GameBoardTouchListener listener = getTouchListener();
        if (listener == null) return;
        listener.onTouchDown();
    }

    public void onTouchUp(float downX, float downY, float upX, float upY) {
        // Convert view coordinates to canvas coordinates and, eventually,
        // to index values for the cells being displayed.

        // If the scale is 1, it is easy. When the scale is one, we know that exactly
        // pNumSquaresAlongSide are on the screen. The rest are off the screen.
        // Since that has already been accounted for, in the origin offset values,
        // simply add the x or y arg values to the origin offsets and divide
        // by the square width to get index values.
        float fx = (mOriginOffsetX + upX - mPosX) / mSquareWidth;
        float fy = (mOriginOffsetY + upY - mPosY) / mSquareHeight;
        float dfx = (mOriginOffsetX * mScaleFactor + downX - mPosX) / mSquareWidth;
        float dfy = (mOriginOffsetY * mScaleFactor + downY - mPosY) / mSquareHeight;
        float fx2 = fx, dfx2 = dfx;
        float fy2 = fy, dfy2 = dfy;

        /*if (mScaleFactor == 1.0f) {
            // Use the four float values already computed. Convert them to int values. See below.
        } else {*/
            // If scaling is on, we have to adjust the up and down points by the scale factor and
            // we have to account for the points that do not show up in the visible view.

            // 1. Figure out how many squares are showing on the screen. We need that to convert view
            // coordinates to index numbers.
            float scaledSqWidth = (mScaleFactor * mSquareWidth);
            float scaledSqHeight = (mScaleFactor * mSquareHeight);

            // 2 (new method)
            // We zoom around the center point of the canvas.
            // First we need to figure out where the point is, in view coordinates. Look at the unzoomed values we have saved.
            // The focus point never changes as we zoom. Use its x and y values to determine how many squares are showing on the screen.
            // If you know the number of squares visible left and above the focus point, you can figure out how many squares are
            // offscreen. (Remember we know the focus point is half of the canvas width and height.)
            float vFocusX = mFocusX - mOriginOffsetX;
            float vFocusY = mFocusY - mOriginOffsetY;
            float numSquaresToLeft = vFocusX / scaledSqWidth;
            float numSquaresAbove = vFocusY / scaledSqHeight;
            float numSquaresToCenter = mMaxCanvasWidth / mSquareWidth / 2f;
            float numSquaresOffscreenLeft = numSquaresToCenter - numSquaresToLeft;
            float numSquaresOffscreenAbove = numSquaresToCenter - numSquaresAbove;
            float vsqX = (upX - mPosX) / scaledSqWidth;
            float vsqY = (upY - mPosY) / scaledSqHeight;
            fx2 = vsqX + numSquaresOffscreenLeft;
            fy2 = vsqY + numSquaresOffscreenAbove;
            dfx2 = (downX - mPosX) / scaledSqWidth + numSquaresOffscreenLeft;
            dfy2 = (downY - mPosY) / scaledSqHeight + numSquaresOffscreenAbove;

        //}

        float x2 = 0, y2 = 0;

        // We want integer index values to call the listener.
        // Use floor to round down. Do not need to add one since origin offset includes whole square.
        //
        int sUpX = (int) Math.floor(fx2) + 1;
        int sUpY = (int) Math.floor(fy2) + 1;
        int sDownX = (int) Math.floor(dfx2) + 1;
        int sDownY = (int) Math.floor(dfy2) + 1;

        // Next check to see if there is a listener for these events.
        // If there is not, there is nothing else to do.
        GameBoardTouchListener listener = getTouchListener();
        if (listener == null) return;

        listener.onTouchUp(sDownX, sDownY, sUpX, sUpY);
    }


    @Override
    public void onTouchDown() {

    }

    @Override
    public void onTouchUp(int downX, int downY, int upX, int upY) {
        //Check that click is on board
        if (upX > (mMaxCanvasWidth / mSquareWidth) || upY > (mMaxCanvasHeight / mSquareHeight))
            return;
        if (upX < 1 || upY < 1) return;

        Tile selectedTile = mGrid[upX - 1][upY - 1];
        if (selectedTile.Players == null) return;
        if (selectedTile.Players.size() > 0) {
            //TODO Missing check on what figure it is and swap with correct figure
            selectedTile.Players.get(0).Icon = R.drawable.player32selected;
            invalidate(); //This method renders the map
            //TODO call function for showing items
        }

    }

    @Override
    public void onLongTouchUp(int downX, int downY, int upX, int upY) {

    }
} // end class
  
