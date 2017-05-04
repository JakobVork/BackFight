package com.studio.jarn.backfight;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.studio.jarn.backfight.Items.gameItem;

import java.util.ArrayList;

class itemAdaptor extends BaseAdapter{

    private Context context;
    private ArrayList<gameItem> items;
    private gameItem item;

    itemAdaptor(Context c, ArrayList<gameItem> itemList) {
        this.context = c;
        this.items = itemList;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater itemInflator = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = itemInflator.inflate(R.layout.items_and_stats_list_item, null);
        }

        item = items.get(position);
        if(item != null) {
            TextView txtTitle = (TextView) convertView.findViewById(R.id.fragment_items_and_stats_txt_title);
            txtTitle.setText(item.Title);

            ImageView imgView = (ImageView) convertView.findViewById(R.id.fragment_items_and_stats_img);
            imgView.setImageResource(item.image);
        }

        return convertView;
    }
}
