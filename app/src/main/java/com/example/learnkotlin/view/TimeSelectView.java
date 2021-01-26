package com.example.learnkotlin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.learnkotlin.R;

public class TimeSelectView extends View {
    private float hour = 7;
    private float minute = 30;
    private Paint dishPaint;
    private Paint hourPaint;
    private Paint minutePaint;
    private Paint centerTimePaint;
    private int textSize;
    private int width;
    private int height;
    private Rect bgRect;
    private Rect hourRect;
    private Rect minuteRect;
    private int minuteLineColor;
    private int hourLineColor;
    private int textColor;



    public TimeSelectView(Context context) {
        super(context);
        initAttr(context, null);
    }

    public TimeSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
    }


    public TimeSelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeSelectView);
        hour = typedArray.getInt(R.styleable.TimeSelectView_selectHour, 0);
        minute = typedArray.getInt(R.styleable.TimeSelectView_selectMinute, 0);
        textSize = typedArray.getDimensionPixelSize(R.styleable.TimeSelectView_timeSelectFontSize, 60);
        hourLineColor = typedArray.getColor(R.styleable.TimeSelectView_hourLineColor, Color.RED);
        minuteLineColor = typedArray.getColor(R.styleable.TimeSelectView_minuteLineColor, Color.RED);
        textColor = typedArray.getColor(R.styleable.TimeSelectView_textColor, Color.BLACK);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDish(canvas);
        drawText(canvas);
        drawHour(canvas);
        drawMinute(canvas);
    }

    private void drawHour(Canvas canvas) {
        canvas.translate(-width / 2, -height / 2);
        hourPaint = new Paint();
        hourPaint.setDither(true);
        hourPaint.setColor(hourLineColor);
        hourPaint.setStrokeWidth(3);
        float hourAngle = hour % 12 * 30;
        int x = (int) (getPointX(hourAngle, width / 3 + width / 12) + width / 2);
        int y = (int) (-getPointY(hourAngle, height / 3 + height / 12) + height / 2);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) (getResources().getDrawable(R.mipmap.timer_hours));
        Bitmap hourBitmap = bitmapDrawable.getBitmap();
        Rect hourSrc = new Rect(0, 0, hourBitmap.getWidth(), hourBitmap.getHeight());
        hourRect = new Rect(x - width / 15, y - height / 15, x + width / 15, y + height / 15);
        canvas.drawBitmap(hourBitmap, hourSrc, hourRect, hourPaint);
        int dishX = (int) (getPointX(hourAngle, width / 4) + width / 2);
        int dishY = (int) (-getPointY(hourAngle, height / 4) + height / 2);
        int hourX = (int) (getPointX(hourAngle, (width / 3 + width / 12) - hourRect.width() / 2) + width / 2);
        int hourY = (int) (-getPointY(hourAngle, (width / 3 + width / 12) - hourRect.height() / 2) + height / 2);
        canvas.drawLine(dishX, dishY, hourX, hourY, hourPaint);

    }

    private void drawMinute(Canvas canvas) {
        minutePaint = new Paint();
        minutePaint.setDither(true);
        minutePaint.setColor(minuteLineColor);
        minutePaint.setStrokeWidth(3);
        double minuteAngle = minute % 60 * 6;
        int x = (int) (getPointX(minuteAngle, width / 3 + width / 12) + width / 2);
        int y = (int) (-getPointY(minuteAngle, height / 3 + width / 12) + width / 2);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) (getResources().getDrawable(R.mipmap.timer_mins));
        Bitmap minuteBitmap = bitmapDrawable.getBitmap();
        Rect minuteSrc = new Rect(0, 0, minuteBitmap.getWidth(), minuteBitmap.getHeight());
        minuteRect = new Rect(x - width / 15, y - height / 15, x + width / 15, y + height / 15);
        canvas.drawBitmap(minuteBitmap, minuteSrc, minuteRect, minutePaint);
        int dishX = (int) (getPointX(minuteAngle, width / 4) + width / 2);
        int dishY = (int) (-getPointY(minuteAngle, height / 4) + height / 2);
        int hourX = (int) (getPointX(minuteAngle, (width / 3 + width / 12) - minuteRect.width() / 2) + width / 2);
        int hourY = (int) (-getPointY(minuteAngle, (width / 3 + width / 12) - minuteRect.height() / 2) + height / 2);
        canvas.drawLine(dishX, dishY, hourX, hourY, minutePaint);

    }

    private void drawText(Canvas canvas) {
        centerTimePaint = new Paint();
        centerTimePaint.setAntiAlias(true);
        centerTimePaint.setColor(textColor);
        centerTimePaint.setTextSize(textSize);
        String time = CombineText();
        float textWidth = centerTimePaint.measureText(time);
        Rect textRect = new Rect();
        centerTimePaint.getTextBounds(time, 0, time.length(), textRect);
        canvas.translate(width / 2, height / 2);
        canvas.drawText(time, -textWidth / 2, textRect.height() / 2, centerTimePaint);
    }

    private String CombineText() {
        int intHour = Math.round(hour);
        int intMinute = Math.round(minute);
        if (intHour == 12) intHour = 0;
        if (intMinute == 60) intMinute = 0;
        String textHour = intHour > 9 ? intHour + "" : "0" + intHour;
        String textMiute = intMinute > 9 ? intMinute + "" : "0" + intMinute;
        return textHour + " : " + textMiute;
    }

    private void drawDish(Canvas canvas) {
        dishPaint = new Paint();
        dishPaint.setColor(Color.BLACK);
//        canvas.drawCircle(width/2,height/2,width/2,dishPaint);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.add_timer);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        bgRect = new Rect(width / 4, height / 4, width / 4 * 3, height / 4 * 3);
        Rect srcRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        canvas.drawBitmap(bitmap, srcRect, bgRect, dishPaint);

    }

    private double getPointX(double angle, double diameter) {
        return (Math.sin(angle * Math.PI / 180) * diameter);

    }

    private double getPointY(double angle, double diameter) {
        return (Math.cos(angle * Math.PI / 180) * diameter);
    }

    int currentTouchView = 0;//当前触摸的按钮 0：没碰到，1：碰到小时按钮，2：碰到分钟按钮

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX;
        float downY;
        float pressX;
        float pressY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (hourRect.contains((int) downX, (int) downY)) {
                    currentTouchView = 1;

                } else if (minuteRect.contains((int) downX, (int) downY)) {
                    currentTouchView = 2;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                pressX = event.getX();
                pressY = event.getY();
                if (currentTouchView == 1) {
                    double angle = 180 - calculateAngle(pressX, pressY) - 90;
                    hour = (float) ((-angle * 12 / 360 + 12) % 12);
                } else if (currentTouchView == 2) {
                    double angle = 180 - calculateAngle(pressX, pressY) - 90;
                    minute = (float) ((-angle * 60 / 360 + 60) % 60);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                currentTouchView = 0;
                timeSelectLitener.received(this);
                break;
        }
        return true;
        //return super.onTouchEvent(event);
    }

    private double calculateAngle(float pressX, float pressY) {
        double sin = width / 2 - pressY;
        double cos = height / 2 - pressX;
        return (Math.atan2(sin, cos) * 180 / Math.PI);
    }

    public int getSelectHour() {
        int intHour = Math.round(hour);
        if (intHour == 12) intHour = 0;
        return intHour;
    }

    public int getSelectMinute() {
        int intMinute = Math.round(minute);
        if (intMinute == 60) intMinute = 0;
        return intMinute;
    }

    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }

    public void setTextSize(int size) {
        textSize = size;
    }

    public void setSelectHour(int hour) {
        this.hour = hour;
        invalidate();
    }

    public void setSelectMinute(int minute) {
        this.minute = minute;
        invalidate();
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static float px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (pxValue / fontScale);
    }

    private TimeSelectLitener timeSelectLitener;

    public void setOnTimeSelectListener(TimeSelectLitener timeSelectListener) {
        this.timeSelectLitener = timeSelectListener;
    }

    public interface TimeSelectLitener {
        void received(TimeSelectView timeSelectView);
    }


}


