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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.dst.ayyapatelugu.Services.LocationForegroundService;
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

    private boolean showNearbyOnly = false;
    private String selectedTempleId;
    private String selectedLat;
    private String selectedLng;

    // Custom info card (real View overlay, so its buttons are clickable)
    private View infoCard;

    // Gap tuning: increase to move the card closer to the marker, decrease (or negative) to move it away
    private static final int CARD_OVERLAP_DP = 0;

    // Marker images cropped to the visible pin (so transparent padding is not tappable); cached
    private android.graphics.Bitmap greenIconBitmap;
    private android.graphics.Bitmap redIconBitmap;
    private Marker selectedMarker;

    // ================= ON CREATE =================
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){

            showNearbyOnly =
                    getArguments().getBoolean(
                            "OPEN_NEARBY",
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

        // Wrap the map layout in a FrameLayout so we can float the info card on top of it
        FrameLayout root = new FrameLayout(requireContext());
        root.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        infoCard = inflater.inflate(R.layout.custom_info_window, root, false);
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
        infoCard.findViewById(R.id.info_close).setOnClickListener(v -> hideInfoCard());

        // Only the Start Navigation button starts navigation
        infoCard.findViewById(R.id.start_navigation_button).setOnClickListener(v -> {
            if (selectedMarker != null) {
                startNavigation(selectedMarker.getPosition());
            }
        });

        return root;
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
            startAnnadanamTracking();
        }

        float savedZoom = SharedPreferenceHelper.getZoomLevel(getContext());
        mMap.moveCamera(CameraUpdateFactory.zoomTo(savedZoom));

        mMap.setOnCameraIdleListener(() -> {
            currentZoomLevel = mMap.getCameraPosition().zoom;
            SharedPreferenceHelper.setZoomLevel(getContext(), currentZoomLevel);
        });
    }

    private void startAnnadanamTracking() {
        Intent i =
                new Intent(
                        requireContext(),
                        LocationForegroundService.class
                );

        i.setAction("ANNADANAM_ON");


        ContextCompat.startForegroundService(
                requireContext(),
                i
        );

    }

    private void getCurrentLocation() {

        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(requireContext());

        client.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {

                        userLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        userLocation, 14f
                                )
                        );
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Re-evaluate green/red when the user comes back (time may have changed)
        if (mMap != null && mapList != null && !mapList.isEmpty()) {
            addMarkers(mapList);
        }
    }

    private void fetchLocationData() {
        // 1) Show cached data immediately (fast), if we have any
        mapList = SharedPreferenceHelper.getTempleData(getContext());
        if (mapList != null && !mapList.isEmpty()) {
            new Handler().postDelayed(() -> addMarkers(mapList), 200);
        }

        // 2) ALWAYS refresh from the API, so API changes reach the app
        Call<MapDataResponse> call = apiClient.getMapList();
        call.enqueue(new Callback<MapDataResponse>() {
            @Override
            public void onResponse(Call<MapDataResponse> call, Response<MapDataResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().getErrorCode())) {

                    List<MapDataResponse.Result> data = response.body().getResult();
                    if (data == null) return;

                    SharedPreferenceHelper.saveTempleData(getContext(), data);   // update cache
                    mapList = data;
                    new Handler().postDelayed(() -> addMarkers(data), 200);

                    Intent i = new Intent(getActivity(), LocationForegroundService.class);
                    i.setAction("ANNADANAM_ON");
                    requireActivity().startService(i);
                }
            }

            @Override
            public void onFailure(Call<MapDataResponse> call, Throwable t) {
                // No internet: keep showing the cached markers
            }
        });
    }

    // ⭐ ADD MARKERS WITH GREEN/RED LOGIC ⭐
    private void addMarkers(List<MapDataResponse.Result> locations) {
        if (mMap == null) return;

        mMap.clear();
        hideInfoCard();

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

                android.graphics.Bitmap iconBmp = getCroppedMarkerBitmap(isActive);
                BitmapDescriptor icon = iconBmp != null
                        ? BitmapDescriptorFactory.fromBitmap(iconBmp)
                        : BitmapDescriptorFactory.fromResource(
                                isActive ? R.drawable.marker_green : R.drawable.marker_red);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(temple.getAnnadhanamNameTelugu())
                        .snippet(temple.getLocation())
                        .icon(icon)
                );

                marker.setTag(new Object[]{ temple, isActive });
                if(selectedTempleId != null &&
                        selectedTempleId.equals(
                                temple.getAnnadhanamId()
                        )) {

                    showInfoCard(marker);

                    mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                    pos,
                                    18f
                            )
                    );
                }

            } catch (Exception ignored) { }
        }
    }

    private void setupMarkerClicks() {
        mMap.setOnMarkerClickListener(marker -> {
            showInfoCard(marker);
            return true;
        });

        // Card moves along with the map
        mMap.setOnCameraMoveListener(this::updateInfoCardPosition);

        // Tapping anywhere else on the map closes the card
        mMap.setOnMapClickListener(latLng -> hideInfoCard());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isCurrentDateTimeBetween(String startDate, String endDate,
                                             String startTime, String endTime) {
        try {
            Calendar now = Calendar.getInstance();

            // 1) Date range: start day 00:00 .. end day 23:59:59
            Calendar startDay = Calendar.getInstance();
            startDay.setTimeInMillis(Long.parseLong(startDate) * 1000);
            startDay.set(Calendar.HOUR_OF_DAY, 0);
            startDay.set(Calendar.MINUTE, 0);
            startDay.set(Calendar.SECOND, 0);
            startDay.set(Calendar.MILLISECOND, 0);

            Calendar endDay = Calendar.getInstance();
            endDay.setTimeInMillis(Long.parseLong(endDate) * 1000);
            endDay.set(Calendar.HOUR_OF_DAY, 23);
            endDay.set(Calendar.MINUTE, 59);
            endDay.set(Calendar.SECOND, 59);
            endDay.set(Calendar.MILLISECOND, 999);

            boolean dateOk = !now.before(startDay) && !now.after(endDay);

            // Marker color depends on the DATE range only (time of day is not checked here)
            return dateOk;

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

    private void showInfoCard(Marker marker) {
        if (infoCard == null) return;
        selectedMarker = marker;
        bindInfoCard(infoCard, marker);

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

    // Scrolls the map so that (card + pin) ends up in the middle of the screen
    private void centerMarkerWithCard() {
        if (mMap == null || infoCard == null || selectedMarker == null) return;

        View parent = (View) infoCard.getParent();
        android.graphics.Point p = mMap.getProjection().toScreenLocation(selectedMarker.getPosition());

        int blockHeight = getMarkerVisibleHeightPx(selectedMarker) + infoCard.getHeight();

        float targetX = parent.getWidth() / 2f;
        float targetY = parent.getHeight() / 2f + blockHeight / 2f;   // marker tip position

        mMap.animateCamera(CameraUpdateFactory.scrollBy(p.x - targetX, p.y - targetY));
    }

    // Crops the transparent padding around the pin, so only the pin itself is tappable
    private android.graphics.Bitmap getCroppedMarkerBitmap(boolean active) {
        if (active && greenIconBitmap != null) return greenIconBitmap;
        if (!active && redIconBitmap != null) return redIconBitmap;

        int resId = active ? R.drawable.marker_green : R.drawable.marker_red;
        android.graphics.Bitmap src =
                android.graphics.BitmapFactory.decodeResource(getResources(), resId);
        if (src == null) return null;

        int w = src.getWidth();
        int h = src.getHeight();
        int[] px = new int[w * h];
        src.getPixels(px, 0, w, 0, 0, w, h);

        int minX = w, minY = h, maxX = -1, maxY = -1;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((px[y * w + x] >>> 24) > 10) {   // visible pixel
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        android.graphics.Bitmap result = src;
        if (maxX >= minX && maxY >= minY) {
            result = android.graphics.Bitmap.createBitmap(
                    src, minX, minY, maxX - minX + 1, maxY - minY + 1);
        }

        if (active) greenIconBitmap = result; else redIconBitmap = result;
        return result;
    }

    // Height of the (cropped) pin, used to place the card just above it
    private int getMarkerVisibleHeightPx(Marker marker) {
        boolean active = true;
        Object tag = marker.getTag();
        if (tag instanceof Object[]) {
            active = (boolean) ((Object[]) tag)[1];
        }
        android.graphics.Bitmap bmp = getCroppedMarkerBitmap(active);
        return bmp != null ? bmp.getHeight() : 0;
    }

    // Puts the card just above the selected marker (bottom-center of card = top of marker icon)
    private void updateInfoCardPosition() {
        if (mMap == null || infoCard == null || selectedMarker == null) return;

        android.graphics.Point p = mMap.getProjection().toScreenLocation(selectedMarker.getPosition());

        int iconHeight = getMarkerVisibleHeightPx(selectedMarker);

        // Centered over the marker, but kept fully inside the screen
        View parent = (View) infoCard.getParent();
        float edge = 8 * getResources().getDisplayMetrics().density;
        float x = p.x - infoCard.getWidth() / 2f;
        float maxX = parent.getWidth() - infoCard.getWidth() - edge;
        x = Math.max(edge, Math.min(x, maxX));
        infoCard.setTranslationX(x);
        int overlapPx = (int) (CARD_OVERLAP_DP * getResources().getDisplayMetrics().density);
        infoCard.setTranslationY(p.y - iconHeight - infoCard.getHeight() + overlapPx);
    }

    private void hideInfoCard() {
        selectedMarker = null;
        if (infoCard != null) {
            infoCard.setVisibility(View.GONE);
        }
    }

    private void bindInfoCard(View view, Marker marker) {

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

            sDate.setText("From : " + formatDate(d.getStartingDate()));
            eDate.setText("To : " + formatDate(d.getEndingDate()));
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        cal = Calendar.getInstance();
                    }
                    String nowStr = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
            SimpleDateFormat in = new SimpleDateFormat(t.length() == 5 ? "HH:mm" : "HH:mm:ss", Locale.getDefault());
            Date d = in.parse(t);
            return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d);
        } catch (Exception e) { return t; }
    }

    @Override
    public void onPause() {
        super.onPause();


        Intent i =
                new Intent(
                        requireContext(),
                        LocationForegroundService.class
                );

        i.setAction("ANNADANAM_OFF");


        ContextCompat.startForegroundService(
                requireContext(),
                i
        );

    }
}