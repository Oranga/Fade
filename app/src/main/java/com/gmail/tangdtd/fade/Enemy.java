package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Menace on 1/8/2015.
 */
public class Enemy {
    private final static String vertexShader =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec4 a_Color;" +
            "varying vec4 v_Color;" +
            "void main() {" +
            "   v_Color = a_Color;" +
            "   gl_Position = uMVPMatrix * a_Position;" +
            "}";
    private final static String fragmentShader =
            "precision mediump float;" +
            "varying vec4 v_Color;" +
            "void main () {" +
            "   gl_FragColor = v_Color;" +
            "}";
    private static int eProgramHandle;
    private int eMVPMatrixHandle, ePositionHandle, eColorHandle;
    private float [] eModelMatrix = new float[16];
    private float [] eRotateMatrix = new float [16];
    private float [] eVerticesData;
    private short [] eDrawOrder;
    private FloatBuffer eVerticesBuffer;
    private ShortBuffer eDrawListBuffer;

    private final int eStrideBytes = 7*4;
    private final int ePositionOffset = 0;
    private final int ePositionDataSize = 3;
    private final int eColorOffset = 3;
    private final int eColorDataSize = 4;


    private float numSides;
    private float xLoc, yLoc, zLoc, newXLoc, newYLoc, newZLoc, xRot, yRot, zRot,colorR, colorB, colorG, innerAlpha, outerR, outerB, outerG, outerA;
    private final float eScale = 2f;
    private final float speed = 0.01f;
    private final float hitFactor = 0.005f;


    public Enemy(){
        this(3, 0f, 0f, 5f);
    }
    public Enemy(int n){
        this(n, 0f, 0f, 5f);
    }
    public Enemy(float x, float y, float z){
        this(3, x, y, z);
    }
    public Enemy(int n, float x, float y, float z){

        Matrix.setIdentityM(eRotateMatrix, 0);
        numSides = n;
        newXLoc = xLoc = x;
        newYLoc = yLoc = y;
        newZLoc = zLoc = z;


        colorR = 0f;
        colorB = 0f;
        colorG = 0f;
        innerAlpha = 1f;
        outerR = 0.5f;
        outerG = 0f;
        outerB = 1f;

        eVerticesData = new float[n * 7 + 7];
        eDrawOrder = new short[n * 3];

        int vertexShaderHandle = fadeRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = fadeRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader);

        eVerticesData[0] = 0f;
        eVerticesData[1] = 0f;
        eVerticesData[2] = 0f;
        eVerticesData[3] = colorR;
        eVerticesData[4] = colorG;
        eVerticesData[5] = colorB;
        eVerticesData[6] = 1f;

