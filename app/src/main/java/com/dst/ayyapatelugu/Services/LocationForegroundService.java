package com.dst.ayyapatelugu.Services;

import android.Manifest;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.*;
import androidx.annotation.Nullable;
import androidx.core.app.*;

import com.dst.ayyapatelugu.Activity.*;
import com.dst.ayyapatelugu.DataBase.*;
import com.dst.ayyapatelugu.Model.*;
import com.dst.ayyapatelugu.R;
import com.google.android.gms.location.*;
import android.app.Service;

import java.util.*;

public class LocationForegroundService extends Service {

    // Channels
    private static final String CHANNEL_ANNADANAM = "ANNADANAM_CHANNEL";
    private static final String CHANNEL_TEMPLE = "TEMPLE_CHANNEL";
    private static final String CHANNEL_AYYAPPA = "AYYAPPA_CHANNEL";

    // Distance limits
    private static final float ANNADANAM_DISTANCE = 5000f;
    private static final float TEMPLE_DISTANCE = 4000f;
    private static final float AYYAPPA_DISTANCE = 4000f;

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;

    private Handler handler;
    private Runnable runnable;

    // 🔥 MAP ACTIVE FLAGS
    private boolean isAnnadanamMapActive = false;
    private boolean isTempleMapActive = false;
    private boolean isAyyappaMapActive = false;

    // 🔥 Prevent duplicate in same session
    private Set<String> notified = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();

        createChannel();
        startForeground(1, buildForegroundNotification());

        client = LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();
        startChecker();
    }

    // ================= MAP CONTROL =================
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {

            switch (intent.getAction()) {

                case "ANNADANAM_ON":
                    isAnnadanamMapActive = true;
                    isTempleMapActive = false;
                    isAyyappaMapActive = false;
                    break;

                case "TEMPLE_ON":
                    isAnnadanamMapActive = false;
                    isTempleMapActive = true;
                    isAyyappaMapActive = false;
                    break;

                case "AYYAPPA_ON":
                    isAnnadanamMapActive = false;
                    isTempleMapActive = false;
                    isAyyappaMapActive = true;
                    break;
            }
        }

        return START_STICKY;
    }

    // ================= LOCATION =================
    private void startLocationUpdates() {

        LocationRequest request =
                new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                        .setMinUpdateIntervalMillis(5000)
                        .build();

        locationCallback = new LocationCallback() {};

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        client.requestLocationUpdates(request, locationCallback, getMainLooper());
    }

    // ================= MAIN LOOP =================
    private void startChecker() {

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                if (ActivityCompat.checkSelfPermission(
                        LocationForegroundService.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    handler.postDelayed(this, 5 * 60 * 1000);
                    return;
                }

                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener(location -> {

                            if (location != null) {

                                checkAnnadanam(location);
                                checkTemples(location);
                                checkAyyappa(location);
                            }
                        });

                handler.postDelayed(this, 5 * 60 * 1000);
            }
        };

        runnable.run();
    }

    // ================= ANNADANAM =================
    private void checkAnnadanam(Location loc) {

        if (!isAnnadanamMapActive) return;

        List<MapDataResponse.Result> list =
                SharedPreferenceHelper.getTempleData(this);

        if (list == null) return;

        for (MapDataResponse.Result item : list) {

            String key = "A_" + item.getAnnadhanamId();

            if (notified.contains(key)) continue;

            try {
                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(), loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= ANNADANAM_DISTANCE) {

                    sendAnnadanam(item, res[0]);
                    notified.add(key);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================= TEMPLE =================
    private void checkTemples(Location loc) {

        if (!isTempleMapActive) return;

        List<TempleMapDataResponse.Result> list =
                SharedPreferenceManager.getTempleData(this);

        if (list == null) return;

        for (TempleMapDataResponse.Result item : list) {

            String key = "T_" + item.getTempleId();

            if (notified.contains(key)) continue;

            try {
                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(), loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= TEMPLE_DISTANCE) {

                    sendTemple(item, res[0]);
                    notified.add(key);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================= AYYAPPA =================
    private void checkAyyappa(Location loc) {

        if (!isAyyappaMapActive) return;

        List<AyyappaTempleMapDataResponse.Result> list =
                SharedManager.getTempleData(this);

        if (list == null) return;

        for (AyyappaTempleMapDataResponse.Result item : list) {

            String key = "Y_" + item.getTempleId();

            if (notified.contains(key)) continue;

            try {
                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(), loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= AYYAPPA_DISTANCE) {

                    sendAyyappa(item, res[0]);
                    notified.add(key);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================= NOTIFICATIONS =================

    private void sendAnnadanam(MapDataResponse.Result item, float dist) {
        sendBase(CHANNEL_ANNADANAM,
                item.getAnnadhanamNameTelugu(),
                dist,
                new Intent(this, AnadanamActivity.class));
    }

    private void sendTemple(TempleMapDataResponse.Result item, float dist) {
        sendBase(CHANNEL_TEMPLE,
                item.getTempleNameTelugu(),
                dist,
                new Intent(this, ViewAllTemplesActivity.class));
    }

    private void sendAyyappa(AyyappaTempleMapDataResponse.Result item, float dist) {
        sendBase(CHANNEL_AYYAPPA,
                item.getTempleNameTelugu(),
                dist,
                new Intent(this, ViewAllAyyappaTemplesActivity.class));
    }

    private void sendBase(String channel, String title, float dist, Intent intent) {

        String text = String.format(Locale.getDefault(), "%.1f KM away", dist / 1000);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channel)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pi);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) return;

        NotificationManagerCompat.from(this)
                .notify((int) System.currentTimeMillis(), builder.build());
    }

    // ================= FOREGROUND =================
    private Notification buildForegroundNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ANNADANAM)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Map Tracking Active")
                .setContentText("Nearby places monitoring")
                .setOngoing(true)
                .build();
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ANNADANAM, "Annadanam", NotificationManager.IMPORTANCE_HIGH));

            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_TEMPLE, "Temples", NotificationManager.IMPORTANCE_HIGH));

            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_AYYAPPA, "Ayyappa", NotificationManager.IMPORTANCE_HIGH));
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }

        if (client != null && locationCallback != null) {
            client.removeLocationUpdates(locationCallback);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}