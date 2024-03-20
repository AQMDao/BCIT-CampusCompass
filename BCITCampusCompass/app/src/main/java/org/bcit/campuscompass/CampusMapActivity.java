package org.bcit.campuscompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Dictionary;
import java.util.List;
import java.util.Objects;

// this activity is the start of our implementation for the application
// for developers: used to debug and experiment implementations on for the campus level
// for users: view and interact with a selected campus from google map activity
public class CampusMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    MapData campusMap;
    MapData SW01_1 = new MapData("SW01_1", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_2 = new MapData("SW01_2", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_3 = new MapData("SW01_3", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData SW01_4 = new MapData("SW01_4", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
    MapData[] SW01 = {SW01_1, SW01_2, SW01_3, SW01_4};
    String[] burnabyBuildingList = {"SW01"};
    AutoCompleteTextView buildingAutoCompleteTextView;
    ArrayAdapter<String> buildingAdapter;
    Intent openBuildingActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_map);
        // create an instance of an auto complete text view for buildings
        buildingAutoCompleteTextView = findViewById(R.id.building_auto_complete_text_view);
        // check which campus we are coming from
        Intent openCampusActivity = getIntent();
        String campusSelected = openCampusActivity.getStringExtra("campusSelect");
        // set the correct adapter to the auto complete text view for the buildings to select based on the campus selected
        switch(Objects.requireNonNull(campusSelected)) {
            case "Aerospace Technology":
                break;
            case "Annacis Island":
                break;
            case "Burnaby":
                // create the adapter for the building auto complete text view and set it
                buildingAdapter = new ArrayAdapter<String>(this, R.layout.list_items, burnabyBuildingList);
                // get the map data for the campus selected
                campusMap = openCampusActivity.getParcelableExtra("burnabyCampus");
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
        buildingAutoCompleteTextView.setAdapter(buildingAdapter);
        buildingAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String buildingSelect = parent.getItemAtPosition(position).toString();
                switch(buildingSelect) {
                    case "SW01":
                        /*
                        // create the intention to start the new building activity
                        openBuildingActivity = new Intent(CampusMapActivity.this, BuildingMapActivity.class);
                        // pass to the new building activity which building was selected and its map data
                        openBuildingActivity.putExtra("Building Selected", buildingSelect);
                        openBuildingActivity.putExtra("SW01_1 MapData", SW01_1);
                        openBuildingActivity.putExtra("SW01_2 MapData", SW01_2);
                        openBuildingActivity.putExtra("SW01_3 MapData", SW01_3);
                        openBuildingActivity.putExtra("SW01_4 MapData", SW01_4);
                        */
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
                // start the new building activity
                //CampusMapActivity.this.startActivity(openBuildingActivity);
            }
        });
        // create a SupportMapFragment instance and add it to GoogleMapsActivity
        GoogleMapOptions campusMapOptions = new GoogleMapOptions()
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
        SupportMapFragment campusMapFragment = SupportMapFragment.newInstance(campusMapOptions);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.campus_map_fragment, campusMapFragment)
                .commit();
        // call getMapAsync() to set the callback on the map fragment
        campusMapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // move camera to selected campus
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(campusMap.getMapBounds(), 0));
        float mapZoom = googleMap.getCameraPosition().zoom;
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().bearing(90).zoom(mapZoom).target(campusMap.getMapCenter()).build()));
    }
}