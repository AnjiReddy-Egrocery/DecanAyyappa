package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.SharanughosaModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.squareup.picasso.Picasso;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SharanughoshaActivity extends AppCompatActivity {

    // Image base URL (okate vaadali, rendu chotla same). Mee server path ki taggattu marchandi.
    private static final String IMAGE_BASE_URL = "https://www.ayyappatelugu.com/public/assets/img/activity/";

    // Zoom settings
    private static final int MIN_ZOOM = 70;    // percent
    private static final int MAX_ZOOM = 250;   // percent
    private static final float BASE_SMALL_TEXT_SP = 14f; // small description original size

    Toolbar toolbar;
    TextView txtName, txtsmallDiscription;
    WebView webView;
    ImageView imageView, imageAnadanam, imgNityaPooja;
    TextView textAndanam, txtNityaPooja;

    String activitiesId = "21";
    private int textZoom = 100;

    // Pinch zoom (hand tho zoom)
    private ScaleGestureDetector scaleDetector;
    private float pinchZoom = 100f;
    private boolean pinching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharanughosha);

        // ---------- Toolbar ----------
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ---------- Bottom bar ----------
        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(v ->
                startActivity(new Intent(SharanughoshaActivity.this, AnadanamActivity.class)));

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(SharanughoshaActivity.this, AnadanamActivity.class)));

        imgNityaPooja = findViewById(R.id.img_nitya_pooja);
        imgNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(SharanughoshaActivity.this, NityaPoojaActivity.class)));

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(SharanughoshaActivity.this, NityaPoojaActivity.class)));

        // ---------- Content views ----------
        txtName = findViewById(R.id.txt_sharanu_name);
        webView = findViewById(R.id.sharanu_webview);
        imageView = findViewById(R.id.sharanu_image_view);
        txtsmallDiscription = findViewById(R.id.sharanu_small_discription);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);

        // ---------- Zoom (pinch only) ----------
        try {
            textZoom = Integer.parseInt(getSharanuSharedPreferences("textZoom", "100"));
        } catch (NumberFormatException e) {
            textZoom = 100;
        }
        applyZoom(true);
        scaleDetector = new ScaleGestureDetector(this, new PinchListener());

        // ---------- Load cached data first (instant), then refresh from API ----------
        String savedTitle = getSharanuSharedPreferences("title", null);
        String savedDescription = getSharanuSharedPreferences("description", null);
        String savedPhoto = getSharanuSharedPreferences("photo", null);
        String savedSmallDiscription = getSharanuSharedPreferences("smallDescription", null);

        if (savedTitle != null && savedDescription != null
                && savedPhoto != null && savedSmallDiscription != null) {
            showData(savedTitle, savedDescription, savedPhoto, savedSmallDiscription);
        }

        // API call okasari matrame
        String savedActivityId = getSharanuSharedPreferences("activitiesId", activitiesId);
        resultMethod(savedActivityId);
        saveToSharanuSharedPreferences("activitiesId", savedActivityId);
    }

    // ================= ZOOM =================
    private void applyZoom(boolean save) {
        // WebView text (description)
        webView.getSettings().setTextZoom(textZoom);

        // Small description TextView
        txtsmallDiscription.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                BASE_SMALL_TEXT_SP * textZoom / 100f);

        // Save so next time same zoom untundi
        if (save) {
            saveToSharanuSharedPreferences("textZoom", String.valueOf(textZoom));
        }
    }

    // ================= PINCH ZOOM =================
    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            pinchZoom = textZoom;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            pinchZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, pinchZoom * detector.getScaleFactor()));
            int newZoom = Math.round(pinchZoom);
            if (newZoom != textZoom) {
                textZoom = newZoom;
                applyZoom(false); // save at the end of pinch
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            saveToSharanuSharedPreferences("textZoom", String.valueOf(textZoom));
        }
    }

    // Screen meeda ekkada pinch chesina zoom avtundi (ScrollView/WebView lopala unna sare)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            pinching = false;
        }

        if (ev.getPointerCount() > 1 || scaleDetector.isInProgress() || pinching) {
            if (!pinching) {
                pinching = true;
                // child views ki (WebView/ScrollView) cancel pampi, scroll aapali
                MotionEvent cancel = MotionEvent.obtain(ev);
                cancel.setAction(MotionEvent.ACTION_CANCEL);
                super.dispatchTouchEvent(cancel);
                cancel.recycle();
            }
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    // ================= UI =================
    private void showData(String title, String description, String photo, String smallDescription) {
        txtName.setText(title);
        txtsmallDiscription.setText(smallDescription);

        if (photo != null && !photo.isEmpty()) {
            Picasso.get().load(IMAGE_BASE_URL + photo).into(imageView);
        }

        String htmlContent = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                "<style>" +
                "body { background-color: transparent; color: white; font-size: 14px; line-height: 1.6; }" +
                "* { color: white !important; }" +
                "</style>" +
                "</head><body>" + description + "</body></html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    // ================= API =================
    private void resultMethod(String activitiesId) {
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

        APiInterface apiClient = retrofit.create(APiInterface.class);
        RequestBody activityIdPart = RequestBody.create(MediaType.parse("text/plain"), activitiesId);
        Call<SharanughosaModel> call = apiClient.PostActivity(activityIdPart);

        call.enqueue(new Callback<SharanughosaModel>() {
            @Override
            public void onResponse(Call<SharanughosaModel> call, Response<SharanughosaModel> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful()) {
                    SharanughosaModel model = response.body();
                    if (model != null && "200".equals(model.getErrorCode())
                            && model.getResult() != null && !model.getResult().isEmpty()) {

                        String title = model.getResult().get(0).getTitle();
                        String description = model.getResult().get(0).getDescription();
                        String smallDescription = model.getResult().get(0).getSmallDescription();
                        String photo = model.getResult().get(0).getImage();

                        showData(title, description, photo, smallDescription);

                        saveToSharanuSharedPreferences("title", title);
                        saveToSharanuSharedPreferences("description", description);
                        saveToSharanuSharedPreferences("photo", photo);
                        saveToSharanuSharedPreferences("smallDescription", smallDescription);
                    }
                } else {
                    Toast.makeText(SharanughoshaActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SharanughosaModel> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(SharanughoshaActivity.this,
                        "Request Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ================= SharedPreferences =================
    private void saveToSharanuSharedPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private String getSharanuSharedPreferences(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
