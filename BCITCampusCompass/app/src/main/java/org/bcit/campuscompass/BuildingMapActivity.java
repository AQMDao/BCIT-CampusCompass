package org.bcit.campuscompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.button.MaterialButton;

// this activity is the end point of our implementation for the application
// for developers: used to debug and experiment implementations at the building level
// for users: view and interact with the building selected from the campus map activity and its floors
public class BuildingMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    // local storage data
    MapData SW01_1 = new MapData("SW01_1", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_2 = new MapData("SW01_2", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_3 = new MapData("SW01_3", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_4 = new MapData("SW01_4", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData[] SW01 = {SW01_1, SW01_2, SW01_3, SW01_4};
    /*
    MapData SW02_1;
    MapData SW02_2;
    MapData SW02_3;
    MapData[] SW02 = {SW02_1, SW02_2, SW02_3};
    MapData SW03_1;
    MapData SW03_2;
    MapData SW03_3;
    MapData SW03_4;
    MapData[] SW03 = {SW03_1, SW03_2, SW03_3, SW03_4};
    MapData SW05_1;
    MapData SW05_2;
    MapData[] SW05 = {SW05_1, SW05_2};
    MapData SW09_1;
    MapData SW09_2;
    MapData[] SW09 = {SW09_1, SW09_2};
    */

    String[] burnabyBuildingList = {
            SW01_1.getMapName().split("_")[0]
    };
    // other variables
    List<MapData> buildingMaps;
    MapData floorMap;
    String floorSelected;
    Intent openedBuildingActivity;
    // location variables
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallBack;
    SettingsClient settingsClient;
    LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;
    Location lastLocation;
    // check for location permissions from user
    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(BuildingMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(BuildingMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(BuildingMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(BuildingMapActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    // start the location tracking
    public void startLocationUpdating() {
        // get the fused location provider client for the campus map activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(BuildingMapActivity.this);
        // get the client settings for the location services for the campus map activity
        settingsClient = LocationServices.getSettingsClient(BuildingMapActivity.this);
        // create a location callback that runs when the location is determined
        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                receiveLocation(locationResult);
            }
        };
        // configure settings for the actual location request including accuracy, minimum time/distance interval, etc.
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL).setMinUpdateIntervalMillis(500).setMinUpdateDistanceMeters(1).setWaitForAccurateLocation(true).build();
        // create a location request builder
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        // add the request to the builder
        builder.addLocationRequest(locationRequest);
        // set the location request settings with what the builder built
        locationSettingsRequest = builder.build();
        // start getting the device location according to the above settings
        startLocationUpdates();
    }
    // stop location updates
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
    // start location updating depending the outcome of requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(BuildingMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        startLocationUpdating();
                    }
                }
                break;
        }
    }
    // start location updating
    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(locationSettingsResponse -> {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper());
        }).addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
        });
    }
    // stop location updating
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallBack).addOnCompleteListener(task -> {});
    }
    private void receiveLocation(LocationResult locationResult) {
        lastLocation = locationResult.getLastLocation();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_map);
        // handle location permissions and set up user location tracking
        checkLocationPermissions();
        startLocationUpdating();
        // get the intent used to start the building activity
        openedBuildingActivity = getIntent();
        // get the building selected from the campus activity
        String buildingSelected = openedBuildingActivity.getStringExtra("buildingSelect");
        // clear any existing maps in the building maps list
        buildingMaps = new ArrayList<MapData>();
        // set the starting floor
        floorSelected = "1";
        // fill the building map list based on the building selected and the starting floor map
        MaterialButtonToggleGroup floorSelectGroup = findViewById(R.id.floor_select_group);
        switch(Objects.requireNonNull(buildingSelected)) {
            case "SW01":
                buildingMaps = Arrays.asList(SW01);
                floorMap = buildingMaps.get(Integer.parseInt(floorSelected) - 1);
                for(int i = 1; i <= buildingMaps.size(); i++) {
                    MaterialButton floorButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    floorButton.setText(String.valueOf(i));
                    floorButton.setBackgroundColor(getResources().getColor(R.color.white));
                    floorSelectGroup.addView(floorButton);
                }
                break;
            case "SW02":
                break;
            case "SW03":
                break;
            case "SW05":
                break;
            case "SW09":
                break;
            default:
                break;
        }
        GoogleMapOptions buildingMapOptions = new GoogleMapOptions();
        SupportMapFragment buildingMapFragment = SupportMapFragment.newInstance(buildingMapOptions);
        getSupportFragmentManager().beginTransaction().add(R.id.building_map_fragment, buildingMapFragment).commit();
        buildingMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // set padding for our own interface
        googleMap.setPadding(0, 150, 0, 0);
        // move to selected building
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(floorMap.getMapBounds(), 0));
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().bearing(90).zoom(googleMap.getCameraPosition().zoom).target(floorMap.getMapCenter()).build()));
    }
}