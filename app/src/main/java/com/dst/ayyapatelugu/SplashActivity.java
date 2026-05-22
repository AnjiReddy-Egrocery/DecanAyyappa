package com.dst.ayyapatelugu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.dst.ayyapatelugu.Activity.AyyapaKarmaDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyapaMandaliDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyapaPetamDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBooksDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaTourseActivity;
import com.dst.ayyapatelugu.Activity.BlogDetailsActivity;
import com.dst.ayyapatelugu.Activity.CalenderActivity;
import com.dst.ayyapatelugu.Activity.GuruSwamiDetailsActivity;
import com.dst.ayyapatelugu.Activity.ImagesListActivity;
import com.dst.ayyapatelugu.Activity.PadayatraDetailsActivity;
import com.dst.ayyapatelugu.Activity.PanchagamActivity;
import com.dst.ayyapatelugu.Activity.PostVideosActivity;
import com.dst.ayyapatelugu.Activity.ProductDetailsActivity;
import com.dst.ayyapatelugu.Activity.TeluguCalenderActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAnadanamDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
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
                    String guruswamiId = incomingIntent.getStringExtra("guruswamiId");
                    String bajanamandaliId = incomingIntent.getStringExtra("bajanamandaliId");
                    String tourpackageId = incomingIntent.getStringExtra("tourpackageId");
                    String decoratorId = incomingIntent.getStringExtra("decoratorId");
                    String productId = incomingIntent.getStringExtra("productId");
                    String bookId = incomingIntent.getStringExtra("bookId");
                    String activitiesId = incomingIntent.getStringExtra("activitiesId");
                    String Year = incomingIntent.getStringExtra("Year");
                    String month = incomingIntent.getStringExtra("month");
                    String year = incomingIntent.getStringExtra("year");
                    String date = incomingIntent.getStringExtra("date");
                    String blogId = incomingIntent.getStringExtra("blogId");
                    String songId = incomingIntent.getStringExtra("songId");
                    String padayatrabrundamId = incomingIntent.getStringExtra("padayatrabrundamId");
                    String templeId = incomingIntent.getStringExtra("templeId");
                    String ayyappatempleId = incomingIntent.getStringExtra("templeId");
                    String annadhanamId = incomingIntent.getStringExtra("annadhanamId");
                    String images = incomingIntent.getStringExtra("images");
                    String videos = incomingIntent.getStringExtra("videos");

                    Intent intent;

                    if (newsId != null && !newsId.isEmpty()) {
                        // 🔥 Direct ga News Details ki vellali
                        intent = new Intent(SplashActivity.this, ViewAllNewsDetailsActivity.class);
                        intent.putExtra("newsId", newsId);
                        Log.d("FCM_DEBUG", "Received newsId: " + newsId);
                    }
                    else if (guruswamiId != null && !guruswamiId.isEmpty()){
                         intent = new Intent(SplashActivity.this, GuruSwamiDetailsActivity.class);
                         intent.putExtra("guruswamiId",guruswamiId);
                        Log.d("FCM_DEBUG", "Received guruSwamiId: " + guruswamiId);
                    }
                    else if (bajanamandaliId != null && !bajanamandaliId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyapaMandaliDetailsActivity.class);
                        intent.putExtra("bajanamandaliId",bajanamandaliId);
                        Log.d("FCM_DEBUG", "Received bajanamandaliId: " + bajanamandaliId);
                    }
                    else if (tourpackageId != null && !tourpackageId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyappaTourseActivity.class);
                        intent.putExtra("tourpackageId",tourpackageId);
                        Log.d("FCM_DEBUG", "Received tourpackageId: " + tourpackageId);
                    }
                    else if (decoratorId != null && !decoratorId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyapaPetamDetailsActivity.class);
                        intent.putExtra("decoratorId",decoratorId);
                        Log.d("FCM_DEBUG", "Received decoratorId: " + decoratorId);
                    }
                    else if (productId != null && !productId.isEmpty()){
                        intent = new Intent(SplashActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("productId",productId);
                        Log.d("FCM_DEBUG", "Received productId: " + productId);
                    }
                    else if (bookId != null && !bookId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyappaBooksDetailsActivity.class);
                        intent.putExtra("bookId",bookId);
                        Log.d("FCM_DEBUG", "Received bookId: " + bookId);
                    }
                    else if (activitiesId != null && !activitiesId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyapaKarmaDetailsActivity.class);
                        intent.putExtra("activitiesId",activitiesId);
                        Log.d("FCM_DEBUG", "Received activitiesId: " + activitiesId);
                    }
                    else if (Year != null && !Year.isEmpty()){
                        intent = new Intent(SplashActivity.this, CalenderActivity.class);
                        intent.putExtra("Year",Year);
                        Log.d("FCM_DEBUG", "Received Year: " + Year);
                    }
                    else if (date != null && !date.isEmpty()) {

                        intent = new Intent(SplashActivity.this, PanchagamActivity.class);

                        intent.putExtra("date", date);

                        Log.d("FCM_DEBUG", "Received Panchagam Date: " + date);
                    }
                    else if (month != null && !month.isEmpty()
                            && year != null && !year.isEmpty()) {

                        intent = new Intent(SplashActivity.this, TeluguCalenderActivity.class);

                        intent.putExtra("month", month);
                        intent.putExtra("year", year);

                        Log.d("FCM_DEBUG", "Received Telugu Month: " + month);
                        Log.d("FCM_DEBUG", "Received Telugu Year: " + year);
                    }
                    else if (blogId != null && !blogId.isEmpty()){
                        intent = new Intent(SplashActivity.this, BlogDetailsActivity.class);
                        intent.putExtra("blogId",blogId);
                        Log.d("FCM_DEBUG", "Received blogId: " + blogId);
                    }
                    else if (songId != null && !songId.isEmpty()){
                        intent = new Intent(SplashActivity.this, AyyappaBajanaSognsDetailsActivity.class);
                        intent.putExtra("songId",songId);
                        Log.d("FCM_DEBUG", "Received songId: " + songId);
                    }
                    else if (padayatrabrundamId != null && !padayatrabrundamId.isEmpty()){
                        intent = new Intent(SplashActivity.this, PadayatraDetailsActivity.class);
                        intent.putExtra("padayatrabrundamId",padayatrabrundamId);
                        Log.d("FCM_DEBUG", "Received padayatrabrundamId: " + padayatrabrundamId);
                    }
                    else if (templeId != null && !templeId.isEmpty()){
                        intent = new Intent(SplashActivity.this, ViewTempleListDetailsActivity.class);
                        intent.putExtra("templeId",templeId);
                        Log.d("FCM_DEBUG", "Received templeId: " + templeId);
                    }

                    else if (ayyappatempleId != null && !ayyappatempleId.isEmpty()){
                        intent = new Intent(SplashActivity.this, ViewTempleListDetailsActivity.class);
                        intent.putExtra("templeId",ayyappatempleId);
                        Log.d("FCM_DEBUG", "Received ayyappatempleid: " + ayyappatempleId);
                    }
                    else if (annadhanamId != null && !annadhanamId.isEmpty()){
                        intent = new Intent(SplashActivity.this, ViewAnadanamDetailsActivity.class);
                        intent.putExtra("annadhanamId",annadhanamId);
                        Log.d("FCM_DEBUG", "Received annadhanamId: " + annadhanamId);
                    }
                    else if (images != null && images.equals("true")) {

                        intent = new Intent(SplashActivity.this, ImagesListActivity.class);

                        Log.d("FCM_DEBUG", "Open Images List Activity");
                    }
                    else if (videos != null && videos.equals("true")) {

                        intent = new Intent(SplashActivity.this, PostVideosActivity.class);

                        Log.d("FCM_DEBUG", "Open Videos Activity");
                    }

                    else {
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

