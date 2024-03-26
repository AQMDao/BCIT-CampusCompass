package org.bcit.campuscompass;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    /* LOCAL DATA */

    MapData burnabyCampus;
    MapData sw01_1;
    MapData sw01_2;
    MapData sw01_3;
    MapData sw01_4;

    /* MEMBERS */

    // Google Maps
    private GoogleMap googleMap;

    // Our Map
    private MapData currentMapData;
    private MapData nextMapData;
    private GroundOverlay mapOverlay;

    // Floating Action Buttons
    FloatingActionButton[] mapFabs;

    // Popup Menu
    private PopupMenu mapPum;

    // Location


    /* METHODS */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the view
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        GoogleMapOptions tempGoogleMapOptions = new GoogleMapOptions()
                .mapType(GoogleMap.MAP_TYPE_NONE)
                .rotateGesturesEnabled(true)
                .scrollGesturesEnabled(true)
                .tiltGesturesEnabled(true)
                .zoomControlsEnabled(true)
                .compassEnabled(true);
        SupportMapFragment tempSupportMapFragment = SupportMapFragment.newInstance(tempGoogleMapOptions);
        getChildFragmentManager().beginTransaction().add(R.id.map_fcv, tempSupportMapFragment).commit();

        tempSupportMapFragment.getMapAsync(MapFragment.this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initializeAllMapData();
                // add a ground overlay and animate pan to it
                currentMapData = burnabyCampus;
                addOverlay(currentMapData);
                centerMapTo(currentMapData);

                initializeMapFabOcls();
            }
        });
    }

    /* HELPER FUNCTIONS */
    private void initializeAllMapData() {
        // Burnaby Campus
        double[] tempNorth = {49, 15, 10.42};
        double[] tempSouth = {49, 14, 36.89};
        double[] tempEast = {-122, -59, -28.33};
        double[] tempWest = {-123, -0, -47.94};
        float tempBearing = 90;
        BitmapDescriptor tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        double[][] tempData = {tempSouth, tempWest, tempNorth, tempEast};
        burnabyCampus = createMapData("Burnaby Campus", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 1
        tempNorth = new double[]{49, 15, 5.67};
        tempSouth = new double[]{49, 15, 1.10};
        tempEast = new double[]{-123, -0, -4.81};
        tempWest = new double[]{-123, -0, -15.54};
        tempBearing = 90.4743f;

        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_1);
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        sw01_1 = createMapData("SW01_1", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 2
        tempNorth = new double[]{49, 15, 5.36};
        tempSouth = new double[]{49, 15, 1.11};
        tempEast = new double[]{-123, -0, -5.13};
        tempWest = new double[]{-123, -0, -15.22};
        tempBearing = 90.3857f;
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_2);
        sw01_2 = createMapData("SW01_2", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 3
        tempNorth = new double[]{49, 15, 5.72};
        tempSouth = new double[]{49, 15, 1.19};
        tempEast = new double[]{-123, -0, -5.10};
        tempWest = new double[]{-123, -0, -15.80};
        tempBearing = 90.4109f;
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_3);
        sw01_3 = createMapData("SW01_3", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 4
        tempNorth = new double[]{49, 15, 5.81};
        tempSouth = new double[]{49, 15, 1.26};
        tempEast = new double[]{-123, -0, -4.99};
        tempWest = new double[]{-123, -0, -15.72};
        tempBearing = 90.3929f;
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_4);
        sw01_4 = createMapData("SW01_4", tempData, tempBearing, tempBitmapDescriptor);
    }

    private void initializeMapFabOcls() {
        MainActivity mainActivity = (MainActivity) requireActivity();
        mapFabs = mainActivity.getMapFabButtons();

        View.OnClickListener centerMapFabOcl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerMapTo(currentMapData);
            }
        };
        View.OnClickListener searchMapFabOcl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapPum = new PopupMenu(requireActivity(), mapFabs[1]);
                mapPum.getMenuInflater().inflate(R.menu.map_menu, mapPum.getMenu());
                mapPum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.outdoor) {
                            nextMapData = burnabyCampus;
                        }
                        if (item.getItemId() == R.id.outdoor_bcit) {
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        if (item.getItemId() == R.id.outdoor_sw01) {
                            if (currentMapData == nextMapData) {
                                centerMapTo(sw01_1);
                            } else {
                                mapOverlay.remove(); // remove the current map
                                addOverlay(nextMapData); // add the next map
                                centerMapTo(sw01_1); // pan to next map
                                currentMapData = nextMapData; // set the new current map
                            }
                        }
                        if (item.getItemId() == R.id.outdoor_sw03) {
                        }

                        if (item.getItemId() == R.id.indoor) {
                        }
                        if (item.getItemId() == R.id.indoor_sw01) {
                        }
                        if (item.getItemId() == R.id.indoor_sw01_1) {
                            nextMapData = sw01_1;
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        if (item.getItemId() == R.id.indoor_sw01_2) {
                            nextMapData = sw01_2;
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        if (item.getItemId() == R.id.indoor_sw01_3) {
                            nextMapData = sw01_3;
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        if (item.getItemId() == R.id.indoor_sw01_4) {
                            nextMapData = sw01_4;
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        return true;
                    }
                });
                mapPum.show();
            }
        };

        View.OnClickListener toggleLocationOcl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.getLocationFabState()) {
                    mainActivity.updateLocationFab();
                }
                else {
                    mainActivity.updateLocationFab();
                }
            }
        };

        mapFabs[0].setOnClickListener(centerMapFabOcl);
        mapFabs[1].setOnClickListener(searchMapFabOcl);
        mapFabs[2].setOnClickListener(toggleLocationOcl);
    }

    /* HELPER FUNCTIONS */

    private double toDegrees(double[] inDegreesMinutesSeconds) {
        double degrees = inDegreesMinutesSeconds[0];
        double minutes = (inDegreesMinutesSeconds[1] / 60);
        double seconds = (inDegreesMinutesSeconds[2] / 3600);
        return degrees + minutes + seconds;
    }
    private MapData createMapData(String name, double[][] tempData, float tempBearing, BitmapDescriptor tempBitmapDescriptor) {
        float tempZoom = getMapZoom(new LatLngBounds(new LatLng(toDegrees(tempData[0]), toDegrees(tempData[1])), new LatLng(toDegrees(tempData[2]), toDegrees(tempData[3]))));
        return new MapData(name, tempData, tempBearing, tempBitmapDescriptor, tempZoom);
    }
    private float getMapZoom(LatLngBounds bounds) {
        CameraPosition tempCameraPosition = googleMap.getCameraPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        float zoom = googleMap.getCameraPosition().zoom;
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(tempCameraPosition));
        return zoom;
    }
    private void addOverlay(MapData mapData) {
        mapOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions().image(mapData.getBitmapDescriptor()).positionFromBounds(mapData.getBounds()).bearing(mapData.getBearing()));
    }
    private void centerMapTo(MapData mapData) {
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraPosition tempCameraPosition = new CameraPosition.Builder().target(mapData.getBounds().getCenter()).bearing(mapData.getBearing()).zoom(mapData.getMapZoomLevel()).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(tempCameraPosition), 1000, new GoogleMap.CancelableCallback() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onFinish() {

                    }
                });
            }
        });
    }
    private void moveTo(MapData mapData) {
        mapOverlay.remove(); // remove the current map
        addOverlay(mapData); // add the next map
        centerMapTo(mapData); // pan to next map
        currentMapData = mapData; // set the new current map
    }
}