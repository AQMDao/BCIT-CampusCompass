package org.bcit.campuscompass;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Arrays;

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

        double[] test = {5.3, 3.2, 1.6};

        // set debug message to the textview
        //debugTextView.setText(getValuetoDebug());
        debugTextView.setText(Arrays.deepToString(getValuetoDebug()));
        debugTextView.setVisibility(View.VISIBLE);

        return rootView;

    }

    //test function to see if we can get values from databases
    public double[][] getValuetoDebug() {
        DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
        try {
            dbHelper.copyDatabase();
            dbHelper.openDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return new double[]{1.0,2.0,3.0};
        return dbHelper.getLocationDimensions("SW1_floor_1");
    }

    // hi michael here
}