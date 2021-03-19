package com.task.utils.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


public class CustomerEdittextViewBold extends AppCompatEditText {

    public CustomerEdittextViewBold(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomerEdittextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomerEdittextViewBold(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Bold.ttf");
            setTypeface(tf);
        }
    }

}