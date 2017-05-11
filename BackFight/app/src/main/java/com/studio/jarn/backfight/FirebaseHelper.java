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

    private static final String DATABASE_POSTFIX_GRID = "Grid";
    private static final String DATABASE_POSTFIX_PLAYERS = "PlayerList";
    private static final String DATABASE_POSTFIX_STARTGAME = "StartGame";
    private static final String DATABASE_POSTFIX_RADIOGROUP = "RadioGroup";
    private static final String DATABASE_POSTFIX_NUMBERPICKER = "NumberPicker";
    private FirebaseDatabase database;
    private String mGameId;
    private String mGameIdRadio;
    private String mGameIdStartGame;
    private String mGameIdNumberPicker;
    private String mGameIdGrid;
    private String mGameIdPlayers;
    private NewGameListener newGameListener;
    private LobbyListener lobbyListener;
    private GameViewListener gameViewListener;
    //newGameListener
    private String dialogInput;

    FirebaseHelper(Context context) {
        database = FirebaseDatabase.getInstance();

        if (context instanceof NewGameListener) {
            newGameListener = (NewGameListener) context;
        } else if (context instanceof LobbyListener) {
            lobbyListener = (LobbyListener) context;
        } else if (context instanceof GameViewListener) {
            gameViewListener = (GameViewListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    FirebaseHelper(View view) {
        database = FirebaseDatabase.getInstance();

        if (view instanceof GameViewListener) {
            gameViewListener = (GameViewListener) view;
        } else {
            throw new RuntimeException(view.toString()
                    + " must implement Listener");
        }
    }


    void setStandardKey(String gameId) {
        mGameId = gameId;
        mGameIdRadio = gameId + DATABASE_POSTFIX_RADIOGROUP;
        mGameIdStartGame = gameId + DATABASE_POSTFIX_STARTGAME;
        mGameIdNumberPicker = gameId + DATABASE_POSTFIX_NUMBERPICKER;
        mGameIdGrid = gameId + DATABASE_POSTFIX_GRID;
        mGameIdPlayers = gameId + DATABASE_POSTFIX_PLAYERS;
    }

    //NewGameListener
    void validateIfGameExist(String input) {
        dialogInput = input;

        DatabaseReference databaseReference = database.getReference(dialogInput);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    newGameListener.onTest(true, dialogInput);
                } else {
                    newGameListener.onTest(false, dialogInput);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //lobbyListener
    void addPlayerToDb(Player player) {
        database.getReference(mGameId).push().setValue(player);
    }

    void setupStartGameListener() {
        database.getReference(mGameIdStartGame).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    lobbyListener.startGameClient();
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
        database.getReference(mGameIdNumberPicker).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    lobbyListener.setNumberPickerValue(Ints.checkedCast(((long) dataSnapshot.getValue())));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });

        database.getReference(mGameIdRadio).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lobbyListener.setRadioGroupButton((Ints.checkedCast(((long) dataSnapshot.getValue()))));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setListViewListener() {
        database.getReference(mGameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Player> playerList = new ArrayList<Player>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(Player.class));
                }
                lobbyListener.setPlayerList(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setNumberPicker(int value) {
        database.getReference(mGameIdNumberPicker).setValue(value);
    }

    void setStartGame() {
        database.getReference(mGameIdStartGame).setValue(true);
    }

    void setGridType(int value) {
        database.getReference(mGameIdRadio).setValue(value);
    }


    //GameViewListener
    void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates) {
        database.getReference(mGameIdPlayers).setValue(playersWithCoordinates);
    }

    void setupGridListener() {
        database.getReference(mGameIdGrid).addValueEventListener(new ValueEventListener() {
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
                gameViewListener.setGrid(sizeOfArrayOnFirebase, grid);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    void setPlayerListListener() {
        database.getReference(mGameIdPlayers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Tuple<Player, Coordinates>> playerList = new ArrayList<>();

                GenericTypeIndicator<Tuple<Player, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<Player, Coordinates>>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(genericTypeIndicator));
                }
                gameViewListener.setPlayerList(playerList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    void setGrid(List<List<Tile>> list) {
        database.getReference(mGameIdGrid).setValue(list);
    }
}
