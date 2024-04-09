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
        - Home, Profile, Map (+ Street View fragment), Settings fragments as activities.
        - Clean and intuitive user interface that aims to be non-intrusive.
        - Using SQLite databases to store relevant map and user data for external storage.
        - Integration of Maps SDK For Android serves as the foundation for BCIT CampusCompass.
        - Fully compatible with a Nexus 5 running OS version 9.0 (API level 28).

    Disclaimer
    BCIT CampusCompass will only demonstrate its complete functionality exclusively for:
        - BCIT Burnaby campus, specifically building SW01.
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
    /* MEMBERS */
    // Main Activity Binding
    private ActivityMainBinding activityMainBinding;
    // FragmentManager
    private FragmentManager fragmentManager;
    // FragmentTransaction
    private FragmentTransaction fragmentTransaction;
    // Fragments
    private HomeFragment homeFragment;
    private ProfileFragment profileFragment;
    private MapFragment mapFragment;
    private SettingsFragment settingsFragment;

    // Expand Floating Action Button
    private boolean toggledExpandFab, toggledLocationFab, toggledViewFab, toggledRoomFab;
    private View.OnClickListener expandFabOnClickListener;

    public MainActivity() {

    }
    /* METHODS */
    // runs on creation of MainActivity, usually involves all initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeBinding();
        initializeFragments();
        initializeExpandFab();
    }
    // initializes binding to enable operation of fragments as activities
    private void initializeBinding() {
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
    }
    // initializes each fragment used in the application, starting at the home fragment
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
    // initialize the main floating action button that shows/hides fragment-specific actions
    private void initializeExpandFab() {
        toggledExpandFab = false;
        toggledLocationFab = false;
        toggledViewFab = false;
        toggledRoomFab = false;
        hideAllFab();
        activityMainBinding.expandFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment.isVisible()) {
                        if (toggledExpandFab) {
                            closeExpandFab();
                        } else {
                            openExpandFab(fragment);
                        }
                    }
                }
            }
        });
    }
    // shows a fragment based on what navigation tab is clicked by the user
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
    // hides all the fragment-specific floating action buttons
    private void hideAllFab() {
        activityMainBinding.centerMapFab.hide();
        activityMainBinding.focusBuildingFab.hide();
        activityMainBinding.toggleLocationFab.hide();
        activityMainBinding.toggleViewFab.hide();
        activityMainBinding.toggleRoomFab.hide();
    }
    // closes the main floating action button
    private void closeExpandFab() {
        hideAllFab();
        activityMainBinding.expandFab.animate().rotation(0);
        toggledExpandFab = false;
    }
    // opens the main floating action button
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
            activityMainBinding.toggleRoomFab.show();

        }
        if(fragment instanceof SettingsFragment) {
        }
        activityMainBinding.expandFab.animate().rotation(225f);
        toggledExpandFab = true;
    }
    // runs when the main fab is opened while viewing the Map Fragment
    public FloatingActionButton[] getMapButtons() {
        return new FloatingActionButton[] {activityMainBinding.centerMapFab, activityMainBinding.focusBuildingFab, activityMainBinding.toggleLocationFab, activityMainBinding.toggleViewFab, activityMainBinding.toggleRoomFab};
    }
    // runs when the toggle location floating action button is pressed while viewing the Map Fragment
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
    public boolean getLocationFabState() {
        return toggledLocationFab;
    }

    // runs when the toggle view floating action button is pressed while viewing the Map Fragment
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
    public boolean getViewFabState() {
        return toggledViewFab;
    }

    // runs when the toggle find room floating action button is pressed while viewing the Map Fragment
    public void toggledRoomFab() {
        if (toggledRoomFab) {
            toggledRoomFab = false;
            activityMainBinding.toggleRoomFab.setImageResource(R.drawable.rounded_explore_off_24);
        }
        else {
            toggledRoomFab = true;
            activityMainBinding.toggleRoomFab.setImageResource(R.drawable.rounded_explore_24);
        }
    }
    public boolean getRoomFabState() {
        return toggledRoomFab;
    }
}