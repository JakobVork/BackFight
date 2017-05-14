package com.studio.jarn.backfight;

import android.content.Intent;
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
import com.studio.jarn.backfight.Items.ItemFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyActivity extends AppCompatActivity implements FirebaseLobbyListener {

    Button mBtnBack;
    Button mBtnStart;
    TextView mTvId;
    ListView mLvPlayers;
    RadioButton mRbDefault;
    RadioButton mRbMaze;
    NumberPicker mNpGridSize;
    PlayerAdapter mPlayerAdapter;
    List<Player> mListOfPlayersCurrentlyInGame = new ArrayList<>();
    String mGameId;
    RadioGroup mRg;
    FirebaseHelper mFirebaseHelper;
    Intent intent;
    boolean host;

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

        if (host) setupHost();
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
        mGameId = intent.getExtras().getString(getString(R.string.EXTRA_UUID));
        mFirebaseHelper.setStandardKey(mGameId);
        //TODO needs to be extracted from SharedPrefs
        mFirebaseHelper.addPlayerToDb(new Player(R.drawable.player32, R.drawable.player32selected, "AndersClient"));
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
        mFirebaseHelper.setStandardKey(mGameId);
        setupListView();
        setupRadioGroupListener();
        mFirebaseHelper.setNumberPicker(15); //Set 15 as default on db
        mTvId.setText(mGameId);
        //TODO needs to be extracted from SharedPrefs
        mFirebaseHelper.addPlayerToDb(new Player(R.drawable.player32, R.drawable.player32selected, "AndersHost"));
    }

    private void getValuesFromIntent() {
        intent = getIntent();
        host = intent.getExtras().getBoolean(getString(R.string.EXTRA_HOST), false);
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
                startGameHost();
            }
        });
    }

    // Starts the game
    private void startGameHost() {
        if (mListOfPlayersCurrentlyInGame.size() == 0) return;

        Intent StartGameIntent = new Intent(this, GameActivity.class);
        String jsonPlayerList = new Gson().toJson(mListOfPlayersCurrentlyInGame);
        StartGameIntent.putExtra(getString(R.string.EXTRA_PLAYERLIST), jsonPlayerList);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), mGameId);
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), true);
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDSIZE), mNpGridSize.getValue());
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDTYPE), gridTypeSelector());

        mFirebaseHelper.setStartGame();
        startActivity(StartGameIntent);
    }

    public void startGameClient() {
        Intent StartGameIntent = new Intent(this, GameActivity.class);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), mGameId);
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), false);
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
