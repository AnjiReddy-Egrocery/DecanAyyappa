package com.dst.ayyapatelugu.Adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.FullImageActivity;
import com.dst.ayyapatelugu.Activity.ImagesListActivity;
import com.dst.ayyapatelugu.Model.ImagesModel;
import com.dst.ayyapatelugu.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
    Context context;
    List<ImagesModel> list;
    String baseUrl;

    public ImageAdapter(ImagesListActivity imagesListActivity, List<ImagesModel> list, String baseUrl) {
        this.context = imagesListActivity;
        this.list = list;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_image, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {
        ImagesModel model = list.get(position);
        String fullUrl = baseUrl + model.getImage();
        Glide.with(context)
                .load(fullUrl)
                .into(holder.img);

        holder.btnShare.setOnClickListener(v -> {
            shareImage(fullUrl);
        });

        // DOWNLOAD
        holder.btnDownload.setOnClickListener(v -> {
            downloadImage(fullUrl, model.getImage());
        });

     /*   holder.img.setOnClickListener(v -> {

            Intent intent = new Intent(context, FullImageActivity.class);
            intent.putExtra("image", fullUrl); // full image URL
            context.startActivity(intent);
        });*/
    }

    private void downloadImage(String fullUrl, String image) {
        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {

                InputStream input = new java.net.URL(fullUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                saveToGallery(bitmap, image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void saveToGallery(Bitmap bitmap, String name) {
        OutputStream fos;

        try {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Ayyappa");

                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(imageUri);

            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).toString();

                File image = new File(imagesDir, name);
                fos = new FileOutputStream(image);
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void shareImage(String fullUrl) {
        new Thread(() -> {
            try {
                InputStream input = new java.net.URL(fullUrl).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                Uri uri = saveImageAndGetUri(bitmap);

                ((Activity) context).runOnUiThread(() -> {

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(Intent.createChooser(intent, "Share Image"));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private Uri saveImageAndGetUri(Bitmap bitmap) {
        File cachePath = new File(context.getCacheDir(), "images");
        cachePath.mkdirs();

        File file = new File(cachePath, "shared_image.png");

        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                file
        );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ImageView btnShare, btnDownload;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
