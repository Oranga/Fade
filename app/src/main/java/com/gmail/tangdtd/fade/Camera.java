package com.gmail.tangdtd.fade;

import android.opengl.Matrix;

/**
 * Created by Menace on 1/9/2015.
 */
public class Camera {
    private float [] cModelMatrix = new float[16];
    private float [] cViewMatrix = new float[16];
    private float [] cProjMatrix = new float[16];
    private float [] cMVPMatrix = new float[16];
    private float [] cRotateMatrix = new float[16];
    private volatile float cDeltaX, cDeltaY, cSlowFactor;
    private final float cPrecision = 0.05f;
    public Camera (){
        cDeltaX = 0f;
        cDeltaY = 0f;
        cSlowFactor = 0.1f;
        float[] defaultProjMatrix = new float[16];
        Matrix.frustumM(defaultProjMatrix, 0, -1f, 1f, -1f, 1f, 1f, 10f);
        setProjMatrix(defaultProjMatrix);
        Matrix.setIdentityM(cRotateMatrix, 0);
        Matrix.setLookAtM(cViewMatrix, 0, 0f, 0f, 0f, 0f, 0f, 1f, 0f, 1f, 0f);
    }
    public void setProjMatrix(float [] projMatrix){
         System.arraycopy(projMatrix, 0, cProjMatrix, 0, 16);
    }
    public float [] view(){
        Matrix.setIdentityM(cModelMatrix, 0);
        float[] tempMatrix = new float[16];
        Matrix.rotateM(cModelMatrix, 0, cDeltaX, 0f, 1f, 0f);
        Matrix.rotateM(cModelMatrix, 0, cDeltaY, 1f, 0f, 0f);
//        slowDown();
        cDeltaX = 0f;
        cDeltaY = 0f;
        Matrix.multiplyMM(tempMatrix, 0, cModelMatrix, 0, cRotateMatrix, 0);
        System.arraycopy(tempMatrix, 0, cRotateMatrix, 0, 16);
        Matrix.multiplyMM(cMVPMatrix, 0, cViewMatrix, 0, tempMatrix, 0);
        Matrix.multiplyMM(tempMatrix, 0, cProjMatrix, 0, cMVPMatrix, 0);
        return tempMatrix;
    }
    public void move(float dx, float dy){
        cDeltaX = dx;
        cDeltaY = dy;
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
