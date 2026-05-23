package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean firstLaunch = prefs.getBoolean("first_launch", true);
            boolean loggedIn = prefs.getBoolean("logged_in", false);

            Intent intent;

            if(firstLaunch){
                intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                prefs.edit().putBoolean("first_launch", false).apply();
            } else if(loggedIn){
                String role = prefs.getString("role", "customer");
                if(role.equals("owner")){
                    intent = new Intent(SplashActivity.this, DoctorMainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, PatientMainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, AuthActivity.class);
            }

            startActivity(intent);
            finish();
        }, 2500);
    }
}
