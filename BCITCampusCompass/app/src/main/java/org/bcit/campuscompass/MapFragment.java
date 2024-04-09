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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    /* LOCAL DATA */

    MapData burnabyCampus, burnabyCampusPhysical;
    MapData sw01_1, sw01_2, sw01_3, sw01_4;
    MapData sw03_1, sw03_2, sw03_3, sw03_4;
    Marker sw01_1021, sw01_1025;
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
    private PopupMenu selectMapMenu, selectRoomMenu;
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
    // Marker
    private Marker userMarker, svpMarker, mapMarker;
    private Marker currentRoom;
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
    // Polyline
    Polyline roomLine;

    /* METHODS */

    // a default constructor that contains code that checks for location permissions on startup
    public MapFragment() {
        // instantiate an activity result launcher that checks for and handles permissions
        arl = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                Boolean allPermissionsGranted = true; // ignore Boolean yellow squiggle to prevent undefined behaviour
                for (Boolean permissionGranted : result.values())
                    allPermissionsGranted = allPermissionsGranted && permissionGranted;
                if (allPermissionsGranted) initializeLocationService();
                else
                    Toast.makeText(requireActivity(), "Failed: Permission(s) not granted", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // runs when mapfragment is started
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // frame layouts for the map view and the street view
        mapFl = view.findViewById(R.id.map_fcv);
        svpFl = view.findViewById(R.id.svp_fcv);

        // define google map options
        GoogleMapOptions gmo = new GoogleMapOptions()
            .mapType(GoogleMap.MAP_TYPE_NONE)
            .rotateGesturesEnabled(true)
            .scrollGesturesEnabled(true)
            .tiltGesturesEnabled(true)
            .zoomControlsEnabled(false)
            .compassEnabled(true)
            .mapToolbarEnabled(false);

        // define street view panorama options
        StreetViewPanoramaOptions svpo = new StreetViewPanoramaOptions()
            .zoomGesturesEnabled(true)
            .panningGesturesEnabled(true)
            .userNavigationEnabled(true)
            .streetNamesEnabled(false);

        // google map fragment
        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance(gmo);
        // google street view fragment
        SupportStreetViewPanoramaFragment supportStreetViewPanoramaFragment = SupportStreetViewPanoramaFragment.newInstance(svpo);

        // create a child fragment manager that manages fragments within a fragment
        FragmentManager childFragmentManager = getChildFragmentManager();

        // instantiate a fragment transaction that adds the google map and street view fragments to mapfragment
        FragmentTransaction childFragmentTransaction = childFragmentManager.beginTransaction();
        childFragmentTransaction.add(R.id.map_fcv, supportMapFragment);
        childFragmentTransaction.add(R.id.svp_fcv, supportStreetViewPanoramaFragment);
        childFragmentTransaction.commit();

        // get the googlemap and streetviewpanorama objects from their respective fragments
        supportMapFragment.getMapAsync(MapFragment.this);
        supportStreetViewPanoramaFragment.getStreetViewPanoramaAsync(MapFragment.this);

        // create a reference to mainActivity for communicating parameters and functions to and from mapfragment
        mainActivity = (MainActivity) requireActivity();

        // return view as needed for oncreateview
        return view;
    }
    // runs when the google map is ready for user interaction (note: not necessarily when map is fully loaded)
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        appGoogleMap = googleMap;
        // to prevent app crashing due to camera animations and local data initialization when map is not fully loaded
        appGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // initializes all local data from maps to rooms
                initializeMapData();
                // a sequence of code that transitions the map view to the burnaby campus by default
                currentMapData = burnabyCampus;
                addOverlay(currentMapData);
                CameraPosition cp = new CameraPosition.Builder()
                    .target(currentMapData.getBounds().getCenter())
                    .bearing(currentMapData.getBearing())
                    .zoom(currentMapData.getMapZoomLevel())
                    .build();
                appGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                // restricting the camera panning within bcit burnaby campus
                appGoogleMap.setLatLngBoundsForCameraTarget(burnabyCampusPhysical.getBounds());
                // restricting the camera minimum zoom to bcit burnaby campus
                appGoogleMap.setMinZoomPreference(burnabyCampus.getMapZoomLevel());
                // runs when the camera is moved
                appGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @SuppressLint("PotentialBehaviorOverride")
                    @Override
                    public void onCameraMove() {
                        // if the mapmarker exists, then set the new position for the mapmarker
                        if(mapMarker != null) mapMarker.setPosition(appGoogleMap.getCameraPosition().target);
                        // otherwise, create a new mapmarker with the new position and necessary options
                        else {
                            mapMarker = appGoogleMap.addMarker(new MarkerOptions()
                                .title("Map View")
                                .position(appGoogleMap.getCameraPosition().target)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .visible(false)
                            );
                        }
                        // link the street view position to the map camera position
                        appStreetViewPanorama.setPosition(appGoogleMap.getCameraPosition().target);
                    }
                });
                // initializes all of the on click listeners for the extra floating action buttons specific to mapfragment
                initializeMapButtonOcls();
            }
        });
    }
    // runs when the street view panorama is ready for user interaction
    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        appStreetViewPanorama = streetViewPanorama;
        // runs when the street view panorama changes location
        appStreetViewPanorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(@NonNull StreetViewPanoramaLocation streetViewPanoramaLocation) {
                // ignore the warning squiggles as this is a necessary workaround that ensures a valid street view upon map camera movement
                if (streetViewPanoramaLocation != null && streetViewPanoramaLocation.links != null) {
                    // if street view marker exists, then set its new position
                    if (svpMarker != null) svpMarker.setPosition(streetViewPanoramaLocation.position);
                    // otherwise, create a street view marker with the new position and the necessary options
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
    // a helper function to initialize all required local data from the sqlite database
    private void initializeMapData() {
        //noinspection resource
        DatabaseHelper db = new DatabaseHelper(requireActivity());
        try {
            db.copyDatabase();
            db.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* MAPS */
        // Burnaby Campus Physical
        double[][] Data = db.getLocationDimensions("burnaby_campus_p");
        BitmapDescriptor tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        burnabyCampusPhysical = createMapData("Burnaby Campus Physical", Data, tempBitmapDescriptor);
        // Burnaby Campus
        Data = db.getLocationDimensions("burnaby_campus");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.burnaby_campus);
        burnabyCampus = createMapData("Burnaby Campus", Data, tempBitmapDescriptor);
        // SW01 Floor 1
        Data = db.getLocationDimensions("SW01_1");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_1);
        sw01_1 = createMapData("SW01_1", Data, tempBitmapDescriptor);
        // SW01 Floor 2
        Data = db.getLocationDimensions("SW01_2");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_2);
        sw01_2 = createMapData("SW01_2", Data, tempBitmapDescriptor);
        // SW01 Floor 3
        Data = db.getLocationDimensions("SW01_3");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_3);
        sw01_3 = createMapData("SW01_3", Data, tempBitmapDescriptor);
        // SW01 Floor 4
        Data = db.getLocationDimensions("SW01_4");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw01_4);
        sw01_4 = createMapData("SW01_4", Data, tempBitmapDescriptor);
        // SW03 Floor 1
        Data = db.getLocationDimensions("SW03_1");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_1);
        sw03_1 = createMapData("SW03_1", Data, tempBitmapDescriptor);
        // SW03 Floor 2
        Data = db.getLocationDimensions("SW03_2");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_2);
        sw03_2 = createMapData("SW03_2", Data, tempBitmapDescriptor);
        // SW03 Floor 3
        Data = db.getLocationDimensions("SW03_3");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_3);
        sw03_3 = createMapData("SW03_3", Data, tempBitmapDescriptor);
        // SW03 Floor 4
        Data = db.getLocationDimensions("SW03_4");
        tempBitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.sw03_4);
        sw03_4 = createMapData("SW03_4", Data, tempBitmapDescriptor);

        /* ROOMS */
        Data = new double[2][3];
        // SW01 1021
        Data = db.getRoomLocation("SW01_1021");
        sw01_1021 = appGoogleMap.addMarker(new MarkerOptions().position(new LatLng(toDegrees(Data[0]), toDegrees(Data[1]))).title("SW01_1021").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).visible(false));
        // SW01 1025
        Data = db.getRoomLocation("SW01_1025");
        sw01_1025 = appGoogleMap.addMarker(new MarkerOptions().position(new LatLng(toDegrees(Data[0]), toDegrees(Data[1]))).title("SW01_1025").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)).visible(false));
    }
    // a helper function that initializes the on click listeners for the various floating action buttons specific to mapfragment
    private void initializeMapButtonOcls() {
        // get a reference to all of the extra buttons from mainactivity
        mapButtons = mainActivity.getMapButtons();

        // center map button onclicklistener
        View.OnClickListener ocl0 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerTo(currentMapData);
            }
        };
        // search map button onclicklistener
        View.OnClickListener ocl1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set up a popup menu that handles the map searching functionality
                selectMapMenu = new PopupMenu(requireActivity(), mapButtons[1]);
                selectMapMenu.getMenuInflater().inflate(R.menu.map_menu, selectMapMenu.getMenu());
                selectMapMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // in general it shows/hides maps based on selection and performs the required suitable camera animations
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
                // needed for the popup menu to show when button is clicked initially
                selectMapMenu.show();
            }
        };
        // toggle user location button onclicklistener
        View.OnClickListener ocl2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the button is toggled, then stop location updates
                if (mainActivity.getLocationFabState()) {
                    stopLocationUpdates();
                }
                // otherwise the button is not toggled...
                else {
                    // first location is a quality of life boolean that is explained later
                    firstLocation = true;
                    // launch a permission request when trying to enable user location tracking
                    arl.launch(new String[] {
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.INTERNET
                    });
                }
                // toggle the button
                mainActivity.toggleLocationFab();
            }
        };

        // street view toggle button onclicklistener
        View.OnClickListener ocl3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // similar toggle logic to the location toggle
                // if street view is toggled
                if (mainActivity.getViewFabState()) {
                    // dedicate the entire screen back to the map view via layout parameters
                    mapFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,1));
                    svpFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0f));
                    // make the street view and map view markers invisible
                    if (svpMarker != null) svpMarker.setVisible(false);
                    if (mapMarker != null) mapMarker.setVisible(false);
                }
                // otherwise, if street view is not toggled
                else {
                    // split the screen evenly for the map view and street view
                    mapFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.5f));
                    svpFl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 0.5f));
                    // make the street view and map view markers visible to make distinctions easier
                    if (svpMarker != null) svpMarker.setVisible(true);
                    if (mapMarker != null) mapMarker.setVisible(true);
                }
                // toggle the street view button
                mainActivity.toggledViewFab();
            }
        };

        // find room button onclicklistener
        View.OnClickListener ocl4 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if the find room button is toggled
                if (mainActivity.getRoomFabState()) {
                    // if the path to the room is drawn, then remove it
                    if(roomLine != null) {
                        roomLine.remove();
                    }
                    // if a current room has been selected, then remove it
                    if(currentRoom != null) currentRoom.setVisible(false);
                    // toggle the find room button
                    mainActivity.toggledRoomFab();
                }
                // otherwise, if the find room button is not toggled
                else {
                    // set up a popup menu that handles room selection
                    selectRoomMenu = new PopupMenu(requireActivity(), mapButtons[4]);
                    selectRoomMenu.getMenuInflater().inflate(R.menu.room_menu, selectRoomMenu.getMenu());
                    selectRoomMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // in general, it display a marker of the current room based on user selection
                            if(currentRoom != null) currentRoom.setVisible(false);
                            if (item.getItemId() == R.id.sw01_1021) {
                                currentRoom = sw01_1021;
                                currentRoom.setVisible(true);
                            }
                            if (item.getItemId() == R.id.sw01_1025) {
                                currentRoom = sw01_1025;
                                currentRoom.setVisible(true);
                            }
                            return true;
                        }
                    });
                    // needed to show the popup menu when button is clicked
                    selectRoomMenu.show();
                    // toggle the room fab
                    mainActivity.toggledRoomFab();
                }
        }};
        // attach all of the above listeners to their respective buttons
        View.OnClickListener[] ocls = {ocl0, ocl1, ocl2, ocl3, ocl4};
        for (int i = 0; i < ocls.length; i++) {
            mapButtons[i].setOnClickListener(ocls[i]);
        }
    }
    // a helper function to convert degrees, minutes, seconds, into decimal degrees
    private double toDegrees(double[] dms) {
        double d = dms[0];
        double m = (dms[1] / 60);
        double s = (dms[2] / 3600);
        return d + m + s;
    }
    // a helper function to create MapData objects based on what is read from the SQLite database
    private MapData createMapData(String name, double[][] tempData, BitmapDescriptor tempBitmapDescriptor) {
        float tempZoom = getMapZoom(new LatLngBounds(new LatLng(toDegrees(tempData[0]), toDegrees(tempData[1])), new LatLng(toDegrees(tempData[2]), toDegrees(tempData[3]))));
        return new MapData(name, tempData, tempBitmapDescriptor, tempZoom);
    }
    // a helper function that calculates the zoom level needed to have a map fully visible with zero padding
    private float getMapZoom(LatLngBounds bounds) {
        CameraPosition cp = appGoogleMap.getCameraPosition();
        appGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        float zoom = appGoogleMap.getCameraPosition().zoom;
        appGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        return zoom;
    }
    // a helper function that simply adds overlays onto google maps based on selected mapdata
    private void addOverlay(MapData mapData) {
        mapOverlay = appGoogleMap.addGroundOverlay(new GroundOverlayOptions().image(mapData.getBitmapDescriptor()).positionFromBounds(mapData.getBounds()).bearing(mapData.getBearing()));
    }
    // a helper function that re-centers the camera view via animation based on the current map visible
    private void centerTo(MapData mapdata) {
        appGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraPosition tempCameraPosition = new CameraPosition.Builder().target(mapdata.getBounds().getCenter()).bearing(mapdata.getBearing()).zoom(mapdata.getMapZoomLevel()).build();
                appGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(tempCameraPosition), 1000, new GoogleMap.CancelableCallback() {@Override public void onCancel() {}@Override public void onFinish() {}});
            }
        });
    }
    // a helper function that handles transition between visible maps
    private void moveTo(MapData mapData) {
        mapOverlay.remove();
        addOverlay(mapData);
        centerTo(mapData);
        currentMapData = mapData;
    }
    // ALL OF THE LOCATION PERMISSION AND SERVICE HANDLING FUNCTIONS BELOW
    // start location updates
    @SuppressLint("MissingPermission") // suppressed because already requested prior to this function call
    private void startLocationUpdates() {
        // if the location setting is turned on
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnSuccessListener(locationSettingsResponse -> {
            Toast.makeText(requireActivity(), "Success: Location setting enabled", Toast.LENGTH_SHORT).show();
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        })
        .addOnFailureListener(exception -> {
            // if location setting is turned off
            if(exception instanceof ResolvableApiException) {
                Toast.makeText(requireActivity(), "Failed: Location currently disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // stop location updates
    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback).addOnCompleteListener(task -> {
            Toast.makeText(requireActivity(), "Location updates stopped", Toast.LENGTH_SHORT).show();
            if(userMarker != null) userMarker.remove();
        });
    }
    // initializes location services involving permissions and current application state
    public void initializeLocationService() {
        // location provider client that basically gets us the user location
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(requireActivity());
        settingsClient=LocationServices.getSettingsClient(requireActivity());
        // a callback that runs when a new location result is obtained
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                receiveLocation(locationResult);
            }
        };
        // define location request settings where the location updates after a minimum or maximum update interval of 2000ms and 4000ms has passed, or a minimum distance change of 1 meter, with high accuracy priority
        locationRequest = new LocationRequest.Builder(4000).setMinUpdateIntervalMillis(2000).setMinUpdateDistanceMeters(1).setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
        // start location updates
        startLocationUpdates();
    }
    // what to do with the newly received location update
    private void receiveLocation(LocationResult locationResult) {
        // Location
        Location nextLocation = locationResult.getLastLocation();
        if(nextLocation != null) {
            // only keep location updates enabled if the user is actually on campus
            if (burnabyCampusPhysical.getBounds().contains(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude()))) {
                // tldr: sets the new location for the user marker
                if (userMarker != null) userMarker.setPosition(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude()));
                // just for the first location grabbed after enabling location updates, itll pan the camera over (without changing zoom) to the user location
                if (firstLocation) {
                    CameraPosition cp = new CameraPosition.Builder().target(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude())).build();
                    appGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onCancel() {
                        }
                        @Override
                        public void onFinish() {
                        }
                    });
                    firstLocation = false;
                }
                else {
                    userMarker = appGoogleMap.addMarker(new MarkerOptions().title("User Location").position(new LatLng(nextLocation.getLatitude(), nextLocation.getLongitude())));
                }
            }
            else {
                Toast.makeText(requireActivity(), "Failed: Not on campus", Toast.LENGTH_SHORT).show();
                stopLocationUpdates();
                mainActivity.toggleLocationFab();
            }
        }
    }
}