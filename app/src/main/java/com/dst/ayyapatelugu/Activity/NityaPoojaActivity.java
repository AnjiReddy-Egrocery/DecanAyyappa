package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.NityaPoojaModel;
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

public class NityaPoojaActivity extends AppCompatActivity {

    // Image base URL (okate vaadali, rendu chotla same). Mee server path ki taggattu marchandi.
    private static final String IMAGE_BASE_URL = "https://www.ayyappatelugu.com/public/assets/img/activity/";

    // Zoom settings
    private static final int MIN_ZOOM = 70;    // percent
    private static final int MAX_ZOOM = 250;   // percent

    Toolbar toolbar;
    TextView txtName;
    ImageView imageView, imageAnadanam;
    TextView textAndanam;
    String activityId = "29";
    WebView webView;

    // Pinch zoom (fingers tho zoom)
    private int textZoom = 100;
    private ScaleGestureDetector scaleDetector;
    private float pinchZoom = 100f;
    private boolean pinching = false;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nitya_pooja);

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
                startActivity(new Intent(NityaPoojaActivity.this, AnadanamActivity.class)));

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(NityaPoojaActivity.this, AnadanamActivity.class)));

        // ---------- Content views ----------
        txtName = findViewById(R.id.txt_name);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);
        imageView = findViewById(R.id.image_view);

        // ---------- Zoom (pinch only) ----------
        try {
            textZoom = Integer.parseInt(getFromPreferences("textZoom", "100"));
        } catch (NumberFormatException e) {
            textZoom = 100;
        }
        applyZoom(true);
        scaleDetector = new ScaleGestureDetector(this, new PinchListener());

        // ---------- Load cached data first (instant), then refresh from API ----------
        String savedTitle = getFromPreferences("title", null);
        String savedDescription = getFromPreferences("description", null);
        String savedPhoto = getFromPreferences("photo", null);

        if (savedTitle != null && savedDescription != null && savedPhoto != null) {
            showData(savedTitle, savedDescription, savedPhoto);
        }

        // API call okasari matrame
        String savedActivityId = getFromPreferences("activityId", activityId);
        resultMethod(savedActivityId);
        saveToPreferences("activityId", savedActivityId);
    }

    // ================= ZOOM =================
    private void applyZoom(boolean save) {
        webView.getSettings().setTextZoom(textZoom);
        if (save) {
            saveToPreferences("textZoom", String.valueOf(textZoom));
        }
    }

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
            saveToPreferences("textZoom", String.valueOf(textZoom));
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
    private void showData(String title, String description, String photo) {
        txtName.setText(title);

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
    @SuppressLint("NewApi")
    private void resultMethod(String activityId) {
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
        RequestBody activityIdPart = RequestBody.create(MediaType.parse("text/plain"), activityId);
        Call<NityaPoojaModel> call = apiClient.PostActivityId(activityIdPart);

        call.enqueue(new Callback<NityaPoojaModel>() {
            @Override
            public void onResponse(Call<NityaPoojaModel> call, Response<NityaPoojaModel> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful()) {
                    NityaPoojaModel model = response.body();
                    if (model != null && "200".equals(model.getErrorCode())
                            && model.getResult() != null && !model.getResult().isEmpty()) {

                        String title = model.getResult().get(0).getTitle();
                        String description = model.getResult().get(0).getDescription();
                        String photo = model.getResult().get(0).getImage();

                        showData(title, description, photo);

                        saveToPreferences("title", title);
                        saveToPreferences("description", description);
                        saveToPreferences("photo", photo);
                    }
                } else {
                    Toast.makeText(NityaPoojaActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NityaPoojaModel> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                Toast.makeText(NityaPoojaActivity.this,
                        "Request Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ================= SharedPreferences =================
    private void saveToPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private String getFromPreferences(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
