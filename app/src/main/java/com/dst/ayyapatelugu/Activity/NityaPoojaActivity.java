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
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.NityaPoojaModel;
import com.dst.ayyapatelugu.Model.SignUpWithGmail;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.dst.ayyapatelugu.User.LoginActivity;
import com.dst.ayyapatelugu.User.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

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

    Toolbar toolbar;
    TextView txtName;
    ImageView imageView, imageAnadanam;
    TextView textAndanam;
    String activityId = "29";
    WebView webView;

    @SuppressLint({"MissingInflatedId","NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nitya_pooja);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(v -> {
            Intent intent = new Intent(NityaPoojaActivity.this, AnadanamActivity.class);
            startActivity(intent);
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v -> {
            Intent intent = new Intent(NityaPoojaActivity.this, AnadanamActivity.class);
            startActivity(intent);
        });

        txtName = findViewById(R.id.txt_name);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // if needed
        webView.setBackgroundColor(Color.TRANSPARENT);

        imageView = findViewById(R.id.image_view);

        String savedTitle = getFromPreferences("title", null);
        String savedDescription = getFromPreferences("description", null);
        String savedPhoto = getFromPreferences("photo", null);

        if (savedTitle != null && savedDescription != null && savedPhoto != null) {
            // Load data into UI
            txtName.setText(savedTitle);
            String url = "https://www.ayyappatelugu.com/assets/activity/" + savedPhoto;
            Picasso.get().load(url).into(imageView);
            String htmlContent = "<html><head>" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                    "<style>" +
                    "body { background-color: transparent; color: white; font-size: 14px; line-height: 1.6; }" +
                    "* { color: white !important; }" +
                    "</style>" +
                    "</head><body>" + savedDescription + "</body></html>";

            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } else {
            // Call API if no data is saved
            resultMethod(activityId);
        }

        // Retrieve activityId from SharedPreferences
        String savedActivityId = getFromPreferences("activityId", "29");
        resultMethod(savedActivityId);

        saveToPreferences("activityId", activityId);
    }
    @SuppressLint("NewApi")
    private void resultMethod(String activityId) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(), UnsafeTrustManager.createTrustAllTrustManager())
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
                if (response.isSuccessful()) {
                    NityaPoojaModel nityaPoojaModel = response.body();
                    if (nityaPoojaModel != null && nityaPoojaModel.getErrorCode().equals("200")) {
                        String title = nityaPoojaModel.getResult().get(0).getTitle();
                        String description = nityaPoojaModel.getResult().get(0).getDescription();
                        String photo = nityaPoojaModel.getResult().get(0).getImage();
                        String url = "https://www.ayyappatelugu.com/assets/activity/" + photo;

                        txtName.setText(title);
                        Picasso.get().load(url).into(imageView);
                        String htmlContent = "<html><head>" +
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                                "<style>" +
                                "body { background-color: transparent; color: white; font-size: 14px; line-height: 1.6; }" +
                                "* { color: white !important; }" +
                                "</style>" +
                                "</head><body>" + description + "</body></html>";

                        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

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
                Toast.makeText(NityaPoojaActivity.this, "Request Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Method to Save Data in SharedPreferences
    private void saveToPreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getFromPreferences(String key, String defaultValue) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}