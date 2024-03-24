package org.bcit.campuscompass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.Objects;

public class MapFragment extends Fragment {
    /* LOCAL DATA */

    // BCIT
    double[] bcit_North = {49, 15, 10.42};
    double[] bcit_South = {49, 14, 36.89};
    double[] bcit_East = {-122, -59, -28.33};
    double[] bcit_West = {-123, -0, -47.94};
    float bcit_Bearing = 90;

    // SW01_1
    double[] sw01_1_North = {49, 15, 5.67};
    double[] sw01_1_South = {49, 15, 1.10};
    double[] sw01_1_East = {-123, -0, -4.81};
    double[] sw01_1_West = {-123, -0, -15.54};
    float sw01_1_Bearing = 90.4743f;

    // SW01_2
    double[] sw01_2_North = {49, 15, 5.36};
    double[] sw01_2_South = {49, 15, 1.11};
    double[] sw01_2_East = {-123, -0, -5.13};
    double[] sw01_2_West = {-123, -0, -15.22};
    float sw01_2_Bearing = 90.3857f;

    // SW01_3
    double[] sw01_3_North = {49, 15, 5.72};
    double[] sw01_3_South = {49, 15, 1.19};
    double[] sw01_3_East = {-123, -0, -5.10};
    double[] sw01_3_West = {-123, -0, -15.80};
    float sw01_3_Bearing = 90.4109f;

    // SW01_4
    double[] sw01_4_North = {49, 15, 5.81};
    double[] sw01_4_South = {49, 15, 1.26};
    double[] sw01_4_East = {-123, -0, -4.99};
    double[] sw01_4_West = {-123, -0, -15.72};
    float sw01_4_Bearing = 90.3929f;

    /* MEMBERS */

    // GoogleMap
    private GoogleMap campusGoogleMap;

    // OnMapReadyCallback
    private OnMapReadyCallback onMapReadyCallback;

    // Ground Overlay
    private GroundOverlayOptions bcitOverlayOptions;
    private GroundOverlayOptions sw01_1_OverlayOptions;
    private GroundOverlayOptions sw01_2_OverlayOptions;
    private GroundOverlayOptions sw01_3_OverlayOptions;
    private GroundOverlayOptions sw01_4_OverlayOptions;
    private GroundOverlay mapOverlay;

    // LatLng
    private LatLngBounds mapBounds;

    // Floating Action Buttons
    FloatingActionButton[] mapFabButtons;

    // Listeners
    private View.OnClickListener centerMapFabOcl;
    private View.OnClickListener buildingFabOcl;
    private View.OnClickListener toggleLocationFabOnClickListener;

    // Popup Menu
    PopupMenu buildingPum;
    PopupMenu.OnMenuItemClickListener buildingPumOmicl;

    /* METHODS */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initializeGoogleMaps();
        return view;
    }
    private void initializeOnMapReadyCallback() {
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                campusGoogleMap = googleMap;
                initializeGroundOverlays();
                initializeMapFabOcls();
            }
        };
    }

    /* HELPER FUNCTIONS */
    private void initializeMapFabOcls() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mapFabButtons = mainActivity.getMapFabButtons();

        centerMapFabOcl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapOverlay.isVisible() && mapOverlay != null) {
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
            }
        };
        buildingFabOcl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildingPum = new PopupMenu(requireActivity(), mapFabButtons[1]);
                buildingPum.getMenuInflater().inflate(R.menu.building_menu, buildingPum.getMenu());
                initializeBuildingPumOmicl();
                buildingPum.setOnMenuItemClickListener(buildingPumOmicl);
                buildingPum.show();
            }
        };

        mapFabButtons[0].setOnClickListener(centerMapFabOcl);
        mapFabButtons[1].setOnClickListener(buildingFabOcl);
        // implement the other buttons soon tm
    }
    private void centerToOverlay(LatLngBounds bounds, float bearing) {
        campusGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        campusGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .zoom(campusGoogleMap.getCameraPosition().zoom)
                .target(bounds.getCenter())
                .bearing(bearing).build()));
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

    private void initializeGoogleMaps() {
        // Create a fragment for managing the lifecycle of a GoogleMap object.
        GoogleMapOptions googleMapOptions = new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NONE)
                .rotateGesturesEnabled(true)
                .scrollGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .zoomControlsEnabled(false)
                .compassEnabled(false);
        SupportMapFragment campusSupportMapFragment = SupportMapFragment.newInstance(googleMapOptions);
        getChildFragmentManager().beginTransaction().add(R.id.map_fcv, campusSupportMapFragment).commit();

        // Set the callback on the fragment to get the GoogleMap object contained in it
        initializeOnMapReadyCallback();
        campusSupportMapFragment.getMapAsync(onMapReadyCallback);
    }

    private void initializeBuildingPumOmicl() {
        buildingPumOmicl = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.bcit_whole) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(bcitOverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
                if (item.getItemId() == R.id.bcit_sw01) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(bcitOverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(sw01_1_OverlayOptions.getBounds(), sw01_1_OverlayOptions.getBearing());
                }
                if (item.getItemId() == R.id.bcit_sw03) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(bcitOverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    // center to sw3 (not implemented yet)
                    //centerToOverlay(sw03_1_OverlayOptions.getBounds(), sw03_1_OverlayOptions.getBearing());
                }
                if(item.getItemId() == R.id.sw01_1) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(sw01_1_OverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
                if(item.getItemId() == R.id.sw01_2) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(sw01_2_OverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
                if(item.getItemId() == R.id.sw01_3) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(sw01_3_OverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
                if(item.getItemId() == R.id.sw01_4) {
                    if(mapOverlay != null) mapOverlay.remove();
                    mapOverlay = campusGoogleMap.addGroundOverlay(sw01_4_OverlayOptions);
                    Objects.requireNonNull(mapOverlay).setVisible(true);
                    centerToOverlay(mapOverlay.getBounds(), mapOverlay.getBearing());
                }
                return true;
            }
        };
    }
    private void initializeGroundOverlays() {
        bcitOverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus)).positionFromBounds(toLatLngBounds(bcit_South, bcit_West, bcit_North, bcit_East)).bearing(bcit_Bearing).transparency(0.1f).visible(false);
        sw01_1_OverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_1)).positionFromBounds(toLatLngBounds(sw01_1_South, sw01_1_West, sw01_1_North, sw01_1_East)).bearing(sw01_1_Bearing).transparency(0.1f).visible(false);
        sw01_2_OverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_2)).positionFromBounds(toLatLngBounds(sw01_2_South, sw01_2_West, sw01_2_North, sw01_2_East)).bearing(sw01_2_Bearing).transparency(0.1f).visible(false);
        sw01_3_OverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_3)).positionFromBounds(toLatLngBounds(sw01_3_South, sw01_3_West, sw01_3_North, sw01_3_East)).bearing(sw01_3_Bearing).transparency(0.1f).visible(false);
        sw01_4_OverlayOptions = new GroundOverlayOptions().image(BitmapDescriptorFactory.fromResource(R.drawable.sw01_4)).positionFromBounds(toLatLngBounds(sw01_4_South, sw01_4_West, sw01_4_North, sw01_4_East)).bearing(sw01_4_Bearing).transparency(0.1f).visible(false);
    }
}