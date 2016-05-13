package com.example.guxiuzhong.favoreffect.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Debug;
import android.os.SystemClock;
import android.util.Log;

import com.example.guxiuzhong.favoreffect.R;

import java.util.Random;

/**
 * Created by chenlei on 2016/5/11.
 */
public class HeartView {
    Bitmap bitmap;
    private float initX = 0, initY = 0;  // 初始心形的位置
    private float distanceInitY = 0;
    Paint paint;
    private Random random = new Random();//用于实现随机功能
    private int screenW, screenH;
    int alpha = 255;
    /***
     * 贝塞尔曲线中间两个点
     */
    private PointF pointA;
    private PointF pointB;
    private float bezierDuration = 3500f;

    private float scaleDuation = 300f;

    /**
     * 贝塞尔曲线起点和终点
     */
    private PointF startPointf;
    private PointF endPointf;

    /***
     * 开始的时间
     */
    private long startTime;

    float scaleX = 0;
    float scaleY = 0;

    public HeartView(Context mContext, int screenW, int screenH) {
        startTime = SystemClock.uptimeMillis();
        this.screenW = screenW;
        this.screenH = screenH;
        pointA = getPointF(2);
        pointB = getPointF(1);

        int temp = random.nextInt(3);

        if(temp==0){
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.red);
        }else if(temp==1){
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.blue);
        }else if(temp ==2){
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.yellow);
        }

        startPointf = new PointF((screenW - bitmap.getWidth()) / 2, screenH - bitmap.getHeight());
        endPointf = new PointF(random.nextInt(screenW/2)+screenW/4, 0);

        paint = new Paint();
        paint.setAlpha(alpha);
        paint.setAntiAlias(true);
        initX = screenW / 2 - bitmap.getWidth() / 2;
        initY = screenH - bitmap.getHeight();

        distanceInitY =( screenH - bitmap.getHeight())-( screenH - bitmap.getHeight())*16/bezierDuration;

        int add = random.nextInt((int)(distanceInitY/2));

        Log.i("sssssss","distanceInitY="+distanceInitY+"    add="+add);
        distanceInitY = distanceInitY+add ;
    }

    /****
     * 绘制心形
     */
    public void drawHeart(Canvas canvas) {
        canvas.save();
        canvas.scale(scaleX, scaleY, screenW / 2, screenH);
        canvas.drawBitmap(bitmap, initX, initY, paint);
        canvas.restore();
    }

    /****
     * 绘制心形位置逻辑
     */

    long startLong = 0;

    public void drawHeartLogic() {
        /***
         * 1.根据bezierDuration计算0-1之间的时间
         */

        long currentTime = SystemClock.uptimeMillis();
        float bezierTime = (currentTime - startTime) / bezierDuration;

        Log.i("heartView", "currentTime = " + currentTime + "  startTime= " + startTime +
                "   currentTime - startTime=" + (currentTime - startTime) + "    bezierDuration=" + bezierDuration+"   bezierTime=  "+bezierTime);

        PointF pointF = getBezierPointF(bezierTime, startPointf, endPointf, pointA, pointB);


        /***
         * 1.设置滚动路线
         */
        initX = pointF.x;
//        initY =  pointF.y;


        /****
         * 開始
         */

        long middleTime =  SystemClock.uptimeMillis()-startTime;
        if(middleTime>bezierDuration){
            initY = 0;
        }else{
            initY =( screenH - bitmap.getHeight())-( screenH - bitmap.getHeight())*middleTime/bezierDuration;
        }

//        initX =   (screenW - bitmap.getWidth()) / 2;

        /****
         *結束
         */
//        Log.i("heartView", "initX = " + initX + "  initY= " + initY + "    bezierTime=" + bezierTime);

        paint.setAlpha((int) (255 - (255f * bezierTime)));
        if (bezierTime >= 1) {
            paint.setAlpha(0);
        }

        float scaleTime = (currentTime - startTime) / scaleDuation;

        float tempScaleTime = 0.4f + scaleTime;
        if (tempScaleTime <= 1) {
            scaleX = scaleY = +tempScaleTime;

        } else {
            scaleX = scaleY = 1;
        }
    }


    /***
     * 是否达到顶部
     */
    public boolean isTop() {
        return initY <= 0;
    }

    private PointF getPointF(int scale) {
        PointF pointF = new PointF();
        pointF.x = random.nextInt((screenW/2))+screenW/4;
        pointF.y = random.nextInt((screenH/2)) +screenH/4;
        return pointF;
    }

    public PointF getBezierPointF(float time, PointF startValue, PointF endValue, PointF middleA, PointF middleB) {

        float timeLeft = 1.0f - time;
        PointF point = new PointF();//结果

        PointF point0 = startValue;//起点

        PointF point3 = endValue;//终点
        //代入公式
        point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                + 3 * timeLeft * timeLeft * time * (middleA.x)
                + 3 * timeLeft * time * time * (middleB.x)
                + time * time * time * (point3.x);

        point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                + 3 * timeLeft * timeLeft * time * (middleA.y)
                + 3 * timeLeft * time * time * (middleB.y)
                + time * time * time * (point3.y);
        return point;

    }

}
