package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.BajanaMandaliDetailsResponse;
import com.dst.ayyapatelugu.Model.GuruSwamiDetailsResponse;
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

public class AyyapaMandaliDetailsActivity extends AppCompatActivity {
    Toolbar toolbar;

    TextView txtname, txtGuruName, txtVillage, txtNumber, txtEmail, txtDiscription;
    ImageView imageView;

    TabLayout tabLayout;
    ViewPager viewPager;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    String bajanamandaliid;
    String imagePath;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyapa_mandali_details);


        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        bajanamandaliid = getIntent().getStringExtra("bajanamandaliId");
        if (bajanamandaliid != null) {
            bajanamandaliid = bajanamandaliid.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + bajanamandaliid);
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
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyapaMandaliDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });


        txtname = findViewById(R.id.txt_name);
        txtGuruName = findViewById(R.id.txt_guru_name);
        txtVillage = findViewById(R.id.txt_village);
        txtNumber = findViewById(R.id.txt_number);
        txtEmail = findViewById(R.id.txt_email);
        imageView = findViewById(R.id.image_view);
        txtDiscription = findViewById(R.id.txt_discription);
        Bundle bundle = getIntent().getExtras();
        String name = bundle.getString("ItemName");
        String guruName = bundle.getString("ItemGuruName");
        String village = bundle.getString("ItemCity");
        String number = bundle.getString("ItemNumber");
        String email = bundle.getString("ItemEmail");
        String discription = bundle.getString("Discription");
       /* txtname.setText(name);
        txtGuruName.setText(guruName);
        txtVillage.setText(village);
        txtNumber.setText(number);
        txtEmail.setText(email);

        Spanned spanned=Html.fromHtml(discription);
        String plainText=spanned.toString();
        txtDiscription.setText(plainText);
        Linkify.addLinks(txtDiscription,Linkify.WEB_URLS);

        String image_path = bundle.getString("imagePath");
        Picasso.get().load(image_path).into(imageView);*/

        loadBajamandaliDetails(bajanamandaliid);

    }

    private void loadBajamandaliDetails(String bajanamandaliid) {
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
        Call<BajanaMandaliDetailsResponse> call = apiClient.getBajanamandaliDetails(bajanamandaliid);
        call.enqueue(new Callback<BajanaMandaliDetailsResponse>() {
            @Override
            public void onResponse(Call<BajanaMandaliDetailsResponse> call, Response<BajanaMandaliDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "200".equals(response.body().errorCode)) {

                    BajanaMandaliDetailsResponse data = response.body();

                    if (data.result != null && !data.result.isEmpty()) {

                        BajanaMandaliDetailsResponse.BajanaMandaliItem item = data.result.get(0);

                        txtname.setText(item.getBajanamandaliName());
                        txtGuruName.setText(item.getNameOfGuru());
                        txtVillage.setText(item.getBajanamandaliCity());
                        txtNumber.setText(item.getBajanamandaliMobile());
                        txtEmail.setText(item.getBajanamandaliEmail());

                        Spanned spanned=Html.fromHtml(item.getBajanamandaliDescription());
                        String plainText=spanned.toString();
                        txtDiscription.setText(plainText);
                        Linkify.addLinks(txtDiscription,Linkify.WEB_URLS);


                        String fullImageUrl = data.imageUrl + item.profilePic;



                        Log.d("IMAGE_DEBUG", "Final URL: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)

                                .into(imageView);


                    }
                }
            }

            @Override
            public void onFailure(Call<BajanaMandaliDetailsResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }
}