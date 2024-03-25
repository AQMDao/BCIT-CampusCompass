package org.bcit.campuscompass;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class HomeFragment extends Fragment {

    private TextView debugTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //find the debug TextView
        debugTextView = rootView.findViewById(R.id.debugTextView);

        //debug message
        String debugMessage = "Latitude and Longitude: " + getValuetoDebug("Burnaby Campus", "SW1", "1172");

        // set debug message to the textview
        debugTextView.setText(debugMessage);
        debugTextView.setVisibility(View.VISIBLE);

        return rootView;



    }

    public String getValuetoDebug(String campusName, String buildingName, String roomNumber) {
        //debug logic here
        DatabaseHelper dbHelper = new DatabaseHelper(getContext(), "building_floor_rooms_db.db", null, 1);
        LatLng latLng = dbHelper.getRoomLatLng(campusName, buildingName, roomNumber);
        //latLng = null;
        if(true) {
            return "1";//latLng.toString();
        } else {
            return "LatLng is null";
        }
    }
}