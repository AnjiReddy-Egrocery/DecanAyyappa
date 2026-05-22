package com.dst.ayyapatelugu.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.ActivitiesDetailsResponse;
import com.dst.ayyapatelugu.Model.BajanaMandaliDetailsResponse;
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

    Toolbar toolbar;

    TextView txtName;
    WebView webView;

    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    String activitiesId;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyapa_karma_details);

        toolbar = findViewById(R.id.toolbar);

       /* toolbar.setLogo(R.drawable.user_profile_background);


        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/

        activitiesId = getIntent().getStringExtra("activitiesId");
        if (activitiesId != null) {
            activitiesId = activitiesId.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + activitiesId);

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
                Intent intent=new Intent(AyyapaKarmaDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaKarmaDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyapaKarmaDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaKarmaDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtName = findViewById(R.id.txt_name);
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // if needed
        webView.setBackgroundColor(Color.TRANSPARENT);
        imageView = findViewById(R.id.image_view);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("Name");
        String discription = bundle.getString("Discription");
        String imagePath = bundle.getString("ImagePath");

       /* txtName.setText(name);
        Picasso.get().load(imagePath).into(imageView);
        String htmlContent = "<html><head>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                "<style>" +
                "body { background-color: transparent; color: white; font-size: 14px; line-height: 1.6; }" +
                "* { color: white !important; }" +
                "</style>" +
                "</head><body>" + discription + "</body></html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);*/
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.loadDataWithBaseURL(null, discription, "text/html", "utf-8", null);
            ;

            //webView.loadData(discription, "text/html", "UTF-8");

        }*/

        loadPoojakaryakaramamDetails(activitiesId);

    }

    private void loadPoojakaryakaramamDetails(String activitiesId) {
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
        Call<ActivitiesDetailsResponse> call = apiClient.getActivitiesDetails(activitiesId);
        call.enqueue(new Callback<ActivitiesDetailsResponse>() {
            @Override
            public void onResponse(Call<ActivitiesDetailsResponse> call, Response<ActivitiesDetailsResponse> response) {
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

                        fullImageUrl = fullImageUrl.replace(
                                "https://www.ayyappatelugu.com/",
                                "https://www.ayyappatelugu.com/public/"
                        );

                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)

                                .into(imageView);


                    }
                }
            }

            @Override
            public void onFailure(Call<ActivitiesDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });

    }
}