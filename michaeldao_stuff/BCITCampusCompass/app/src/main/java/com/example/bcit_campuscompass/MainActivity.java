package com.example.bcit_campuscompass;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_NORMAL).compassEnabled(true).rotateGesturesEnabled(true).scrollGesturesEnabled(true).tiltGesturesEnabled(true).zoomControlsEnabled(true).zoomGesturesEnabled(true));
        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // data
        LatLng bcitSW = new LatLng(49.24129170924556 + 0.00007, -123.0069251883724 + 0.0006);
        LatLng bcitNE = new LatLng(49.25534581727106 - 0.00019, -122.99643645897372 - 0.0002);
        LatLngBounds bcitBounds = new LatLngBounds(bcitSW, bcitNE);
        googleMap.addMarker(new MarkerOptions().position(bcitSW));
        googleMap.addMarker(new MarkerOptions().position(bcitNE));
        // map settings
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setBuildingsEnabled(false);
        // zoom limits
        googleMap.setMaxZoomPreference(20);
        googleMap.setMinZoomPreference(15);
        // restrict camera panning
        googleMap.setLatLngBoundsForCameraTarget(bcitBounds);
        // initial camera placement
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bcitBounds, 0));
        // overlay placement
        LatLng newarkLatLng = new LatLng(40.714086, -74.228697);
        GroundOverlayOptions bcitMap = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.bcit_burnaby_campus)).positionFromBounds(bcitBounds).transparency(0);
        googleMap.addGroundOverlay(bcitMap);
    }
}

