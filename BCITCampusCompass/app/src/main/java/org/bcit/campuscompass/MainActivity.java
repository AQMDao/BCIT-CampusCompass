/*
    Authors: Michael Dao, Eddie Sherban

    COMP 7855 Project
    The purpose is to design and develop an Android application to address a specific need and
    practice software engineering concepts through proper documentation. It should adhere to the
    following Minimum Viable Product (MVP) requirements:
        - Must have multiple activities.
        - Implement clean and intuitive user interfaces.
        - Database usage: utilize local data storage (SQLite) for storing application data.
        - Integrate at least one external API.
        - Ensure compatibility w/ Android devices running OS versions 9.0 (API level 28) and above.

    BCIT CampusCompass
    An Android application that addresses the lack of an interactive map for the BCIT Burnaby campus
    (specifically indoor maps) with Google Maps' functionality. As a Minimum Viable Product:
        - Multiple activities involving interaction with and navigating BCIT.
        - Clean and intuitive user interface that aims to be non-intrusive.
        - Using SQLite databases to store relevant map and user data for external storage.
        - Integration of Google Maps' API serves as the foundation for BCIT CampusCompass.
        - Fully compatible with a Nexus 5 running OS version 9.0 (API level 28).

    Disclaimer
    BCIT CampusCompass will only demonstrate its complete functionality exclusively for:
        - BCIT Burnaby campus and its building SW01.
*/

package org.bcit.campuscompass;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.bcit.campuscompass.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    /* LOCAL DATA */
    // any local data that is hard stored into the app, will remove upon integration with sqlite

    /* MEMBERS */

    // Main Activity Binding
    private ActivityMainBinding activityMainBinding;

    // Navigation Fragments, Manager, and Transaction
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private MapFragment mapFragment;
    private SettingsFragment settingsFragment;

    // Expand Floating Action Button
    private boolean toggledExpandFab, toggledLocationFab, toggledViewFab;
    private View.OnClickListener expandFabOnClickListener;

    public MainActivity() {

    }
    /* METHODS */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeBinding();
        initializeFragments();
        initializeExpandFab();
        toggledLocationFab = false;
        toggledViewFab = false;
    }

    /* HELPER FUNCTIONS */
    private void initializeBinding() {
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
    }
    private void initializeFragments() {
        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        mapFragment = new MapFragment();
        settingsFragment = new SettingsFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fl, homeFragment);
        fragmentTransaction.add(R.id.main_fl,profileFragment);
        fragmentTransaction.add(R.id.main_fl,mapFragment);
        fragmentTransaction.add(R.id.main_fl,settingsFragment);
        fragmentTransaction.hide(profileFragment);
        fragmentTransaction.hide(mapFragment);
        fragmentTransaction.hide(settingsFragment);
        fragmentTransaction.commit();

        activityMainBinding.mainBnv.setSelectedItemId(R.id.home_navigation);

        activityMainBinding.mainBnv.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.home_navigation)
                    showFragment(homeFragment);
                if(item.getItemId() == R.id.profile_navigation)
                    showFragment(profileFragment);
                if(item.getItemId() == R.id.map_navigation)
                    showFragment(mapFragment);
                if(item.getItemId() == R.id.settings_navigation)
                    showFragment(settingsFragment);
                return true;
            }
        });
    }
    private void initializeExpandFab() {
        toggledExpandFab = false;
        hideAllFab();
        initializeAllFab();
        activityMainBinding.expandFab.setOnClickListener(expandFabOnClickListener);
    }
    private void initializeAllFab() {
        expandFabOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment.isVisible()) {
                        if(toggledExpandFab) {
                            closeExpandFab();
                        }
                        else {
                            openExpandFab(fragment);
                        }
                    }
                }
            }
        };
    }
    private void showFragment(Fragment fragmentToShow) {
        fragmentTransaction = fragmentManager.beginTransaction();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if(fragment == fragmentToShow) {
                fragmentTransaction.show(fragment);
                if (toggledExpandFab) openExpandFab(fragment);
            }
            else {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.commit();
    }
    private void hideAllFab() {
        activityMainBinding.centerMapFab.hide();
        activityMainBinding.focusBuildingFab.hide();
        activityMainBinding.toggleLocationFab.hide();
        activityMainBinding.toggleViewFab.hide();
    }
    private void closeExpandFab() {
        hideAllFab();
        activityMainBinding.expandFab.animate().rotation(0);
        toggledExpandFab = false;
    }
    private void openExpandFab(Fragment fragment) {
        hideAllFab();
        if(fragment instanceof HomeFragment) {
        }
        if(fragment instanceof ProfileFragment) {
        }
        if(fragment instanceof MapFragment) {
            activityMainBinding.centerMapFab.show();
            activityMainBinding.focusBuildingFab.show();
            activityMainBinding.toggleLocationFab.show();
            activityMainBinding.toggleViewFab.show();

        }
        if(fragment instanceof SettingsFragment) {
        }
        activityMainBinding.expandFab.animate().rotation(225f);
        toggledExpandFab = true;
    }
    public FloatingActionButton[] getMapButtons() {
        return new FloatingActionButton[] {activityMainBinding.centerMapFab, activityMainBinding.focusBuildingFab, activityMainBinding.toggleLocationFab, activityMainBinding.toggleViewFab};
    }

    public void toggleLocationFab() {
        if (toggledLocationFab) {
            toggledLocationFab = false;
            activityMainBinding.toggleLocationFab.setImageResource(R.drawable.rounded_location_searching_24);
        }
        else {
            toggledLocationFab = true;
            activityMainBinding.toggleLocationFab.setImageResource(R.drawable.rounded_my_location_24);
        }
    }

    public void toggledViewFab() {
        if (toggledViewFab) {
            toggledViewFab = false;
            activityMainBinding.toggleViewFab.setImageResource(R.drawable.rounded_splitscreen_24);
        }
        else {
            toggledViewFab = true;
            activityMainBinding.toggleViewFab.setImageResource(R.drawable.rounded_map_24);
        }
    }
    public boolean getLocationFabState() {
        return toggledLocationFab;
    }
    public boolean getViewFabState() {
        return toggledViewFab;
    }
}