package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Menace on 1/6/2015.
 */
public class fadeRenderer implements GLSurfaceView.Renderer {

    private float [] fMVPMatrix = new float[16];
    private float [] fProjMatrix = new float[16];
    private Crosshair crosshair;
    private Camera camera;
    private Background bg;
    private Enemy enemy1, enemy2, enemy3;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        bg = new Background();
        crosshair = new Crosshair();
        enemy1 = new Enemy(6, 8f, 0f, 8f);
        enemy2 = new Enemy(4, 0f, 8f, -8f);
        enemy3 = new Enemy(3, 5f, 5f, 5f);

        camera = new Camera();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(fProjMatrix, 0,  -ratio, ratio, -1f, 1f, 3f, 30f);
        camera.setProjMatrix(fProjMatrix);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        fMVPMatrix = camera.view();
        bg.draw(fMVPMatrix);

        enemy2.draw(fMVPMatrix);
        enemy1.draw(fMVPMatrix);
        enemy3.draw(fMVPMatrix);

        //if (enemy1.isStopped()){
            //Random rGenerator = new Random();
//            float scale = 7f;
//            enemy1.moveTo(scale * (2f * rGenerator.nextFloat() - 1f) , scale * (2f * rGenerator.nextFloat()  - 1f), scale * (2f * rGenerator.nextFloat() - 1f));

        //}
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

    public void moveCamera(float dx, float dy){
        camera.move(dx, dy);
    }
    public void slowCameraTo(float slowfactor){
//        Log.d("Fade-fadeRender-slowfactor", "Value: " + Float.toString(slowfactor));
        camera.setSlowFactor(slowfactor);
    }

    public void touch(float x, float y){
        crosshair.moveTo(x, y);
    }
}
