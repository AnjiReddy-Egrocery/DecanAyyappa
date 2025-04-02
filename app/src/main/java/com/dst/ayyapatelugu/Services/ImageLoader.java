package com.dst.ayyapatelugu.Services;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.dst.ayyapatelugu.R;

import okhttp3.OkHttpClient;

public class ImageLoader {
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        // Create GlideUrl with headers
        GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                .addHeader("User-Agent", "Your User Agent") // Example header
                .addHeader("Accept", "*/*") // Accept all types
                .build());

        // Log the URL for debugging
        Log.d("ImageLoader", "Loading image URL: " + imageUrl);

        // Use Glide to load the image
        Glide.with(context)
                .load(glideUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Caching strategy

                )
                .listener(new com.bumptech.glide.request.RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("ImageLoader", "Image load failed: " + (e != null ? e.getMessage() : "Unknown error"));
                        return false; // Allow Glide to handle the error
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("ImageLoader", "Image loaded successfully");
                        return false; // Allow Glide to continue processing
                    }
                })
                .into(imageView);
    }
}
