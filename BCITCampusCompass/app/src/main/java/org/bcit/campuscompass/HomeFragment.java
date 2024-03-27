package org.bcit.campuscompass;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

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
        //String debugMessage = "Latitude and Longitude: " + getValuetoDebug("Burnaby Campus");

        // set debug message to the textview
        debugTextView.setText("debugMessage");
        debugTextView.setVisibility(View.VISIBLE);

        return rootView;

    }

    public String getValuetoDebug(String campusName) {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.copyDatabase();
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dbHelper.getRoomDimensions();
    }

    // hi michael here
}