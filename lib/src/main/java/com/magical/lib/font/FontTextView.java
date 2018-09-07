package com.magical.lib.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.magical.lib.R;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 带字体的textview
 */
public class FontTextView extends android.support.v7.widget.AppCompatTextView {
    private String mFontPath = "font/AkzidenzGrotesk-BoldCond.otf";
    private Map<String, SoftReference<Typeface>> mCache = new HashMap<>();

    public FontTextView(Context context) {
        this(context, null);
    }

    public FontTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    public String getFontPath() {
        return mFontPath;
    }

    /**
     * <p>Set font file fontPath</p>
     *
     * @param fontPath The file name of the font data in the assets directory
     */
    public void setFontPath(String fontPath) {
        mFontPath = fontPath;

        if (!TextUtils.isEmpty(mFontPath)) {
            FontUtils.getInstance().replaceFontFromAsset(this, mFontPath);
        }
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.FontTextView, defStyleAttr, 0);
        String path = typedArray.getString(R.styleable.FontTextView_font_path);
        typedArray.recycle();

        if (!TextUtils.isEmpty(path)) {
            mFontPath = path;
        }
        if (!TextUtils.isEmpty(mFontPath)) {
            FontUtils.getInstance().replaceFontFromAsset(this, mFontPath);
        }
    }
}
