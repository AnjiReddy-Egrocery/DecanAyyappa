package com.dst.ayyapatelugu.Adapter;

import static com.ibm.icu.text.CurrencyMetaInfo.hasData;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.UploadDetailsActivity;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.R;

import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PosterPagerAdapter  extends RecyclerView.Adapter<PosterPagerAdapter.ViewHolder> {

    Context context;
    String imageUrl;
    String flyerName;
    String flyerDesignation;
    String flyerPic;

    public PosterPagerAdapter(Context context,
                              String imageUrl,
                              String flyerName,
                              String flyerDesignation,
                              String flyerPic) {

        this.context = context;
        this.imageUrl = imageUrl;
        this.flyerName = flyerName;
        this.flyerDesignation = flyerDesignation;
        this.flyerPic = flyerPic;
    }

    @Override
    public int getItemCount() {
        Log.d("POSTER", "getItemCount");
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout;

        if (viewType == 0) {
            layout = R.layout.poster_slide1;
        } else if (viewType == 1) {
            layout = R.layout.poster_slide2;
        } else {
            layout = R.layout.poster_slide3;
        }

        return new ViewHolder(
                LayoutInflater.from(context).inflate(layout, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("POSTER", "onBindViewHolder = " + position);

        ImageView img = holder.itemView.findViewById(R.id.img);

        TextView txtName = holder.itemView.findViewById(R.id.txtName);
        TextView txtDesignation = holder.itemView.findViewById(R.id.txtDesignation);
        CircleImageView profileImage = holder.itemView.findViewById(R.id.profileImage);

        Button btnUploadDetails =
                holder.itemView.findViewById(R.id.btnUploadDetails);

        ImageView btnShare =
                holder.itemView.findViewById(R.id.btnShare);

        ImageView btnDownload =
                holder.itemView.findViewById(R.id.btnDownload);


        // 👉 GET DATA FROM SHARED PREF
        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        String flyerName = pref.getFlyerName();
        String flyerDesignation = pref.getFlyerDesignation();
        String flyerPic = pref.getFlyerPic();

        Log.d("GLIDE_CHECK", "flyerPic = " + flyerPic);
        Log.d("GLIDE_CHECK", "imageUrl = " + imageUrl);

        // TEXT
        Glide.with(context)
                .load(imageUrl)
                .into(img);

        boolean hasData =
                flyerName != null && !flyerName.trim().isEmpty()
                        && flyerDesignation != null && !flyerDesignation.trim().isEmpty()
                        && flyerPic != null && !flyerPic.trim().isEmpty();

        btnDownload.setOnClickListener(v -> {

            if (!hasData) {

                Toast.makeText(
                        context,
                        "Please Upload Details First",
                        Toast.LENGTH_SHORT
                ).show();



                return;
            }

            downloadPoster(holder.itemView);
        });

        btnShare.setOnClickListener(v -> {

            if (!hasData) {

                Toast.makeText(
                        context,
                        "Please Upload Details First",
                        Toast.LENGTH_SHORT
                ).show();



                return;
            }

            sharePoster(holder.itemView);
        });

        if (hasData) {

            // 👉 SHOW DETAILS
            if (txtName != null) {
                txtName.setVisibility(View.VISIBLE);
                txtName.setText(flyerName);
            }

            if (txtDesignation != null) {
                txtDesignation.setVisibility(View.VISIBLE);
                txtDesignation.setText(flyerDesignation);
            }

            if (profileImage != null) {
                profileImage.setVisibility(View.VISIBLE);
                Glide.with(context).load(flyerPic).into(profileImage);
            }

            // 👉 HIDE BUTTON
            if (btnUploadDetails != null) {
                btnUploadDetails.setVisibility(View.GONE);
            }

        } else {

            // 👉 NO DATA → SHOW BUTTON
            if (btnUploadDetails != null) {
                btnUploadDetails.setVisibility(View.VISIBLE);
            }

            if (txtName != null) txtName.setVisibility(View.GONE);
            if (txtDesignation != null) txtDesignation.setVisibility(View.GONE);
            if (profileImage != null) profileImage.setVisibility(View.VISIBLE);
        }

        // BUTTON CLICK (only if visible)
        if (btnUploadDetails != null) {
            btnUploadDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, UploadDetailsActivity.class);
                intent.putExtra("from_activity", "images_list"); // 🛑 ఇది పెట్టాలి
                context.startActivity(intent);
            });
        }
    }

    private void sharePoster(View itemView) {

        try {

            ImageView btnShare = itemView.findViewById(R.id.btnShare);
            ImageView btnDownload = itemView.findViewById(R.id.btnDownload);

            // hide icons
            btnShare.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);


            Bitmap bitmap = Bitmap.createBitmap(
                    itemView.getWidth(),
                    itemView.getHeight(),
                    Bitmap.Config.ARGB_8888
            );

            Canvas canvas = new Canvas(bitmap);
            itemView.draw(canvas);


            // show icons back
            btnShare.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.VISIBLE);


            File cachePath =
                    new File(context.getCacheDir(), "images");

            cachePath.mkdirs();


            File file =
                    new File(cachePath, "shared_poster.png");


            FileOutputStream stream =
                    new FileOutputStream(file);


            bitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    stream
            );

            stream.close();


            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );


            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);

            shareIntent.setType("image/*");

            shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    uri
            );

            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
            );


            context.startActivity(
                    Intent.createChooser(
                            shareIntent,
                            "Share Poster"
                    )
            );


        } catch(Exception e){

            e.printStackTrace();

            Toast.makeText(
                    context,
                    "Share Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void downloadPoster(View itemView) {
        try {

            ImageView btnShare = itemView.findViewById(R.id.btnShare);
            ImageView btnDownload = itemView.findViewById(R.id.btnDownload);


            // hide icons
            btnShare.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);



            Bitmap bitmap = Bitmap.createBitmap(
                    itemView.getWidth(),
                    itemView.getHeight(),
                    Bitmap.Config.ARGB_8888
            );


            Canvas canvas = new Canvas(bitmap);
            itemView.draw(canvas);



            // show icons back
            btnShare.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.VISIBLE);



            MediaStore.Images.Media.insertImage(
                    context.getContentResolver(),
                    bitmap,
                    "Poster_" + System.currentTimeMillis(),
                    "Ayyappa Poster"
            );


            Toast.makeText(
                    context,
                    "Poster Downloaded Successfully",
                    Toast.LENGTH_SHORT
            ).show();


        } catch(Exception e){

            e.printStackTrace();

            Toast.makeText(
                    context,
                    "Download Failed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
