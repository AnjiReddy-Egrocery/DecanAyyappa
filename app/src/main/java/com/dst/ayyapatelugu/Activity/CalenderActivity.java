package com.dst.ayyapatelugu.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String TAG = "CalenderActivity";

    // "2026-27", "2026 - 27", "2026–27", "2026-2027" -> "2026 - 27"
    private static final Pattern YEAR_RANGE =
            Pattern.compile("^(\\d{4})\\s*[-\u2013\u2014]\\s*(\\d{2,4})$");

    Toolbar toolbar;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefresh;
    TextView txtYear;
    ImageView imageLeft, imageRight;
    ImageView imageAnadanam, imageNityaPooja;
    TextView textAndanam, txtNityaPooja;

    CalenderAdapter calenderAdapter;

    // These hold the ORIGINAL strings from the server (sent back to the API as they are).
    // Only the text shown in txtYear is normalized.
    private String Calender = "";
    private String previousYear = "";
    private String currentYear = "";
    private String nextYear = "";

    // Created once and reused for every request
    private APiInterface apiClient;
    private Call<CalenderDataResponse> runningCall;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        toolbar = findViewById(R.id.toolbar);
        txtYear = findViewById(R.id.txt_calender);
        imageLeft = findViewById(R.id.image_left);
        imageRight = findViewById(R.id.image_right);

        String intentYear = getIntent().getStringExtra("Year");
        Log.d(TAG, "Raw intent year: " + intentYear);
        Calender = (intentYear != null && !intentYear.isEmpty()) ? intentYear : "";

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) {
            nav.setTint(getResources().getColor(R.color.white));
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bottom bar shortcuts
        View.OnClickListener openAnadanam =
                v -> startActivity(new Intent(CalenderActivity.this, AnadanamActivity.class));
        View.OnClickListener openNityaPooja =
                v -> startActivity(new Intent(CalenderActivity.this, NityaPoojaActivity.class));

        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        textAndanam = findViewById(R.id.layout_txt_anadanam);
        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageAnadanam.setOnClickListener(openAnadanam);
        textAndanam.setOnClickListener(openAnadanam);
        txtNityaPooja.setOnClickListener(openNityaPooja);
        imageNityaPooja.setOnClickListener(openNityaPooja);

        // Year navigation
        imageRight.setOnClickListener(view -> {
            if (nextYear != null && !nextYear.isEmpty()) {
                Calender = nextYear;
                VerifyMethod(Calender);
            } else {
                Toast.makeText(this, "No data available for the next year.", Toast.LENGTH_SHORT).show();
            }
        });

        imageLeft.setOnClickListener(view -> {
            if (previousYear != null && !previousYear.isEmpty()) {
                Calender = previousYear;
                VerifyMethod(Calender);
            } else {
                Toast.makeText(this, "No data available for the previous year.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView = findViewById(R.id.recycler_calender);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiClient = createApiClient();

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(() -> VerifyMethod(Calender));

        VerifyMethod(Calender);
    }

    private APiInterface createApiClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // TODO: remove UnsafeTrustManager once the server has a valid SSL certificate.
        // Trusting all certificates is a security risk and can cause Google Play warnings.
        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(),
                        UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(APiInterface.class);
    }

    private void VerifyMethod(String calender) {
        Log.d(TAG, "VerifyMethod year: " + calender);

        // Cancel any request still in progress (e.g. quick taps on the arrows)
        if (runningCall != null) {
            runningCall.cancel();
        }
        RequestBody yearPart = RequestBody.create(
                MediaType.parse("text/plain"), calender == null ? "" : calender);
        runningCall = apiClient.calenderData(yearPart);
         runningCall.enqueue(new Callback<CalenderDataResponse>() {
            @Override
            public void onResponse(@NonNull Call<CalenderDataResponse> call,
                                   @NonNull Response<CalenderDataResponse> response) {
                swipeRefresh.setRefreshing(false);
                if (isFinishing() || isDestroyed()) return;

                CalenderDataResponse dataResponse = response.body();
                if (!response.isSuccessful() || dataResponse == null) {
                    Toast.makeText(CalenderActivity.this, "Server error. Please try again.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String errorCode = dataResponse.getErrorCode();
                if ("200".equals(errorCode)) {
                    showResult(dataResponse.getResult());
                } else if ("202".equals(errorCode)) {
                    Toast.makeText(CalenderActivity.this,
                            "Invalid Request, no data found for your request",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CalenderActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CalenderDataResponse> call, @NonNull Throwable t) {
                swipeRefresh.setRefreshing(false);
                if (call.isCanceled() || isFinishing() || isDestroyed()) return;

                Log.e(TAG, "Calendar request failed", t);
                Toast.makeText(CalenderActivity.this,
                        "Network error. Please check your connection and try again.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showResult(List<CalenderDataResponse.Result> results) {
        if (results == null || results.isEmpty()) {
            Toast.makeText(this, "No data available.", Toast.LENGTH_SHORT).show();
            return;
        }

        // The API returns one result object per year
        CalenderDataResponse.Result result = results.get(0);

        currentYear = result.getYear() != null ? result.getYear() : "";
        previousYear = result.getPrevYear() != null ? result.getPrevYear() : "";
        nextYear = result.getNextYear() != null ? result.getNextYear() : "";

        List<CalenderDataResponse.Result.Poojas> poojaList = result.getPoojasList();
        calenderAdapter = new CalenderAdapter(this, poojaList);
        recyclerView.setAdapter(calenderAdapter);

        txtYear.setText(formatYearLabel(currentYear));   // display only
    }

    // Display only: always "2026 - 27" style, whatever format the server sends
    private String formatYearLabel(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        Matcher m = YEAR_RANGE.matcher(s);
        if (m.matches()) {
            String end = m.group(2);
            if (end.length() == 4) end = end.substring(2);
            return m.group(1) + " - " + end;
        }
        return s;
    }

    @Override
    protected void onDestroy() {
        if (runningCall != null) {
            runningCall.cancel();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.popup_info) {
            informationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingInflatedId")
    private void informationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);
        View dialogView = LayoutInflater.from(CalenderActivity.this)
                .inflate(R.layout.dialog_calender_information, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        ImageButton closeButton = dialogView.findViewById(R.id.btn_close);
        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