        float angle = 0;
        for (int i = 7; i < eVerticesData.length; i+=7){
            eVerticesData[i] = (float)Math.cos(angle);
            //Log.d("Fade-eVertexData-" + i + "-X", Float.toString(eVerticesData[i]));
            eVerticesData[i+1] = (float)Math.sin(angle);
            //Log.d("Fade-eVertexData-" + (i + 1) + "-Y", Float.toString(eVerticesData[i + 1]));
            eVerticesData[i+2] = 0f;
            //Log.d("Fade-eVertexData-" + (i + 2) + "-Z", Float.toString(eVerticesData[i + 2]));
            eVerticesData[i+3] = outerR;
            //Log.d("Fade-eVertexData-" + (i + 3) + "-R", Float.toString(eVerticesData[i + 3]));
            eVerticesData[i+4] = outerG;
            //Log.d("Fade-eVertexData-" + (i + 4) + "-G", Float.toString(eVerticesData[i + 4]));
            eVerticesData[i+5] = outerB;
            //Log.d("Fade-eVertexData-" + (i + 5) + "-B", Float.toString(eVerticesData[i + 5]));
            eVerticesData[i+6] = innerAlpha;
            //Log.d("Fade-eVertexData-" + (i + 6) + "-A", Float.toString(eVerticesData[i + 6]));
            angle += 2 * Math.PI/numSides;
        }
        for (int i = 0; i < n; i++){
            eDrawOrder[i*3] = 0;
            //Log.d("Fade-eDrawData-" + (i*n) + "-1", Short.toString(eDrawOrder[i*3]));
            eDrawOrder[i*3+2] = (short) (i+1);
            //Log.d("Fade-eDrawData-" + (i*n+1) + "-2", Short.toString(eDrawOrder[i*3+1]));
            eDrawOrder[i*3+1] = (short) ((i+1) % n + 1);
            //Log.d("Fade-eDrawData-" + (i*n+2) + "-3", Short.toString(eDrawOrder[i*3+2]));
        }
        eVerticesBuffer = ByteBuffer.allocateDirect(eVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        eVerticesBuffer.put(eVerticesData).position(0);

        // initialize byte buffer for the draw list
        eDrawListBuffer = ByteBuffer.allocateDirect(eDrawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        eDrawListBuffer.put(eDrawOrder).position(0);



        eProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(eProgramHandle, vertexShaderHandle);
        GLES20.glAttachShader(eProgramHandle, fragmentShaderHandle);
        GLES20.glBindAttribLocation(eProgramHandle, 0, "a_Position");
        GLES20.glBindAttribLocation(eProgramHandle, 1, "a_Color");
        GLES20.glLinkProgram(eProgramHandle);

        eMVPMatrixHandle = GLES20.glGetUniformLocation(eProgramHandle, "uMVPMatrix");
        ePositionHandle = GLES20.glGetAttribLocation(eProgramHandle, "a_Position");
        eColorHandle = GLES20.glGetAttribLocation(eProgramHandle, "a_Color");

        Matrix.setIdentityM(eModelMatrix, 0);
        Matrix.scaleM(eModelMatrix, 0, eScale, eScale, eScale);
    }
    public float getRotationAngles(float x, float y){
        if (x == 0 && y == 0){
            return 0f;
        }else if (y > 0 && x == 0 || y < 0 && x == 0 ) {
            return 90f;
        }else if (x < 0 ) {
            return (float) ((Math.atan((double) y / x) + Math.PI) * 180.0/Math.PI);
        }else{
            return (float) (Math.atan((double) y / x)* 180.0/Math.PI);
        }
    }
    public  float [] getModelMatrix(){
        Matrix.setIdentityM(eModelMatrix, 0);
        Matrix.translateM(eModelMatrix, 0, xLoc, yLoc, zLoc);
        Matrix.rotateM(eModelMatrix, 0, xRot, 1f, 0f, 0f);
        Matrix.rotateM(eModelMatrix, 0, yRot, 0f, 1f, 0f);
        Matrix.scaleM(eModelMatrix, 0, eScale, eScale, eScale);
        return eModelMatrix;
    }
    public void setRotation(float dx, float dy, float dz){
        float [] tempMatrix = new float[16];
        Matrix.setIdentityM(tempMatrix, 0);
        Matrix.rotateM(tempMatrix, 0, dx, 1f, 0f, 0f);
        Matrix.rotateM(tempMatrix, 0, dy, 0f, 1f, 0f);
        Matrix.rotateM(tempMatrix, 0, dz, 0f, 0f, 1f);

        Matrix.multiplyMM(eRotateMatrix, 0, tempMatrix, 0, eModelMatrix,0);
        System.arraycopy(eRotateMatrix, 0, eModelMatrix, 0, 16);

    }
    public void rotate() {
        yRot = getRotationAngles(zLoc, xLoc);
        xRot = -getRotationAngles(zLoc, yLoc);
        if (yRot == 180f){
            yRot = 0;
        }
        zRot = 0f;
    }

    public void teleport(float x, float y, float z){
        newXLoc = xLoc = x;
        newYLoc = yLoc = y;
        newZLoc = zLoc = z;
        zRot = 0;
    }
    public void moveTo(float x, float y, float z){
        newXLoc = x;
        newYLoc = y;
        newZLoc = z;
    }
    public void move(){
        if(Math.abs(xLoc - newXLoc) < 0.01f) {
            xLoc = newXLoc;
        }else if (xLoc < newXLoc ){
            xLoc += speed;
        }else if (xLoc > newXLoc){
            xLoc -= speed;
        }
        if(Math.abs(yLoc - newYLoc) < 0.01f) {
            yLoc = newYLoc;
        }else if (yLoc < newYLoc ){
            yLoc += speed;
        }else if (yLoc > newYLoc){
            yLoc -= speed;
        }
        if(Math.abs(zLoc - newZLoc) < 0.01f) {
            zLoc = newZLoc;
        }else if (zLoc < newZLoc ){
            zLoc += speed;
        }else if (zLoc > newZLoc){
            zLoc -= speed;
        }
        rotate();
    }

    public boolean isStopped(){
        return (xLoc == newXLoc && yLoc == newYLoc && zLoc == newZLoc);
    }

    public void draw(float [] mvpMatrix) {
//        GLES20.glDisable(GLES20.GL_CULL_FACE);
        float [] eFinalMatrix = new float[16];

        if (xLoc == 0 && yLoc == 0 && zLoc == 0) {

        }else{
            move();
            getModelMatrix();
        }


        Matrix.multiplyMM(eFinalMatrix, 0, mvpMatrix, 0, eModelMatrix, 0);
        GLES20.glUseProgram(eProgramHandle);

        eVerticesBuffer.position(ePositionOffset);
        GLES20.glVertexAttribPointer(ePositionHandle, ePositionDataSize, GLES20.GL_FLOAT, false, eStrideBytes, eVerticesBuffer);
        GLES20.glEnableVertexAttribArray(ePositionHandle);

        eVerticesBuffer.position(eColorOffset);
        GLES20.glVertexAttribPointer(eColorHandle, eColorDataSize, GLES20.GL_FLOAT, false, eStrideBytes, eVerticesBuffer);
        GLES20.glEnableVertexAttribArray(eColorHandle);

        GLES20.glUniformMatrix4fv(eMVPMatrixHandle, 1, false, eFinalMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, eDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, eDrawListBuffer);
//        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glDisableVertexAttribArray(ePositionHandle);
        GLES20.glDisableVertexAttribArray(eColorHandle);
    }
    public void onHit(){
        if (numSides <= 3){

        }else {
            numSides = numSides - hitFactor;
            int intSides = (int) Math.ceil(numSides);
            eVerticesData = new float[intSides * 7 + 7];
            eDrawOrder = new short[intSides * 3];

            eVerticesData[0] = 0f;
            eVerticesData[1] = 0f;
            eVerticesData[2] = 0f;
            eVerticesData[3] = colorR;
            eVerticesData[4] = colorG;
            eVerticesData[5] = colorB;
            eVerticesData[6] = 1f;

            float angle = 0;
            for (int i = 7; i < eVerticesData.length; i += 7) {
                eVerticesData[i] = (float) Math.cos(angle);
                //Log.d("Fade-eVertexData-" + i + "-X", Float.toString(eVerticesData[i]));
                eVerticesData[i + 1] = (float) Math.sin(angle);
                //Log.d("Fade-eVertexData-" + (i + 1) + "-Y", Float.toString(eVerticesData[i + 1]));
                eVerticesData[i + 2] = 0f;
                //Log.d("Fade-eVertexData-" + (i + 2) + "-Z", Float.toString(eVerticesData[i + 2]));
                eVerticesData[i + 3] = outerR;
                //Log.d("Fade-eVertexData-" + (i + 3) + "-R", Float.toString(eVerticesData[i + 3]));
                eVerticesData[i + 4] = outerG;
                //Log.d("Fade-eVertexData-" + (i + 4) + "-G", Float.toString(eVerticesData[i + 4]));
                eVerticesData[i + 5] = outerB;
                //Log.d("Fade-eVertexData-" + (i + 5) + "-B", Float.toString(eVerticesData[i + 5]));
                eVerticesData[i + 6] = innerAlpha;
                //Log.d("Fade-eVertexData-" + (i + 6) + "-A", Float.toString(eVerticesData[i + 6]));
                angle += 2 * Math.PI / numSides;
            }
            for (int i = 0; i < intSides; i++) {
                eDrawOrder[i * 3] = 0;
                //Log.d("Fade-eDrawData-" + (i*n) + "-1", Short.toString(eDrawOrder[i*3]));
                eDrawOrder[i * 3 + 2] = (short) (i + 1);
                //Log.d("Fade-eDrawData-" + (i*n+1) + "-2", Short.toString(eDrawOrder[i*3+1]));
                eDrawOrder[i * 3 + 1] = (short) ((i + 1) % (float) intSides + 1);
                //Log.d("Fade-eDrawData-" + (i*n+2) + "-3", Short.toString(eDrawOrder[i*3+2]));
            }
            eVerticesBuffer = ByteBuffer.allocateDirect(eVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            eVerticesBuffer.put(eVerticesData).position(0);

            // initialize byte buffer for the draw list
            eDrawListBuffer = ByteBuffer.allocateDirect(eDrawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
            eDrawListBuffer.put(eDrawOrder).position(0);
        }
    }
}
