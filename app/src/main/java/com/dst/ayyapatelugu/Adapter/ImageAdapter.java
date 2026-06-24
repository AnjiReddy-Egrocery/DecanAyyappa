package com.dst.ayyapatelugu.Adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.FullImageActivity;
import com.dst.ayyapatelugu.Activity.ImagesListActivity;
import com.dst.ayyapatelugu.Model.ImagesModel;
import com.dst.ayyapatelugu.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
    Context context;
    List<ImagesModel> list;
    String baseUrl;

    String flyerName;
    String flyerDesignation;
    String flyerPic;

    public ImageAdapter(Context context,
                        List<ImagesModel> list,
                        String baseUrl,
                        String flyerName,
                        String flyerDesignation,
                        String flyerPic) {

        this.context = context;
        this.list = list;
        this.baseUrl = baseUrl;
        this.flyerName = flyerName;
        this.flyerDesignation = flyerDesignation;
        this.flyerPic = flyerPic;
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


        Log.d("CHECK",
                "POSITION=" + position);

        ImagesModel model = list.get(position);

        String imageUrl =
                baseUrl + model.getImage();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL,
                        false);

        holder.recyclerSlider.setLayoutManager(
                layoutManager);

        PosterPagerAdapter sliderAdapter =
                new PosterPagerAdapter(
                        context,
                        imageUrl,
                        flyerName,
                        flyerDesignation,
                        flyerPic
                );

        holder.recyclerSlider.setAdapter(
                sliderAdapter);

        PagerSnapHelper snapHelper =
                new PagerSnapHelper();

        if (holder.recyclerSlider.getOnFlingListener() == null) {
            snapHelper.attachToRecyclerView(
                    holder.recyclerSlider
            );
        }

        createDots(holder.layoutDots, 0);

        holder.recyclerSlider.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(
                            @NonNull RecyclerView recyclerView,
                            int newState) {

                        super.onScrollStateChanged(
                                recyclerView,
                                newState);

                        if (newState ==
                                RecyclerView.SCROLL_STATE_IDLE) {

                            View centerView =
                                    snapHelper.findSnapView(
                                            layoutManager);

                            if (centerView != null) {

                                int pos =
                                        layoutManager.getPosition(
                                                centerView);

                                createDots(
                                        holder.layoutDots,
                                        pos);
                            }
                        }
                    }
                });
    }

    private void createDots(
            LinearLayout layoutDots,
            int selectedPos){

        layoutDots.removeAllViews();

        for(int i=0;i<3;i++){

            ImageView dot =
                    new ImageView(context);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            25,
                            25);

            params.setMargins(
                    8,
                    0,
                    8,
                    0);

            dot.setLayoutParams(params);

            if(i==selectedPos){
                dot.setImageResource(
                        R.drawable.selected_dot);
            }else{
                dot.setImageResource(
                        R.drawable.dot);
            }

            layoutDots.addView(dot);
        }
    }

   /* private void downloadCard(MyViewHolder holder) {

        holder.btnShare.setVisibility(View.GONE);
        holder.btnDownload.setVisibility(View.GONE);
        try {

            Bitmap bitmap = Bitmap.createBitmap(
                    holder.itemView.getWidth(),
                    holder.itemView.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);

            holder.itemView.draw(canvas);

            saveToGallery(
                    bitmap,
                    "Ayyappa_" + System.currentTimeMillis() + ".jpg"
            );

            Toast.makeText(
                    context,
                    "Card Downloaded",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {
            holder.btnShare.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.VISIBLE);

            e.printStackTrace();
            e.printStackTrace();
        }
    }

    private void saveToGallery(Bitmap bitmap, String name) {

        try {

            OutputStream fos;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContentResolver resolver =
                        context.getContentResolver();

                ContentValues values =
                        new ContentValues();

                values.put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        name
                );

                values.put(
                        MediaStore.Images.Media.MIME_TYPE,
                        "image/jpeg"
                );

                values.put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/Ayyappa"
                );

                Uri uri = resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );

                fos = resolver.openOutputStream(uri);

            } else {

                File dir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                        ),
                        "Ayyappa"
                );

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(dir, name);

                fos = new FileOutputStream(file);
            }

            bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    fos
            );

            fos.flush();
            fos.close();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void shareCard(MyViewHolder holder) {
        holder.btnShare.setVisibility(View.GONE);
        holder.btnDownload.setVisibility(View.GONE);
        try {

            Bitmap bitmap = Bitmap.createBitmap(
                    holder.itemView.getWidth(),
                    holder.itemView.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);

            holder.itemView.draw(canvas);

            Uri uri = saveImageAndGetUri(bitmap);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(
                    Intent.createChooser(intent, "Share")
            );

        } catch (Exception e) {
            holder.btnShare.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.VISIBLE);

            e.printStackTrace();

        }
    }*/




/*    private Uri saveImageAndGetUri(Bitmap bitmap) {
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
    }*/

    @Override
    public int getItemCount() {
        Log.d("ADAPTER_COUNT",
                "COUNT = " + list.size());
        return list.size();
    }

    public void addData(List<ImagesModel> newList) {
        int start = list.size();

        list.addAll(newList);

        notifyItemRangeInserted(
                start,
                newList.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerSlider;
        LinearLayout layoutDots;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerSlider =
                    itemView.findViewById(R.id.recyclerSlider);

            layoutDots =
                    itemView.findViewById(R.id.layoutDots);
        }
    }
}
