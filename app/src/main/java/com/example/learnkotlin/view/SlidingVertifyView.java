package com.example.learnkotlin.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.learnkotlin.R;

import java.util.Random;

public class SlidingVertifyView extends View {
    private Drawable verifyDrawable;//用于显示的验证码图片
    private int allowoffect;//滑动目的地允许的偏差范围
    private int slideWidth;//滑块宽度
    private boolean isRondomPosition;//是否随机位置
    private Rect bgRect;//背景
    private Rect startRect;//滑块起始位置
    private Rect endRect;//滑块目标位置
    private Rect currentRect;//滑块当前位置
    int positionLeft;
    private Paint slidePaint;//绘制滑块的画笔
    private Paint bgPaint;//绘制背景的笔
    private Paint spacePaint;//绘制空缺位置的笔
    private Context context;
    private Rect bitmapRect;
    private Rect bitmapAllRect;
    private boolean needMeasure=true;

    private Bitmap bitmap;//绘制完后的背景

    public SlidingVertifyView(Context context) {
        super(context);
    }

    public SlidingVertifyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public SlidingVertifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context,AttributeSet attrs){
        this.context=context;
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.SlidingVertifyView);
        verifyDrawable=typedArray.getDrawable(R.styleable.SlidingVertifyView_vertifyDrawable);
        allowoffect= (int) typedArray.getDimension(R.styleable.SlidingVertifyView_allowableTolerance,5);
        slideWidth= (int) typedArray.getDimension(R.styleable.SlidingVertifyView_slideWidth,30);
        isRondomPosition=typedArray.getBoolean(R.styleable.SlidingVertifyView_randomPosition,true);
        if (verifyDrawable==null){
            verifyDrawable= ContextCompat.getDrawable(context,R.drawable.device10);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.d("TAG", "onMeasure: "+getMeasuredWidth()+"---"+getMeasuredWidth());

        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("TAG", "onMeasure after super: "+getMeasuredWidth()+"---"+getMeasuredWidth());

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(needMeasure){
            initSlidePosition(canvas);
            needMeasure=false;
        }
        drawBg(canvas);
        drawSelect(canvas);
    }

    private void drawSelect(Canvas canvas) {
        slidePaint=new Paint();
        slidePaint.setStrokeWidth(5);
        slidePaint.setColor(Color.RED);
        slidePaint.setFilterBitmap(false);
//        canvas.clipRect(endRect);
        //canvas.drawBitmap(bitmap,new Rect(0,0,bitmap.getWidth(),bitmap.getHeight()),startRect,slidePaint);
        slidePaint.setStyle(Paint.Style.STROKE);


        canvas.drawBitmap(bitmap,bitmapAllRect,startRect,slidePaint);

//        canvas.drawBitmap(bitmap,bitmapRect,endRect,slidePaint);
        canvas.drawRect(startRect,slidePaint);
        canvas.drawRect(endRect,slidePaint);
    }

    private void drawBg(Canvas canvas) {
        bgPaint=new Paint();
        bgPaint.setColor(ContextCompat.getColor(context,R.color.blue));
        Rect rect=new Rect(0,0,verifyDrawable.getIntrinsicWidth(),verifyDrawable.getIntrinsicHeight());
        verifyDrawable.setBounds(bgRect);

        verifyDrawable.draw(canvas);
//        canvas.drawRect(bgRect,bgPaint);

//        canvas.drawBitmap(((BitmapDrawable)verifyDrawable).getBitmap(),rect,bgRect,bgPaint);
    }

    //初始化位置
    private void initSlidePosition(Canvas canvas) {
        bgRect=new Rect(0,0,getMeasuredWidth(),getMeasuredHeight());
        startRect=new Rect(0,getMeasuredHeight()/2-slideWidth/2,slideWidth,getMeasuredHeight()/2+slideWidth/2);
        if (isRondomPosition){
//        positionLeft=new Random().nextInt(getMeasuredWidth()-slideWidth);
         positionLeft=   getRandomPosition();
        }else{
            positionLeft=getMeasuredWidth()/2;
        }
        bitmapAllRect=new Rect(0,0,verifyDrawable.getIntrinsicWidth(),verifyDrawable.getIntrinsicHeight());
        int positionTop=getMeasuredHeight()/2-slideWidth/2;
        int positionRight=positionLeft+slideWidth;
        int positionBottom=getMeasuredHeight()/2+slideWidth/2;
        int bitmapHeight=verifyDrawable.getIntrinsicHeight();
        int bitmapWidth=verifyDrawable.getIntrinsicWidth();
        float vertivalRate=(float) bitmapHeight/getHeight();
        float horizontalRate=(float)bitmapWidth/getWidth();


//        int btleft= (int) (positionLeft*horizontalRate);
//        int btTop= (int) (positionTop*vertivalRate);
//        int btRight= (int) (positionRight*horizontalRate);
//        int btBottom= (int) (positionBottom*vertivalRate);
        BitmapDrawable bitmapDrawable= (BitmapDrawable) verifyDrawable;
//        bitmap=bitmapDrawable.getBitmap();

        float bitmapSelectWidth=slideWidth*horizontalRate;
        float bitmapSelectHeight=slideWidth*vertivalRate;
        int btleft= (int) (positionLeft*horizontalRate);
        int btTop= (int) (bitmapHeight/2-bitmapSelectHeight/2);
        int btRight= (int) (btleft+bitmapSelectWidth);
        int btBottom= (int) (bitmapHeight/2+bitmapSelectHeight/2);

//        bitmap=bitmap.createBitmap(bitmap,btleft,btTop,(int) bitmapSelectWidth,(int)bitmapSelectHeight);
        bitmapRect=new Rect(btleft,btTop,btRight,btBottom);//图片范围
//        bitmap=convertViewToBitmap();
        endRect=new Rect(positionLeft,positionTop,positionRight,positionBottom);
            bitmap=convertViewToBitmap(canvas);

    }
    private int getRandomPosition(){
        int num=0;
        while (num<slideWidth){
        num= new Random().nextInt(getMeasuredWidth()-slideWidth);
        }
       return num;
    }

    public Bitmap convertViewToBitmap(Canvas canvas){
        Bitmap bitmap=Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Path path=new Path();
//        path.addRect(endRect,);
        Canvas canvas1=new Canvas(bitmap);
        canvas1.save();
        canvas1.clipRect(startRect);
//        canvas1.drawBitmap(this.bitmap,bitmapAllRect,endRect,slidePaint);
        Log.d("TAG", "convertViewToBitmap: "+bitmap.toString());
        verifyDrawable.draw(canvas);
        canvas1.restore();

        //利用bitmap生成画布

        //把view中的内容绘制在画布上
        return bitmap;
    }

    private Boolean isTouching=false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float downX;
        float downY;
        float pressX;
        float pressY;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                if (startRect.contains((int)downX,(int)downY)){
                    isTouching=true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                pressX=event.getX();
                pressY=event.getY();
                if ((bgRect.left<pressX-slideWidth/2)&&(bgRect.right>pressX+slideWidth/2)){
//                    Log.d("slideRecult",pressX+"--"+startRect.left);
                        startRect.left= (int) (pressX-slideWidth/2);
                        startRect.right= (int) (pressX+slideWidth/2);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (isTouching){
                isTouching=false;
                if ((startRect.left>endRect.left-allowoffect)&&startRect.right<endRect.right+allowoffect){
                    Log.d("slideRecult","success vertify");
                    startRect.left=0;
                    startRect.right=slideWidth;
                    invalidate();
                }else{
                    Log.d("slideRecult","failure vertify");
                    startRect.left=0;
                    startRect.right=slideWidth;
                    invalidate();
                }
                }
                break;
        }
        return true;
    }
}
