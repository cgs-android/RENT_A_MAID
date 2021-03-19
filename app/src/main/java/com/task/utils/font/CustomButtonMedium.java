package com.task.utils.font;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;


public class CustomButtonMedium extends AppCompatButton {

    public CustomButtonMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButtonMedium(Context context) {
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