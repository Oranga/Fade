package com.gmail.tangdtd.fade;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;



public class main_fade extends Activity {

    private GLSurfaceView fGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Create a GLSurfaceView instance and set it
        fGLView = new fadeGLSurfaceView(this, displayMetrics.density, displayMetrics.widthPixels, displayMetrics.heightPixels);
        setContentView(fGLView);
    }

    @Override
    protected void onResume() {
        fGLView.onResume();
        super.onResume();
    }
    @Override
    protected void onPause() {
        fGLView.onPause();
        super.onPause();
    }

}
