package com.studio.jarn.weatheraarhusgroup07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ander on 07-05-2017.
 */

public class WeatherAdapter extends ArrayAdapter<WeatherInfo> {

    SimpleDateFormat sdf;

    public WeatherAdapter(Context context, ArrayList<WeatherInfo> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WeatherInfo WeatherInfoClass = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_weatherclass, parent, false);
        }
        // Lookup view for data population
        TextView tv_desc = (TextView) convertView.findViewById(R.id.item_weatherclass_tv_desc);
        TextView tv_date = (TextView) convertView.findViewById(R.id.item_weatherclass_tv_date);
        TextView tv_temp = (TextView) convertView.findViewById(R.id.item_weatherclass_tv_temp);
        // Populate the data into the template view using the data object
        tv_desc.setText(WeatherInfoClass.Description);
        DecimalFormat df = new DecimalFormat("#.0");

        tv_temp.setText( df.format(WeatherInfoClass.Temp)+"\u00b0C");


        http://stackoverflow.com/questions/8745297/want-current-date-and-time-in-dd-mm-yyyy-hhmmss-ss-format

        sdf = new SimpleDateFormat("dd-MM yyyy HH:mm");
        String strDate = sdf.format(WeatherInfoClass.Timestamp);
        tv_date.setText(strDate);


        // Return the completed view to render on screen
        return convertView;
    }
}
