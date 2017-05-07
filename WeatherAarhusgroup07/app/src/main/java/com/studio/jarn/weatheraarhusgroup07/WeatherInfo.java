package com.studio.jarn.weatheraarhusgroup07;

import com.example.Weather;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by ander on 07-05-2017.
 */

public class WeatherInfo {
    public long Id;
    public String Description;
    public double Temp;
    public Date Timestamp;

    WeatherInfo(long id, String desc, double temp, int time){
        Id = id;
        Description = desc;
        Temp = temp;
        //http://stackoverflow.com/questions/3371326/java-date-from-unix-timestamp
        Timestamp = new Date((long)time*1000);
    }

    long getTime(){
        return Timestamp.getTime()/1000;
    }
}
