package org.bcit.campuscompass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "building_floor_rooms_onetable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "Rooms";
    private static final String COLUMN_ID = "room_id";
    private static final String COLUMN_NUMBER = "room_number";
    private static final String COLUMN_BUILDING = "building_id";
    private static final String COLUMN_FLOOR = "floor_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        this.context = context;


    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void copyDatabase() throws IOException {
        InputStream inputStream = context.getAssets().open(DATABASE_NAME);
        String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();
        OutputStream outputStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public SQLiteDatabase openDatabase() throws SQLException {
        String path = context.getDatabasePath(DATABASE_NAME).getPath();
        return SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
    }

    //this function takes the name of the location to return its dimensions by accessing the existing database
    //name of the location takes the following form: [building number]_floor_[floor number}
    //Example: getLocationDimensions(SW1_floor_1)
    //for campus: [campus city]_campus
    //Example: getLocationDimensions(burnaby_campus)
    //returns dimensions in a double array, where each element is the dimensions in S,W,E,N order
    public double[][] getLocationDimensions(String location){

        double[][] dimensions = {
                {0.0, 0.0, 0.0}, //south
                {0.0, 0.0, 0.0}, //west
                {0.0, 0.0, 0.0}, //north
                {0.0, 0.0, 0.0}  //east
        };

        //define columns want to retrieve
        String[] projection = {"degrees", "minutes", "seconds"};

        //define selection criteria
        String selection = "name = ?";

        dimensions[0] = execQuery(selection, projection, location, "_S");
        dimensions[1] = execQuery(selection, projection, location, "_W");
        dimensions[2] = execQuery(selection, projection, location, "_N");
        dimensions[3] = execQuery(selection, projection, location, "_E");

        return dimensions;
    }

    public double[] pinpointRoom(String room_name) {
        double[] roomDimension = {0.0, 0.0, 0.0};

        //define columns want to retrieve
        String[] projection = {"degrees", "minutes", "seconds"};

        //define selection criteria
        String selection = "name = ?";

        //note: room dimensions do not require N,S,E,W so direction is left as ""
        roomDimension = execQuery(selection, projection, room_name, "");

        return roomDimension;
    }

    private double[] execQuery(String selection, String[] projection, String location, String direction){
        SQLiteDatabase db = this.getReadableDatabase();
        double[] dimensions = null;
        String[] selectionArgs = {location + direction};

        //Execute the query
        Cursor cursor = db.query("locations_1", projection, selection, selectionArgs, null, null, null);

        //check if the cursor has data
        if(cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") double degrees = cursor.getDouble(cursor.getColumnIndex("degrees"));
            @SuppressLint("Range") double minutes = cursor.getDouble(cursor.getColumnIndex("minutes"));
            @SuppressLint("Range") double seconds = cursor.getDouble(cursor.getColumnIndex("seconds"));

            dimensions = new double[]{degrees, minutes, seconds};

            cursor.close();
        }

        //close database connection
        db.close();

        return dimensions;
    }
}