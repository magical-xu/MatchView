package com.magical.lib.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by magical.zhang on 2017/2/16.
 */
public class ImageLoader {

    private static final String TAG = "ImageLoader";
    private static volatile ImageLoader instance;

    private ImageLoader() {
    }

    public static ImageLoader getInstance() {
        if (null == instance) {
            synchronized (ImageLoader.class) {
                if (null == instance) {
                    instance = new ImageLoader();
                }
            }
        }
        return instance;
    }

    /**
     * 普通加载
     */
    public void loadNormal(String url, ImageView view) {

        Glide.with(view).load(url).into(view);
    }

    public void loadResource(int resId, ImageView imageView) {

        Glide.with(imageView).load(resId).into(imageView);
    }

    @SuppressLint("CheckResult")
    public void loadBySize(String url, ImageView view, @DrawableRes int drawableId, int width,
            int height) {

        RequestOptions requestOptions = new RequestOptions().format(DecodeFormat.PREFER_RGB_565).
                placeholder(drawableId).centerCrop().override(width, height);
        Glide.with(view).applyDefaultRequestOptions(requestOptions).load(url).into(view);
    }

    public void loadByOption(String url, ImageView view, RequestOptions options) {

        Glide.with(view).load(url).apply(options).into(view);
    }

    /**
     * 加载 “无alpha” 通道的本地资源大图时使用 有alpha通道的图片会忽略我们的配置
     * 虽然内存占用可以减少一半，但有些图片失真过度界面效果可能达不到要求了
     * 所以使用需慎重
     */
    public void loadBigLocal(@DrawableRes int drawableId, View view) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(view.getResources(), drawableId, options);
        if (null == bitmap) {
            return;
        }

        if (view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bitmap);
        } else {
            view.setBackground(new BitmapDrawable(view.getResources(), bitmap));
        }
    }

    /**
     * 获取 Glide 的 Bitmap池
     */
    public BitmapPool getBitmapPool(Context context) {
        return Glide.get(context).getBitmapPool();
    }

    /**
     * 清除内存缓存
     * 清理所有内存并非特别经济，并且应该尽可能避免，以避免出现抖动和增加加载时间
     * https://muyangmin.github.io/glide-docs-cn/doc/caching.html
     *
     * @param context Context
     */
    public void clearMemoryCache(Context context) {

        // This method must be called on the main thread.
        Glide.get(context).clearMemory();
    }
}
