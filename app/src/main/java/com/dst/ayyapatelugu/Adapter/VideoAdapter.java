package com.dst.ayyapatelugu.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.content.FileProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer; //  Media3 ExoPlayer
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.PostVideosActivity;
import com.dst.ayyapatelugu.Activity.UploadDetailsActivity;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.Model.VideoModel;
import com.dst.ayyapatelugu.R;
import com.dst.ayyapatelugu.Services.VideoProcessor;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private final Context context;
    private final List<VideoModel> list;
    private final String baseUrl;
    private final VideoProcessor videoProcessor;

    public VideoAdapter(PostVideosActivity activity, List<VideoModel> list, String baseUrl) {
        this.context = activity;
        this.list = list;
        this.baseUrl = baseUrl;
        this.videoProcessor = new VideoProcessor(context);
    }

    @OptIn(markerClass = UnstableApi.class)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        VideoModel item = list.get(position);
        String videoUrlStr = baseUrl + item.getVideo();

        SharedPrefManager pref = SharedPrefManager.getInstance(context);
        String flyerName = pref.getFlyerName();
        String flyerDesignation = pref.getFlyerDesignation();
        String flyerPic = pref.getFlyerPic();

        boolean hasData = flyerName != null && !flyerName.trim().isEmpty()
                && flyerDesignation != null && !flyerDesignation.trim().isEmpty();

        holder.bind(videoUrlStr, hasData, flyerName, flyerDesignation, flyerPic);

        String imagePathToProcess = flyerPic;
        if (flyerPic != null && !flyerPic.trim().isEmpty() && !flyerPic.startsWith("http://") && !flyerPic.startsWith("https://")) {
            imagePathToProcess = baseUrl + flyerPic;
        } else if (flyerPic == null || flyerPic.trim().isEmpty()) {
            imagePathToProcess = "";
        }

        final String finalImagePath = imagePathToProcess;
        int myCardHeight = 220;

        // ─── ⬇ 1. SHARE BUTTON CLICK WITH CUSTOM DIALOG ───
        holder.imageShare.setOnClickListener(v -> {
            if (!hasData) {
                Toast.makeText(context, "Please Upload Details First", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🛑 మీ ఇమేజ్ లో లాంటి కస్టమ్ పాపప్ డైలాగ్ క్రియేషన్
            android.app.Dialog customDialog = new android.app.Dialog(context);

            // డైలాగ్ బ్యాక్‌గ్రౌండ్ రౌండ్ కార్నర్స్ అండ్ వైట్ కలర్ కోసం
            android.graphics.drawable.GradientDrawable dialogBg = new android.graphics.drawable.GradientDrawable();
            dialogBg.setColor(android.graphics.Color.WHITE);
            dialogBg.setCornerRadius(30f); // గుండ్రటి అంచులు
            if (customDialog.getWindow() != null) {
                customDialog.getWindow().setBackgroundDrawable(dialogBg);
            }

            // డైనమిక్ గా లేఅవుట్ క్రియేట్ చేయడం
            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(60, 60, 60, 60);
            layout.setGravity(android.view.Gravity.CENTER);

            // టెక్స్ట్ వ్యూ (వీడియోను ప్రాసెస్ అవుతుంది...)
            android.widget.TextView textView = new android.widget.TextView(context);
            textView.setText("వీడియోను ప్రాసెస్ అవుతుంది...");
            textView.setTextColor(android.graphics.Color.parseColor("#222222"));
            textView.setTextSize(18f);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            textView.setGravity(android.view.Gravity.CENTER);
            layout.addView(textView);

            // హరిజాంటల్ ప్రోగ్రెస్ బార్ (ఆరెంజ్ కలర్ లోడింగ్ బార్)
            android.widget.ProgressBar progressBar = new android.widget.ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setIndeterminate(true);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 40, 0, 10);
            progressBar.setLayoutParams(lp);

            // బార్ కలర్ ను ఆరెంజ్ (#FF6600) లోకి మార్చడం
            progressBar.getIndeterminateDrawable().setColorFilter(android.graphics.Color.parseColor("#FF6600"), android.graphics.PorterDuff.Mode.SRC_IN);
            layout.addView(progressBar);

            customDialog.setContentView(layout);
            customDialog.setCancelable(false);

            // డైలాగ్ వెడల్పు స్క్రీన్ సైజ్ కి తగ్గట్టు సెట్ చేయడం
            if (customDialog.getWindow() != null) {
                customDialog.getWindow().setLayout((int) (context.getResources().getDisplayMetrics().widthPixels * 0.85), android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            customDialog.show();

            File cacheDir = context.getCacheDir();
            File outputShareFile = new File(cacheDir, "shared_branded_video_" + System.currentTimeMillis() + ".mp4");

            videoProcessor.mixVideoAndPoster(
                    Uri.parse(videoUrlStr),
                    finalImagePath,
                    R.drawable.userimage,
                    outputShareFile,
                    flyerName,
                    flyerDesignation,
                    myCardHeight,
                    () -> {
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {
                                // ప్రాసెస్ పూర్తి కాగానే కస్టమ్ డైలాగ్ క్లోజ్ అవుతుంది
                                if (customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }
                                startShareIntentAfterProcessing(outputShareFile);
                            });
                        }
                    }
            );
        });

        // ─── ⬇ 2. DOWNLOAD BUTTON CLICK WITH CUSTOM DIALOG ───
        holder.imageDownload.setOnClickListener(v -> {
            if (!hasData) {
                Toast.makeText(context, "Please Upload Details First", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🛑 మీ ఇమేజ్ లో లాంటి కస్టమ్ పాపప్ డైలాగ్ క్రియేషన్
            android.app.Dialog customDialog = new android.app.Dialog(context);
            android.graphics.drawable.GradientDrawable dialogBg = new android.graphics.drawable.GradientDrawable();
            dialogBg.setColor(android.graphics.Color.WHITE);
            dialogBg.setCornerRadius(30f);
            if (customDialog.getWindow() != null) {
                customDialog.getWindow().setBackgroundDrawable(dialogBg);
            }

            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(60, 60, 60, 60);
            layout.setGravity(android.view.Gravity.CENTER);

            android.widget.TextView textView = new android.widget.TextView(context);
            textView.setText("వీడియోను ప్రాసెస్ అవుతుంది...");
            textView.setTextColor(android.graphics.Color.parseColor("#222222"));
            textView.setTextSize(18f);
            textView.setTypeface(null, android.graphics.Typeface.BOLD);
            textView.setGravity(android.view.Gravity.CENTER);
            layout.addView(textView);

            android.widget.ProgressBar progressBar = new android.widget.ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
            progressBar.setIndeterminate(true);
            android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 40, 0, 10);
            progressBar.setLayoutParams(lp);
            progressBar.getIndeterminateDrawable().setColorFilter(android.graphics.Color.parseColor("#FF6600"), android.graphics.PorterDuff.Mode.SRC_IN);
            layout.addView(progressBar);

            customDialog.setContentView(layout);
            customDialog.setCancelable(false);

            if (customDialog.getWindow() != null) {
                customDialog.getWindow().setLayout((int) (context.getResources().getDisplayMetrics().widthPixels * 0.85), android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            customDialog.show();

            File cacheDir = context.getCacheDir();
            File tempOutputFile = new File(cacheDir, "temp_downloaded_video_" + System.currentTimeMillis() + ".mp4");

            videoProcessor.mixVideoAndPoster(
                    Uri.parse(videoUrlStr),
                    finalImagePath,
                    R.drawable.userimage,
                    tempOutputFile,
                    flyerName,
                    flyerDesignation,
                    myCardHeight,
                    () -> {
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {

                                if (customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }

                                try {
                                    android.content.ContentValues values = new android.content.ContentValues();
                                    String fileName = "AyyappaTelugu_" + System.currentTimeMillis() + ".mp4";
                                    values.put(android.provider.MediaStore.Video.Media.DISPLAY_NAME, fileName);
                                    values.put(android.provider.MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                                    values.put(android.provider.MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                        values.put(android.provider.MediaStore.Video.Media.RELATIVE_PATH, "Movies/AyyappaTelugu");
                                        values.put(android.provider.MediaStore.Video.Media.IS_PENDING, 1);
                                    } else {
                                        File publicDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_MOVIES);
                                        File customFolder = new File(publicDir, "AyyappaTelugu");
                                        if (!customFolder.exists()) customFolder.mkdirs();
                                        File finalFile = new File(customFolder, fileName);
                                        values.put(android.provider.MediaStore.Video.Media.DATA, finalFile.getAbsolutePath());
                                    }

                                    Uri collection = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                    Uri videoTableUri = context.getContentResolver().insert(collection, values);

                                    if (videoTableUri != null) {
                                        try (java.io.InputStream in = new java.io.FileInputStream(tempOutputFile);
                                             java.io.OutputStream out = context.getContentResolver().openOutputStream(videoTableUri)) {
                                            byte[] buf = new byte[4096];
                                            int len;
                                            while ((len = in.read(buf)) > 0) {
                                                out.write(buf, 0, len);
                                            }
                                        }

                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                            values.clear();
                                            values.put(android.provider.MediaStore.Video.Media.IS_PENDING, 0);
                                            context.getContentResolver().update(videoTableUri, values, null, null);
                                        } else {
                                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                            mediaScanIntent.setData(videoTableUri);
                                            context.sendBroadcast(mediaScanIntent);
                                        }

                                        Toast.makeText(context, "వీడియో విజయవంతంగా గ్యాలరీ లో సేవ్ చేయబడింది!", Toast.LENGTH_LONG).show();
                                    }

                                    if (tempOutputFile.exists()) {
                                        tempOutputFile.delete();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "గ్యాలరీ సేవ్ లోపం: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
            );
        });
        if (holder.btnUploadDetails != null) {
            holder.btnUploadDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, UploadDetailsActivity.class);
                intent.putExtra("from_activity", "post_videos"); // 🛑 ఇది పెట్టాలి
                context.startActivity(intent);
            });
        }
    }
    private void startShareIntentAfterProcessing(File file) {
        if (file != null && file.exists()) {
            try {
                // FileProvider ద్వారా సురక్షితమైన URI ని పొందడం
                Uri uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider", // మీ ప్యాకేజీ నేమ్ + .provider
                        file
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/mp4"); // నిర్దిష్టంగా వీడియో టైప్ చెప్పడం మంచిది
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // పర్మిషన్ గ్రాంట్ చేయడం ముఖ్యం

                // షేర్ విండో ఓపెన్ అవుతుంది
                context.startActivity(Intent.createChooser(shareIntent, "Share Video Via"));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "షేరింగ్ లో లోపం జరిగింది: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "ఫైల్ క్రియేట్ అవ్వలేదు!", Toast.LENGTH_SHORT).show();
        }
    }
    public void addData(List<VideoModel> newList) {
        int start = list.size();
        list.addAll(newList);
        notifyItemRangeInserted(start, newList.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<VideoModel> newList) {
        this.list.clear();
        this.list.addAll(newList);
        notifyDataSetChanged();
    }

    // ─── VIEW HOLDER CLASS ──────────────────────────────────────────────────
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        public ExoPlayer player; //  androidx.media3.exoplayer.ExoPlayer గా మార్చబడింది
        ImageView imageShare, imageDownload, imgPoster;
        TextView txtName, txtDesignation;
        CircleImageView profileImage;
        Button btnUploadDetails;

        @OptIn(markerClass = UnstableApi.class)
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            imageShare = itemView.findViewById(R.id.btnShare);
            imageDownload = itemView.findViewById(R.id.btnDownload);
            imgPoster = itemView.findViewById(R.id.imgPoster);

            txtName = itemView.findViewById(R.id.txtName);
            txtDesignation = itemView.findViewById(R.id.txtDesignation);
            profileImage = itemView.findViewById(R.id.profileImage);
            btnUploadDetails = itemView.findViewById(R.id.btnUploadDetails);

            playerView.setUseController(true);
            playerView.setControllerAutoShow(true);
            playerView.setControllerHideOnTouch(false);
            playerView.setControllerShowTimeoutMs(1000);
        }

        void bind(String videoUrl, boolean hasData, String flyerName, String flyerDesignation, String flyerPic) {
                if (player != null) {
                    player.release();
                    player = null;
                }

                imgPoster.setVisibility(View.VISIBLE);

                Glide.with(itemView.getContext())
                        .load(videoUrl)
                        .into(imgPoster);

                // 🛑 ఇక్కడ మార్చాము: flyerPic ఖాళీగా ఉంటే బటన్ రాకుండా డిఫాల్ట్ ఇమేజ్ సెట్ అవుతుంది
                if (hasData) {
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
                        // ఇమేజ్ ఉంటే లోడ్ అవుతుంది, లేదంటే డిఫాల్ట్ బొమ్మ వస్తుంది
                        if (flyerPic != null && !flyerPic.trim().isEmpty()) {
                            Glide.with(itemView.getContext()).load(flyerPic).into(profileImage);
                        } else {
                            profileImage.setImageResource(R.drawable.userimage);
                        }
                    }
                    if (btnUploadDetails != null) {
                        btnUploadDetails.setVisibility(View.GONE); // బటన్ మాయం అవుతుంది
                    }
                } else {
                    if (btnUploadDetails != null) {
                        btnUploadDetails.setVisibility(View.VISIBLE); // డేటా లేకపోతేనే బటన్ వస్తుంది
                    }
                    if (txtName != null) txtName.setVisibility(View.GONE);
                    if (txtDesignation != null) txtDesignation.setVisibility(View.GONE);
                    if (profileImage != null) {
                        profileImage.setVisibility(View.VISIBLE);
                        profileImage.setImageResource(R.drawable.userimage);
                    }
                }

                // ExoPlayer కోడ్ యథాతథంగా ఉంటుంది...
                player = new ExoPlayer.Builder(itemView.getContext()).build();
                playerView.setPlayer(player);

                MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                player.setMediaItem(mediaItem);
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
                player.setVolume(1f);
                player.prepare();
                player.pause();

                player.addListener(new Player.Listener() {
                    @Override
                    public void onIsPlayingChanged(boolean isPlaying) {
                        if (isPlaying) {
                            imgPoster.setVisibility(View.GONE);
                        }
                    }
                });
            }
    }
    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.player != null) {
            holder.player.release();
            holder.player = null;
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.player != null) {
            holder.player.pause();
        }
    }
}