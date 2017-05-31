package com.studio.jarn.backfight.Lobby;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.studio.jarn.backfight.Firebase.FirebaseHelper;
import com.studio.jarn.backfight.Firebase.FirebaseLobbyListener;
import com.studio.jarn.backfight.Gameboard.GameActivity;
import com.studio.jarn.backfight.Gameboard.GridType;
import com.studio.jarn.backfight.Player.Player;
import com.studio.jarn.backfight.Player.PlayerAdapter;
import com.studio.jarn.backfight.R;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.studio.jarn.backfight.MainMenuActivity.PHONE_UUID_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.AVATAR_IMAGE_SELECTED_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.AVATAR_IMAGE_SP;
import static com.studio.jarn.backfight.Settings.SettingsActivity.PROFILE_NAME_SP;

public class LobbyActivity extends AppCompatActivity implements FirebaseLobbyListener {

    private final List<Player> mListOfPlayersCurrentlyInGame = new ArrayList<>();
    private Button mBtnBack;
    private Button mBtnStart;
    private TextView mTvId;
    private ListView mLvPlayers;
    private RadioButton mRbDefault;
    private RadioButton mRbMaze;
    private NumberPicker mNpGridSize;
    private PlayerAdapter mPlayerAdapter;
    private String mGameId;
    private RadioGroup mRg;
    private FirebaseHelper mFirebaseHelper;
    private Intent mIntent;
    private boolean mHost;
    private boolean mSpectate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        hideActionBar();

        setAllWidgets();
        setOnClickListeners();
        getData();
        setNumberPicker();
        getValuesFromIntent();

        mFirebaseHelper = new FirebaseHelper(this);

