package com.dst.ayyapatelugu.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dst.ayyapatelugu.Activity.AyyapaKarmaDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyapaMandaliDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyapaPetamDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBajanaSognsDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaBooksDetailsActivity;
import com.dst.ayyapatelugu.Activity.AyyappaTourseActivity;
import com.dst.ayyapatelugu.Activity.BlogDetailsActivity;
import com.dst.ayyapatelugu.Activity.CalenderActivity;
import com.dst.ayyapatelugu.Activity.GuruSwamiDetailsActivity;
import com.dst.ayyapatelugu.Activity.ImagesListActivity;
import com.dst.ayyapatelugu.Activity.PadayatraDetailsActivity;
import com.dst.ayyapatelugu.Activity.PanchagamActivity;
import com.dst.ayyapatelugu.Activity.PostVideosActivity;
import com.dst.ayyapatelugu.Activity.ProductDetailsActivity;
import com.dst.ayyapatelugu.Activity.TeluguCalenderActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
import com.dst.ayyapatelugu.Activity.ViewAnadanamDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.User.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCM_TOKEN", token);

        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM_TOPIC", "Subscribed to all_users");
                    } else {
                        Log.d("FCM_TOPIC", "Subscription failed");
                    }
                });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getData().size() > 0) {

            String title = message.getData().get("title");
            String body = message.getData().get("message");
            String type = message.getData().get("notificationFor");

            Intent intent;

            switch (type) {

                case "news":
                    intent = new Intent(this, ViewAllNewsDetailsActivity.class);
                    String newsId = message.getData().get("newsId");

                    if (newsId != null) {
                        newsId = newsId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + newsId);

                    intent.putExtra("newsId", newsId);


                    break;

                case "guruswami":
                    intent = new Intent(this, GuruSwamiDetailsActivity.class);
                    String guruswamiId = message.getData().get("guruswamiId");

                    if (guruswamiId != null) {
                        guruswamiId = guruswamiId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + guruswamiId);

                    intent.putExtra("newsId", guruswamiId);


                    break;

                case "bajanamandali":
                    intent = new Intent(this, AyyapaMandaliDetailsActivity.class);
                    String bajanamandaliId = message.getData().get("bajanamandaliId");

                    if (bajanamandaliId != null) {
                        bajanamandaliId = bajanamandaliId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + bajanamandaliId);

                    intent.putExtra("newsId", bajanamandaliId);
                    break;

                case "tourpackage":
                    intent = new Intent(this, AyyappaTourseActivity.class);
                    String tourpackageId = message.getData().get("tourpackageId");

                    if (tourpackageId != null) {
                        tourpackageId = tourpackageId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + tourpackageId);

                    intent.putExtra("newsId", tourpackageId);
                    break;

                case "decorator":
                    intent = new Intent(this, AyyapaPetamDetailsActivity.class);
                    String decoratorId = message.getData().get("decoratorId");

                    if (decoratorId != null) {
                        decoratorId = decoratorId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + decoratorId);

                    intent.putExtra("newsId", decoratorId);
                    break;

                case "product":
                    intent = new Intent(this, ProductDetailsActivity.class);
                    String productId = message.getData().get("productId");

                    if (productId != null) {
                        productId = productId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + productId);

                    intent.putExtra("newsId", productId);
                    break;

                case "book":
                    intent = new Intent(this, AyyappaBooksDetailsActivity.class);
                    String bookId = message.getData().get("bookId");

                    if (bookId != null) {
                        bookId = bookId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + bookId);

                    intent.putExtra("newsId", bookId);
                    break;

                case "activity":
                    intent = new Intent(this, AyyapaKarmaDetailsActivity.class);
                    String activitiesId = message.getData().get("activitiesId");

                    if (activitiesId != null) {
                        activitiesId = activitiesId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + activitiesId);

                    intent.putExtra("newsId", activitiesId);
                    break;

                case "ayyappacalender":
                    intent = new Intent(this, CalenderActivity.class);

                    String Year = message.getData().get("Year");

                    if (Year != null) {
                        Year = Year.replace("'", "").trim();
                    }

                    Log.d("FCM_DEBUG", "Calendar Year: " + Year);

                    intent.putExtra("Year", Year);

                    break;

                case "telugucalender":

                    Log.d("FCM_DEBUG", "Telugu Calendar notification received");

                    intent = new Intent(this, TeluguCalenderActivity.class);

                    String month = message.getData().get("month");
                    String year = message.getData().get("year");

                    if (month != null) {
                        month = month.replace("'", "").trim();
                    }

                    if (year != null) {
                        year = year.replace("'", "").trim();
                    }

                    Log.d("FCM_DEBUG", "FCM Month: " + month);
                    Log.d("FCM_DEBUG", "FCM Year: " + year);

                    intent.putExtra("month", month);
                    intent.putExtra("year", year);

                    break;

                case "panchagam":

                    Log.d("FCM_DEBUG", "Panchagam notification received");

                    intent = new Intent(this, PanchagamActivity.class);

                    String date = message.getData().get("date");

                    if (date != null) {
                        date = date.replace("'", "").trim();
                    }

                    Log.d("FCM_DEBUG", "FCM Date: " + date);

                    intent.putExtra("date", date);

                    break;

                case "blog":
                    intent = new Intent(this, BlogDetailsActivity.class);
                    String blogId = message.getData().get("blogId");

                    if (blogId != null) {
                        blogId = blogId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + blogId);

                    intent.putExtra("newsId", blogId);
                    break;

                case "bajanasong":
                    intent = new Intent(this, AyyappaBajanaSognsDetailsActivity.class);
                    String songId = message.getData().get("songId");

                    if (songId != null) {
                        songId = songId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean NewsId: " + songId);

                    intent.putExtra("newsId", songId);
                    break;

                case "padayatra":
                    intent = new Intent(this, PadayatraDetailsActivity.class);
                    String padayatrabrundamId = message.getData().get("padayatrabrundamId");

                    if (padayatrabrundamId != null) {
                        padayatrabrundamId = padayatrabrundamId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean padayatrabrundamId: " + padayatrabrundamId);

                    intent.putExtra("padayatrabrundamId", padayatrabrundamId);
                    break;

                case "temple":
                    intent = new Intent(this, ViewTempleListDetailsActivity.class);
                    String templeId = message.getData().get("templeId");

                    if (templeId != null) {
                        templeId = templeId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean templeId: " + templeId);

                    intent.putExtra("templeId", templeId);
                    break;

                case "ayyappatemple":
                    intent = new Intent(this, ViewTempleListDetailsActivity.class);
                    String ayyappatempleId = message.getData().get("templeId");

                    if (ayyappatempleId != null) {
                        ayyappatempleId = ayyappatempleId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean templeId: " + ayyappatempleId);

                    intent.putExtra("templeId", ayyappatempleId);
                    break;

                case "annadhanam":
                    intent = new Intent(this, ViewAnadanamDetailsActivity.class);
                    String annadhanamId = message.getData().get("annadhanamId");

                    if (annadhanamId != null) {
                        annadhanamId = annadhanamId.replace("'", "").trim(); // 🔥 fix here also
                    }

                    Log.d("FCM_DEBUG", "Clean annadhanamId: " + annadhanamId);

                    intent.putExtra("templeId", annadhanamId);
                    break;

                case "images":

                    Log.d("FCM_DEBUG", "Images notification received");

                    intent = new Intent(this, ImagesListActivity.class);

                    intent.putExtra("images", "true");

                    break;

                case "videos":

                    Log.d("FCM_DEBUG", "Videos notification received");

                    intent = new Intent(this, PostVideosActivity.class);

                    intent.putExtra("videos", "true");

                    break;


                default:
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }

            showNotification(title, body, intent);
        }
    }

    private void showNotification(String title, String body, Intent intent) {


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "fcm_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "FCM Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.applogo)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(0, builder.build());
    }
}
