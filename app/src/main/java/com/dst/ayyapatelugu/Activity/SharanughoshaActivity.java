package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.NityaPoojaModel;
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

    Toolbar toolbar;
    TextView txtName, txtsmallDiscription;
    WebView webView;
    ImageView imageView, imageAnadanam,imgNityaPooja;
    TextView textAndanam,txtNityaPooja;
    String activitiesId = "21";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharanughosha);


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
            Intent intent = new Intent(SharanughoshaActivity.this, AnadanamActivity.class);
            startActivity(intent);
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(v -> {
            Intent intent = new Intent(SharanughoshaActivity.this, AnadanamActivity.class);
            startActivity(intent);
        });

        imgNityaPooja = findViewById(R.id.img_nitya_pooja);
        imgNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SharanughoshaActivity.this, NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(SharanughoshaActivity.this, NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        txtName = findViewById(R.id.txt_sharanu_name);
        webView = findViewById(R.id.sharanu_webview);
        webView.getSettings().setJavaScriptEnabled(true); // if needed
        webView.setBackgroundColor(Color.TRANSPARENT);
        imageView = findViewById(R.id.sharanu_image_view);
        txtsmallDiscription  = findViewById(R.id.sharanu_small_discription);


        String savedTitle = getSharanuSharedPreferences("title", null);
        String savedDescription = getSharanuSharedPreferences("description", null);
        String savedPhoto = getSharanuSharedPreferences("photo", null);
        String savedSmallDiscription = getSharanuSharedPreferences("smallDescription", null);

        if (savedTitle != null && savedDescription != null && savedPhoto != null && savedSmallDiscription != null) {
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
            txtsmallDiscription.setText(savedSmallDiscription);

        } else {
            // Call API if no data is saved
            resultMethod(activitiesId);
        }

        String savedActivityId = getSharanuSharedPreferences("activitiesId", "21");
        resultMethod(savedActivityId);

        saveToSharanuSharedPreferences("activitiesId", activitiesId);

    }

    private void resultMethod(String activitiesId) {
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
        RequestBody activityIdPart = RequestBody.create(MediaType.parse("text/plain"), activitiesId);
        Call<SharanughosaModel> call = apiClient.PostActivity(activityIdPart);

        call.enqueue(new Callback<SharanughosaModel>() {
            @Override
            public void onResponse(Call<SharanughosaModel> call, Response<SharanughosaModel> response) {
                if (response.isSuccessful()){
                    SharanughosaModel sharanughosaModel= response.body();
                    if (sharanughosaModel != null && sharanughosaModel.getErrorCode().equals("200")) {
                        String title = sharanughosaModel.getResult().get(0).getTitle();
                        String description = sharanughosaModel.getResult().get(0).getDescription();
                        String smallDiscription = sharanughosaModel.getResult().get(0).getSmallDescription();
                        String photo = sharanughosaModel.getResult().get(0).getImage();
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
                        txtsmallDiscription.setText(smallDiscription);

                        saveToSharanuSharedPreferences("title", title);
                        saveToSharanuSharedPreferences("description", description);
                        saveToSharanuSharedPreferences("photo", photo);
                        saveToSharanuSharedPreferences("smallDescription", smallDiscription);
                    }
                } else {
                    Toast.makeText(SharanughoshaActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SharanughosaModel> call, Throwable t) {
                Toast.makeText(SharanughoshaActivity.this, "Request Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToSharanuSharedPreferences(String activityId, String activitiesId) {

        SharedPreferences  sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(activityId,activitiesId);
        editor.apply();

    }

    private String getSharanuSharedPreferences(String smallDescription, String val) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp" , Context.MODE_PRIVATE);
        return sharedPreferences.getString(smallDescription, val);
    }
}