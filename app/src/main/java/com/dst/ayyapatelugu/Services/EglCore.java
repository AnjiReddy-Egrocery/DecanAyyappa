package com.dst.ayyapatelugu.Services;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.util.Log;

public class EglCore {

    private static final String TAG = "EglCore";

    public static final int FLAG_RECORDABLE = 0x01;
    public static final int FLAG_TRY_GLES3 = 0x02;

    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLConfig mEGLConfig = null;
    private int mGlVersion = -1;

    public EglCore() {
        this(null, FLAG_TRY_GLES3);
    }

    public EglCore(EGLContext sharedContext, int flags) {

        if (sharedContext == null) {
            sharedContext = EGL14.EGL_NO_CONTEXT;
        }

        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);

        if (mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("Unable to get EGL14 display");
        }

        int[] version = new int[2];

        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = null;
            throw new RuntimeException("Unable to initialize EGL14");
        }

        // GLES3
        if ((flags & FLAG_TRY_GLES3) != 0) {

            EGLConfig config = getConfig(flags, 3);

            if (config != null) {

                int[] attrib3List = {
                        EGL14.EGL_CONTEXT_CLIENT_VERSION, 3,
                        EGL14.EGL_NONE
                };

                EGLContext context = EGL14.eglCreateContext(
                        mEGLDisplay,
                        config,
                        sharedContext,
                        attrib3List,
                        0);

                if (EGL14.eglGetError() == EGL14.EGL_SUCCESS) {
                    mEGLConfig = config;
                    mEGLContext = context;
                    mGlVersion = 3;
                }
            }
        }

        // GLES2
        if (mEGLContext == EGL14.EGL_NO_CONTEXT) {

            EGLConfig config = getConfig(flags, 2);

            int[] attrib2List = {
                    EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                    EGL14.EGL_NONE
            };

            EGLContext context = EGL14.eglCreateContext(
                    mEGLDisplay,
                    config,
                    sharedContext,
                    attrib2List,
                    0);

            checkEglError("eglCreateContext");

            mEGLConfig = config;
            mEGLContext = context;
            mGlVersion = 2;
        }

        int[] values = new int[1];

        EGL14.eglQueryContext(
                mEGLDisplay,
                mEGLContext,
                EGL14.EGL_CONTEXT_CLIENT_VERSION,
                values,
                0);

        Log.d(TAG, "EGL Context created, GLES " + values[0]);
    }

    private EGLConfig getConfig(int flags, int version) {

        int renderableType =
                version >= 3 ? EGLExt.EGL_OPENGL_ES3_BIT_KHR
                        : EGL14.EGL_OPENGL_ES2_BIT;

        int[] attribList = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, renderableType,
                EGL14.EGL_NONE, 0,
                EGL14.EGL_NONE
        };

        if ((flags & FLAG_RECORDABLE) != 0) {
            attribList[attribList.length - 3] = 0x3142;
            attribList[attribList.length - 2] = 1;
        }

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];

        if (!EGL14.eglChooseConfig(
                mEGLDisplay,
                attribList,
                0,
                configs,
                0,
                configs.length,
                numConfigs,
                0)) {

            Log.w(TAG, "Unable to find RGB8888 / " + version + " EGLConfig");

            return null;
        }

        return configs[0];
    }

    public void release() {

        if (mEGLDisplay != EGL14.EGL_NO_DISPLAY) {

            EGL14.eglMakeCurrent(
                    mEGLDisplay,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_SURFACE,
                    EGL14.EGL_NO_CONTEXT);

            EGL14.eglDestroyContext(
                    mEGLDisplay,
                    mEGLContext);

            EGL14.eglReleaseThread();

            EGL14.eglTerminate(
                    mEGLDisplay);
        }

        mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        mEGLContext = EGL14.EGL_NO_CONTEXT;
        mEGLConfig = null;
    }

    public EGLContext getEGLContext() {
        return mEGLContext;
    }

    public EGLDisplay getEGLDisplay() {
        return mEGLDisplay;
    }

    public EGLConfig getEGLConfig() {
        return mEGLConfig;
    }

    public int getGlVersion() {
        return mGlVersion;
    }

    public void makeCurrent(EGLSurface eglSurface) {

        if (!EGL14.eglMakeCurrent(
                mEGLDisplay,
                eglSurface,
                eglSurface,
                mEGLContext)) {

            throw new RuntimeException("eglMakeCurrent failed");
        }
    }

    public void swapBuffers(EGLSurface eglSurface) {
        EGL14.eglSwapBuffers(mEGLDisplay, eglSurface);
    }

    public void setPresentationTime(
            EGLSurface eglSurface,
            long nsecs) {

        EGLExt.eglPresentationTimeANDROID(
                mEGLDisplay,
                eglSurface,
                nsecs);
    }

    public void releaseSurface(EGLSurface eglSurface) {
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface);
    }

    private void checkEglError(String msg) {

        int error;

        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(
                    msg + ": EGL error: 0x"
                            + Integer.toHexString(error));
        }
    }
}
