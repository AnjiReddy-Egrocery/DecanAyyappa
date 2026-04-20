package com.dst.ayyapatelugu.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dst.ayyapatelugu.Activity.PanchagamActivity;
import com.dst.ayyapatelugu.Activity.ViewAllNewsListActivity;
import com.dst.ayyapatelugu.Activity.ViewAllTemplesActivity;
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
                    intent = new Intent(this, ViewAllNewsListActivity.class);

                    break;

                case "temple":
                    intent = new Intent(this, ViewAllTemplesActivity.class);
                    break;

                case "panchangam":
                    intent = new Intent(this, PanchagamActivity.class);
                    break;

                case "blog":
                    intent = new Intent(this, ViewAllNewsListActivity.class);
                    String blogId = message.getData().get("blogId");
                    intent.putExtra("blogId", blogId);
                    break;

                default:
                    intent = new Intent(this, LoginActivity.class);
                    break;
            }

            showNotification(title, body, intent);
        }
    }

    private void showNotification(String title, String body, Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
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
                        .setContentIntent(pendingIntent) // 🔥 VERY IMPORTANT
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(0, builder.build());
    }
}
