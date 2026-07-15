package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.dst.ayyapatelugu.R;

public class UpdateActivity extends AppCompatActivity {

    TextView  txtFeatures;
    Button btnUpdate;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);



        txtFeatures = findViewById(R.id.txtFeatures);
        btnUpdate = findViewById(R.id.btnUpdate);

        String version = getIntent().getStringExtra("version");
        String features = getIntent().getStringExtra("features");



        if (features != null) {
            txtFeatures.setText("✔ " + features.replace("|", "\n✔ "));
        }

        btnUpdate.setOnClickListener(v -> {

            try {

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.dst.ayyapatelugu"));
                startActivity(intent);

            } catch (ActivityNotFoundException e) {

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.dst.ayyapatelugu"));
                startActivity(intent);

            }



        });

    }
}