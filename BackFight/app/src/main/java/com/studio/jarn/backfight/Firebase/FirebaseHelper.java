package com.studio.jarn.backfight.Firebase;


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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.studio.jarn.backfight.Gameboard.Coordinates;
import com.studio.jarn.backfight.Gameboard.Tile;
import com.studio.jarn.backfight.Gameboard.Tuple;
import com.studio.jarn.backfight.Items.GameItem;
import com.studio.jarn.backfight.Items.ItemWeapon;
import com.studio.jarn.backfight.Monster.Monster;
import com.studio.jarn.backfight.Player.Player;
import com.studio.jarn.backfight.R;
import com.studio.jarn.backfight.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import static com.studio.jarn.backfight.MainMenuActivity.PHONE_UUID_SP;

public class FirebaseHelper {

    private static final String sDatabasePostfixGrid = "Grid";
    private static final String sDatabasePostfixPlayers = "PlayerList";
    private static final String getsDatabasePostfixMonsters = "MonsterList";
    private static final String sDatabasePostfixItems = "ItemList";
    private static final String sDatabasePostfixStartGame = "StartGame";
    private static final String sDatabasePostfixRadioGroup = "RadioGroup";
    private static final String sDatabasePostfixNumberPicker = "NumberPicker";
    private static final String sDatabasePostfixRoundCount = "RoundCount";
    public int mRound = 0;
    private FirebaseDatabase mDatabase;
    private String mGameId;
    private String mGameIdRadio;
    private String mGameIdStartGame;
    private String mGameIdNumberPicker;
    private String mGameIdGrid;
    private String mGameIdPlayers;
    private String mGameIdMonsters;
    private String mGameIdItems;

    private FirebaseNewGameListener mFirebaseNewGameListener;
    private FirebaseLobbyListener mFirebaseLobbyListener;
    private FirebaseGameViewListener mFirebaseGameViewListener;
    private FirebaseGameActivityListener mFirebaseGameActivityListener;
    private String mDialogInput;
    private String mGameIdRoundCount;
    private Context mContext;

