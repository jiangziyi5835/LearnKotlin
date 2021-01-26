package com.example.learnkotlin.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class RatioImageView extends androidx.appcompat.widget.AppCompatImageView {
    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable=getDrawable();
        if (drawable!=null){
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=width*112/159;
        setMeasuredDimension(width,height);}
        else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
