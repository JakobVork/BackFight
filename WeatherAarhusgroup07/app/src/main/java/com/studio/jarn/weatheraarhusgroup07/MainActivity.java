package com.studio.jarn.weatheraarhusgroup07;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean mBound = false;
    WeatherService mService;
    String Test;
    ListView listView;
    WeatherAdapter adapter;

    private final int REQUEST_PERMISSIONS_INTERNET = 1;
    private final int REQUEST_PERMISSIONS_ACCESS_NETWORK_STATE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkPermissions();

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupListView();
            }
        });



        ServiceConnection mConnection = new ServiceConnection() {
            // Called when the connection with the service is established
            public void onServiceConnected(ComponentName className, IBinder service) {
                // Because we have bound to an explicit
                // service that is running in our own process, we can
                // cast its IBinder to a concrete class and directly access it.
                WeatherService.LocalBinder binder = (WeatherService.LocalBinder) service;
                mService = binder.getService();
                mBound = true;
            }

            // Called when the connection with the service disconnects unexpectedly
            public void onServiceDisconnected(ComponentName className) {
                mBound = false;
            }
        };

        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        /*startService(intent);*/ //Start service
         // Bind service

        Button btnGetWeath = (Button) findViewById(R.id.content_main_btn_getWeather);
        btnGetWeath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.VolleyRequest();
            }
        });



    }

    private void setupListView(){
        ArrayList<WeatherInfo> WeatherClasses = new ArrayList<WeatherInfo>(){};
        // Create the adapter to convert the array to views
        adapter = new WeatherAdapter(this, WeatherClasses);
        // Attach the adapter to a ListView

        listView = (ListView) findViewById(R.id.content_main_lw_weather);

        listView.setAdapter(adapter);
        /*adapter.add(new WeatherInfo(12, "Test", 23.3 , 23));*/

        adapter.addAll(mService.getAllWeatherInfos());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSIONS_INTERNET:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }

                break;
            case REQUEST_PERMISSIONS_ACCESS_NETWORK_STATE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                }
                else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
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
