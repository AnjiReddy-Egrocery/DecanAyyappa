package com.dst.ayyapatelugu.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dst.ayyapatelugu.Activity.AnadanamActivity;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class AnadanamMapFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private APiInterface apiClient;

    ImageButton zoomInButton, zoomOutButton;
    private float currentZoomLevel = 15f;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private LatLng userLocation;
    private List<MapDataResponse.Result> mapList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.anadanam_map,container,false);
        zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomOutButton = view.findViewById(R.id.zoom_out_button);

        // Permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );

        } else {
            initMap();
        }

        zoomInButton.setOnClickListener(v -> zoomInMap());
        zoomOutButton.setOnClickListener(v -> zoomOutMap());

        // Retrofit setup
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(),
                        UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        apiClient = retrofit.create(APiInterface.class);
        return view;
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Handle the case where mapFragment is null
            Log.e("MapFragment", "Map fragment is null");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            setupMarkerClicks();
            displayCurrentUserLocation();
            fetchLocationData();
        }

        float savedZoom = SharedPreferenceHelper.getZoomLevel(getContext());
        mMap.moveCamera(CameraUpdateFactory.zoomTo(savedZoom));

        mMap.setOnCameraIdleListener(() -> {
            currentZoomLevel = mMap.getCameraPosition().zoom;
            SharedPreferenceHelper.setZoomLevel(getContext(), currentZoomLevel);
        });
    }

    private void fetchLocationData() {
        mapList = SharedPreferenceHelper.getTempleData(getContext());

        if (mapList != null && !mapList.isEmpty()) {
            new Handler().postDelayed(() -> addMarkers(mapList), 200);
        } else {
            Call<MapDataResponse> call = apiClient.getMapList();
            call.enqueue(new Callback<MapDataResponse>() {
                @Override
                public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {
                    if (response.isSuccessful() && response.body() != null &&
                            "200".equals(response.body().getErrorCode())) {

                        List<MapDataResponse.Result> data = response.body().getResult();
                        SharedPreferenceHelper.saveTempleData(getContext(), data);
                        new Handler().postDelayed(() -> addMarkers(data), 200);
                    }
                }

                @Override
                public void onFailure(Call<MapDataResponse> call, Throwable t) {
                }
            });
        }
    }

    // ⭐ ADD MARKERS WITH GREEN/RED LOGIC ⭐
    private void addMarkers(List<MapDataResponse.Result> locations) {
        if (mMap == null) return;

        mMap.clear();

        for (MapDataResponse.Result temple : locations) {
            try {
                LatLng pos = new LatLng(
                        Double.parseDouble(temple.getLatitude()),
                        Double.parseDouble(temple.getLongitude())
                );

                boolean isActive = isCurrentDateTimeBetween(
                        temple.getStartingDate(),
                        temple.getEndingDate(),
                        temple.getStartTime(),
                        temple.getEndTime()
                );

                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(
                        isActive ? R.drawable.marker_green : R.drawable.marker_red
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(temple.getAnnadhanamNameTelugu())
                        .snippet(temple.getLocation())
                        .icon(icon)
                );

                marker.setTag(new Object[]{ temple, isActive });

            } catch (Exception ignored) { }
        }
    }

    private void setupMarkerClicks() {
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });

        mMap.setOnInfoWindowClickListener(marker ->
                startNavigation(marker.getPosition()));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isCurrentDateTimeBetween(String startDate, String endDate,
                                             String startTime, String endTime) {
        try {
            // Convert UNIX timestamps to millis
            long startDateMillis = Long.parseLong(startDate) * 1000;
            long endDateMillis = Long.parseLong(endDate) * 1000;

            // Parse time only (HH:mm:ss or HH:mm)
            SimpleDateFormat timeFormat;
            if (startTime.length() == 5)
                timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            else
                timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

            Date startTimeDate = timeFormat.parse(startTime);
            Date endTimeDate = timeFormat.parse(endTime);

            // Create full start datetime
            Calendar startCal = Calendar.getInstance();
            startCal.setTimeInMillis(startDateMillis);
            startCal.set(Calendar.HOUR_OF_DAY, startTimeDate.getHours());
            startCal.set(Calendar.MINUTE, startTimeDate.getMinutes());
            startCal.set(Calendar.SECOND, 0);

            // Create full end datetime
            Calendar endCal = Calendar.getInstance();
            endCal.setTimeInMillis(endDateMillis);
            endCal.set(Calendar.HOUR_OF_DAY, endTimeDate.getHours());
            endCal.set(Calendar.MINUTE, endTimeDate.getMinutes());
            endCal.set(Calendar.SECOND, 0);

            long now = System.currentTimeMillis();

            return now >= startCal.getTimeInMillis() && now <= endCal.getTimeInMillis();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private long convertToTimestamp(String dateStr, String timeStr) {
        try {
            // If UNIX timestamp
            if (dateStr.matches("\\d+")) {
                long date = Long.parseLong(dateStr);
                if (dateStr.length() == 10) date *= 1000;
                return date;
            }

            SimpleDateFormat dateTimeFormat =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            return dateTimeFormat.parse(dateStr + " " + timeStr).getTime();

        } catch (Exception e) {
            return 0;
        }
    }

    private void startNavigation(LatLng pos) {
        Uri uri = Uri.parse("google.navigation:q=" + pos.latitude + "," + pos.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }

    private void zoomInMap() {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom + 1));
    }

    private void zoomOutMap() {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom - 1));
    }

    private void displayCurrentUserLocation() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(getContext());

        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoomLevel));
            }
        });
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(@NonNull Marker marker) {

            View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);

            TextView title = view.findViewById(R.id.info_window_title);
            TextView loc = view.findViewById(R.id.info_location);
            TextView sDate = view.findViewById(R.id.info_start_date);
            TextView eDate = view.findViewById(R.id.info_end_date);
            TextView sTime = view.findViewById(R.id.info_start_time);
            TextView eTime = view.findViewById(R.id.info_end_time);

            title.setText(marker.getTitle());
            loc.setText(marker.getSnippet());

            Object tagObj = marker.getTag();

            if (tagObj instanceof Object[]) {

                Object[] arr = (Object[]) tagObj;

                MapDataResponse.Result d = (MapDataResponse.Result) arr[0];
                boolean isActive = (boolean) arr[1];   // <-- GREEN marker? or RED marker?

                sDate.setText("Start Date: " + formatDate(d.getStartingDate()));
                eDate.setText("End Date: " + formatDate(d.getEndingDate()));
                sTime.setText("Start Time: " + formatTime(d.getStartTime()));
                eTime.setText("End Time: " + formatTime(d.getEndTime()));

                // 🔥 If marker is RED → ALWAYS UNCONDITIONALLY RED
                // 🔥 If marker is GREEN → Check time
                int finalColor;

                if (!isActive) {
                    // ❗ marker RED → start & end time RED
                    finalColor = Color.RED;
                } else {

                    // ✔ Only for GREEN marker → time-base check
                    try {
                        SimpleDateFormat timeFormat;

                        if (d.getStartTime().length() == 5)
                            timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        else
                            timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

                        Date startTime = timeFormat.parse(d.getStartTime());
                        Date endTime   = timeFormat.parse(d.getEndTime());

                        Calendar cal = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            cal = Calendar.getInstance();
                        }
                        String nowStr = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            nowStr = String.format("%02d:%02d",
                                    cal.get(Calendar.HOUR_OF_DAY),
                                    cal.get(Calendar.MINUTE)
                            );
                        }

                        Date now = timeFormat.parse(nowStr);

                        boolean isInTimeRange =
                                now.equals(startTime) ||
                                        now.equals(endTime) ||
                                        (now.after(startTime) && now.before(endTime));

                        finalColor = isInTimeRange ? Color.GREEN : Color.RED;

                    } catch (Exception e) {
                        finalColor = Color.RED;
                    }
                }

                sTime.setTextColor(finalColor);
                eTime.setTextColor(finalColor);
            }

            return view;
        }

        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }
    }


    private String formatDate(String input) {
        try {
            if (input.matches("\\d+")) {
                long ts = Long.parseLong(input);
                if (input.length() == 10) ts *= 1000;
                return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        .format(new Date(ts));
            }

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = f.parse(input);
            return new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(d);

        } catch (Exception e) { return input; }
    }

    private String formatTime(String t) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Date d = in.parse(t);
            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d);
        } catch (Exception e) { return t; }
    }
}

