package com.example.guxiuzhong.favoreffect.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.FactoryConfigurationError;

public class FavorLayoutSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public FavorLayoutSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FavorLayoutSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    private ArrayList<HeartView> heartViewArrayList = new ArrayList<>();  //装载所有心形
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private int ScreenW, screenH; //屏幕宽高
    private boolean flag;  //线程标识位
    private Thread thread;
    private Context mContext;


    Bitmap board = null;
    Canvas boardCanvas = null;

    private void init(Context context) {
        this.mContext = context;
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);
        setFocusable(true);
        heartViewArrayList.clear();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                /****
                 * 点击添加心形
                 */
                synchronized (FavorLayoutSurfaceView.class) {
                    HeartView heartView = new HeartView(mContext, ScreenW, screenH);
                    heartViewArrayList.add(heartView);
                }
            }
        }, 1000, 200);

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        ScreenW = this.getWidth();
        screenH = this.getHeight();
        flag = true;

        thread = new Thread(this);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
        if (thread != null) {
            thread.interrupt();
        }

    }


    //最小程序要30毫秒执行刷新一次
    private long refreshIntervalTime = 16;

    @Override
    public void run() {
        while (flag) {
            long startTime = System.currentTimeMillis();
            myDraw();
            long endTime = System.currentTimeMillis();

            if ((endTime - startTime) < refreshIntervalTime) {
                try {
                    Thread.sleep(refreshIntervalTime - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void refreshBackground(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
    }


    private void myDraw() {
        Canvas canvas = surfaceHolder.lockCanvas();
        if (surfaceHolder == null || canvas == null) {
            return;
        }
        try {
            if (canvas != null) {
                refreshBackground(canvas);
                synchronized (FavorLayoutSurfaceView.class) {
                    Iterator<HeartView> iter = heartViewArrayList.iterator();
                    while (iter.hasNext()) {
                        HeartView heartView = iter.next();

                        heartView.drawHeart(canvas);
                        heartView.drawHeartLogic();

                        if (heartView.isTop()) {
                            iter.remove();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }
}