        if (mHost) setupHost();
        else setupClient();
    }

    private void setAllWidgets() {
        mRg = (RadioGroup) findViewById(R.id.activity_lobby_rg_gridType);
        mRbDefault = (RadioButton) findViewById(R.id.activity_lobby_rb_default);
        mRbMaze = (RadioButton) findViewById(R.id.activity_lobby_rb_maze);
        mTvId = (TextView) findViewById(R.id.activity_lobby_tv_id);
        mNpGridSize = (NumberPicker) findViewById(R.id.activity_lobby_np_mapSize);
        mLvPlayers = (ListView) findViewById(R.id.activity_lobby_lv_players);
        mBtnBack = (Button) findViewById(R.id.activity_lobby_btn_back);
        mBtnStart = (Button) findViewById(R.id.activity_lobby_btn_start);
    }

    private void setupClient() {
        mGameId = mIntent.getExtras().getString(getString(R.string.EXTRA_UUID));
        mSpectate = mIntent.getExtras().getBoolean(getString(R.string.EXTRA_SPECTATE), false);
        mFirebaseHelper.setStandardKey(mGameId, this);

        if (!mSpectate) {
            SharedPreferences sp = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
            String PlayerName = sp.getString(PROFILE_NAME_SP, "");
            int Image = sp.getInt(AVATAR_IMAGE_SP, R.drawable.player32);
            int ImageSelected = sp.getInt(AVATAR_IMAGE_SELECTED_SP, R.drawable.player32selected);
            String Uuid = sp.getString(PHONE_UUID_SP, "");
            if (PlayerName.length() == 11 && (ByteBuffer.wrap(PlayerName.getBytes(Charset.forName("UTF-8"))).getInt() == 1147236980)) {
                Image = R.drawable.player32;
                ImageSelected = R.drawable.player32selected;
            }
            mFirebaseHelper.addPlayerToDb(new Player(Image, ImageSelected, PlayerName, Uuid));
        }

        mFirebaseHelper.setupStartGameListener();
        mFirebaseHelper.setupWidgetsListener();

        mBtnStart.setVisibility(View.GONE);
        mNpGridSize.setEnabled(false);
        mRbDefault.setEnabled(false);
        mRbMaze.setEnabled(false);
        mTvId.setText(mGameId);

        setupListView();

    }

    private void setupHost() {
        mGameId = UUID.randomUUID().toString().substring(30);
        mFirebaseHelper.setStandardKey(mGameId, this);
        setupListView();
        setupRadioGroupListener();
        mFirebaseHelper.setNumberPicker(15); //Set 15 as default on db
        mTvId.setText(mGameId);

        SharedPreferences sp = this.getSharedPreferences(getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE);
        int Image = sp.getInt(AVATAR_IMAGE_SP, R.drawable.player32);
        int ImageSelected = sp.getInt(AVATAR_IMAGE_SELECTED_SP, R.drawable.player32selected);
        String PlayerName = sp.getString(PROFILE_NAME_SP, "");
        String Uuid = sp.getString(PHONE_UUID_SP, "");
        if (PlayerName.length() == 11 && (ByteBuffer.wrap(PlayerName.getBytes(Charset.forName("UTF-8"))).getInt() == 1147236980)) {
            Image = R.drawable.player32;
            ImageSelected = R.drawable.player32selected;
        }

        mFirebaseHelper.addPlayerToDb(new Player(Image, ImageSelected, PlayerName, Uuid));
    }

    private void getValuesFromIntent() {
        mIntent = getIntent();
        mHost = mIntent.getExtras().getBoolean(getString(R.string.EXTRA_HOST), false);
    }

    private void setupRadioGroupListener() {

        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                mFirebaseHelper.setGridType((mRg.indexOfChild(findViewById(mRg.getCheckedRadioButtonId()))));
            }
        });
        mFirebaseHelper.setGridType(0);//Set default as default on db
    }

    private void setupListView() {
        ArrayList<Player> playerList = new ArrayList<>();
        mPlayerAdapter = new PlayerAdapter(this, playerList);
        mLvPlayers.setAdapter(mPlayerAdapter);
        mFirebaseHelper.setListViewListener();
    }

    @Override
    public void setPlayerList(ArrayList<Player> playerList) {
        mListOfPlayersCurrentlyInGame.clear();
        for (Player player : playerList) {
            mListOfPlayersCurrentlyInGame.add(player);
        }
        mPlayerAdapter.clear();
        mPlayerAdapter.addAll(playerList);
    }

    @Override
    public void setNumberPickerValue(int value) {
        mNpGridSize.setValue(value);
    }

    private void setNumberPicker() {
        mNpGridSize.setMaxValue(30);
        mNpGridSize.setMinValue(5);
        mNpGridSize.setValue(15);

        mNpGridSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mFirebaseHelper.setNumberPicker(newVal);
            }
        });
    }

    private void setOnClickListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToNewGame();
            }
        });

        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameHost(v);
            }
        });
    }

    // Starts the game
    private void startGameHost(View view) {
        if (mListOfPlayersCurrentlyInGame.size() == 0) return;

        Intent StartGameIntent = new Intent(this, GameActivity.class);
        String jsonPlayerList = new Gson().toJson(mListOfPlayersCurrentlyInGame);
        StartGameIntent.putExtra(getString(R.string.EXTRA_PLAYERLIST), jsonPlayerList);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), mGameId);
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), true);
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDSIZE), mNpGridSize.getValue());
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDTYPE), gridTypeSelector());

        mFirebaseHelper.setStartGame();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(StartGameIntent, bundle);
        } else
        startActivity(StartGameIntent);
    }

    public void startGameClient() {
        Intent StartGameIntent = new Intent(this, GameActivity.class);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), mGameId);
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), false);
        StartGameIntent.putExtra(getString(R.string.EXTRA_SPECTATE), mSpectate);

        startActivity(StartGameIntent);
    }


    private GridType gridTypeSelector() {
        switch (mRg.getCheckedRadioButtonId()) {
            case R.id.activity_lobby_rb_default:
                return GridType.DefaultGrid;
            case R.id.activity_lobby_rb_maze:
                return GridType.Maze;
        }
        return null;
    }

    @Override
    public void setRadioGroupButton(int value) {
        ((RadioButton) mRg.getChildAt(value)).setChecked(true);
    }

    // Go back to main menu
    private void backToNewGame() {
        finish();
    }

    // Hides the actionbar
    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }

    // Get data from database
    private void getData() {
    }
}
