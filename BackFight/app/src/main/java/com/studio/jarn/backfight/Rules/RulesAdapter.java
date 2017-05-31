package com.studio.jarn.backfight.Rules;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.studio.jarn.backfight.R;

import java.util.ArrayList;

// Inspired by FragmentsArnieMovies by Kasper
class RulesAdapter extends BaseAdapter implements Filterable {
    private final Context mContext;
    private final ArrayList<Rules> mRules;
    private ArrayList<Rules> mFilteredRules;

    RulesAdapter(Context c, ArrayList<Rules> rulesList) {
        mRules = rulesList;
        mFilteredRules = rulesList;
        mContext = c;

    }

    @Override
    public int getCount() {
        if (mFilteredRules != null) {
            return mFilteredRules.size();
        } else {
            return 0;
        }
    }

    @Override
    public Rules getItem(int position) {
        if (mFilteredRules != null) {
            return mFilteredRules.get(position);
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
            LayoutInflater RulesInflator = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = RulesInflator.inflate(R.layout.list_item, null);
        }

        Rules rules = mFilteredRules.get(position);
        if (rules != null) {
            TextView txtTitle = (TextView) convertView.findViewById(R.id.listItem_tv_type);
            txtTitle.setText(rules.mRulesName);

        }
        return convertView;
    }

    @Override
    public android.widget.Filter getFilter() {
        return new android.widget.Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                //If there's nothing to filter on, return the original data for your list
                if (constraint == null || constraint.length() == 0) {
                    results.values = mRules;
                    results.count = mRules.size();
                } else {
                    ArrayList<Rules> filteredResultsData = new ArrayList<>();

                    for (Rules rule : mRules) {
                        mRules.get(1).getRulesName();
                        rule.getRulesName();
                        if (rule.getRulesName().toLowerCase().startsWith(constraint.toString())) {
                            filteredResultsData.add(rule);
                        }
                    }

                    results.values = filteredResultsData;
                    results.count = filteredResultsData.size();
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredRules = (ArrayList<Rules>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
