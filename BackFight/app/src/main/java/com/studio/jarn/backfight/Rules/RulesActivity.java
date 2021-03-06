package com.studio.jarn.backfight.Rules;

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

import com.studio.jarn.backfight.R;

import java.util.ArrayList;

public class RulesActivity extends AppCompatActivity {

    private Button mBtnBack;
    private ListView mLwList;
    private RulesAdapter mAdapter;
    private EditText mSearch;
    private ArrayList<Rules> mRules;
    private TextView mTitle;
    private TextView mDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        hideActionBar();
        initViewItems();
        getRules();
    }

    // Set the details for the rule selected
    private void onRuleSelected(int position) {

        Rules selectedRule = mAdapter.getItem(position);
        if (selectedRule != null) {
            mTitle.setText(selectedRule.mRulesName);

            String temp = "";
            for (int i = 0; i < selectedRule.mRulesDescription.size(); i++) {
                temp = temp + selectedRule.mRulesDescription.get(i) + System.getProperty("line.separator");
            }
            mDetails.setText(temp);
        }
    }

    // Get all rules from raw files
    private void getRules() {
        mRules = new LoadRules(this).getRules();
        setRulesInListView();
    }

    // Inspiration from http://www.androidhive.info/2012/09/android-adding-search-functionality-to-listview/
    private void setRulesInListView() {
        mAdapter = new RulesAdapter(this, mRules);
        mLwList.setAdapter(mAdapter);

        mLwList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onRuleSelected(position);
            }
        });

        mSearch.addTextChangedListener(new TextWatcher() {
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


    // Find the view items in the layout file and call to make details on them
    private void initViewItems() {
        mBtnBack = (Button) findViewById(R.id.activity_rules_btn_back);
        mTitle = (TextView) findViewById(R.id.activity_rules_tv_title);
        mDetails = (TextView) findViewById(R.id.activity_rules_tv_details);
        mLwList = (ListView) findViewById(R.id.activity_rules_lw_listOfRules);
        mSearch = (EditText) findViewById(R.id.activity_rules_et_search);

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

