package com.dst.ayyapatelugu.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Adapter.GuruSwamiDetailsAdapter;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.GuruSwamiDetailsResponse;
import com.dst.ayyapatelugu.Model.NewsDetailsResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GuruSwamiDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;

    TextView txtName, txtTemple, txtcity;
    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;
    String guruswamiId;
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_swami_details);

        toolbar = findViewById(R.id.toolbar);
    /*    toolbar.setLogo(R.drawable.user_profile_background);

        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/

        guruswamiId = getIntent().getStringExtra("guruswamiId");
        if (guruswamiId != null) {
            guruswamiId = guruswamiId.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + guruswamiId);
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

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("వివరణ"));
        tabLayout.addTab(tabLayout.newTab().setText("స్వీయ చరిత్ర"));
        tabLayout.addTab(tabLayout.newTab().setText("సందేశం"));
        final GuruSwamiDetailsAdapter adapter = new GuruSwamiDetailsAdapter(GuruSwamiDetailsActivity.this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuruSwamiDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuruSwamiDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GuruSwamiDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuruSwamiDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtName = findViewById(R.id.txt_name);
       // txtNumber = findViewById(R.id.txt_number);
        txtTemple = findViewById(R.id.txt_temple);
        txtcity = findViewById(R.id.txt_city);
        imageView = findViewById(R.id.image_view);

        Bundle bundle = getIntent().getExtras();

        String name = bundle.getString("Name");
        String number = bundle.getString("Number");
        String temple = bundle.getString("Temple");
        String city = bundle.getString("City");
        String imagePath = bundle.getString("Image");

        /*txtName.setText(name);
        txtNumber.setText(number);
        txtTemple.setText(temple);
        txtcity.setText(city);
        Picasso.get().load(imagePath).into(imageView);*/

        loadGuruSwamiDetails(guruswamiId);


    }

    private void loadGuruSwamiDetails(String guruswamiId) {
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
        Call<GuruSwamiDetailsResponse> call = apiClient.getGuruSwamiDetails(guruswamiId);
        call.enqueue(new Callback<GuruSwamiDetailsResponse>() {
            @Override
            public void onResponse(Call<GuruSwamiDetailsResponse> call, Response<GuruSwamiDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    GuruSwamiDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        GuruSwamiDetailsResponse.GuruSwamiItem item = data.result.get(0);

                        txtName.setText(item.guruswamiName);
                        txtTemple.setText(item.getTempleName());
                        txtcity.setText(item.getCityName());


                        String fullImageUrl = data.imageUrl + item.profilePic;



                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)

                                .into(imageView);


                    }
                }
            }

            @Override
            public void onFailure(Call<GuruSwamiDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}