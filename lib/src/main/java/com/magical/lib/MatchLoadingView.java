package com.magical.lib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by magical.zhang on 2018/7/4.
 * Description :
 */
public class MatchLoadingView extends View {

    private ValueAnimator valueAnimator;
    private int bgColor;
    private Paint mLinePaint;
    private int lineWidth;
    private int lineInitWidth = 50;
    private int mHeight;
    private int mWidth;
    private RectF rectF;

    private int[] colors = new int[] {
            Color.parseColor("#33FFFFFF"), Color.parseColor("#80FFFFFF"),
            Color.parseColor("#FFFFFFFF"), Color.parseColor("#80FFFFFF"),
            Color.parseColor("#33FFFFFF")
    };

    private LinearGradient linearGradient;

    public MatchLoadingView(Context context) {
        this(context, null);
    }

    public MatchLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {

        bgColor = Color.parseColor("#33000000");
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStyle(Paint.Style.FILL);

        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        if (mWidth != 0 && mHeight != 0) {

            if (null == linearGradient) {
                linearGradient = new LinearGradient(0, 0, mWidth, mHeight, colors, null,
                        Shader.TileMode.CLAMP);
                mLinePaint.setShader(linearGradient);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(bgColor);

        rectF.set((mWidth / 2 - lineWidth / 2), 0, (mWidth / 2 + lineWidth / 2), mHeight);

        canvas.drawRoundRect(rectF, 10, 10, mLinePaint);
    }

    public void showLoading() {

        if (null == valueAnimator) {
            valueAnimator = ValueAnimator.ofInt(0, mWidth);
            valueAnimator.setDuration(1200);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.setRepeatMode(ValueAnimator.RESTART);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    MatchLoadingView.this.lineWidth =
                            (int) animation.getAnimatedValue() + lineInitWidth;
                    invalidate();
                }
            });
        }

        valueAnimator.start();
    }

    public void stopLoading() {

        if (null != valueAnimator) {
            valueAnimator.cancel();
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.removeAllListeners();
            valueAnimator = null;
        }
    }
}
