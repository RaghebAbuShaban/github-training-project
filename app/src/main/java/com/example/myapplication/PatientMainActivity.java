package com.example.myapplication;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PatientMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);

        bottomNav = findViewById(R.id.bottomNav);

        if (savedInstanceState == null) {
            replaceFragment(new PatientHomeFragment(), false);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            Fragment selected = null;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    if (!(current instanceof PatientHomeFragment))
                        selected = new PatientHomeFragment();
                    break;

                case R.id.nav_appointment:
                    if (!(current instanceof PatientAppointmentsFragment))
                        selected = new PatientAppointmentsFragment();
                    break;

                case R.id.nav_MedicalRecord:
                    if (!(current instanceof PatientMedicalRecordFragment))
                        selected = new PatientMedicalRecordFragment();
                    break;

                case R.id.nav_profile:
                    if (!(current instanceof PatientProfileFragment))
                        selected = new PatientProfileFragment();
                    break;
            }

            if (selected != null) replaceFragment(selected, true);
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (current instanceof PatientHomeFragment) {
                bottomNav.setSelectedItemId(R.id.nav_home);
            } else if (current instanceof PatientAppointmentsFragment) {
                bottomNav.setSelectedItemId(R.id.nav_appointment);
            }else if (current instanceof PatientMedicalRecordFragment) {
                bottomNav.setSelectedItemId(R.id.nav_MedicalRecord);
            }else if (current instanceof PatientProfileFragment) {
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
                    PatientMainActivity.super.onBackPressed();
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
