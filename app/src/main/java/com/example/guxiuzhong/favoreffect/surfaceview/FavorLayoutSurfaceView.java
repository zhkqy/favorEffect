package com.example.guxiuzhong.favoreffect.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.InputStream;
import java.lang.ref.SoftReference;
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
    private int jishicount = 0;
    private ArrayList<HeartViewModel> imageCache = new ArrayList<HeartViewModel>();
    private FavorLayoutSurfaceviewUtils surfaceviewUtils;

    private int onlineNum = 10000;


    public FavorLayoutSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public FavorLayoutSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {

        surfaceviewUtils = new FavorLayoutSurfaceviewUtils();
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
        timer = new Timer();

        jishicount = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

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
        timer.schedule(timerTask, 1000, 200);
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
                            Bitmap bitmap = heartView.getBitmap();
                            addBitmapToCache(heartView.getName(), bitmap);
//                            if (bitmap != null && !bitmap.isRecycled()) {
//                                bitmap.recycle();
//                            }
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

    public void addBitmapToCache(String name, Bitmap bitmap) {

        // 软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = new SoftReference<Bitmap>(bitmap);
        // 添加该对象到Map中使其缓存
        imageCache.add(new HeartViewModel(name, softBitmap));
    }

    public Bitmap getBitmapByPath(String name) {

        // 从缓存中取软引用的Bitmap对象
        SoftReference<Bitmap> softBitmap = null;
        boolean flag = true;

        Iterator<HeartViewModel> iter = imageCache.iterator();
        while (flag && iter.hasNext()) {
            HeartViewModel viewModel = iter.next();
            if (name.equals(viewModel.getName())) {
                softBitmap = viewModel.getSoftBitmap();
                iter.remove();
                flag = false;
            }
        }
        // 判断是否存在软引用
        if (softBitmap == null) {
            return null;
        }
        // 取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
        Bitmap bitmap = softBitmap.get();
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addSelfHeart();
        return super.onTouchEvent(event);

    }

    public void addSelfHeart() {
        addHeart(true);
    }

    public void addHeart(boolean isSelf) {
        synchronized (FavorLayoutSurfaceView.class) {

            if (jishicount >= onlineNum) {
                clearTimerTask();
                return;
            }
            InputStream is = null;

            if(isSelf){
                surfaceviewUtils.randomSelfHeart();
            }else{
                surfaceviewUtils.randomHeart();
            }

            if (!TextUtils.isEmpty(surfaceviewUtils.getName())&&surfaceviewUtils.getValue()!=0) {

                Bitmap b = getBitmapByPath(surfaceviewUtils.getName());
                if (b != null) {
                    HeartView heartView = new HeartView(surfaceviewUtils.getName(), ScreenW, screenH, b);
                    heartViewArrayList.add(heartView);
                } else {
                    is = mContext.getResources().openRawResource(surfaceviewUtils.getValue());
                    Bitmap bitmap = null;
                    try {
                        // 实例化Bitmap
                        bitmap = BitmapFactory.decodeResource(mContext.getResources(), surfaceviewUtils.getValue());
                    } catch (OutOfMemoryError e) {
                        //
                    } catch (Exception e) {

                    }
                    if (bitmap == null) {
                        return;
                    }
                    HeartView heartView = new HeartView( surfaceviewUtils.getName(), ScreenW, screenH, bitmap);
                    heartViewArrayList.add(heartView);
                    jishicount++;
                }
            }
        }
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
        jishicount=0;
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
        timer.schedule(timerTask, 1000, 200);
    }
}
