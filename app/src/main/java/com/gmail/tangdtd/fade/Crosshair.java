package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Timothy D. Mahon on 1/10/2015.
 */
public class Crosshair {
    private final static String vertexShader =
                    "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 a_Position;" +
                    "void main() {" +
                    "   gl_Position = uMVPMatrix * a_Position;" +
                    "}";
    private final static String fragmentShader =
                    "precision mediump float;" +
                    "uniform vec4 v_Color;" +
                    "void main () {" +
                    "   gl_FragColor = v_Color;" +
                    "}";
    private float xLoc, yLoc;
    float [] ray1, ray2, nearLeft, nearRight, farLeft, farRight;
    private float [] crInvertMatrix = new float[16];
    private static int crProgramHandle;
    private int crMVPMatrixHandle, crPositionHandle, crColorHandle;
    private final int crStrideBytes = 4*4;
    private final int crPositionOffset = 0;
    private final int crPositionDataSize = 3;
    private float  viewXMin,  viewYMin,  viewXMax,  viewYMax;


    private FloatBuffer crVerticesBuffer;


    //private boolean locked;
    public Crosshair(){
        this(0, 0, 0, 0, 100, 100);
    }
    public Crosshair(float x, float y, float xmin, float ymin, float xmax, float ymax){
        xLoc = x;
        yLoc = y;
        viewXMin = xmin;
        viewYMin = ymin;
        viewXMax = xmax;
        viewYMax = ymax;
        int vertexShaderHandle = fadeRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = fadeRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader);

        crProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(crProgramHandle, vertexShaderHandle);
        GLES20.glAttachShader(crProgramHandle, fragmentShaderHandle);
        GLES20.glBindAttribLocation(crProgramHandle, 0, "a_Position");
//        GLES20.glBindAttribLocation(crProgramHandle, 1, "a_Color");
        GLES20.glLinkProgram(crProgramHandle);

        crMVPMatrixHandle = GLES20.glGetUniformLocation(crProgramHandle, "uMVPMatrix");
        crPositionHandle = GLES20.glGetAttribLocation(crProgramHandle, "a_Position");
        crColorHandle = GLES20.glGetUniformLocation(crProgramHandle, "v_Color");

    }
    public void moveTo(float x, float y){
        xLoc = x;
        yLoc = y;
    }
    public void setViewPort(float xmin, float ymin, float xmax, float ymax){
        viewXMin = xmin;
        viewYMin = ymin;
        viewXMax = xmax;
        viewYMax = ymax;
    }
    private float[] unproject(float [] mvpMatrix, float xLoc, float yLoc, float zLoc){
        float [] crRay = {((xLoc - viewXMin)/(viewXMax - viewXMin)) * 2f - 1f , (2f * (yLoc - viewYMin)/(viewYMax - viewYMin) - 1f), zLoc * 2f -1f, 1f};
        float [] crNewRay = new float[4];
        if ( !Matrix.invertM(crInvertMatrix, 0, mvpMatrix, 0)){
            Log.d("Fade-Crosshair-unproject", "Value = uninvertable");
            return null;
        }
        Matrix.multiplyMV(crNewRay, 0, crInvertMatrix, 0, crRay, 0);
        if (crNewRay[3] == 0f){
            Log.d("Fade-Crosshair-unproject", "Value = 4d mishap!");
            return null;
        }

        crNewRay[3] = 1.0f/ crNewRay[3];
        crRay[0] = crNewRay[0] * crNewRay[3];
        crRay[1] = crNewRay[1] * crNewRay[3];
        crRay[2] = crNewRay[2] * crNewRay[3];

        return crRay;
    }
    public void draw(float [] mvpMatrix){
        if (farLeft != null) {
            crVerticesBuffer = ByteBuffer.allocateDirect((nearLeft.length + nearRight.length + farLeft.length + farRight.length) * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            crVerticesBuffer.put(nearLeft).put(nearRight).put(farRight).put(farLeft);
            GLES20.glUseProgram(crProgramHandle);
            crVerticesBuffer.position(crPositionOffset);
            GLES20.glVertexAttribPointer(crPositionHandle, crPositionDataSize, GLES20.GL_FLOAT, false, crStrideBytes, crVerticesBuffer);
            GLES20.glEnableVertexAttribArray(crPositionHandle);

            float[] crFinalMatrix = new float[16];
            Matrix.setIdentityM(crFinalMatrix, 0);
            GLES20.glUniformMatrix4fv(crMVPMatrixHandle, 1, false, mvpMatrix, 0);

            float[] color = {0f, 0f, 0f, 1f};
            GLES20.glUniform4fv(crColorHandle, 1, color, 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(crPositionHandle);
        }
    }
    public void fireAndMove(float [] mvpMatrix){
        nearRight = unproject(mvpMatrix, (viewXMax-viewXMin)/2f + 10f, 1f, 0.125f);
        nearLeft = unproject(mvpMatrix, (viewXMax-viewXMin)/2f - 10f , 1f, 0.125f);
    }
    public void fire(float [] mvpMatrix){
        ray1 = unproject(mvpMatrix, xLoc, yLoc, 0f);
        ray2 = unproject(mvpMatrix, xLoc, yLoc, 1f);
        nearRight = unproject(mvpMatrix, (viewXMax-viewXMin)/2f + 10f, 1f, 0.125f);
        nearLeft = unproject(mvpMatrix, (viewXMax-viewXMin)/2f - 10f , 1f, 0.125f);
        farLeft = unproject(mvpMatrix, xLoc - 5f, yLoc, 0.125f);
        farRight = unproject(mvpMatrix, xLoc + 5f, yLoc, 0.125f);
//        Log.d("fade-Crosshair-nearLeft", "(" + Float.toString(nearLeft[0]) + ", " + Float.toString(nearLeft[1]) + ", " + Float.toString(nearLeft[2]) + ")");
//        Log.d("fade-Crosshair-nearRight", "(" + Float.toString(nearRight[0]) + ", " + Float.toString(nearRight[1]) + ", " + Float.toString(nearRight[2]) + ")");
//        Log.d("fade-Crosshair-farLeft", "(" + Float.toString(farLeft[0]) + ", " + Float.toString(farLeft[1]) + ", " + Float.toString(farLeft[2]) + ")");
//        Log.d("fade-Crosshair-farRight", "(" + Float.toString(farRight[0]) + ", " + Float.toString(farRight[1]) + ", " + Float.toString(farRight[2]) + ")");
    }
    public float[] getNormalRay(){
        float [] ray;
        if (ray1 != null && ray2 != null) {
            ray = new float[]{ray2[0] - ray1[0], ray2[1] - ray1[1], ray2[2] - ray1[2]};
            float size = Matrix.length(ray[0], ray[1], ray[2]);
            ray[0] /= size;
            ray[1] /= size;
            ray[2] /= size;
        /*Log.d("Fade-Crosshair-getNormalRay", "ray: (" + ray[0] + ", " + ray[1] + ", " + ray[2] + ")");
        Log.d("Fade-Crosshair-getNormalRay", "ray1: (" + ray1[0] + ", " + ray1[1] + ", " + ray1[2] + ")");
        Log.d("Fade-Crosshair-getNormalRay", "ray2: (" + ray2[0] + ", " + ray2[1] + ", " + ray2[2] + ")");*/

        }else{
            ray = new float[]{0f,0f,0f};
        }
        return ray;
    }

}
