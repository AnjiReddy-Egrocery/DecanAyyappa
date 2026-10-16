package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.MonthAdapter;
import com.dst.ayyapatelugu.Model.DateTimeUtil;
import com.dst.ayyapatelugu.Model.PanchangDay;
import com.dst.ayyapatelugu.Model.PanchangamData;
import com.dst.ayyapatelugu.Model.TeluguCalenderDataResponse;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeluguCalenderActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView imageAnadanam, imageNityaPooja;
    private TextView textAndanam, txtNityaPooja;

    private RecyclerView rv;
    private MonthAdapter adapter;
    private final List<PanchangDay> days = new ArrayList<>();
    private int displayMonth, displayYear;

    private APiInterface apiClient;

    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telugu_calender);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Drawable nav = toolbar.getNavigationIcon();
        if (nav != null) nav.setTint(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bottom bar clicks
        View.OnClickListener openAnadanam =
                v -> startActivity(new Intent(this, AnadanamActivity.class));
        View.OnClickListener openNityaPooja =
                v -> startActivity(new Intent(this, NityaPoojaActivity.class));

        imageAnadanam = findViewById(R.id.layout_image_anadanam);
        textAndanam = findViewById(R.id.layout_txt_anadanam);
        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageAnadanam.setOnClickListener(openAnadanam);
        textAndanam.setOnClickListener(openAnadanam);
        txtNityaPooja.setOnClickListener(openNityaPooja);
        imageNityaPooja.setOnClickListener(openNityaPooja);

        // RecyclerView
        rv = findViewById(R.id.rvCalendar);
        rv.setLayoutManager(new GridLayoutManager(this, 7));
        adapter = new MonthAdapter(this, days, this::showDayDialog);
        rv.setAdapter(adapter);

        // Retrofit (once)
        apiClient = buildApi();

        // Prev / Next
        findViewById(R.id.btnPrev).setOnClickListener(v -> {
            displayMonth--;
            if (displayMonth < 1) { displayMonth = 12; displayYear--; }
            updateMonthTitle();
            loadMonth(displayMonth, displayYear);
        });
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            displayMonth++;
            if (displayMonth > 12) { displayMonth = 1; displayYear++; }
            updateMonthTitle();
            loadMonth(displayMonth, displayYear);
        });

        // Intent month/year (FCM)
        String intentMonth = getIntent().getStringExtra("month");
        String intentYear = getIntent().getStringExtra("year");
        displayMonth = (intentMonth != null && !intentMonth.isEmpty())
                ? Integer.parseInt(intentMonth) : LocalDate.now().getMonthValue();
        displayYear = (intentYear != null && !intentYear.isEmpty())
                ? Integer.parseInt(intentYear) : LocalDate.now().getYear();

        updateMonthTitle();
        loadMonth(displayMonth, displayYear);
    }

    private APiInterface buildApi() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(UnsafeTrustManager.createTrustAllSslSocketFactory(),
                        UnsafeTrustManager.createTrustAllTrustManager())
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://www.ayyappatelugu.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(APiInterface.class);
    }

    private void loadMonth(int month, int year) {
        RequestBody monthPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(month));
        RequestBody yearPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(year));

        apiClient.telugucalenderdata(monthPart, yearPart)
                .enqueue(new Callback<TeluguCalenderDataResponse>() {
                    @Override
                    public void onResponse(Call<TeluguCalenderDataResponse> call,
                                           Response<TeluguCalenderDataResponse> response) {
                        if (response.isSuccessful() && response.body() != null
                                && "success".equals(response.body().getStatus())) {

                            days.clear();
                            List<PanchangDay> list = response.body().getData();
                            if (list != null && !list.isEmpty()) {
                                // Sunday-first offset
                                int startOffset = LocalDate.of(year, month, 1)
                                        .getDayOfWeek().getValue() % 7;
                                for (int i = 0; i < startOffset; i++) days.add(new PanchangDay());
                                days.addAll(list);
                            }
                            Log.d("PANCHANG", "days size = " + days.size());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(TeluguCalenderActivity.this,
                                    "Empty response or Status failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TeluguCalenderDataResponse> call, Throwable t) {
                        Toast.makeText(TeluguCalenderActivity.this,
                                "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("MissingInflatedId")
    private void showDayDialog(PanchangDay day) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_day_detail, null);
        b.setView(v);

        PanchangamData dd = (day.getData() != null) ? day.getData().getData() : null;

        ((TextView) v.findViewById(R.id.tvDetailTitle)).setText(day.getDate());
        ((TextView) v.findViewById(R.id.tvDetailVaara))
                .setText("వారం: " + (dd != null && dd.getVaara() != null ? dd.getVaara() : ""));
        ((TextView) v.findViewById(R.id.tvDetailTithi))
                .setText("తిథి: " + (dd != null && dd.getTithi() != null && !dd.getTithi().isEmpty()
                        ? dd.getTithi().get(0).getName() : ""));
        ((TextView) v.findViewById(R.id.tvDetailNakshatra))
                .setText("నక్షత్రం: " + (dd != null && dd.getNakshatra() != null && !dd.getNakshatra().isEmpty()
                        ? dd.getNakshatra().get(0).getName() : ""));
        ((TextView) v.findViewById(R.id.tvDetailSunrise))
                .setText("సూర్యోదయం: " + (dd != null ? DateTimeUtil.formatISOToTime(dd.getSunrise()) : ""));
        ((TextView) v.findViewById(R.id.tvDetailSunset))
                .setText("సూర్యాస్తమయం: " + (dd != null ? DateTimeUtil.formatISOToTime(dd.getSunset()) : ""));

        b.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void updateMonthTitle() {
        String mName = java.time.Month.of(displayMonth)
                .getDisplayName(TextStyle.FULL, new Locale("te", "IN"));
        ((TextView) findViewById(R.id.tvMonth)).setText(mName + " " + displayYear);
    }
}