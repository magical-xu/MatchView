package com.magical.matchview;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * 播放音效专用
 *
 * @author magical.zhang
 * @date 2018/5/19
 */
public class SoundPoolManager implements SoundPool.OnLoadCompleteListener {

    private static final String TAG = SoundPoolManager.class.getSimpleName();
    private SoundPool.Builder spBuilder;
    private SoundPool soundPool;
    private SparseIntArray fmArray;
    private float volume;

    public void init(Context context, int[] soundArray) {

        //初始默认音量
        volume = 1.0f;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (null == spBuilder) {
                spBuilder = new SoundPool.Builder();
                AudioAttributes.Builder builder = new AudioAttributes.Builder();
                builder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
                spBuilder.setAudioAttributes(builder.build());
                spBuilder.setMaxStreams(10);
            }
            if (null == soundPool) {
                soundPool = spBuilder.build();
            }
        } else {
            if (null == soundPool) {
                //最多播放10个音效，格式为Steam_music，音质为10
                soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 10);
            }
        }

        soundPool.setOnLoadCompleteListener(this);
        if (null != soundPool) {

            if (null == fmArray) {
                fmArray = new SparseIntArray();
            }
            for (int i = 0; i < soundArray.length; i++) {
                //将需要播放的资源添加到SoundPool中，并保存返回的StreamID，通过StreamID可以停止某个音效
                fmArray.put(i, soundPool.load(context, soundArray[i], 1));
            }
        }
    }

    public void play(int position) {

        if (null == soundPool
                || position < 0
                || null == fmArray
                || fmArray.size() <= 0
                || position >= fmArray.size()) {
            return;
        }

        soundPool.play(fmArray.get(position), volume, volume, 0, 0, 1);
    }

    /**
     * 播放音效
     *
     * @param position 初始化时的资源索引
     * @param volume 相对当前音量的乘机因子
     */
    public void play(int position, int volume) {

        if (null == soundPool
                || position < 0
                || null == fmArray
                || fmArray.size() <= 0
                || position >= fmArray.size()) {
            return;
        }

        soundPool.play(fmArray.get(position), volume, volume, 0, 0, 1);
    }

    /**
     * 设置音量因子
     *
     * @param volume 系数
     */
    public void setVolume(@FloatRange(from = 0.0f, to = 1.0f) float volume) {
        this.volume = volume;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        Log.d(TAG, " onLoadComplete");
    }

    /**
     * 释放SoundPool资源
     */
    public void release() {

        if (null != soundPool) {
            soundPool.release();
            soundPool = null;
        }
    }
}
