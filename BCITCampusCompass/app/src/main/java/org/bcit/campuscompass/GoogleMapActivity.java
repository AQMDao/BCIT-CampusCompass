package org.bcit.campuscompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


// this activity is mainly an entry point in our application
// for developers: used to debug and extensively test experimental implementations for google maps
// for end-users: a good scenario for this activity is if someone does not want to pick a campus and just use google maps
public class GoogleMapActivity extends AppCompatActivity implements OnMapReadyCallback, Serializable {
    String[] campusList = {
        "Aerospace Technology",
        "Annacis Island",
        "Burnaby",
        "Centre for Applied Research and Innovation (CARI)",
        "Downtown",
        "Marine"
    };
    MapData burnabyCampus = new MapData("Burnaby", new LatLng(49.24814402642466 + 0.00011, -122.99923370291539 + 0.00010), 1150f, 0.25f);
    AutoCompleteTextView campusAutoCompleteTextView;
    ArrayAdapter<String> campusAdapter;
    Intent openCampusActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // create and set up the campus selector button
        campusAutoCompleteTextView = findViewById(R.id.campus_auto_complete_text_view);
        campusAdapter = new ArrayAdapter<String>(this, R.layout.list_items, campusList);
        campusAutoCompleteTextView.setAdapter(campusAdapter);
        // set the campus selector button to listen on item clicks
        campusAutoCompleteTextView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // grab which campus was selected
                String campusSelect = parent.getItemAtPosition(position).toString();
                // create the intention to start the new campus activity
                openCampusActivity = new Intent(GoogleMapActivity.this, CampusMapActivity.class);
                // pass to the new campus activity which campus was selected
                openCampusActivity.putExtra("campusSelect", campusSelect);
                // pass to the new campus activity map data depending on the campus selected
                switch(campusSelect) {
                    case "Aerospace Technology":
                        break;
                    case "Annacis Island":
                        break;
                    case "Burnaby":
                        openCampusActivity.putExtra("burnabyCampus", burnabyCampus);
                        break;
                    case "Centre for Applied Research and Innovation (CAR)":
                        break;
                    case "Downtown":
                        break;
                    case "Marine":
                        break;
                    default:
                        break;
                }
                // start the new campus activity
                GoogleMapActivity.this.startActivity(openCampusActivity);
            }
        });
        // create a SupportMapFragment instance and add it to GoogleMapsActivity
        GoogleMapOptions googleMapOptions = new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NORMAL) // normal on debug, none on release
                .zoomControlsEnabled(true)
                .compassEnabled(false)
                .zoomGesturesEnabled(true)
                .scrollGesturesEnabled(true)
                .rotateGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .zOrderOnTop(true)
                .liteMode(false)
                .mapToolbarEnabled(false)
                .scrollGesturesEnabledDuringRotateOrZoom(true)
                .ambientEnabled(true);
        SupportMapFragment googleMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.google_map_fragment, googleMapFragment)
                .commit();
        // call getMapAsync() to set the callback on the map fragment
        googleMapFragment.getMapAsync(this);
    }
    // runs when the google map is ready to receive user input
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // enable the traffic layer
        googleMap.setTrafficEnabled(true);
        // disable 3d building visibility
        googleMap.setBuildingsEnabled(true);
        // enable indoor maps
        googleMap.setIndoorEnabled(true); // only available on normal map type
        // enable default indoor level picker
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        // MAP PADDING
        // padding can be helpful when designing UIs that overlap some portion of the map
        // camera movements via API calls or button presses are relative to the padded region
        // getCameraPosition
        // returns the center of the padded region
        // Projection, getVisibleRegion
        // returns the padded region
        // do not obscure the Google logo or copyright notices!!!!!!!

        // move to the campus selected
    }
}