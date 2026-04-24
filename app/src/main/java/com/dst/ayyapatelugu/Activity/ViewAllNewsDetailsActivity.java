package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.NewsDetailsResponse;
import com.dst.ayyapatelugu.Model.NewsList;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewAllNewsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView txtName;


    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;
    private TextToSpeech textToSpeech;
    private ImageView playAudioIcon;
    ImageView playReplayIcon;
    private TextView textContent,txtViewMore;

    private boolean isPlaying = false; // Track the playing state

    private String[] textChunks;
    private int currentChunkIndex = 0;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "tts_prefs";
    private static final String KEY_INDEX = "current_index";
    String newsId;
    String imagePath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_news_details);

        toolbar = findViewById(R.id.toolbar);

       /* toolbar.setLogo(R.drawable.user_profile_background);


        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        newsId = getIntent().getStringExtra("newsId");
        if (newsId != null) {
            newsId = newsId.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + newsId);


        txtViewMore = findViewById(R.id.txt_view_more);
        playAudioIcon = findViewById(R.id.play_audio_icon); // XML లో music player icon ID
        playReplayIcon = findViewById(R.id.play_replay_icon);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {

            nav.setTint(getResources().getColor(R.color.white));
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewAllNewsDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentChunkIndex = sharedPreferences.getInt(KEY_INDEX, 0); // Load saved index

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewAllNewsDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ViewAllNewsDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewAllNewsDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        txtName = findViewById(R.id.txt_name);
        textContent = findViewById(R.id.webview);
        imageView = findViewById(R.id.image_view);

        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("Name");
        imagePath = bundle.getString("imagePath");
        String discription = bundle.getString("Discription");

   /*     txtName.setText(name);*/

        txtViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnews=new Intent(ViewAllNewsDetailsActivity.this,ViewAllNewsListActivity.class);
                startActivity(intentnews);
            }
        });

       /* Picasso.get().load(imagePath).into(imageView);
        String htmlDescription = bundle.getString("Discription");

// Render HTML tags & remove special characters like &zwnj; &lsquo;
        if (htmlDescription != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textContent.setText(Html.fromHtml(htmlDescription, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textContent.setText(Html.fromHtml(htmlDescription));
            }
        }*/
       // textContent.setText(discription);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("te", "IN"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Telugu language not supported!");
                    }

                    // 👉 Listener for utterance completion
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

    private void loadnews(String newsId) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true) // Bypasses hostname verification
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);

        Call<NewsDetailsResponse> call = apiClient.getNewsDetails(newsId);
        call.enqueue(new Callback<NewsDetailsResponse>() {
            @Override
            public void onResponse(Call<NewsDetailsResponse> call, Response<NewsDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    NewsDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        NewsDetailsResponse.NewsItem item = data.result.get(0);

                        txtName.setText(item.newsTitle);


                        String fullImageUrl = data.imageUrl + item.image;

                        fullImageUrl = fullImageUrl.replace(
                                "https://www.ayyappatelugu.com/",
                                "https://www.ayyappatelugu.com/public/"
                        );

                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)

                                .into(imageView);

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
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

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
        //currentChunkIndex = sharedPreferences.getInt(KEY_INDEX, 0);
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

            // 🔥 Advance the index to skip already half-spoken chunk
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
}