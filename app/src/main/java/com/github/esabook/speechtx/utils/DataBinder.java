package com.github.esabook.speechtx.utils;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.view.View;

public class DataBinder {
    @BindingAdapter("my:bg_color")
    public static void setBgColor(View v, @ColorRes int color) {
        v.setBackgroundResource(color);
    }
}
