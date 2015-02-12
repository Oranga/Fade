package com.gmail.tangdtd.fade;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Timothy D. Mahon on 1/6/2015.
 */
public class fadeRenderer implements GLSurfaceView.Renderer {

    private float [] fMVPMatrix = new float[16];
    private int viewWidth, viewHeight;
    private Crosshair crosshair;
    private Camera camera, camera2;
    private Background bg;
    private List<Enemy> enemies = new ArrayList<Enemy>();
    private boolean touch;
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        bg = new Background();
        crosshair = new Crosshair();

        enemies.add(new Enemy(3, 1f, 0f, 10f));
        enemies.add(new Enemy(5, 0f, 10f, -10f));
        enemies.add(new Enemy(6, 5f, 5f, 5f));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        viewWidth = width;
        viewHeight = height;
//        Log.d("Fade-fadeRenderer-viewHW", "w:" + viewWidth + " h: " + viewHeight);

        camera = new Camera();
        /*camera2 = new Camera(-30f, 0f, 0f,
                             0f, 0f, 0f,
                             0f, 1f, 0f,
                             viewWidth, viewHeight/2f, 3f, 200f);*/
        float ratio = (float) viewWidth / ((float) viewHeight);
        float [] fProjMatrix = new float[16];
        Matrix.frustumM(fProjMatrix, 0,  -ratio, ratio, -1f, 1f, 3f, 200f);
        camera.setProjMatrix(fProjMatrix);
        crosshair.setViewPort(0f, 0f, viewWidth, viewHeight);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glViewport(0,0, viewWidth, viewHeight);
        fMVPMatrix = camera.view();
        bg.draw(fMVPMatrix);
        for (int i = 0; i < enemies.size(); i++){
            if (touch){
                int ret = enemies.get(i).isHit(crosshair.getNormalRay());
                if ( ret >= 0){
                    enemies.get(i).onHit();
                }
            }
            enemies.get(i).draw(fMVPMatrix);
        }
        crosshair.draw(fMVPMatrix);
//        GLES20.glViewport(0, viewHeight/2, viewWidth, viewHeight);
//        GLES20.glDisable(GLES20.GL_CULL_FACE);
//        float [] observe = camera2.view();
//        for (int i = 0; i < enemies.size(); i++){
//            enemies.get(i).draw(observe);
//        }
//        crosshair.draw(observe);
//        GLES20.glEnable(GLES20.GL_CULL_FACE);
//            Random rGenerator = new Random();
//            float scale = 7f;
//            enemy1.moveTo(scale * (2f * rGenerator.nextFloat() - 1f) , scale * (2f * rGenerator.nextFloat()  - 1f), scale * (2f * rGenerator.nextFloat() - 1f));
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
        camera.move(dy, dx, 0);
        crosshair.fireAndMove(fMVPMatrix);
    }
    /*public void moveCamera2(float dx, float dy){
        camera2.move(dy, 0, -dx);
    }*/
    public void slowCameraTo(float slowfactor){
//        Log.d("Fade-fadeRender-slowfactor", "Value: " + Float.toString(slowfactor));
        camera.setSlowFactor(slowfactor);
        touch = false;
    }

    public void onTouch(float x, float y){
        Log.d("Fade-fadeRenderer-touch-xy ", "x:" + Float.toString(x) + " y: " + Float.toString(y));
        crosshair.moveTo(x, (float)viewHeight - y);
        crosshair.fire(fMVPMatrix);
        touch = true;
    }
}
