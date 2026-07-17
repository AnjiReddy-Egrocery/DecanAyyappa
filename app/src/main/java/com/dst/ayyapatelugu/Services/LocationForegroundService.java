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
import android.util.Log;

import java.util.*;

public class LocationForegroundService extends Service {

    // Channels
    private static final String CHANNEL_ANNADANAM = "ANNADANAM_CHANNEL";
    private static final String CHANNEL_TEMPLE = "TEMPLE_CHANNEL";
    private static final String CHANNEL_AYYAPPA = "AYYAPPA_CHANNEL";

    // Distance limits
    private static final float ANNADANAM_DISTANCE = 5000f;
    private static final float TEMPLE_DISTANCE = 5000f;
    private static final float AYYAPPA_DISTANCE = 5000f;

    private FusedLocationProviderClient client;
    private LocationCallback locationCallback;

    private Handler handler;
    private Runnable runnable;

    // 🔥 MAP ACTIVE FLAGS
    private boolean isAnnadanamMapActive = false;
    private boolean isTempleMapActive = false;
    private boolean isAyyappaMapActive = false;

    // 🔥 Prevent duplicate in same session
    /*private Set<String> notified = new HashSet<>();*/

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


                    // First try cached location (fast)
                    client.getLastLocation()
                            .addOnSuccessListener(location -> {


                                if(location != null){

                                    Log.d(
                                            "TempleService",
                                            "Last Location Checking Annadanam"
                                    );


                                    checkAnnadanam(location);


                                }
                                else {


                                    // If last location unavailable
                                    client.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY,
                                            null
                                    ).addOnSuccessListener(currentLocation -> {


                                        if(currentLocation != null){

                                            Log.d(
                                                    "TempleService",
                                                    "Current Location Checking Annadanam"
                                            );


                                            checkAnnadanam(currentLocation);

                                        }


                                    });


                                }


                            });


                    break;

                case "TEMPLE_ON":

                    isTempleMapActive = true;

                    client.getLastLocation()
                            .addOnSuccessListener(location -> {


                                if(location != null){

                                    Log.d(
                                            "TempleService",
                                            "Last Location Checking Annadanam"
                                    );


                                    checkTemples(location);


                                }
                                else {


                                    // If last location unavailable
                                    client.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY,
                                            null
                                    ).addOnSuccessListener(currentLocation -> {


                                        if(currentLocation != null){

                                            Log.d(
                                                    "TempleService",
                                                    "Current Location Checking Annadanam"
                                            );


                                            checkTemples(location);


                                        }


                                    });


                                }


                            });


                    break;

                case "AYYAPPA_ON":

                    isAyyappaMapActive = true;


                    client.getLastLocation()
                            .addOnSuccessListener(location -> {


                                if(location != null){

                                    Log.d(
                                            "TempleService",
                                            "Last Location Checking Annadanam"
                                    );


                                    checkAyyappa(location);


                                }
                                else {


                                    // If last location unavailable
                                    client.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY,
                                            null
                                    ).addOnSuccessListener(currentLocation -> {


                                        if(currentLocation != null){

                                            Log.d(
                                                    "TempleService",
                                                    "Current Location Checking Annadanam"
                                            );


                                            checkAyyappa(location);


                                        }


                                    });


                                }


                            });




                    break;
                case "ANNADANAM_OFF":
                    isAnnadanamMapActive = false;
                    break;

                case "TEMPLE_OFF":
                    isTempleMapActive = false;
                    break;

                case "AYYAPPA_OFF":
                    isAyyappaMapActive = false;
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

        if (list == null || list.isEmpty()) {

            Log.d("TempleService",
                    "Annadanam data not available. Retry after 5 sec.");

            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> {

                        client.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                        ).addOnSuccessListener(location -> {

                            if (location != null) {
                                checkAnnadanam(location);
                            }
                        });

                    }, 1000);

            return;
        }

        ArrayList<MapDataResponse.Result> nearbyList = new ArrayList<>();

        for (MapDataResponse.Result item : list) {

//            String key = "A_" + item.getAnnadhanamId();
//
//            // Already notified అయితే skip
//            if (notified.contains(key))
//                continue;

            try {

                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= ANNADANAM_DISTANCE) {

                    nearbyList.add(item);

                    // తర్వాత duplicate notification రాకుండా

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!nearbyList.isEmpty()) {

            sendNearbyAnnadanamNotification(nearbyList);

        }
    }

    private void sendNearbyAnnadanamNotification(ArrayList<MapDataResponse.Result> nearbyList) {

        // Save nearby temples
        SharedPreferenceHelper.saveNearbyAnnadanam(
                this,
                nearbyList
        );


        Intent intent =
                new Intent(this, NearbyAnnadanamListActivity.class);

        intent.putExtra("OPEN_NEARBY", true);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                500,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ANNADANAM)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("\uD83D\uDE4F అన్నదానం నిర్వహించే దేవాలయాలు ")
                        .setContentText("మీ సమీపంలో "
                                + nearbyList.size()
                                + " దేవాలయాలు అందుబాటులో ఉన్నాయి.\n"
                                + "వివరాల కోసం క్లిక్ చేయండి."
                        )
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nearbyList.size() + " Annadanam temples are within 5 KM.\nTap to view the complete list."))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pi);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(this)
                .notify(1001, builder.build());
    }

    // ================= TEMPLE =================
    private void checkTemples(Location loc) {

        if (!isTempleMapActive) return;

        List<TempleMapDataResponse.Result> list =
                SharedPreferenceManager.getTempleData(this);

        if (list == null || list.isEmpty()) {

            Log.d("TempleService",
                    "Annadanam data not available. Retry after 5 sec.");

            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> {

                        client.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                        ).addOnSuccessListener(location -> {

                            if (location != null) {
                                checkTemples(location);
                            }
                        });

                    }, 1000);

            return;
        }

        ArrayList<TempleMapDataResponse.Result> nearbyList = new ArrayList<>();

        for (TempleMapDataResponse.Result item : list) {

//            String key = "A_" + item.getAnnadhanamId();
//
//            // Already notified అయితే skip
//            if (notified.contains(key))
//                continue;

            try {

                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= TEMPLE_DISTANCE) {

                    nearbyList.add(item);

                    // తర్వాత duplicate notification రాకుండా

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!nearbyList.isEmpty()) {

            sendNearbyTempleNotification(nearbyList);

        }


    }

    private void sendNearbyTempleNotification(ArrayList<TempleMapDataResponse.Result> nearbyList) {
        SharedPreferenceHelper.saveNearbyTemple(
                this,
                nearbyList
        );


        Intent intent =
                new Intent(this, NearbyTempleListActivity.class);

        intent.putExtra("OPEN_TEMPLE", true);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                500,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ANNADANAM)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("\uD83D\uDE4F దేవాలయాలు ")
                        .setContentText("మీ సమీపంలో "
                                + nearbyList.size()
                                + " దేవాలయాలు అందుబాటులో ఉన్నాయి.\n"
                                + "వివరాల కోసం క్లిక్ చేయండి."
                        )
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nearbyList.size() + " Temples are within 5 KM.\nTap to view the complete list."))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pi);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(this)
                .notify(1001, builder.build());

    }

    // ================= AYYAPPA =================
    private void checkAyyappa(Location loc) {


        if (!isAyyappaMapActive) return;

        List<AyyappaTempleMapDataResponse.Result> list =
                SharedManager.getTempleData(this);

        if (list == null || list.isEmpty()) {

            Log.d("TempleService",
                    "Annadanam data not available. Retry after 5 sec.");

            new Handler(Looper.getMainLooper())
                    .postDelayed(() -> {

                        client.getCurrentLocation(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                null
                        ).addOnSuccessListener(location -> {

                            if (location != null) {
                                checkAyyappa(location);
                            }
                        });

                    }, 1000);

            return;
        }

        ArrayList<AyyappaTempleMapDataResponse.Result> nearbyList = new ArrayList<>();

        for (AyyappaTempleMapDataResponse.Result item : list) {

//            String key = "A_" + item.getAnnadhanamId();
//
//            // Already notified అయితే skip
//            if (notified.contains(key))
//                continue;

            try {

                float[] res = new float[1];

                Location.distanceBetween(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude()),
                        res
                );

                if (res[0] <= AYYAPPA_DISTANCE) {

                    nearbyList.add(item);

                    // తర్వాత duplicate notification రాకుండా

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!nearbyList.isEmpty()) {

            sendNearbyAyyappaTempleNotification(nearbyList);

        }




    }

    private void sendNearbyAyyappaTempleNotification(ArrayList<AyyappaTempleMapDataResponse.Result> nearbyList) {
        SharedPreferenceHelper.saveNearbyAyyappaTemple(
                this,
                nearbyList
        );


        Intent intent =
                new Intent(this, NearbyAyyappaTempleListActivity.class);

        intent.putExtra("OPEN_AYYAPPATEMPLE", true);

        PendingIntent pi = PendingIntent.getActivity(
                this,
                500,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ANNADANAM)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("\uD83D\uDE4F అయ్యప్ప దేవాలయాలు ")
                        .setContentText("మీ సమీపంలో "
                                + nearbyList.size()
                                + " అయ్యప్ప దేవాలయాలు అందుబాటులో ఉన్నాయి.\n"
                                + "వివరాల కోసం క్లిక్ చేయండి."
                        )
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(nearbyList.size() + " AyyappaTemples are within 5 KM.\nTap to view the complete list."))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pi);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationManagerCompat.from(this)
                .notify(1001, builder.build());

    }

    // ================= NOTIFICATIONS =================

    private void sendAnnadanam(MapDataResponse.Result item, float dist) {
        Intent intent =
                new Intent(this, AnadanamActivity.class);


        intent.putExtra(
                "TEMPLE_ID",
                item.getAnnadhanamId()
        );


        intent.putExtra(
                "TEMPLE_LAT",
                item.getLatitude()
        );


        intent.putExtra(
                "TEMPLE_LNG",
                item.getLongitude()
        );


        sendBase(
                CHANNEL_ANNADANAM,
                item.getAnnadhanamNameTelugu(),
                dist,
                intent
        );
    }

    private void sendTemple(TempleMapDataResponse.Result item, float dist) {


        Intent intent =
                new Intent(this, ViewAllTemplesActivity.class);


        intent.putExtra(
                "TEMPLE_ID",
                item.getTempleId()
        );


        intent.putExtra(
                "TEMPLE_LAT",
                item.getLatitude()
        );


        intent.putExtra(
                "TEMPLE_LNG",
                item.getLongitude()
        );


        sendBase(
                CHANNEL_TEMPLE,
                item.getTempleNameTelugu(),
                dist,
                intent
        );
    }

    private void sendAyyappa(
            AyyappaTempleMapDataResponse.Result item,
            float dist) {


        Intent intent =
                new Intent(this, ViewAllAyyappaTemplesActivity.class);


        intent.putExtra(
                "TEMPLE_ID",
                item.getTempleId()
        );


        intent.putExtra(
                "TEMPLE_LAT",
                item.getLatitude()
        );


        intent.putExtra(
                "TEMPLE_LNG",
                item.getLongitude()
        );


        sendBase(
                CHANNEL_AYYAPPA,
                item.getTempleNameTelugu(),
                dist,
                intent
        );
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

        Log.d("TempleService",
                "POST_NOTIFICATIONS not granted");

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