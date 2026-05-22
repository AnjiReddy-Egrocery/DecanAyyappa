package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.ImageAdapter;
import com.dst.ayyapatelugu.Adapter.VideoAdapter;
import com.dst.ayyapatelugu.Model.ImagesModel;
import com.dst.ayyapatelugu.Model.ImagesResponse;
import com.dst.ayyapatelugu.Model.VideoModel;
import com.dst.ayyapatelugu.Model.VideoResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostVideosActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    VideoAdapter adapter;
    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_videos);

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

        Log.d("FCM_DEBUG", "PostVideosActivity Opened");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_video_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

// SNAP (important for reels effect)
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    playVisibleVideo();
                }
            }
        });


        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostVideosActivity.this, AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostVideosActivity.this, AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PostVideosActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostVideosActivity.this, NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        fetchDataFromDataBase();
       /* SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {

            swipeRefresh.setRefreshing(false);
        });*/

    }

    private void fetchDataFromDataBase() {
        Log.d("FCM_DEBUG", "Fetching Videos API");
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
        Call<VideoResponse> call = apiClient.getVideos();
        call.enqueue(new Callback<VideoResponse>() {
            @Override
            public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    VideoResponse data = response.body();

                    String baseUrl = data.getVideoUrl();
                    List<VideoModel> list = data.getResult();

                    adapter = new VideoAdapter(PostVideosActivity.this, list, baseUrl);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<VideoResponse> call, Throwable t) {
                Toast.makeText(PostVideosActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void playVisibleVideo() {

        LinearLayoutManager layoutManager =
                (LinearLayoutManager)
                        recyclerView.getLayoutManager();

        int visiblePosition =
                layoutManager
                        .findFirstCompletelyVisibleItemPosition();

        if (visiblePosition ==
                RecyclerView.NO_POSITION) {

            visiblePosition =
                    layoutManager
                            .findFirstVisibleItemPosition();
        }

        for (int i = 0;
             i < recyclerView.getChildCount();
             i++) {

            View child =
                    recyclerView.getChildAt(i);

            RecyclerView.ViewHolder holder =
                    recyclerView.getChildViewHolder(child);

            if (holder instanceof
                    VideoAdapter.MyViewHolder) {

                VideoAdapter.MyViewHolder vh =
                        (VideoAdapter.MyViewHolder) holder;

                int position =
                        holder.getAdapterPosition();

                if (position == visiblePosition) {

                    vh.player.play();

                } else {

                    vh.player.pause();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pauseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView.postDelayed(() -> {
            playVisibleVideo();
        }, 300);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        pauseAllVideos();
    }

    private void pauseAllVideos() {

        for (int i = 0;
             i < recyclerView.getChildCount();
             i++) {

            RecyclerView.ViewHolder holder =
                    recyclerView.getChildViewHolder(
                            recyclerView.getChildAt(i)
                    );

            if (holder instanceof
                    VideoAdapter.MyViewHolder) {

                ((VideoAdapter.MyViewHolder) holder)
                        .player
                        .pause();
            }
        }
    }

}