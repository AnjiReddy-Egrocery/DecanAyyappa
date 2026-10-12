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
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.BajanaSongDetailsResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AyyappaBajanaSognsDetailsActivity extends AppCompatActivity {

    // Zoom settings
    private static final int MIN_ZOOM = 70;    // percent
    private static final int MAX_ZOOM = 250;   // percent

    Toolbar toolbar;

    TextView txtName, txtSingerNamer;
    WebView webView;

    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;

    String songId;

    // Pinch zoom (fingers tho zoom)
    private int textZoom = 100;
    private ScaleGestureDetector scaleDetector;
    private float pinchZoom = 100f;
    private boolean pinching = false;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyappa_bajana_sogns_details);

        toolbar = findViewById(R.id.toolbar);

        songId = getIntent().getStringExtra("songId");
        if (songId != null) {
            songId = songId.replace("'", "").trim(); // remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + songId);

        // ---------- Toolbar ----------
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
                startActivity(new Intent(AyyappaBajanaSognsDetailsActivity.this, AnadanamActivity.class)));

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(AyyappaBajanaSognsDetailsActivity.this, AnadanamActivity.class)));

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(AyyappaBajanaSognsDetailsActivity.this, NityaPoojaActivity.class)));

        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(AyyappaBajanaSognsDetailsActivity.this, NityaPoojaActivity.class)));

        // ---------- Content views ----------
        txtName = findViewById(R.id.txt_songname);
        txtSingerNamer = findViewById(R.id.txt_singername);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.TRANSPARENT);

        // ---------- Zoom (pinch only) ----------
        try {
            textZoom = Integer.parseInt(getFromPreferences("textZoom", "100"));
        } catch (NumberFormatException e) {
            textZoom = 100;
        }
        applyZoom(true);
        scaleDetector = new ScaleGestureDetector(this, new PinchListener());

        loadBajanaSongDetails(songId);
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

    // ================= API =================
    private void loadBajanaSongDetails(String songId) {
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
        Call<BajanaSongDetailsResponse> call = apiClient.getBajanaSongDetails(songId);
        call.enqueue(new Callback<BajanaSongDetailsResponse>() {
            @Override
            public void onResponse(Call<BajanaSongDetailsResponse> call, Response<BajanaSongDetailsResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    BajanaSongDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        BajanaSongDetailsResponse.BajanaSongItem item = data.result.get(0);

                        txtName.setText(item.getSongTitle());
                        txtSingerNamer.setText(item.getSingerName());

                        String htmlContent = "<html><head>" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                                "<style>" +
                                "body { background-color: transparent; color: white; font-size: 16px; line-height: 1.6; }" +
                                "* { color: white !important; }" +
                                "</style>" +
                                "</head><body>" + item.getSongDescription() + "</body></html>";

                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
                    }
                }
            }

            @Override
            public void onFailure(Call<BajanaSongDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", String.valueOf(t.getMessage()));
            }
        });
    }

    // ================= SharedPreferences =================
    private void saveToPreferences(String key, String value) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private String getFromPreferences(String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
}
