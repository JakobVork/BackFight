package com.studio.jarn.backfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
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
    Boolean onlyOnce = true;
    FirebaseDatabase database;
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
    private Rect mDestRect;
    private RectF mDestRectF;
    private Tile[][] mGrid;
    //TODO Player should be GameObject
    private ArrayList<Tuple<Player, Coordinates>> mGameObjectList = new ArrayList<>();
    private int mGridSizeWidthAndHeight;
    private int mSquaresViewedAtStartup;
    private String mUuid;
    private DatabaseReference databaseReference;
    private Player mSelectedObject;
    private int mTileDivision = 4;
    private String mUuidPlayers;



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

    //TODO should be implemented correctly
    public void addGameObjects() {
/*        mGameObjectList.add(new Tuple<>();
    mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Pernille"), new Coordinates(0, 1, 0, 0)));
    mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Pernille"), new Coordinates(0, 0, 0, 1)));
    mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Pernille"), new Coordinates(0, 0, 1, 0)));
    mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Anders"), new Coordinates(0, 0, 1, 1)));
    mGameObjectList.add(new Tuple<>(new Player(R.drawable.player32, R.drawable.player32selected, "Anders"), new Coordinates(1, 0, 0, 0)));
    invalidate();*/
    }

    public void initAddPlayers(List<Player> players) {
        int coordinatesCounter = 0;
        ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates = new ArrayList<>();
        outerLoop:
        for (int row = 0; row < mGridSizeWidthAndHeight; row++) {
            for (int column = 0; column < mGridSizeWidthAndHeight; column++) {
                if (mGrid[row][column].CanBePassed) {
                    for (Player player : players) {
                        playersWithCoordinates.add(new Tuple<>(player, new Coordinates(column, row, coordinatesCounter++, 0)));
                    }
                    addPlayerListToDb(playersWithCoordinates);

                    break outerLoop;
                }
            }
        }
    }

    private void addPlayerListToDb(ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates) {
        databaseReference = database.getReference(mUuidPlayers);
        databaseReference.setValue(playersWithCoordinates);
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
    myDraw(canvas);
}

    private void myDraw(Canvas canvas) {
        for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), tuple.x.getFigure()), getXCoordFromObjectPlacement(tuple.y), getYCoordFromObjectPlacement(tuple.y), null);
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
    protected void setupToDraw(Context context, AttributeSet attrs, int defStyle) {
        super.setupToDraw(context, attrs, defStyle);
    }

    public void initHostGrid(Tile grid[][]) {
        // Set up the grid  and grid selection variables.
        if (mGrid == null)
            mGrid = grid;

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(mUuid);
        setupOnDataChange();
        updateGrid(grid);
    }

    public void initClientGrid() {
        database = FirebaseDatabase.getInstance();
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

                if (dataSnapshot.getValue() == null) return;

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
        GameTouchListener listener = getTouchListener();
        if (listener == null) return;
        listener.onTouchDown();
    }

    public void onTouchUp(float downX, float downY, float upX, float upY) {
        if (onlyOnce) {
            addGameObjects();
            onlyOnce = false;
        }

        //Calculate the coordinates pressed on the map
        Coordinates map = getTileFromPixelValue(upX, upY);

        GameTouchListener listener = getTouchListener();
        if (listener == null) return;
        listener.onTouchUp(map.tileX, map.tileY, map.placementOnTileX, map.placementOnTileY);
    }


    @Override
    public void onTouchDown() {

    }

    @Override
    public void onTouchUp(int tileX, int tileY, int placementX, int placementY) {

        //Click is outside map: do nothing
        if (placementX < 0 || placementY < 0 || tileX >= (mMaxCanvasWidth / mSquareWidth) || tileY >= (mMaxCanvasHeight / mSquareHeight))
            return;
        if (!mGrid[tileY][tileX].CanBePassed) return;

        //Check every object on the map
        for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
            //Move selected object
            if (tuple.x.equals(mSelectedObject)) {
                for (Tuple<Player, Coordinates> tuple1 : mGameObjectList) {
                    if (tuple1.y.tileX == tileX && tuple1.y.tileY == tileY && tuple1.y.placementOnTileX == placementX && tuple1.y.placementOnTileY == placementY) {
                        mSelectedObject.SelectPlayer();
                        tuple1.x.SelectPlayer();
                        mSelectedObject = tuple1.x;

                        if (tuple.x.equals(tuple1.x)) {
                            tuple.x.SelectPlayer();
                            mSelectedObject = null;
                        }

                        invalidate();
                        return;
                }
                }

                tuple.y = moveToTile(tileX, tileY);
                tuple.x.SelectPlayer();
                mSelectedObject = null;
            }

            //Select or DeSelect object
            if (tuple.y.tileX == tileX && tuple.y.tileY == tileY && tuple.y.placementOnTileX == placementX && tuple.y.placementOnTileY == placementY) {
                if (!tuple.x.isSelected()) {
                    tuple.x.SelectPlayer();
                    if (mSelectedObject != null) mSelectedObject.SelectPlayer();
                    mSelectedObject = tuple.x;
                }
            }


        }

        addPlayerListToDb(mGameObjectList); //TODo
        //Render map
        invalidate();
    }

    @Override
    public void onLongTouchUp(int downX, int downY, int upX, int upY) {

    }

    //TODO Should be moved to own class
    private Coordinates getTileFromPixelValue(float xCoord, float yCoord) {
        float x = (mOriginOffsetX + xCoord - (mPosX - ((mMaxCanvasWidth / 2) * mScaleFactor - (mMaxCanvasWidth / 2)))) / mScaleFactor;
        float y = (mOriginOffsetY + yCoord - (mPosY - ((mMaxCanvasHeight / 2) * mScaleFactor - (mMaxCanvasHeight / 2)))) / mScaleFactor;

        //Coordinates to tile
        Coordinates map = new Coordinates();
        map.tileX = (int) (x / mSquareWidth);
        map.tileY = (int) (y / mSquareHeight);
        float placementWidth = mSquareWidth / mTileDivision;
        float placementHeight = mSquareHeight / mTileDivision;


        map.placementOnTileX = (int) (x - (mSquareWidth * map.tileX));
        if (0 <= map.placementOnTileX && map.placementOnTileX <= placementWidth)
            map.placementOnTileX = 0;
        else if (placementWidth < map.placementOnTileX && map.placementOnTileX <= (placementWidth * 2))
            map.placementOnTileX = 1;
        else if ((placementWidth * 2) < map.placementOnTileX && map.placementOnTileX <= (placementWidth * 3))
            map.placementOnTileX = 2;
        else if ((placementWidth * 3) < map.placementOnTileX && map.placementOnTileX <= placementWidth * 4)
            map.placementOnTileX = 3;

        map.placementOnTileY = (int) (y - (mSquareHeight * map.tileY));
        if (0 <= map.placementOnTileY && map.placementOnTileY <= placementHeight)
            map.placementOnTileY = 0;
        else if (placementHeight < map.placementOnTileY && map.placementOnTileY <= (placementHeight * 2))
            map.placementOnTileY = 1;
        else if ((placementHeight * 2) < map.placementOnTileY && map.placementOnTileY <= (placementHeight * 3))
            map.placementOnTileY = 2;
        else if ((placementHeight * 3) < map.placementOnTileY && map.placementOnTileY <= (placementHeight * 4))
            map.placementOnTileY = 3;

        return map;
    }

    private float getXCoordFromObjectPlacement(Coordinates objectCoordinates) {
        return (mSquareWidth * objectCoordinates.tileX) + (mSquareWidth * objectCoordinates.placementOnTileX / mTileDivision);
    }

    private float getYCoordFromObjectPlacement(Coordinates objectCoordinates) {
        return (mSquareHeight * objectCoordinates.tileY) + (mSquareHeight * objectCoordinates.placementOnTileY / mTileDivision);
    }

    private Coordinates moveToTile(int tileX, int tileY) {
        ArrayList<Tuple<Player, Coordinates>> onTile = new ArrayList<>();

        for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
            if (tuple.y.tileX == tileX && tuple.y.tileY == tileY) onTile.add(tuple);
        }
        /*for(Tuple<Monster, Coordinates> tuple : mGameObjectList){
            if(tuple.y.tileX == toX && tuple.y.tileY == toY) onTile.add(tuple);
        }
        for(Tuple<Item, Coordinates> tuple : mGameObjectList){
            if(tuple.y.tileX == toX && tuple.y.tileY == toY) onTile.add(tuple);
        }*/

        if (onTile.size() == (mTileDivision * mTileDivision)) return null;


        for (int y = 0; y < mTileDivision; y++) {
            for (int x = 0; x < mTileDivision; x++) {
                Boolean placementFree = true;
                for (Tuple<Player, Coordinates> tuple : onTile) {
                    if (tuple.y.placementOnTileX == x && tuple.y.placementOnTileY == y)
                        placementFree = false;
                }
                if (placementFree) return new Coordinates(tileX, tileY, x, y);
            }
        }

        return null;
    }

    public void setPlayerListener(String uuidPlayers) {
        mUuidPlayers = uuidPlayers;
        databaseReference = database.getReference(mUuidPlayers);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mGameObjectList.clear();

                GenericTypeIndicator<Tuple<Player, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<Player, Coordinates>>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mGameObjectList.add(postSnapshot.getValue(genericTypeIndicator));
                }
                invalidate();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }
} // end class
  
