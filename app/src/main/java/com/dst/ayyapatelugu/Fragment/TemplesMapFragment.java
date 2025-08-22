package com.dst.ayyapatelugu.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Adapter.TemplesMapAdapter;
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

public class TemplesMapFragment extends Fragment implements OnMapReadyCallback {

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

    Button butNearsttemples;
    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.temple_map_fragment,container,false);
        zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomOutButton = view.findViewById(R.id.zoom_out_button);

        butNearsttemples = view.findViewById(R.id.btn_nearest_temple);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions here
            ActivityCompat.requestPermissions(getActivity(),
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

        butNearsttemples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TempleDebug", "Button clicked!");
                displayNearByTemples();

            }
        });

        //context = this;

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

        return view;
    }

    private void displayNearByTemples() {
        if (userLocation != null && templeList != null) {
            // List to store temples that are within 10 km
            List<TempleMapDataResponse.Result> nearbyTemples = new ArrayList<>();

            for (TempleMapDataResponse.Result temple : templeList) {
                // Exclude unnamed temples or temples named "ayyappa" (case insensitive)
                if (temple.getTempleNameTelugu() == null || temple.getTempleNameTelugu().trim().isEmpty() ||
                        temple.getLocation().equalsIgnoreCase("ayyappa") || temple.getLocation().equalsIgnoreCase("kothur")
                        || temple.getLocation().equalsIgnoreCase("barunagar") || temple.getLocation().equalsIgnoreCase("savithribhai") || temple.getLocation().equalsIgnoreCase("Polla")) {
                    continue;
                }

                try {
                    // Get the temple's latitude and longitude
                    double templeLatitude = Double.parseDouble(temple.getLatitude());
                    double templeLongitude = Double.parseDouble(temple.getLongitude());

                    // Calculate the distance between the user's location and the temple
                    float[] results = new float[1];
                    Location.distanceBetween(
                            userLocation.latitude, userLocation.longitude,
                            templeLatitude, templeLongitude,
                            results
                    );
                    float distanceInMeters = results[0]; // Distance in meters

                    // Check if the temple is within 10 km
                    if (distanceInMeters <= 10000) {
                        nearbyTemples.add(temple); // Add temple to the list
                        Log.d("TempleDebug", "Nearby Temple Added: " + temple.getTempleNameTelugu() + " | Distance: " + distanceInMeters);
                    }
                } catch (NumberFormatException e) {
                    Log.e("Invalid Temple Data", "Invalid latitude/longitude for temple: " + temple.getTempleNameTelugu());
                    continue; // Skip invalid entries
                }
            }
            Log.d("TempleDebug", "Nearby Temples Count: " + nearbyTemples.size());
            // Check if there are any nearby temples
            if (!nearbyTemples.isEmpty()) {
                showNearbyTemplesDialog(nearbyTemples);
            } else {
                Log.d("TempleDebug", "No temples found within 10 km.");
                Toast.makeText(getContext(), "No temples found within 10 km.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Location or temple list not available.", Toast.LENGTH_SHORT).show();
        }
    }
    @SuppressLint("MissingInflatedId")
    private void showNearbyTemplesDialog(List<TempleMapDataResponse.Result> nearbyTemples) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_temple_nearest_temples, null);
        builder.setView(dialogView);

         RecyclerView recyclerView = dialogView.findViewById(R.id.rv_nearest_temples);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TemplesMapAdapter adapter = new TemplesMapAdapter(nearbyTemples);
        adapter.setOnNavigateListener(new TemplesMapAdapter.OnNavigateListener() {
            @Override
            public void onNavigate(String latitude, String longitude) {
                openGoogleMapsNavigation(latitude, longitude);
            }
        });
        recyclerView.setAdapter(adapter);

        RadioGroup radioGroup = dialogView.findViewById(R.id.rg_radius);
        RadioButton rb5km = dialogView.findViewById(R.id.rb_5km);
        RadioButton rb10km = dialogView.findViewById(R.id.rb_10km);
        RadioButton rb20km = dialogView.findViewById(R.id.rb_20km);

        // Default selection to 10 km
        rb10km.setChecked(true);

        // Filter temples based on selected radius
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedRadius = 10000; // Default radius is 10 km
            if (checkedId == R.id.rb_5km) {
                selectedRadius = 5000; // 5 km
            } else if (checkedId == R.id.rb_20km) {
                selectedRadius = 20000; // 20 km
            }
            // Update the temple list and adapter
            List<TempleMapDataResponse.Result> filteredTemples = filterTemplesByRadius(selectedRadius);
            adapter.updateData(filteredTemples);
        });

        AlertDialog dialog = builder.create();

        ImageButton closeButton = dialogView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();


    }

    private List<TempleMapDataResponse.Result> filterTemplesByRadius(int selectedRadius) {
        List<TempleMapDataResponse.Result> filteredTemples = new ArrayList<>();
        if (userLocation != null && templeList != null) {
            for (TempleMapDataResponse.Result temple : templeList) {
                try {
                    double templeLatitude = Double.parseDouble(temple.getLatitude());
                    double templeLongitude = Double.parseDouble(temple.getLongitude());

                    float[] results = new float[1];
                    Location.distanceBetween(
                            userLocation.latitude, userLocation.longitude,
                            templeLatitude, templeLongitude,
                            results
                    );
                    if (results[0] <= selectedRadius) {
                        filteredTemples.add(temple);
                    }
                } catch (NumberFormatException e) {
                    Log.d("TempleDebug", "Invalid latitude/longitude for temple: " + temple.getTempleNameTelugu());
                }
            }
        }
        return filteredTemples;
    }

    private void initMap() {
        //displayCurrentUserLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_devalayalu_fragment);
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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            displayCurrentUserLocation();
            setupMarkerClickListeners();
            fetchLocationDataAndAddMarkers();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        float initialZoomLevel = SharedPreferenceManager.getZoomLevel(getContext());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoomLevel));
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float newZoomLevel = mMap.getCameraPosition().zoom;
                if (Math.abs(newZoomLevel - currentZoomLevel) > ZOOM_THRESHOLD) {
                    // Update markers or perform other actions based on zoom level change
                    currentZoomLevel = newZoomLevel;

                    SharedPreferenceManager.setZoomLevel(getContext(), newZoomLevel);

                }
            }
        });
    }



    private void addMarkers(List<TempleMapDataResponse.Result> temples) {
        for (TempleMapDataResponse.Result temple : temples) {
            try {
                LatLng position = new LatLng(Double.parseDouble(temple.getLatitude()), Double.parseDouble(temple.getLongitude()));
                mMap.addMarker(new MarkerOptions().position(position)
                        .title(temple.getTempleNameTelugu())
                        .snippet(temple.getLocation()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupMarkerClickListeners() {
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
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

        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(getContext(), "No navigation app installed. Please install a navigation app.", Toast.LENGTH_SHORT).show();
        }
    }



    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
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
        templeList = SharedPreferenceManager.getTempleData(getContext());

        if (templeList != null && !templeList.isEmpty()) {
            new Handler().postDelayed(() -> {
                addMarkers(templeList);
            }, 200);
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
                            new Handler().postDelayed(() -> {
                                addMarkers(templeList);
                            }, 200);

                            // Save data to SharedPreferences for future use
                            SharedPreferenceManager.saveTempleData(getContext(), templeList);
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
                Toast.makeText(getContext(), "Location permission denied. Map functionality may be limited.", Toast.LENGTH_SHORT).show();
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
               /* try {
                    Thread.sleep(50); // sleep for 50 ms so that main UI thread can handle user actions in the meantime
                } catch (InterruptedException e) {
                    // NOP (no operation)
                }*/
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

    private void openGoogleMapsNavigation(String latitude, String longitude) {
        String destinationStr = latitude + "," + longitude;
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destinationStr);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "No navigation app installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
