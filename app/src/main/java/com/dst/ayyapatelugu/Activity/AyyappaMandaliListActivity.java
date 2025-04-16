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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.dst.ayyapatelugu.Adapter.AyyapamandaliAdapter;
import com.dst.ayyapatelugu.Adapter.GuruSwamiListAdapter;
import com.dst.ayyapatelugu.HomeActivity;
import com.dst.ayyapatelugu.Model.BajanaManadaliListModel;
import com.dst.ayyapatelugu.Model.BajanaMandaliList;
import com.dst.ayyapatelugu.Model.GuruSwamiModelList;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.text.Transliterator;


import java.lang.reflect.Type;
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

public class AyyappaMandaliListActivity extends AppCompatActivity {
    Toolbar toolbar;

    List<BajanaManadaliListModel> bajanaManadaliList, filteredList;

    RecyclerView recyclerView;
    AyyapamandaliAdapter ayyapamandaliAdapter;

    SearchView searchView;
    private Retrofit retrofit;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayyappa_mandali_list);

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

        imageAnadanam=findViewById(R.id.layout_image_anadanam);
        imageAnadanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaMandaliListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaMandaliListActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(AyyappaMandaliListActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AyyappaMandaliListActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

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



        recyclerView = findViewById(R.id.recycler_manadali);
        bajanaManadaliList=new ArrayList<>();
        filteredList = new ArrayList<>();
        LinearLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        String cachedData = getSharedPreferences("AyyappaMandaliPrefs", Context.MODE_PRIVATE)
                .getString("bajana_mandali_list", null);


        if (cachedData != null && !cachedData.isEmpty()) {
            // Use cached data
            bajanaManadaliList = parseCachedData(cachedData);
            filteredList = parseCachedData(cachedData);

            updateRecyclerview();
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
                fetchMandaliList();
            }
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    private void updateRecyclerview() {
        if (ayyapamandaliAdapter == null) {
            ayyapamandaliAdapter = new AyyapamandaliAdapter(AyyappaMandaliListActivity.this, filteredList);
            recyclerView.setAdapter(ayyapamandaliAdapter);
        } else {
            ayyapamandaliAdapter.updateList(filteredList);
        }
    }


    private void filterResults(String query) {
        filteredList.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredList.addAll(bajanaManadaliList);
            updateRecyclerview();
            return;
        }

        String normalizedQuery = normalize(query);
        String teluguQuery = transliterateToTelugu(normalizedQuery);
        String englishQuery = transliterateToEnglish(normalizedQuery);

        // Normalize query to remove diacritics
        String asciiQuery = removeDiacritics(normalizedQuery);
        String asciiTeluguQuery = removeDiacritics(teluguQuery);
        String asciiEnglishQuery = removeDiacritics(englishQuery);

        for (BajanaManadaliListModel item : bajanaManadaliList) {
            String city = item.getBajanamandaliCity();
            String name = item.getNameOfGuru();
            String mobile = item.getBajanamandaliMobile();

            // Normalize city and name to remove diacritics
            String normalizedCity = normalize(city);
            String normalizedName = normalize(name);
            String normalizedMobile = normalize(mobile);

            // Transliterate city to English and Telugu
            String cityInEnglish = normalize(transliterateToEnglish(normalizedCity));
            String cityInTelugu = normalize(transliterateToTelugu(normalizedCity));

            // Remove diacritics from city names and transliterations
            String asciiCityInEnglish = removeDiacritics(cityInEnglish);
            String asciiCityInTelugu = removeDiacritics(cityInTelugu);

            // Compare the ASCII-normalized query and city
            if (
                    normalizedName.contains(normalizedQuery) ||
                            normalizedCity.contains(normalizedQuery) ||
                            normalizedMobile.contains(normalizedQuery) ||
                            cityInEnglish.contains(normalizedQuery) ||
                            cityInTelugu.contains(normalizedQuery) ||
                            asciiCityInEnglish.contains(asciiQuery) ||
                            asciiCityInTelugu.contains(asciiQuery) ||
                            normalizedName.contains(teluguQuery) ||
                            normalizedCity.contains(teluguQuery) ||
                            normalizedName.contains(englishQuery) ||
                            normalizedCity.contains(englishQuery) ||
                            asciiCityInEnglish.contains(asciiTeluguQuery) ||
                            asciiCityInEnglish.contains(asciiEnglishQuery)
            ) {
                filteredList.add(item);
            }
        }

        updateRecyclerview();
    }
    private String removeDiacritics(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Removes all marks/diacritics
                .toLowerCase(Locale.getDefault())
                .trim();
    }
    // Helper to check if query is in Telugu script
    private boolean isTelugu(String query) {
        // Check if the query contains Telugu characters
        for (char c : query.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.TELUGU) {
                return true; // It's in Telugu
            }
        }
        return false; // Otherwise, it's not
    }

    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
                .toLowerCase(Locale.getDefault())
                .trim();
    }

    private String transliterateToEnglish(String input) {
        Transliterator transliterator = Transliterator.getInstance("Telugu-Latin");
        return transliterator.transliterate(input);
    }

    private String transliterateToTelugu(String input) {
        Transliterator transliterator = Transliterator.getInstance("Latin-Telugu");
        return transliterator.transliterate(input);
    }


    private void fetchMandaliList() {

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

        Call<BajanaMandaliList> call = apiClient.getBajamandaliList();
        call.enqueue(new Callback<BajanaMandaliList>() {
            @Override
            public void onResponse(Call<BajanaMandaliList> call, Response<BajanaMandaliList> response) {
                if (response.isSuccessful()) {

                    Log.d("API Response", response.body().toString());
                    // Handle successful response
                    BajanaMandaliList data = response.body();


                    bajanaManadaliList.clear();
                    bajanaManadaliList.addAll(Arrays.asList(data.getResult()));


                    filteredList.clear();
                    filteredList.addAll(bajanaManadaliList);

                    // Save the data to SharedPreferences for future use
                    saveDataToSharedPreferences(data);



                    // Update your UI with the received data
                     updateRecyclerview();

                } else {
                    // Handle unsuccessful response
                    // You might want to show an error message or handle it in some way
                    Log.e("API Response", "Unsuccessful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<BajanaMandaliList> call, Throwable t) {
                // Handle failure, for example, show an error message
                Log.e("API Failure", "Error: " + t.getMessage());
            }
        });

    }






    private void saveDataToSharedPreferences(BajanaMandaliList data) {
        // Save the data to SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("AyyappaMandaliPrefs", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(data.getResult());
        editor.putString("bajana_mandali_list", json);
        editor.apply();
    }

    private List<BajanaManadaliListModel> parseCachedData(String cachedData) {
        // Use Gson to parse the JSON string back to your data model
        Gson gson = new Gson();
        Type listType = new TypeToken<List<BajanaManadaliListModel>>() {}.getType();

        return gson.fromJson(cachedData, listType);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AyyappaMandaliListActivity.this);
        View dialogView = LayoutInflater.from(AyyappaMandaliListActivity.this).inflate(R.layout.dialog_mandali_information, null);
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
