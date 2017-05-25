package com.studio.jarn.backfight.NewGame;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studio.jarn.backfight.Firebase.FirebaseHelper;
import com.studio.jarn.backfight.Firebase.FirebaseNewGameListener;
import com.studio.jarn.backfight.Lobby.LobbyActivity;
import com.studio.jarn.backfight.R;

public class NewGameActivity extends AppCompatActivity implements FirebaseNewGameListener {

    Button mBtnBack;
    Button mBtnCreate;
    Button mBtnJoin;
    String mDialogText;
    FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        hideActionBar();
        initButtons();

        mFirebaseHelper = new FirebaseHelper(this);
    }

    // Find the buttons in the layoutfile and call to make OnClickListener on them
    private void initButtons() {
        mBtnBack = (Button) findViewById(R.id.activity_newGame_btn_back);
        mBtnCreate = (Button) findViewById(R.id.activity_newGame_btn_create);
        mBtnJoin = (Button) findViewById(R.id.activity_newGame_btn_join);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLobby(v);
            }
        });

        mBtnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayEnterIdDialog();
            }
        });
    }


    // Display Dialog to enter game ID
    private void DisplayEnterIdDialog() {
        //Source: http://stackoverflow.com/questions/10903754/input-text-dialog-android
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.newGame_dialogTitle);

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        input.setText(mDialogText);
        input.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);

        builder.setView(input);
        builder.setMessage(R.string.newGame_dialogMessage);
        builder.setPositiveButton(R.string.newGame_dialogBtnPositive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mFirebaseHelper.validateIfGameExist(input.getText().toString());

            }
        });
        builder.setNegativeButton(R.string.newGame_dialogBtnNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    //Creates a lobby players can join before starting a game
    private void createLobby(View view) {
        Intent lobbyIntent = new Intent(this, LobbyActivity.class);
        lobbyIntent.putExtra(getString(R.string.EXTRA_HOST), true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Bundle bundle;
            bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
            startActivity(lobbyIntent, bundle);
        } else
        startActivity(lobbyIntent);
    }

    // Go back to main menu
    private void backToMainMenu() {
        finish();
    }

    //Hides the actionbar
    private void hideActionBar() {
        ActionBar mActionBar = getSupportActionBar();
        if(mActionBar != null)
            mActionBar.hide();
    }

    @Override
    public void gameExist(boolean test, String input) {
        if (test) {
            Intent lobbyIntent = new Intent(NewGameActivity.this, LobbyActivity.class);
            lobbyIntent.putExtra(getString(R.string.EXTRA_HOST), false);
            lobbyIntent.putExtra(getString(R.string.EXTRA_UUID), input);

            startActivity(lobbyIntent);
        } else {
            Toast.makeText(NewGameActivity.this, R.string.newGame_dialogErrorMessage, Toast.LENGTH_SHORT).show();
            mDialogText = input;
        }

    }
}
