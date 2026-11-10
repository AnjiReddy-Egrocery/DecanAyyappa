package com.dst.ayyapatelugu.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.ayyapatelugu.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SendTemplesDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    Toolbar toolbar;
    ScrollView scrollView;
    EditText edtName,  edtStartTime, edtEndTime, edtLocation, edtAddress;

    ImageButton btnSearch;

    GoogleMap googleMap;
    FusedLocationProviderClient fusedClient;

    // selected location (submit ki vadataru)
    double selectedLat = 0, selectedLng = 0;
    boolean hasLocation = false;

    boolean ignoreNextIdle = false;
    int geocodeRequestId = 0;

    Calendar startCal = null, endCal = null;
    int startH = -1, startM = -1, endH = -1, endM = -1;

    private static final String LOCATION_HINT = "Location Map (type or move map)";

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                        || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                if (granted) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(this, "Location permission denied. Search or move the map",
                            Toast.LENGTH_LONG).show();
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_temples_details);

        toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scrollView);
        edtName = findViewById(R.id.edt_name);
        edtStartTime = findViewById(R.id.edt_start_time);
        edtEndTime = findViewById(R.id.edt_end_time);
        edtAddress = findViewById(R.id.edt_display_address);
        edtLocation = findViewById(R.id.edt_location);
        btnSearch = findViewById(R.id.btn_search);

        setSupportActionBar(toolbar);
        ;
        getSupportActionBar().setDisplayShowTitleEnabled(false);   // <- add
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        fusedClient = LocationServices.getFusedLocationProviderClient(this);
/*
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());*/

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        findViewById(R.id.map_touch_overlay).setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                scrollView.requestDisallowInterceptTouchEvent(true);
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        });

        findViewById(R.id.btn_my_location).setOnClickListener(v -> fetchCurrentLocation());

        edtStartTime.setOnClickListener(v -> pickTime(true));
        edtEndTime.setOnClickListener(v -> pickTime(false));

        btnSearch.setOnClickListener(v -> searchLocation());
        edtLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchLocation();
                return true;
            }
            return false;
        });

        findViewById(R.id.but_submit).setOnClickListener(v -> {
            if (validate()) {
                String name = edtName.getText().toString().trim();
                String startTime = edtStartTime.getText().toString();
                String endTime = edtEndTime.getText().toString();
                String address= edtAddress.getText().toString();
                String location = edtLocation.getText().toString().trim();
                String lat = String.format(Locale.US, "%.6f", selectedLat);
                String lng = String.format(Locale.US, "%.6f", selectedLng);

                // TODO: ikkada API call cheyandi (name, type, dates, times, location, lat, lng)
                Toast.makeText(this, "Details submitted successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------- MAP ----------------
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // map aagipoyaka: center pin = selected location
        googleMap.setOnCameraIdleListener(() -> {
            edtLocation.setHint(LOCATION_HINT);
            if (ignoreNextIdle) {
                ignoreNextIdle = false;
                return;
            }
            LatLng center = googleMap.getCameraPosition().target;
            selectedLat = center.latitude;
            selectedLng = center.longitude;
            hasLocation = true;
            reverseGeocode(center);
        });

        googleMap.setOnCameraMoveStartedListener(reason -> {
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                edtLocation.setHint("Move map to select location...");
            }
        });

        // default: India, taruvata current location ki velthundi
        ignoreNextIdle = true;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.5937, 78.9629), 4f));

        fetchCurrentLocation();
    }

    // ---------------- CURRENT LOCATION ----------------
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        if (googleMap == null) return;

        if (!hasLocationPermission()) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }

        googleMap.setMyLocationEnabled(true);          // blue dot
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,
                        new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        moveToLocation(location);
                    } else {
                        fusedClient.getLastLocation().addOnSuccessListener(last -> {
                            if (last != null) {
                                moveToLocation(last);
                            } else {
                                Toast.makeText(this, "Unable to get location. Turn on GPS",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show());
    }

    private void moveToLocation(Location location) {
        if (googleMap == null || isFinishing()) return;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // pin map center lo untundi, camera ikkadiki velthe pin current location lo untundi
        selectedLat = latLng.latitude;
        selectedLng = latLng.longitude;
        hasLocation = true;

        // camera kadilithe idle listener address fill chestundi; kadalakapote manual ga
        LatLng current = googleMap.getCameraPosition().target;
        boolean same = Math.abs(current.latitude - latLng.latitude) < 0.00001
                && Math.abs(current.longitude - latLng.longitude) < 0.00001;
        ignoreNextIdle = false;
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
        if (same) reverseGeocode(latLng);
    }

    // lat/long -> address
    private void reverseGeocode(LatLng latLng) {
        final int requestId = ++geocodeRequestId;
        new Thread(() -> {
            String text = String.format(Locale.US, "%.6f, %.6f", latLng.latitude, latLng.longitude);
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (list != null && !list.isEmpty() && list.get(0).getMaxAddressLineIndex() >= 0) {
                    text = list.get(0).getAddressLine(0);
                }
            } catch (IOException ignored) { }
            String finalText = text;
            runOnUiThread(() -> {
                if (requestId == geocodeRequestId && !isFinishing()) {
                    edtLocation.setText(finalText);
                }
            });
        }).start();
    }

    // address -> lat/long
    private void searchLocation() {
        String query = edtLocation.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            edtLocation.setError("Enter a location");
            return;
        }
        hideKeyboard();

        new Thread(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocationName(query, 1);
                if (list != null && !list.isEmpty()) {
                    Address a = list.get(0);
                    LatLng latLng = new LatLng(a.getLatitude(), a.getLongitude());
                    runOnUiThread(() -> {
                        if (googleMap == null) return;
                        LatLng current = googleMap.getCameraPosition().target;
                        boolean same = Math.abs(current.latitude - latLng.latitude) < 0.00001
                                && Math.abs(current.longitude - latLng.longitude) < 0.00001;

                        selectedLat = latLng.latitude;
                        selectedLng = latLng.longitude;
                        hasLocation = true;

                        // typed text address tho overwrite avvakunda
                        ignoreNextIdle = !same;
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Network error. Try again", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(edtLocation.getWindowToken(), 0);
    }

    // ---------------- DATE / TIME ----------------

    private void pickTime(boolean isStart) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, h, m) -> {
            int hour12 = (h % 12 == 0) ? 12 : h % 12;
            String text = String.format(Locale.US, "%02d:%02d %s", hour12, m, h < 12 ? "AM" : "PM");
            if (isStart) {
                startH = h;
                startM = m;
                edtStartTime.setText(text);
            } else {
                endH = h;
                endM = m;
                edtEndTime.setText(text);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false).show();
    }

    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
                && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR);
    }

    // ---------------- VALIDATION ----------------
    private boolean validate() {
        if (TextUtils.isEmpty(edtName.getText().toString().trim())) {
            edtName.setError("Please enter name");
            edtName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edtAddress.getText().toString().trim())) {
            edtAddress.setError("Please enter Display Address");
            edtAddress.requestFocus();
            return false;
        }
        if (startH < 0) {
            Toast.makeText(this, "Select start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endH < 0) {
            Toast.makeText(this, "Select end time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isSameDay(startCal, endCal) && endH * 60 + endM <= startH * 60 + startM) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!hasLocation) {
            Toast.makeText(this, "Move the map or search a location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}