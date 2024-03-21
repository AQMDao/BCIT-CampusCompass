package org.bcit.campuscompass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

// TODO: add room markers for SW01
// note would be best to do this while running the actual app on a physical phone probably for the geo coordinates
// TODO: database stuff eddie please try to finish it this week btw

// this activity is the start of our implementation for the application
// for developers: used to debug and experiment implementations at the campus level
// for users: view and interact with the bcit burnaby campus
public class CampusMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    // local storage data
    MapData burnabyCampus = new MapData("burnaby_campus", new LatLng(49.24814402642466 + 0.00011, -122.99923370291539 + 0.00010), 1533.3333333333f, 0f);
    String[] burnabyBuildingList = {"SW01", "SW02", "SW03", "SW05", "SW09"};
    // google map variable
    GoogleMap campusMap;
    // location variables
    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallBack;
    SettingsClient settingsClient;
    LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;
    Location lastLocation;
    // check for location permissions from user
    public void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(CampusMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(CampusMapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(CampusMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(CampusMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }
    // start the location tracking
    public void startLocationUpdating() {
        // get the fused location provider client for the campus map activity
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(CampusMapActivity.this);
        // get the client settings for the location services for the campus map activity
        settingsClient = LocationServices.getSettingsClient(CampusMapActivity.this);
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
                    if (ContextCompat.checkSelfPermission(CampusMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
        setContentView(R.layout.activity_campus_map);
        // handle location permissions and set up user location tracking
        checkLocationPermissions();
        startLocationUpdating();
        // create an instance of a building auto complete text view
        AutoCompleteTextView buildingAutoCompleteTextView = findViewById(R.id.building_auto_complete_text_view);
        // create the adapter for the building auto complete text view and set it
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<String>(this, R.layout.list_items, burnabyBuildingList);
        buildingAutoCompleteTextView.setAdapter(buildingAdapter);
        // set the building auto complete text view to listen for item clicks
        buildingAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String buildingSelect = parent.getItemAtPosition(position).toString();
                // create the intention to start the new building activity
                Intent openBuildingActivity = new Intent(CampusMapActivity.this, BuildingMapActivity.class);
                // pass to the new building activity which building was selected
                openBuildingActivity.putExtra("buildingSelect", buildingSelect);
                // start the new building activity
                CampusMapActivity.this.startActivity(openBuildingActivity);
            }
        });
        // create a GoogleMapOptions to pass into the SupportMapFragment so we can adjust Google map functionality as needed
        GoogleMapOptions campusMapOptions = new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_NONE).rotateGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(true).zoomGesturesEnabled(true).zoomControlsEnabled(true).compassEnabled(false); // currently on default settings
        // create a SupportMapFragment instance and add it to this activity
        SupportMapFragment campusMapFragment = SupportMapFragment.newInstance(campusMapOptions);
        getSupportFragmentManager().beginTransaction().add(R.id.campus_map_fragment, campusMapFragment).commit();
        // call getMapAsync() to set the callback on the map fragment
        campusMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // assign our campusMap the googleMap when its ready
        campusMap = googleMap;
        // move camera to the burnaby campus in a smart fashion (zoomed out until full map is displayed)
        campusMap.moveCamera(CameraUpdateFactory.newLatLngBounds(burnabyCampus.getMapBounds(), 0));
        campusMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().zoom(googleMap.getCameraPosition().zoom).target(burnabyCampus.getMapCenter()).bearing(90f).build()));
        // add the campus map
        @SuppressLint("DiscouragedApi") int burnabyCampusResourceId = CampusMapActivity.this.getResources().getIdentifier(burnabyCampus.getMapName(), "drawable", CampusMapActivity.this.getPackageName());
        campusMap.addGroundOverlay(new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(burnabyCampusResourceId)).position(burnabyCampus.getMapCenter(), burnabyCampus.getMapWidth()).bearing(90f).transparency(burnabyCampus.getMapTransparency()));
    }
}