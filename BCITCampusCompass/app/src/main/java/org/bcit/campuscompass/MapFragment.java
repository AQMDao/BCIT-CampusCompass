package org.bcit.campuscompass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.StreetViewUtils;

import java.util.Map;

import kotlin.jvm.JvmMultifileClass;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {
    /* LOCAL DATA */
    MapData burnabyCampus, burnabyCampusPhysical;
    MapData sw01_1, sw01_2, sw01_3, sw01_4;
    MapData sw03_1, sw03_2, sw03_3, sw03_4;
    /* MEMBERS */
    // GoogleMap
    private GoogleMap appGoogleMap;
    // MapData
    private MapData currentMapData, nextMapData;
    // GroundOverlay
    private GroundOverlay mapOverlay;
    // FloatingActionButton
    FloatingActionButton[] mapButtons;
    // PopupMenu
    private PopupMenu selectMapMenu;
    // FusedLocationProviderClient
    private FusedLocationProviderClient fusedLocationProviderClient;
    // LocationCallback
    private LocationCallback locationCallback;
    // SettingsClient
    private SettingsClient settingsClient;
    // LocationRequest
    private LocationRequest locationRequest;
    // LocationSettingsRequest
    private LocationSettingsRequest locationSettingsRequest;
    // Location
    private Location currentLocation;
    // Marker
    private Marker userMarker, svpMarker, mapMarker;
    // StreetViewPanorama
    StreetViewPanorama appStreetViewPanorama;
    // MainActivity
    private MainActivity mainActivity;
    // FragmentContainerView
    private FrameLayout mapFl, svpFl;
    // ActivityResultLauncher<I>
    ActivityResultLauncher<String[]> arl;
    // boolean
    private boolean firstLocation;

    /* METHODS */
    public MapFragment() {
        arl = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Boolean allPermissionsGranted = true;
                for (Boolean permissionGranted : result.values())
                    allPermissionsGranted = allPermissionsGranted && permissionGranted;
                if (allPermissionsGranted) initializeLocationService();
                else
                    Toast.makeText(requireActivity(), "Failed: Permission(s) not granted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mapFl = view.findViewById(R.id.map_fcv);
        svpFl = view.findViewById(R.id.svp_fcv);

        GoogleMapOptions gmo = new GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NONE)
            .rotateGesturesEnabled(true)
            .scrollGesturesEnabled(true)
            .tiltGesturesEnabled(true)
            .zoomControlsEnabled(false)
            .compassEnabled(true)
            .mapToolbarEnabled(false);

        StreetViewPanoramaOptions svpo = new StreetViewPanoramaOptions()
            .zoomGesturesEnabled(true)
            .panningGesturesEnabled(true)
            .userNavigationEnabled(true)
            .streetNamesEnabled(false);

        // SupportMapFragment
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(gmo);
        // SupportStreetViewPanoramaFragment
        SupportStreetViewPanoramaFragment supportStreetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance(svpo);

        // FragmentManager
        FragmentManager childFragmentManager = getChildFragmentManager();
        // FragmentTransaction
        FragmentTransaction childFragmentTransaction = childFragmentManager.beginTransaction();
        childFragmentTransaction.add(R.id.map_fcv, supportMapFragment);
        childFragmentTransaction.add(R.id.svp_fcv, supportStreetViewPanoramaFragment);
        childFragmentTransaction.commit();

        supportMapFragment.getMapAsync(MapFragment.this);
        supportStreetViewPanoramaFragment.getStreetViewPanoramaAsync(MapFragment.this);

        // other stuff atm
        mainActivity = (MainActivity) requireActivity();

        return view;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        appGoogleMap = googleMap;
        appGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                initalizeMapData();
                currentMapData = burnabyCampus;
                addOverlay(currentMapData);
                CameraPosition cp = new CameraPosition.Builder()
                    .target(currentMapData.getBounds().getCenter())
                    .bearing(currentMapData.getBearing())
                    .zoom(currentMapData.getMapZoomLevel())
                    .build();
                appGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                appGoogleMap.setLatLngBoundsForCameraTarget(burnabyCampusPhysical.getBounds());
                appGoogleMap.setMinZoomPreference(burnabyCampus.getMapZoomLevel());
                appGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @SuppressLint("PotentialBehaviorOverride")
                    @Override
                    public void onCameraMove() {
                        if(mapMarker != null) mapMarker.setPosition(appGoogleMap.getCameraPosition().target);
                        else {
                            mapMarker = appGoogleMap.addMarker(new MarkerOptions()
                                .title("Map View")
                                .position(appGoogleMap.getCameraPosition().target)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .alpha(0.5f)
                            );
                        }
                        appStreetViewPanorama.setPosition(appGoogleMap.getCameraPosition().target);
                    }
                });
                initializeMapButtonOcls();
            }
        });
    }
    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        appStreetViewPanorama = streetViewPanorama;
        appStreetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(@NonNull StreetViewPanoramaLocation streetViewPanoramaLocation) {
                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                    if (svpMarker != null) svpMarker.setPosition(streetViewPanoramaLocation.position);
                    else {
                        svpMarker = appGoogleMap.addMarker(new MarkerOptions()
                                .position(streetViewPanoramaLocation.position)
                                .title("Street View Marker")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        );
                    }
                }
            }
        });
    }
    /* HELPER FUNCTIONS */
    private void initalizeMapData() {
        // Burnaby Campus Physical
        double[] tempNorth = {49, 15, 19.64};
        double[] tempSouth = {49, 14, 27.67};
        double[] tempEast = {-122, -59, -42.45};
        double[] tempWest = {-123, -0, -33.81};
        float tempBearing = -0.4f;
        BitmapDescriptor tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        double[][] tempData = {tempSouth, tempWest, tempNorth, tempEast};
        burnabyCampusPhysical = createMapData("Burnaby Campus Physical", tempData, tempBearing, tempBitmapDescriptor);

        // Burnaby Campus
        tempNorth = new double[] {49, 15, 10.42};
        tempSouth = new double[] {49, 14, 36.89};
        tempEast = new double[] {-122, -59, -28.33};
        tempWest = new double[] {-123, -0, -47.94};
        tempBearing = 90;
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        tempData = new double[][] {tempSouth, tempWest, tempNorth, tempEast};
        burnabyCampus = createMapData("Burnaby Campus", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 1
        tempNorth = new double[]{49, 15, 5.58};
        tempSouth = new double[]{49, 15, 1.00};
        tempEast = new double[]{-123, -0, -4.80};
        tempWest = new double[]{-123, -0, -15.55};
        tempBearing = 90.4f;
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_1);
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        sw01_1 = createMapData("SW01_1", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 2
        tempNorth = new double[]{49, 15, 5.30};
        tempSouth = new double[]{49, 15, 0.95};
        tempEast = new double[]{-123, -0, -5.13};
        tempWest = new double[]{-123, -0, -15.27};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        // access from dat
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_2);
        sw01_2 = createMapData("SW01_2", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 3
        tempNorth = new double[]{49, 15, 5.62};
        tempSouth = new double[]{49, 15, 1.08};
        tempEast = new double[]{-123, -0, -5.09};
        tempWest = new double[]{-123, -0, -15.83};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_3);
        sw01_3 = createMapData("SW01_3", tempData, tempBearing, tempBitmapDescriptor);

        // SW01 Floor 4
        tempNorth = new double[]{49, 15, 5.73};
        tempSouth = new double[]{49, 15, 1.14};
        tempEast = new double[]{-123, -0, -4.98};
        tempWest = new double[]{-123, -0, -15.74};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_4);
        sw01_4 = createMapData("SW01_4", tempData, tempBearing, tempBitmapDescriptor);

        // SW03 Floor 1
        tempNorth = new double[]{49, 15, 2.45};
        tempSouth = new double[]{49, 14, 57.90};
        tempEast = new double[]{-123, -0, -4.28};
        tempWest = new double[]{-123, -0, -14.95};
        tempBearing = 0.3f;
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_1);
        sw03_1 = createMapData("SW03_1", tempData, tempBearing, tempBitmapDescriptor);

        // SW03 Floor 2
        tempNorth = new double[]{49, 15, 2.56};
        tempSouth = new double[]{49, 14, 58.02};
        tempEast = new double[]{-123, -0, -4.28};
        tempWest = new double[]{-123, -0, -14.94};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_2);
        sw03_2 = createMapData("SW03_2", tempData, tempBearing, tempBitmapDescriptor);

        // SW03 Floor 3
        tempNorth = new double[]{49, 15, 3.04};
        tempSouth = new double[]{49, 14, 58.49};
        tempEast = new double[]{-123, -0, -4.01};
        tempWest = new double[]{-123, -0, -14.71};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_3);
        sw03_3 = createMapData("SW03_3", tempData, tempBearing, tempBitmapDescriptor);

        // SW03 Floor 4
        tempNorth = new double[]{49, 15, 2.81};
        tempSouth = new double[]{49, 14, 58.27};
        tempEast = new double[]{-123, -0, -4.01};
        tempWest = new double[]{-123, -0, -14.70};
        tempData = new double[][]{tempSouth, tempWest, tempNorth, tempEast};
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_4);
        sw03_4 = createMapData("SW03_4", tempData, tempBearing, tempBitmapDescriptor);
    }
    private void initializeMapButtonOcls() {
        mapButtons = mainActivity.getMapButtons();
        // center map button
        View.OnClickListener ocl0 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerTo(currentMapData);
            }
        };
        // search map button
        View.OnClickListener ocl1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectMapMenu = new PopupMenu(requireActivity(), mapButtons[1]);
                selectMapMenu.getMenuInflater().inflate(R.menu.map_menu, selectMapMenu.getMenu());
                selectMapMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.outdoor) nextMapData = burnabyCampus;
                        if (item.getItemId() == R.id.outdoor_bcit) {
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.outdoor_sw01) {
                            if (currentMapData == nextMapData) centerTo(sw01_1);
                            else {
                                mapOverlay.remove();
                                addOverlay(nextMapData);
                                centerTo(sw01_1);
                                currentMapData = nextMapData;
                            }
                        }
                        if (item.getItemId() == R.id.outdoor_sw03) {
                            if (currentMapData == nextMapData) centerTo(sw03_1);
                            else {
                                mapOverlay.remove();
                                addOverlay(nextMapData);
                                centerTo(sw03_1);
                                currentMapData = nextMapData;
                            }
                        }
                        if (item.getItemId() == R.id.indoor_sw01_1) {
                            nextMapData = sw01_1;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw01_2) {
                            nextMapData = sw01_2;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw01_3) {
                            nextMapData = sw01_3;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw01_4) {
                            nextMapData = sw01_4;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw03_1) {
                            nextMapData = sw03_1;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw03_2) {
                            nextMapData = sw03_2;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw03_3) {
                            nextMapData = sw03_3;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        if (item.getItemId() == R.id.indoor_sw03_4) {
                            nextMapData = sw03_4;
                            if (currentMapData == nextMapData) centerTo(nextMapData);
                            else moveTo(nextMapData);
                        }
                        return true;
                    }
                });
                selectMapMenu.show();
            }
        };

        // toggle user location button
        View.OnClickListener ocl2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainActivity.getLocationFabState()) {
                    stopLocationUpdates();
                }
                else {
                    firstLocation = true;
                    arl.launch(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                    });
                }
                mainActivity.toggleLocationFab();
            }
        };

        // street view toggle button
        View.OnClickListener ocl3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mainActivity.getViewFabState()) {
                    mapFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,1));
                    svpFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0f));
                    if (svpMarker != null) svpMarker.setVisible(false);
                    if (mapMarker != null) mapMarker.setVisible(true);
                } else {
                    mapFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.5f));
                    svpFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.5f));
                    if (svpMarker != null) svpMarker.setVisible(true);
                    if (mapMarker != null) mapMarker.setVisible(false);
                }
                mainActivity.toggledViewFab();
            }
        };

        View.OnClickListener[] ocls = {ocl0, ocl1, ocl2, ocl3};
        for (int i = 0; i < ocls.length; i++) {
            mapButtons[i].setOnClickListener(ocls[i]);
        }
    }
    private double toDegrees(double[] dms) {
        double d = dms[0];
        double m = (dms[1] / 60);
        double s = (dms[2] / 3600);
        return d + m + s;
    }
    private MapData createMapData(String name, double[][] data, float bearing, BitmapDescriptor bitmapdescriptor) {
        float tempZoom = getMapZoom(new LatLngBounds(new LatLng(toDegrees(data[0]), toDegrees(data[1])), new LatLng(toDegrees(data[2]), toDegrees(data[3]))));
        return new MapData(name, data, bearing, bitmapdescriptor, tempZoom);
    }
    private float getMapZoom(LatLngBounds bounds) {
        CameraPosition cp = appGoogleMap.getCameraPosition();
        appGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        float zoom = appGoogleMap.getCameraPosition().zoom;
        appGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        return zoom;
    }
    private void addOverlay(MapData mapData) {
        mapOverlay = appGoogleMap.addGroundOverlay(new GroundOverlayOptions().image(mapData.getBitmapDescriptor()).positionFromBounds(mapData.getBounds()).bearing(mapData.getBearing()));
    }
    private void centerTo(MapData mapdata) {
        appGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraPosition tempCameraPosition = new CameraPosition.Builder().target(mapdata.getBounds().getCenter()).bearing(mapdata.getBearing()).zoom(mapdata.getMapZoomLevel()).build();
                appGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(tempCameraPosition), 1000, new GoogleMap.CancelableCallback() {@Override public void onCancel() {}@Override public void onFinish() {}});
            }
        });
    }
    private void moveTo(MapData mapData) {
        mapOverlay.remove();
        addOverlay(mapData);
        centerTo(mapData);
        currentMapData = mapData;
    }
    @SuppressLint("MissingPermission") // suppressed because already requested prior to this function call
    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(locationSettingsResponse -> {
            Toast.makeText(requireActivity(), "Success: Location setting enabled", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        }).addOnFailureListener(exception -> {
            if(exception instanceof ResolvableApiException) {
                Toast.makeText(requireActivity(), "Failed: Location currently disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback).addOnCompleteListener(task -> {
            Toast.makeText(requireActivity(), "Location updates stopped", Toast.LENGTH_SHORT).show();
            if(userMarker != null) userMarker.remove();
        });
    }
    public void initializeLocationService() {
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
        // Location
        Location nextLocation = locationResult.getLastLocation();
        if(burnabyCampusPhysical.getBounds().contains(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude()))) {
            if(userMarker != null) {
                if(currentLocation != null) userMarker.setPosition(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude()));
                else userMarker.setPosition(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude()));
                currentLocation = nextLocation;
                if(firstLocation) {
                    CameraPosition cp = new CameraPosition.Builder().target(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude())).build();
                    appGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, new GoogleMap.CancelableCallback() {@Override public void onCancel() {} @Override public void onFinish() {}});
                    firstLocation = false;
                }
            }
            else userMarker = appGoogleMap.addMarker(new MarkerOptions().title("User").position(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude())));
        }
        else {
            Toast.makeText(requireActivity(), "Failed: Not on campus", Toast.LENGTH_SHORT).show();
            stopLocationUpdates();
            mainActivity.toggleLocationFab();
        }
    }
}