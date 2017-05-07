package com.studio.jarn.weatheraarhusgroup07;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.Example;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class WeatherService extends Service {
    private final IBinder mBinder = new LocalBinder();
    public String Test;
    WeatherDbHelper mDbHelper;
    private Context mContext;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDbHelper = new WeatherDbHelper(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        WeatherService getService() {
            // Return this instance of LocalService so clients can call public methods
            return WeatherService.this;
        }
    }

    public List<WeatherInfo> getAllWeatherInfos(){
        return mDbHelper.getAllWeatherInfo();
    }


    public void VolleyRequest() {
        String url = "http://api.openweathermap.org/data/2.5/weather?id=2624652&APPID=2f340ec18fa5dcbc72f44bb06fbfd563";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response.toString());
                            JSONObject main = reader.getJSONObject("main");
                            Test = main.getString("temp");
                            Log.d("From VolleyRequest: ", Test);

                            Gson gson = new Gson();
                            Example example = gson.fromJson(response.toString(), Example.class);
                            Log.d("GSON: ", example.getMain().getTemp().toString());
                                                                                                                                                    //adding 2 hours for gtm +1 (Denmark) and summertime
                            mDbHelper.AddWeatherInfo(new WeatherInfo(0, example.getWeather().get(0).getDescription(), getTempInCelsius(example.getMain().getTemp()), example.getDt() + 60*60*2));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("", "Error: " + error.getMessage());

                    }
                });
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        //Adding request to the queue
        requestQueue.add(jsObjRequest);

    }

    private double getTempInCelsius(double k){
        return k - 273.15;
    }
}
