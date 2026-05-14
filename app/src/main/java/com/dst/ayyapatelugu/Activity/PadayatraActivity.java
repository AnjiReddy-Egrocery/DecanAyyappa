package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dst.ayyapatelugu.Adapter.AyyappaBooksListAdapter;
import com.dst.ayyapatelugu.Adapter.AyyappaPadayatraListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.BooksListModel;
import com.dst.ayyapatelugu.Model.BooksModelResult;
import com.dst.ayyapatelugu.Model.PadayatraBrundam;
import com.dst.ayyapatelugu.Model.PadayatraResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PadayatraActivity extends AppCompatActivity {

    Toolbar toolbar;

    List<PadayatraBrundam> padayatraList;

    RecyclerView recyclerView;

    SearchView searchView;


    AyyappaPadayatraListAdapter ayyappaPadayatraListAdapter;
    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padayatra);

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.recycler_padayatra);
        padayatraList = new ArrayList<>();

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PadayatraActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PadayatraActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(PadayatraActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PadayatraActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        fetchDataFromSharedPreferences();

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search by title");
        searchView.setIconifiedByDefault(false); // Keep it expanded
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setClickable(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHint("Search by title");
        searchEditText.setHintTextColor(Color.GRAY); // Change hint color if needed

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Open search input on click
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

              /*  if (ayyappaBooksListAdapter != null) {
                    ayyappaBooksListAdapter.getFilter().filter(query);
                }*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (ayyappaPadayatraListAdapter != null) {
                    ayyappaPadayatraListAdapter.getFilter().filter(newText);
                }
                return true;
            }
        });

        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            fetchDataFromDataBase();
            swipeRefresh.setRefreshing(false);
        });

    }




    private void fetchDataFromSharedPreferences() {
        List<PadayatraBrundam> storedPadayatraList = SharedPreferencesManager.getPadayatraList(PadayatraActivity.this);

        if (storedPadayatraList != null && !storedPadayatraList.isEmpty()) {
            // Data exists in SharedPreferences, update RecyclerView
            updateRecyclerView(storedPadayatraList);
        } else {
            // Data doesn't exist in SharedPreferences, fetch from the network
            fetchDataFromDataBase();
        }
    }

    private void fetchDataFromDataBase() {
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
        Call<PadayatraResponse> call = apiClient.getPadayatraList();
        call.enqueue(new Callback<PadayatraResponse>() {
            @Override
            public void onResponse(Call<PadayatraResponse> call, Response<PadayatraResponse> response) {
                PadayatraResponse  padayatraResponse = response.body();
                padayatraList.clear();
                padayatraList = padayatraResponse.getResult();
                SharedPreferencesManager.savepadayatraList(PadayatraActivity.this, padayatraList);

                updateRecyclerView(padayatraList);
            }

            @Override
            public void onFailure(Call<PadayatraResponse> call, Throwable t) {
                padayatraList = SharedPreferencesManager.getPadayatraList(PadayatraActivity.this);
                if (padayatraList != null && !padayatraList.isEmpty()) {
                    // Update the RecyclerView
                    updateRecyclerView(padayatraList);
                }
            }
        });
    }

    private void updateRecyclerView(List<PadayatraBrundam> padayatraList) {
        if (ayyappaPadayatraListAdapter == null) {
            ayyappaPadayatraListAdapter = new AyyappaPadayatraListAdapter(PadayatraActivity.this, padayatraList);
            recyclerView.setAdapter(ayyappaPadayatraListAdapter);
        } else {
            ayyappaPadayatraListAdapter.setData(padayatraList);
        }
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
    @SuppressLint("MissingInflatedId")
    private void informationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PadayatraActivity.this);
        View dialogView = LayoutInflater.from(PadayatraActivity.this).inflate(R.layout.dialog_padayatra_information, null);
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
