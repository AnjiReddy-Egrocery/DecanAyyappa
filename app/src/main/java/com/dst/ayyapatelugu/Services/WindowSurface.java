package com.dst.ayyapatelugu.Services;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public class WindowSurface extends EglSurfaceBase {

    private Surface mSurface;
    private boolean mReleaseSurface;

    /**
     * Creates a WindowSurface from a Surface.
     *
     * @param eglCore EGL Core
     * @param surface Android Surface
     * @param releaseSurface true అయితే release() లో Surface కూడా release అవుతుంది
     */
    public WindowSurface(EglCore eglCore,
                         Surface surface,
                         boolean releaseSurface) {

        super(eglCore);

        if (surface == null) {
            throw new NullPointerException("surface is null");
        }

        mSurface = surface;
        mReleaseSurface = releaseSurface;

        createWindowSurface(surface);
    }

    /**
     * Creates a WindowSurface from SurfaceTexture.
     */
    public WindowSurface(EglCore eglCore,
                         SurfaceTexture surfaceTexture) {

        super(eglCore);

        if (surfaceTexture == null) {
            throw new NullPointerException("surfaceTexture is null");
        }

        Surface surface = new Surface(surfaceTexture);

        mSurface = surface;
        mReleaseSurface = true;

        createWindowSurface(surface);
    }

    /**
     * Releases EGL surface and Android Surface.
     */
    public void release() {

        releaseEglSurface();

        if (mSurface != null && mReleaseSurface) {
            mSurface.release();
            mSurface = null;
        }
    }

    /**
     * Recreates EGLSurface when EGL context changes.
     */
    public void recreate(EglCore newEglCore) {

        if (mSurface == null) {
            throw new RuntimeException("not yet implemented for SurfaceTexture");
        }

        mEglCore = newEglCore;

        createWindowSurface(mSurface);
    }

    /**
     * Returns underlying Android Surface.
     */
    public Surface getSurface() {
        return mSurface;
    }
}