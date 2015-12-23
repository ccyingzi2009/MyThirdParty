package com.ls.util;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.view.animation.PathInterpolator;

/**
 * Created by user on 15-12-22.
 */
public class Utils {
    public final static int COLOR_ANIMATION_DURATION = 2000;
    public static void animateViewBackGroundColor(View v, int startColor, int endColor) {
        ObjectAnimator animator = ObjectAnimator.ofObject(v, "backgroundColor", new ArgbEvaluator(), startColor, endColor);
        if (Build.VERSION.SDK_INT >= 21) {
            animator.setInterpolator(new PathInterpolator(0.4f, 0f, 1f, 1f));
        }

        animator.setDuration(COLOR_ANIMATION_DURATION);
        animator.start();
    }
}
