package com.app.incroyable.fitnes_hub.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CustomTextView(Context context) {
        super(context);
        initialize();
    }

    public void initialize() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/arial.ttf");
        setTypeface(typeface, Typeface.BOLD);
    }
}
