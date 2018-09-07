package com.magical.matchview;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.magical.lib.MatchSuccessView;
import com.magical.lib.MatchView;
import com.magical.lib.model.MatchInfo;
import com.magical.lib.model.UserInfo;

public class MainActivity extends AppCompatActivity {

    private MatchView mMatchView;
    private MatchSuccessView mSuccessView;

    private SoundPoolManager mSoundPoolManager;
    private BackgroundMusic mBgMusicManager;
    private CountDownTimer mCoinSoundTimer;

    private TextView mHintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initMatch();
    }

    private void initView() {

        mHintView = findViewById(R.id.id_hint);
        mMatchView = findViewById(R.id.id_match_view);
        mMatchView.setVisibility(View.VISIBLE);
        mSuccessView = findViewById(R.id.id_match_success_view);
        mSuccessView.setVisibility(View.GONE);
    }

    /**
     * 初始化匹配的View 从哪进入的都要调用
     * 重新加载匹配View后也要调用一次 重设监听等
     */
    private void initMatch() {

        if (null == mBgMusicManager) {
            mBgMusicManager = new BackgroundMusic(this);
        }

        if (null == mSoundPoolManager) {
            mSoundPoolManager = new SoundPoolManager();
            mSoundPoolManager.init(this, new int[] { R.raw.blip, R.raw.impact, R.raw.coin_effect });
            mSoundPoolManager.setVolume(0.4f);
        }

        mMatchView.setMatchListener(new MatchView.IMatchListener() {
            @Override
            public void onFrame(int second) {

                if (second == 4) {
                    onMatchSuccess();
                }
            }

            @Override
            public void onCircleStart() {
                if (mSuccessView.getVisibility() != View.VISIBLE) {
                    mSoundPoolManager.play(0);
                }
            }

            @Override
            public void onCancel() {
                //GameActivity.this.onCancelMatch();
                mMatchView.setBtnText("开始匹配");
            }

            @Override
            public void onMatchStart() {
                startMatchBGM();
            }
        });

        mSuccessView.setMatchSuccessListener(new MatchSuccessView.ISuccessListener() {
            @Override
            public void onShowVS() {
                mSoundPoolManager.play(1);
            }

            @Override
            public void onShowCoin() {
                playCoinSound();
                mHintView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onExitFinish() {

                if (null != mSuccessView) {
                    mSuccessView.release();
                    //mSuccessView.setVisibility(View.GONE);
                }

                onBGMRelease();

                //releaseAllMatchView();
                //setGameUtilViewVisibility(View.VISIBLE);
            }

            @Override
            public void onStartReverse() {

                if (null != mCoinSoundTimer) {
                    mCoinSoundTimer.cancel();
                    mCoinSoundTimer = null;
                }
            }
        });
    }

    private void playCoinSound() {

        mCoinSoundTimer = new CountDownTimer(1500, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mSoundPoolManager.play(2);
            }

            @Override
            public void onFinish() {
                mMatchView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSuccessView.doReverse();
                    }
                },2000);
            }
        };

        mMatchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mCoinSoundTimer) {
                    mCoinSoundTimer.start();
                }
            }
        }, 1200);
    }

    private void startMatchBGM() {
        if (null == mBgMusicManager) {
            mBgMusicManager = new BackgroundMusic(MainActivity.this);
        }
        mBgMusicManager.playBackgroundMusic("match_search.mp3", true);
    }

    public void onBGMRelease() {

        if (null != mBgMusicManager) {
            mBgMusicManager.stopBackgroundMusic();
            mBgMusicManager.end();
            mBgMusicManager = null;
        }
    }

    public void onBGMPause() {

        if (null != mBgMusicManager && mBgMusicManager.isPlaying()) {
            mBgMusicManager.pauseBackgroundMusic();
        }
    }

    public void onBGMResume() {

        if (null != mBgMusicManager) {
            mBgMusicManager.resumeBackgroundMusic();
        }
    }

    /**
     * 播放对决动画
     */
    private void showMatchSuccessAnim() {

        MatchInfo matchInfo = new MatchInfo();

        UserInfo myInfo = new UserInfo();
        myInfo.nickname = "大宝So3";
        myInfo.gender = 1;
        myInfo.avatar =
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536357781595&di=130c44941f4a6400221df1318fef7629&imgtype=0&src=http%3A%2F%2F01.imgmini.eastday.com%2Fmobile%2F20180819%2F20180819_d463cb3b80b1d48a1f39f84f84f11d12_cover_mwpm_03200403.jpg";

        UserInfo otherInfo = new UserInfo();
        otherInfo.nickname = "doubleSo3";
        otherInfo.gender = 0;
        otherInfo.avatar =
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1536358615133&di=8dd84a30b433883409e1e7d52358fadd&imgtype=0&src=http%3A%2F%2Fupload.jxntv.cn%2F2018%2F0820%2F1534749816811.png";

        matchInfo.setFrom(myInfo);
        matchInfo.setTo(otherInfo);
        matchInfo.mode = MatchInfo.START_TYPE_GAMBLE;
        matchInfo.totalCoin = 100;

        mSuccessView.setMatchData(matchInfo);
        mMatchView.setVisibility(View.GONE);
        mSuccessView.setVisibility(View.VISIBLE);
        mSuccessView.postRunAnim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        onBGMPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onBGMResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSoundPoolManager) {
            mSoundPoolManager.release();
        }
        onBGMRelease();

        if (null != mSuccessView) {
            mSuccessView.release();
        }
    }

    private void onMatchSuccess() {

        onStopMatchView();
        showMatchSuccessAnim();
    }

    /**
     * 停止匹配动画 隐藏匹配View
     */
    private void onStopMatchView() {

        if (null != mMatchView) {
            mMatchView.stopMatch();
            mMatchView.setVisibility(View.GONE);
        }
    }
}
