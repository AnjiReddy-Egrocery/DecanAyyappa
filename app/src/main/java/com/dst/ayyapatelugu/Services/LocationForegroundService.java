package com.dst.ayyapatelugu.Services;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dst.ayyapatelugu.Activity.AnadanamActivity;
import com.dst.ayyapatelugu.Activity.ViewAllAyyappaTemplesActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.DataBase.SharedManager;
import com.dst.ayyapatelugu.DataBase.SharedPreferenceHelper;
import com.dst.ayyapatelugu.DataBase.SharedPreferenceManager;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.MapDataResponse;
import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.dst.ayyapatelugu.R;
import com.google.android.gms.location.*;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class LocationForegroundService extends Service {

    private static final String CHANNEL_ANNADANAM = "ANNADANAM_CHANNEL";
    private static final String CHANNEL_TEMPLE = "TEMPLE_CHANNEL";
    private static final String AYYAPPA_CHANNEL_TEMPLE = "AYYAPPA_TEMPLE_CHANNEL";

    private static final long CHECK_INTERVAL =
            5 * 60 * 1000L; // 15 mins

    private static final long TEMPLE_INTERVAL =
            10 * 60 * 1000L; // 10 mins

    private static final long AYYAPPA_TEMPLE_INTERVAL =
            15 * 60 * 1000L; // 10 mins

    private static final float MAX_DISTANCE =
            5000f; // 5 KM
    private static final float TEMPLE_DISTANCE =
            4000f; // 4 KM

    private static final float AYYAPPA_TEMPLE_DISTANCE =
            4000f; // 4 KM

    private FusedLocationProviderClient client;

    private Location currentLocation;

    private LocationCallback locationCallback;

    private Handler handler;

    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();

        startForeground(
                1,
                buildForegroundNotification()
        );

        client =
                LocationServices
                        .getFusedLocationProviderClient(this);

        startLocationUpdates();

        client.getLastLocation()
                .addOnSuccessListener(location -> {

                    if(location != null){

                        currentLocation = location;

                        checkNearbyTemples(
                                currentLocation
                        );
                    }
                });


        start15MinChecker();
        startTempleChecker();
        startAyyappaTempleChecker();
    }

    private void startAyyappaTempleChecker() {
        Handler templeHandler = new Handler();

        Runnable templeRunnable = new Runnable() {

            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(
                        LocationForegroundService.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    templeHandler.postDelayed(
                            this,
                            AYYAPPA_TEMPLE_INTERVAL
                    );
                    return;
                }

                client.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                ).addOnSuccessListener(location -> {

                    if(location != null){

                        checkNearbyAyyappaTempleNotifications(
                                location
                        );
                    }
                });

                templeHandler.postDelayed(
                        this,
                        AYYAPPA_TEMPLE_INTERVAL
                );
            }
        };

        templeRunnable.run();
    }

    private void checkNearbyAyyappaTempleNotifications(Location location) {
        List<AyyappaTempleMapDataResponse.Result> temples =
                SharedManager
                        .getTempleData(this);

        if (temples == null || temples.isEmpty())
            return;

        for (AyyappaTempleMapDataResponse.Result temple : temples) {

            try {

                double lat =
                        Double.parseDouble(
                                temple.getLatitude()
                        );

                double lng =
                        Double.parseDouble(
                                temple.getLongitude()
                        );

                float[] result =
                        new float[1];

                Location.distanceBetween(
                        location.getLatitude(),
                        location.getLongitude(),
                        lat,
                        lng,
                        result
                );

                float distanceMeters =
                        result[0];

                if (distanceMeters <= AYYAPPA_TEMPLE_DISTANCE) {

                    sendAyyappaTempleNotification(
                            temple,
                            distanceMeters
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void sendAyyappaTempleNotification(AyyappaTempleMapDataResponse.Result temple, float distanceMeters) {
        double km = distanceMeters / 1000.0;

        String distanceText =
                String.format(
                        Locale.getDefault(),
                        "%.1f KM away",
                        km
                );

        Intent intent =
                new Intent(
                        this,
                        ViewAllAyyappaTemplesActivity.class
                );

        intent.putExtra(
                "OPEN_AYYAPPATEMPLE",
                true
        );

        intent.putExtra(
                "TEMPLE_ID",
                temple.getTempleId()
        );

        intent.putExtra(
                "TEMPLE_LAT",
                temple.getLatitude()
        );

        intent.putExtra(
                "TEMPLE_LNG",
                temple.getLongitude()
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        AYYAPPA_CHANNEL_TEMPLE
                )
                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(
                                temple.getTempleNameTelugu()
                        )
                        .setContentText(
                                distanceText
                        )
                        .setStyle(
                                new NotificationCompat
                                        .BigTextStyle()
                                        .bigText(
                                                temple.getTempleNameTelugu()
                                                        + "\n"
                                                        + temple.getLocation()
                                                        + "\n"
                                                        + distanceText
                                        )
                        )
                        .setAutoCancel(true)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setContentIntent(
                                pendingIntent
                        );

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        NotificationManagerCompat
                .from(this)
                .notify(
                        (int) System.currentTimeMillis(),
                        builder.build()
                );
    }

    private void startLocationUpdates() {

        LocationRequest request =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        10000
                )
                        .setMinUpdateIntervalMillis(5000)
                        .build();

        locationCallback =
                new LocationCallback() {

                    @Override
                    public void onLocationResult(
                            LocationResult result
                    ) {

                        if (result == null)
                            return;

                        currentLocation =
                                result.getLastLocation();
                    }
                };

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        client.requestLocationUpdates(
                request,
                locationCallback,
                getMainLooper()
        );
    }

    private void start15MinChecker() {
        handler = new Handler();

        runnable = new Runnable() {

            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(
                        LocationForegroundService.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    handler.postDelayed(
                            this,
                            CHECK_INTERVAL
                    );
                    return;
                }

                client.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                ).addOnSuccessListener(location -> {

                    if(location != null){

                        currentLocation = location;

                        checkNearbyTemples(
                                currentLocation
                        );
                    }
                });

                handler.postDelayed(
                        this,
                        CHECK_INTERVAL
                );
            }
        };

        runnable.run();
    }

    private void startTempleChecker() {

        Handler templeHandler = new Handler();

        Runnable templeRunnable = new Runnable() {

            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(
                        LocationForegroundService.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    templeHandler.postDelayed(
                            this,
                            TEMPLE_INTERVAL
                    );
                    return;
                }

                client.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                ).addOnSuccessListener(location -> {

                    if(location != null){

                        checkNearbyTempleNotifications(
                                location
                        );
                    }
                });

                templeHandler.postDelayed(
                        this,
                        TEMPLE_INTERVAL
                );
            }
        };

        templeRunnable.run();
    }

    private void checkNearbyTempleNotifications(
            Location userLoc
    ) {

        List<TempleMapDataResponse.Result> temples =
                SharedPreferenceManager
                        .getTempleData(this);

        if (temples == null || temples.isEmpty())
            return;

        for (TempleMapDataResponse.Result temple : temples) {

            try {

                double lat =
                        Double.parseDouble(
                                temple.getLatitude()
                        );

                double lng =
                        Double.parseDouble(
                                temple.getLongitude()
                        );

                float[] result =
                        new float[1];

                Location.distanceBetween(
                        userLoc.getLatitude(),
                        userLoc.getLongitude(),
                        lat,
                        lng,
                        result
                );

                float distanceMeters =
                        result[0];

                if (distanceMeters <= TEMPLE_DISTANCE) {

                    sendTempleNotification(
                            temple,
                            distanceMeters
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void sendTempleNotification(
            TempleMapDataResponse.Result temple,
            float distanceMeters
    ) {

        double km = distanceMeters / 1000.0;

        String distanceText =
                String.format(
                        Locale.getDefault(),
                        "%.1f KM away",
                        km
                );

        Intent intent =
                new Intent(
                        this,
                        ViewAllTemplesActivity.class
                );

        intent.putExtra(
                "OPEN_TEMPLE",
                true
        );

        intent.putExtra(
                "TEMPLE_ID",
                temple.getTempleId()
        );

        intent.putExtra(
                "TEMPLE_LAT",
                temple.getLatitude()
        );

        intent.putExtra(
                "TEMPLE_LNG",
                temple.getLongitude()
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_TEMPLE
                )
                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(
                                temple.getTempleNameTelugu()
                        )
                        .setContentText(
                                distanceText
                        )
                        .setStyle(
                                new NotificationCompat
                                        .BigTextStyle()
                                        .bigText(
                                                temple.getTempleNameTelugu()
                                                        + "\n"
                                                        + temple.getLocation()
                                                        + "\n"
                                                        + distanceText
                                        )
                        )
                        .setAutoCancel(true)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setContentIntent(
                                pendingIntent
                        );

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        NotificationManagerCompat
                .from(this)
                .notify(
                        (int) System.currentTimeMillis(),
                        builder.build()
                );
    }

    private void checkNearbyTemples(
            Location userLoc
    ) {

        List<MapDataResponse.Result> temples =
                SharedPreferenceHelper
                        .getTempleData(this);

        if (temples == null
                || temples.isEmpty())
            return;

        for (MapDataResponse.Result temple : temples) {

            try {

                double lat =
                        Double.parseDouble(
                                temple.getLatitude()
                        );

                double lng =
                        Double.parseDouble(
                                temple.getLongitude()
                        );

                float[] result =
                        new float[1];

                Location.distanceBetween(
                        userLoc.getLatitude(),
                        userLoc.getLongitude(),
                        lat,
                        lng,
                        result
                );

                float distanceMeters =
                        result[0];

                if (distanceMeters <= MAX_DISTANCE) {

                    sendNotification(
                            temple,
                            distanceMeters
                    );
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void sendNotification(
            MapDataResponse.Result temple,
            float distanceMeters
    ) {

        double km =
                distanceMeters / 1000.0;

        String distanceText =
                String.format(
                        Locale.getDefault(),
                        "%.1f KM away",
                        km
                );

        Intent intent =
                new Intent(
                        this,
                        AnadanamActivity.class
                );

        intent.putExtra(
                "OPEN_NEARBY",
                true
        );

        intent.putExtra(
                "TEMPLE_ID",
                temple.getAnnadhanamId()
        );

        intent.putExtra(
                "TEMPLE_LAT",
                temple.getLatitude()
        );

        intent.putExtra(
                "TEMPLE_LNG",
                temple.getLongitude()
        );

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        (int) System.currentTimeMillis(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                                | PendingIntent.FLAG_IMMUTABLE
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ANNADANAM
                )
                        .setSmallIcon(
                                R.drawable.ic_launcher_foreground
                        )
                        .setContentTitle(
                                temple.getAnnadhanamNameTelugu()
                        )
                        .setContentText(
                                distanceText
                        )
                        .setStyle(
                                new NotificationCompat
                                        .BigTextStyle()
                                        .bigText(
                                                temple.getAnnadhanamNameTelugu()
                                                        + "\n"
                                                        + distanceText
                                        )
                        )
                        .setAutoCancel(true)
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setContentIntent(
                                pendingIntent
                        );

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        NotificationManagerCompat
                .from(this)
                .notify(
                        (int) System.currentTimeMillis(),
                        builder.build()
                );
    }

    private Notification buildForegroundNotification() {

        return new NotificationCompat.Builder(
                this,
                CHANNEL_ANNADANAM
        )
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Annadanam Tracking Active")
                .setContentText("Location monitoring is running in background")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel annadanamChannel =
                    new NotificationChannel(
                            CHANNEL_ANNADANAM,
                            "Annadanam Tracking",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationChannel templeChannel =
                    new NotificationChannel(
                            CHANNEL_TEMPLE,
                            "Temple Tracking",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationChannel ayyappatempleChannel =
                    new NotificationChannel(
                            AYYAPPA_CHANNEL_TEMPLE,
                            "Temple Tracking",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            manager.createNotificationChannel(annadanamChannel);
            manager.createNotificationChannel(templeChannel);
            manager.createNotificationChannel(ayyappatempleChannel);

        }
    }



    @Override
    public int onStartCommand(
            Intent intent,
            int flags,
            int startId
    ) {

        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (handler != null
                && runnable != null) {

            handler.removeCallbacks(
                    runnable
            );
        }

        if (client != null
                && locationCallback != null) {

            client.removeLocationUpdates(
                    locationCallback
            );
        }
    }

    @Nullable
    @Override
    public IBinder onBind(
            Intent intent
    ) {
        return null;
    }
}