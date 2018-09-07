package com.magical.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.magical.lib.font.FontTextView;
import com.magical.lib.model.MatchInfo;
import com.magical.lib.model.UserInfo;
import com.magical.lib.util.DensityUtil;
import com.magical.lib.util.ImageLoader;
import com.magical.lib.widget.HeadImageView;

/**
 * 匹配成功界面
 *
 * @author magical.zhang
 * @date 2018/7/3
 */
public class MatchSuccessView extends FrameLayout {

    /**
     * 普通模式
     */
    private static final int MODE_NORMAL = 0;
    /**
     * 赏金模式 有金币动画
     */
    private static final int MODE_REWARD = 1;

    /**
     * 性别男
     */
    private static final int SEX_MALE = 0;

    /**
     * 性别女
     */
    private static final int SEX_FEMALE = 1;

    private View mContentView;
    private ImageView mBlueView;
    private ImageView mRedView;
    private ImageView mLeftPeople;
    private ImageView mRightPeople;
    private ImageView mBlueShadow;
    private ImageView mRedShadow;
    private ViewGroup mLeftCard;
    private ViewGroup mRightCard;
    private HeadImageView mLeftAvatar;
    private HeadImageView mRightAvatar;
    private TextView mLeftNick;
    private TextView mRightNick;

    private ImageView mVSBgView;
    private ImageView mVSIconView;

    private LottieAnimationView mBlueLottieView;
    private LottieAnimationView mRedLottieView;
    private FontTextView mCoinTextView;

    private MatchLoadingView mLoadingView;

    /**
     * 红蓝背景图 无缩放 三角高度的一半 像素是通过图片测量的
     */
    private int triangleHeight = 24;
    private int shadowMove;
    private int mBlueMoveHeight;
    private int mRedMoveHeight;
    private int avatarHeight;
    private int screenWidth;
    private float mPersonOffset;
    private int moveY;

    private long mCoinTotal;
    private int mCurMode;
    private int mSelfGender;
    private int mOtherGender;

    private ISuccessListener listener;
    private int mViewHeight;
    private boolean mResizeSuc;
    private boolean hadReverseShown;

    public MatchSuccessView(@NonNull Context context) {
        this(context, null);
    }

    public MatchSuccessView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchSuccessView(@NonNull Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int height = getHeight();
        if (height > 0 && height != mViewHeight) {

            postViewHeight(height);
        }
    }