    public FirebaseHelper(Context context) {
        mDatabase = FirebaseDatabase.getInstance();
        if (context instanceof FirebaseNewGameListener) {
            mFirebaseNewGameListener = (FirebaseNewGameListener) context;
        } else if (context instanceof FirebaseLobbyListener) {
            mFirebaseLobbyListener = (FirebaseLobbyListener) context;
        } else if (context instanceof FirebaseGameActivityListener) {
            mFirebaseGameActivityListener = (FirebaseGameActivityListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Listener");
        }
    }

    public FirebaseHelper(View view) {
        mDatabase = FirebaseDatabase.getInstance();

        if (view instanceof FirebaseGameViewListener) {
            mFirebaseGameViewListener = (FirebaseGameViewListener) view;
        } else {
            throw new RuntimeException(view.toString()
                    + " must implement Listener");
        }
    }


    public void setStandardKey(String gameId, Context context) {
        new SharedPreferencesHelper(context).addGameIdToRecentGameIds(gameId);
        mContext = context;

        mGameId = gameId;
        mGameIdRadio = gameId + sDatabasePostfixRadioGroup;
        mGameIdStartGame = gameId + sDatabasePostfixStartGame;
        mGameIdNumberPicker = gameId + sDatabasePostfixNumberPicker;
        mGameIdGrid = gameId + sDatabasePostfixGrid;
        mGameIdPlayers = gameId + sDatabasePostfixPlayers;
        mGameIdItems = gameId + sDatabasePostfixItems;
        mGameIdRoundCount = gameId + sDatabasePostfixRoundCount;
        mGameIdMonsters = gameId + getsDatabasePostfixMonsters;
    }

    //FirebaseNewGameListener
    public void validateIfGameExist(String input) {
        mDialogInput = input;

        DatabaseReference databaseReference = mDatabase.getReference(mDialogInput);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mFirebaseNewGameListener.gameExist(true, mDialogInput);
                } else {
                    mFirebaseNewGameListener.gameExist(false, mDialogInput);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //mFirebaseLobbyListener
    public void addPlayerToDb(final Player player) {
        final DatabaseReference databaseReference = mDatabase.getReference(mGameId);
        final boolean[] playerFound = {false};
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if ((postSnapshot.getValue(Player.class)).id.equals(player.id))
                        playerFound[0] = true;
                }
                if (!playerFound[0])
                    databaseReference.push().setValue(player);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setupStartGameListener() {
        mDatabase.getReference(mGameIdStartGame).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mFirebaseLobbyListener.startGameClient();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setupWidgetsListener() {
        mDatabase.getReference(mGameIdNumberPicker).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mFirebaseLobbyListener.setNumberPickerValue(Ints.checkedCast(((long) dataSnapshot.getValue())));
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
                mFirebaseLobbyListener.setRadioGroupButton((Ints.checkedCast(((long) dataSnapshot.getValue()))));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void setListViewListener() {
        mDatabase.getReference(mGameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Player> playerList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    playerList.add(postSnapshot.getValue(Player.class));
                }
                mFirebaseLobbyListener.setPlayerList(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public void setNumberPicker(int value) {
        mDatabase.getReference(mGameIdNumberPicker).setValue(value);
    }

    public void setStartGame() {
        mDatabase.getReference(mGameIdStartGame).setValue(true);
    }

    public void setGridType(int value) {
        mDatabase.getReference(mGameIdRadio).setValue(value);
    }


    //FirebaseGameViewListener
    public void setPlayerList(ArrayList<Tuple<Player, Coordinates>> playersWithCoordinates) {
        mDatabase.getReference(mGameIdPlayers).setValue(playersWithCoordinates);
    }

    public void setMonsterList(ArrayList<Tuple<Monster, Coordinates>> monstersWithCoordinates) {
        mDatabase.getReference(mGameIdMonsters).setValue(monstersWithCoordinates);
    }

    public void setupGridListener() {
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
                mFirebaseGameViewListener.setGrid(sizeOfArrayOnFirebase, grid);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
            }
        });
    }

    public void setPlayerListListener() {
        mDatabase.getReference(mGameIdPlayers).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Tuple<Player, Coordinates>> playerList = new ArrayList<>();

                GenericTypeIndicator<Tuple<Player, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<Player, Coordinates>>() {
                };

                /*
                 * Because we use inheritance firebase can not handle that the list is of GameItem,
                 * and that the item can be of ItemWeapon type. Therefore we have go through every
                 * PlayerTuple -> Player -> ItemList -> Item
                 * in order to keep all properties. If not, weapons would lose their damage.
                 */

                for (DataSnapshot playerTuple : dataSnapshot.getChildren()) {
                    ArrayList<GameItem> itemList = new ArrayList<>();
                    for (DataSnapshot player : playerTuple.getChildren()) {
                        for (DataSnapshot playerProperties : player.getChildren()) {
                            for (DataSnapshot playerItems : playerProperties.getChildren()) {
                                ItemWeapon item = playerItems.getValue(ItemWeapon.class);
                                itemList.add(item);
                            }
                        }
                    }

                    Tuple<Player, Coordinates> player = playerTuple.getValue(genericTypeIndicator);
                    player.mGameObject.PlayerItems = itemList;
                    playerList.add(player);

                }
                mFirebaseGameViewListener.setPlayerList(playerList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public void setMonsterListListener() {
        mDatabase.getReference(mGameIdMonsters).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Tuple<Monster, Coordinates>> monsterList = new ArrayList<>();

                GenericTypeIndicator<Tuple<Monster, Coordinates>> monsterGenericTypeIndicator = new GenericTypeIndicator<Tuple<Monster, Coordinates>>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    monsterList.add(postSnapshot.getValue(monsterGenericTypeIndicator));
                }
                mFirebaseGameViewListener.setMonsterList(monsterList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public void increaseRoundCount() {

        //http://stackoverflow.com/questions/40405181/firebase-database-increment-an-int
        mDatabase.getReference(mGameIdRoundCount).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Long value = mutableData.getValue(Long.class);
                if (value == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(value + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                Log.d("", "transaction:onComplete:" + databaseError);
            }
        });
    }

    public void setRoundCountListener(Context context) {
        if (context instanceof FirebaseGameActivityListener) {
            mFirebaseGameActivityListener = (FirebaseGameActivityListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement Listener");
        }

        mDatabase.getReference(mGameIdRoundCount).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    mRound = dataSnapshot.getValue(int.class);
                    mFirebaseGameActivityListener.setRound(mRound);
                    mFirebaseGameActivityListener.setActionCounter(3);
                    mFirebaseGameActivityListener.sendNotificationNewRound();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }


    public void setGrid(List<List<Tile>> list) {
        mDatabase.getReference(mGameIdGrid).setValue(list);
    }


    public void setItemList(ArrayList<Tuple<GameItem, Coordinates>> itemsWithCoordinates) {
        mDatabase.getReference(mGameIdItems).setValue(itemsWithCoordinates);
    }

    private Tuple<GameItem, Coordinates> convert(Tuple<ItemWeapon, Coordinates> tuple) {
        return new Tuple<GameItem, Coordinates>(tuple.mGameObject, tuple.mCoordinates);
    }

    public void setItemListListener() {
        mDatabase.getReference(mGameIdItems).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String item = new Gson().toJson(dataSnapshot.getValue());
                Log.d("Test", item);

                GenericTypeIndicator<Tuple<ItemWeapon, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<ItemWeapon, Coordinates>>() {
                };
                ArrayList<Tuple<GameItem, Coordinates>> itemList = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    itemList.add(convert(postSnapshot.getValue(genericTypeIndicator)));
                }

                mFirebaseGameViewListener.setItemList(itemList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }

    //Used for setting the action counter to the correct value if people rejoin
    public void getActionCount() {
        mDatabase.getReference(mGameIdPlayers).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Tuple<Player, Coordinates>> genericTypeIndicator = new GenericTypeIndicator<Tuple<Player, Coordinates>>() {
                };
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Tuple<Player, Coordinates> tuple = (postSnapshot.getValue(genericTypeIndicator));
                    String playerId = mContext.getSharedPreferences(
                            mContext.getResources().getString(R.string.all_sp_name), Context.MODE_PRIVATE).getString(PHONE_UUID_SP, "");
                    if (tuple.mGameObject.id.equals(playerId))
                        mFirebaseGameActivityListener.setActionCounter(tuple.mGameObject.mActionsRemaining);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w("", "Failed to read value.", databaseError.toException());
            }
        });
    }
}
