package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.DisplayMetrics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Menace on 1/6/2015.
 */
public class fadeRenderer implements GLSurfaceView.Renderer {

    private float [] fMVPMatrix = new float[16];
    private float [] fViewMatrix = new float[16];
    private float [] fProjMatrix = new float[16];
    private float [] fModelMatrix = new float[16];
    private float [] fRotateMatrix = new float[16];
    private volatile float deltaX, deltaY;
    private Background bg;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        Matrix.setLookAtM(fViewMatrix, 0, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f);
        Matrix.setIdentityM(fRotateMatrix, 0);
        bg = new Background();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        deltaX = 0;
        deltaY = 0;
        Matrix.frustumM(fProjMatrix, 0,  -ratio, ratio, -1f, 1f, 1f, 7f);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT  );
        Matrix.setIdentityM(fModelMatrix, 0);
        float [] scratch = new float [16];
        Matrix.rotateM(fModelMatrix, 0, deltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(fModelMatrix, 0, deltaY, 1.0f, 0.0f, 0.0f);
        deltaX = 0f;
        deltaY = 0f;
        Matrix.multiplyMM(scratch, 0, fModelMatrix, 0, fRotateMatrix, 0);
        System.arraycopy(scratch, 0, fRotateMatrix, 0, 16);
        Matrix.multiplyMM(fMVPMatrix, 0, fViewMatrix, 0, scratch, 0);
        Matrix.multiplyMM(fMVPMatrix, 0, fProjMatrix, 0, fMVPMatrix, 0);
        bg.draw(fMVPMatrix);
    }

    public void moveCamera(float dx, float dy){
        deltaX += dx;
        deltaY += dy;

    }
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add source code to shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
