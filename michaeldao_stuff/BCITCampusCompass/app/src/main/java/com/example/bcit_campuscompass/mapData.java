package com.example.bcit_campuscompass;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

public class mapData {
    // members
    private String mapName;
    private String floorName;
    private LatLng mapCenter;
    private float mapWidth;
    private LatLngBounds mapBounds;
    private float mapTransparency;

    // constructors
    public mapData(String map, String floor, LatLng center, float width, float transparency) {
        this.mapName = map;
        this.floorName = floor;
        this.mapCenter = center;
        this.mapWidth = width;
        this.mapBounds = toBounds(this.mapCenter, this.mapWidth);
        this.mapTransparency = transparency;
    }
    public mapData(String map, LatLng center, float width, float transparency) {
        this.mapName = map;
        this.mapCenter = center;
        this.mapWidth = width;
        this.mapBounds = toBounds(this.mapCenter, this.mapWidth);
    }
    public mapData() {

    }

    // get parameters
    public String getMapName() {
        return this.mapName;
    }
    public String getFloorName() {
        return this.floorName;
    }
    public LatLng getMapCenter() {
        return this.mapCenter;
    }
    public float getMapWidth() {
        return this.mapWidth;
    }
    public void setMapWidth(float width) {
        this.mapWidth = width;
    }
    public LatLngBounds getMapBounds() {
        return this.mapBounds;
    }
    public float getMapTransparency() {
        return this.mapTransparency;
    }

    // set parameters
    public void setMapName(String map) {
        this.mapName = map;
    }
    public void setFloorName(String floor) {
        this.mapName = floor;
    }
    public void setMap(LatLng center, float width) {
        this.mapCenter = center;
        this.mapWidth = width;
        this.mapBounds = toBounds(this.mapCenter, this.mapWidth);
    }
    public void setTransparency(float transparency) {
        this.mapTransparency = transparency;
    }

    // helpers
    private LatLngBounds toBounds(LatLng center, double mapWidth) {
        double distanceFromCenterToCorner = mapWidth / 2 * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
}
