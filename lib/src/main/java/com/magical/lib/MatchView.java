package com.magical.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.magical.lib.util.ImageLoader;

/**
 * Created by magical.zhang on 2018/7/2.
 * Description : 匹配搜索对手的View
 */
public class MatchView extends FrameLayout {

    private ShaderView shaderView;
    private MatchCircleView circleView;
    private TextView textView;
    private TextView searchTextView;
    private Button cancelBtn;
    private int count;
    private boolean startAnim;
    private IMatchListener listener;
    private int mViewHeight;

    private AnimatorListenerAdapter mShaderListener;
    private AnimatorListenerAdapter mCircleListener;

    public MatchView(@NonNull Context context) {
        this(context, null);
    }

    public MatchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int height = getHeight();
        if (height > 0 && height != mViewHeight) {
            mViewHeight = height;
            setAnimCenter(mViewHeight);
        }
    }

    private void init() {

        View rootView =
                LayoutInflater.from(getContext()).inflate(R.layout.item_match_view, this, true);

        ImageView bgRootView = rootView.findViewById(R.id.id_match_bg);
        ImageLoader.getInstance().loadBigLocal(R.drawable.bg_matching_background_icon, bgRootView);

        shaderView = rootView.findViewById(R.id.id_shader);
        shaderView.setVisibility(View.GONE);
        circleView = rootView.findViewById(R.id.id_circle_view);
        textView = rootView.findViewById(R.id.id_count_time);
        searchTextView = rootView.findViewById(R.id.id_search);
        searchTextView.setVisibility(View.GONE);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        WindowManager windowManager =
                (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            int height = displayMetrics.heightPixels;
            setAnimCenter(height);
        }

        cancelBtn = rootView.findViewById(R.id.id_cancel);
        cancelBtn.setText("开始匹配");
        cancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String btnText = cancelBtn.getText().toString();
                if (btnText.equals("开始匹配")) {
                    startMatch();
                } else {
                    if (null != listener) {
                        listener.onCancel();
                    }
                }
            }
        });
    }

    public void setBtnText(String text) {
        cancelBtn.setText(text);
    }

    private void setAnimCenter(int height) {
        float scaleY = height * 3 / 5f;
        shaderView.setAnimY(scaleY);
        circleView.setAnimY(scaleY);
    }

    /**
     * 开始匹配
     */
    public void startMatch() {
        count = 0;
        shaderView.setVisibility(View.VISIBLE);
        circleView.setVisibility(View.VISIBLE);
        searchTextView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        startAnim = true;

        cancelBtn.setText("取消");

        if (null != listener) {
            listener.onMatchStart();
        }
        doAnim();
    }

    private void doAnim() {

        if (null == mShaderListener) {

            mShaderListener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    textView.setText(String.valueOf(++count));
                    circleView.doAnim(mCircleListener);
                }
            };
        }

        if (null == mCircleListener) {

            mCircleListener = new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);

                    if (null != listener) {
                        listener.onCircleStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    if (startAnim) {

                        if (null != listener) {
                            listener.onFrame(count);
                        }
                        doAnim();
                    }
                }
            };
        }

        if (null != shaderView) {
            shaderView.doAnim(mShaderListener);
        }
    }

    /**
     * 停止匹配
     */
    public void stopMatch() {

        startAnim = false;
        shaderView.setVisibility(View.GONE);
        searchTextView.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        circleView.setVisibility(View.GONE);

        cancelBtn.setText("开始匹配");
        textView.setText("");

        if (null != shaderView) {
            shaderView.stopAnim();
        }

        if (null != circleView) {
            circleView.stopAnim();
        }
    }

    public void setMatchListener(IMatchListener listener) {
        this.listener = listener;
    }

    public interface IMatchListener {

        void onFrame(int second);

        void onCircleStart();

        void onCancel();

        void onMatchStart();
    }
}
