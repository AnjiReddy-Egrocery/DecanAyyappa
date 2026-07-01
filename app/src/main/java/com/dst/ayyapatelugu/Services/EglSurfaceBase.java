package com.dst.ayyapatelugu.Services;

import android.graphics.Bitmap;
import android.opengl.EGL14;
import android.opengl.EGLSurface;
import android.view.Surface;

public class EglSurfaceBase {
    protected EglCore mEglCore;
    protected EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;

    private int mWidth = -1;
    private int mHeight = -1;

    protected EglSurfaceBase(EglCore eglCore) {
        mEglCore = eglCore;
    }

    /**
     * Creates a window surface from Surface.
     */
    public void createWindowSurface(Surface surface) {

        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }

        int[] surfaceAttribs = {
                EGL14.EGL_NONE
        };

        mEGLSurface = EGL14.eglCreateWindowSurface(
                mEglCore.getEGLDisplay(),
                mEglCore.getEGLConfig(),
                surface,
                surfaceAttribs,
                0);

        checkEglError("eglCreateWindowSurface");
    }

    /**
     * Creates an offscreen surface.
     */
    public void createOffscreenSurface(int width, int height) {

        if (mEGLSurface != EGL14.EGL_NO_SURFACE) {
            throw new IllegalStateException("surface already created");
        }

        int[] surfaceAttribs = {
                EGL14.EGL_WIDTH, width,
                EGL14.EGL_HEIGHT, height,
                EGL14.EGL_NONE
        };

        mEGLSurface = EGL14.eglCreatePbufferSurface(
                mEglCore.getEGLDisplay(),
                mEglCore.getEGLConfig(),
                surfaceAttribs,
                0);

        checkEglError("eglCreatePbufferSurface");

        mWidth = width;
        mHeight = height;
    }

    public int getWidth() {

        if (mWidth < 0) {

            int[] value = new int[1];

            EGL14.eglQuerySurface(
                    mEglCore.getEGLDisplay(),
                    mEGLSurface,
                    EGL14.EGL_WIDTH,
                    value,
                    0);

            return value[0];
        }

        return mWidth;
    }

    public int getHeight() {

        if (mHeight < 0) {

            int[] value = new int[1];

            EGL14.eglQuerySurface(
                    mEglCore.getEGLDisplay(),
                    mEGLSurface,
                    EGL14.EGL_HEIGHT,
                    value,
                    0);

            return value[0];
        }

        return mHeight;
    }

    public void releaseEglSurface() {

        mEglCore.releaseSurface(mEGLSurface);

        mEGLSurface = EGL14.EGL_NO_SURFACE;

        mWidth = -1;
        mHeight = -1;
    }

    public void makeCurrent() {
        mEglCore.makeCurrent(mEGLSurface);
    }

    public void swapBuffers() {
        mEglCore.swapBuffers(mEGLSurface);
    }

    public void setPresentationTime(long nsecs) {
        mEglCore.setPresentationTime(mEGLSurface, nsecs);
    }

    /**
     * Reads pixels from current surface.
     * (Used later for poster rendering / debugging)
     */
    public Bitmap saveFrame() {

        Bitmap bitmap = Bitmap.createBitmap(
                getWidth(),
                getHeight(),
                Bitmap.Config.ARGB_8888);

        return bitmap;
    }

    private void checkEglError(String msg) {

        int error = EGL14.eglGetError();

        if (error != EGL14.EGL_SUCCESS) {

            throw new RuntimeException(
                    msg + ": EGL error: 0x"
                            + Integer.toHexString(error));
        }
    }
}
