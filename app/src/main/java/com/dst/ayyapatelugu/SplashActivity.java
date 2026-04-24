package com.dst.ayyapatelugu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.User.LoginActivity;
import android.Manifest;
import android.util.Log;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askNotificationPermission();
        Thread loading = new Thread() {
            public void run() {
                try {
                    // Show splash screen for 3 seconds
                    sleep(3000);

                    // Check login state
                    SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                    Intent incomingIntent = getIntent();
                    String newsId = incomingIntent.getStringExtra("newsId");

                    Intent intent;

                    if (newsId != null && !newsId.isEmpty()) {
                        // 🔥 Direct ga News Details ki vellali
                        intent = new Intent(SplashActivity.this, ViewAllNewsDetailsActivity.class);
                        intent.putExtra("newsId", newsId);
                        Log.d("FCM_DEBUG", "Received newsId: " + newsId);
                    } else {
                        if (isLoggedIn) {
                            intent = new Intent(SplashActivity.this, HomeActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, LoginActivity.class);
                        }
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

    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }
    }
}

