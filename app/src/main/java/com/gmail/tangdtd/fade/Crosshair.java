package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Menace on 1/10/2015.
 */
public class Crosshair {
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
    private float xLoc, yLoc, width, height;
    float [] ray1, ray2;
    private float [] crInvertMatrix = new float[16];
    private static int crProgramHandle;
    private int crMVPMatrixHandle, crPositionHandle, crColorHandle;
    private final int crStrideBytes = 4*4;
    private final int ePositionOffset = 0;
    private final int crPositionDataSize = 3;
//    private final int eColorOffset = 3;
//    private final int crColorDataSize = 4;

    private FloatBuffer crVerticesBuffer;
//    private ShortBuffer eDrawListBuffer;


    //private boolean locked;
    public Crosshair(){
        this(0, 0);
    }
    public Crosshair(float x, float y){
        xLoc = x;
        yLoc = y;
        setWidthHeight(100, 100);

        int vertexShaderHandle = fadeRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = fadeRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader);

        crProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(crProgramHandle, vertexShaderHandle);
        GLES20.glAttachShader(crProgramHandle, fragmentShaderHandle);
        GLES20.glBindAttribLocation(crProgramHandle, 0, "a_Position");
        GLES20.glBindAttribLocation(crProgramHandle, 1, "a_Color");
        GLES20.glLinkProgram(crProgramHandle);

        crMVPMatrixHandle = GLES20.glGetUniformLocation(crProgramHandle, "uMVPMatrix");
        crPositionHandle = GLES20.glGetAttribLocation(crProgramHandle, "a_Position");
        crColorHandle = GLES20.glGetUniformLocation(crProgramHandle, "v_Color");

    }
    public void setWidthHeight(float w, float h){
        width = w;
        height = h;
    }
    public void moveTo(float x, float y){
        xLoc = x;
        yLoc = y;
    }
    public float[] unproject(float [] mvpMatrix, float xLoc, float yLoc, float zLoc, float viewXMin, float viewYMin, float viewXMax, float viewYMax){
        float [] crRay = {((xLoc - viewXMin)/(viewXMax - viewXMin)) * 2f - 1f , (2f * (yLoc - viewYMin)/(viewYMax - viewYMin) - 1f), zLoc * 2f -1f, 1f};
        float [] crNewRay = new float[4];
        if ( !Matrix.invertM(crInvertMatrix, 0, mvpMatrix, 0)){
            Log.d("Fade-Crosshair-unproject", "Value = uninvertable");
            return null;
        }
        Matrix.multiplyMV(crNewRay, 0, crInvertMatrix, 0, crRay, 0);
        if (crNewRay[3] == 0f){
            Log.d("Fade-Crosshair-unproject", "Value = fucked.");
            return null;
        }

        crNewRay[3] = 1.0f/ crNewRay[3];
        crRay[0] = crNewRay[0] * crNewRay[3];
        crRay[1] = crNewRay[1] * crNewRay[3];
        crRay[2] = crNewRay[2] * crNewRay[3];
        return crRay;
    }
    public void fire(){
        Log.d("Fade-Crosshair-ray1", "x: " + Float.toString(ray1[0]) +
                " y: " + Float.toString(ray1[1]) +
                " z: " + Float.toString(ray1[2]));
        Log.d("Fade-Crosshair-ray2", "x: " + Float.toString(ray2[0]) +
                " y: " + Float.toString(ray2[1]) +
                " z: " + Float.toString(ray2[2]));
    }
    public void draw(float [] mvpMatrix){
        crVerticesBuffer = ByteBuffer.allocateDirect(ray1.length * 4 + ray2.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        crVerticesBuffer.put(ray1).put(ray2).position(0);

        GLES20.glUseProgram(crProgramHandle);
        crVerticesBuffer.position(ePositionOffset);
        GLES20.glVertexAttribPointer(crPositionHandle, crPositionDataSize, GLES20.GL_FLOAT, false, crStrideBytes, crVerticesBuffer);
        GLES20.glEnableVertexAttribArray(crPositionHandle);

        float [] crFinalMatrix = new float[16];
        Matrix.setIdentityM(crFinalMatrix, 0);
        GLES20.glUniformMatrix4fv(crMVPMatrixHandle, 1, false, mvpMatrix, 0);

        float [] color = {0f, 0f, 0f, 1f};
        GLES20.glUniform4fv(crColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        GLES20.glDisableVertexAttribArray(crPositionHandle);
    }
    public void draw(float [] projMatrix, float [] viewMatrix, float [] mvpMatrix, float viewXMin, float viewYMin, float viewXMax, float viewYMax){
        ray1 = unproject(mvpMatrix, xLoc, yLoc, 0f, viewXMin, viewYMin, viewXMax, viewYMax);
        ray2 = unproject(mvpMatrix, xLoc, yLoc, 1f, viewXMin, viewYMin, viewXMax, viewYMax);
        draw(mvpMatrix);
    }

}
