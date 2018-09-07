package com.magical.lib;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by magical.zhang on 2018/7/2.
 * Description : 光圈View
 */
public class MatchCircleView extends View {

    private Bitmap bitmap;
    private float scale;
    private Matrix matrix;
    private ValueAnimator valueAnimator;

    private float animY;   //竖直方向 缩放点位置

    public MatchCircleView(Context context) {
        this(context, null);
    }

    public MatchCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        matrix = new Matrix();
        getBitmap();
    }

    private void getBitmap() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_matching_luminous_circle, options);
    }

    private float getAnimY() {
        return this.animY <= 0 ? getHeight() * 2 / 3f : this.animY;
    }

    public void setAnimY(float dy) {
        this.animY = dy;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (null == bitmap) {
            getBitmap();
        }
        float width = bitmap.getWidth() * scale;
        float height = bitmap.getHeight() * scale;

        canvas.translate(getWidth() / 2 - width / 2, getAnimY() - height / 2);
        matrix.setScale(scale, scale);
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public void doAnim(AnimatorListenerAdapter listener) {

        if (null == valueAnimator) {
            valueAnimator = ValueAnimator.ofFloat(0.07f, 2);
            valueAnimator.setDuration(900);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    MatchCircleView.this.scale = (float) animation.getAnimatedValue() * 12;

                    float animatedFraction = animation.getAnimatedFraction();
                    float alpha = 0.9f - animatedFraction;
                    if (alpha < 0) {
                        alpha = 0;
                    }
                    setAlpha(alpha);
                    invalidate();
                }
            });
            if (null != listener) {
                valueAnimator.addListener(listener);
            }
        }

        valueAnimator.start();
    }

    public void stopAnim() {
        if (null != valueAnimator) {
            valueAnimator.cancel();
            valueAnimator.removeAllListeners();
            valueAnimator = null;
        }

        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }
}
