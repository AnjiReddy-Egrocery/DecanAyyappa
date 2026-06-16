package com.dst.ayyapatelugu.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.ayyapatelugu.Activity.PostVideosActivity;
import com.dst.ayyapatelugu.Model.VideoModel;
import com.dst.ayyapatelugu.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder>{
    Context context;
    List<VideoModel> list;
    String baseUrl;



    public VideoAdapter(PostVideosActivity imagesListActivity, List<VideoModel> list, String baseUrl) {
        this.context = imagesListActivity;
        this.list = list;
        this.baseUrl = baseUrl;
    }

    @NonNull
    @Override
    public VideoAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);
        return new VideoAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.MyViewHolder holder, int position) {

        String url = baseUrl + list.get(position).getVideo();

        holder.bind(url);

        holder.imageShare.setOnClickListener(v -> {
            new Thread(() -> {
                try {

                    URL videoUrl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) videoUrl.openConnection();
                    connection.connect();

                    InputStream input = connection.getInputStream();

                    File file = new File(context.getCacheDir(), "share_video.mp4");
                    FileOutputStream output = new FileOutputStream(file);

                    byte[] buffer = new byte[1024];
                    int len;

                    while ((len = input.read(buffer)) > 0) {
                        output.write(buffer, 0, len);
                    }

                    output.close();
                    input.close();

                    Uri uri = FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".provider",
                            file
                    );

                    ((Activity) context).runOnUiThread(() -> {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        context.startActivity(Intent.createChooser(shareIntent, "Share Video"));
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });

        // ⬇ DOWNLOAD
        holder.imageDownload.setOnClickListener(v -> {
            downloadVideo(url);
        });







     /*   holder.img.setOnClickListener(v -> {

            Intent intent = new Intent(context, FullImageActivity.class);
            intent.putExtra("image", fullUrl); // full image URL
            context.startActivity(intent);
        });*/
    }

    public void addData(List<VideoModel> newList) {
        int start = list.size();
        list.addAll(newList);
        notifyItemRangeInserted(start, newList.size());
    }

    private void downloadVideo(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading video...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "video_" + System.currentTimeMillis() + ".mp4"
        );

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);

        Toast.makeText(context, "Download started...", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        public ExoPlayer player;
        ImageView imageShare,imageDownload;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            imageShare = itemView.findViewById(R.id.btnShare);
            imageDownload = itemView.findViewById(R.id.btnDownload);




            playerView.setUseController(true);

            playerView.setControllerAutoShow(true);

            playerView.setControllerHideOnTouch(false);

            playerView.setControllerShowTimeoutMs(1000);
        }

        void bind(String url) {

            // RELEASE OLD PLAYER
            if (player != null) {

                player.release();
            }

            player =
                    new ExoPlayer.Builder(context).build();

            playerView.setPlayer(player);

            MediaItem mediaItem =
                    MediaItem.fromUri(Uri.parse(url));

            player.setMediaItem(mediaItem);

            // LOOP VIDEO
            player.setRepeatMode(Player.REPEAT_MODE_ONE);

            // VOLUME
            player.setVolume(1f);

            // PREPARE
            player.prepare();

            // DON'T AUTO PLAY HERE
            player.pause();

            // KEEP LAST FRAME
            playerView.setKeepContentOnPlayerReset(true);

            // PLAYER LISTENER
            player.addListener(new Player.Listener() {

                @Override
                public void onPlaybackStateChanged(
                        int playbackState
                ) {

                    if (playbackState
                            == Player.STATE_READY) {

                        // READY
                    }
                }
            });
        }
    }

    @Override
    public void onViewRecycled(
            @NonNull MyViewHolder holder
    ) {

        super.onViewRecycled(holder);

        if (holder.player != null) {

            holder.player.release();

            holder.player = null;
        }
    }

    @Override
    public void onViewDetachedFromWindow(
            @NonNull MyViewHolder holder
    ) {

        super.onViewDetachedFromWindow(holder);

        if (holder.player != null) {

            holder.player.pause();
        }
    }
}