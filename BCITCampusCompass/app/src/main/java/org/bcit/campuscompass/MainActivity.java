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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import org.bcit.campuscompass.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    // Local Data (to be accessed via SQLite database when implemented)

    // Fragments
    Fragment homeFragment;
    Fragment profileFragment;
    Fragment mapFragment;
    Fragment settingsFragment;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    BottomNavigationView bottomNavigationView;

    // binding
    private ActivityMainBinding binding;
    // fab menu
    private boolean isFloatingActionButtonExpanded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFragments();

        binding.mapCenterFloatingActionButton.hide();
        binding.mapLocationFloatingActionButton.hide();

        binding.mainFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandFloatingActionButtonMenu();
            }
        });
    }

    private void initializeFragments() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        homeFragment = new HomeFragment();
        profileFragment = new ProfileFragment();
        mapFragment = new MapFragment();
        settingsFragment = new SettingsFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_constraint_layout, homeFragment);
        fragmentTransaction.show(homeFragment);
        fragmentTransaction.add(R.id.main_constraint_layout,profileFragment);
        fragmentTransaction.hide(profileFragment);
        fragmentTransaction.add(R.id.main_constraint_layout,mapFragment);
        fragmentTransaction.hide(mapFragment);
        fragmentTransaction.add(R.id.main_constraint_layout,settingsFragment);
        fragmentTransaction.hide(settingsFragment);
        fragmentTransaction.commit();



        bottomNavigationView.setSelectedItemId(R.id.home_navigation_button);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home_navigation_button) {
                showFragment(homeFragment);
            }
            if(item.getItemId() == R.id.profile_navigation_button) {
                showFragment(profileFragment);
            }
            if(item.getItemId() == R.id.map_navigation_button) {
                showFragment(mapFragment);
            }
            if(item.getItemId() == R.id.settings_navigation_button) {
                showFragment(settingsFragment);
            }
            return true;
        });
    }
    private void showFragment(Fragment fragmentToShow) {
        fragmentTransaction = fragmentManager.beginTransaction();

        for (Fragment fragment : fragmentManager.getFragments()) {
            if(fragment == fragmentToShow) {
                fragmentTransaction.show(fragment);
            }
            else {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.commit();
    }

    private void expandFloatingActionButtonMenu() {
        if(isFloatingActionButtonExpanded) {
            isFloatingActionButtonExpanded = false;
            binding.mainFloatingActionButton.animate().rotationBy(-45f);

            binding.mapCenterFloatingActionButton.hide();
            binding.mapLocationFloatingActionButton.hide();
        }
        else {
            isFloatingActionButtonExpanded = true;
            binding.mainFloatingActionButton.animate().rotationBy(45f);

            binding.mapCenterFloatingActionButton.show();
            binding.mapLocationFloatingActionButton.show();
        }
    }
}