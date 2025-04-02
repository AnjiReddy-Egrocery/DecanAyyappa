package com.dst.ayyapatelugu.Activity;

import static com.dst.ayyapatelugu.Services.UnsafeTrustManager.createTrustAllSslSocketFactory;
import static com.dst.ayyapatelugu.Services.UnsafeTrustManager.createTrustAllTrustManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.dst.ayyapatelugu.Adapter.GuruSwamiListAdapter;

import com.dst.ayyapatelugu.Adapter.TemplesAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesHelper;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.GuruSwamiList;
import com.dst.ayyapatelugu.Model.GuruSwamiModelList;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.ApiClient;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.ibm.icu.text.Transliterator;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;


import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GuruSwamiListActivity extends AppCompatActivity {

    Toolbar toolbar;

    List<GuruSwamiModelList> guruswamiList, filteredList;

    RecyclerView recyclerView;

    GuruSwamiListAdapter guruSwamiListAdapter;

    SharedPreferencesHelper sharedPreferencesHelper;

    SearchView searchView;

    private Retrofit retrofit;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    private static final Transliterator latinToTelugu = Transliterator.getInstance("Latin-Telugu");

    private ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru_swami_list);

        toolbar = findViewById(R.id.toolbar);
       /* toolbar.setLogo(R.drawable.user_profile_background);
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
                Intent intent=new Intent(GuruSwamiListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuruSwamiListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(GuruSwamiListActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(GuruSwamiListActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        searchView = findViewById(R.id.searchView);
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search by name, city, or mobile number");
        searchView.setIconifiedByDefault(false); // Keep it expanded
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setClickable(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHint("Search by name, city, or mobile number");
        searchEditText.setHintTextColor(Color.GRAY); // Change hint color if needed

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Open search input on click
            }
        });
        sharedPreferencesHelper= new SharedPreferencesHelper(this);

        recyclerView = findViewById(R.id.recycler_guruswami_list);
        guruswamiList = new ArrayList<>();
        filteredList = new ArrayList<>();
        guruSwamiListAdapter = new GuruSwamiListAdapter(this, filteredList);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setAdapter(guruSwamiListAdapter);



        List<GuruSwamiModelList> cachedList = sharedPreferencesHelper.getGuruSwamiList();
        if (cachedList != null && !cachedList.isEmpty()) {
            // Step 2: Load cached data into the RecyclerView
            guruswamiList .addAll(cachedList);
            filteredList .addAll(cachedList);
            updateRecyclerView();

        }

        if (isNetworkAvailable()) {
            fetchFreshDataInBackground();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //filterResults(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResults(newText);
                return false;
            }
        });
    }

    private void fetchFreshDataInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                fetchGuruSwamiList();
            }
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void filterResults(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(guruswamiList);
            Log.e("FETCH_GURUSWAMI", "Showing all items, size: " + filteredList.size());
            updateRecyclerView();
            return;
        }

        String normalizedQuery = normalize(query);
        String teluguQuery = transliterateToTelugu(normalizedQuery);
        String englishQuery = transliterateToEnglish(normalizedQuery);

        for (GuruSwamiModelList item : guruswamiList) {
            String normalizedName = normalize(item.getGuruswamiName());
            String normalizedCity = normalize(item.getCityName());
            String normalizedMobile = normalize(item.getMobileNo());

            Log.d("FILTERING", "Checking: " + normalizedName + ", " + normalizedCity);

            if (normalizedName.contains(normalizedQuery) ||
                    normalizedCity.contains(normalizedQuery) ||
                    normalizedMobile.contains(normalizedQuery) ||
                    normalizedName.contains(teluguQuery) ||
                    normalizedCity.contains(teluguQuery) ||
                    normalizedName.contains(englishQuery) ||
                    normalizedCity.contains(englishQuery)) {

                filteredList.add(item);
            }
        }
        updateRecyclerView();
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
                .toLowerCase(Locale.getDefault());
    }


    private void updateRecyclerView() {
      if (guruSwamiListAdapter == null) {
            guruSwamiListAdapter = new GuruSwamiListAdapter(GuruSwamiListActivity.this, filteredList);
            recyclerView.setAdapter(guruSwamiListAdapter);
        } else {
            guruSwamiListAdapter.updateList(filteredList);
        }
    }

    private String transliterateToTelugu(String input) {
        Transliterator transliterator = Transliterator.getInstance("Latin-Telugu");
        return transliterator.transliterate(input);
    }

    private String transliterateToEnglish(String input) {
        Transliterator transliterator = Transliterator.getInstance("Telugu-Latin");
        return transliterator.transliterate(input);
    }

    private void fetchGuruSwamiList() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Change to Level.BASIC for less detail

        // Create OkHttpClient without SSL bypassing
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor) // Add the logging interceptor
                .build();

        // Initialize Retrofit with the OkHttpClient
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Ensure this is your correct base URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<GuruSwamiList> call = apiClient.getGuruSwamiList();

        call.enqueue(new Callback<GuruSwamiList>() {
            @Override
            public void onResponse(Call<GuruSwamiList> call, Response<GuruSwamiList> response) {
                if (response.isSuccessful() ) {

                    GuruSwamiList guruSwamiList = response.body();


                    guruswamiList.clear();
                    guruswamiList.addAll(Arrays.asList(guruSwamiList.getResult()));
                    filteredList.clear();
                    filteredList.addAll(guruswamiList);
                    //updateRecyclerView();
                    // Save to cache
                    sharedPreferencesHelper.saveGuruSwamiList(guruswamiList);

                    updateRecyclerView(); // Update UI
                } else {

                }
            }

            @Override
            public void onFailure(Call<GuruSwamiList> call, Throwable t) {
                // Try to load cached data if API fails
                List<GuruSwamiModelList> cachedList = sharedPreferencesHelper.getGuruSwamiList();
                if (cachedList != null && !cachedList.isEmpty()) {
                    guruswamiList.addAll(cachedList);
                    updateRecyclerView();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId(); // Get the clicked menu item ID

        if (id == R.id.popup_info) {
            informationDialog();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void informationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(GuruSwamiListActivity.this);
            View dialogView = LayoutInflater.from(GuruSwamiListActivity.this).inflate(R.layout.dialog_guruswami_information, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            ImageButton closeButton = dialogView.findViewById(R.id.btn_close);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

}