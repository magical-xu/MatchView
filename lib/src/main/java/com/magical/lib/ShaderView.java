package com.magical.lib;

import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;

/**
 * Created by magical.zhang on 2018/7/2.
 * Description : 光点View
 */
public class ShaderView extends View {

    private ValueAnimator valueAnimator;
    private Paint paint;
    private float mItemDistance = 60;
    private float mCircleRadius = 12.5f;
    private float mBlurRadius = 10f;
    private int mDrawCount;
    private int mWidth;
    private ArrayList<Float> cxList;
    private float offset;
    private float animY;
    private float density;

    public ShaderView(Context context) {
        this(context, null);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int dp2px(float dpValue) {
        return (int) (dpValue * density + 0.5f);
    }

    private float getAnimY() {
        return this.animY <= 0 ? getHeight() * 2 / 3 : this.animY;
    }

    public void setAnimY(float dy) {
        this.animY = dy;
    }

    private void reset() {

        int ceil = (int) Math.ceil(mWidth / mItemDistance);
        mDrawCount = ceil % 2 == 0 ? ceil + 3 : ceil + 2;

        getCircleX();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        if (width > 0 && width != mWidth) {
            mWidth = width;
            reset();
        }
    }

    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        density = getResources().getDisplayMetrics().density;
        mItemDistance = dp2px(mItemDistance);
        mCircleRadius = dp2px(mCircleRadius);

        cxList = new ArrayList<>();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setMaskFilter(new BlurMaskFilter(mBlurRadius, BlurMaskFilter.Blur.SOLID));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < cxList.size(); i++) {

            float cx = cxList.get(i);
            canvas.drawCircle(cx - offset, getAnimY(), mCircleRadius, paint);
        }
    }

    private void getCircleX() {

        cxList.clear();
        int centerIndex = mDrawCount / 2;
        int centerX = mWidth / 2;

        for (int i = 0; i < mDrawCount; i++) {

            int count = i - centerIndex;
            float cx = centerX + count * mItemDistance;

            cxList.add(i, cx);
        }
    }

    public void doAnim(AnimatorListenerAdapter listener) {

        if (null == valueAnimator) {
            valueAnimator = ValueAnimator.ofFloat(0, mItemDistance);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    offset = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            if (null != listener) {
                valueAnimator.addListener(listener);
            }
            valueAnimator.setDuration(450);
        }

        valueAnimator.start();
    }

    public void stopAnim() {
        if (null != valueAnimator) {
            valueAnimator.cancel();
            valueAnimator.removeAllListeners();
            valueAnimator = null;
        }
    }
}
