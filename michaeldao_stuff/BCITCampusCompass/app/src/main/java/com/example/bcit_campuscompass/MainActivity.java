package com.example.bcit_campuscompass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
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

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback
{
    private static final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myGoogleMap;
    private Spinner mapSpinner;
    private Spinner floorSpinner;
    private ArrayAdapter<CharSequence> floorAdapter;
    private String newMapSelected;
    private String newFloorSelected;
    private GroundOverlayOptions mapOverlayOptions;
    private GroundOverlay mapOverlay;
    private mapData bcitBurnabyCampus;
    private mapData bcitSW01_1;
    private mapData bcitSW01_2;
    private mapData bcitSW01_3;
    private mapData bcitSW01_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bcitBurnabyCampus = new mapData("CAMPUS", new LatLng(49.24814402642466 + 0.00011, -122.99923370291539 + 0.00010), 1150f, 0.25f);
        bcitSW01_1 = new mapData("SW1", "1", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
        bcitSW01_2 = new mapData("SW1", "2",  new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
        bcitSW01_3 = new mapData("SW1", "3", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);
        bcitSW01_4 = new mapData("SW1", "4", new LatLng(49.25095038345488, -123.00279032907925 - 0.00006), 145, 0.25f);

        floorSpinner = (Spinner) findViewById(R.id.floor_spinner);
        mapSpinner = (Spinner) findViewById(R.id.map_spinner);
        ArrayAdapter<CharSequence> mapAdapter = ArrayAdapter.createFromResource(this, R.array.mapSpinnerArray, android.R.layout.simple_spinner_item);
        mapAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapSpinner.setAdapter(mapAdapter);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(true).zoomControlsEnabled(true).zoomGesturesEnabled(true));
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.map_fragment, mapFragment).commit();
        mapFragment.getMapAsync(MainActivity.this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myGoogleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        myGoogleMap.setMyLocationEnabled(true);

        myGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        myGoogleMap.setMaxZoomPreference(21);
        myGoogleMap.setMinZoomPreference(2);

        mapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newMapSelected = mapSpinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "Map selected: " + newMapSelected, Toast.LENGTH_SHORT).show();

                if (mapOverlay != null) {
                    mapOverlay.remove();
                }
                if (newMapSelected.equals("NONE")) {
                    floorSpinner.setAdapter(null);
                }
                if (newMapSelected.equals("CAMPUS")) {
                    floorSpinner.setAdapter(null);

                    mapOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.bcit_burnaby_campus)).position(bcitBurnabyCampus.getMapCenter(), bcitBurnabyCampus.getMapWidth()).transparency(bcitBurnabyCampus.getMapTransparency());
                    mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);

                    toMap(bcitBurnabyCampus);
                }
                if (newMapSelected.equals("SW1")) {
                    floorAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.fourFloorSpinnerArray, android.R.layout.simple_spinner_item);
                    floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    floorSpinner.setAdapter(floorAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        floorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newFloorSelected = floorSpinner.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(), "Floor selected: " + newFloorSelected, Toast.LENGTH_SHORT).show();
                if (mapOverlay != null) {
                    mapOverlay.remove();
                }
                if (newMapSelected.equals("SW1")) {
                    if (newFloorSelected.equals("1")) {
                        mapOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_1)).position(bcitSW01_1.getMapCenter(), bcitSW01_1.getMapWidth()).transparency(bcitSW01_1.getMapTransparency());
                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        toMap(bcitSW01_1);
                    }
                    if (newFloorSelected.equals("2")) {
                        mapOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_2)).position(bcitSW01_3.getMapCenter(), bcitSW01_2.getMapWidth()).transparency(bcitSW01_2.getMapTransparency());
                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        toMap(bcitSW01_2);
                    }
                    if (newFloorSelected.equals("3")) {
                        mapOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_3)).position(bcitSW01_3.getMapCenter(), bcitSW01_3.getMapWidth()).transparency(bcitSW01_3.getMapTransparency());                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        toMap(bcitSW01_3);
                    }
                    if (newFloorSelected.equals("4")) {
                        mapOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_4)).position(bcitSW01_4.getMapCenter(), bcitSW01_4.getMapWidth()).transparency(bcitSW01_4.getMapTransparency());                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        mapOverlay = googleMap.addGroundOverlay(mapOverlayOptions);
                        toMap(bcitSW01_4);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void toMap(mapData map) {
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(map.getMapBounds(), 0));
        float mapZoom = myGoogleMap.getCameraPosition().zoom;
        myGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().bearing(90).zoom(mapZoom).target(map.getMapCenter()).build()));
    }
}

