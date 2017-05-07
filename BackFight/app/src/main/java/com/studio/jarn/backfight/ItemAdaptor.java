package com.studio.jarn.backfight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.jarn.backfight.Items.GameItem;

import java.util.ArrayList;

class ItemAdaptor extends BaseAdapter {

    private Context mContext;
    private ArrayList<GameItem> mItems;
    private GameItem mItem;

    ItemAdaptor(Context c, ArrayList<GameItem> itemList) {
        this.mContext = c;
        this.mItems = itemList;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).Id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater itemInflator = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = itemInflator.inflate(R.layout.items_and_stats_list_item, null);
        }

        mItem = mItems.get(position);
        if (mItem != null) {
            TextView tvTitle = (TextView) convertView.findViewById(R.id.fragment_list_item_txt_title);
            tvTitle.setText(mItem.Title);

            ImageView imgView = (ImageView) convertView.findViewById(R.id.fragment_list_item_img);
            imgView.setImageResource(mItem.Image);
        }

        return convertView;
    }
}
