package org.bcit.campuscompass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
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
    private static final String DATABASE_NAME = "building_floor_rooms_db.db";
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

    public String getRoomDimensions(){
        SQLiteDatabase db = this.getReadableDatabase();
        String value = "";

        Cursor testing = db.rawQuery("SELECT * FROM " + "Building", null);


        //define columns want to retrieve
        String[] projection = {"campus_id"};

        //define selection criteria
        String selection = "building_id = ?";
        String[] selectionArgs = {"1"};

        //Execute the query
        Cursor cursor = db.query("Building", projection, selection, selectionArgs, null, null, null);

        //check if the cursor has data
        if(cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int degrees = cursor.getInt(cursor.getColumnIndex("degrees"));
            @SuppressLint("Range") int minutes = cursor.getInt(cursor.getColumnIndex("minutes"));
            @SuppressLint("Range") int seconds = cursor.getInt(cursor.getColumnIndex("seconds"));

            value = degrees + " " + minutes + " " + seconds;

            cursor.close();
        }

        //close database connection
        db.close();

        return value;
    }
}
