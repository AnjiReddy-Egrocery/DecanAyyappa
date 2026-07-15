package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Model.BlogDetail;
import com.dst.ayyapatelugu.Model.BlogResponse;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.Model.PadayatraResponse;
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

public class BlogDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;

    TextView txtName;


    ImageView imageView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "tts_prefs";
    private static final String KEY_INDEX = "current_index";
    String blogId;
    private TextView textContent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);

        toolbar = findViewById(R.id.toolbar);

       /* toolbar.setLogo(R.drawable.user_profile_background);


        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        blogId = getIntent().getStringExtra("BlogId");
        if (blogId != null) {
            blogId = blogId.replace("'", "").trim(); // 🔥 remove quotes
        }

        Log.d("FCM_DEBUG", "Final: " + blogId);




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
                Intent intent=new Intent(BlogDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });



        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BlogDetailsActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(BlogDetailsActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BlogDetailsActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        txtName = findViewById(R.id.txt_name);
        textContent = findViewById(R.id.webview);
        imageView = findViewById(R.id.image_view);


        loadBlog(blogId);
    }

    private void loadBlog(String blogId) {
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

        Call<BlogResponse> call = apiClient.getBlogDetails(blogId);
        call.enqueue(new Callback<BlogResponse>() {
            @Override
            public void onResponse(Call<BlogResponse> call, Response<BlogResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    BlogResponse res = response.body();

                    if ("200".equals(res.getErrorCode()) &&
                            res.getResult() != null &&
                            !res.getResult().isEmpty()) {

                        BlogDetail item = res.getResult().get(0);

                        // ✅ Name
                        txtName.setText(item.getTitle());

                        // ✅ Image
                        String fullImageUrl = res.getImageUrl() + item.getImage();




                        Log.d("FCM_DEBUG", "Final: " + fullImageUrl);

                        Picasso.get()
                                .load(fullImageUrl)
                                .into(imageView);

                        // ✅ Description (HTML)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            textContent.setText(Html.fromHtml(
                                    item.getDescription(),
                                    Html.FROM_HTML_MODE_LEGACY
                            ));
                        } else {
                            textContent.setText(Html.fromHtml(item.getDescription()));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BlogResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }



}
