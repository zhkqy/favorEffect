package com.example.guxiuzhong.favoreffect.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.bumptech.glide.Glide;
import com.example.guxiuzhong.favoreffect.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class FavorLayoutSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private ArrayList<HeartView> heartViewArrayList = new ArrayList<>();  //装载所有心形
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private int ScreenW, screenH; //屏幕宽高
    private boolean flag;  //线程标识位
    private Thread thread;
    private Context mContext;
    private Timer timer;
    private TimerTask timerTask;
    private FavorLayoutSurfaceviewUtils surfaceviewUtils;
    private Object synchCacheObj = new Object();//申请一个对象  ;

    /***
     * 间隔时间  默认 500 毫秒
     */
    private int intervalTime = 500;


    public FavorLayoutSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FavorLayoutSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    Bitmap bitmapXin1, bitmapXin2, bitmapXin3;

    private void init(Context context) {
        this.mContext = context;

        surfaceviewUtils = new FavorLayoutSurfaceviewUtils();
        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        paint = new Paint();
        paint.setTextSize(40);
        paint.setColor(Color.WHITE);
        setFocusable(true);
        heartViewArrayList.clear();
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        timer = new Timer();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (isOutsideStop || isIngkeeStop) {
            return;
        }

        synchronized (FavorLayoutSurfaceView.class) {
            heartViewArrayList.clear();
        }

        ScreenW = this.getWidth();
        screenH = this.getHeight();
        flag = true;

        thread = new Thread(this);
        thread.start();

        clearTimerTask();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                /****
                 * 点击添加心形
                 */
                addHeart(false);
            }
        };
        timer.schedule(timerTask, 10, intervalTime);
    }

    private void clearTimerTask() {
        if (timerTask != null) {
            try {
                timerTask.cancel();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
        clearTimerTask();
    }

    boolean isOutsideStop = false;  //是否为 除了状态栏 其他的外部停止

    boolean isIngkeeStop = false;   //是否为状态栏外部停止

    public void setIsIngkeeStop(boolean isIngkeeStop) {
        this.isIngkeeStop = isIngkeeStop;
    }

    public void setIsOutsideStop(boolean isOutsideStop) {
        this.isOutsideStop = isOutsideStop;
    }

    public boolean isOutsideStop() {
        return isOutsideStop;
    }

    //最小程序要30毫秒执行刷新一次
    private long refreshIntervalTime = 16;

    @Override
    public void run() {

        bitmapXin1 = null;
        try {
            bitmapXin1 = Glide.
                    with(mContext).
                    load(R.mipmap.xin_purple).
                    asBitmap().
                    into(-1, -1).
                    get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmapXin2 = null;
        try {
            bitmapXin2 = Glide.
                    with(mContext).
                    load(R.mipmap.xin_yellow).
                    asBitmap().
                    into(-1, -1).
                    get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmapXin3 = null;
        try {
            bitmapXin3 = Glide.
                    with(mContext).
                    load(R.mipmap.xin_green).
                    asBitmap().
                    into(-1, -1).
                    get();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void addSelfHeart() {
        addHeart(true);
    }

    public void addHeart(boolean isSelf) {

        if (isSelf) {
            surfaceviewUtils.randomSelfHeart();
        } else {
            surfaceviewUtils.randomHeart();
        }
        String name = surfaceviewUtils.getName();

        if (!TextUtils.isEmpty(name)) {

            Bitmap bitmap = null;

            if (name.equals("purple")) {
                bitmap = bitmapXin1;
            } else if (name.equals("yellow")) {
                bitmap = bitmapXin2;
            } else if (name.equals("green")) {
                bitmap = bitmapXin3;
            }
            if (bitmap == null) {
                return;
            }
            HeartView heartView = new HeartView(name, ScreenW, screenH, bitmap);
            synchronized (FavorLayoutSurfaceView.class) {
                heartViewArrayList.add(heartView);
            }
        }
    }

    public void setOnlineNum(int onlineNum) {
        clearTimerTask();
        onlineToRate(onlineNum);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                /****
                 * 点击添加心形
                 */
                addHeart(false);
            }
        };
        timer.schedule(timerTask, 10, intervalTime);
    }

    /***
     * 根据房间人数  默认红心速率
     */
    private void onlineToRate(int onlineNum) {
        if (onlineNum >= 10000) {
            intervalTime = 200;
        } else if (onlineNum <= 1000) {
            intervalTime = 500;
        } else {
            intervalTime = 500 - (int) ((float) (onlineNum - 1000) / 9000 * 300);
        }
    }
}
