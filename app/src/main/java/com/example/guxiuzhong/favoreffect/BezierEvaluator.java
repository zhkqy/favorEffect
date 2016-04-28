package com.example.guxiuzhong.favoreffect;

import android.animation.TypeEvaluator;
import android.graphics.PointF;
import android.util.Log;

/**
 * @author 顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * @Title: BezierEvaluator
 * @Package com.example.guxiuzhong.favoreffect
 * @Description:
 * @date 15/12/10
 * @time 下午4:16
 */
public class BezierEvaluator  implements TypeEvaluator<PointF> {
        private PointF pointF1;//途径的两个点
        private PointF pointF2;
        public BezierEvaluator(PointF pointF1,PointF pointF2){
                this.pointF1 = pointF1;
                this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float time, PointF startValue, PointF endValue) {

//                Log.i("BezierEvaluator","time = "+time+"    startValue   x=  "+startValue.x+"  y= "+startValue.y+"    "+"endValue   x=  "+endValue.x+"  y= "+endValue.y);
                float timeLeft = 1.0f - time;
                PointF point = new PointF();//结果

                PointF point0 = startValue;//起点

                PointF point3 = endValue;//终点
                //代入公式
                point.x = timeLeft * timeLeft * timeLeft * (point0.x)
                        + 3 * timeLeft * timeLeft * time * (pointF1.x)
                        + 3 * timeLeft * time * time * (pointF2.x)
                        + time * time * time * (point3.x);

                point.y = timeLeft * timeLeft * timeLeft * (point0.y)
                        + 3 * timeLeft * timeLeft * time * (pointF1.y)
                        + 3 * timeLeft * time * time * (pointF2.y)
                        + time * time * time * (point3.y);
                return point;
        }
}
