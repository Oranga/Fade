package com.gmail.tangdtd.fade;

/**
 * Created by Menace on 1/10/2015.
 */
public class Crosshair {

    private int shell;
    private float xLoc, yLoc;
    private boolean locked;
    public Crosshair(){

    }
    public Crosshair(float x, float y){
        xLoc = x;
        yLoc = y;
    }
    public void moveTo(float x, float y){
        xLoc = x;
        yLoc = y;
    }
    public void fire(float [] mvpMatrix){


    }

}
