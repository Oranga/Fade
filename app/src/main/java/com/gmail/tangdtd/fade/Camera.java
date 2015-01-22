package com.gmail.tangdtd.fade;

import android.opengl.Matrix;

/**
 * Created by Timothy D. Mahon on 1/9/2015.
 */
public class Camera {
    private float [] cModelMatrix = new float[16];
    private float [] cViewMatrix = new float[16];
    private float [] cProjMatrix = new float[16];
    private float [] cMVPMatrix = new float[16];
    private float [] cRotateMatrix = new float[16];
    private volatile float cDeltaX, cDeltaY, cDeltaZ, cSlowFactor;
    private final float cPrecision = 0.05f;
    public Camera (){
        this(0f, 0f, 0f,
                0f, 0f, 1f,
                0f, 1f, 0f,
                1f, 1f, 3f, 30f);
    }
    public Camera (float eyeX, float eyeY, float eyeZ,  float disX, float disY, float disZ, float upX, float upY, float upZ, float width, float height, float near, float far){
        cDeltaX = 0f;
        cDeltaY = 0f;
        cDeltaZ = 0f;
        cSlowFactor = 0.1f;
        float ratio = width/height;
        Matrix.frustumM(cProjMatrix, 0, -ratio, ratio, -1f, 1f, near, far);
        Matrix.setIdentityM(cRotateMatrix, 0);
        Matrix.setLookAtM(cViewMatrix, 0, eyeX, eyeY, eyeZ, disX, disY, disZ, upX, upY, upZ);
    }
    public void setProjMatrix(float [] projMatrix){
         System.arraycopy(projMatrix, 0, cProjMatrix, 0, 16);
    }
    public float [] view(){
        Matrix.setIdentityM(cModelMatrix, 0);
        float[] tempMatrix = new float[16];
        Matrix.rotateM(cModelMatrix, 0, cDeltaX, 1f, 0f, 0f);
        Matrix.rotateM(cModelMatrix, 0, cDeltaY, 0f, 1f, 0f);
        Matrix.rotateM(cModelMatrix, 0, cDeltaZ, 0f, 0f, 1f);
//        slowDown();
        cDeltaX = 0f;
        cDeltaY = 0f;
        cDeltaZ = 0f;
        Matrix.multiplyMM(tempMatrix, 0, cModelMatrix, 0, cRotateMatrix, 0);
        System.arraycopy(tempMatrix, 0, cRotateMatrix, 0, 16);
        Matrix.multiplyMM(cMVPMatrix, 0, cViewMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(tempMatrix, 0, cProjMatrix, 0, cMVPMatrix, 0);
        return tempMatrix;
    }

    public void move(float dx, float dy, float dz){
        cDeltaX = dx;
        cDeltaY = dy;
        cDeltaZ = dz;
    }
    public void setSlowFactor(float slowFactor){
        cSlowFactor = slowFactor;
    }
    public void slowDown(){
        if (Math.abs(cDeltaX) < cPrecision){
            cDeltaX = 0;
        }else if (cDeltaX > 0f) {
            cDeltaX -= cSlowFactor;
        }else if (cDeltaX < 0f) {
            cDeltaX += cSlowFactor;
        }
        if (Math.abs(cDeltaY) < cPrecision) {
            cDeltaY = 0;
        }else if (cDeltaY > 0f){
            cDeltaY -= cSlowFactor;
        }else if (cDeltaY < 0f){
            cDeltaY += cSlowFactor;
        }
    }
}
