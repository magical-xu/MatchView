package com.magical.lib.font;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class FontUtils {

    private static final String TAG = FontUtils.class.getSimpleName();
    private Map<String, SoftReference<Typeface>> mCache = new HashMap<>();
    private static FontUtils sSingleton = null;

    public static Typeface DEFAULT = Typeface.DEFAULT;

    // disable instantiate
    private FontUtils() {
    }

    public static FontUtils getInstance() {
        // double check
        if (sSingleton == null) {
            synchronized (FontUtils.class) {
                if (sSingleton == null) {
                    sSingleton = new FontUtils();
                }
            }
        }
        return sSingleton;
    }

    public void replaceFontFromAsset(@NonNull View root, @NonNull String fontPath) {
        replaceFont(root, createTypefaceFromAsset(root.getContext(), fontPath));
    }

    public void replaceFontFromAsset(@NonNull View root, @NonNull String fontPath, int style) {
        replaceFont(root, createTypefaceFromAsset(root.getContext(), fontPath), style);
    }

    public void replaceFontFromFile(@NonNull View root, @NonNull String fontPath) {
        replaceFont(root, createTypefaceFromFile(fontPath));
    }

    public void replaceFontFromFile(@NonNull View root, @NonNull String fontPath, int style) {
        replaceFont(root, createTypefaceFromFile(fontPath), style);
    }

    /**
     * 给view设置字体，可以递归的去找ziview去设置
     */
    private void replaceFont(@NonNull View root, @NonNull Typeface typeface) {
        if (root == null || typeface == null) {
            return;
        }

        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView) root;
            // Extract previous style of TextView
            int style = Typeface.NORMAL;
            if (textView.getTypeface() != null) {
                style = textView.getTypeface().getStyle();
            }
            textView.setTypeface(typeface, style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), typeface);
            }
        } // else return
    }

    private void replaceFont(@NonNull View root, @NonNull Typeface typeface, int style) {
        if (root == null || typeface == null) {
            return;
        }
        if (style < 0 || style > 3) {
            style = Typeface.NORMAL;
        }

        if (root instanceof TextView) { // If view is TextView or it's subclass, replace it's font
            TextView textView = (TextView) root;
            textView.setTypeface(typeface, style);
        } else if (root instanceof ViewGroup) { // If view is ViewGroup, apply this method on it's child views
            ViewGroup viewGroup = (ViewGroup) root;
            for (int i = 0; i < viewGroup.getChildCount(); ++i) {
                replaceFont(viewGroup.getChildAt(i), typeface, style);
            }
        } // else return
    }

    private Typeface createTypefaceFromAsset(Context context, String fontPath) {
        SoftReference<Typeface> typefaceRef = mCache.get(fontPath);
        Typeface typeface = null;
        if (typefaceRef == null || (typeface = typefaceRef.get()) == null) {
            typeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            typefaceRef = new SoftReference<>(typeface);
            mCache.put(fontPath, typefaceRef);
        }
        return typeface;
    }

    private Typeface createTypefaceFromFile(String fontPath) {
        SoftReference<Typeface> typefaceRef = mCache.get(fontPath);
        Typeface typeface = null;
        if (typefaceRef == null || (typeface = typefaceRef.get()) == null) {
            typeface = Typeface.createFromFile(fontPath);
            typefaceRef = new SoftReference<>(typeface);
            mCache.put(fontPath, typefaceRef);
        }
        return typeface;
    }

    public void replaceSystemDefaultFontFromAsset(@NonNull Context context,
            @NonNull String fontPath) {
        replaceSystemDefaultFont(createTypefaceFromAsset(context, fontPath));
    }

    public void replaceSystemDefaultFontFromFile(@NonNull Context context,
            @NonNull String fontPath) {
        replaceSystemDefaultFont(createTypefaceFromFile(fontPath));
    }

    private void replaceSystemDefaultFont(@NonNull Typeface typeface) {
        modifyObjectField(null, "MONOSPACE", typeface);
    }

    private void modifyObjectField(Object obj, String fieldName, Object value) {
        try {
            Field defaultField = Typeface.class.getDeclaredField(fieldName);
            defaultField.setAccessible(true);
            defaultField.set(obj, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}