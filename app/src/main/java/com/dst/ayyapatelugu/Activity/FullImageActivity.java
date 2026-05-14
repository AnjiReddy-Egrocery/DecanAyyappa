package com.dst.ayyapatelugu.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.R;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageActivity extends AppCompatActivity {

    PhotoView fullImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImage = findViewById(R.id.fullImage);

        String imageUrl = getIntent().getStringExtra("image");

        Glide.with(this)
                .load(imageUrl)
                .into(fullImage);

        // click → close
        fullImage.setOnClickListener(v -> finish());
    }
}