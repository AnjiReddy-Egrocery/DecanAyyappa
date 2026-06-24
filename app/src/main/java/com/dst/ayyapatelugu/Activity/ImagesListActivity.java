package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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

    private int  currentIndex = 0;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private boolean userScrolled = false;

    private float startY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_list);

        Log.d("CHECK", "onCreate");

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

        LinearLayoutManager manager =
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false);

        recyclerView.setLayoutManager(manager);

        PagerSnapHelper pagerSnapHelper =
                new PagerSnapHelper();

        pagerSnapHelper.attachToRecyclerView(recyclerView);


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

        recyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(
                            @NonNull RecyclerView recyclerView,
                            int newState) {

                        super.onScrollStateChanged(
                                recyclerView,
                                newState);

                        if (newState ==
                                RecyclerView.SCROLL_STATE_IDLE) {

                            View snapView =
                                    pagerSnapHelper.findSnapView(
                                            manager);

                            if (snapView != null
                                    && adapter != null) {

                                int position =
                                        manager.getPosition(
                                                snapView);

                                Log.d("VERTICAL_PAGE",
                                        "position = " + position);

                                if (position >=
                                        adapter.getItemCount() - 1
                                        && !isLoading) {

                                    currentIndex++;

                                    Log.d("INDEX",
                                            "NEXT API = " + currentIndex);

                                    fetchDataFromDataBase();
                                }
                            }
                        }
                    }
                });
    }



    private void fetchDataFromDataBase() {
        Log.d("PAGINATION",
                "fetchDataFromDataBase Entered");

        if (isLoading) {
            Log.d("PAGINATION",
                    "RETURNED because isLoading true");
            return;
        }

        isLoading = true;

        Log.d("PAGINATION",
                "API CALL => " + currentIndex);
        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(
                HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client =
                new OkHttpClient.Builder()
                        .sslSocketFactory(
                                UnsafeTrustManager.createTrustAllSslSocketFactory(),
                                UnsafeTrustManager.createTrustAllTrustManager())
                        .hostnameVerifier((hostname, session) -> true)
                        .addInterceptor(loggingInterceptor)
                        .build();

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://www.ayyappatelugu.com/")
                        .addConverterFactory(
                                GsonConverterFactory.create())
                        .client(client)
                        .build();

        APiInterface apiClient =
                retrofit.create(APiInterface.class);

        Call<ImagesResponse> call =
                apiClient.getImages(currentIndex);

        call.enqueue(new Callback<ImagesResponse>() {
            @Override
            public void onResponse(
                    Call<ImagesResponse> call,
                    Response<ImagesResponse> response) {

                Log.d("CHECK",
                        "RESPONSE RECEIVED currentIndex="
                                + currentIndex);

                isLoading = false;

                if (response.isSuccessful()
                        && response.body() != null) {

                    ImagesResponse data =
                            response.body();

                    List<ImagesModel> list =
                            data.getResult();

                    if (list == null || list.isEmpty()) {

                        hasMoreData = false;

                        Log.d("PAGINATION",
                                "NO MORE DATA");

                        return;
                    }

                    Log.d("PAGINATION",
                            "RECEIVED SIZE = "
                                    + list.size());

                    if (adapter == null) {

                        Log.d("CHECK",
                                "adapter NULL");

                        adapter = new ImageAdapter(
                                ImagesListActivity.this,
                                new ArrayList<>(list),
                                data.getImageUrl(),
                                flyerName,
                                flyerDesignation,
                                flyerPic
                        );

                        recyclerView.setAdapter(adapter);

                    } else {

                        Log.d("CHECK",
                                "adapter NOT NULL");

                        adapter.addData(list);
                    }



                }
            }

            @Override
            public void onFailure(
                    Call<ImagesResponse> call,
                    Throwable t) {

                isLoading = false;

                Log.e("API_DEBUG",
                        t.getMessage());

                Toast.makeText(
                        ImagesListActivity.this,
                        t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
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