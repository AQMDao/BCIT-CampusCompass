package org.bcit.campuscompass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "building_floor_rooms_db.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Rooms";
    private static final String COLUMN_ID = "room_id";
    private static final String COLUMN_NUMBER = "room_number";
    private static final String COLUMN_BUILDING = "building_id";
    private static final String COLUMN_FLOOR = "floor_id";

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public LatLng getRoomLatLng(String campusName, String buildingName, String roomNumber){
        SQLiteDatabase db = this.getReadableDatabase();

        String query =  "SELECT Room.latitude, Room.longitude " +
                        "FROM Room " +
                        "INNER JOIN Floor ON Room.floor_id = Floor.floor_id " +
                        "INNER JOIN Building ON Floor.building_id = Building.building_id " +
                        "INNER JOIN Campus ON Building.campus_id = Campus.campus_id " +
                        "WHERE Campus.campus_name = ? " +
                        "AND Building.building_name = ? " +
                        "AND Room.room_number = ?";

        Cursor cursor = db.rawQuery(query, new String[]{campusName, buildingName, roomNumber});

        LatLng latLng = null;
        if (cursor != null && cursor.moveToFirst()){
            @SuppressLint("Range") double latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            @SuppressLint("Range") double longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            latLng = new LatLng(latitude, longitude);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return latLng;
    }
}
