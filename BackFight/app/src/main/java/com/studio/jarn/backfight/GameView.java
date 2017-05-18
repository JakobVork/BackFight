package com.studio.jarn.backfight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemFactory;
import com.studio.jarn.backfight.monster.Monster;
import com.studio.jarn.backfight.monster.MonsterFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


/**
 * GameBoardView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

public class GameView extends PanZoomView implements GameTouchListener, FirebaseGameViewListener, PlayerGameViewListener
{
    protected float mFocusX = 1000; //default value
    protected float mFocusY = 1000; //default value
    protected GameTouchListener mTouchListener;
    boolean onlyOnce = true;
    int mObjectMarginValue;
    int mObjectWidthValue;
    int mObjectHeightValue;
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
    private ArrayList<Tuple<GameItem, Coordinates>> mGameItemList = new ArrayList<>();
    private ArrayList<Tuple<Monster, Coordinates>> mMonsterList = new ArrayList<>();
    private int mGridSize;
    private int mSquaresViewedAtStartup;
    private FirebaseHelper mFirebaseHelper;
    private Player mSelectedObject;
    private int mTileDivision = 4;
    private boolean mScalingValuesCalculated = false;

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
        for (int row = 0; row < mGridSize; row++) {
            for (int column = 0; column < mGridSize; column++) {
                if (mGrid[row][column].CanBePassed) {
                    for (Player player : players) {
                        mGameObjectList.add(new Tuple<>(player, new Coordinates(column, row, coordinatesCounter++, 0)));
                    }
                    mFirebaseHelper.setPlayerList(mGameObjectList);
                    break outerLoop;
                }
            }
        }
    }

    public void setGridSize(int newValue) {
        mGridSize = newValue;
    }

    public void setViewSizeAtStartup(int newValue) {
        mSquaresViewedAtStartup = newValue;
    }

    public void setupFirebase(String uuid) {
        mFirebaseHelper = new FirebaseHelper(this);
        mFirebaseHelper.setStandardKey(uuid);
    }

    //Todo: remove hardcoded Players
    public void drawOnCanvas(Canvas canvas) {

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
        for (int j = 0; j < mGridSize; j++) {
            dx = 0;
            for (int i = 0; i < mGridSize; i++) {
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

        setMargins();

        for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
            if (mSelectedObject == null) {
                scaleBitmapAndAddToCanvas(canvas, tuple.y, tuple.x.mFigure);
            } else {
                if (tuple.x.id.equals(mSelectedObject.id)) {
                    scaleBitmapAndAddToCanvas(canvas, tuple.y, tuple.x.mFigureSelected);
                } else {
                    scaleBitmapAndAddToCanvas(canvas, tuple.y, tuple.x.mFigure);
                }
            }
        }

        for (Tuple<Monster, Coordinates> tuple : mMonsterList) {
            scaleBitmapAndAddToCanvas(canvas, tuple.y, tuple.x.getFigure());
        }

        for (Tuple<GameItem, Coordinates> tuple : mGameItemList) {
            scaleBitmapAndAddToCanvas(canvas, tuple.y, tuple.x.Image);
        }
    }

    private void setMargins() {
        if (!mScalingValuesCalculated) {
            mObjectMarginValue = Double.valueOf(mSquareWidth / mTileDivision * 0.05).intValue(); //a 5% margin to the side of the tile.
            mObjectWidthValue = Double.valueOf(mSquareWidth / mTileDivision * 0.90).intValue(); //Scale image width to 90% size to make room for the 5% margin on both sides.
            mObjectHeightValue = Double.valueOf(mSquareHeight / mTileDivision * 0.90).intValue();//Scale image height to 90% size to make room for the 5% margin on both sides.
            mScalingValuesCalculated = true;
        }
    }

    void scaleBitmapAndAddToCanvas(Canvas canvas, Coordinates y, int mFigure) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mFigure);
        Bitmap bitmapScaled = Bitmap.createScaledBitmap(bitmap, mObjectWidthValue, mObjectHeightValue, true);
        canvas.drawBitmap(bitmapScaled, getXCoordFromObjectPlacement(y) + mObjectMarginValue, getYCoordFromObjectPlacement(y) + mObjectMarginValue, null);
    }

    /**
     * onDrawPz
     * This method is copied from here http://www.wglxy.com/android-tutorials/android-zoomable-game-board
     */

    @Override
    public void onDrawPz(Canvas canvas) {

        canvas.save();

        // Get the width and height of the view.
        int viewH = getHeight(), viewW = getWidth();

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
    mMaxCanvasWidth  = mGridSize * mSquareWidth;
    mMaxCanvasHeight = mGridSize * mSquareHeight;
    mHalfMaxCanvasWidth  = mMaxCanvasWidth / 2.0f;
    mHalfMaxCanvasHeight = mMaxCanvasHeight / 2.0f;

        float totalOffscreenSquaresX = mGridSize - numSquaresAlongX;
        if (totalOffscreenSquaresX < 0f) totalOffscreenSquaresX = 0f;
        float totalOffscreenSquaresY = mGridSize - numSquaresAlongY;
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
        canvas.translate(x, y);

        // The focus point for zooming is the center of the
        // displayable region. That point is defined by half
        // the canvas width and height.

        mFocusX = mHalfMaxCanvasWidth;
        mFocusY = mHalfMaxCanvasHeight;

        //TODO This has been commented out to move the focus to the top left corner, this is a temp hotfix for a better zoom experience.
        //The out commented code is there for the person who needs to fix to know what was there before
        canvas.scale(mScaleFactor, mScaleFactor, mFocusX, mFocusY);

        // Set up the grid  and grid selection variables.
        if (mGrid == null)
            mGrid = new Tile[mGridSize][mGridSize];

        // Set up the rectangles we use for drawing, if not done already.
        // Set width and height to be used for the rectangle to be drawn.
        Rect dest = mDestRect;
        if (dest == null) {
            int ih = (int) Math.floor(mSquareHeight);
            int iw = (int) Math.floor(mSquareWidth);
            dest = new Rect(0, 0, iw, ih);
            mDestRect = dest;
        }
        RectF dest1 = mDestRectF;
        if (dest1 == null) {
            dest1 = new RectF();
            dest1.left = dest.left;
            dest1.top = dest.top;
            dest1.right = mSquareWidth;
            dest1.bottom = mSquareHeight;
            mDestRectF = dest1;
        }

        // Do the drawing operation for the view.
        drawOnCanvas(canvas);

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

        mFirebaseHelper.setupGridListener();
        mFirebaseHelper.increaseRoundCount();
        updateGrid(grid);
    }

    public void initClientGrid() {
        mFirebaseHelper.setupGridListener();
    }

    @Override
    public void setGrid(int sizeOfArrayOnFirebase, Tile[][] grid) {
        if (mGridSize != sizeOfArrayOnFirebase) {
            mGridSize = sizeOfArrayOnFirebase;
            mGrid = new Tile[mGridSize][mGridSize];
        }
        mGrid = grid;
        invalidate();
    }

    public void setListeners() {
        mFirebaseHelper.setPlayerListListener();
        mFirebaseHelper.setRoundCountListener(getContext());
    }

    @Override
    public void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList) {
        mGameObjectList.clear();
        mGameObjectList = playerList;
        invalidate();
    }

    public void startMonsterTurn() {
        MonsterTurn();
        ((GameActivity) getContext()).showMonsterDialog();
    }

    @Override
    public void actionTaken() {
        //Check if all players have used their turns
        for (Tuple<Player, Coordinates> player : mGameObjectList) {
            if (player.x.mActionsRemaining > 0) return;
        }
        nextRound();
    }

    private void nextRound() {
        startMonsterTurn();
        resetPlayerActions();
        mFirebaseHelper.increaseRoundCount();
    }

    private void resetPlayerActions() {
        for (Tuple<Player, Coordinates> player : mGameObjectList) {
            player.x.resetActions();
        }
    }

    public void updateGrid(Tile grid[][]) {

        List<List<Tile>> list = new ArrayList<>();
        for (Tile[] aMGrid : grid) {
            list.add(Arrays.asList(aMGrid));
        }
        mFirebaseHelper.setGrid(list);
    }

    public void onTouchDown(float downX, float downY) {
        mFocusX = downX;
        mFocusY = downY;
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
            if (mSelectedObject != null) {
                if (tuple.x.id.equals(mSelectedObject.id)) {
                    for (Tuple<Player, Coordinates> tuple1 : mGameObjectList) {
                        if (tuple1.y.tileX == tileX && tuple1.y.tileY == tileY && tuple1.y.placementOnTileX == placementX && tuple1.y.placementOnTileY == placementY) {
                            mSelectedObject = tuple1.x;

                            if (tuple.x.equals(tuple1.x)) {
                                mSelectedObject = null;
                            }

                            invalidate();
                            return;
                        }
                    }
                    movePlayer(tuple, tileX, tileY);
                }
            }
            //Select or DeSelect object
            if (tuple.y.tileX == tileX && tuple.y.tileY == tileY && tuple.y.placementOnTileX == placementX && tuple.y.placementOnTileY == placementY) {
                mSelectedObject = tuple.x;
            }
        }
        mFirebaseHelper.setPlayerList(mGameObjectList);

        //Render map
        invalidate();
    }

    private void movePlayer(Tuple<Player, Coordinates> tuple, int tileX, int tileY) {
        Coordinates movedTo = moveToTile(tileX, tileY);
        if (movedTo != null && tuple.x.canTakeAction()) {
            tuple.y = movedTo;
            mSelectedObject = null;
            tuple.x.takeAction(getContext(), this);
        }
    }

    public void MonsterTurn() {
        for (Tuple<Monster, Coordinates> monster : mMonsterList) {
            monster.x.resetActions();
            while (monster.x.canTakeAction()) {
                boolean attacked = false;

                for (Tuple<Player, Coordinates> player : mGameObjectList) {
                    if (monster.y.tileY == player.y.tileY && monster.y.tileX == player.y.tileX) {
                        attackPlayer(player, monster);
                        attacked = true;
                        break;
                    }
                }
                if (!attacked) {
                    while (!moveSingleMonster(monster)) ;
                }
            }
        }
    }

    private void attackPlayer(Tuple<Player, Coordinates> player, Tuple<Monster, Coordinates> monster) {
        //Monster attacks

        monster.x.takeAction();
    }

    private boolean moveSingleMonster(Tuple<Monster, Coordinates> tuple) {
        Random random = new Random();
        int move;
        Coordinates movedTo = new Coordinates(-1, 0, 0, 0);
        if (random.nextBoolean())
            move = 1;
        else
            move = -1;

        if (random.nextBoolean()) {
            if ((tuple.y.tileX + move) < (mMaxCanvasWidth / mSquareWidth) && (tuple.y.tileX + move) > 0)
                movedTo = moveToTile(tuple.y.tileX + move, tuple.y.tileY);
        } else {
            if ((tuple.y.tileY + move) < (mMaxCanvasHeight / mSquareHeight) && (tuple.y.tileY + move) > 0)
                movedTo = moveToTile(tuple.y.tileX, tuple.y.tileY + move);
        }

        if (movedTo != null && tuple.x.canTakeAction()) {
            if (movedTo.tileX != -1) {
                tuple.y = movedTo;
                tuple.x.takeAction();
                return true;
            }
        }

        return false;
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

    private Coordinates moveToRandomTile() {
        while (true) {
            Coordinates coordinates = Coordinates.getRandom(mGridSize);
            if ((coordinates = moveToTile(coordinates.tileX, coordinates.tileY)) != null) {
                int awayFromPlayer = 0;
                for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
                    if (coordinates.tileX > (tuple.y.tileX) || (tuple.y.tileX) > (coordinates.tileX)) {
                        if (coordinates.tileY > (tuple.y.tileY) || coordinates.tileY < (tuple.y.tileY)) {
                            awayFromPlayer++;
                        }
                    }
                }

                if (awayFromPlayer == mGameObjectList.size())
                    return coordinates;
            }
        }
    }

    private Coordinates moveToTile(int tileX, int tileY) {
        if (!mGrid[tileY][tileX].CanBePassed) return null;
        ArrayList<Tuple<Player, Coordinates>> onTile = new ArrayList<>();
        ArrayList<Tuple<Monster, Coordinates>> monsterOnTile = new ArrayList<>();

        for (Tuple<Player, Coordinates> tuple : mGameObjectList) {
            if (tuple.y.tileX == tileX && tuple.y.tileY == tileY) onTile.add(tuple);
        }

        for (Tuple<Monster, Coordinates> tuple : mMonsterList) {
            if (tuple.y.tileX == tileX && tuple.y.tileY == tileY) monsterOnTile.add(tuple);
        }

        if (onTile.size() + monsterOnTile.size() == (mTileDivision * mTileDivision)) return null;

        for (int y = 0; y < mTileDivision; y++) {
            for (int x = 0; x < mTileDivision; x++) {
                Boolean placementFree = true;
                for (Tuple<Player, Coordinates> tuple : onTile) {
                    if (tuple.y.placementOnTileX == x && tuple.y.placementOnTileY == y)
                        placementFree = false;
                }
                for (Tuple<Monster, Coordinates> tuple : monsterOnTile) {
                    if (tuple.y.placementOnTileX == x && tuple.y.placementOnTileY == y)
                        placementFree = false;
                }
                if (placementFree) return new Coordinates(tileX, tileY, x, y);
            }
        }

        return null;
    }

    public void spawnItems(int numberOfItems) {
        ItemFactory fac = new ItemFactory(getContext());
        int i = 0;

        // Continue untill all items are spawned
        while (i < numberOfItems) {

            Coordinates coordinates = Coordinates.getRandom(mGridSize);
            // Check if tile is passable, else just roll again.
            if (tileIsPassable(coordinates)) {
                spawnItemOnTile(fac.Weapons.getRandomWeapon(), coordinates);
                i++;
            }
        }

        invalidate();
    }

    //Type is a integer for type of monster spawned, 0 is normal, 1 is Elite, 2 is boss
    public void spawnMonster(int type) {
        MonsterFactory fac = new MonsterFactory(getContext());
        int numberOfPlayers = mGameObjectList.size();
        //find real rounds
        int rounds = 1;

        switch (type) {
            case 0:
                spawnMonsterOnTile(fac.getRandomNormalMonster(numberOfPlayers, rounds), moveToRandomTile());
                break;
            case 1:
                spawnMonsterOnTile(fac.getRandomEliteMonster(numberOfPlayers, rounds), moveToRandomTile());
                break;
            case 2:
                spawnMonsterOnTile(fac.getRandomBossMonster(numberOfPlayers, rounds), moveToRandomTile());
                break;
        }

        invalidate();
    }

    public void spawnStartMonsters(int numberOfMonsters) {
        for (int c = 0; c < numberOfMonsters - 1; c++) {
            spawnMonster(0);
        }
        spawnMonster(1);
    }

    public void spawnItemOnTile(GameItem item, Coordinates coordinates) {
        mGameItemList.add(new Tuple<>(item, coordinates));
    }

    public void spawnMonsterOnTile(Monster monster, Coordinates coordinates) {
        mMonsterList.add(new Tuple<>(monster, coordinates));
    }

    private boolean tileIsPassable(Coordinates coordinates) {
        return mGrid[coordinates.tileY][coordinates.tileX].CanBePassed;
    }

} // end class
  
