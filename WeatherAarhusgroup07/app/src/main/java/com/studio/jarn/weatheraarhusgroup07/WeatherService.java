package com.studio.jarn.weatheraarhusgroup07;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.studio.jarn.weatheraarhusgroup07.ApiDtos.Example;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class WeatherService extends Service {
    private AsyncTask timerTask;
    private final IBinder mBinder = new LocalBinder();
    private WeatherDbHelper mDbHelper;
    private int UpdateInterval = 30; // min

    public class LocalBinder extends Binder {
        WeatherService getService() {
            return WeatherService.this;
        }
    }

    public WeatherService() {
        Log.d("WeatherService", "WeatherService: Created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mDbHelper = new WeatherDbHelper(this);

        timerTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                Log.d("WeatherService", "doInBackground: Start Timer");
                tick(UpdateInterval * 1000 * 60 /*min*/);
                return null;
            }
        };

        timerTask.execute();

        return  START_STICKY;
    }

    private void tick(int time) {
        try {
            Thread.sleep(time);

            Log.d("Tick", "Ticking!");
            NewInfoReceivedBroadcast();

            tick(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void NewInfoReceivedBroadcast() {
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent broadcastMessage = new Intent("timerTick");
        broadcaster.sendBroadcast(broadcastMessage);
    }

    @Override
    public void onDestroy() {
        timerTask.cancel(true);
        Log.d("WeatherService", "onDestroy: Called");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("WeatherService", "onBind: Called");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("WeatherService", "onUnbind: Called");
        return super.onUnbind(intent);
    }

    /////////// Weather Methods /////////////
    public List<WeatherInfo> getPastWeather(){
        return mDbHelper.getAllWeatherInfo();
    }
    public WeatherInfo getCurrentWeather() {return mDbHelper.getWeatherInfo();}

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
                            String JsonResponse = main.getString("temp");
                            Log.d("From VolleyRequest: ", JsonResponse);

                            Gson gson = new Gson();
                            Example example = gson.fromJson(response.toString(), Example.class);
                            Log.d("GSON: ", example.getMain().getTemp().toString());
                            //adding 2 hours for gtm +1 (Denmark) and summertime
                            WeatherInfo weather = new WeatherInfo(0, example.getWeather().get(0).getDescription(), getTempInCelsius(example.getMain().getTemp()), example.getDt() + 60*60*2);
                            // Check if current weather has the same timestamp.
                            WeatherInfo test = mDbHelper.getWeatherInfo();
                            if(mDbHelper.getWeatherInfo().getTime() != weather.getTime()) {
                                mDbHelper.AddWeatherInfo(weather);
                            }

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

        NewInfoReceivedBroadcast();
    }

    private double getTempInCelsius(double k){
        return k - 273.15;
    }
}
