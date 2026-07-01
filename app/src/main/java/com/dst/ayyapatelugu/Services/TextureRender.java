package com.dst.ayyapatelugu.Services;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TextureRender {
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uSTMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}";

    private static final float[] VERTICES = {

            -1.0f,-1.0f,0f,
            1.0f,-1.0f,0f,
            -1.0f, 1.0f,0f,
            1.0f, 1.0f,0f
    };

    private static final float[] TEX = {

            0f,1f,
            1f,1f,
            0f,0f,
            1f,0f
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer texBuffer;

    private int program;

    private int aPosition;
    private int aTextureCoord;

    private int uMVPMatrix;
    private int uSTMatrix;

    private int textureID;
    private SurfaceTexture surfaceTexture;

    private int posterTexture = -1;

    private boolean updateSurface = false;

    private final Object frameSyncObject = new Object();

    private final float[] mvpMatrix = new float[16];
    private final float[] stMatrix = new float[16];

    private Bitmap posterBitmap;

    public TextureRender() {

        vertexBuffer =
                ByteBuffer.allocateDirect(VERTICES.length*4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();

        vertexBuffer.put(VERTICES).position(0);

        texBuffer =
                ByteBuffer.allocateDirect(TEX.length*4)
                        .order(ByteOrder.nativeOrder())
                        .asFloatBuffer();

        texBuffer.put(TEX).position(0);

        Matrix.setIdentityM(mvpMatrix,0);
        Matrix.setIdentityM(stMatrix,0);
    }

    public void surfaceCreated(){

        program =
                GLUtil.createProgram(
                        VERTEX_SHADER,
                        FRAGMENT_SHADER);

        aPosition =
                GLES20.glGetAttribLocation(
                        program,
                        "aPosition");

        aTextureCoord =
                GLES20.glGetAttribLocation(
                        program,
                        "aTextureCoord");

        uMVPMatrix =
                GLES20.glGetUniformLocation(
                        program,
                        "uMVPMatrix");

        uSTMatrix =
                GLES20.glGetUniformLocation(
                        program,
                        "uSTMatrix");

        textureID = createOESTexture();
    }

    private int createOESTexture(){

        int[] tex = new int[1];

        GLES20.glGenTextures(1,tex,0);

        GLES20.glBindTexture(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                tex[0]);

        GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameterf(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glTexParameteri(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);

        return tex[0];
    }

    public int getTextureId(){

        return textureID;
    }

    public void setPosterBitmap(Bitmap bitmap){

        posterBitmap = bitmap;
    }

    public SurfaceTexture createSurfaceTexture() {

        surfaceTexture = new SurfaceTexture(textureID);

        surfaceTexture.setOnFrameAvailableListener(
                new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {

                        synchronized (frameSyncObject) {

                            updateSurface = true;

                            frameSyncObject.notifyAll();
                        }
                    }
                });

        return surfaceTexture;
    }
    public void updateTexImage() {

        synchronized (frameSyncObject) {

            if (!updateSurface) {

                return;
            }

            updateSurface = false;
        }

        surfaceTexture.updateTexImage();

        surfaceTexture.getTransformMatrix(stMatrix);
    }
    private void loadPosterTexture() {

        if (posterBitmap == null)
            return;

        if (posterTexture != -1)
            return;

        int[] tex = new int[1];

        GLES20.glGenTextures(1, tex, 0);

        posterTexture = tex[0];

        GLES20.glBindTexture(
                GLES20.GL_TEXTURE_2D,
                posterTexture);

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLUtils.texImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                posterBitmap,
                0);
    }

    public void drawFrame() {

        updateTexImage();

        loadPosterTexture();

        GLES20.glViewport(0,0,1080,2220);

        GLES20.glClearColor(0f,0f,0f,1f);

        GLES20.glClear(
                GLES20.GL_COLOR_BUFFER_BIT);

        drawVideo();

        drawPoster();
    }

    private void drawPoster() {


            if (posterTexture == -1) {
                return;
            }

            // Bottom 300px area
            GLES20.glViewport(
                    0,
                    0,
                    1080,
                    300);

            GLES20.glBindTexture(
                    GLES20.GL_TEXTURE_2D,
                    posterTexture);

            GLES20.glDrawArrays(
                    GLES20.GL_TRIANGLE_STRIP,
                    0,
                    4);

            // Restore viewport
            GLES20.glViewport(
                    0,
                    300,
                    1080,
                    1920);

    }

    private void drawVideo() {
        GLES20.glUseProgram(program);

        GLES20.glUniformMatrix4fv(
                uMVPMatrix,
                1,
                false,
                mvpMatrix,
                0);

        GLES20.glUniformMatrix4fv(
                uSTMatrix,
                1,
                false,
                stMatrix,
                0);

        GLES20.glEnableVertexAttribArray(aPosition);

        GLES20.glVertexAttribPointer(
                aPosition,
                3,
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoord);

        GLES20.glVertexAttribPointer(
                aTextureCoord,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                texBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(
                GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                textureID);

        GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP,
                0,
                4);
    }
}
