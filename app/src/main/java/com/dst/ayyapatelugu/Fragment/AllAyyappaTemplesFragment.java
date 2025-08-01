package com.dst.ayyapatelugu.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dst.ayyapatelugu.Adapter.ViewAllAyyappaTempleListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.AyyaTempleListModel;
import com.dst.ayyapatelugu.Model.AyyappaTempleList;
import com.dst.ayyapatelugu.Model.GuruSwamiModelList;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

public class AllAyyappaTemplesFragment extends Fragment {

    RecyclerView recyclerviewayyappaTemples;

    List<AyyaTempleListModel> templeList;

    ViewAllAyyappaTempleListAdapter viewAllAyyappaTempleListAdapter;

    private SearchView searchView;


    private Retrofit retrofit;

    private double userLatitude = 0.0, userLongitude = 0.0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.ayyappatemples_fragment,container,false);
        recyclerviewayyappaTemples = view.findViewById(R.id.recycler_ayyappatemples);

        templeList = new ArrayList<>();
        LinearLayoutManager layoutManager =new LinearLayoutManager(getContext());
        recyclerviewayyappaTemples.setLayoutManager(layoutManager);

        fechedDatafromShredPreferences();


        searchView = view.findViewById(R.id.searchView);
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
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (viewAllAyyappaTempleListAdapter != null) {
                    viewAllAyyappaTempleListAdapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewAllAyyappaTempleListAdapter != null) {
                    viewAllAyyappaTempleListAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            fetchDataFromDataBase();
            swipeRefresh.setRefreshing(false);
        });

        fetchDataFromDataBase();


        return view;
    }

    private void fechedDatafromShredPreferences() {
        templeList = SharedPreferencesManager.getAyyappaTemplesList(getActivity());



        if (templeList != null && !templeList.isEmpty()) {
            Log.e("TempleList", "Loaded from SharedPreferences: " + templeList.size());
            updateRecyclerView(templeList);
        } else {
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
        Call<AyyappaTempleList> modelCall=apiClient.getAyyappaTempleList();
        modelCall.enqueue(new Callback<AyyappaTempleList>() {
            @Override
            public void onResponse(Call<AyyappaTempleList> call, Response<AyyappaTempleList> response) {
                AyyappaTempleList list=response.body();
                templeList=new ArrayList<>(Arrays.asList(list.getResult()));
                SharedPreferencesManager.clearAyyappaTempleList(getContext());
                SharedPreferencesManager.saveAyyappaTempleList(getContext(), templeList);
                Log.e("TempleList", "Loaded from API: " + templeList.size());
                updateRecyclerView(templeList);

            }

            @Override
            public void onFailure(Call<AyyappaTempleList> call, Throwable t) {
                templeList = SharedPreferencesManager.getAyyappaTemplesList(getContext());
                if (templeList != null && !templeList.isEmpty()) {
                    // Update the RecyclerView
                    updateRecyclerView(templeList);
                }
                Log.e("TempleList", "API Failed: " + t.getMessage());
            }
        });

    }

    private void updateRecyclerView(List<AyyaTempleListModel> templesListModels) {
        viewAllAyyappaTempleListAdapter = new ViewAllAyyappaTempleListAdapter(getActivity(), templesListModels);
        recyclerviewayyappaTemples.setAdapter(viewAllAyyappaTempleListAdapter);
    }




}
