package com.studio.jarn.backfight.Gameboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import com.studio.jarn.backfight.Firebase.FirebaseGameViewListener;
import com.studio.jarn.backfight.Firebase.FirebaseHelper;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemFactory;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.Monster.MonsterFactory;
import com.studio.jarn.backfight.Player.Player;
import com.studio.jarn.backfight.Player.PlayerGameViewListener;
import com.studio.jarn.backfight.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static com.studio.jarn.backfight.MainMenuActivity.PHONE_UUID_SP;

/**
 * GameBoardView
 * inspiration for this class has been found here: http://www.wglxy.com/android-tutorials/android-zoomable-game-board
 */

public class GameView extends PanZoomView implements GameTouchListener, FirebaseGameViewListener, PlayerGameViewListener
{
    protected float mFocusX = 1000; //default value
    protected float mFocusY = 1000; //default value
    protected GameTouchListener mTouchListener;
    int mObjectMarginValue;
    int mObjectWidthValue;
    int mObjectHeightValue;
    List<SimpleCoordinates> mCoordinatesListTileShadowed = new ArrayList<>();
    List<SimpleCoordinates> mCoordinatesListTileVisible = new ArrayList<>();
    // Variables that control placement and translation of the canvas.
    // Initial values are for debugging on 480 mGameObject 320 screen. They are reset in onDrawPz.
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
    private ArrayList<Tuple<Player, Coordinates>> mGamePlayerList = new ArrayList<>();
    private ArrayList<Tuple<GameItem, Coordinates>> mGameItemList = new ArrayList<>();
    private ArrayList<Tuple<Monster, Coordinates>> mMonsterList = new ArrayList<>();
    private int mGridSize;
    private int mSquaresViewedAtStartup;
    private FirebaseHelper mFirebaseHelper;
    private Player mSelectedPlayer;
    private int mTileDivision = 4;
    private boolean mScalingValuesCalculated = false;
    private String mPlayerId;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTouchListener(this);
        mPlayerId = context.getSharedPreferences(
                getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE).getString(PHONE_UUID_SP, "");
    }

    /**
     * //https://stackoverflow.com/questions/12891520/how-to-programmatically-change-contrast-of-a-bitmap-in-android
     * @param bmp        input bitmap
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapBrightness(Bitmap bmp, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        1, 0, 0, 0, brightness,
                        0, 1, 0, 0, brightness,
                        0, 0, 1, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return bitmap;
    }

    public GameTouchListener getTouchListener() {
        return mTouchListener;
    }

    public void setTouchListener(GameTouchListener newListener) {
        mTouchListener = newListener;
    }

    public void initAddPlayers(List<Player> players) {
        int coordinatesCounter = 0;
        ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates = new ArrayList<>();
        outerLoop:
        for (int row = 0; row < mGridSize; row++) {
            for (int column = 0; column < mGridSize; column++) {
                if (mGrid[row][column].CanBePassed) {
                    for (Player player : players) {
                        playersWithCoordinates.add(new Tuple<>(player, new Coordinates(column, row, coordinatesCounter++, 0)));
                    }
                    mFirebaseHelper.setPlayerList(playersWithCoordinates);
                    break outerLoop;
                }
            }
        }
    }

    private void addItemListToDb(ArrayList<Tuple<GameItem, Coordinates>> itemsWithCoordinates) {
        mFirebaseHelper.setItemList(itemsWithCoordinates);
    }

    public void setGridSize(int newValue) {
        mGridSize = newValue;
    }

    public void setViewSizeAtStartup(int newValue) {
        mSquaresViewedAtStartup = newValue;
    }

    public void setupFirebase(String uuid) {
        mFirebaseHelper = new FirebaseHelper(this);
        mFirebaseHelper.setStandardKey(uuid, getContext());
    }

    //Todo: remove hardcoded Players
    public void drawOnCanvas(Canvas canvas) {

        Paint paint = new Paint();

        Bitmap bm_wall = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wall128);
        Bitmap bm_floor = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.floor128);
        Bitmap bm_shadow = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shadow128);
        Bitmap bm_shadowedWall = changeBitmapBrightness(bm_wall, -50);
        Bitmap bm_shadowedFloor = changeBitmapBrightness(bm_floor, -50);


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

                boolean tileVisible = new SimpleCoordinates(i, j).existInList(mCoordinatesListTileVisible);
                boolean tileShadowed = new SimpleCoordinates(i, j).existInList(mCoordinatesListTileShadowed);
                //Draw the map with the shadows depending on where the players are placed.
                if (tileVisible) {
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
                } else if (tileShadowed) {
                    switch (mGrid[j][i].Type) {
                        case Wall: {
                            canvas.drawBitmap(bm_shadowedWall, null, dest1, paint);
                            break;
                        }
                        case WoodenFloor: {
                            canvas.drawBitmap(bm_shadowedFloor, null, dest1, paint);
                            break;
                        }
                    }
                } else {
                    canvas.drawBitmap(bm_shadow, null, dest1, paint);
                }
                dx = dx + mSquareWidth;
            }
            dy = dy + mSquareHeight;
        }
        drawMapObjects(canvas);
    }

    private void drawMapObjects(Canvas canvas) {

        setMargins();

        for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
            if (mSelectedPlayer == null) {
                scaleBitmapAndAddToCanvas(canvas, tuple.mCoordinates, tuple.mGameObject.mFigure);
            } else {
                if (tuple.mGameObject.id.equals(mSelectedPlayer.id)) {
                    scaleBitmapAndAddToCanvas(canvas, tuple.mCoordinates, tuple.mGameObject.mFigureSelected);
                } else {
                    scaleBitmapAndAddToCanvas(canvas, tuple.mCoordinates, tuple.mGameObject.mFigure);
                }
            }
        }

        for (Tuple<Monster, Coordinates> tuple : mMonsterList) {
            if (new SimpleCoordinates(tuple.mCoordinates.tileX, tuple.mCoordinates.tileY).existInList(mCoordinatesListTileVisible))
                scaleBitmapAndAddToCanvas(canvas, tuple.mCoordinates, tuple.mGameObject.mFigure);
        }

        for (Tuple<GameItem, Coordinates> tuple : mGameItemList) {
            if (new SimpleCoordinates(tuple.mCoordinates.tileX, tuple.mCoordinates.tileY).existInList(mCoordinatesListTileVisible))
                scaleBitmapAndAddToCanvas(canvas, tuple.mCoordinates, tuple.mGameObject.Image);
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
        mFirebaseHelper.setMonsterListListener();
        mFirebaseHelper.getActionCount();
    }

    public void setItemListener() {
        mFirebaseHelper.setItemListListener();
    }

    @Override
    public void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playerList) {
        mGamePlayerList.clear();
        mGamePlayerList = playerList;

        //Setting places to show shadows

        mCoordinatesListTileVisible.clear();

        for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
            for (int i = -tuple.mGameObject.LineOfSight; i <= tuple.mGameObject.LineOfSight; i++) {
                for (int j = -tuple.mGameObject.LineOfSight; j <= tuple.mGameObject.LineOfSight; j++) {
                    addCoordinateToSimpleCoordinatesList(tuple.mCoordinates.tileX + i, tuple.mCoordinates.tileY + j);
                }
            }
        }

        invalidate();
    }


    void addCoordinateToSimpleCoordinatesList(int tileXCoordinate, int tileYCoordinate) {
        //Add coordinate if it does not exist in lists
        if (!new SimpleCoordinates(tileXCoordinate, tileYCoordinate).existInList(mCoordinatesListTileShadowed))
            mCoordinatesListTileShadowed.add(new SimpleCoordinates(tileXCoordinate, tileYCoordinate));

        if (!new SimpleCoordinates(tileXCoordinate, tileYCoordinate).existInList(mCoordinatesListTileVisible))
            mCoordinatesListTileVisible.add(new SimpleCoordinates(tileXCoordinate, tileYCoordinate));

    }

    @Override
    public void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monsterList) {
        mMonsterList.clear();
        mMonsterList = monsterList;
        invalidate();
    }

    @Override
    public void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemList) {
        mGameItemList.clear();
        mGameItemList = itemList;
        invalidate();
    }

    public void startMonsterTurn() {
        MonsterTurn();

        //TODO!!

        GameActivity gameActivity = (GameActivity) getContext();
        gameActivity.showMonsterDialog();

        gameActivity.sendNotificationNewRound();

    }

    @Override
    public void actionTaken() {
        //Check if all players have used their turns
        for (Tuple<Player, Coordinates> player : mGamePlayerList) {
            if (player.mGameObject.mActionsRemaining > 0) return;
        }
        nextRound();
    }

    private void nextRound() {
        startMonsterTurn();
        resetPlayerActions();
        mFirebaseHelper.increaseRoundCount();
    }

    private void resetPlayerActions() {
        for (Tuple<Player, Coordinates> player : mGamePlayerList) {
            player.mGameObject.resetActions();
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

        //Set variables to see what handle is done
        Boolean monsterHandled = false;
        Boolean playerHandled = HandlePlayerClicked(tileX, tileY, placementX, placementY);

        if (!playerHandled)
            monsterHandled = HandleMonsterClicked(tileX, tileY, placementX, placementY);

        if (!playerHandled && !monsterHandled)
            HandleItemClicked(tileX, tileY, placementX, placementY);

        mFirebaseHelper.setMonsterList(mMonsterList);
        mFirebaseHelper.setPlayerList(mGamePlayerList);
        mFirebaseHelper.setItemList(mGameItemList);

        //Render map
        invalidate();
    }


    private Boolean HandlePlayerClicked(int tileX, int tileY, int placementX, int placementY) {

        //Check every object on the map
        for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
            //Check if a Player is already selected
            if (mSelectedPlayer != null && tuple.mGameObject.id.equals(mSelectedPlayer.id)) {
                //Check if a player is clicked
                for (Tuple<Player, Coordinates> tuple1 : mGamePlayerList) {
                    if (tuple1.mCoordinates.tileX == tileX && tuple1.mCoordinates.tileY == tileY && tuple1.mCoordinates.placementOnTileX == placementX && tuple1.mCoordinates.placementOnTileY == placementY) {
                        mSelectedPlayer = tuple1.mGameObject;

                        //Same player: deselect
                        if (tuple.mGameObject.equals(tuple1.mGameObject)) {
                            mSelectedPlayer = null;
                        }

                        //Player related click happened
                        return true;
                    }
                }

                if (AttackMonster(tileX, tileY, placementX, placementY)) {
                    return true;
                } else if (PlayerPickUpItemClick(tileX, tileY, placementX, placementY)) {
                    return true;
                } else if (movePlayer(tuple, tileX, tileY))

                return true;
            }

            //Check if player is clicked and select it
            if (tuple.mCoordinates.tileX == tileX && tuple.mCoordinates.tileY == tileY && tuple.mCoordinates.placementOnTileX == placementX && tuple.mCoordinates.placementOnTileY == placementY) {
                if (tuple.mGameObject.id.equals(mPlayerId)) {
                    mSelectedPlayer = tuple.mGameObject;
                    //Player related click happened
                    return true;
                } else {
                    ((GameActivity) getContext()).hideItemListFragment(); // Hides any other fragment if visible
                    ((GameActivity) getContext()).showItemListFragment(tuple.mGameObject); // Show fragment for clicked player
                }
            }
        }

        //Nothing player related happened
        return false;
    }

    private Boolean HandleMonsterClicked(int tileX, int tileY, int placementX, int placementY) {
        //TODO
        return false;
    }

    private Boolean AttackMonster(int tileX, int tileY, int placementX, int placementY) {
        // Check if player is on the same tile
        Tuple<Player, Coordinates> localPlayerTuple = getPlayerTuple();
        if (localPlayerTuple.mCoordinates.tileX == tileX && localPlayerTuple.mCoordinates.tileY == tileY) {

            // Since we can not remove monstre, while we are in the for-loop, we have to make a
            // copy of it, in order to remove it safely afterwards.
            boolean monsterWasKilled = false;
            Tuple<Monster, Coordinates> killedMonster = null;

            // Player is on the same tile as the monster that was clicked
            // Find monster that was clicked
            for (Tuple<Monster, Coordinates> monsterTuple : mMonsterList) {
                if (monsterTuple.mCoordinates.tileX == tileX &&
                        monsterTuple.mCoordinates.tileY == tileY &&
                        monsterTuple.mCoordinates.placementOnTileX == placementX &&
                        monsterTuple.mCoordinates.placementOnTileY == placementY) {

                    // Player roll dmg
                    int dmg = localPlayerTuple.mGameObject.rollAttack();

                    // Damage monster
                    monsterTuple.mGameObject.mHitPoints -= dmg;
                    Log.d("AttackMonster", "Monster lost " + dmg + " HP");
                    if (monsterTuple.mGameObject.mHitPoints <= 0) {
                        // Monster died, remove it from map
                        Log.d("HandleMonsterClicked", "Monster died");
                        monsterWasKilled = true;
                        killedMonster = monsterTuple; // Copy by value, since it's java
                    }

                    // Player lose a turn
                    localPlayerTuple.mGameObject.takeAction(getContext(), this);
                }
            }

            if (monsterWasKilled) {
                mMonsterList.remove(killedMonster);
            }
        }

        return false;
    }

    private Boolean HandleItemClicked(int tileX, int tileY, int placementX, int placementY) {
        // TODO
        Log.d("Debug", "HandleItemClicked: called");
        return false;
    }

    private Boolean PlayerPickUpItemClick(int tileX, int tileY, int placementX, int placementY) {
        Log.d("Debug", "PlayerPickUpItemClick: called");
        for (Tuple<Player, Coordinates> playerTuple : mGamePlayerList) {
            if(playerTuple.mGameObject.id.equals(mPlayerId)) {
                // Check for items clicked
                Tuple<GameItem, Coordinates> mapItem = mapItemClicked(tileX, tileY, placementX, placementY);
                if(mapItem != null && playerTuple.mCoordinates.tileX == tileX && playerTuple.mCoordinates.tileY == tileY) {
                    if (playerTuple.mGameObject.canTakeAction()) {
                        pickUpItem(mapItem);
                        playerTuple.mGameObject.takeAction(getContext(), this);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Tuple<GameItem, Coordinates> mapItemClicked(int tileX, int tileY, int placementX, int placementY) {
        for (Tuple<GameItem, Coordinates> itemTuple : mGameItemList) {
            if (itemTuple.mCoordinates.tileX == tileX && itemTuple.mCoordinates.tileY == tileY && itemTuple.mCoordinates.placementOnTileX == placementX && itemTuple.mCoordinates.placementOnTileY == placementY) {
                return itemTuple;
            }
        }
        return null;
    }

    private void pickUpItem(Tuple<GameItem, Coordinates> mapItem) {
        Log.d("ItemClicked", "onTouchUp: Item on map was clicked. " + mapItem.mGameObject.Title);

        // Get item
        GameItem itemPickedUp = mapItem.mGameObject;

        // Remove item from map
        mGameItemList.remove(mapItem);

        // Add item to players itemlist
        addItemToPlayer(itemPickedUp, getPlayerName());
    }

    private void addItemToPlayer(GameItem item, String playerName) {
        for (Tuple<Player, Coordinates> playerTuple : mGamePlayerList) {
            if (playerTuple.mGameObject.mName.equals(playerName)) {
                Log.d("addItemToPlayer", "addItemToPlayer: " + playerTuple.mGameObject.mName + " = " + playerName);
                // Check if list is null due to the way firebase handles empty lists.
                if (playerTuple.mGameObject.PlayerItems == null) {
                    playerTuple.mGameObject.PlayerItems = new ArrayList<>();
                    playerTuple.mGameObject.PlayerItems.add(item);
                } else {
                    Log.d("testing", "addItemToPlayer: " + playerTuple.mGameObject.mName);
                    playerTuple.mGameObject.PlayerItems.add(item);
                }
            }
        }
    }

    private boolean movePlayer(Tuple<Player, Coordinates> tuple, int tileX, int tileY) {
        //Not your own player, and should not be moved
        if (!tuple.mGameObject.id.equals(mPlayerId)) return false;
        //Moving to the same tile
        if (tuple.mCoordinates.tileX == tileX && tuple.mCoordinates.tileY == tileY) return false;
        //Click is on an non-passable tile do nothing
        if (!mGrid[tileY][tileX].CanBePassed) return false;

        if (!MovementIsMoreThanOneTile(tuple.mCoordinates, tileX, tileY)) {
            Coordinates movedTo = moveToTile(tileX, tileY);
            if (movedTo != null && tuple.mGameObject.canTakeAction()) {
                tuple.mCoordinates = movedTo;
                mSelectedPlayer = null;
                tuple.mGameObject.takeAction(getContext(), this);
                return true;
            }
        }

        return false;
    }

    private Boolean MovementIsMoreThanOneTile(Coordinates currentPlayerPlacement, int tileX, int tileY) {
        if (!((currentPlayerPlacement.tileX + 1) == tileX && currentPlayerPlacement.tileY == tileY))
            if (!((currentPlayerPlacement.tileX) == tileX && (currentPlayerPlacement.tileY + 1) == tileY))
                if (!((currentPlayerPlacement.tileX) == tileX && (currentPlayerPlacement.tileY - 1) == tileY))
                    if (!((currentPlayerPlacement.tileX - 1) == tileX && (currentPlayerPlacement.tileY) == tileY))
                        return true;
        return false;

    }

    public void MonsterTurn() {
        //Each monster take turn
        for (Tuple<Monster, Coordinates> monster : mMonsterList) {
            monster.mGameObject.resetActions();
            //as long the monster can take turn
            while (monster.mGameObject.canTakeAction()) {
                boolean attack = false;
                //check if same spot as player
                for (Tuple<Player, Coordinates> player : mGamePlayerList) {
                    if (monster.mCoordinates.tileY == player.mCoordinates.tileY && monster.mCoordinates.tileX == player.mCoordinates.tileX) {
                        attackPlayer(player, monster);
                        attack = true;
                        break;
                    }
                }
                // If not same space as player move
                if (!attack) {
                    while (!moveSingleMonster(monster)) ;
                }

                // TODO: Would make sense that monster also attacked if it landed on a tile with a player
                // ... else the mechanics would be waaaaay to easy. (Move -> Attack -> Move = Safe)
            }
        }

        mFirebaseHelper.setMonsterList(mMonsterList);

        //Render map
        invalidate();
    }

    private void attackPlayer(Tuple<Player, Coordinates> player, Tuple<Monster, Coordinates> monster) {
        //Get ref to player in list
        for (Tuple<Player, Coordinates> playerTuple : mGamePlayerList) {
            if (playerTuple.equals(player)) {

                // Damage player
                player.mGameObject.Health -= monster.mGameObject.mAttackPower;
                Log.d("Info", "attackPlayer: Player lost " + monster.mGameObject.mAttackPower + " HP");
                if (player.mGameObject.Health <= 0) {
                    // Player died - remove him from map
                    mGamePlayerList.remove(playerTuple);
                    Log.d("Info", "attackPlayer: Player died");
                }
            }
        }

        monster.mGameObject.takeAction();
    }

    private boolean moveSingleMonster(Tuple<Monster, Coordinates> tuple) {
        Random random = new Random();
        int move;
        Coordinates movedTo = null;
        //50 % chance for moved 1 space forward or backwards
        if (random.nextBoolean())
            move = 1;
        else
            move = -1;

        // 50 % chance to do it at x-axis or y-axis
        if (random.nextBoolean()) {
            if ((tuple.mCoordinates.tileX + move) < (mMaxCanvasWidth / mSquareWidth) && (tuple.mCoordinates.tileX + move) > 0)
                movedTo = moveToTile(tuple.mCoordinates.tileX + move, tuple.mCoordinates.tileY);
        } else {
            if ((tuple.mCoordinates.tileY + move) < (mMaxCanvasHeight / mSquareHeight) && (tuple.mCoordinates.tileY + move) > 0)
                movedTo = moveToTile(tuple.mCoordinates.tileX, tuple.mCoordinates.tileY + move);
        }
        // if check if legal move
        if (movedTo != null && tuple.mGameObject.canTakeAction()) {
            tuple.mCoordinates = movedTo;
            tuple.mGameObject.takeAction();
            return true;
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

    // Get coordinate to Random tile, at least one tile away from a player.
    private Coordinates moveToRandomTile() {
        for (int i = 0; i < 15; i++) {
            Coordinates coordinates = Coordinates.getRandom(mGridSize);
            if ((coordinates = moveToTile(coordinates.tileX, coordinates.tileY)) != null) {
                int awayFromPlayer = 0;
                for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
                    if (coordinates.tileX > (tuple.mCoordinates.tileX) || (tuple.mCoordinates.tileX) > (coordinates.tileX)) {
                        if (coordinates.tileY > (tuple.mCoordinates.tileY) || coordinates.tileY < (tuple.mCoordinates.tileY)) {
                            awayFromPlayer++;
                        }
                    }
                }

                if (awayFromPlayer == mGamePlayerList.size())
                    return coordinates;
            }
        }

        return null;
    }

    private Coordinates moveToTile(int tileX, int tileY) {
        if (!mGrid[tileY][tileX].CanBePassed) return null;
        ArrayList<Tuple<Player, Coordinates>> onTile = new ArrayList<>();
        ArrayList<Tuple<Monster, Coordinates>> monsterOnTile = new ArrayList<>();

        for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
            if (tuple.mCoordinates.tileX == tileX && tuple.mCoordinates.tileY == tileY)
                onTile.add(tuple);
        }

        for (Tuple<Monster, Coordinates> tuple : mMonsterList) {
            if (tuple.mCoordinates.tileX == tileX && tuple.mCoordinates.tileY == tileY)
                monsterOnTile.add(tuple);
        }

        if (onTile.size() + monsterOnTile.size() == (mTileDivision * mTileDivision)) return null;

        for (int y = 0; y < mTileDivision; y++) {
            for (int x = 0; x < mTileDivision; x++) {
                Boolean placementFree = true;
                for (Tuple<Player, Coordinates> tuple : onTile) {
                    if (tuple.mCoordinates.placementOnTileX == x && tuple.mCoordinates.placementOnTileY == y)
                        placementFree = false;
                }
                for (Tuple<Monster, Coordinates> tuple : monsterOnTile) {
                    if (tuple.mCoordinates.placementOnTileX == x && tuple.mCoordinates.placementOnTileY == y)
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

        addItemListToDb(mGameItemList);
        invalidate();
    }

    //Type is a integer for type of monster spawned, 0 is normal, 1 is Elite, 2 is boss
    public void spawnMonster(int type) {
        MonsterFactory fac = new MonsterFactory(getContext());
        int numberOfPlayers = mGamePlayerList.size();
        //find real rounds
        int rounds = mFirebaseHelper.mRound;


        Coordinates coordinates = moveToRandomTile();
        //check if moveToRandomTile have found a tile, if not just find a random coordinate
        if (coordinates == null) {
            while (tileIsPassable((coordinates = Coordinates.getRandom(mGridSize)))) {
            }

        }

        switch (type) {
            // Normal monster
            case 0:
                spawnMonsterOnTile(fac.getRandomNormalMonster(numberOfPlayers, rounds), coordinates);
                break;
            // Epic monster
            case 1:
                spawnMonsterOnTile(fac.getRandomEliteMonster(numberOfPlayers, rounds), coordinates);
                break;
            // Boss monster
            case 2:
                spawnMonsterOnTile(fac.getRandomBossMonster(numberOfPlayers, rounds), coordinates);
                break;
        }

        invalidate();
    }

    // method who spawns monster at start
    public void spawnStartMonsters(int numberOfMonsters) {
        for (int c = 0; c < numberOfMonsters - 1; c++) {
            spawnMonster(0);
        }
        spawnMonster(1);

        mFirebaseHelper.setMonsterList(mMonsterList);
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

    public Tuple<Player, Coordinates> getPlayerTuple() {
        for (Tuple<Player, Coordinates> tuple : mGamePlayerList) {
            if (tuple.mGameObject.id.equals(mPlayerId)) {
                return tuple;
            }
        }

        Log.d("getPlayerCoordinates", "Could not find local player.");

        return null;
    }

    public String getPlayerName() {
        for (Tuple<Player, Coordinates> playerTuple : mGamePlayerList) {
            if (playerTuple.mGameObject.id.equals(mPlayerId)) {
                return playerTuple.mGameObject.mName;
            }
        }

        return ""; //TODO: Maybe throw an exception instead?.
    }

    public Player getLocalPlayer() {
        for (Tuple<Player, Coordinates> playerTuple : mGamePlayerList) {
            if (playerTuple.mGameObject.id.equals(mPlayerId)){
                return playerTuple.mGameObject;
            }
        }

        return null;
    }
} // end class
  
