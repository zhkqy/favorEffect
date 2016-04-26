package com.example.guxiuzhong.favoreffect;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

/**
 * @author 顾修忠-guxiuzhong@youku.com/gfj19900401@163.com
 * @Title: FavorLayout
 * @Package com.example.guxiuzhong.favoreffect
 * @Description: http://www.jianshu.com/p/03fdcfd3ae9c  实现Periscope点赞效果
 * @date 15/12/10
 * @time 下午3:43
 */
public class FavorLayout extends RelativeLayout {

    private static final String TAG = "FavorLayout";
    private Random random = new Random();//用于实现随机功能

    private int dHeight;//爱心的高度
    private int dWidth;//爱心的宽度
    private int mHeight;//FavorLayout的高度
    private int mWidth;//FavorLayout的宽度
    private Drawable[] drawables;
    //定义一个LayoutParams 用它来控制子view的位置
    private LayoutParams lp;
    // 我为了实现 变速效果 挑选了几种插补器
    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    // 在init中初始化
    private Interpolator[] interpolators;


    public FavorLayout(Context context) {
        this(context, null);
    }

    public FavorLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FavorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //为了显示区域,我设置了一个背景颜色,随意
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //init里做一些初始化变量的操作
        init();
    }


    private void init() {
        //首先定义 3个代表不同爱心的drawable,以及他们的和drawables
        drawables = new Drawable[3];
        Drawable red = getResources().getDrawable(R.mipmap.red);
        Drawable yellow = getResources().getDrawable(R.mipmap.yellow);
        Drawable blue = getResources().getDrawable(R.mipmap.blue);
        //赋值给drawables
        drawables[0] = red;
        drawables[1] = yellow;
        drawables[2] = blue;

        //注意 我这里3张图片的大小都是一样的,所以我只取了一个
        dHeight = red.getIntrinsicHeight();
        dWidth = red.getIntrinsicWidth();

        //底部 并且 水平居中
        lp = new LayoutParams(dWidth, dHeight);
        lp.addRule(CENTER_HORIZONTAL, TRUE); //这里的TRUE 要注意 不是true
        lp.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        //好了,之后只要给子view设置LayoutParams就可以实现了

        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = dce;
        interpolators[2] = dce;
        interpolators[3] = dce;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //注意!!  获取本身的宽高 需要在测量之后才有宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }

    public void addFavor() {
        ImageView imageView = new ImageView(getContext());
        //随机选一个
        imageView.setImageDrawable(drawables[random.nextInt(3)]);//右边是开区间[0,3)取不到3
        // 设置底部 水平居中
        imageView.setLayoutParams(lp);

        addView(imageView);
        Log.v(TAG, "add后子view数:" + getChildCount());

        Animator set = getAnimator(imageView);
        set.addListener(new AnimEndListener(imageView));
        set.start();

    }

    private Animator getAnimator(View target) {
        AnimatorSet set = getEnterAnimtor(target);

        ValueAnimator bezierValueAnimator = getBezierValueAnimator(target);

        AnimatorSet finalSet = new AnimatorSet();
//        finalSet.playSequentially(set);
        finalSet.playTogether(set, bezierValueAnimator);
//        finalSet.setInterpolator(interpolators[random.nextInt(4)]);//实现随机变速
        finalSet.setTarget(target);
        return finalSet;
    }

    //我封装了一个方法  利用ObjectAnimator AnimatorSet来实现 alpha以及x,y轴的缩放功能
    //target就是爱心
    private AnimatorSet getEnterAnimtor(final View target) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(target, View.ALPHA, 0.3f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, View.SCALE_X, 0.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, View.SCALE_Y, 0.3f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(300);
        enter.setInterpolator(new LinearInterpolator());
        enter.playTogether(alpha, scaleX, scaleY);
        enter.setTarget(target);
        return enter;
    }

    private ValueAnimator getBezierValueAnimator(View target) {

        //初始化一个BezierEvaluator
        BezierEvaluator evaluator = new BezierEvaluator(getPointF(2), getPointF(1));

        //这里最好画个图 理解一下 传入了起点 和 终点
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, new PointF((mWidth - dWidth) / 2, mHeight - dHeight), new PointF(random.nextInt(getWidth()), 0));//随机
        animator.addUpdateListener(new BezierListenr(target));
        animator.setTarget(target);
        animator.setDuration(4000);
        return animator;
    }//这里涉及到另外一个方法:getPointF(),这个是我用来获取途径的两个点
// 这里的取值可以随意调整,调整到你希望的样子就好

    /**
     * 获取中间的两个 点
     *
     * @param scale
     */
    private PointF getPointF(int scale) {

        PointF pointF = new PointF();
        pointF.x = random.nextInt((mWidth - 100));//减去100 是为了控制 x轴活动范围,看效果 随意~~
        //再Y轴上 为了确保第二个点 在第一个点之上,我把Y分成了上下两半 这样动画效果好一些  也可以用其他方法
        pointF.y = random.nextInt((mHeight - 100)) / scale;
        return pointF;
    }

    private class BezierListenr implements ValueAnimator.AnimatorUpdateListener {

        private View target;

        public BezierListenr(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            //这里获取到贝塞尔曲线计算出来的的x y值 赋值给view 这样就能让爱心随着曲线走啦
            PointF pointF = (PointF) animation.getAnimatedValue();
            target.setX(pointF.x);
            target.setY(pointF.y);
            // 这里偷个懒,顺便做一个alpha动画,这样alpha渐变也完成啦
            target.setAlpha(1 - animation.getAnimatedFraction());
        }
    }

    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
            Log.v(TAG, "removeView后子view数:" + getChildCount());
        }
    }
}
