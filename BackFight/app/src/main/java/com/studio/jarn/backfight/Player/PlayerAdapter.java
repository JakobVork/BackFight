package com.studio.jarn.backfight.Player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.jarn.backfight.R;

import java.util.ArrayList;

public class PlayerAdapter extends ArrayAdapter<Player> {


    public PlayerAdapter(Context context, ArrayList<Player> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Player PlayerClass = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_playerclass, parent, false);
        }
        // Lookup view for data population
        TextView tv_name = (TextView) convertView.findViewById(R.id.item_playerclass_tv_name);
        ImageView iw_image = (ImageView) convertView.findViewById(R.id.item_playerclass_iv_icon);
        // Populate the data into the template view using the data object
        assert PlayerClass != null;
        tv_name.setText(PlayerClass.Name);
        iw_image.setBackgroundResource(PlayerClass.Figure);
        // Return the completed view to render on screen
        return convertView;
    }
}
