package com.task.utils.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;


public class CustomerEdittextViewRegular extends AppCompatEditText {
    private static Typeface mTypeface;

    public CustomerEdittextViewRegular(final Context context) {
        this(context, null);
    }

    public CustomerEdittextViewRegular(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomerEdittextViewRegular(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Montserrat-Regular.ttf");
        }
        setTypeface(mTypeface);
    }

}
