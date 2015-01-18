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
    private int viewWidth, viewHeight;
    private Crosshair crosshair;
    private Camera camera, camera2;
    private Background bg;
    private Enemy enemy1, enemy2, enemy3, camEnemy;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        bg = new Background();
        crosshair = new Crosshair();
        camEnemy = new Enemy(4, 0f, 0f, 0f);
        enemy1 = new Enemy(3, 1f, 0f, 10f);
        enemy2 = new Enemy(5, 0f, 10f, -10f);
        enemy3 = new Enemy(6, 5f, 5f, 5f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewWidth = width;
        viewHeight = height;
        camera = new Camera();
        crosshair.setWidthHeight(viewWidth, viewHeight/2f);
        float ratio = (float) viewWidth/(viewHeight/2f);
        float [] fProjMatrix = new float[16];
        Matrix.frustumM(fProjMatrix, 0,  -ratio, ratio, -1f, 1f, 3f, 200f);
        camera.setProjMatrix(fProjMatrix);
        camera2 = new Camera(-30f, 0f, 0f,
                               0f, 0f, 0f,
                               0f, 1f, 0f,
                               (float)viewWidth, (viewHeight/2f), 3f, 150f  );
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glViewport(0,0, viewWidth, viewHeight/2);
//        GLES20.glViewport(0,0, viewWidth, viewHeight);
        fMVPMatrix = camera.view();
        bg.draw(fMVPMatrix);
        enemy1.draw(fMVPMatrix);
        enemy2.draw(fMVPMatrix);
        enemy3.draw(fMVPMatrix);
        crosshair.draw(camera.getProjMatrix(), camera.getViewMatrix(), fMVPMatrix, 0f, 0f, (float)viewWidth, (float)viewHeight/2f);
        GLES20.glViewport(0, viewHeight/2, viewWidth, viewHeight/2);
        float [] observe = camera2.view();
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        bg.draw(observe);
        camEnemy.draw(observe);
        enemy1.draw(observe);
        enemy2.draw(observe);
        enemy3.draw(observe);
        crosshair.draw(observe);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
//        if (enemy1.isStopped()){
//            Random rGenerator = new Random();
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
        camera.move(dy, dx, 0); camEnemy.setRotation(-dy, -dx, 0);
    }
    public void moveCamera2(float dx, float dy) { camera2.move(0, dx, dy);}
    public void slowCameraTo(float slowfactor){
//        Log.d("Fade-fadeRender-slowfactor", "Value: " + Float.toString(slowfactor));
        camera.setSlowFactor(slowfactor);

    }

    public void touch(float x, float y){
        crosshair.moveTo(x, y); crosshair.fire();
    }
}
