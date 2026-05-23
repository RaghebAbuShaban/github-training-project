package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DoctorMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            replaceFragment(new DoctorAppointmentsFragment(), false);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            Fragment selected = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_appointment) {
                if (!(current instanceof DoctorAppointmentsFragment)) selected = new DoctorAppointmentsFragment();
            } else if (itemId == R.id.nav_profile) {
                if (!(current instanceof DoctorProfileFragment)) selected = new DoctorProfileFragment();
            }
            if (selected != null) replaceFragment(selected, true);
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (current instanceof DoctorAppointmentsFragment) {
                bottomNav.setSelectedItemId(R.id.nav_appointment);
            } else if (current instanceof DoctorProfileFragment) {
                bottomNav.setSelectedItemId(R.id.nav_profile);
            }
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    setEnabled(false);
                    DoctorMainActivity.super.onBackPressed();
                }
            }
        });
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        var transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment);
        if (addToBackStack) transaction.addToBackStack(null);
        transaction.commit();
    }
}
