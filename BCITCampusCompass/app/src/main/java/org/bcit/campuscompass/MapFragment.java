package org.bcit.campuscompass;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment {
    /* LOCAL DATA */

    // any local data that is hard stored into the app, will remove upon integration with sqlite
    double[] campusNorth = {49, 15, 10.42};
    double[] campusSouth = {49, 14, 36.89};
    double[] campusEast = {-122, -59, -28.33};
    double[] campusWest = {-123, -0, -47.94};
    float campusBearing = 90;
    float campusTransparency = 0; // purely for debugging

    /* MEMBERS */

    // GoogleMap
    private GoogleMap campusGoogleMap;

    // OnMapReadyCallback
    private OnMapReadyCallback onMapReadyCallback;

    // Ground Overlay
    private GroundOverlay mapOverlay;

    // LatLng
    private LatLngBounds mapBounds;

    // Listeners
    private View.OnClickListener centerMapFabOnClickListener;

    /* METHODS */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        // Create a fragment for managing the lifecycle of a GoogleMap object.
        GoogleMapOptions googleMapOptions = new GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NONE)
            .rotateGesturesEnabled(true)
            .scrollGesturesEnabled(true)
            .tiltGesturesEnabled(true)
            .zoomControlsEnabled(false)
            .compassEnabled(false);
        SupportMapFragment campusSupportMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        getChildFragmentManager().beginTransaction().add(R.id.campus_fragment_container_view, campusSupportMapFragment).commit();

        // Set the callback on the fragment to get the GoogleMap object contained in it
        initializeOnMapReadyCallback();
        campusSupportMapFragment.getMapAsync(onMapReadyCallback);
        return view;
    }
    private void initializeOnMapReadyCallback() {
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                campusGoogleMap = googleMap;
                mapBounds = toLatLngBounds(campusSouth, campusWest, campusNorth, campusEast);
                mapOverlay = campusGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus))
                        .positionFromBounds(mapBounds)
                        .bearing(campusBearing)
                        .transparency(campusTransparency));
                // move camera to the burnaby campus in a smart fashion (zoomed out until full map is displayed)
                centerMapView();

                MainActivity activity = (MainActivity) getActivity();
                assert activity != null;
                FloatingActionButton[] mapFabButtons = activity.getMapFabButtons();
                initializeCenterMapOnClickFabListener();
                mapFabButtons[0].setOnClickListener(centerMapFabOnClickListener);
            }
        };
    }

    /* HELPER FUNCTIONS */
    private void initializeCenterMapOnClickFabListener() {
        centerMapFabOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMapView();
            }
        };
    }
    private void centerMapView() {
        assert mapOverlay != null;
        campusGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapOverlay.getBounds(), 0));
        campusGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .zoom(campusGoogleMap.getCameraPosition().zoom)
                .target(mapOverlay.getBounds().getCenter())
                .bearing(mapOverlay.getBearing()).build()));
    }
    private double toDegrees(@NonNull double[] degreesMinutesSeconds) {
        double degrees = degreesMinutesSeconds[0];
        double minutes = (degreesMinutesSeconds[1] / 60);
        double seconds = (degreesMinutesSeconds[2] / 3600);
        return degrees + minutes + seconds;
    }
    @NonNull
    private LatLngBounds toLatLngBounds(double[] south, double[] west, double[] north, double[] east) {
        // REMEMBER
        // LATITUDE IS DEGREES NORTH/SOUTH OF EQUATOR
        // LONGITUDE IS DEGREES EAST/WEST OF PRIME MERIDIAN
        LatLng southWest = new LatLng(toDegrees(south), toDegrees(west));
        LatLng northEast = new LatLng(toDegrees(north), toDegrees(east));
        return new LatLngBounds(southWest, northEast);
    }
}