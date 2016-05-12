package com.example.guxiuzhong.favoreffect.surfaceview;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    private float mRed;
    private float mGreen;
    private float mBlue;

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    public void setColor(float r, float g, float b){
        this.mRed = r;
        this.mGreen = g;
        this.mBlue = b;
    }
}