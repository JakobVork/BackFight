package com.studio.jarn.backfight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// Inspired by FragmentsArnieMovies by Kasper
class RulesAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<Rules> mRules;
    private Rules rules = null;

    RulesAdapter(Context c, ArrayList<Rules> rulesList) {
        mRules = rulesList;
        mcontext = c;
    }

    @Override
    public int getCount() {
        if (mRules != null) {
            return mRules.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if (mRules != null) {
            return mRules.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater RulesInflator = (LayoutInflater) this.mcontext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = RulesInflator.inflate(R.layout.list_item, null);
        }

        rules = mRules.get(position);
        if (rules != null) {
            TextView txtTitle = (TextView) convertView.findViewById(R.id.listItem_tv_type);
            txtTitle.setText(rules.rulesName);

        }
        return convertView;
    }
}
