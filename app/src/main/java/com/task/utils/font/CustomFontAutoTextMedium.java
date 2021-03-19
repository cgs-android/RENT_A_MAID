package com.task.utils.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;


public class CustomFontAutoTextMedium extends TextInputEditText {

    public CustomFontAutoTextMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomFontAutoTextMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFontAutoTextMedium(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "font/Montserrat-Medium.ttf");
            setTypeface(tf);
        }
    }

}