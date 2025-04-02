package com.dst.ayyapatelugu.Activity;

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
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.AyyappaTourseDetailsAdapter;

import com.dst.ayyapatelugu.Adapter.GuruSwamiListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.GuruSwamiModelList;
import com.dst.ayyapatelugu.Model.YatraList;
import com.dst.ayyapatelugu.Model.YatraListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.google.gson.Gson;
import com.ibm.icu.text.Transliterator;


import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AyyappaTourseDetailsACtivity extends AppCompatActivity {

    Toolbar toolbar;

    List<YatraListModel> yatraListModels, filteredList;

    RecyclerView recyclerView;

    AyyappaTourseDetailsAdapter ayyappaTourseDetailsAdapter;

    SearchView searchView;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyappa_tourse_details);

        toolbar = findViewById(R.id.toolbar);
        /*toolbar.setLogo(R.drawable.user_profile_background);
        toolbar.setTitle("www.ayyappatelugu.com");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));*/
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
                Intent intent=new Intent(AyyappaTourseDetailsACtivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaTourseDetailsACtivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyappaTourseDetailsACtivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaTourseDetailsACtivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });
        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search by Name");
        searchView.setIconifiedByDefault(false); // Keep it expanded
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setClickable(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHint("Search by Name");
        searchEditText.setHintTextColor(Color.GRAY); // Change hint color if needed

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false); // Open search input on click
            }
        });

        recyclerView = findViewById(R.id.frecycler_tourse_list);
        yatraListModels=new ArrayList<>();
        filteredList = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        YatraList savedYatraList = SharedPreferencesManager.getYatraList(this);
        if (savedYatraList != null) {
            yatraListModels = new ArrayList<>(Arrays.asList(savedYatraList.getResult()));
            if (ayyappaTourseDetailsAdapter == null) {
                ayyappaTourseDetailsAdapter = new AyyappaTourseDetailsAdapter(AyyappaTourseDetailsACtivity.this, filteredList);
                recyclerView.setAdapter(ayyappaTourseDetailsAdapter);
            } else {
                ayyappaTourseDetailsAdapter.updateList(filteredList);
            }

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
        fetchDataFromApi();
    }

    private void filterResults(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(yatraListModels);
            if (ayyappaTourseDetailsAdapter == null) {
                ayyappaTourseDetailsAdapter = new AyyappaTourseDetailsAdapter(AyyappaTourseDetailsACtivity.this, filteredList);
                recyclerView.setAdapter(ayyappaTourseDetailsAdapter);
            } else {
                ayyappaTourseDetailsAdapter.updateList(filteredList);
            }
            return;
        }

        String normalizedQuery = normalize(query);

        // **Auto Transliterate Based on Input Language**
        String teluguQuery = transliterateToTelugu(normalizedQuery);
        String englishQuery = transliterateToEnglish(normalizedQuery);

        for (YatraListModel item : yatraListModels) {
            String normalizedName = normalize(item.getNameOfPlace());

            if (normalizedName.contains(normalizedQuery) ||

                    normalizedName.contains(teluguQuery) ||

                    normalizedName.contains(englishQuery)) {

                filteredList.add(item);
            }
        }

        if (ayyappaTourseDetailsAdapter == null) {
            ayyappaTourseDetailsAdapter = new AyyappaTourseDetailsAdapter(AyyappaTourseDetailsACtivity.this, filteredList);
            recyclerView.setAdapter(ayyappaTourseDetailsAdapter);
        } else {
            ayyappaTourseDetailsAdapter.updateList(filteredList);
        }
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
                .toLowerCase(Locale.getDefault());
    }
    private String transliterateToTelugu(String input) {
        Transliterator transliterator = Transliterator.getInstance("Latin-Telugu");
        return transliterator.transliterate(input);
    }

    private String transliterateToEnglish(String input) {
        Transliterator transliterator = Transliterator.getInstance("Telugu-Latin");
        return transliterator.transliterate(input);
    }

    private void fetchDataFromApi() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        APiInterface apiClient = retrofit.create(APiInterface.class);
        Call<YatraList> call = apiClient.getYatraList();

        call.enqueue(new Callback<YatraList>() {
            @Override
            public void onResponse(Call<YatraList> call, Response<YatraList> response) {
                YatraList yatraList = response.body();

                yatraListModels.clear();
                yatraListModels = new ArrayList<>(Arrays.asList(yatraList.getResult()));

                filteredList.clear();
                filteredList.addAll(yatraListModels);

                // Save YatraList object to SharedPreferences
                 SharedPreferencesManager.saveYatraList(AyyappaTourseDetailsACtivity.this, yatraList);

                ayyappaTourseDetailsAdapter = new AyyappaTourseDetailsAdapter(AyyappaTourseDetailsACtivity.this, yatraListModels);
                recyclerView.setAdapter(ayyappaTourseDetailsAdapter);
            }

            @Override
            public void onFailure(Call<YatraList> call, Throwable t) {
                if (yatraListModels != null && !yatraListModels.isEmpty()) {
                    Toast.makeText(AyyappaTourseDetailsACtivity.this,"Failed to fetch new data. Using cached data.",Toast.LENGTH_SHORT).show();
                } else {
                    // Handle failure when no cached data is available
                    Toast.makeText(AyyappaTourseDetailsACtivity.this,"Failed to fetch data. Please check your network connection.",Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AyyappaTourseDetailsACtivity.this);
        View dialogView = LayoutInflater.from(AyyappaTourseDetailsACtivity.this).inflate(R.layout.dialog_tourse_information, null);
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