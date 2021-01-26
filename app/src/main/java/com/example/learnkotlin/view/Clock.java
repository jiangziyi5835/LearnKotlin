package com.example.learnkotlin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.learnkotlin.R;

public class Clock extends View {
    private Paint clockDishPaint;
    private Paint hourHandsPaint;
    private Paint minuteHandsPaint;
    private int hourHandsColor;
    private int minuteHandsColor;
    private int clockDishColor;
    private int timeForHour;
    private int timeForMinute;
    private int width = 200;
    private int height = 200;
    private int hour = 0;
    private int minute = 0;

    public Clock(Context context) {
        super(context);

    }

    public Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Clock);
        hourHandsColor = typedArray.getColor(R.styleable.Clock_hourcolor, Color.BLACK);
        minuteHandsColor = typedArray.getColor(R.styleable.Clock_mimuteColor, Color.BLACK);
        clockDishColor = typedArray.getColor(R.styleable.Clock_dishColor, Color.BLUE);
        hour=typedArray.getInt(R.styleable.Clock_hour,0);
        minute=typedArray.getInt(R.styleable.Clock_minute,0);
        typedArray.recycle();
    }

    public Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
       /* TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Clock);
        hourHandsColor = typedArray.getColor(R.styleable.Clock_hourcolor, Color.BLACK);
        minuteHandsColor = typedArray.getColor(R.styleable.Clock_mimuteColor, Color.BLACK);
        clockDishColor = typedArray.getColor(R.styleable.Clock_dishColor, Color.BLUE);

        typedArray.recycle();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth() - 10;
        height = getMeasuredHeight() - 10;
        Log.d("measure", "measure width:" + width + "---height:" + height);
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawDish(canvas);
        drawScale(canvas);
        drawHour(canvas);
        drawMinute(canvas);
    }


    /**
     * 绘制表盘
     *
     * @param canvas
     */
    private void drawDish(Canvas canvas) {
        clockDishPaint = new Paint();
        clockDishPaint.setColor(clockDishColor);
        clockDishPaint.setStrokeWidth(5);
        clockDishPaint.setStyle(Paint.Style.STROKE);
        clockDishPaint.setAntiAlias(true);
        Log.d("measuretest", "measure width:" + width + "---height:" + height + "---"
                + clockDishColor
        );

        canvas.drawCircle(width / 2 + 5, height / 2 + 5, height / 2, clockDishPaint);
    }

    /**
     * 绘制刻度
     *
     * @param canvas
     */
    private void drawScale(Canvas canvas) {
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0) {
                canvas.drawLine(width / 2 + 5, 15, width / 2 + 5, 40, clockDishPaint);
            } else {
                canvas.drawLine(width / 2 + 5, 15, width / 2 + 5, 25, clockDishPaint);
            }
            canvas.rotate(30, width / 2 + 5, height / 2 + 5);
        }
    }

    /**
     * 绘制时针
     * @param canvas
     */
    private void drawHour(Canvas canvas) {
        int hourAngle=hour%12*30;
        Log.d("measuretest","  "+hourAngle);
        hourHandsPaint = new Paint();
        hourHandsPaint.setColor(hourHandsColor);
        hourHandsPaint.setStyle(Paint.Style.STROKE);
        hourHandsPaint.setStrokeWidth(7);
        hourHandsPaint.setAntiAlias(true);
        canvas.translate(width / 2 + 5, height / 2 + 5);
       /* canvas.rotate(30);
        canvas.drawLine(0,0,0,-height/4,hourHandsPaint);
        canvas.rotate(-30);*/
        canvas.drawLine(getPointX(hourAngle, width / 4), -getPointY(hourAngle, width / 4), 0, 0, hourHandsPaint);
    }

    /**
     * 绘制分针
     * @param canvas
     */
    private void drawMinute(Canvas canvas){
        int minuteAngle=minute%60*6;
        minuteHandsPaint=new Paint();
        minuteHandsPaint.setColor(minuteHandsColor);
        minuteHandsPaint.setStrokeWidth(5);
        minuteHandsPaint.setAntiAlias(true);
        canvas.drawLine(0,0,getPointX(minuteAngle,width/3),-getPointY(minuteAngle,width/3),minuteHandsPaint);

    }

    private float getPointX(int angle, int diameter) {
        return (float) (Math.sin(angle * Math.PI / 180) * diameter);

    }

    private float getPointY(int angle, int diameter) {
        return (float) Math.cos(angle * Math.PI / 180) * diameter;
    }

    /**
     * 设定时间
     *
     * @param hour
     * @param minute
     */
    public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        invalidate();
    }


}
