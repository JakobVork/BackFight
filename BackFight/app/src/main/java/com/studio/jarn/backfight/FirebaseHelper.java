package com.studio.jarn.backfight;


import android.content.Context;
import android.util.Log;

import com.google.common.primitives.Ints;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class FirebaseHelper {

    private static final String DATABASE_POSTFIX_STARTGAME = "StartGame";
    private static final String DATABASE_POSTFIX_RADIOGROUP = "RadioGroup";
    private static final String DATABASE_POSTFIX_NUMBERPICKER = "NumberPicker";
    private FirebaseDatabase database;
    private String mGameId;
    private String mGameIdRadio;
    private String mGameIdStarGame;
    private String mGameIdNumberPicker;
    private NewGameListener newGameListener;
    private LobbyListener lobbyListener;
    //newGameListener
    private String dialogInput;

    FirebaseHelper(Context context) {
        database = FirebaseDatabase.getInstance();

        if (context instanceof NewGameListener) {
            newGameListener = (NewGameListener) context;
        } else if (context instanceof LobbyListener) {
            lobbyListener = (LobbyListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    void setStandardKey(String gameId) {
        mGameId = gameId;
        mGameIdRadio = gameId + DATABASE_POSTFIX_RADIOGROUP;
        mGameIdStarGame = gameId + DATABASE_POSTFIX_STARTGAME;
        mGameIdNumberPicker = gameId + DATABASE_POSTFIX_NUMBERPICKER;
    }

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
        database.getReference(mGameIdStarGame).addValueEventListener(new ValueEventListener() {
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
        database.getReference(mGameId).setValue(true);
    }

    void setGridType(int value) {
        database.getReference(mGameIdRadio).setValue(value);
    }
}
