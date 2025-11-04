package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.ayyapatelugu.Model.panchagamModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.APiInterface;
import com.dst.ayyapatelugu.Services.UnsafeTrustManager;

import java.util.Date;
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

public class PanchagamActivity extends AppCompatActivity {
    Toolbar toolbar;
    ImageView imageLeft,imageRight;

    ImageView imageAnadanam,imageNityaPooja;
    TextView textAndanam,txtNityaPooja;
    TextView txtCalender, txtVaara, txtTithi, txtNakshatra, txtYoga, txtKarana,
            txtSunrise, txtSunset, txtAshubha, txtShubha,txtMoonrise, txtMoonset,txtDate;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panchagam);

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
                Intent intent=new Intent(PanchagamActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        textAndanam = findViewById(R.id.layout_txt_anadanam);
        textAndanam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PanchagamActivity.this,AnadanamActivity.class);
                startActivity(intent);
            }
        });

        txtNityaPooja = findViewById(R.id.txt_nitya_pooja);
        txtNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(PanchagamActivity.this, NityaPoojaActivity.class);
                startActivity(intent);

            }
        });
        imageNityaPooja = findViewById(R.id.img_nitya_pooja);
        imageNityaPooja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PanchagamActivity.this,NityaPoojaActivity.class);
                startActivity(intent);
            }
        });

        txtCalender = findViewById(R.id.txt_calender);
        txtVaara = findViewById(R.id.txtVaara);
        txtTithi = findViewById(R.id.txtTithi);
        txtNakshatra = findViewById(R.id.txtNakshatra);
        txtYoga = findViewById(R.id.txtYoga);
        txtKarana = findViewById(R.id.txtKarana);
        txtSunrise = findViewById(R.id.txtSunrise);
        txtSunset = findViewById(R.id.txtSunset);
        txtMoonrise = findViewById(R.id.txtMoonrise);
        txtMoonset = findViewById(R.id.txtMoonset);
        txtDate = findViewById(R.id.txtDate);
        txtAshubha = findViewById(R.id.txtAshubha);
        txtShubha = findViewById(R.id.txtShubha);
        TextView txtCalender = findViewById(R.id.txt_calender);
        ImageView imageLeft = findViewById(R.id.image_left);
        ImageView imageRight = findViewById(R.id.image_right);

        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("te", "IN")); // Telugu month names
        Calendar calendar = Calendar.getInstance();

// ✅ Set current date
        txtCalender.setText(displayFormat.format(calendar.getTime()));
        txtDate.setText(displayFormat.format(calendar.getTime()));
        loadPanchang(apiFormat.format(calendar.getTime())); // initial load

// ⬅️ Previous date
        imageLeft.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, -1); // subtract 1 day
            txtCalender.setText(displayFormat.format(calendar.getTime()));
            txtDate.setText(displayFormat.format(calendar.getTime()));

            loadPanchang(apiFormat.format(calendar.getTime()));
        });

