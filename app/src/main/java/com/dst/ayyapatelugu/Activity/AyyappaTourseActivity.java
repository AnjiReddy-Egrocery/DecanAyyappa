package com.dst.ayyapatelugu.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.BajanaMandaliDetailsResponse;
import com.dst.ayyapatelugu.Model.TourseDetailsResponse;
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

public class AyyappaTourseActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView txtName, txtdays, txtdetails, txtamount;
    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    String tourpackageId;
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyappa_tourse);

        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        tourpackageId = getIntent().getStringExtra("tourpackageId");
        if (tourpackageId != null) {
            tourpackageId = tourpackageId.replace("'", "").trim(); // 🔥 remove quotes
        }
        Log.d("FCM_DEBUG", "Final: " + tourpackageId);
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
                Intent intent=new Intent(AyyappaTourseActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaTourseActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyappaTourseActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaTourseActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtName = findViewById(R.id.txt);
        txtdays = findViewById(R.id.txt_add);
        txtdetails = findViewById(R.id.txt_details);
        txtamount = findViewById(R.id.txt_amount);
        imageView = findViewById(R.id.img);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("Name");
        String days = bundle.getString("Days");
        String details = bundle.getString("Details");
        String amount = bundle.getString("Amount");
        String imagePath = bundle.getString("imagePath");

       /* txtName.setText(name);
        txtdays.setText(days);
        txtdetails.setText(details);
        txtamount.setText(amount);
        Picasso.get().load(imagePath).into(imageView);*/

        loadTourseList(tourpackageId);
    }

    private void loadTourseList(String tourpackageId) {
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
        Call<TourseDetailsResponse> call = apiClient.getTourseDetails(tourpackageId);
        call.enqueue(new Callback<TourseDetailsResponse>() {
            @Override
            public void onResponse(Call<TourseDetailsResponse> call, Response<TourseDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    TourseDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        TourseDetailsResponse.TourseItem item = data.result.get(0);

                        txtName.setText(item.getNameOfPlace());
                        txtdays.setText(item.getDays());
                        txtdetails.setText(item.getDevotees());
                        txtamount.setText(item.getAmount());


                        String fullImageUrl = data.imageUrl + item.image;



                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)

                                .into(imageView);


                    }
                }
            }

            @Override
            public void onFailure(Call<TourseDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}