package com.gmail.tangdtd.fade;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Menace on 1/7/2015.
 */
public class Background {
    private final String vertexShader =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec4 a_Color;" +
            "varying vec4 v_Color;" +
            "void main() {" +
            "   v_Color = a_Color;" +
            "   gl_Position = uMVPMatrix * a_Position;" +
            "}";
    private final String fragmentShader =
            "precision mediump float;" +
            "varying vec4 v_Color;" +
            "void main () {" +
            "   gl_FragColor = v_Color;" +
            "}";
    private final FloatBuffer bgVerticesBuffer;
    private final ShortBuffer drawListBuffer;
    private final int programHandle;
    private int bgMVPMatrixHandle;
    private int bgPositionHandle;
    private int bgColorHandle;

    private final float c0 = 30f;
    private final float c1 =  c0 * (float)((1.0 + Math.sqrt(5.0)))/2.0f;
    private final int bgStrideBytes = 7*4;
    private final int bgPositionOffset = 0;
    private final int bgPositionDataSize = 3;
    private final int bgColorOffset = 3;
    private final int bgColorDataSize = 4;

    private float red, blue, green, alpha;

    private final float[] bgVerticesData = {
            -c0, c1, 0f,
            0f, 0f, 1f, 1f,
            c0, c1, 0f,
            0f, 0f, 1f, 1f,
            -c0, -c1, 0f,
            0f, 0f, 1f, 1f,
            c0, -c1, 0f,
            0f, 0f, 1f, 1f,

            0f, -c0, c1,
            1f, 0f, 0f, 1f,
            0f, c0, c1,
            1f, 0f, 0f, 1f,
            0f, -c0, -c1,
            1f, 0f, 0f, 1f,
            0f,c0,-c1,
            1f, 0f, 0f, 1f,

            c1, 0f, -c0,
            0f, 1f, 0f, 1f,
            c1, 0f, c0,
            0f, 1f, 0f, 1f,
            -c1, 0f, -c0,
            0f, 1f, 0f, 1f,
            -c1, 0f, c0,
            0f, 1f, 0f, 1f,
    };

    private final short bgDrawOrder[] = {
            5, 11, 0,
            1, 5, 0,
            7, 1, 0,
            10, 7, 0,
            11, 10, 0,
            9, 5, 1,
            4, 11, 5,
            2, 10, 11,
            6, 7, 10,
            8, 1, 7,
            4, 9, 3,
            2, 4, 3,
            6, 2, 3,
            8, 6, 3,
            9, 8, 3,
            5, 9, 4,
            11, 4, 2,
            10, 2, 6,
            7, 6, 8,
            1, 8, 9
    };

    public Background(){

        bgVerticesBuffer = ByteBuffer.allocateDirect(bgVerticesData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bgVerticesBuffer.put(bgVerticesData).position(0);

         // initialize byte buffer for the draw list
        drawListBuffer = ByteBuffer.allocateDirect(bgDrawOrder.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        drawListBuffer.put(bgDrawOrder).position(0);

        int vertexShaderHandle = fadeRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = fadeRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShader);

        programHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(programHandle, vertexShaderHandle);
        GLES20.glAttachShader(programHandle, fragmentShaderHandle);
        GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
        GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
        GLES20.glLinkProgram(programHandle);

        bgMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "uMVPMatrix");
        bgPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
        bgColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");

    }

    public void draw(float [] mvpMatrix){

        GLES20.glUseProgram(programHandle);
        bgVerticesBuffer.position(bgPositionOffset);
        GLES20.glVertexAttribPointer(bgPositionHandle, bgPositionDataSize, GLES20.GL_FLOAT, false, bgStrideBytes, bgVerticesBuffer);
        GLES20.glEnableVertexAttribArray(bgPositionHandle);

        bgVerticesBuffer.position(bgColorOffset);
        GLES20.glVertexAttribPointer(bgColorHandle, bgColorDataSize, GLES20.GL_FLOAT, false, bgStrideBytes, bgVerticesBuffer);
        GLES20.glEnableVertexAttribArray(bgColorHandle);

        GLES20.glUniformMatrix4fv(bgMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, bgDrawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(bgPositionHandle);
        GLES20.glDisableVertexAttribArray(bgColorHandle);
    }

}
