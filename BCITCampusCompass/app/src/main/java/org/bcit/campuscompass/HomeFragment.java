package org.bcit.campuscompass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import org.bcit.campuscompass.DatabaseHelper;
import org.bcit.campuscompass.R;

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
        debugTextView.setText(getValuetoDebug("lol"));
        debugTextView.setVisibility(View.VISIBLE);

        return rootView;

    }

    //test function to see if we can get values from databases
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