package com.dst.ayyapatelugu.Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.effect.MatrixTransformation;
import androidx.media3.effect.Presentation;
import androidx.media3.effect.ScaleAndRotateTransformation;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.effect.BitmapOverlay;
import androidx.media3.effect.OverlayEffect;

import androidx.media3.effect.StaticOverlaySettings;
import androidx.media3.transformer.Composition;
import androidx.media3.transformer.EditedMediaItem;
import androidx.media3.transformer.EditedMediaItemSequence;
import androidx.media3.transformer.Effects;
import androidx.media3.transformer.ExportException;
import androidx.media3.transformer.ExportResult;
import androidx.media3.transformer.Transformer;

import com.google.common.collect.ImmutableList;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

@OptIn(markerClass = UnstableApi.class)
    public class VideoProcessor {

    private final Context context;

    public VideoProcessor(Context context) {
        this.context = context;
    }

    public void mixVideoAndPoster(Uri videoUri, String imagePathOrUrl, int defaultDrawableId, File outputFile, String flyerName, String flyerDesignation, int cardHeight, Runnable onSuccessCallback) {

        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, "వీడియో మరియు స్ట్రిప్ మెర్జ్ అవుతున్నాయి...", Toast.LENGTH_SHORT).show()
        );

        new Thread(() -> {
            File tempInputFile = null;
            File tempUserImageFile = null;
            String finalLocalImagePath = "";

            try {
                // 1. వీడియో డౌన్‌లోడ్
                tempInputFile = new File(context.getCacheDir(), "temp_input_video_" + System.currentTimeMillis() + ".mp4");
                URL url = new URL(videoUri.toString());
                URLConnection connection = url.openConnection();
                connection.setConnectTimeout(8000);
                connection.connect();

                try (InputStream input = new BufferedInputStream(url.openStream(), 8192);
                     FileOutputStream output = new FileOutputStream(tempInputFile)) {
                    byte[] data = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                    }
                    output.flush();
                }

                // 2. యూజర్ ఇమేజ్ డౌన్‌లోడ్
                if (imagePathOrUrl != null && (imagePathOrUrl.startsWith("http://") || imagePathOrUrl.startsWith("https://"))) {
                    try {
                        tempUserImageFile = new File(context.getCacheDir(), "temp_user_img_" + System.currentTimeMillis() + ".png");
                        URL imgUrl = new URL(imagePathOrUrl);
                        URLConnection imgConn = imgUrl.openConnection();
                        imgConn.setConnectTimeout(5000);
                        try (InputStream in = new BufferedInputStream(imgConn.getInputStream());
                             FileOutputStream out = new FileOutputStream(tempUserImageFile)) {
                            byte[] buffer = new byte[2048];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                            out.flush();
                        }
                        finalLocalImagePath = tempUserImageFile.getAbsolutePath();
                    } catch (Exception imgEx) {
                        finalLocalImagePath = "";
                    }
                } else {
                    finalLocalImagePath = imagePathOrUrl;
                }

                // 3. వీడియో సైజ్ రీడింగ్
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(tempInputFile.getAbsolutePath());
                String widthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                int videoWidth = (widthStr != null) ? Integer.parseInt(widthStr) : 720;
                int videoHeight = (heightStr != null) ? Integer.parseInt(heightStr) : 1280;
                retriever.release();

                // స్ట్రిప్ సైజ్ లెక్క (24%) మరియు కొత్త టోటల్ హైట్
                int adjustedCardHeight = (int) (videoWidth * 0.24f);
                int totalNewHeight = videoHeight + adjustedCardHeight;

                // 🛑 మార్పు 1: కేవలం స్ట్రిప్ సైజ్ లోనే బిట్‌మ్యాప్ కార్డును క్రియేట్ చేస్తున్నాం (ఫుల్ సైజ్ కాదు)
                Bitmap brandingCard = createBrandingCard(videoWidth, adjustedCardHeight, flyerName, flyerDesignation, finalLocalImagePath, defaultDrawableId);

                // 🛑 మార్పు 2: కొత్త వెడల్పు మరియు ఎత్తుతో ప్రెజెంటేషన్ ఫ్రేమ్ క్రియేషన్
                Presentation presentation = Presentation.createForWidthAndHeight(
                        videoWidth,
                        totalNewHeight,
                        Presentation.LAYOUT_SCALE_TO_FIT
                );

// వీడియోను క్రాప్ చేయకుండా పై భాగానికి నెట్టే లాజిక్
                float transitionY = (float) adjustedCardHeight / totalNewHeight;
                MatrixTransformation videoMoveUp = new MatrixTransformation() {
                    @Override
                    public android.graphics.Matrix getMatrix(long presentationTimeUs) {
                        return new android.graphics.Matrix();
                    }

                    @Override
                    public float[] getGlMatrixArray(long presentationTimeUs) {
                        float[] glMatrix = new float[16];
                        android.opengl.Matrix.setIdentityM(glMatrix, 0);
                        android.opengl.Matrix.translateM(glMatrix, 0, 0.0f, transitionY, 0.0f);
                        return glMatrix;
                    }
                };

// స్ట్రిప్ కార్డును కరెక్ట్ గా బాటమ్ (కింద ఖాళీ స్థలం) లో కూర్చోబెట్టడం
                StaticOverlaySettings overlaySettings = new StaticOverlaySettings.Builder()
                        .setBackgroundFrameAnchor(0.0f, -1.0f)
                        .setOverlayFrameAnchor(0.0f, -1.0f)
                        .build();

                BitmapOverlay bitmapOverlay = BitmapOverlay.createStaticBitmapOverlay(brandingCard, overlaySettings);
                OverlayEffect overlayEffect = new OverlayEffect(ImmutableList.of(bitmapOverlay));

                MediaItem mediaItem = MediaItem.fromUri(Uri.fromFile(tempInputFile));

// ఎఫెక్ట్స్ ఆర్డర్
                Effects effects = new Effects(
                        ImmutableList.of(),
                        ImmutableList.of(presentation, videoMoveUp, overlayEffect)
                );

                EditedMediaItem editedMediaItem = new EditedMediaItem.Builder(mediaItem)
                        .setEffects(effects)
                        .build();

                final File finalTempFile = tempInputFile;
                final File finalTempImg = tempUserImageFile;

                // 4. ట్రాన్స్‌ఫార్మర్ రన్
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        Transformer transformer = new Transformer.Builder(context)
                                .setVideoMimeType(MimeTypes.VIDEO_H264)
                                .build();

                        transformer.start(editedMediaItem, outputFile.getAbsolutePath());
                        Log.d("VideoProcessor", "Rendering Started Smoothly!");

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (finalTempFile.exists()) finalTempFile.delete();
                            if (finalTempImg != null && finalTempImg.exists()) finalTempImg.delete();

                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                            }
                        }, 8000);

                    } catch (Exception e) {
                        Log.e("VideoProcessor", "Transformer error: " + e.getMessage());
                        if (finalTempFile.exists()) finalTempFile.delete();
                    }
                });

            } catch (Exception e) {
                Log.e("VideoProcessor", "Process Error: " + e.getMessage());
                if (tempInputFile != null && tempInputFile.exists()) tempInputFile.delete();
                if (tempUserImageFile != null && tempUserImageFile.exists()) tempUserImageFile.delete();
            }
        }).start();
    }

    private Bitmap createBrandingCard(int width, int height, String name, String des, String imgPath, int defaultId) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawColor(Color.parseColor("#FFFDF7")); // వైట్ బ్యాక్‌గ్రౌండ్

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // ఆరెంజ్ బోర్డర్ (పైన)
        paint.setColor(Color.parseColor("#FF6600"));
        canvas.drawRect(0, 0, width, height * 0.06f, paint);

        // ─── 1. కుడివైపు ప్రొఫైల్ ఫోటో (Right Side Flyer Pic) ───
        Bitmap userPhoto = null;
        try {
            if (imgPath != null && !imgPath.isEmpty()) {
                userPhoto = BitmapFactory.decodeFile(imgPath);
            }
        } catch (Exception e) {
            Log.e("VideoProcessor", "Img Decode fail");
        }
        if (userPhoto == null) {
            userPhoto = BitmapFactory.decodeResource(context.getResources(), defaultId);
        }

        int avatarSize = (int) (height * 0.75f);
        // 🛑 మార్పు: ఫోటోను కుడివైపు మార్జిన్ (Width నుండి మైనస్ చేసి) ఇచ్చాము
        int avatarLeft = width - avatarSize - (int) (width * 0.04f);
        int avatarTop = (height - avatarSize) / 2;

        if (userPhoto != null) {
            Bitmap circularBitmap = getCircleBitmap(userPhoto, avatarSize);
            canvas.drawBitmap(circularBitmap, avatarLeft, avatarTop, null);
        }

        // ─── 2. ఎడమవైపు టెక్స్ట్ (Left Side Name & Designation) ───
        // 🛑 మార్పు: టెక్స్ట్ ఎడమవైపు నుండి ప్రారంభం కావడానికి మార్జిన్ సెట్ చేసాము
        float textX = (width * 0.05f);

        // పేరు (Name)
        paint.setColor(Color.BLACK);
        paint.setTextSize(height * 0.28f);
        paint.setFakeBoldText(true);
        canvas.drawText(name, textX, height * 0.45f, paint);

        // హోదా (Designation)
        paint.setColor(Color.parseColor("#444444"));
        paint.setTextSize(height * 0.20f);
        paint.setFakeBoldText(false);
        canvas.drawText(des, textX, height * 0.75f, paint);

        return bitmap;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap, int size) {
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        Rect src = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect dst = new Rect(0, 0, size, size);
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
}