package com.studio.jarn.weatheraarhusgroup07;

import android.provider.BaseColumns;

/**
 * Created by Banders on 23-04-2017.
 */

public class WeatherReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private WeatherReaderContract() {}

    /* Inner class that defines the table contents */
    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_NAME_ID= "id";
        public static final String COLUMN_NAME_DESC = "desc";
        public static final String COLUMN_NAME_TEMP = "temp";
        public static final String COLUMN_NAME_TIME = "time";
    }
}
