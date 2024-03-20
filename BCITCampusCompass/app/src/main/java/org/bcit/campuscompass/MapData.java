package org.bcit.campuscompass;

        import android.os.Parcel;
        import android.os.Parcelable;

        import androidx.annotation.NonNull;

        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        // from dependency com.google.maps.android:android-maps-utils:0.4.4
        import com.google.maps.android.SphericalUtil;

public class MapData implements Parcelable {
    // members
    private String mapName;
    private LatLng mapCenter;
    private float mapWidth;
    private LatLngBounds mapBounds;
    private float mapTransparency;
    // full constructor
    public MapData(String name, LatLng center, float width, float transparency) {
        this.mapName = name;
        this.mapCenter = center;
        this.mapWidth = width;
        this.mapBounds = toBounds(this.mapCenter, this.mapWidth);
        this.mapTransparency = transparency;
    }
    // empty constructor
    public MapData() {

    }
    // parcel input for passing to a new activity
    protected MapData(Parcel in) {
        mapName = in.readString();
        mapCenter = in.readParcelable(LatLng.class.getClassLoader());
        mapWidth = in.readFloat();
        mapBounds = in.readParcelable(LatLngBounds.class.getClassLoader());
        mapTransparency = in.readFloat();
    }
    // a creator function that packs the object into a parcel for delivery
    public static final Creator<MapData> CREATOR = new Creator<MapData>() {
        @Override
        public MapData createFromParcel(Parcel in) {
            return new MapData(in);
        }
        @Override
        public MapData[] newArray(int size) {
            return new MapData[size];
        }
    };
    // get parameters
    public String getMapName() {
        return this.mapName;
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
    public void setMapName(String name) {
        this.mapName = name;
    }
    public void setMap(LatLng center, float width) {
        this.mapCenter = center;
        this.mapWidth = width;
        this.mapBounds = toBounds(this.mapCenter, this.mapWidth);
    }
    public void setTransparency(float transparency) {
        this.mapTransparency = transparency;
    }
    // a helper function that converts a map center and radius to its equivalent sw+ne bounds
    private LatLngBounds toBounds(LatLng center, double mapWidth) {
        double distanceFromCenterToCorner = mapWidth / 2 * Math.sqrt(2.0);
        LatLng southwestCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner = SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
    // parcel output for a new activity to receive
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(mapName);
        dest.writeParcelable(mapCenter, flags);
        dest.writeFloat(mapWidth);
        dest.writeParcelable(mapBounds, flags);
        dest.writeFloat(mapTransparency);
    }
}