package org.bcit.campuscompass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    /* LOCAL DATA */

    MapData burnabyCampus;
    MapData sw01_1;
    MapData sw01_2;
    MapData sw01_3;
    MapData sw01_4;
    MapData sw03_1;
    MapData sw03_2;

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

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private Location lastLocation;
    private ActivityResultLauncher<String[]> activityResultLauncher;
    private Marker userMarker;

    /* METHODS */

    public MapFragment() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Boolean areAllGranted = true;
                for(Boolean b: result.values()) {
                    areAllGranted = areAllGranted && b;
                }
                if(areAllGranted) {
                    Toast.makeText(requireActivity(), "Permissions GOOD", Toast.LENGTH_SHORT).show();
                    init();
                }
                else {
                    Toast.makeText(requireActivity(), "Permissions BAD", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        BitmapDescriptor tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        double[][] tempData = {tempSouth, tempWest, tempNorth, tempEast};
        burnabyCampus = createMapData("Burnaby Campus", tempData, tempBitmapDescriptor);

        // SW01 Floor 1
        tempNorth = new double[]{49, 15, 5.58};
        tempSouth = new double[]{49, 15, 1.00};
        tempEast = new double[]{-123, -0, -4.80};
        tempWest = new double[]{-123, -0, -15.55};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_1);
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        sw01_1 = createMapData("SW01_1", tempData, tempBitmapDescriptor);

        // SW01 Floor 2
        tempNorth = new double[]{49, 15, 5.30};
        tempSouth = new double[]{49, 15, 0.95};
        tempEast = new double[]{-123, -0, -5.13};
        tempWest = new double[]{-123, -0, -15.27};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        // access from dat
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_2);
        sw01_2 = createMapData("SW01_2", tempData, tempBitmapDescriptor);

        // SW01 Floor 3
        tempNorth = new double[]{49, 15, 5.62};
        tempSouth = new double[]{49, 15, 1.08};
        tempEast = new double[]{-123, -0, -5.09};
        tempWest = new double[]{-123, -0, -15.83};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_3);
        sw01_3 = createMapData("SW01_3", tempData, tempBitmapDescriptor);

        // SW01 Floor 4
        tempNorth = new double[]{49, 15, 5.73};
        tempSouth = new double[]{49, 15, 1.14};
        tempEast = new double[]{-123, -0, -4.98};
        tempWest = new double[]{-123, -0, -15.74};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_4);
        sw01_4 = createMapData("SW01_4", tempData, tempBitmapDescriptor);

        // SW03 Floor 1
        tempNorth = new double[]{49, 15, 2.45};
        tempSouth = new double[]{49, 14, 57.90};
        tempEast = new double[]{-123, -0, -4.28};
        tempWest = new double[]{-123, -0, -14.95};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_1);
        sw03_1 = createMapData("SW03_1", tempData, tempBitmapDescriptor);

        // SW03 Floor 2
        tempNorth = new double[]{49, 15, 2.56};
        tempSouth = new double[]{49, 14, 58.02};
        tempEast = new double[]{-123, -0, -4.28};
        tempWest = new double[]{-123, -0, -14.94};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_2);
        sw03_2 = createMapData("SW03_2", tempData, tempBitmapDescriptor);
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
                            if (currentMapData == nextMapData) {
                                centerMapTo(sw01_1);
                            } else {
                                mapOverlay.remove(); // remove the current map
                                addOverlay(nextMapData); // add the next map
                                centerMapTo(sw01_1); // pan to next map
                                currentMapData = nextMapData; // set the new current map
                            }
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
                        if(item.getItemId() == R.id.indoor_sw03_1) {
                            nextMapData = sw03_1;
                            if (currentMapData == nextMapData) {
                                centerMapTo(nextMapData);
                            } else {
                                moveTo(nextMapData);
                            }
                        }
                        if(item.getItemId() == R.id.indoor_sw03_2) {
                            nextMapData = sw03_2;
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
                    stopLocationUpdates();
                    mainActivity.updateLocationFab();
                }
                else {
                    activityResultLauncher.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
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
    private MapData createMapData(String name, double[][] tempData, BitmapDescriptor tempBitmapDescriptor) {
        float tempZoom = getMapZoom(new LatLngBounds(new LatLng(toDegrees(tempData[0]), toDegrees(tempData[1])), new LatLng(toDegrees(tempData[2]), toDegrees(tempData[3]))));
        return new MapData(name, tempData, tempBitmapDescriptor, tempZoom);
    }
    private float getMapZoom(LatLngBounds bounds) {
        CameraPosition tempCameraPosition = googleMap.getCameraPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        float zoom = googleMap.getCameraPosition().zoom;
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(tempCameraPosition));
        return zoom;
    }
    private void addOverlay(MapData mapData) {
        float tempBearing;
        if(mapData.getName() == "Burnaby Campus") {
            tempBearing = 90f;
        }
        else {
            switch (mapData.getName().split("_")[0]) {
                case "SW01":
                    tempBearing = 90.4f;
                    break;
                case "SW03":
                    tempBearing = 0.3f;
                    break;
                default:
                    tempBearing = 90f;
                    break;
            }
        }
        mapOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions().image(mapData.getBitmapDescriptor()).positionFromBounds(mapData.getBounds()).bearing(tempBearing));
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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(locationSettingsResponse -> {
            Toast.makeText(requireActivity(), "Location Settings GOOD", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }).addOnFailureListener(e -> {
            int statusCode = ((ApiException) e).getStatusCode();
            Toast.makeText(requireActivity(), "Location Settings BAD:" + e, Toast.LENGTH_SHORT).show();
        });
    }
    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback).addOnCompleteListener(task -> {
            Toast.makeText(requireActivity(), "Location Updates STOPPED", Toast.LENGTH_SHORT).show();
            if(userMarker != null) {
                userMarker.remove();
            }
        });
    }
    public void init() {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(requireActivity());
        settingsClient=LocationServices.getSettingsClient(requireActivity());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                receiveLocation(locationResult);
            }
        };
        locationRequest = new LocationRequest.Builder(4000).setMinUpdateIntervalMillis(2000).setMinUpdateDistanceMeters(1).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
        startLocationUpdates();
    }

    private void receiveLocation(LocationResult locationResult) {
        lastLocation = locationResult.getLastLocation();
        Toast.makeText(requireActivity(), "Latitude: " + lastLocation.getLatitude() + " | Longitude: " + lastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        if(userMarker != null) {
            userMarker.setPosition(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
        else {
            userMarker = googleMap.addMarker(new MarkerOptions().title("User").position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())));
        }
    }
}