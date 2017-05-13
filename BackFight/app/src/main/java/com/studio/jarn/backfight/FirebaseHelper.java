package com.studio.jarn.backfight;


import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class FirebaseHelper {

    private static final String sDatabasePostfixGrid = "Grid";
    private static final String sDatabasePostfixPlayers = "PlayerList";
    private static final String sDatabasePostfixStartGame = "StartGame";
    private static final String sDatabasePostfixRadioGroup = "RadioGroup";
    private static final String sDatabasePostfixNumberPicker = "NumberPicker";
    private FirebaseDatabase mDatabase;
    private String mGameId;
    private String mGameIdRadio;
    private String mGameIdStartGame;
    private String mGameIdNumberPicker;
    private String mGameIdGrid;
    private String mGameIdPlayers;
    private NewGameListener mNewGameListener;
    private LobbyListener mLobbyListener;
    private GameViewListener mGameViewListener;
    private String mDialogInput;

    FirebaseHelper(Context context) {
        mDatabase = FirebaseDatabase.getInstance();

        if (context instanceof NewGameListener) {
            mNewGameListener = (NewGameListener) context;
        } else if (context instanceof LobbyListener) {
            mLobbyListener = (LobbyListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    FirebaseHelper(View view) {
        mDatabase = FirebaseDatabase.getInstance();

        if (view instanceof GameViewListener) {
            mGameViewListener = (GameViewListener) view;
        } else {
            throw new RuntimeException(view.toString()
                    + " must implement Listener");
        }
    }


    void setStandardKey(String gameId) {
        mGameId = gameId;
        mGameIdRadio = gameId + sDatabasePostfixRadioGroup;
        mGameIdStartGame = gameId + sDatabasePostfixStartGame;
        mGameIdNumberPicker = gameId + sDatabasePostfixNumberPicker;
        mGameIdGrid = gameId + sDatabasePostfixGrid;
        mGameIdPlayers = gameId + sDatabasePostfixPlayers;
    }

    //NewGameListener
    void validateIfGameExist(String input) {
        mDialogInput = input;

        DatabaseReference databaseReference = mDatabase.getReference(mDialogInput);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mNewGameListener.gameExist(true, mDialogInput);
                } else {
                    mNewGameListener.gameExist(false, mDialogInput);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //mLobbyListener
    void addPlayerToDb(Player player) {
        mDatabase.getReference(mGameId).push().setValue(player);
    }

    void setupStartGameListener() {
        mDatabase.getReference(mGameIdStartGame).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mLobbyListener.startGameClient();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setupWidgetsListener() {
        mDatabase.getReference(mGameIdNumberPicker).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mLobbyListener.setNumberPickerValue(Ints.checkedCast(((long) dataSnapshot.getValue())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });

        mDatabase.getReference(mGameIdRadio).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mLobbyListener.setRadioGroupButton((Ints.checkedCast(((long) dataSnapshot.getValue()))));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setListViewListener() {
        mDatabase.getReference(mGameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Player> playerList = new ArrayList<Player>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(Player.class));
                }
                mLobbyListener.setPlayerList(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setNumberPicker(int value) {
        mDatabase.getReference(mGameIdNumberPicker).setValue(value);
    }

    void setStartGame() {
        mDatabase.getReference(mGameIdStartGame).setValue(true);
    }

    void setGridType(int value) {
        mDatabase.getReference(mGameIdRadio).setValue(value);
    }


    //GameViewListener
    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates) {
        mDatabase.getReference(mGameIdPlayers).setValue(playersWithCoordinates);
    }

    void setupGridListener() {
        mDatabase.getReference(mGameIdGrid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int row = -1;
                int column = -1;

                if (dataSnapshot.getValue() == null) return;

                int sizeOfArrayOnFirebase = Iterables.size(dataSnapshot.getChildren());

                Tile[][] grid = new Tile[sizeOfArrayOnFirebase][sizeOfArrayOnFirebase];

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    row++;
                    for (DataSnapshot postSnapshot1 : postSnapshot.getChildren()) {
                        column++;
                        grid[row][column] = postSnapshot1.getValue(Tile.class);
                    }
                    column = -1;
                }
                mGameViewListener.setGrid(sizeOfArrayOnFirebase, grid);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    void setPlayerListListener() {
        mDatabase.getReference(mGameIdPlayers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Tuple<Player, Coordinates>> playerList = new ArrayList<>();
                boolean allPlayerActionsUsed = true;

                GenericTypeIndicator<Tuple<Player, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<Player, Coordinates>>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(genericTypeIndicator));
                }
                mGameViewListener.setPlayerList(playerList);

                allTurnsUsed(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    private void allTurnsUsed(ArrayList<Tuple<Player, Coordinates>> playerList) {
        for (Tuple<Player, Coordinates> player : playerList) {
            if (player.x.actionsRemaning != 0) return;
        }
        mGameViewListener.startMonsterTurn();
        for (Tuple<Player, Coordinates> player : playerList) {
            player.x.actionsRemaning = 3;
        }
    }


    void setGrid(List<List<Tile>> list) {
        mDatabase.getReference(mGameIdGrid).setValue(list);
    }
}
