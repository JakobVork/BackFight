package com.studio.jarn.backfight;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.primitives.Ints;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LobbyActivity extends AppCompatActivity {

    static final String DATABASE_POSTFIX_STARTGAME = "StartGame";
    static final String DATABASE_POSTFIX_RADIOGROUP = "RadioGroup";
    static final String DATABASE_POSTFIX_NUMBERPICKER = "NumberPicker";
    Button mBtnBack;
    Button mBtnStart;
    TextView mTvId;
    ListView mLvPlayers;
    RadioButton mRbDefault;
    RadioButton mRbMaze;
    NumberPicker mNpGridSize;
    PlayerAdapter mPlayerAdapter;
    List<Player> listOfPlayersCurrentlyInGame = new ArrayList<>();
    String gameId;
    RadioGroup rg;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        hideActionBar();
        initButtons();
        getData();
        setNumberPicker();

        Intent i = getIntent();
        mTvId = (TextView) findViewById(R.id.activity_lobby_tv_id);
        boolean host = i.getExtras().getBoolean(getString(R.string.EXTRA_HOST), false);
        database = FirebaseDatabase.getInstance();

        if (host) {

            gameId = UUID.randomUUID().toString().substring(30);
            mTvId.setText(gameId);

            databaseReference = database.getReference(gameId);
            databaseReference.push().setValue(new Player(R.drawable.player32, R.drawable.player32selected, "AndersHost"));

            setupListView();
            setupRadioGroupListener();
            updateNumberPickerOnDb(15); //Set 15 as default on db


        } else {
            mBtnStart.setVisibility(View.GONE);
            mNpGridSize.setEnabled(false);

            mRbDefault = (RadioButton) findViewById(R.id.activity_lobby_rb_default);
            mRbMaze = (RadioButton) findViewById(R.id.activity_lobby_rb_maze);
            mRbDefault.setEnabled(false);
            mRbMaze.setEnabled(false);
            gameId = i.getExtras().getString(getString(R.string.EXTRA_UUID));
            mTvId.setText(gameId);

            databaseReference = database.getReference(gameId);
            databaseReference.push().setValue(new Player(R.drawable.player32, R.drawable.player32selected, "AndersClient"));

            setupStartGameListener();

            setupListView();

            syncWhenChangingControllers();
        }

    }

    private void setupStartGameListener() {
        //Setup listener to listen when the game starts
        String gameIdRadioButtonSelected = gameId + DATABASE_POSTFIX_STARTGAME;
        databaseReference = database.getReference(gameIdRadioButtonSelected);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    startGameClient();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });

    }

    private void setupRadioGroupListener() {
        rg = (RadioGroup) findViewById(R.id.activity_lobby_rg_gridType);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                updateGridTypeOnDb(rg.indexOfChild(findViewById(rg.getCheckedRadioButtonId())));
            }
        });
        updateGridTypeOnDb(0); //Set default as default on db
    }

    private void syncWhenChangingControllers() {
        mRbDefault = (RadioButton) findViewById(R.id.activity_lobby_rb_default);
        mRbMaze = (RadioButton) findViewById(R.id.activity_lobby_rb_maze);

        String gameIdNumberPicker = gameId + DATABASE_POSTFIX_NUMBERPICKER;
        databaseReference = database.getReference(gameIdNumberPicker);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mNpGridSize = (NumberPicker) findViewById(R.id.activity_lobby_np_mapSize);
                mNpGridSize.setValue(Ints.checkedCast(((long) dataSnapshot.getValue())));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });

        String gameIdButtonSelected = gameId + DATABASE_POSTFIX_RADIOGROUP;
        databaseReference = database.getReference(gameIdButtonSelected);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rg = (RadioGroup) findViewById(R.id.activity_lobby_rg_gridType);
                ((RadioButton) rg.getChildAt((Ints.checkedCast(((long) dataSnapshot.getValue()))))).setChecked(true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void setupListView() {
        ArrayList<Player> playerList = new ArrayList<Player>() {
        };
        // Create the adapter to convert the array to views
        mPlayerAdapter = new PlayerAdapter(this, playerList);
        // Attach the adapter to a ListView

        mLvPlayers = (ListView) findViewById(R.id.activity_lobby_lv_players);
        mLvPlayers.setAdapter(mPlayerAdapter);
        mPlayerAdapter.add(new Player(R.drawable.player32, R.drawable.player32selected, "Test"));
        databaseReference = database.getReference(gameId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Player> playerList = new ArrayList<Player>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(Player.class));
                }
                for (Player player : playerList) {
                    listOfPlayersCurrentlyInGame.add(player);
                }

                mPlayerAdapter.clear();
                mPlayerAdapter.addAll(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }


    private void setNumberPicker() {
        mNpGridSize = (NumberPicker) findViewById(R.id.activity_lobby_np_mapSize);
        mNpGridSize.setMaxValue(30);
        mNpGridSize.setMinValue(5);
        mNpGridSize.setValue(15);

        mNpGridSize.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateNumberPickerOnDb(newVal);
            }
        });
    }

    private void updateNumberPickerOnDb(int value) {
        String gameIdNumberPicker = gameId + DATABASE_POSTFIX_NUMBERPICKER;
        databaseReference = database.getReference(gameIdNumberPicker);
        databaseReference.setValue(value);
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mBtnBack = (Button) findViewById(R.id.activity_lobby_btn_back);
        mBtnStart = (Button) findViewById(R.id.activity_lobby_btn_start);

        setOnClickListeners();
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
        Intent StartGameIntent = new Intent(this, GameActivity.class);

        if (listOfPlayersCurrentlyInGame.size() == 0)
            return;
        String jsonPlayerList = new Gson().toJson(listOfPlayersCurrentlyInGame);
        StartGameIntent.putExtra(getString(R.string.EXTRA_PLAYERLIST), jsonPlayerList);

        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), "String");
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), true);

        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDSIZE), mNpGridSize.getValue());
        GridType gridType = gridTypeSelector();
        StartGameIntent.putExtra(getString(R.string.EXTRA_GRIDTYPE), gridType);

        //Updating the StartGame key to other players know the game has started!!!
        String gameStartGame = gameId + DATABASE_POSTFIX_STARTGAME;
        databaseReference = database.getReference(gameStartGame);
        databaseReference.setValue(true);

        startActivity(StartGameIntent);
    }

    private void startGameClient() {
        Intent StartGameIntent = new Intent(this, GameActivity.class);
        StartGameIntent.putExtra(getString(R.string.EXTRA_UUID), "String");
        StartGameIntent.putExtra(getString(R.string.EXTRA_HOST), false);
        startActivity(StartGameIntent);
    }

    private GridType gridTypeSelector() {
        rg = (RadioGroup) findViewById(R.id.activity_lobby_rg_gridType);

        switch (rg.getCheckedRadioButtonId()) {
            case R.id.activity_lobby_rb_default:
                return GridType.DefaultGrid;
            case R.id.activity_lobby_rb_maze:
                return GridType.Maze;
        }
        return null;
    }

    private void updateGridTypeOnDb(int value) {
        String gameIdRadioButtonSelected = gameId + DATABASE_POSTFIX_RADIOGROUP;
        databaseReference = database.getReference(gameIdRadioButtonSelected);
        databaseReference.setValue(value);
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
