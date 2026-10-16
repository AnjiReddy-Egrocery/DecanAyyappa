package com.dst.ayyapatelugu.Services;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.dst.ayyapatelugu.Activity.*;
import com.dst.ayyapatelugu.HomeActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * One place that decides: open INSIDE the app, or open in the BROWSER.
 *
 * Admin panel lo link ela ivvali:
 *   Web link   ->  https://www.google.com            (browser lo open avtundi)
 *   App page   ->  app://anadanam                    (app lo AnadanamActivity open avtundi)
 *   App detail ->  app://news?newsId=25              (app lo News details, id=25)
 *                  app://annadhanam?annadhanamId=10
 *   Plain name ->  guruswami                         (same as app://guruswami)
 *   Other app  ->  pkg://com.whatsapp                (installed ayithe open, lekapothe Play Store)
 *   Other app  ->  otherapp://page?id=5              (aa app ki deep link, lekapothe error toast)
 *   Phone etc. ->  tel:+919999999999 , mailto:abc@x.com , whatsapp://send?phone=91...
 */
public class LinkRouter {

    private static final String APP_SCHEME = "app";

    // ---- route name -> Activity (list / main pages) ----
    private static final Map<String, Class<?>> ROUTES = new HashMap<>();

    static {
        // Home
        ROUTES.put("home", HomeActivity.class);

        // List / main pages (same as Home screen + drawer menu)
        ROUTES.put("anadanam", AnadanamActivity.class);
        ROUTES.put("temples", ViewAllTemplesActivity.class);
        ROUTES.put("ayyappatemples", ViewAllAyyappaTemplesActivity.class);
        ROUTES.put("bajanamandali", AyyappaMandaliListActivity.class);
        ROUTES.put("guruswami", GuruSwamiListActivity.class);
        ROUTES.put("poojapetam", AyyappaPetamListActivity.class);
        ROUTES.put("sharanughosha", SharanughoshaActivity.class);
        ROUTES.put("nityapooja", NityaPoojaActivity.class);
        ROUTES.put("calender", CalenderActivity.class);
        ROUTES.put("telugucalender", TeluguCalenderActivity.class);
        ROUTES.put("panchagam", PanchagamActivity.class);
        ROUTES.put("tours", AyyappaTourseDetailsACtivity.class);
        ROUTES.put("books", AyyapaBooksListActivity.class);
        ROUTES.put("products", ProductsListActivity.class);
        ROUTES.put("karyam", AyyappaKaryamListActivity.class);
        ROUTES.put("blogs", BlogsActivity.class);
        ROUTES.put("padayatra", PadayatraActivity.class);
        ROUTES.put("songs", AyyappaBajanaSognsActivity.class);
        ROUTES.put("images", ImagesListActivity.class);
        ROUTES.put("videos", PostVideosActivity.class);
        ROUTES.put("news", ViewAllNewsListActivity.class);
        ROUTES.put("about", AboutActivity.class);

        // Detail pages (need an id in the query, same keys as SplashActivity)
        ROUTES.put("newsdetails", ViewAllNewsDetailsActivity.class);            // newsId
        ROUTES.put("guruswamidetails", GuruSwamiDetailsActivity.class);         // guruswamiId
        ROUTES.put("bajanamandalidetails", AyyapaMandaliDetailsActivity.class); // bajanamandaliId
        ROUTES.put("tourdetails", AyyappaTourseActivity.class);                 // tourpackageId
        ROUTES.put("petamdetails", AyyapaPetamDetailsActivity.class);           // decoratorId
        ROUTES.put("productdetails", ProductDetailsActivity.class);             // productId
        ROUTES.put("bookdetails", AyyappaBooksDetailsActivity.class);           // bookId
        ROUTES.put("karmadetails", AyyapaKarmaDetailsActivity.class);           // activitiesId
        ROUTES.put("blogdetails", BlogDetailsActivity.class);                   // blogId
        ROUTES.put("songdetails", AyyappaBajanaSognsDetailsActivity.class);     // songId
        ROUTES.put("padayatradetails", PadayatraDetailsActivity.class);         // padayatrabrundamId
        ROUTES.put("templedetails", ViewTempleListDetailsActivity.class);       // templeId
        ROUTES.put("annadhanam", ViewAnadanamDetailsActivity.class);            // annadhanamId
    }

    /** Call this from any adapter / activity when a banner or ad is tapped. */
    public static void open(Context context, String link) {
        if (context == null || TextUtils.isEmpty(link)) return;

        link = link.trim();

        // "guruswami" ani matrame ichchina in-app ga teesukovali
        if (!link.contains(":") && !link.contains(".") && ROUTES.containsKey(link.toLowerCase())) {
            link = APP_SCHEME + "://" + link;
        }

        Uri uri = Uri.parse(link);
        String scheme = uri.getScheme();

        if (scheme == null || "http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            openInBrowser(context, link);                     // web link (Android YouTube/Play Store app ki thane pampistundi)
        } else if (APP_SCHEME.equalsIgnoreCase(scheme)) {
            openInApp(context, uri);                          // app://...  -> this app
        } else if ("pkg".equalsIgnoreCase(scheme)) {
            openOtherApp(context, uri.getHost());             // pkg://com.other.app -> another installed app
        } else {
            openExternalScheme(context, link);                // tel:, mailto:, whatsapp://, otherapp:// ...
        }
    }

    // ---------------- IN-APP ----------------
    private static void openInApp(Context context, Uri uri) {
        // app://anadanam?x=1  -> host = "anadanam"
        String route = uri.getHost();
        if (TextUtils.isEmpty(route)) {
            route = uri.getPath() == null ? "" : uri.getPath().replace("/", "");
        }
        route = route.toLowerCase();

        Class<?> target = ROUTES.get(route);
        if (target == null) {
            Log.w("LinkRouter", "Unknown in-app route: " + route);
            Toast.makeText(context, "This page is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(context, target);

        // every query parameter becomes an intent extra: ?newsId=25 -> putExtra("newsId","25")
        for (String key : uri.getQueryParameterNames()) {
            intent.putExtra(key, uri.getQueryParameter(key));
        }

        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    // ---------------- OTHER APP (by package name) ----------------
    private static void openOtherApp(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) return;

        Intent launch = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) {
            if (!(context instanceof Activity)) launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch);
            return;
        }

        // Not installed (or not visible) -> Play Store page of that app
        try {
            Intent store = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            if (!(context instanceof Activity)) store.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(store);
        } catch (ActivityNotFoundException e) {
            openInBrowser(context, "https://play.google.com/store/apps/details?id=" + packageName);
        }
    }

    // ---------------- OTHER SCHEMES (tel:, mailto:, whatsapp://, otherapp://) ----------------
    private static void openExternalScheme(Context context, String link) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            if (!(context instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------- BROWSER ----------------
    private static void openInBrowser(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to open this link", Toast.LENGTH_SHORT).show();
        }
    }
}
