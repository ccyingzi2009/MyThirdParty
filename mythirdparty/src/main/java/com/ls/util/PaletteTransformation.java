package com.ls.util;


import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by user on 15-12-22.
 */
public class PaletteTransformation implements Transformation {
    private static final PaletteTransformation INSTANCE = new PaletteTransformation();
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

    public static PaletteTransformation instance(){
        return INSTANCE;
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }
    @Override
    public Bitmap transform(Bitmap source) {
        Palette palette = new Palette.Builder(source).generate();
        CACHE.put(source, palette);
        return source;
    }

    @Override
    public String key() {
        return "";
    }
}
