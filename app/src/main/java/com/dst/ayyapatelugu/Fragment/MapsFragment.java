package com.dst.ayyapatelugu.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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

import com.dst.ayyapatelugu.Adapter.TemplesAdapter;
import com.dst.ayyapatelugu.Adapter.TemplesMapAdapter;
import com.dst.ayyapatelugu.DataBase.SharedManager;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.LocationForegroundService;
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

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private APiInterface apiClient;
    ImageButton zoomInButton, zoomOutButton;

    private float currentZoomLevel = 15.0f;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;

    private LatLng userLocation;

    private MapsFragment context;

    private static final float ZOOM_LEVEL_IN = 1.0f;
    private static final float ZOOM_LEVEL_OUT = -1.0f;

    private static final float ZOOM_THRESHOLD = 1.0f;

    private List<AyyappaTempleMapDataResponse.Result> templeList;

    Button butNearsttemples;
    TemplesAdapter adapter;

    private boolean showNearbyOnly = false;
    private String selectedTempleId;
    private String selectedLat;
    private String selectedLng;

    // Custom info card (real View overlay, so its buttons are clickable)
    private View infoCard;
    private Marker selectedMarker;

    // Height of the default Google marker pin (dp); tune if the card is too high/low
    private static final int MARKER_HEIGHT_DP = 40;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){

            showNearbyOnly =
                    getArguments().getBoolean(
                            "OPEN_AYYAPPATEMPLE",
                            false
                    );

            selectedTempleId =
                    getArguments().getString(
                            "TEMPLE_ID"
                    );

            selectedLat =
                    getArguments().getString(
                            "TEMPLE_LAT"
                    );

            selectedLng =
                    getArguments().getString(
                            "TEMPLE_LNG"
                    );
        }
    }


    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_fragment, container, false);
        zoomInButton = view.findViewById(R.id.zoom_in_button);
        zoomOutButton = view.findViewById(R.id.zoom_out_button);

        butNearsttemples = view.findViewById(R.id.btn_nearest_temple);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions here
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }else{
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
                Log.d("TempleDebug", "Button Clicked: Fetching Nearby Temples...");
                Log.d("TempleDebug", "User Location: " + userLocation);
                Log.d("TempleDebug", "Temple List Size: " + (templeList != null ? templeList.size() : "null"));
                displayNearByTemples();

            }
        });
        //getContext() = this;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiClient = retrofit.create(APiInterface.class);

        // Wrap the map layout in a FrameLayout so we can float the info card on top of it
        FrameLayout root = new FrameLayout(requireContext());
        root.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        infoCard = inflater.inflate(R.layout.custom_ayyappa_window, root, false);
        FrameLayout.LayoutParams cardParams = (FrameLayout.LayoutParams) infoCard.getLayoutParams();
        cardParams.gravity = Gravity.TOP | Gravity.START;
        cardParams.setMargins(0, 0, 0, 0);   // position is set from the marker's screen point

        // Fixed, compact width (max 300dp, never wider than the screen minus 16dp each side)
        float density = getResources().getDisplayMetrics().density;
        int screenW = getResources().getDisplayMetrics().widthPixels;
        cardParams.width = Math.min((int) (300 * density), screenW - (int) (32 * density));

        infoCard.setLayoutParams(cardParams);
        infoCard.setVisibility(View.GONE);
        root.addView(infoCard);

        // Only the close button closes the card
        infoCard.findViewById(R.id.ayyappa_window_close).setOnClickListener(v -> hideInfoCard());

        // Only the Start Navigation button starts navigation
        infoCard.findViewById(R.id.start_navigation).setOnClickListener(v -> {
            if (selectedMarker != null) {
                startNavigation(selectedMarker.getPosition());
            }
        });

        return root;
    }

    private void displayNearByTemples() {
        if (userLocation != null && templeList != null) {
            // List to store temples that are within 10 km
            List<AyyappaTempleMapDataResponse.Result> nearbyTemples = new ArrayList<>();

            for (AyyappaTempleMapDataResponse.Result temple : templeList) {
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

            // ✅ Update RecyclerView with new data
            /*adapter.updateData(nearbyTemples);
            adapter.notifyDataSetChanged();*/

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

    private void showNearbyTemplesDialog(List<AyyappaTempleMapDataResponse.Result> nearbyTemples) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nearest_temples, null);
        builder.setView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.rv_nearest_temples);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter  = new TemplesAdapter(nearbyTemples);
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
            List<AyyappaTempleMapDataResponse.Result> filteredTemples = filterTemplesByRadius(selectedRadius);
            adapter.updateData(filteredTemples);
        });

        AlertDialog dialog = builder.create();

        ImageButton closeButton = dialogView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> {
            // Dismiss the dialog when the close button is clicked
            dialog.dismiss();
        });


        dialog.show();
    }

    private List<AyyappaTempleMapDataResponse.Result> filterTemplesByRadius(int selectedRadius) {
        List<AyyappaTempleMapDataResponse.Result> filteredTemples = new ArrayList<>();
        if (userLocation != null && templeList != null) {
            for (AyyappaTempleMapDataResponse.Result temple : templeList) {
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
                    Log.e("Invalid Temple Data", "Invalid latitude/longitude for temple: " + temple.getTempleNameTelugu());
                }
            }
        }
        return filteredTemples;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is granted
            mMap.setMyLocationEnabled(true);
            displayCurrentUserLocation();
            setupMarkerClickListeners();
            fetchLocationDataAndAddMarkers();
            startAyyappaTempleTracking();
        } else {
            // Request location permissions here
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        float initialZoomLevel = SharedManager.getZoomLevel(getContext());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(initialZoomLevel));

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                float newZoomLevel = mMap.getCameraPosition().zoom;
                if (Math.abs(newZoomLevel - currentZoomLevel) > ZOOM_THRESHOLD) {
                    // Update markers or perform other actions based on zoom level change
                    currentZoomLevel = newZoomLevel;

                    SharedManager.saveZoomLevel(getContext(), newZoomLevel);

                }
            }
        });
    }

    private void startAyyappaTempleTracking() {
        Intent i =
                new Intent(
                        requireContext(),
                        LocationForegroundService.class
                );

        i.setAction("AYYAPPA_ON");


        ContextCompat.startForegroundService(
                requireContext(),
                i
        );
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_ayyappadevlyalu_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Handle the case when mapFragment is null
        }
    }







    private void displayCurrentUserLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                        } else {
                            // Handle the case when mMap is null
                            initMap();
                        }

                    }
                });
    }

    private void moveCameraToUserLocation() {
        if (userLocation != null && mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoomLevel));
        }
    }

    private void setupMarkerClickListeners() {
        mMap.setOnMarkerClickListener(marker -> {
            showInfoCard(marker);
            return true;
        });

        // Card moves along with the map
        mMap.setOnCameraMoveListener(this::updateInfoCardPosition);

        // Tapping anywhere else on the map closes the card
        mMap.setOnMapClickListener(latLng -> hideInfoCard());
    }

    private void showInfoCard(Marker marker) {
        if (infoCard == null) return;
        selectedMarker = marker;

        TextView title = infoCard.findViewById(R.id.ayyappa_window_title);
        title.setText(marker.getTitle());
        TextView txtLocation = infoCard.findViewById(R.id.ayyappa_location);
        txtLocation.setText(marker.getSnippet());

        // Wait for the card to be measured, then place it above the marker
        infoCard.setVisibility(View.INVISIBLE);
        infoCard.post(() -> {
            updateInfoCardPosition();
            if (selectedMarker != null) {
                infoCard.setVisibility(View.VISIBLE);
                centerMarkerWithCard();
            }
        });
    }

    private void hideInfoCard() {
        selectedMarker = null;
        if (infoCard != null) {
            infoCard.setVisibility(View.GONE);
        }
    }

    // Puts the card just above the selected marker
    private void updateInfoCardPosition() {
        if (mMap == null || infoCard == null || selectedMarker == null) return;

        float density = getResources().getDisplayMetrics().density;
        android.graphics.Point p = mMap.getProjection().toScreenLocation(selectedMarker.getPosition());
        int markerHeight = (int) (MARKER_HEIGHT_DP * density);

        // Centered over the marker, but kept fully inside the screen
        View parent = (View) infoCard.getParent();
        float edge = 8 * density;
        float x = p.x - infoCard.getWidth() / 2f;
        float maxX = parent.getWidth() - infoCard.getWidth() - edge;
        x = Math.max(edge, Math.min(x, maxX));

        infoCard.setTranslationX(x);
        infoCard.setTranslationY(p.y - markerHeight - infoCard.getHeight());
    }

    // Scrolls the map so that (card + pin) ends up in the middle of the screen
    private void centerMarkerWithCard() {
        if (mMap == null || infoCard == null || selectedMarker == null) return;

        View parent = (View) infoCard.getParent();
        android.graphics.Point p = mMap.getProjection().toScreenLocation(selectedMarker.getPosition());

        int markerHeight = (int) (MARKER_HEIGHT_DP * getResources().getDisplayMetrics().density);
        int blockHeight = markerHeight + infoCard.getHeight();

        float targetX = parent.getWidth() / 2f;
        float targetY = parent.getHeight() / 2f + blockHeight / 2f;   // marker tip position

        mMap.animateCamera(CameraUpdateFactory.scrollBy(p.x - targetX, p.y - targetY));
    }

    private void fetchLocationDataAndAddMarkers() {
        templeList = SharedManager.getTempleData(getContext());

        if (templeList != null && !templeList.isEmpty()) {
            Log.d("TempleDebug", "Temple List Loaded from SharedManager: " + templeList.size());
            new Handler().postDelayed(() -> {
                addMarkers(templeList);
            }, 200); // Delay marker loading

            displayNearByTemples(); // ✅ Directly call when data exists
        } else {
            Log.d("TempleDebug", "Temple List Empty. Fetching from API...");

            Call<AyyappaTempleMapDataResponse> call = apiClient.getAyyaooaTempleMapList();
            call.enqueue(new Callback<AyyappaTempleMapDataResponse>() {
                @Override
                public void onResponse(Call<AyyappaTempleMapDataResponse> call, Response<AyyappaTempleMapDataResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AyyappaTempleMapDataResponse ayyappaTempleMapDataResponse = response.body();
                        if ("200".equals(ayyappaTempleMapDataResponse.getErrorCode())) {
                            templeList = ayyappaTempleMapDataResponse.getResult();

                            // Save data to SharedManager

                            // ✅ Add markers & update UI
                            new Handler().postDelayed(() -> {
                                addMarkers(templeList);
                            }, 200); // Delay marker loading

                            SharedManager.saveTempleData(getContext(), templeList);

                            Log.d("TempleDebug", "Temple List Fetched from API: " + templeList.size());



                            Intent i = new Intent(getActivity(),
                                    LocationForegroundService.class);
                            i.setAction("AYYAPPA_ON");
                            requireActivity().startService(i);


                            // displayNearByTemples(); // ✅ Force UI refresh
                        } else {
                            Log.e("API Response", "Invalid response: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<AyyappaTempleMapDataResponse> call, Throwable t) {
                    Log.e("API Response", "Error fetching data: " + t.getMessage());
                }
            });
        }


    }
    private void addMarkers(List<AyyappaTempleMapDataResponse.Result> ayyappatemples) {
        for (AyyappaTempleMapDataResponse.Result temple : ayyappatemples) {
            try {

                LatLng pos = new LatLng(
                        Double.parseDouble(temple.getLatitude()),
                        Double.parseDouble(temple.getLongitude())
                );

                Marker marker = mMap.addMarker(
                        new MarkerOptions()
                                .position(pos)
                                .title(temple.getTempleNameTelugu())
                                .snippet(temple.getLocation())
                );

                // ✅ AUTO FOCUS LOGIC (notification click)
                if (selectedTempleId != null &&
                        selectedTempleId.equals(temple.getTempleId())) {

                    // Zoom in first, then show the card (it centers itself after the zoom)
                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(pos, 18f),
                            new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    showInfoCard(marker);
                                }

                                @Override
                                public void onCancel() {
                                    showInfoCard(marker);
                                }
                            }
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        List<AyyappaTempleMapDataResponse.Result> myAyyappaTemples;

        public DrawMarkersTask(List<AyyappaTempleMapDataResponse.Result> ayyappatemples) {

            myAyyappaTemples=ayyappatemples;
        }

        @Override
        protected void onPreExecute() {
            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
        }

        @Override
        protected Void doInBackground(Void... voids) {

            for (int i = 0; i < myAyyappaTemples.size(); i++) {
                // Check if the activity is still available

                MarkerOptions markerOptions = new MarkerOptions().position(
                        new LatLng(Double.parseDouble(myAyyappaTemples.get(i)
                                .getLatitude()), Double
                                .parseDouble(myAyyappaTemples.get(i)
                                        .getLongitude()))).title(
                        myAyyappaTemples.get(i).getTempleNameTelugu()).snippet(
                        myAyyappaTemples.get(i).getLocation());

                publishProgress(markerOptions); // pass it for the main UI thread for displaying

               /* try {
                   // Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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



    @Override
    public void onPause() {
        super.onPause();
        Intent i =
                new Intent(
                        requireContext(),
                        LocationForegroundService.class
                );

        i.setAction("AYYAPPA_OFF");


        ContextCompat.startForegroundService(
                requireContext(),
                i
        );
    }
}
