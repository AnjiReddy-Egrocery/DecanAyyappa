package com.dst.ayyapatelugu.Services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dst.ayyapatelugu.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class PosterRenderer {
    public interface PosterCallback {
        void onPosterReady(Bitmap bitmap);
    }

    public static void createPoster(Context context,
                                    String name,
                                    String designation,
                                    String imageUrl,
                                    PosterCallback callback) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.poster_layout, null, false);

        TextView txtName = view.findViewById(R.id.txtName);
        TextView txtDesignation = view.findViewById(R.id.txtDesignation);
        CircleImageView profile = view.findViewById(R.id.profileImage);

        txtName.setText(name);
        txtDesignation.setText(designation);

        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {

                        profile.setImageBitmap(resource);

                        view.measure(
                                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY));

                        view.layout(0,0,1080,300);

                        Bitmap bitmap = Bitmap.createBitmap(
                                1080,
                                300,
                                Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(bitmap);
                        view.draw(canvas);

                        callback.onPosterReady(bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable android.graphics.drawable.Drawable placeholder) {

                    }
                });

    }

    private static Bitmap createBitmap(View view) {

        view.measure(
                View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(300, View.MeasureSpec.EXACTLY));

        view.layout(0, 0, 1080, 300);

        Bitmap bitmap = Bitmap.createBitmap(
                1080,
                300,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        view.draw(canvas);

        return bitmap;
    }

    public static File saveBitmap(Context context, Bitmap bitmap)
            throws IOException {

        File file = new File(context.getCacheDir(), "poster.png");

        FileOutputStream out = new FileOutputStream(file);

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        out.flush();
        out.close();

        return file;
    }
}

