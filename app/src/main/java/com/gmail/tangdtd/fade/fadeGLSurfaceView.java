package com.gmail.tangdtd.fade;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by Timothy D. Mahon on 1/9/2015.
 */
public class fadeGLSurfaceView extends GLSurfaceView {

    private fadeRenderer fRenderer;
    private float fPreviousX;
    private float fPreviousY;
    private final float TOUCH_SCALE_FACTOR , fTouchThres, fDensity, height, width;

    public fadeGLSurfaceView(Context context, float density, float w, float h) {
        super(context);

        width = w;
        height = h;
        Log.d("Fade-fadeGLSurfaceView-xy ", "w:" + Float.toString(w) + " h: " + Float.toString(h));
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on to the GLSurfaceView
        fRenderer = new fadeRenderer();
        setRenderer(fRenderer);

        fDensity = density;
        TOUCH_SCALE_FACTOR = fDensity / 32f;
        fTouchThres = 2f * fDensity / 2.0f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();
        float dx = fPreviousX - x;
        float dy = y - fPreviousY ;
        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                fRenderer.slowCameraTo(0.1f);
                break;
            case MotionEvent.ACTION_MOVE:
//                if (y > height/2){
                    if (Math.abs(dx) > fTouchThres || Math.abs(dy) > fTouchThres) {
                        fRenderer.moveCamera(dx * TOUCH_SCALE_FACTOR, dy * TOUCH_SCALE_FACTOR);
                    }
//                }else{
//                    fRenderer.moveCamera2(dx * TOUCH_SCALE_FACTOR, dy * TOUCH_SCALE_FACTOR );
//                }
                requestRender();
                break;
            case MotionEvent.ACTION_DOWN:
//                if (y > height/2)
                    fRenderer.onTouch(x, y);
                //fRenderer.slowCameraTo( (float)(Math.sqrt( (double)(dx * dx + dy * dy) ) * 0.01f) );
                //requestRender();
                break;
        }
        fPreviousX = x;
        fPreviousY = y;
        return true;
    }

}