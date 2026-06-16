package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.AyyappaPadayatraListAdapter;
import com.dst.ayyapatelugu.Adapter.ImageAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.ImagesModel;
import com.dst.ayyapatelugu.Model.ImagesResponse;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.Model.PadayatraResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ImagesListActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    String flyerName;
    String flyerDesignation;
    String flyerPic;


    String userId;

    private int currentIndex = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_list);

        toolbar = findViewById(R.id.toolbar);
        /*toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
        setSupportActionBar(toolbar);
        ;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }



        SharedPrefManager sp = SharedPrefManager.getInstance(this);

        flyerName = sp.getFlyerName();
        flyerDesignation = sp.getFlyerDesignation();
        flyerPic = sp.getFlyerPic();


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_images_list);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ImagesListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ImagesListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ImagesListActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ImagesListActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        Log.d("FCM_DEBUG", "Fetching Images API");
        fetchDataFromDataBase();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();

                int visibleItemCount = lm.getChildCount();
                int totalItemCount = lm.getItemCount();
                int firstVisibleItem = lm.findFirstVisibleItemPosition();

                if (!isLoading && hasMoreData) {

                    if ((visibleItemCount + firstVisibleItem) >= totalItemCount - 1) {

                        currentIndex++;   // 👈 0 → 1 → 2 → 3

                        fetchDataFromDataBase();
                    }
                }
            }
        });

    }



    private void fetchDataFromDataBase() {
        Log.d("INDEX", String.valueOf(currentIndex)); // 👈 HERE (BEST PLACE)

        Log.d("FCM_DEBUG", "Fetching Images API");
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
        Call<ImagesResponse> call = apiClient.getImages(currentIndex);
        call.enqueue(new Callback<ImagesResponse>() {
            @Override
            public void onResponse(Call<ImagesResponse> call, Response<ImagesResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ImagesResponse data = response.body();

                    String baseUrl = data.getImageUrl();
                    List<ImagesModel> list = data.getResult();
                    if (adapter == null) {
                        adapter = new ImageAdapter(ImagesListActivity.this, list, baseUrl, flyerName,
                                flyerDesignation,
                                flyerPic);
                        recyclerView.setAdapter(adapter);
                    }else {

                        adapter.addData(list);
                    }

                }
            }

            @Override
            public void onFailure(Call<ImagesResponse> call, Throwable t) {
                Toast.makeText(ImagesListActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001
                && resultCode == RESULT_OK
                && data != null) {

            flyerName = data.getStringExtra("flyer_name");
            flyerDesignation = data.getStringExtra("flyer_designation");
            flyerPic = data.getStringExtra("flyer_pic");

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}