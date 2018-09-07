package com.magical.lib.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.magical.lib.util.ImageLoader;

public class HeadImageView extends CircularImage {

    public HeadImageView(Context context) {
        this(context, null);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHeadImageUrl(final String url) {

        ImageLoader.getInstance().loadNormal(url, this);
    }
}
