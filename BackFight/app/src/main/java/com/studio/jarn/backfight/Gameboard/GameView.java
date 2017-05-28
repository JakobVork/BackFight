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
    private List<Player> mGamePlayerList = new ArrayList<>();
    private List<GameItem> mGameItemList = new ArrayList<>();
    private List<Monster> mMonsterList = new ArrayList<>();
    //private ArrayList<Tuple<Player, Coordinates>> mGamePlayerList = new ArrayList<>();
    //private ArrayList<Tuple<GameItem, Coordinates>> mGameItemList = new ArrayList<>();
    //private ArrayList<Tuple<Monster, Coordinates>> mMonsterList = new ArrayList<>();
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

    /*
     * https://stackoverflow.com/questions/12891520/how-to-programmatically-change-contrast-of-a-bitmap-in-android
     * @param bmp input bitmap
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

    // Give each player a coordinate and push to firebase.
    public void initAddPlayers(List<Player> players) {
        int coordinatesCounter = 0;
        outerLoop:
        for (int row = 0; row < mGridSize; row++) {
            for (int column = 0; column < mGridSize; column++) {
                if (mGrid[row][column].CanBePassed) {
                    for (Player player : players) {
                        player.Coordinate = new Coordinates(column, row, coordinatesCounter++, 0);
                    }
                    mFirebaseHelper.setPlayerList(players);
                    break outerLoop;
                }
            }
        }
    }

    private void addItemListToDb(List<GameItem> itemsWithCoordinates) {
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

        for (Player player : mGamePlayerList) {
            if (mSelectedPlayer == null) {
                scaleBitmapAndAddToCanvas(canvas, player.Coordinate, player.Figure);
            } else {
                if (player.Id.equals(mSelectedPlayer.Id)) {
                    scaleBitmapAndAddToCanvas(canvas, player.Coordinate, player.FigureSelected);
                } else {
                    scaleBitmapAndAddToCanvas(canvas, player.Coordinate, player.Figure);
                }
            }
        }

        for (Monster monster : mMonsterList) {
            if (new SimpleCoordinates(monster.coordinate.tileX, monster.coordinate.tileY).existInList(mCoordinatesListTileVisible))
                scaleBitmapAndAddToCanvas(canvas, monster.coordinate, monster.Figure);
        }

        for (GameItem item : mGameItemList) {
            if (new SimpleCoordinates(item.Coordinate.tileX, item.Coordinate.tileY).existInList(mCoordinatesListTileVisible))
                scaleBitmapAndAddToCanvas(canvas, item.Coordinate, item.Image);
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

        LimitCameraDistanceToBoard();

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

    private void LimitCameraDistanceToBoard(){
        int margin = 600;

        if (mPosX > (-(mMaxCanvasWidth / 2) + margin + (mMaxCanvasWidth / 2) * mScaleFactor)) mPosX = -(mMaxCanvasWidth / 2) + margin + (mMaxCanvasWidth / 2) * mScaleFactor;
        if (mPosY > (-(mMaxCanvasHeight / 2) + margin + (mMaxCanvasHeight / 2) * mScaleFactor)) mPosY = -(mMaxCanvasHeight / 2) + margin + (mMaxCanvasHeight / 2) * mScaleFactor;
        if (mPosX < (((mMaxCanvasWidth / 2) + margin - (mMaxCanvasWidth / 2) * mScaleFactor) - mMaxCanvasWidth)) mPosX = ((mMaxCanvasWidth / 2) + margin - (mMaxCanvasWidth / 2) * mScaleFactor) - mMaxCanvasWidth;
        if (mPosY < (((mMaxCanvasHeight / 2) + margin - (mMaxCanvasHeight / 2) * mScaleFactor) - mMaxCanvasHeight)) mPosY = ((mMaxCanvasHeight / 2) + margin - (mMaxCanvasHeight / 2) * mScaleFactor) - mMaxCanvasHeight;
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
    public void setPlayerList(List<Player> playerList) {
        mGamePlayerList.clear();
        mGamePlayerList = playerList;

        mCoordinatesListTileVisible.clear();

        for (Player player : mGamePlayerList) {
            for (int i = -player.LineOfSight; i <= player.LineOfSight; i++) {
                for (int j = -player.LineOfSight; j <= player.LineOfSight; j++) {
                    addCoordinateToSimpleCoordinatesList(player.Coordinate.tileX + i, player.Coordinate.tileY + j);
                }
            }
        }

        invalidate();
    }


    void addCoordinateToSimpleCoordinatesList(int tileXCoordinate, int tileYCoordinate) {
        // Add coordinate if it does not exist in lists
        //Add coordinate if it does not exist in lists
        if (mCoordinatesListTileShadowed == null)
            mCoordinatesListTileShadowed = new ArrayList<>();

        if (!new SimpleCoordinates(tileXCoordinate, tileYCoordinate).existInList(mCoordinatesListTileShadowed))
            mCoordinatesListTileShadowed.add(new SimpleCoordinates(tileXCoordinate, tileYCoordinate));

        if (!new SimpleCoordinates(tileXCoordinate, tileYCoordinate).existInList(mCoordinatesListTileVisible))
            mCoordinatesListTileVisible.add(new SimpleCoordinates(tileXCoordinate, tileYCoordinate));

        mFirebaseHelper.setTileShadowedList(mCoordinatesListTileShadowed);
    }

    @Override
    public void setMonsterList(List<Monster> monsterList) {
        mMonsterList.clear();
        mMonsterList = monsterList;
        invalidate();
    }

    @Override
    public void setItemList(List<GameItem> itemList) {
        mGameItemList.clear();
        mGameItemList = itemList;
        invalidate();
    }

    public void getTileShadowedList() {
        mFirebaseHelper.getTileShadowedList();
    }

    @Override
    public void setTileShadowedList(List<SimpleCoordinates> simpleCoordinatesList) {
        mCoordinatesListTileShadowed = simpleCoordinatesList;
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
        for (Player player : mGamePlayerList) {
            if (player.ActionsRemaining > 0) return;
        }
        nextRound();
    }

    private void nextRound() {
        startMonsterTurn();
        resetPlayerActions();
        mFirebaseHelper.increaseRoundCount();
    }

    private void resetPlayerActions() {
        for (Player player : mGamePlayerList) {
            player.resetActions();
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
    public void onLongTouchUp(int downX, int downY, int upX, int upY) {

    }

    @Override
    public void onTouchUp(int tileX, int tileY, int placementX, int placementY) {

        //Click is outside map: do nothing
        if (placementX < 0 || placementY < 0 || tileX >= (mMaxCanvasWidth / mSquareWidth) || tileY >= (mMaxCanvasHeight / mSquareHeight))
            return;

        //Convert seperated coords to a coordinate
        Coordinates coordinate = new Coordinates(tileX, tileY, placementX, placementY);

        // Get player to check for state
        Player localPlayer = getLocalPlayer();

        if (mSelectedPlayer != null && mSelectedPlayer.equals(localPlayer)) {
            // Player is selected
            HandleActionForSelectedPlayer(coordinate);
        } else {
            // Player is not selected
            HandleActionForNonSelectedPlayer(coordinate);
        }

        mFirebaseHelper.setMonsterList(mMonsterList);
        mFirebaseHelper.setPlayerList(mGamePlayerList);
        mFirebaseHelper.setItemList(mGameItemList);

        //Render map
        invalidate();
    }


    // Only called when local player is selected first
    private void HandleActionForSelectedPlayer(Coordinates coordinate) {
        // Check what the player clicked on and do action accordenly
        if (clickedOnLocalPlayer(coordinate)) {
            // Clicked on local player - Deselect
            mSelectedPlayer = null;
        } else if (clickedOnItem(coordinate)) {
            // Cliked on item - Pick it up
            pickUpItem(getLocalPlayer(), coordinate);
        } else if (clickedOnMonster(coordinate)) {
            // Clicked on Monster - Attack it
            AttackMonster(getLocalPlayer(), getMonsterOnCoord(coordinate));
        } else if (tileNextToPlayer(getLocalPlayer(), coordinate.tileX, coordinate.tileY, 1)){ // 1 = distance
            // Ensure player has actions left
            if (!getLocalPlayer().canTakeAction())
                return;

            // Check that player isn't on the tile already
            if (getLocalPlayer().Coordinate.tileX == coordinate.tileX && getLocalPlayer().Coordinate.tileY == coordinate.tileY)
                return;

            movePlayerToTile(getLocalPlayer(), coordinate.tileX, coordinate.tileY);
            getLocalPlayer().takeAction(getContext(), this);
        }

        // Player is now deselected, therefore also don't show the "select" image
        mSelectedPlayer = null;

        // TODO: Do something, if another player is clicked?
    }

    // Only called whe local player is not selected
    private void HandleActionForNonSelectedPlayer(Coordinates coordinate) {
        // Check what the player clicked on and do action accordenly
        if (clickedOnLocalPlayer(coordinate)) {
            // Clicked on local player - Select
            mSelectedPlayer = getLocalPlayer();
        } else if (clickedOnItem(coordinate)) {
            // TODO: Show item stats on a fragment
        } else if (clickedOnMonster(coordinate)) {
            Monster monsterToShow = getMonsterOnCoord(coordinate);
            ((GameActivity) getContext()).hideFragment(); // Hides any other fragment if visible
            ((GameActivity) getContext()).showOverviewFragment(monsterToShow); // Show fragment for clicked monster
        } else if (clickedOnOtherPlayer(coordinate)) {
            // Show stats and item fragment for the clicked player
            Player playerToShow = getPlayerOnCoord(coordinate);
            ((GameActivity) getContext()).hideFragment(); // Hides any other fragment if visible
            ((GameActivity) getContext()).showOverviewFragment(playerToShow); // Show fragment for clicked player
        }
    }


    private boolean clickedOnLocalPlayer(Coordinates coord) {
        for (Player player : mGamePlayerList) {
            if (player.Id.equals(mPlayerId) && player.Coordinate != null && player.Coordinate.equals(coord))
                return true;
        }

        return false;
    }

    private boolean clickedOnItem(Coordinates coord) {
        for (GameItem item : mGameItemList) {
            if (item.Coordinate != null && item.Coordinate.equals(coord))
                return true;
        }

        return false;
    }

    private boolean clickedOnMonster(Coordinates coord) {
        for (Monster monster : mMonsterList) {
            if (monster.coordinate != null && monster.coordinate.equals(coord))
                return true;
        }

        return false;
    }

    private void pickUpItem(Player player, Coordinates coord) {
        Log.d("ItemClicked", "pickUpItem: Item on map was clicked.");

        // Ensure player is on the same file as item
        if (!(player.Coordinate.tileX == coord.tileX && player.Coordinate.tileY == coord.tileY))
            return;

        // get item that was clicked on
        GameItem item = getItemOnCoord(coord);
        if (item == null)
            return;

        // Remove item from map
        mGameItemList.remove(item);
        item.Coordinate = null; // It does not appear on map anymore, therefore no coordinates needed.

        // Add item to players itemlist
        // List might be null due to firebase
        if (player.PlayerItems == null)
            player.PlayerItems = new ArrayList<>();

        player.PlayerItems.add(item);

        player.takeAction(getContext(), this);
    }

    private GameItem getItemOnCoord(Coordinates coord) {
        for (GameItem item : mGameItemList) {
            if (item.Coordinate != null && item.Coordinate.equals(coord))
                return item;
        }

        return null;
    }

    private Monster getMonsterOnCoord(Coordinates coord) {
        for (Monster monster : mMonsterList) {
            if (monster.coordinate != null && monster.coordinate.equals(coord))
                return monster;
        }

        return null;
    }

    private void AttackMonster(Player player, Monster monster) {
        if (player.Coordinate.tileX == monster.coordinate.tileX && player.Coordinate.tileY == monster.coordinate.tileY) {

            // Player roll dmg
            int dmg = player.rollAttack();

            // Damage monster
            monster.HitPoints -= dmg;
            Log.d("AttackMonster", "Monster lost " + dmg + " HP");
            if (monster.HitPoints <= 0) {
                // Monster died, remove it from map
                Log.d("HandleMonsterClicked", "Monster died");
                mMonsterList.remove(monster);
            }

            // Player lose a turn
            player.takeAction(getContext(), this);
        }
    }

    private boolean clickedOnOtherPlayer(Coordinates coord) {
        for (Player player :mGamePlayerList) {
            if (player.Coordinate != null && player.Coordinate.equals(coord) && !player.Id.equals(mPlayerId))
                return true;
        }

        return false;
    }

    private Player getPlayerOnCoord(Coordinates coord) {
        for (Player player :mGamePlayerList) {
            if (player.Coordinate != null && player.Coordinate.equals(coord))
                return player;
        }

        return null;
    }

    public void MonsterTurn() {
        //Each monster take turn
        for (Monster monster : mMonsterList) {
            monster.resetActions();

            //as long the monster can take turn
            while (monster.canTakeAction()) {
                boolean attack = false;
                //check if same spot as player
                for (Player player : mGamePlayerList) {
                    if (monster.coordinate.tileY == player.Coordinate.tileY && monster.coordinate.tileX == player.Coordinate.tileX) {
                        attackPlayer(player, monster);
                        attack = true;
                        break;
                    }
                }
                // If monster is not attacking
                if (!attack) {
                    moveSingleMonster(monster);
                    monster.takeAction();
                }
            }
        }

        mFirebaseHelper.setMonsterList(mMonsterList);

        //Render map
        invalidate();
    }

    private void attackPlayer(Player player, Monster monster) {
        // Damage player
        player.Health -= monster.AttackPower;
        Log.d("Info", "attackPlayer: Player lost " + monster.AttackPower + " HP");
        if (player.Health <= 0)
        {
            // Player died - remove him from map
            mGamePlayerList.remove(player);
            Log.d("Info", "attackPlayer: Player died");
        }

        monster.takeAction();
    }

    private void moveSingleMonster(Monster monster) {
        // Check if monster has actions left
        if (!monster.canTakeAction())
            return;

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
            if ((monster.coordinate.tileX + move) < (mMaxCanvasWidth / mSquareWidth) && (monster.coordinate.tileX + move) > 0)
                moveMonsterToTile(monster, monster.coordinate.tileX + move, monster.coordinate.tileY);
        } else {
            if ((monster.coordinate.tileY + move) < (mMaxCanvasHeight / mSquareHeight) && (monster.coordinate.tileY + move) > 0)
                moveMonsterToTile(monster, monster.coordinate.tileX, monster.coordinate.tileY + move);
        }
    }

    private boolean tileNextToPlayer(Player player, int tileX, int tileY, int distance) {

        // Can't do this in one if-statment, since that would allow the player to move diagonal
        if (player.Coordinate.tileX <= tileX + distance && player.Coordinate.tileX >= tileX - distance) {
            if (player.Coordinate.tileY <= tileY && player.Coordinate.tileY >= tileY) {
                return true;
            }
        }

        if (player.Coordinate.tileY <= tileY + distance && player.Coordinate.tileY >= tileY - distance) {
            if (player.Coordinate.tileX <= tileX && player.Coordinate.tileX >= tileX){
                return true;
            }
        }

        return false;
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
            Coordinates availableCoord = availableCoord(coordinates.tileX, coordinates.tileY);

            if (availableCoord != null) {
                int awayFromPlayer = 0;

                for (Player player : mGamePlayerList) {
                    if (availableCoord .tileX > (player.Coordinate.tileX) || (player.Coordinate.tileX) > (availableCoord .tileX)) {
                        if (availableCoord .tileY > (player.Coordinate.tileY) || availableCoord .tileY < (player.Coordinate.tileY)) {
                            awayFromPlayer++;
                        }
                    }
                }

                if (awayFromPlayer == mGamePlayerList.size())
                    return availableCoord;
            }
        }

        return null;
    }

    private void movePlayerToTile(Player player, int tileX, int tileY) {
        // Check if tile is passable
        if (!mGrid[tileY][tileX].CanBePassed) return;

        Coordinates coord = availableCoord(tileX, tileY);
        if (coord != null) {
            player.Coordinate = coord;
        }
    }

    private void moveMonsterToTile(Monster monster, int tileX, int tileY) {
        // Check if tile is passable
        if (!mGrid[tileY][tileX].CanBePassed) return;

        Coordinates coord = availableCoord(tileX, tileY);
        if (coord != null) {
            monster.coordinate = coord;
        }
    }

    private Coordinates availableCoord(int tileX, int tileY) {
        if (tileIsFree(tileX, tileY)) {
            int unitsOnTile = UnitsOnTile(tileX, tileY);
            return new Coordinates(tileX, tileY, unitsOnTile % 3, unitsOnTile / 3);
        }

        // No free space on tile
        return null;
    }

    private boolean tileIsFree(int tileX, int tileY) {
        int maximumAmountOfPlayers = mTileDivision * mTileDivision;
        return UnitsOnTile(tileX, tileY) < maximumAmountOfPlayers;
    }

    private int UnitsOnTile(int tileX, int tileY) {
        int unitsOnTile = 0;

        for (Player player : mGamePlayerList) {
            if (player.Coordinate.tileX == tileX && player.Coordinate.tileY == tileY)
                unitsOnTile++;
        }

        for (Monster monster : mMonsterList) {
            if (monster.coordinate.tileX == tileX && monster.coordinate.tileY == tileY)
                unitsOnTile++;
        }

        return unitsOnTile;
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


        // Find a tile that is away from player and is passable.
        Coordinates coordinates = moveToRandomTile();
        while (coordinates == null || !tileIsPassable(coordinates)) {
            coordinates = moveToRandomTile();
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
        item.Coordinate = coordinates;
        mGameItemList.add(item);
    }

    public void spawnMonsterOnTile(Monster monster, Coordinates coordinates) {
        monster.coordinate = coordinates;
        mMonsterList.add(monster);
    }

    private boolean tileIsPassable(Coordinates coordinates) {
        return mGrid[coordinates.tileY][coordinates.tileX].CanBePassed;
    }


    public Player getLocalPlayer() {
        for (Player player : mGamePlayerList) {
            if (player.Id.equals(mPlayerId)){
                return player;
            }
        }

        return null;
    }
} // end class
  
