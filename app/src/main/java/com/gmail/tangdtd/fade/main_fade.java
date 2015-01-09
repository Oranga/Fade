package com.gmail.tangdtd.fade;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


public class main_fade extends Activity {

    private GLSurfaceView fGLView;
    private fadeRenderer fRenderer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a GLSurfaceView instance and set it
        fGLView = new fadeGLSurfaceView(this);
        setContentView(fGLView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fGLView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        fGLView.onPause();
    }

    class fadeGLSurfaceView extends GLSurfaceView {

        public fadeGLSurfaceView(Context context) {

            super(context);

            // Create an OpenGL ES 2.0 context
            setEGLContextClientVersion(2);

            setEGLConfigChooser(8, 8, 8, 8, 16, 0);

            // Set the Renderer for drawing on to the GLSurfaceView
            fRenderer = new fadeRenderer();
            setRenderer(fRenderer);
            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            fDensity = displayMetrics.density;
            System.out.println(fDensity);
        }
        private float fPreviousX;
        private float fPreviousY;
        private float fDensity;

        private final float TOUCH_SCALE_FACTOR = 0.5f;

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = fPreviousX - x;
                    float dy = y - fPreviousY;
                    fRenderer.moveCamera(dx * TOUCH_SCALE_FACTOR, dy * TOUCH_SCALE_FACTOR);
                    requestRender();

            }
            fPreviousX = x;
            fPreviousY = y;
            return true;
        }

    }
}
