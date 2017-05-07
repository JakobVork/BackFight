package com.studio.jarn.backfight;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RulesActivity extends AppCompatActivity {

    Button mBtnBack;
    ListView mLwList;
    RulesAdapter mAdapter;
    EditText mSeach;
    ArrayList<Rules> mRules;
    TextView mTitle;
    TextView mDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        hideActionBar();
        initViewItems();
        getRules();
    }

    // Set the details for the rule selected
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onMovieSelected(int position) {
        Rules selectedRule = mAdapter.getItem(position);
        if (selectedRule != null) {
            mTitle.setText(selectedRule.rulesName);

            String temp = "";
            for (int i = 0; i < selectedRule.rulesDescription.size(); i++) {
                temp = temp + selectedRule.rulesDescription.get(i) + System.lineSeparator();
            }
            mDetails.setText(temp);
        }
    }

    // Get all rules from raw files
    private void getRules() {

        ILoadRules loader = new LoadRules(this);
        mRules = loader.getRules();

        if (mRules == null) {
            //if null create fake list
            mRules = new ArrayList<>();
        }

        setRulesInListView();
    }

    // Inspration from http://www.androidhive.info/2012/09/android-adding-search-functionality-to-listview/
    private void setRulesInListView() {
        mAdapter = new RulesAdapter(this, mRules);
        mLwList.setAdapter(mAdapter);

        mLwList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onMovieSelected(position);
            }
        });

        mSeach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Must be empty
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Must be Empty
            }
        });
    }


    // Find the view items in the layoutfile and call to make details on them
    private void initViewItems() {
        mBtnBack = (Button) findViewById(R.id.activity_rules_btn_back);
        mTitle = (TextView) findViewById(R.id.activity_rules_tv_title);
        mDetails = (TextView) findViewById(R.id.activity_rules_tv_details);
        mLwList = (ListView) findViewById(R.id.activity_rules_lw_listOfRules);
        mSeach = (EditText) findViewById(R.id.activity_rules_et_search);

        initViewDetails();
    }

    private void initViewDetails() {
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainMenu();
            }
        });
        mDetails.setMovementMethod(new ScrollingMovementMethod());
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
}

