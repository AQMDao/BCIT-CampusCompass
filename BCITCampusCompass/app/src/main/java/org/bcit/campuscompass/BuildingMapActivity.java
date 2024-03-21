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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
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
    MapData SW01_1 = new MapData("SW01_1", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 224.0909090909f, 0f);
    MapData SW01_2 = new MapData("SW01_2", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 224.0909090909f, 0f);
    MapData SW01_3 = new MapData("SW01_3", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 224.0909090909f, 0f);
    MapData SW01_4 = new MapData("SW01_4", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 224.0909090909f, 0f);
    MapData[] SW01 = {SW01_1, SW01_2, SW01_3, SW01_4};
    // variables
    List<MapData> buildingMaps;
    MapData floorMap;
    Intent openedBuildingActivity;
    // location variables
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallBack;
    SettingsClient settingsClient;
    LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;
    Location lastLocation;
    // google map variable
    GoogleMap buildingMap;
    // ground overlay variable
    GroundOverlay mapOverlay;
    // material toggle button group variable
    MaterialButtonToggleGroup floorSelectGroup;
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
        // fill the building map list based on the building selected and the starting floor map
        floorSelectGroup = findViewById(R.id.floor_select_group);
        switch(Objects.requireNonNull(buildingSelected)) {
            case "SW01":
                buildingMaps = Arrays.asList(SW01);
                floorMap = buildingMaps.get(0);
                for (int i = 1; i <= buildingMaps.size(); i++) {
                    MaterialButton floorButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
                    floorButton.setText(String.valueOf(i));
                    floorButton.setId(i);
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
        // google map stuff
        GoogleMapOptions buildingMapOptions = new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_NONE).rotateGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(true).zoomGesturesEnabled(true).zoomControlsEnabled(true).compassEnabled(false);
        SupportMapFragment buildingMapFragment = SupportMapFragment.newInstance(buildingMapOptions);
        getSupportFragmentManager().beginTransaction().add(R.id.building_map_fragment, buildingMapFragment).commit();
        buildingMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // set buildingMap to the googleMap
        buildingMap = googleMap;
        // move camera to the burnaby campus in a smart fashion (zoomed out until full map is displayed)
        buildingMap.moveCamera(CameraUpdateFactory.newLatLngBounds(floorMap.getMapBounds(), 0));
        buildingMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().zoom(buildingMap.getCameraPosition().zoom).target(floorMap.getMapCenter()).bearing(90f).build()));
        // setting the floor select group listener in OnMapReady because it involves adding GroundOverlays
        floorSelectGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    for (int i = 1; i <= buildingMaps.size(); i++) {
                        if (checkedId == i) {
                            // set the floor map to the correct floor plan
                            floorMap = buildingMaps.get(i - 1);
                            // move camera to the burnaby campus in a smart fashion (zoomed out until full map is displayed)
                            buildingMap.moveCamera(CameraUpdateFactory.newLatLngBounds(floorMap.getMapBounds(), 0));
                            buildingMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().zoom(buildingMap.getCameraPosition().zoom).target(floorMap.getMapCenter()).bearing(90f).build()));
                            // add the floor overlay
                            if(mapOverlay != null) {
                                mapOverlay.remove();
                            }
                            @SuppressLint("DiscouragedApi") int buildingFloorResourceId = BuildingMapActivity.this.getResources().getIdentifier(floorMap.getMapName().toLowerCase(), "drawable", BuildingMapActivity.this.getPackageName());
                            mapOverlay = buildingMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(buildingFloorResourceId)).position(floorMap.getMapCenter(), floorMap.getMapWidth()).bearing(90f).transparency(floorMap.getMapTransparency()));
                        }
                    }
                }
            }
        });
    }
}