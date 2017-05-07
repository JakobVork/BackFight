package com.studio.jarn.weatheraarhusgroup07;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.Weather;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ander on 07-05-2017.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "WeatherReader.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long AddWeatherInfo(WeatherInfo t){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(WeatherReaderContract.TaskEntry.COLUMN_NAME_DESC, t.Description);
        values.put(WeatherReaderContract.TaskEntry.COLUMN_NAME_TEMP, t.Temp);
        values.put(WeatherReaderContract.TaskEntry.COLUMN_NAME_TIME, t.getTime());

        // Insert the new row, returning the primary key value of the new row
        long returnValue = db.insert(WeatherReaderContract.TaskEntry.TABLE_NAME, null, values);
        Log.d("Database: ", "Wrote WeatherInfo to Db");
        return returnValue;
    }


    public List<WeatherInfo> getAllWeatherInfo(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + WeatherReaderContract.TaskEntry.TABLE_NAME, null);
        try {
            List itemIds = new ArrayList<>();
            while(cursor.moveToNext()) {

                long id = cursor.getLong( cursor.getColumnIndex(WeatherReaderContract.TaskEntry.COLUMN_NAME_ID) );
                String desc = cursor.getString( cursor.getColumnIndex(WeatherReaderContract.TaskEntry.COLUMN_NAME_DESC) );
                double temp = cursor.getDouble( cursor.getColumnIndex(WeatherReaderContract.TaskEntry.COLUMN_NAME_TEMP) );
                int time = cursor.getInt( cursor.getColumnIndex(WeatherReaderContract.TaskEntry.COLUMN_NAME_TIME) );

                itemIds.add(new WeatherInfo(id, desc, temp, time));
            }
            cursor.close();
            return  itemIds;
        }
        catch (Exception e) {
            return null;
        }
    }



    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + WeatherReaderContract.TaskEntry.TABLE_NAME + " (" +
                    WeatherReaderContract.TaskEntry.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    WeatherReaderContract.TaskEntry.COLUMN_NAME_DESC + " TEXT," +
                    WeatherReaderContract.TaskEntry.COLUMN_NAME_TEMP + " REAL," +
                    WeatherReaderContract.TaskEntry.COLUMN_NAME_TIME + " INTEGER)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + WeatherReaderContract.TaskEntry.TABLE_NAME;
}
