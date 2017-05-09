package com.studio.jarn.weatheraarhusgroup07;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean mBound = false;
    WeatherService mService;
    ListView listView;
    WeatherAdapter adapter;
    WeatherInfo info;
    List<WeatherInfo> weatherInfos;

    private final int REQUEST_PERMISSIONS_INTERNET = 1;
    private final int REQUEST_PERMISSIONS_ACCESS_NETWORK_STATE = 2;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            WeatherService.LocalBinder binder = (WeatherService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermissions();


        // Setup FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mService.VolleyRequest();
                updateUI();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.NewWeatherinfoRecieved), Toast.LENGTH_SHORT).show();
            }
        });

        // Setup background service
        startBackgroundService();
        bindToService();

        // Setup list
        setupListView();

        // Setup broadcaster receiver.
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUI();
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(receiver, new IntentFilter("timerTick"));


        if(savedInstanceState != null){

        }

    }

    private void updateUI() {
        TextView txtWeatherInfo = (TextView) findViewById(R.id.content_main_tv_current_weather);
        weatherInfos = mService.getPastWeather();

        // use newest entry on txtWeatherInfo
        if(!weatherInfos.isEmpty()){
            info  = weatherInfos.get(0);
            txtWeatherInfo.setText(info.Description + "\n" + String.format("%.1f", info.Temp) + "\u2103");
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoWeatherInfo), Toast.LENGTH_SHORT).show();
        }

        // Assign list to have all entries
        adapter.clear();
        adapter.addAll(weatherInfos);
    }

    private void bindToService() {
        Intent startBoundedService = new Intent(getApplicationContext(), WeatherService.class);
        bindService(startBoundedService, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void startBackgroundService() {
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    private void setupListView(){
        ArrayList<WeatherInfo> WeatherClasses = new ArrayList<WeatherInfo>(){};
        // Create the adapter to convert the array to views
        adapter = new WeatherAdapter(this, WeatherClasses);
        // Attach the adapter to a ListView

        listView = (ListView) findViewById(R.id.content_main_lw_weather);

        listView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSIONS_INTERNET:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(this, getResources().getString(R.string.PermissionNotGranted), Toast.LENGTH_SHORT).show();
                }

                break;
            case REQUEST_PERMISSIONS_ACCESS_NETWORK_STATE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(this, getResources().getString(R.string.PermissionNotGranted), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_PERMISSIONS_INTERNET);
        }
        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    REQUEST_PERMISSIONS_ACCESS_NETWORK_STATE);
        }
    }
}
