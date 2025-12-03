package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.DataBase.SharedPreferenceHelper;
import com.dst.ayyapatelugu.Model.MapDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AnadanamActivity extends AppCompatActivity implements OnMapReadyCallback {
    Toolbar toolbar;
    private GoogleMap mMap;
    private APiInterface apiClient;
    private Context context;

    ImageButton zoomInButton, zoomOutButton;

    private float currentZoomLevel = 15.0f;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private LatLng userLocation;

    private static final float ZOOM_THRESHOLD = 1.0f;

    private static final float ZOOM_LEVEL_IN = 1.0f;
    private static final float ZOOM_LEVEL_OUT = -1.0f;

    private List<MapDataResponse.Result> mapList;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadanam);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        zoomInButton = findViewById(R.id.zoom_in_button);
        zoomOutButton = findViewById(R.id.zoom_out_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initMap();
        }

        zoomInButton.setOnClickListener(v -> zoomInMap());
        zoomOutButton.setOnClickListener(v -> zoomOutMap());

        context = this;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiClient = retrofit.create(APiInterface.class);
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("AnadanamActivity", "mapFragment is null in initMap()");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            setupMarkerClickListeners();
            displayCurrentUserLocation();
            fetchLocationDataAndAddMarkers();
        }

        float initialZoomLevel = SharedPreferenceHelper.getZoomLevel(this);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoomLevel));

        mMap.setOnCameraIdleListener(() -> {
            float newZoomLevel = mMap.getCameraPosition().zoom;
            if (Math.abs(newZoomLevel - currentZoomLevel) > ZOOM_THRESHOLD) {
                currentZoomLevel = newZoomLevel;
                SharedPreferenceHelper.setZoomLevel(AnadanamActivity.this, newZoomLevel);
            }
        });
    }

    private void fetchLocationDataAndAddMarkers() {
        mapList = SharedPreferenceHelper.getTempleData(this);

        if (mapList != null && !mapList.isEmpty()) {
            new Handler().postDelayed(() -> addMarkers(mapList), 200);
        } else {
            Call<MapDataResponse> call = apiClient.getMapList();

            call.enqueue(new Callback<MapDataResponse>() {
                @Override
                public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {
                    if (response.isSuccessful()) {
                        MapDataResponse mapDataResponse = response.body();

                        if (mapDataResponse != null && "200".equals(mapDataResponse.getErrorCode())) {
                            List<MapDataResponse.Result> locations = mapDataResponse.getResult();
                            new Handler().postDelayed(() -> addMarkers(locations), 200);
                            SharedPreferenceHelper.saveTempleData(AnadanamActivity.this, locations);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MapDataResponse> call, Throwable t) {
                    Log.e("AnadanamActivity", "API call failed: " + t.getMessage(), t);
                }
            });
        }
    }

    private void addMarkers(List<MapDataResponse.Result> locations) {
        if (mMap == null || locations == null) return;

        mMap.clear();
        for (MapDataResponse.Result temple : locations) {
            try {
                LatLng position = new LatLng(
                        Double.parseDouble(temple.getLatitude()),
                        Double.parseDouble(temple.getLongitude())
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(temple.getAnnadhanamNameTelugu())
                        .snippet(temple.getLocation()));

                if (marker != null) {
                    marker.setTag(temple);
                }
            } catch (Exception e) {
                Log.e("AnadanamActivity", "Error adding marker: " + temple, e);
            }
        }
    }

    private void setupMarkerClickListeners() {
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        mMap.setOnInfoWindowClickListener(marker -> startNavigation(marker.getPosition()));
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View mContentsView;

        CustomInfoWindowAdapter() {
            mContentsView = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {

            TextView title = mContentsView.findViewById(R.id.info_window_title);
            TextView txtLocation = mContentsView.findViewById(R.id.info_location);
            TextView txtStartDate = mContentsView.findViewById(R.id.info_start_date);
            TextView txtEndDate = mContentsView.findViewById(R.id.info_end_date);
            TextView txtStartTime = mContentsView.findViewById(R.id.info_start_time);
            TextView txtEndTime = mContentsView.findViewById(R.id.info_end_time);

            title.setText(marker.getTitle());
            txtLocation.setText(marker.getSnippet());

            Object tag = marker.getTag();
            if (tag instanceof MapDataResponse.Result) {
                MapDataResponse.Result data = (MapDataResponse.Result) tag;

                String startDateRaw = safeString(data.getStartingDate());
                String endDateRaw = safeString(data.getEndingDate());
                String startTimeRaw = safeString(data.getStartTime());
                String endTimeRaw = safeString(data.getEndTime());

// ⭐ ALWAYS normalize – Telugu OR 24-hour OR English
                String startTimeClean = fixMissingAmPm(startTimeRaw);
                String endTimeClean = fixMissingAmPm(endTimeRaw);
                Log.d("TIME_CHECK", "Start=" + startTimeClean + " End=" + endTimeClean);

                String startTime24 = detectFormatAndConvert(startTimeClean);
                String endTime24   = detectFormatAndConvert(endTimeClean);

                Log.d("TIME_CHECK", "Start24=" + startTime24 + " End24=" + endTime24);

                txtStartDate.setText("Start Date: " + formatDate(startDateRaw));
                txtEndDate.setText("End Date: " + formatDate(endDateRaw));
                txtStartTime.setText("Start Time: " + safeTo12Hour(startTime24));
                txtEndTime.setText("End Time: " + safeTo12Hour(endTime24));


                boolean activeNow = isCurrentTimeBetween(startTime24, endTime24);

                if (activeNow) {
                    txtStartTime.setTextColor(Color.GREEN);
                    txtEndTime.setTextColor(Color.GREEN);
                } else {
                    txtStartTime.setTextColor(Color.RED);
                    txtEndTime.setTextColor(Color.RED);
                }

                // ********************************************
            }

            return mContentsView;
        }
    }

    private String fixMissingAmPm(String startTimeRaw) {
        if (startTimeRaw == null || startTimeRaw.isEmpty()) return null;

        try {
            // 24-hour already
            if (startTimeRaw.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
                return startTimeRaw;
            }

            // No AM/PM → guess from time
            String[] parts = startTimeRaw.split(":");
            int hour = Integer.parseInt(parts[0]);

            // Default logic:
            // Before 8 → AM, After 8 → PM
            String suffix = (hour < 8) ? " AM" : " PM";

            return startTimeRaw + suffix;

        } catch (Exception e) {
            return null;
        }
    }

    /***
     * NEW METHOD
     * Check if current time is between Start Time and End Time
     */
    private boolean isCurrentTimeBetween(String start, String end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date now = sdf.parse(sdf.format(new Date()));
            Date startTime = sdf.parse(start);
            Date endTime = sdf.parse(end);

            // Normal time (e.g., 08:00 - 20:00)
            if (startTime.before(endTime)) {
                return now.after(startTime) && now.before(endTime);
            }

            // Midnight cross (e.g., 22:00 - 05:00)
            else {
                return now.after(startTime) || now.before(endTime);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startNavigation(LatLng position) {
        String destinationStr = position.latitude + "," + position.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destinationStr);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No navigation app installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void zoomOutMap() {
        if (mMap != null) {
            float newZoomLevel = mMap.getCameraPosition().zoom + ZOOM_LEVEL_OUT;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoomLevel));
        }
    }

    private void zoomInMap() {
        if (mMap != null) {
            float newZoomLevel = mMap.getCameraPosition().zoom + ZOOM_LEVEL_IN;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(newZoomLevel));
        }
    }

    private void displayCurrentUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        moveCameraToUserLocation();
                    }
                });
    }

    private void moveCameraToUserLocation() {
        if (userLocation != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoomLevel));
        }
    }


    private String safeString(String s) {
        return s == null ? "" : s;
    }

    // ⭐ FIXED – NOW CONVERTS UNIX TIMESTAMP → dd-MM-yyyy ⭐
    private String formatDate(String inputDate) {
        try {
            if (inputDate == null || inputDate.trim().isEmpty()) return "-";

            if (inputDate.matches("\\d+")) {
                long ts = Long.parseLong(inputDate);
                if (inputDate.length() == 10) {
                    ts = ts * 1000;
                }
                Date date = new Date(ts);
                return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date);
            }

            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date parsed = in.parse(inputDate);
            return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(parsed);

        } catch (Exception e) {
            return inputDate;
        }
    }

    private String formatTime(String inputTime) {
        if (inputTime == null || inputTime.trim().isEmpty()) return "-";

        try {
            SimpleDateFormat in = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date parsed = in.parse(inputTime);

            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(parsed);

        } catch (Exception e) {
            return inputTime;
        }
    }

    private String parseTeluguTime(String raw) {

        if (raw == null || raw.trim().isEmpty()) return null;

        raw = raw.trim();
        String meridiem = ""; // AM/PM

        if (raw.contains("ఉదయం")) meridiem = "AM";
        else if (raw.contains("మధ్యాహ్నం")) meridiem = "PM";
        else if (raw.contains("సాయంత్రం")) meridiem = "PM";
        else if (raw.contains("రాత్రి")) meridiem = "PM";

        // Extract 12:30 / 3:00 etc
        String time = raw.replaceAll("[^0-9:]", "");

        if (!time.contains(":")) return null;

        // Fix 3:00 → 03:00
        if (time.matches("\\d:\\d{2}")) {
            time = "0" + time;
        }

        return time + " " + meridiem; // 03:00 PM
    }

    private String convertTo12Hour(String time24) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date d = in.parse(time24);

            SimpleDateFormat out = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return out.format(d);

        } catch (Exception e) {
            return null;
        }
    }


    private String normalizeTime(String timeRaw) {

        if (timeRaw == null || timeRaw.trim().isEmpty())
            return null;

        timeRaw = timeRaw.trim();

        // If contains Telugu characters → do not touch
        if (timeRaw.matches(".*[అ-హౠౡా-ౌ్౦-౯].*")) {
            return timeRaw;   // Example: "3:00 గం||ల వరకు"
        }

        // Telugu to AM/PM (if any)
        String teluguConverted = parseTeluguTime(timeRaw);
        if (teluguConverted != null) {
            return teluguConverted;  // e.g. "03:00 PM"
        }

        // Pure 24-hour input (13:30, 16:02, 05:00)
        if (timeRaw.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            return timeRaw;  // KEEP as 24-hour, do NOT convert here
        }

        // Already AM/PM (12:30 PM)
        if (timeRaw.toUpperCase().contains("AM") || timeRaw.toUpperCase().contains("PM")) {
            return timeRaw;
        }

        return timeRaw;

    }

    private String convertTo24Hour(String time) {
        if (time == null) return null;

        try {
            // If it's already 24-hour (e.g., 13:30)
            if (time.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
                return time;
            }

            // Convert AM/PM to 24-hour
            SimpleDateFormat in = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            SimpleDateFormat out = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return out.format(in.parse(time));

        } catch (Exception e) {
            return null;
        }    }


    private String detectFormatAndConvert(String time) {
        if (time == null || time.trim().isEmpty()) return null;

        // If already 24-hour (e.g., 15:00)
        if (time.matches("^([01]?\\d|2[0-3]):[0-5]\\d$")) {
            return time; // no change
        }

        // Else convert 12-hour to 24-hour
        return convertTo24Hour(time);
    }

    private String safeTo12Hour(String time24) {
        if (time24 == null || time24.trim().isEmpty()) return "";

        try {
            // Already 24-hour (works for 00:00 - 23:59)
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            Date date = sdf24.parse(time24);
            return sdf12.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return time24; // worst case, show raw time
        }
    }

}
