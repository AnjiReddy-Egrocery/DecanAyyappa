package com.dst.ayyapatelugu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.dst.ayyapatelugu.User.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread loading = new Thread() {
            public void run() {
                try {
                    // Show splash screen for 3 seconds
                    sleep(3000);

                    // Check login state
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                    Intent intent;
                    if (isLoggedIn) {
                        // Redirect to HomeActivity if user is logged in
                        intent = new Intent(SplashActivity.this, HomeActivity.class);
                    } else {
                        // Redirect to LoginActivity if user is not logged in
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    finish(); // Close SplashActivity
                }
            }
        };

        loading.start();
    }
}

