package ca.bcit.campuscompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    public static final long DEFAULT_INTERVAL_MILLIS = 30000;
    public static final int DEFAULT_FAST_INTERVAL_MILLIS = 5000;
    public static final int MAX_WAIT_TIME = 100;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    //References to UI elements
    TextView tv_lat, tv_lon, tv_altitude, tv_accuracy, tv_speed, tv_sensor, tv_updates, tv_address;
    Switch sw_locationsupdates, sw_gps;

    //Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    //Location request is a config file for all settings related to fusedlocationproviderclient
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findview();
        locationRequestSetup();
        SetupOnClickListener();
        updateGPS();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "permit me u dweeb", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


    private void findview(){
        //UI values
        tv_lon = findViewById(R.id.tv_lon);
        tv_lat = findViewById(R.id.tv_lat);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_speed = findViewById(R.id.tv_speed);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        sw_gps = findViewById(R.id.sw_gps);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
    }

    //properties of LocationRequest
    private void locationRequestSetup(){
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, DEFAULT_INTERVAL_MILLIS)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(DEFAULT_FAST_INTERVAL_MILLIS)
                .setMaxUpdateDelayMillis(MAX_WAIT_TIME)
                .build();
    }

    private void updateGPS() {
        // get permissions from the user to track GPS
        // get the current location from the fused client
        //update the UI - i.e. set all properties in their associated text view items

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. Put the values of location. XXX into the UI components
                    updateUIValues(location);
                }
            });
        }
        else {
            //permissions not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);

            }

        }
    }

    private void updateUIValues(Location location) {

        //update all of the text view objects with a new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lat.setText(String.valueOf(location.getLongitude()));
        tv_lat.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        }
        else {
            tv_altitude.setText("Not Available");
        }

        if (location.hasSpeed()) {
            tv_altitude.setText(String.valueOf(location.getSpeed()));
        }
        else {
            tv_altitude.setText("Not Available");
        }
    }

    private void SetupOnClickListener(){
        /// TODO: possibly look for a way to change the location request priority without copy pasting the whole builder constructor

        sw_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()){
                    // most accurate - use GPS (created a new locationrequest since it's immutable)
                    locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, DEFAULT_INTERVAL_MILLIS)
                            .setWaitForAccurateLocation(false)
                            .setMinUpdateIntervalMillis(DEFAULT_FAST_INTERVAL_MILLIS)
                            .setMaxUpdateDelayMillis(MAX_WAIT_TIME)
                            .build();

                    tv_sensor.setText("Using GPS sensors");
                }

                else {
                    locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, DEFAULT_INTERVAL_MILLIS)
                            .setWaitForAccurateLocation(false)
                            .setMinUpdateIntervalMillis(DEFAULT_FAST_INTERVAL_MILLIS)
                            .setMaxUpdateDelayMillis(MAX_WAIT_TIME)
                            .build();
                    tv_sensor.setText("Using Towers + WIFI");
                }
            }
        });
    }
}