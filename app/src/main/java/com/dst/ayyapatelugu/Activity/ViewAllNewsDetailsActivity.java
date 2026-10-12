package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.NewsDetailsResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewAllNewsDetailsActivity extends AppCompatActivity {

    // Zoom settings
    private static final int MIN_ZOOM = 70;    // percent
    private static final int MAX_ZOOM = 250;   // percent

    Toolbar toolbar;

    TextView txtName;

    ImageView imageView;

    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;
    private TextToSpeech textToSpeech;
    private ImageView playAudioIcon;
    ImageView playReplayIcon;
    private TextView textContent, txtViewMore;

    private boolean isPlaying = false; // Track the playing state

    private String[] textChunks;
    private int currentChunkIndex = 0;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "tts_prefs";
    private static final String KEY_INDEX = "current_index";
    String newsId;

    // Pinch zoom (fingers tho zoom)
    private int textZoom = 100;
    private float baseTextSizePx;           // news text original size
    private ScaleGestureDetector scaleDetector;
    private float pinchZoom = 100f;
    private boolean pinching = false;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_news_details);

        toolbar = findViewById(R.id.toolbar);

        newsId = getIntent().getStringExtra("newsId");
        if (newsId != null) {
            newsId = newsId.replace("'", "").trim(); // remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + newsId);

        txtViewMore = findViewById(R.id.txt_view_more);
        playAudioIcon = findViewById(R.id.play_audio_icon);
        playReplayIcon = findViewById(R.id.play_replay_icon);

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
                startActivity(new Intent(ViewAllNewsDetailsActivity.this, AnadanamActivity.class)));

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentChunkIndex = sharedPreferences.getInt(KEY_INDEX, 0); // Load saved index

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v ->
                startActivity(new Intent(ViewAllNewsDetailsActivity.this, AnadanamActivity.class)));

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(ViewAllNewsDetailsActivity.this, NityaPoojaActivity.class)));

        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(v ->
                startActivity(new Intent(ViewAllNewsDetailsActivity.this, NityaPoojaActivity.class)));

        // ---------- Content views ----------
        txtName = findViewById(R.id.txt_name);
        textContent = findViewById(R.id.webview);
        imageView = findViewById(R.id.image_view);

        txtViewMore.setOnClickListener(v ->
                startActivity(new Intent(ViewAllNewsDetailsActivity.this, ViewAllNewsListActivity.class)));

        // ---------- Zoom (pinch only) ----------
        baseTextSizePx = textContent.getTextSize(); // original size (px)
        try {
            textZoom = Integer.parseInt(getFromPreferences("textZoom", "100"));
        } catch (NumberFormatException e) {
            textZoom = 100;
        }
        applyZoom(true);
        scaleDetector = new ScaleGestureDetector(this, new PinchListener());

        // ---------- Text to Speech ----------
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("te", "IN"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Telugu language not supported!");
                    }

                    // Listener for utterance completion
                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {}

                        @Override
                        public void onDone(String utteranceId) {
                            currentChunkIndex++;
                            if (isPlaying && currentChunkIndex < textChunks.length) {
                                runOnUiThread(() -> speakNextChunk());
                            } else {
                                isPlaying = false;
                                sharedPreferences.edit().remove(KEY_INDEX).apply();
                                runOnUiThread(() -> playAudioIcon.setImageResource(R.drawable.baseline_play_arrow_24));
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.e("TTS", "Utterance error: " + utteranceId);
                        }
                    });

                } else {
                    Log.e("TTS", "TTS Initialization failed!");
                }
            }
        });

        playAudioIcon.setImageResource(R.drawable.baseline_play_arrow_24);

        playAudioIcon.setOnClickListener(v -> {
            if (isPlaying) {
                pauseTextToSpeech();
            } else {
                if (textChunks == null) {
                    prepareChunks(); // split text first time
                }
                speakNextChunk(); // resume from currentChunkIndex
                isPlaying = true;
                playAudioIcon.setImageResource(R.drawable.baseline_pause_24);
                playReplayIcon.setVisibility(View.VISIBLE); // Show replay icon
            }
        });

        playReplayIcon.setOnClickListener(v -> {
            currentChunkIndex = 0;
            isPlaying = true;
            speakNextChunk(); // start from beginning
            playAudioIcon.setImageResource(R.drawable.baseline_pause_24);
        });

        loadnews(newsId);
    }

    // ================= ZOOM =================
    private void applyZoom(boolean save) {
        textContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, baseTextSizePx * textZoom / 100f);
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

    // Screen meeda ekkada pinch chesina zoom avtundi (ScrollView lopala unna sare)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);

        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            pinching = false;
        }

        if (ev.getPointerCount() > 1 || scaleDetector.isInProgress() || pinching) {
            if (!pinching) {
                pinching = true;
                // child views (ScrollView) ki cancel pampi, scroll aapali
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
    private void loadnews(String newsId) {
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

        Call<NewsDetailsResponse> call = apiClient.getNewsDetails(newsId);
        call.enqueue(new Callback<NewsDetailsResponse>() {
            @Override
            public void onResponse(Call<NewsDetailsResponse> call, Response<NewsDetailsResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    NewsDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        NewsDetailsResponse.NewsItem item = data.result.get(0);

                        txtName.setText(item.newsTitle);

                        String fullImageUrl = data.imageUrl + item.image;
                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get().load(fullImageUrl).into(imageView);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textContent.setText(Html.fromHtml(item.newsDescription, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            textContent.setText(Html.fromHtml(item.newsDescription));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<NewsDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", String.valueOf(t.getMessage()));
            }
        });
    }

    // ================= TTS =================
    private void speakText() {
        String text = textContent.getText().toString();
        textChunks = text.split("(?<=[.!?])\\s+"); // Split by sentence end
        currentChunkIndex = 0;
        speakNextChunk();
        isPlaying = true;
        playAudioIcon.setImageResource(R.drawable.baseline_pause_24);
    }

    private void speakNextChunk() {
        if (textChunks != null && currentChunkIndex < textChunks.length) {
            Bundle params = new Bundle();
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "chunk_" + currentChunkIndex);
            textToSpeech.speak(textChunks[currentChunkIndex], TextToSpeech.QUEUE_FLUSH, params, "chunk_" + currentChunkIndex);
        }
    }

    private void prepareChunks() {
        String fullText = textContent.getText().toString();
        textChunks = fullText.split("(?<=[.!?])\\s+"); // Split by sentence
        currentChunkIndex = sharedPreferences.getInt(KEY_INDEX, 0);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void pauseTextToSpeech() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            isPlaying = false;

            // Advance the index to skip already half-spoken chunk
            currentChunkIndex++;

            sharedPreferences.edit().putInt(KEY_INDEX, currentChunkIndex).apply();

            playAudioIcon.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    private void stopTextToSpeech() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop(); // This resets the speaking
            isPlaying = false;
            playAudioIcon.setImageResource(R.drawable.baseline_play_arrow_24);
        }
    }

    // ================= SharedPreferences (zoom) =================
    private void saveToPreferences(String key, String value) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private String getFromPreferences(String key, String defaultValue) {
        SharedPreferences sp = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }
}
