package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.DataBase.SharedPreferenceManager;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DevlyaluActivity extends AppCompatActivity implements OnMapReadyCallback {
    Toolbar toolbar;
    private GoogleMap mMap;
    private APiInterface apiClient;

    private Context context;

    ImageButton zoomInButton, zoomOutButton;
    private float currentZoomLevel = 15.0f;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private LatLng userLocation;

    private static final float ZOOM_LEVEL_IN = 1.0f;
    private static final float ZOOM_LEVEL_OUT = -1.0f;

    private static final float ZOOM_THRESHOLD = 1.0f;

    private List<TempleMapDataResponse.Result> templeList;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devlyalu);

        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        setSupportActionBar(toolbar);
        ;
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

        zoomInButton = findViewById(R.id.zoom_in_button);
        zoomOutButton = findViewById(R.id.zoom_out_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions here
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            initMap();
        }



        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomInMap();
            }
        });
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomOutMap();
            }
        });

        context = this;

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
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
        //displayCurrentUserLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_devalayalu_fragment);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            displayCurrentUserLocation();
            setupMarkerClickListeners();
           fetchLocationDataAndAddMarkers();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        float initialZoomLevel = SharedPreferenceManager.getZoomLevel(this);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoomLevel));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float newZoomLevel = mMap.getCameraPosition().zoom;
                if (Math.abs(newZoomLevel - currentZoomLevel) > ZOOM_THRESHOLD) {
                    // Update markers or perform other actions based on zoom level change
                    currentZoomLevel = newZoomLevel;

                    SharedPreferenceManager.setZoomLevel(DevlyaluActivity.this, newZoomLevel);

                }
            }
        });
    }



    private void addMarkers(List<TempleMapDataResponse.Result> temples) {
        if(mMap != null) {
            DrawMarkersTask drawMarkersTask = new DrawMarkersTask(temples);
            drawMarkersTask.execute();
        }
    }

    private void setupMarkerClickListeners() {
        mMap.setInfoWindowAdapter(new DevlyaluActivity.CustomInfoWindowAdapter());
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                startNavigation(marker.getPosition());
            }
        });
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        View mContentsView;

        CustomInfoWindowAdapter() {
            mContentsView = getLayoutInflater().inflate(R.layout.custom_temple_window, null);
        }

        @Nullable
        @Override
        public View getInfoContents(@NonNull Marker marker) {
            return null;
        }

        @Nullable
        @Override
        public View getInfoWindow(@NonNull Marker marker) {
            TextView title = mContentsView.findViewById(R.id.window_title);
            title.setText(marker.getTitle());

            TextView txtLocation = mContentsView.findViewById(R.id.window_location);
            //String address = (String) marker.getTag();
            txtLocation.setText(marker.getSnippet());

            // Handle the "Start Navigation" button click
            LinearLayout startNavigationButton = mContentsView.findViewById(R.id.start_navigation_button);
            startNavigationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startNavigation(marker.getPosition());
                }
            });

            return mContentsView;
        }
    }

    private void startNavigation(LatLng position) {
        String destinationStr = position.latitude + "," + position.longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destinationStr);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Use the Google Maps app

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(this, "No navigation app installed. Please install a navigation app.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressStr = new StringBuilder();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressStr.append(address.getAddressLine(i)).append("\n");
                }
                return addressStr.toString().trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Address not found";
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
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        userLocation = new LatLng(latitude, longitude);
                        if (mMap != null) {
                            moveCameraToUserLocation();
                        }else {
                            initMap();
                        }
                    }
                });
    }

    private void fetchLocationDataAndAddMarkers() {
        templeList = SharedPreferenceManager.getTempleData(this);

        if (templeList != null && !templeList.isEmpty()) {
            addMarkers(templeList);
        } else {
            // Fetch data from API if SharedPreferences data is not available
            Call<TempleMapDataResponse> call = apiClient.getTempleMapList();
            call.enqueue(new Callback<TempleMapDataResponse>() {
                @Override
                public void onResponse(Call<TempleMapDataResponse> call, Response<TempleMapDataResponse> response) {
                    if (response.isSuccessful()) {
                        TempleMapDataResponse templeMapDataResponse = response.body();
                        if (templeMapDataResponse != null && templeMapDataResponse.getErrorCode().equals("200")) {
                            templeList = templeMapDataResponse.getResult();
                            addMarkers(templeList);

                            // Save data to SharedPreferences for future use
                            SharedPreferenceManager.saveTempleData(DevlyaluActivity.this, templeList);
                        } else {
                            // Handle error
                        }
                    }
                }

                @Override
                public void onFailure(Call<TempleMapDataResponse> call, Throwable t) {
                    // Handle failure
                }
            });
        }
    }
    private void moveCameraToUserLocation() {
        if (userLocation != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoomLevel));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, initialize the map
                initMap();
                // Fetch and update temple data
                //viewModel.fetchTempleData(apiClient);
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Location permission denied. Map functionality may be limited.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public class DrawMarkersTask extends AsyncTask<Void, MarkerOptions, Void> {
        List<TempleMapDataResponse.Result>  myTempleList ;
        public DrawMarkersTask(List<TempleMapDataResponse.Result> temples) {
           myTempleList = temples;
        }

        protected void onPreExecute() {
            // this method executes in UI thread
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }

        @Override
        protected Void doInBackground(Void... params) {
            // this method executes in separate background thread
            // you CANT modify UI here
            for (int i = 0; i < myTempleList.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions().position(
                        new LatLng(Double.parseDouble(myTempleList.get(i)
                                .getLatitude()), Double
                                .parseDouble(myTempleList.get(i)
                                        .getLongitude()))).title(
                        myTempleList.get(i).getTempleNameTelugu()).snippet(
                                myTempleList.get(i).getLocation());

                publishProgress(markerOptions); // pass it for the main UI thread for displaying
                try {
                    Thread.sleep(50); // sleep for 50 ms so that main UI thread can handle user actions in the meantime
                } catch (InterruptedException e) {
                    // NOP (no operation)
                }
            }
            return null;
        }

        protected void onProgressUpdate(MarkerOptions... markerOptions) {
            // this executes in main ui thread so you can add prepared marker to your map
            mMap.addMarker(markerOptions[0]);
        }

        protected void onPostExecute(Void result) {

          }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Get the clicked menu item ID

        if (id == R.id.popup_info) {
            informationDialog();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
    @SuppressLint("MissingInflatedId")
    private void informationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DevlyaluActivity.this);
        View dialogView = LayoutInflater.from(DevlyaluActivity.this).inflate(R.layout.dialog_devlyalu_information, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

       ImageButton closeButton = dialogView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}