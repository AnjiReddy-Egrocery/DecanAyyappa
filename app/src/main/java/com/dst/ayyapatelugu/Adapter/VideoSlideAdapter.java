package com.dst.ayyapatelugu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dst.ayyapatelugu.Activity.UploadDetailsActivity;
import com.dst.ayyapatelugu.DataBase.SharedPrefManager;
import com.dst.ayyapatelugu.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoSlideAdapter extends RecyclerView.Adapter<VideoSlideAdapter.VH> {

    Context context;
    String videoUrl;
    String flyerName;
    String flyerDesignation;
    String flyerPic;

    public VideoSlideAdapter(Context context,
                             String videoUrl,
                             String flyerName,
                             String flyerDesignation,
                             String flyerPic) {

        this.context = context;
        this.videoUrl = videoUrl;
        this.flyerName = flyerName;
        this.flyerDesignation = flyerDesignation;
        this.flyerPic = flyerPic;
    }

    @Override
    public int getItemCount() {
        return 3; // 🔥 3 slides
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layout;

        if (viewType == 0) {
            layout = R.layout.video_slide1;
        } else if (viewType == 1) {
            layout = R.layout.video_slide2;
        } else {
            layout = R.layout.video_slide3;
        }

        View v = LayoutInflater.from(context).inflate(layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        holder.bind(videoUrl);

        PlayerView playerView = holder.itemView.findViewById(R.id.playerView);

        ExoPlayer player = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));

        player.setMediaItem(mediaItem);
        player.prepare();
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.setVolume(0f);
        player.pause();

        playerView.setUseController(false);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(com.google.android.exoplayer2.PlaybackException error) {
                Log.e("EXO_ERROR", "Video error: " + error.getMessage());
            }
        });

        TextView txtName = holder.itemView.findViewById(R.id.txtName);
        TextView txtDesignation = holder.itemView.findViewById(R.id.txtDesignation);
        CircleImageView profileImage = holder.itemView.findViewById(R.id.profileImage);
        Button btnUploadDetails = holder.itemView.findViewById(R.id.btnUploadDetails);

        SharedPrefManager pref = SharedPrefManager.getInstance(context);

        String flyerName = pref.getFlyerName();
        String flyerDesignation = pref.getFlyerDesignation();
        String flyerPic = pref.getFlyerPic();

        boolean hasData =
                flyerName != null && !flyerName.trim().isEmpty()
                        && flyerDesignation != null && !flyerDesignation.trim().isEmpty()
                        && flyerPic != null && !flyerPic.trim().isEmpty();

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

            if (btnUploadDetails != null) {
                btnUploadDetails.setVisibility(View.VISIBLE);
            }

            if (txtName != null) txtName.setVisibility(View.GONE);
            if (txtDesignation != null) txtDesignation.setVisibility(View.GONE);
            if (profileImage != null) profileImage.setVisibility(View.VISIBLE);
        }


        if (btnUploadDetails != null) {
            btnUploadDetails.setOnClickListener(v -> {
                Intent intent = new Intent(context, UploadDetailsActivity.class);
                context.startActivity(intent);
            });
        }
    }

    public static class VH extends RecyclerView.ViewHolder {
        RecyclerView recyclerSlider;
        LinearLayout layoutDots;
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        PlayerView playerView;
        ExoPlayer player;
        public VH(@NonNull View itemView) {
            super(itemView);

            recyclerSlider = itemView.findViewById(R.id.recyclerSlider);
            layoutDots = itemView.findViewById(R.id.layoutDots);

            snapHelper.attachToRecyclerView(recyclerSlider);

            playerView = itemView.findViewById(R.id.playerView);

            player = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(player);
        }

        void bind(String url) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));

            player.setMediaItem(mediaItem);
            player.prepare();
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
            player.setVolume(0f);
            player.pause();
        }
    }


}
