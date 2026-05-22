package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.GuruSwamiDetailsResponse;
import com.dst.ayyapatelugu.Model.TempleDetailsResponse;
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

public class ViewTempleListDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView txtname, txttname, txtstarttime, txtendTime, txtlocation;
    ImageView imageView;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    String templeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_temple_list_details);

        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/

        templeId = getIntent().getStringExtra("templeId");
        if (templeId != null) {
            templeId = templeId.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + templeId);

        setSupportActionBar(toolbar);
        ;
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
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });
        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ViewTempleListDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewTempleListDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });



        imageView = findViewById(R.id.img);
        txtname = findViewById(R.id.txt_fname);
        txttname = findViewById(R.id.txt_city);
        txtstarttime = findViewById(R.id.txt_spec);
        txtendTime = findViewById(R.id.fname);
        txtlocation = findViewById(R.id.txt_location);


        Bundle bundle = getIntent().getExtras();

        String dname = bundle.getString("Name");
        String fname = bundle.getString("TName");
        String city = bundle.getString("Open");
        String Specilization = bundle.getString("Close");
        String village = bundle.getString("Location");
        String imagePath = bundle.getString("imagePath");

        /*txtname.setText(dname);
        txttname.setText(fname);
        txtstarttime.setText(city);
        txtendTime.setText(Specilization);
        txtlocation.setText(village);
        Picasso.get().load(imagePath).into(imageView);*/

        loadTempleDetails(templeId);


    }

    private void loadTempleDetails(String templeId) {
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
        Call<TempleDetailsResponse> call = apiClient.getTempleDetails(templeId);
        call.enqueue(new Callback<TempleDetailsResponse>() {
            @Override
            public void onResponse(Call<TempleDetailsResponse> call, Response<TempleDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    TempleDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        TempleDetailsResponse.TemplesItem item = data.result.get(0);

                        txtname.setText(item.getTempleName());
                        txttname.setText(item.getTempleNameTelugu());
                        txtstarttime.setText(item.getOpeningTime());
                        txtendTime.setText(item.getClosingTime());
                        txtlocation.setText(item.getLocation());


                        String imageUrl = "https://www.ayyappatelugu.com/public/assets/img/temple_images/" + item.image;

                        Picasso.get()
                                .load(imageUrl)

                                .into(imageView);


                    }
                }
            }

            @Override
            public void onFailure(Call<TempleDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}