    private void postViewHeight(int height) {
        mViewHeight = height;
        post(new Runnable() {
            @Override
            public void run() {
                resizeBgLayout();
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w == 0 || h == 0 || oldw == 0 || oldh == 0 || h == oldh) {
            return;
        }

        postViewHeight(h);
    }

    /**
     * 设置游戏模式
     *
     * @param mode 普通模式{@link #MODE_NORMAL} 赏金模式{@link #MODE_REWARD}
     */
    public void setMode(@IntRange(from = 0, to = 1) int mode) {
        this.mCurMode = mode;
    }

    /**
     * 设置匹配双方用户数据
     */
    public void setMatchData(MatchInfo matchData) {

        if (null == matchData) {
            return;
        }

        //自己的数据
        UserInfo from = matchData.getFrom();
        if (null != from) {

            mRightAvatar.setHeadImageUrl(from.avatar);
            mRightNick.setText(from.nickname);
            mSelfGender = from.isMale() ? SEX_MALE : SEX_FEMALE;
            ImageLoader.getInstance().loadBigLocal(getMySelfSexModel(), mRightPeople);
        }

        //对手的数据
        UserInfo to = matchData.getTo();
        if (null != to) {

            mLeftAvatar.setHeadImageUrl(to.avatar);
            mLeftNick.setText(to.nickname);
            mOtherGender = to.isMale() ? SEX_MALE : SEX_FEMALE;
            ImageLoader.getInstance().loadBigLocal(getOtherSexModel(), mLeftPeople);
        }

        //模式
        setMode(matchData.isRewardMode() ? MODE_REWARD : MODE_NORMAL);
        if (matchData.isRewardMode()) {
            this.mCoinTotal = matchData.totalCoin;
        } else {
            mCoinTextView.setVisibility(View.GONE);
            mBlueLottieView.setVisibility(View.GONE);
            mRedLottieView.setVisibility(View.GONE);
        }
        resizeRightPeopleLocation();
    }

    /**
     * 因为男女的图片高不一致 显示效果有偏差
     * 需要重设右侧人物模型 底边距
     */
    private void resizeRightPeopleLocation() {

        LayoutParams layoutParams = (LayoutParams) mRightPeople.getLayoutParams();
        if (mSelfGender == SEX_MALE) {

            layoutParams.bottomMargin = -DensityUtil.dp2px(160);
        } else {
            layoutParams.bottomMargin = -DensityUtil.dp2px(120);
        }
        mRightPeople.setLayoutParams(layoutParams);
    }

    private void init() {
        initView();
        initOffset();
    }

    private void initOffset() {
        shadowMove = 100;
        avatarHeight = DensityUtil.dp2px(220);
        mPersonOffset = DensityUtil.dp2px(10);

        WindowManager windowManager =
                (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (null != windowManager) {

            moveY = DensityUtil.dp2px(triangleHeight);

            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
        }

        mBlueView.setVisibility(View.GONE);
        mRedView.setVisibility(View.GONE);
    }

    /**
     * 修复华为手机等有 底部虚拟导航栏的手机
     * 在匹配对决动画时 动态显示隐藏 虚拟导航栏导致布局变化后
     * 界面红蓝背景高度错误的问题
     */
    private void resizeBgLayout() {

        if (mViewHeight <= 0) {
            return;
        }

        ViewGroup.LayoutParams blueParams = mBlueView.getLayoutParams();
        mBlueMoveHeight = mViewHeight / 2 + moveY;
        blueParams.height = mBlueMoveHeight;
        mBlueView.setLayoutParams(blueParams);

        ViewGroup.LayoutParams redParams = mRedView.getLayoutParams();
        mRedMoveHeight = mViewHeight / 2 + moveY;
        redParams.height = mRedMoveHeight;
        mRedView.setLayoutParams(redParams);

        mResizeSuc = true;
    }

    private void initView() {

        View rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.item_match_success_view, this, true);

        mLoadingView = rootView.findViewById(R.id.id_loading_view);
        mContentView = rootView.findViewById(R.id.id_content);
        mBlueView = rootView.findViewById(R.id.id_blue_side);
        mRedView = rootView.findViewById(R.id.id_red_side);
        mVSBgView = rootView.findViewById(R.id.id_vs_bg);
        ImageLoader.getInstance()
                .loadBigLocal(R.drawable.img_matching_vs_separate_whole1, mVSBgView);
        mVSBgView.setVisibility(View.GONE);
        mVSIconView = rootView.findViewById(R.id.id_vs_icon);
        mVSIconView.setVisibility(View.GONE);

        mBlueShadow = rootView.findViewById(R.id.id_blue_shadow);
        mRedShadow = rootView.findViewById(R.id.id_red_shadow);
        ImageLoader.getInstance()
                .loadBigLocal(R.drawable.bg_matching_blus_side_shadow1, mBlueShadow);
        ImageLoader.getInstance().loadBigLocal(R.drawable.bg_matching_red_side_shadow1, mRedShadow);
        mBlueShadow.setVisibility(View.GONE);
        mRedShadow.setVisibility(View.GONE);

        mLeftCard = rootView.findViewById(R.id.id_blue_card);
        mRightCard = rootView.findViewById(R.id.id_red_card);
        mLeftCard.setVisibility(View.GONE);
        mRightCard.setVisibility(View.GONE);

        mLeftAvatar = rootView.findViewById(R.id.id_blue_avatar);
        mRightAvatar = rootView.findViewById(R.id.id_red_avatar);
        mLeftNick = rootView.findViewById(R.id.id_blue_nick);
        mRightNick = rootView.findViewById(R.id.id_red_nick);

        mLeftPeople = rootView.findViewById(R.id.id_left_person);
        mRightPeople = rootView.findViewById(R.id.id_right_person);
        mLeftPeople.setVisibility(View.GONE);
        mRightPeople.setVisibility(View.GONE);

        mBlueLottieView = rootView.findViewById(R.id.id_lottie_view_blue);
        mBlueLottieView.setImageAssetsFolder("blue_coin/images");
        mBlueLottieView.setAnimation("blue_coin/data.json");
        mBlueLottieView.useHardwareAcceleration(true);
        mBlueLottieView.enableMergePathsForKitKatAndAbove(true);
        mBlueLottieView.addAnimatorUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float fraction = animation.getAnimatedFraction();
                try {
                    mCoinTextView.setText(String.valueOf(Math.round(mCoinTotal * fraction)));
                } catch (Exception ex) {
                    mCoinTextView.setVisibility(View.GONE);
                    ex.printStackTrace();
                }
            }
        });
        mBlueLottieView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                showLoadingView();
                mBlueLottieView.setVisibility(View.GONE);
                mRedLottieView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                if (null != listener) {
                    listener.onShowCoin();
                }
            }
        });

        mRedLottieView = rootView.findViewById(R.id.id_lottie_view_red);
        mRedLottieView.setImageAssetsFolder("red_coin/images");
        mRedLottieView.setAnimation("red_coin/data.json");
        mRedLottieView.useHardwareAcceleration(true);
        mRedLottieView.enableMergePathsForKitKatAndAbove(true);

        mCoinTextView = rootView.findViewById(R.id.id_coin_total);
    }

    /**
     * 对外动画接口
     */
    public void postRunAnim() {

        hadReverseShown = false;
        post(new Runnable() {
            @Override
            public void run() {

                if (mViewHeight <= 0 || !mResizeSuc) {
                    postRunAnim();
                } else {
                    showAnim();
                }
            }
        });
    }

    private void showAnim() {

        mVSBgView.setVisibility(View.GONE);
        mBlueView.setVisibility(View.VISIBLE);
        mRedView.setVisibility(View.VISIBLE);
        mLeftPeople.setVisibility(View.VISIBLE);
        mRightPeople.setVisibility(View.VISIBLE);
        mLeftCard.setVisibility(View.VISIBLE);
        mRightCard.setVisibility(View.VISIBLE);
        mBlueShadow.setVisibility(View.VISIBLE);
        mRedShadow.setVisibility(View.VISIBLE);

        if (mCurMode == MODE_REWARD) {
            mCoinTextView.setText("");
            mCoinTextView.setVisibility(View.VISIBLE);
        } else {
            mCoinTextView.setVisibility(View.GONE);
        }

        ObjectAnimator blueMoveAnimator =
                ObjectAnimator.ofFloat(mBlueView, "translationY", -mBlueMoveHeight, 0);

        ObjectAnimator redMoveAnimator =
                ObjectAnimator.ofFloat(mRedView, "translationY", mRedMoveHeight, 0);

        ObjectAnimator translationLeft =
                ObjectAnimator.ofFloat(mLeftPeople, "translationX", -screenWidth, mPersonOffset);
        translationLeft.setInterpolator(new OvershootInterpolator());
        ObjectAnimator alphaLeft = ObjectAnimator.ofFloat(mLeftPeople, "alpha", 0.2f, 1);

        ObjectAnimator translationRight =
                ObjectAnimator.ofFloat(mRightPeople, "translationX", screenWidth, -mPersonOffset);
        translationRight.setInterpolator(new OvershootInterpolator());
        ObjectAnimator alphaRight = ObjectAnimator.ofFloat(mRightPeople, "alpha", 0.2f, 1);

        ObjectAnimator blueAvatarAnimator =
                ObjectAnimator.ofFloat(mLeftCard, "translationY", -avatarHeight, 0);
        ObjectAnimator redAvatarAnimator =
                ObjectAnimator.ofFloat(mRightCard, "translationY", avatarHeight, 0);

        ObjectAnimator blueTranslation = ObjectAnimator.ofPropertyValuesHolder(mBlueShadow,
                PropertyValuesHolder.ofFloat("translationX", screenWidth, 0),
                PropertyValuesHolder.ofFloat("translationY", shadowMove, 0));

        ObjectAnimator redTranslation = ObjectAnimator.ofPropertyValuesHolder(mRedShadow,
                PropertyValuesHolder.ofFloat("translationX", -screenWidth + 400, 0),
                PropertyValuesHolder.ofFloat("translationY", -shadowMove, 0));

        AnimatorSet set = new AnimatorSet();
        set.playTogether(blueMoveAnimator, redMoveAnimator, translationLeft, translationRight,
                alphaLeft, alphaRight, blueAvatarAnimator, redAvatarAnimator, blueTranslation,
                redTranslation);
        set.setDuration(800);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                showVSAnim();
            }
        });

        set.start();
    }

    /**
     * 反向消退动画
     */
    public void doReverse() {

        stopOptBeforeReverse();

        //红蓝背景
        ObjectAnimator blueMoveAnimator =
                ObjectAnimator.ofFloat(mBlueView, "translationY", 0, -mBlueMoveHeight);
        ObjectAnimator redMoveAnimator =
                ObjectAnimator.ofFloat(mRedView, "translationY", 0, mRedMoveHeight);

        //人物模型
        ObjectAnimator translationLeft =
                ObjectAnimator.ofFloat(mLeftPeople, "translationX", mPersonOffset, -screenWidth);
        ObjectAnimator alphaLeft = ObjectAnimator.ofFloat(mLeftPeople, "alpha", 1, 0);
        ObjectAnimator translationRight =
                ObjectAnimator.ofFloat(mRightPeople, "translationX", -mPersonOffset, screenWidth);
        ObjectAnimator alphaRight = ObjectAnimator.ofFloat(mRightPeople, "alpha", 1, 0);

        //人物卡片
        ObjectAnimator blueAvatarAnimator =
                ObjectAnimator.ofFloat(mLeftCard, "translationY", 0, -avatarHeight);
        ObjectAnimator redAvatarAnimator =
                ObjectAnimator.ofFloat(mRightCard, "translationY", 0, avatarHeight);

        //红蓝阴影
        ObjectAnimator blueTranslationX =
                ObjectAnimator.ofFloat(mBlueShadow, "translationX", 0, screenWidth);
        ObjectAnimator blueTranslationY =
                ObjectAnimator.ofFloat(mBlueShadow, "translationY", 0, shadowMove);
        ObjectAnimator redTranslationX =
                ObjectAnimator.ofFloat(mRedShadow, "translationX", 0, -screenWidth);
        ObjectAnimator redTranslationY =
                ObjectAnimator.ofFloat(mRedShadow, "translationY", 0, -shadowMove);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(blueMoveAnimator, redMoveAnimator, translationLeft, translationRight,
                alphaLeft, alphaRight, blueAvatarAnimator, redAvatarAnimator, blueTranslationX,
                blueTranslationY, redTranslationX, redTranslationY);
        set.setDuration(800);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (null != listener) {
                    listener.onExitFinish();
                }
            }
        });

        set.start();
    }

    private void stopOptBeforeReverse() {

        hadReverseShown = true;
        if (null != listener) {
            listener.onStartReverse();
        }

        if (null != mBlueLottieView && mBlueLottieView.isAnimating()) {
            mBlueLottieView.cancelAnimation();
            mBlueLottieView.setVisibility(View.GONE);
        }
        if (null != mRedLottieView && mRedLottieView.isAnimating()) {
            mRedLottieView.cancelAnimation();
            mRedLottieView.setVisibility(View.GONE);
        }

        mLoadingView.stopLoading();
        mLoadingView.setVisibility(View.GONE);
        mCoinTextView.setVisibility(View.GONE);
        mVSBgView.setVisibility(View.GONE);
        mVSIconView.setVisibility(View.GONE);
    }

    /**
     * VS + 金币动画（赏金模式才有）
     */
    private void showVSAnim() {

        mVSBgView.setVisibility(View.VISIBLE);
        mVSIconView.setVisibility(View.VISIBLE);
        mVSIconView.setAlpha(1.0f);

        ValueAnimator rotateAnimator = ValueAnimator.ofFloat(-90, 0);
        rotateAnimator.setDuration(500);
        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float animatedValue = (float) animation.getAnimatedValue();
                mVSBgView.setRotationX(animatedValue);

                float animatedFraction = animation.getAnimatedFraction();
                mVSBgView.setAlpha(animatedFraction);
            }
        });

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mVSIconView, "scaleX", 0.2f, 1);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mVSIconView, "scaleY", 0.2f, 1);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        scaleX.setDuration(500);
        scaleY.setDuration(500);

        ObjectAnimator shakeAnimator =
                ObjectAnimator.ofFloat(mContentView, "translationY", 0, 25, -25, 25, -25, 15, -15,
                        6, -6, 0);
        shakeAnimator.setDuration(800);

        AnimatorSet set = new AnimatorSet();
        if (mCurMode == MODE_REWARD) {

            ObjectAnimator alpha = ObjectAnimator.ofFloat(mVSIconView, "alpha", 1, 0);
            alpha.setDuration(800);
            set.play(rotateAnimator).with(scaleX).with(scaleY).with(shakeAnimator).before(alpha);
        } else {

            set.play(rotateAnimator).with(scaleX).with(scaleY).with(shakeAnimator);
        }

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (hadReverseShown) {
                    //已经播过消退动画说明游戏开始了 不必要显示金币或Loading了
                    return;
                }

                if (showCoinAnimation()) {
                    showCoinAnim();
                } else {
                    showLoadingView();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                if (null != listener) {
                    listener.onShowVS();
                }
            }
        });
        set.start();
    }

    /**
     * 显示底部Loading
     */
    private void showLoadingView() {
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.showLoading();
    }

    /**
     * 展示金币动画
     */
    private void showCoinAnim() {

        mBlueLottieView.setVisibility(View.VISIBLE);
        mRedLottieView.setVisibility(View.VISIBLE);

        mBlueLottieView.playAnimation();
        mRedLottieView.playAnimation();
    }

    /**
     * 根据性别获取自己的人物模型图
     *
     * @return resId
     */
    private int getMySelfSexModel() {
        return mSelfGender == SEX_MALE ? R.drawable.img_matching_boy_right1
                : R.drawable.img_matching_girl_right1;
    }

    /**
     * 根据性别获取对手的人物模型图
     *
     * @return resId
     */
    private int getOtherSexModel() {
        return mOtherGender == SEX_MALE ? R.drawable.img_matching_boy_left1
                : R.drawable.img_matching_girl_left1;
    }

    /**
     * 是否显示金币动画
     */
    private boolean showCoinAnimation() {
        return mCurMode == MODE_REWARD;
    }

    /**
     * 释放资源
     */
    public void release() {

        if (null != mBlueLottieView) {
            mBlueLottieView.removeAllAnimatorListeners();
            mBlueLottieView.setVisibility(View.GONE);
            mBlueLottieView = null;
        }

        if (null != mRedLottieView) {
            mRedLottieView.removeAllAnimatorListeners();
            mRedLottieView.setVisibility(View.GONE);
            mRedLottieView = null;
        }

        if (null != mLoadingView) {
            mLoadingView.stopLoading();
        }
    }

    public void setMatchSuccessListener(ISuccessListener listener) {
        this.listener = listener;
    }

    public interface ISuccessListener {

        /**
         * 显示VS 动画回调
         */
        void onShowVS();

        /**
         * 显示金币动画回调
         */
        void onShowCoin();

        /**
         * 消退动画结束回调
         */
        void onExitFinish();

        /**
         * 播放消退动画
         */
        void onStartReverse();
    }
}
