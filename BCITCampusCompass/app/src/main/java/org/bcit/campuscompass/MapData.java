package org.bcit.campuscompass;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

// a class to make storing and access of data super easy in my code
public class MapData {

    /* MEMBERS */
    private String mapName;
    private double[][] mapData; // {south, west, north, east} in dms format via Google Earth Pro
    private float mapBearing;
    private BitmapDescriptor mapBitmapDescriptor;
    private float mapZoomLevel;

    /* METHODS */
    public MapData(String name, double[][] data, BitmapDescriptor bitmapDescriptor, float zoom) {
        this.mapName = name;
        this.mapData = data;
        this.mapBitmapDescriptor = bitmapDescriptor;
        this.mapZoomLevel = zoom;
    }

    public String getName() {
        return mapName;
    }
    public LatLngBounds getBounds() {
        double[] test = mapData[0];
        return new LatLngBounds(new LatLng(toDegrees(mapData[0]), toDegrees(mapData[1])), new LatLng(toDegrees(mapData[2]), toDegrees(mapData[3])));
    }
    public float getBearing() {
        return mapBearing;
    }
    public BitmapDescriptor getBitmapDescriptor() {
        return mapBitmapDescriptor;
    }
    public float getMapZoomLevel() {
        return mapZoomLevel;
    }
    private double toDegrees(double[] inDegreesMinutesSeconds) {
        double degrees = inDegreesMinutesSeconds[0];
        double minutes = (inDegreesMinutesSeconds[1] / 60);
        double seconds = (inDegreesMinutesSeconds[2] / 3600);
        return degrees + minutes + seconds;
    }
}
