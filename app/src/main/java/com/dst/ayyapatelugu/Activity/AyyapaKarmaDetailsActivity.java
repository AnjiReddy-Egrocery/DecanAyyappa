package com.dst.ayyapatelugu.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.ActivitiesDetailsResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AyyapaKarmaDetailsActivity extends AppCompatActivity {

    // Zoom settings
    private static final int MIN_ZOOM = 70;    // percent
    private static final int MAX_ZOOM = 250;   // percent

    Toolbar toolbar;

    TextView txtName;
    WebView webView;

    ImageView imageView;

    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;

    String activitiesId;

    // Pinch zoom (fingers tho zoom)
    private int textZoom = 100;
    private ScaleGestureDetector scaleDetector;
    private float pinchZoom = 100f;
    private boolean pinching = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyapa_karma_details);

        toolbar = findViewById(R.id.toolbar);

        activitiesId = getIntent().getStringExtra("activitiesId");
        if (activitiesId != null) {
            activitiesId = activitiesId.replace("'", "").trim(); // remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + activitiesId);

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
                startActivity(new Intent(AyyapaKarmaDetailsActivity.this, AnadanamActivity.class)));

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(AyyapaKarmaDetailsActivity.this, AnadanamActivity.class)));

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(AyyapaKarmaDetailsActivity.this, NityaPoojaActivity.class)));

        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(AyyapaKarmaDetailsActivity.this, NityaPoojaActivity.class)));

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

        loadPoojakaryakaramamDetails(activitiesId);
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
    private void loadPoojakaryakaramamDetails(String activitiesId) {
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
        Call<ActivitiesDetailsResponse> call = apiClient.getActivitiesDetails(activitiesId);
        call.enqueue(new Callback<ActivitiesDetailsResponse>() {
            @Override
            public void onResponse(Call<ActivitiesDetailsResponse> call, Response<ActivitiesDetailsResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    ActivitiesDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        ActivitiesDetailsResponse.ActivitiesItem item = data.result.get(0);

                        txtName.setText(item.getTitle());

                        String htmlContent = "<html><head>" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                                "<style>" +
                                "body { background-color: transparent; color: white; font-size: 14px; line-height: 1.6; }" +
                                "* { color: white !important; }" +
                                "</style>" +
                                "</head><body>" + item.getDescription() + "</body></html>";

                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

                        String fullImageUrl = data.imageUrl + item.image;
                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get().load(fullImageUrl).into(imageView);
                    }
                }
            }

            @Override
            public void onFailure(Call<ActivitiesDetailsResponse> call, Throwable t) {
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
