package org.bcit.campuscompass;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MapFragment extends Fragment {
    // Local Data (to be accessed via SQLite database when implemented)
    double[] campusNorth = {49, 15, 10.42};
    double[] campusSouth = {49, 14, 36.89};
    double[] campusEast = {-122, -59, -28.33};
    double[] campusWest = {-123, -0, -47.94};
    float campusBearing = 90;
    float campusTransparency = 0;
    /*
        Create the entry point for managing the underlying map features and data. The GoogleMap
        object can only be accessed after it has been retrieved from a SupportMapFragment.
    */
    private GoogleMap campusGoogleMap;
    // ground overlay
    private GroundOverlay mapOverlay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Create a fragment for managing the lifecycle of a GoogleMap object.
        SupportMapFragment campusSupportMapFragment = SupportMapFragment.newInstance(
            new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NONE)
                .rotateGesturesEnabled(true)
                .scrollGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .zoomControlsEnabled(false)
                .compassEnabled(false));
        getChildFragmentManager()
            .beginTransaction()
            .add(R.id.campus_fragment_container_view, campusSupportMapFragment)
            .commit();

        // Set the callback on the fragment to get the GoogleMap object contained in it
        campusSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                campusGoogleMap = googleMap;
                campusGoogleMap.setPadding(0, 0, 0, 160);
                // REMEMBER
                // LATITUDE IS DEGREES NORTH/SOUTH OF EQUATOR
                // LONGITUDE IS DEGREES EAST/WEST OF PRIME MERIDIAN
                LatLng campusSouthWest = new LatLng(toDegrees(campusSouth), toDegrees(campusWest));
                LatLng campusNorthEast = new LatLng(toDegrees(campusNorth), toDegrees(campusEast));
                LatLngBounds campusBounds = new LatLngBounds(campusSouthWest, campusNorthEast);
                mapOverlay = campusGoogleMap.addGroundOverlay(new GroundOverlayOptions()
                    .image(BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus))
                    .positionFromBounds(campusBounds)
                    .bearing(campusBearing)
                    .transparency(campusTransparency));
                // move camera to the burnaby campus in a smart fashion (zoomed out until full map is displayed)
                campusGoogleMap.moveCamera(
                        CameraUpdateFactory.newLatLngBounds(campusBounds, 0));
                campusGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                        new CameraPosition.Builder()
                            .zoom(campusGoogleMap.getCameraPosition().zoom)
                            .target(campusBounds.getCenter())
                            .bearing(campusBearing).build()));
            }
        });
        return view;
    }

    private double toDegrees(double[] degreesMinutesSeconds) {
        double degrees = degreesMinutesSeconds[0];
        double minutes = (degreesMinutesSeconds[1] / 60);
        double seconds = (degreesMinutesSeconds[2] / 3600);
        return degrees + minutes + seconds;
    }
}