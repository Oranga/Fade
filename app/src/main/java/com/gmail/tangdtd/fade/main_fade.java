package com.gmail.tangdtd.fade;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Timothy D. Mahon on 1/7/2015.
 */

public class main_fade extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fade);
    }

    @Override
    protected void onResume() {

        super.onResume();
    }
    @Override
    protected void onPause() {

        super.onPause();
    }
    public void Play(View view){
        Intent intent = new Intent(this, gameViewport.class);
        startActivity(intent);
    }

}
