package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.CalenderAdapter;
import com.dst.ayyapatelugu.Model.CalenderDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.ApiClient;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.time.Year;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalenderActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;

    TextView txtYear;

    private String Calender="";
    private String previousYear="";
    private  String currentYear="";
    private  String nextYear="";
    CalenderAdapter calenderAdapter;
    ImageView imageLeft,imageRight;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;


    private static final String PREF_NAME = "CalendarPrefs";
    private static final String KEY_CURRENT_YEAR = "currentYear";
    private static final String KEY_PREVIOUS_YEAR = "previousYear";
    private static final String KEY_NEXT_YEAR = "nextYear";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        toolbar = findViewById(R.id.toolbar);
        /*toolbar.setLogo(R.drawable.user_profile_background);*/
        txtYear=findViewById(R.id.txt_calender);
        imageLeft=findViewById(R.id.image_left);
        imageRight=findViewById(R.id.image_right);
        /*toolbar.setTitle("www.ayyappatelugu.com");
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
                Intent intent=new Intent(CalenderActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalenderActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(CalenderActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CalenderActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        imageRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextYear != null && !nextYear.isEmpty()) {
                    Calender = nextYear; // Update Calender with the next year
                    VerifyMethod(Calender);

                } else {
                    // Data is missing, handle this case (e.g., show a message)

                    Toast.makeText(CalenderActivity.this, "No data available for the next year.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousYear != null && !previousYear.isEmpty()) {
                    Calender = previousYear;
                    VerifyMethod(Calender);
                } else {
                    // Data is missing, handle this case (e.g., show a message)
                    Toast.makeText(CalenderActivity.this, "No data available for the next year.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        recyclerView =findViewById(R.id.recycler_calender);
        LinearLayoutManager layoutManager = new LinearLayoutManager(CalenderActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        VerifyMethod(Calender);

    }

    private void VerifyMethod(String calender) {
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
        RequestBody YearPart = RequestBody.create(MediaType.parse("text/plain"), calender);
        Call<CalenderDataResponse> call=apiClient.calenderData(YearPart);
        call.enqueue(new Callback<CalenderDataResponse>() {
            @Override
            public void onResponse(Call<CalenderDataResponse> call, Response<CalenderDataResponse> response) {
               CalenderDataResponse dataResponse=response.body();
                if (dataResponse.getErrorCode().equals("202")) {

                    Toast.makeText(CalenderActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();

                } else if (dataResponse.getErrorCode().equals("200")) {

                    List<CalenderDataResponse.Result> results=dataResponse.getResult();
                    for (int i = 0; i < results.size(); i++) {
                        currentYear=results.get(i).getYear();
                        previousYear=results.get(i).getPrevYear();
                        nextYear=results.get(i).getNextYear();

                        List<CalenderDataResponse.Result.Poojas> topicsList = results.get(i).getPoojasList();
                        calenderAdapter = new CalenderAdapter(CalenderActivity.this, topicsList);
                        recyclerView.setAdapter(calenderAdapter);

                        // Save data to SharedPreferences
                        saveToSharedPreferences(currentYear, previousYear, nextYear);

                        // Retrieve data from SharedPreferences
                        retrieveFromSharedPreferences();
                    }
                    txtYear.setText(currentYear);
                } else {
                    Toast.makeText(CalenderActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CalenderDataResponse> call, Throwable t) {

            }
        });

    }

    private void retrieveFromSharedPreferences() {

        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String storedCurrentYear = sharedPreferences.getString(KEY_CURRENT_YEAR, "");
        String storedPreviousYear = sharedPreferences.getString(KEY_PREVIOUS_YEAR, "");
        String storedNextYear = sharedPreferences.getString(KEY_NEXT_YEAR, "");

        // Use the stored values as needed
        txtYear.setText(storedCurrentYear);
    }

    private void saveToSharedPreferences(String currentYear, String previousYear, String nextYear) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CURRENT_YEAR, currentYear);
        editor.putString(KEY_PREVIOUS_YEAR, previousYear);
        editor.putString(KEY_NEXT_YEAR, nextYear);
        editor.apply();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);
        View dialogView = LayoutInflater.from(CalenderActivity.this).inflate(R.layout.dialog_calender_information, null);
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