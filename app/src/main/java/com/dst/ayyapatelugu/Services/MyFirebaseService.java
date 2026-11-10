package com.dst.ayyapatelugu.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.text.HtmlCompat;

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
import com.dst.ayyapatelugu.Activity.ViewAnadanamDetailsActivity;
import com.dst.ayyapatelugu.Activity.ViewTempleListDetailsActivity;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.User.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseService extends FirebaseMessagingService {

    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.dst.ayyapatelugu";

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
        Map<String, String> data = message.getData();
        if (data.isEmpty()) return;

        String title = cleanHtml(data.get("title"));
        String body = cleanHtml(data.get("message"));
        String type = data.get("notificationFor");
        if (type == null) type = "";

        Intent intent;

        switch (type) {

            case "news":
                intent = new Intent(this, ViewAllNewsDetailsActivity.class);
                putId(intent, data, "newsId", "newsId");
                break;

            case "guruswami":
                intent = new Intent(this, GuruSwamiDetailsActivity.class);
                putId(intent, data, "guruswamiId", "guruswamiId");
                break;

            case "bajanamandali":
                intent = new Intent(this, AyyapaMandaliDetailsActivity.class);
                putId(intent, data, "bajanamandaliId", "bajanamandaliId");
                break;

            case "tourpackage":
                intent = new Intent(this, AyyappaTourseActivity.class);
                putId(intent, data, "tourpackageId", "tourpackageId");
                break;

            case "decorator":
                intent = new Intent(this, AyyapaPetamDetailsActivity.class);
                putId(intent, data, "decoratorId", "decoratorId");
                break;

            case "product":
                intent = new Intent(this, ProductDetailsActivity.class);
                putId(intent, data, "productId", "productId");
                break;

            case "book":
                intent = new Intent(this, AyyappaBooksDetailsActivity.class);
                putId(intent, data, "bookId", "bookId");
                break;

            case "activity":
                intent = new Intent(this, AyyapaKarmaDetailsActivity.class);
                putId(intent, data, "activitiesId", "activitiesId");
                break;

            case "ayyappacalender":
                intent = new Intent(this, CalenderActivity.class);
                intent.putExtra("Year", clean(data.get("Year")));
                break;

            case "telugucalender":
                intent = new Intent(this, TeluguCalenderActivity.class);
                intent.putExtra("month", clean(data.get("month")));
                intent.putExtra("year", clean(data.get("year")));
                break;

            case "panchagam":
                intent = new Intent(this, PanchagamActivity.class);
                intent.putExtra("date", clean(data.get("date")));
                break;

            case "blog":
                intent = new Intent(this, BlogDetailsActivity.class);
                putId(intent, data, "blogId", "blogId");
                break;

            case "bajanasong":
                intent = new Intent(this, AyyappaBajanaSognsDetailsActivity.class);
                putId(intent, data, "songId", "songId");
                break;

            case "padayatra":
                intent = new Intent(this, PadayatraDetailsActivity.class);
                putId(intent, data, "padayatrabrundamId", "padayatrabrundamId");
                break;

            case "temple":
                intent = new Intent(this, ViewTempleListDetailsActivity.class);
                putId(intent, data, "templeId", "templeId");
                break;

            case "ayyappatemple":
                intent = new Intent(this, ViewTempleListDetailsActivity.class);
                putId(intent, data, "templeId", "templeId");
                break;

            case "annadhanam": {
                intent = new Intent(this, ViewAnadanamDetailsActivity.class);
                String id = clean(data.get("annadhanamId"));
                intent.putExtra("annadhanamId", id);
                intent.putExtra("templeId", id);   // old key kuda (activity edi chaduvutundo teliyaledu)
                break;
            }

            case "images":
                intent = new Intent(this, ImagesListActivity.class);
                intent.putExtra("images", "true");
                break;

            case "videos":
                intent = new Intent(this, PostVideosActivity.class);
                intent.putExtra("videos", "true");
                break;

            case "appupdate":
                // direct ga Play Store (Play Store app lekapote browser lo open avtundi)
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL));
                break;

            default:
                intent = new Intent(this, LoginActivity.class);
                break;
        }

        showNotification(title, body, intent);
    }

    /**
     * FCM data lo nunchi id teesi, activity chaduvu key tho pedutundi.
     * Pata "newsId" key kuda pedutunnam, activity pata key chaduvuthe kuda pani chestundi.
     */
    private void putId(Intent intent, Map<String, String> data, String fcmKey, String extraKey) {
        String id = clean(data.get(fcmKey));
        Log.d("FCM_DEBUG", fcmKey + " = " + id);
        intent.putExtra(extraKey, id);
        if (!"newsId".equals(extraKey)) {
            intent.putExtra("newsId", id);
        }
    }

    private String clean(String value) {
        if (value == null) return null;
        return value.replace("'", "").trim();
    }

    private String cleanHtml(String text) {
        if (text == null) return "";

        String result = text;

        // 1st pass: escaped entities (&lt; &gt;) ni nijamaina tags ga marustundi
        result = HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();

        // 2nd pass: migilipoyina tags teesestundi
        result = result.replaceAll("(?s)<[^>]*>", "");

        // 3rd pass: inka entities unte decode
        result = HtmlCompat.fromHtml(result, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();

        return result
                .replace('\u00A0', ' ')
                .replace("\uFFFC", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private void showNotification(String title, String body, Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int notificationId = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
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
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                        .setSmallIcon(R.drawable.applogo)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(notificationId, builder.build());
    }
}