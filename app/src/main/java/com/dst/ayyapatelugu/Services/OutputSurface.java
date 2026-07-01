package com.dst.ayyapatelugu.Services;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public class OutputSurface implements SurfaceTexture.OnFrameAvailableListener {

    private SurfaceTexture surfaceTexture;
    private Surface surface;
    private TextureRender textureRender;

    private final Object frameSyncObject = new Object();
    private boolean frameAvailable;

    public OutputSurface() {

        textureRender = new TextureRender();
        textureRender.surfaceCreated();

        surfaceTexture = textureRender.createSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(this);

        surface = new Surface(surfaceTexture);
    }

    public Surface getSurface() {
        return surface;
    }

    public TextureRender getTextureRender() {
        return textureRender;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (frameSyncObject) {
            frameAvailable = true;
            frameSyncObject.notifyAll();
        }
    }

    public void awaitNewImage() {
        synchronized (frameSyncObject) {
            while (!frameAvailable) {
                try {
                    frameSyncObject.wait(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            frameAvailable = false;
        }

        textureRender.updateTexImage();
    }

    public void drawImage() {
        textureRender.drawFrame();
    }

    public void release() {
        if (surface != null) {
            surface.release();
        }

        if (surfaceTexture != null) {
            surfaceTexture.release();
        }
    }
}
