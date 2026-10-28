package com.dst.ayyapatelugu.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dst.ayyapatelugu.Adapter.ViewAllAnadanamListAdapter;
import com.dst.ayyapatelugu.DataBase.SharedPreferencesManager;
import com.dst.ayyapatelugu.Model.AnadanamList;
import com.dst.ayyapatelugu.Model.AnadanamListModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

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

public class AnadanamTemplesFragment extends Fragment {
    RecyclerView recyclerviewTemples;

    List<AnadanamListModel> templeList;

    ViewAllAnadanamListAdapter viewAllTempleListAdapter;
    private SearchView searchView;

    private Retrofit retrofit;

    // ---------- Location ----------
    private FusedLocationProviderClient fusedLocationClient;
    private Double currentLat = null;
    private Double currentLng = null;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fine = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarse = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (Boolean.TRUE.equals(fine) || Boolean.TRUE.equals(coarse)) {
                    fetchUserLocation();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.anadanam_temples, container, false);
        recyclerviewTemples = view.findViewById(R.id.recycler_anadanam);
        templeList = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerviewTemples.setLayoutManager(layoutManager);

        // Location init (data load ki mundu)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestLocation();

        fechedDatafromShredPreferences();

        searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("Search by Name");
        searchView.setIconifiedByDefault(false); // Keep it expanded
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.setClickable(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setHint("Search by Name");
        searchEditText.setHintTextColor(Color.GRAY);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (viewAllTempleListAdapter != null) {
                    viewAllTempleListAdapter.getFilter().filter(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (viewAllTempleListAdapter != null) {
                    viewAllTempleListAdapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        SwipeRefreshLayout swipeRefresh = view.findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            requestLocation();          // refresh lo location kuda update
            fetchDataFromDataBase();
            swipeRefresh.setRefreshing(false);
        });

        fetchDataFromDataBase();

        return view;
    }

    // ================= LOCATION =================

    private void requestLocation() {
        if (!isAdded()) return;

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchUserLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @SuppressWarnings("MissingPermission")
    private void fetchUserLocation() {
        CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setMaxUpdateAgeMillis(60_000)
                .build();

        fusedLocationClient.getCurrentLocation(request, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLng = location.getLongitude();
                        if (viewAllTempleListAdapter != null) {
                            viewAllTempleListAdapter.setUserLocation(currentLat, currentLng);
                        }
                    }
                });
    }

    // ================= DATA =================

    private void fechedDatafromShredPreferences() {
        List<AnadanamListModel> anadanamListModels = SharedPreferencesManager.getAyyappaAnadanamList(getContext());
        if (anadanamListModels != null && !anadanamListModels.isEmpty()) {
            // Data exists in SharedPreferences, update RecyclerView
            updateRecyclerView(anadanamListModels);
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
        Call<AnadanamList> modelCall = apiClient.getAnadanam();
        modelCall.enqueue(new Callback<AnadanamList>() {
            @Override
            public void onResponse(Call<AnadanamList> call, Response<AnadanamList> response) {
                if (!isAdded()) return;
                AnadanamList list = response.body();
                if (list == null || list.getResult() == null) return;

                templeList = new ArrayList<>(Arrays.asList(list.getResult()));
                SharedPreferencesManager.clearAyyappaTempleList(getContext());
                SharedPreferencesManager.saveAyyappaAnadanamList(getContext(), templeList);

                updateRecyclerView(templeList);
            }

            @Override
            public void onFailure(Call<AnadanamList> call, Throwable t) {
                if (!isAdded()) return;
                templeList = SharedPreferencesManager.getAyyappaAnadanamList(getContext());
                if (templeList != null && !templeList.isEmpty()) {
                    updateRecyclerView(templeList);
                }
            }
        });
    }

    private void updateRecyclerView(List<AnadanamListModel> templesListModels) {
        viewAllTempleListAdapter = new ViewAllAnadanamListAdapter(getContext(), templesListModels);

        // Adapter malli create ayyetappudu location kuda set cheyyali
        if (currentLat != null && currentLng != null) {
            viewAllTempleListAdapter.setUserLocation(currentLat, currentLng);
        }

        recyclerviewTemples.setAdapter(viewAllTempleListAdapter);
    }
}