// ➡️ Next date
        imageRight.setOnClickListener(v -> {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // add 1 day
            txtCalender.setText(displayFormat.format(calendar.getTime()));
            txtDate.setText(displayFormat.format(calendar.getTime()));

            loadPanchang(apiFormat.format(calendar.getTime()));
        });



    }

    private void loadPanchang(String date) {
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

        RequestBody YearPart = RequestBody.create(MediaType.parse("text/plain"), date);

        Call<panchagamModel> call = apiClient.getPanchaamData(YearPart);

        call.enqueue(new Callback<panchagamModel>() {
            @Override
            public void onResponse(Call<panchagamModel> call, Response<panchagamModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                        panchagamModel.PanchangData data = response.body().data.data;

                        txtVaara.setText("వారము: " + data.vaara);
                        txtSunrise.setText("సూర్యోదయం: " + formatTime(data.sunrise));
                        txtSunset.setText("సూర్యాస్తమయం: " + formatTime(data.sunset));

                        // 🌙 చంద్రోదయం & చంద్రాస్తమయం
                        txtMoonrise.setText("చంద్రోదయం: " + formatTime(data.moonrise));
                        txtMoonset.setText("చంద్రాస్తమయం: " + formatTime(data.moonset));

                        // 🕉 తిథులు
                        if (data.tithi != null && !data.tithi.isEmpty()) {
                            StringBuilder tithiBuilder = new StringBuilder("తిథులు\n");
                            for (panchagamModel.Tithi tithi : data.tithi) {
                                tithiBuilder.append(tithi.name)
                                        .append(" (").append(tithi.paksha).append(")\n")
                                        .append(formatTime(tithi.start))
                                        .append(" - ")
                                        .append(formatTime(tithi.end))
                                        .append("\n\n");
                            }
                            txtTithi.setText(tithiBuilder.toString().trim());
                        }

                        // 🌟 నక్షత్రాలు
                        if (data.nakshatra != null && !data.nakshatra.isEmpty()) {
                            StringBuilder nakBuilder = new StringBuilder("నక్షత్రాలు\n");
                            for (panchagamModel.Nakshatra nak : data.nakshatra) {
                                nakBuilder.append(nak.name);
                                if (nak.lord != null && nak.lord.name != null)
                                    nakBuilder.append(" (అధిపతి: ").append(nak.lord.name).append(")");
                                nakBuilder.append("\n")
                                        .append(formatTime(nak.start)).append(" - ").append(formatTime(nak.end))
                                        .append("\n\n");
                            }
                            txtNakshatra.setText(nakBuilder.toString().trim());
                        }

                        // 🧘‍♂️ యోగాలు
                        if (data.yoga != null && !data.yoga.isEmpty()) {
                            StringBuilder yogaBuilder = new StringBuilder("యోగాలు\n");
                            for (panchagamModel.Yoga yoga : data.yoga) {
                                yogaBuilder.append(yoga.name)
                                        .append("\n")
                                        .append(formatTime(yoga.start)).append(" - ").append(formatTime(yoga.end))
                                        .append("\n\n");
                            }
                            txtYoga.setText(yogaBuilder.toString().trim());
                        }

                        // 🔱 కరణాలు
                        if (data.karana != null && !data.karana.isEmpty()) {
                            StringBuilder karanaBuilder = new StringBuilder("కరణాలు\n");
                            for (panchagamModel.Karana karana : data.karana) {
                                karanaBuilder.append(karana.name)
                                        .append("\n")
                                        .append(formatTime(karana.start)).append(" - ").append(formatTime(karana.end))
                                        .append("\n\n");
                            }
                            txtKarana.setText(karanaBuilder.toString().trim());
                        }

                        // 🪔 శుభ ముహూర్తాలు
                        if (data.auspicious_period != null && !data.auspicious_period.isEmpty()) {
                            StringBuilder shubhaBuilder = new StringBuilder("శుభ ముహూర్తాలు\n");
                            for (panchagamModel.PeriodList item : data.auspicious_period) {
                                if (item.period != null && !item.period.isEmpty()) {
                                    shubhaBuilder.append(item.name)
                                            .append(" (").append(item.type).append(")\n")
                                            .append(formatTime(item.period.get(0).start))
                                            .append(" - ")
                                            .append(formatTime(item.period.get(0).end))
                                            .append("\n\n");
                                }
                            }
                            txtShubha.setText(shubhaBuilder.toString().trim());
                        }

                        // ☠️ అశుభ కాలాలు
                        if (data.inauspicious_period != null && !data.inauspicious_period.isEmpty()) {
                            StringBuilder ashubhaBuilder = new StringBuilder("అశుభ కాలాలు\n");
                            for (panchagamModel.PeriodList item : data.inauspicious_period) {
                                if (item.period != null && !item.period.isEmpty()) {
                                    ashubhaBuilder.append(item.name)
                                            .append(" (").append(item.type).append(")\n")
                                            .append(formatTime(item.period.get(0).start))
                                            .append(" - ")
                                            .append(formatTime(item.period.get(0).end))
                                            .append("\n\n");
                                }
                            }
                            txtAshubha.setText(ashubhaBuilder.toString().trim());
                        }

                    } else {
                        Toast.makeText(PanchagamActivity.this, "No Data", Toast.LENGTH_SHORT).show();
                    }

                }

            @Override
            public void onFailure(Call<panchagamModel> call, Throwable t) {
                Toast.makeText(PanchagamActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(String datetime) {
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH);
            Date date = in.parse(datetime);
            SimpleDateFormat out = new SimpleDateFormat("hh:mm a", Locale.forLanguageTag("te-IN"));
            return out.format(date);
        } catch (Exception e) {
            return datetime;
        }
    }

}