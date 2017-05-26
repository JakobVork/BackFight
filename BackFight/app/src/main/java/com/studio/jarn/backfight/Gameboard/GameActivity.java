package com.studio.jarn.backfight.Gameboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.studio.jarn.backfight.Firebase.FirebaseGameActivityListener;
import com.studio.jarn.backfight.Fragment.ItemsAndStatsFragment;
import com.studio.jarn.backfight.Fragment.fragment_item_details;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;
import com.studio.jarn.backfight.MapGeneration.DefaultMap;
import com.studio.jarn.backfight.MapGeneration.IMapGenerator;
import com.studio.jarn.backfight.MapGeneration.MazeMap;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.Fragment.MonsterDetails;
import com.studio.jarn.backfight.Notification.NotificationIntentService;
import com.studio.jarn.backfight.Player.Player;
import com.studio.jarn.backfight.Player.PlayerGameActivityListener;
import com.studio.jarn.backfight.R;

import java.lang.reflect.Type;
import java.util.List;

import static com.studio.jarn.backfight.Notification.NotificationIntentService.ACTION_NEWROUND;
import static com.studio.jarn.backfight.Notification.NotificationIntentService.EXTRA_TEXT;
import static com.studio.jarn.backfight.Notification.NotificationIntentService.EXTRA_TITLE;


public class GameActivity extends FragmentActivity implements ItemsAndStatsFragment.OnItemSelectedListener, FirebaseGameActivityListener, PlayerGameActivityListener {
    private static final int sSquaresViewedAtStartup = 3;
    private static final int sDefaultGridSize = 15;
    public static boolean isGameActivityVisible = false;
    private static int sGridSize = 16;
    Fragment overviewFragment;
    Fragment detailFragment;
    private Tile[][] mGrid;
    private TextView btnActionCounter;
    private TextView btnRound;
    private ImageView mIvFragmentShow;
    private ImageView mIvFragmentHide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_board_activity);

        Intent i = getIntent();
        if (i != null) {
            setupGameView(i);
        }

        setupItemFragment();
        setupDefaultText();
    }

    private void setupDefaultText() {
        setRound(1);
    }


    private void setupItemFragment() {
        btnActionCounter = (TextView) findViewById(R.id.game_board_activity_tv_actionCount);
        btnRound = (TextView) findViewById(R.id.game_board_activity_tv_round);
        mIvFragmentHide = (ImageView) findViewById(R.id.game_board_activity_iv_hide_items);
        mIvFragmentShow = (ImageView) findViewById(R.id.game_board_activity_iv_show_items);
        hideFragment();

        mIvFragmentHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                hideFragment();
            }
        });

        mIvFragmentShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameView gv = (GameView) findViewById(R.id.boardview);
                showOverviewFragment(gv.getLocalPlayer());
            }
        });
    }

    private void setupGameView(Intent i) {
        String uuid = i.getStringExtra(getString(R.string.EXTRA_UUID));
        boolean host = i.getBooleanExtra(getString(R.string.EXTRA_HOST), false);
        sGridSize = i.getIntExtra(getString(R.string.EXTRA_GRIDSIZE), sDefaultGridSize);

        GameView gv = (GameView) findViewById(R.id.boardview);
        if (gv != null) {
            if (host) {

                //Casting to List example: http://stackoverflow.com/questions/5813434/trouble-with-gson-serializing-an-arraylist-of-pojos
                Type playerListType = new TypeToken<List<Player>>() {
                }.getType();

                String playerListInJson = i.getStringExtra(getString(R.string.EXTRA_PLAYERLIST));
                List<Player> playerList = new Gson().fromJson(playerListInJson, playerListType);

                //http://stackoverflow.com/questions/2836256/passing-enum-or-object-through-an-intent-the-best-solution
                GridType gridType = (GridType) i.getSerializableExtra(getString(R.string.EXTRA_GRIDTYPE));
                setupMap(gridType);

                gv.setGridSize(sGridSize);
                gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
                gv.setupFirebase(uuid);

                //addPlayers();
                gv.initHostGrid(mGrid);
                gv.setListeners();
                gv.initAddPlayers(playerList);

                // Spawn items
                gv.spawnItems(10);

                // Spawn monsters
                gv.spawnStartMonsters(5);

            } else {
                gv.setGridSize(sGridSize);
                gv.setViewSizeAtStartup(sSquaresViewedAtStartup);
                gv.setupFirebase(uuid);
                gv.initClientGrid();

                gv.setListeners();
            }

            gv.setItemListener();
        }
    }

    public void showOverviewFragment(Player player) {
        mIvFragmentShow.setVisibility(View.GONE);
        mIvFragmentHide.setVisibility(View.VISIBLE);

        // Add ItemsAndStats fragment
        overviewFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);

        // Need to create a new every time, since the current fragment might be for another user
        overviewFragment = ItemsAndStatsFragment.newInstance(player);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
        ft.add(R.id.game_board_activity_items_and_stats_fragment, overviewFragment);
        ft.commit();
    }

    public void showOverviewFragment(Monster monster) {
        mIvFragmentShow.setVisibility(View.GONE);
        mIvFragmentHide.setVisibility(View.VISIBLE);

        // Add ItemsAndStats fragment
        overviewFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);

        // Need to create a new every time, since the current fragment might be for another user
        overviewFragment = MonsterDetails.newInstance(monster);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
        ft.add(R.id.game_board_activity_items_and_stats_fragment, overviewFragment);
        ft.commit();
    }

    public void hideFragment() {
        mIvFragmentShow.setVisibility(View.VISIBLE);
        mIvFragmentHide.setVisibility(View.GONE);

        overviewFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        if (overviewFragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_right, 0, 0);
            ft.remove(overviewFragment);
            ft.commit();
        }
    }

    public void setActionCounter(int count) {
        String actionCounterText = getString(R.string.game_actionCount) + String.valueOf(count);
        btnActionCounter.setText(actionCounterText);
    }

    public void setRound(int count) {
        String roundText = getString(R.string.game_roundCounter) + String.valueOf(count);
        btnRound.setText(roundText);
    }

    private void setupMap(GridType gridType) {
        IMapGenerator mapGenerator;

        switch (gridType) {
            case DefaultGrid: {
                mapGenerator = new DefaultMap();
                mGrid = mapGenerator.generateMap(sGridSize);
                break;
            }
            case Maze: {
                mapGenerator = new MazeMap();
                mGrid = mapGenerator.generateMap(sGridSize);
                break;
            }
        }
    }

    public void onItemSelected(GameItem item) {
        Log.d("Item", "onItemSelected: Clicked!");
        overviewFragment = getSupportFragmentManager().findFragmentById(R.id.game_board_activity_items_and_stats_fragment);
        detailFragment = fragment_item_details.newInstance((ItemWeapon) item);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.game_board_activity_items_and_stats_fragment, detailFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    //ToDo for testing purpose
    public void showMonsterDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Monster");
        alertDialog.setMessage("Monster turn :)");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void sendNotificationNewRound() {
        Intent notificationServiceIntent = new Intent(this, NotificationIntentService.class);
        notificationServiceIntent.setAction(ACTION_NEWROUND);
        notificationServiceIntent.putExtra(EXTRA_TITLE, "New Round");
        notificationServiceIntent.putExtra(EXTRA_TEXT, "A new round has started remember to take your turn :)");
        startService(notificationServiceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isGameActivityVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isGameActivityVisible = false;
    }
}
