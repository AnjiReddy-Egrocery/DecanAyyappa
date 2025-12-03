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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Adapter.MonthAdapter;
import com.dst.ayyapatelugu.Model.DateTimeUtil;
import com.dst.ayyapatelugu.Model.PanchangDay;
import com.dst.ayyapatelugu.Model.PanchangamData;
import com.dst.ayyapatelugu.Model.TeluguCalenderDataResponse;
import com.dst.ayyapatelugu.Model.panchagamModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.text.SimpleDateFormat;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
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

    Toolbar toolbar;
    ImageView imageLeft,imageRight;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;

    private RecyclerView rv;
    private MonthAdapter adapter;
    private List<PanchangDay> days = new ArrayList<>();
    private int displayMonth, displayYear;
    @SuppressLint({"MissingInflatedId", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telugu_calender);

        toolbar = findViewById(R.id.toolbar);

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
                Intent intent=new Intent(TeluguCalenderActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TeluguCalenderActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(TeluguCalenderActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TeluguCalenderActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        rv = findViewById(R.id.rvCalendar);
        rv.setLayoutManager(new GridLayoutManager(this, 7));
        adapter = new MonthAdapter(this, days, this::showDayDialog);
        rv.setAdapter(adapter);

       LocalDate now = LocalDate.now();
        displayMonth = now.getMonthValue();
        displayYear = now.getYear();

        findViewById(R.id.btnPrev).setOnClickListener(v -> {
            displayMonth--;
            if (displayMonth < 1) { displayMonth = 12; displayYear--; }
            loadMonth(displayMonth, displayYear);
            updateMonthTitle();
        });
        findViewById(R.id.btnNext).setOnClickListener(v -> {
            displayMonth++;
            if (displayMonth > 12) { displayMonth = 1; displayYear++; }
            loadMonth(displayMonth, displayYear);
            updateMonthTitle();
        });

        updateMonthTitle();
        loadMonth(displayMonth, displayYear);

    }

    @SuppressLint("MissingInflatedId")
    private void showDayDialog(PanchangDay day) {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialog_day_detail, null);
        b.setView(v);

        PanchangamData dd = day.getData() != null ? day.getData().getData() : null;

        ((TextView)v.findViewById(R.id.tvDetailTitle))
                .setText(day.getDate());

        ((TextView)v.findViewById(R.id.tvDetailVaara))
                .setText("వారం: " + (dd != null ? dd.getVaara() : ""));

        ((TextView)v.findViewById(R.id.tvDetailTithi))
                .setText("తిథి: " + (dd != null && dd.getTithi() != null && !dd.getTithi().isEmpty()
                        ? dd.getTithi().get(0).getName() : ""));

        ((TextView)v.findViewById(R.id.tvDetailNakshatra))
                .setText("నక్షత్రం: " + (dd != null && dd.getNakshatra() != null && !dd.getNakshatra().isEmpty()
                        ? dd.getNakshatra().get(0).getName() : ""));

        ((TextView)v.findViewById(R.id.tvDetailSunrise))
                .setText("సూర్యోదయం: " + (dd != null ? DateTimeUtil.formatISOToTime(dd.getSunrise()) : ""));

        ((TextView)v.findViewById(R.id.tvDetailSunset))
                .setText("సూర్యాస్తమయం: " + (dd != null ? DateTimeUtil.formatISOToTime(dd.getSunset()) : ""));

        b.setPositiveButton("Close", (dialog, which) -> dialog.dismiss());
        b.show();
    }

    private void loadMonth(int displayMonth, int displayYear) {
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

        RequestBody MonthPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(displayMonth));
        RequestBody YearPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(displayYear));

        Call<TeluguCalenderDataResponse> call = apiClient.telugucalenderdata(MonthPart,YearPart);

        call.enqueue(new Callback<TeluguCalenderDataResponse>() {
            @Override
            public void onResponse(Call<TeluguCalenderDataResponse> call, Response<TeluguCalenderDataResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        "success".equals(response.body().getStatus())) {

                    days.clear();

                    List<PanchangDay> list = response.body().getData();
                    if (list != null) {

                        // 1️⃣ Find first date of this month
                        LocalDate firstDate = LocalDate.of(displayYear, displayMonth, 1);

                        // 2️⃣ Monday = 1 ... Sunday = 7
                        int dayValue = firstDate.getDayOfWeek().getValue();

                        // 3️⃣ SUNDAY-FIRST OFFSET
                        // Sunday = 7 → offset = 0
                        // Monday = 1 → offset = 1
                        // Tuesday = 2 → offset = 2 ...
                        int startOffset = dayValue % 7;

                        // 4️⃣ Add empty cells before the 1st day
                        for (int i = 0; i < startOffset; i++) {
                            days.add(new PanchangDay()); // empty
                        }

                        // 5️⃣ Add actual API days
                        days.addAll(list);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TeluguCalenderActivity.this,
                            "Empty response or Status failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeluguCalenderDataResponse> call, Throwable t) {
                Toast.makeText(TeluguCalenderActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMonthTitle() {
        String mName = java.time.Month.of(displayMonth).getDisplayName(TextStyle.FULL, new Locale("te", "IN"));
        ((TextView)findViewById(R.id.tvMonth)).setText(mName + " " + displayYear);
    }



   }
