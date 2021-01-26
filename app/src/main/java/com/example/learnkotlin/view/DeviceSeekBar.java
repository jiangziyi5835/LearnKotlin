package com.example.learnkotlin.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.learnkotlin.R;
import com.example.learnkotlin.util.Constants;


/**
 * Created by libra on 2017/12/1.
 */

public class DeviceSeekBar extends View {
    public static final int Normal = 0;
    public static final int Curtain = 1;
    public static final int Curtain_Right = 2;
    public static final int Curtain_Left = 3;
    public static final int Angle = 4;
    public static final int Curtain_Wifi = 5;
    public static final int TDBU = 6;
    public static final int Angle_Half = 7;
    public static final int Awning = 8;
    private Paint mPaint;
    /**
     * bg
     */
    private Rect mBgRect;
    private int mBgColor;
    private int mBgHeight;
    private int mCornerRadius;
    /**
     *
     */
    private int mDotColor;
    private int mDotHeight;
    /**
     * out circle
     */
    private int mOutCircleHeight;
    /**
     * inner circle
     */
    private int mInnerCircleColor = Color.WHITE;
    private int mInnerCircleHeight;
    /**
     * text
     */
    private Paint mTextPaint;
    private int mTextSize;
    private int mStatusTextColor;
    private int mProgressTextColor;
    private int mTextMargin;
    /**
     * 波浪
     */
    private Path mWavePath;//滑块上升后的底座

    private boolean mDirty = true;
    private boolean offLine = false;/**不明白**/

    private int max;
    private float progress;
    private float secondProgress;
    private float dxProgress;
    private boolean mSlide = false;
    private float mTouchDownX, mTouchDownY, mMoveDistanceX;

    private float mCircleAnimDistance;
    private ValueAnimator mStartAnim, mEndAnim;

    private OnSeekBarChangeListener onProgressChangedListener;

    private int deviceMode;
    private Drawable leftControlDrawable, rightControlDrawable;
    private int controlDrawableWidth, controlDrawableHeight;

    private boolean hasPoint = true;
    //无行程
    private Drawable openDrawable;
    private Drawable closeDrawable;
    private Drawable pauseDrawable;
    private int unSelectedColor, selectedColor;
    private int openBackgroundColor, closeBackgroundColor, pauseBackgroundColor;
    private Rect openRect;
    private Rect pauseRect;
    private Rect closeRect;
    private OnClickListener onCloseListener;
    private OnClickListener onPauseListener;
    private OnClickListener onOpenListener;
    private boolean hidePause = false;

    private boolean isControl = true;/**不懂**/
    private boolean isByProgress = true;
    private boolean isOtherBgColor = false;
    private int mOtherBgColor = -1;

    public static final int TEXT = 0;
    public static final int IMAGE = 1;
    private int statusMode;
    private Drawable leftStatusDrawable, rightStatusDrawable, centerStatusDrawable;
    private int leftStatusDrawableWidth, leftStatusDrawableHeight, rightStatusDrawableWidth, rightStatusDrawableHeight, centerStatusDrawableWidth, centerStatusDrawableHeight;
    private Rect leftStatusRect, rightStatusRect, centerStatusRect;
    private OnClickListener onLeftStatusListener, onRightStatusListener, onCenterStatusListener;

    private boolean isMoveAll = false;//标记TDBU控制条整体移动

    public DeviceSeekBar(Context context) {
        super(context);
        init(null, 0);
    }

    public DeviceSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DeviceSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attributeSet, int defStyleAttr) {
        TypedArray typeArray =
                getContext().obtainStyledAttributes(attributeSet, R.styleable.DeviceSeekBarStyle);
        deviceMode = typeArray.getInt(R.styleable.DeviceSeekBarStyle_deviceMode, Normal);
        statusMode = typeArray.getInt(R.styleable.DeviceSeekBarStyle_statusMode, 0);
        leftStatusDrawable = typeArray.getDrawable(R.styleable.DeviceSeekBarStyle_leftStatusDrawable);
        rightStatusDrawable = typeArray.getDrawable(R.styleable.DeviceSeekBarStyle_rightStatusDrawable);
        centerStatusDrawable = typeArray.getDrawable(R.styleable.DeviceSeekBarStyle_centerStatusDrawable);
        typeArray.recycle();

        mBgColor = ContextCompat.getColor(getContext(), R.color.colorAccent);//背景条颜色
        mBgHeight = dp2px(42);//背景条高度
        mCornerRadius = dp2px(8);

        mDotColor = ContextCompat.getColor(getContext(), R.color.white);
        mDotHeight = dp2px(6);

        mOutCircleHeight = mBgHeight;//外圆直径等于背景条高度
        mInnerCircleHeight = dp2px(30);//内圆直径

        mProgressTextColor = ContextCompat.getColor(getContext(), R.color.textBlackColor);//文本颜色
        mStatusTextColor = ContextCompat.getColor(getContext(), R.color.textGrayColor);
        mTextSize = (int) getResources().getDimension(R.dimen.textSize10);//文本大小
        mTextMargin = dp2px(8);

        mWavePath = new Path();
        mWavePath.setFillType(Path.FillType.EVEN_ODD);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mProgressTextColor);
        max = 100;
        progress = 0;
        if (deviceMode == Angle) {
            max = 180;
            mBgColor = ContextCompat.getColor(getContext(), R.color.textGrayColor);
        }
        if (deviceMode == Angle_Half) {
            max = 90;
            mBgColor = ContextCompat.getColor(getContext(), R.color.textGrayColor);
        }
        leftControlDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_control);
        rightControlDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_control);
        if (leftControlDrawable != null) {
            controlDrawableHeight = leftControlDrawable.getIntrinsicHeight();//获得返回值为内部范围
            controlDrawableWidth = leftControlDrawable.getIntrinsicWidth();
        }

        selectedColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        unSelectedColor = ContextCompat.getColor(getContext(), R.color.oneWayDeviceBtnNormalState);
        openBackgroundColor = unSelectedColor;
        closeBackgroundColor = unSelectedColor;
        pauseBackgroundColor = unSelectedColor;

        if (leftStatusDrawable != null) {
            leftStatusDrawableHeight = leftStatusDrawable.getIntrinsicHeight();
            leftStatusDrawableWidth = leftStatusDrawable.getIntrinsicWidth();
        }
        if (rightStatusDrawable != null) {
            rightStatusDrawableHeight = rightStatusDrawable.getIntrinsicHeight();
            rightStatusDrawableWidth = rightStatusDrawable.getIntrinsicWidth();
        }
        if (centerStatusDrawable != null) {
            centerStatusDrawableHeight = centerStatusDrawable.getIntrinsicHeight();
            centerStatusDrawableWidth = centerStatusDrawable.getIntrinsicWidth();
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDirty) {
            measure();
            mDirty = false;
        }
        if (hasPoint) {//有行程
            //画进度条背景
            mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(mBgRect), mCornerRadius, mCornerRadius, mPaint);
            //画状态文字
            if (statusMode == TEXT) {
                mTextPaint.setColor(mStatusTextColor);
                mTextPaint.setTextAlign(Paint.Align.LEFT);
                canvas.drawText(getLeftStatusText(), 0, mBgRect.top - mTextMargin, mTextPaint);
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(getCenterStatusText(), mBgRect.right / 2, mBgRect.top - mTextMargin,
                        mTextPaint);
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(getRightStatusText(), mBgRect.right, mBgRect.top - mTextMargin,
                        mTextPaint);
            } else if (statusMode == IMAGE) {
                if (leftStatusDrawable != null) {
                    leftStatusRect = new Rect(0, mBgRect.top - leftStatusDrawableHeight,
                            mBgRect.left + leftStatusDrawableWidth, mBgRect.top);
                    leftStatusDrawable.setBounds(leftStatusRect);
                    leftStatusDrawable.draw(canvas);
                }
                if (centerStatusDrawable != null) {
                    centerStatusRect = new Rect((mBgRect.right - centerStatusDrawableWidth) / 2,
                            mBgRect.top - centerStatusDrawableHeight,
                            (mBgRect.right + centerStatusDrawableWidth) / 2, mBgRect.top);
                    centerStatusDrawable.setBounds(centerStatusRect);
                    centerStatusDrawable.draw(canvas);
                }
                if (rightStatusDrawable != null) {
                    rightStatusRect = new Rect(mBgRect.right - rightStatusDrawableWidth,
                            mBgRect.top - rightStatusDrawableHeight, mBgRect.right, mBgRect.top);
                    rightStatusDrawable.setBounds(rightStatusRect);
                    rightStatusDrawable.draw(canvas);
                }
            }
            mTextPaint.setColor(mProgressTextColor);
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(getLeftProgressText(), 0,
                    mBgRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(getCenterProgressText(), mBgRect.right / 2,
                    mBgRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
            mTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(getRightProgressText(), mBgRect.right,
                    mBgRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
            //画dot
            mPaint.setColor(mDotColor);
            mPaint.setAlpha(128);
            int dx = mBgRect.right / 12;
            for (int i = 0; i < 11; i++) {
                canvas.drawCircle(dx * (i + 1), mBgRect.top + (mBgRect.bottom - mBgRect.top) / 2,
                        mDotHeight / 2, mPaint);
            }
            mPaint.setAlpha(255);
            if (!offLine) {
                switch (deviceMode) {
                    case Curtain:
                    case Curtain_Right:
                    case Curtain_Left:
                    case Curtain_Wifi:
                        drawCurtain(canvas);
                        break;
                    case TDBU:
                        drawTDBU(canvas);
                        break;
                    default:
                        drawCircle(canvas);
                        break;
                }
            }
        } else {
            //无行程
            mPaint.setColor(closeBackgroundColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(closeRect), mCornerRadius, mCornerRadius, mPaint);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.DITHER_FLAG
                    | Paint.FILTER_BITMAP_FLAG));
            closeDrawable.setBounds(closeRect);
            closeDrawable.draw(canvas);
            if (!hidePause) {
                mPaint.setColor(pauseBackgroundColor);
                mPaint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(new RectF(pauseRect), mCornerRadius, mCornerRadius, mPaint);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                        | Paint.DITHER_FLAG
                        | Paint.FILTER_BITMAP_FLAG));
                pauseDrawable.setBounds(pauseRect);
                pauseDrawable.draw(canvas);
            }
            mPaint.setColor(openBackgroundColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(openRect), mCornerRadius, mCornerRadius, mPaint);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                    | Paint.DITHER_FLAG
                    | Paint.FILTER_BITMAP_FLAG));
            openDrawable.setBounds(openRect);
            openDrawable.draw(canvas);

            mTextPaint.setColor(mStatusTextColor);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
            int width = (mBgRect.right - mBgRect.left - dp2px(24)) / 3;
            canvas.drawText(getResources().getString(R.string.close), closeRect.left + width / 2,
                    closeRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
            if (!hidePause) {
                canvas.drawText(getResources().getString(R.string.pause),
                        pauseRect.left + width / 2,
                        pauseRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
            }
            canvas.drawText(getResources().getString(R.string.open), openRect.left + width / 2,
                    openRect.bottom + getUnderTextBaseLine(mTextPaint), mTextPaint);
        }
    }

    /**
     * curtain模式
     * @param canvas
     */
    private void drawCurtain(Canvas canvas) {//两球不能滑倒对方的范围内---默认为curtainWifi
        float leftCircleX = mBgRect.right / 2 - dxProgress * progress;//左侧圆球x轴坐标，progres计算为圆球中心点到背景框中心点
        float rightCircleX = mBgRect.right / 2 + dxProgress * progress;//右侧圆球x轴坐标同上
        float circleY = mBgRect.top + mOutCircleHeight / 2 - mCircleAnimDistance;//y轴坐标为球在背景中心高度，加上触摸动画偏移的y轴坐标
        if (deviceMode == Curtain_Right) {//如果模式为curtain右侧
            rightCircleX = mBgRect.right - mOutCircleHeight / 2;//右侧球位于背景最右侧不动
            leftCircleX = rightCircleX - dxProgress * (100 - progress);//左侧球坐标为右侧球球心减去progres的距离
        } else if (deviceMode == Curtain_Left) {//左侧球模式
            leftCircleX = mBgRect.left + mOutCircleHeight / 2;//左侧球固定在进度条最左侧不动
            rightCircleX = leftCircleX + dxProgress * (100 - progress);//右侧球坐标为左侧球球心往右progress的距离
        } else if (deviceMode == Curtain) {
            leftCircleX = mBgRect.right / 2 - dxProgress * (100 - progress);//进度为左右两侧
            rightCircleX = mBgRect.right / 2 + dxProgress * (100 - progress);
        }

        //画圆
        mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
        canvas.drawRect(leftCircleX, circleY - mOutCircleHeight / 2, rightCircleX,
                circleY + mOutCircleHeight / 2, mPaint); //绘制矩形位，位置为两侧球心为边界上下顶点为高度和底（左侧球心位置，圆球顶部，，右侧球心，圆球底部）
        canvas.drawCircle(leftCircleX, circleY, mOutCircleHeight / 2, mPaint);//绘制左侧小球
        canvas.drawCircle(rightCircleX, circleY, mOutCircleHeight / 2, mPaint);//绘制右侧小球

        mPaint.setColor(mInnerCircleColor);//设置内圆颜色
        canvas.drawRect(leftCircleX, circleY - mInnerCircleHeight / 2, rightCircleX,
                circleY + mInnerCircleHeight / 2, mPaint);//绘制比前一个矩形同位置高为内圆直径的矩形
        canvas.drawCircle(leftCircleX, circleY, mInnerCircleHeight / 2, mPaint);//绘制左侧小球内圆
        canvas.drawCircle(rightCircleX, circleY, mInnerCircleHeight / 2, mPaint);//绘制右侧小球内圆

        //画圆弧
        float left = leftCircleX - mOutCircleHeight / 4;//左侧圆心偏左四分之一直径
        float top = (float) (circleY + mOutCircleHeight * Math.cos(Math.PI / 6) / 2);//与y轴30度夹角交点高度
        float right = rightCircleX + mOutCircleHeight / 4;//右侧圆心偏左四分之一直径
        float bottom = mBgRect.top;//背景底部
        if (top < mBgRect.top) {//球升起即将脱离背景绘制底座
            mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
            canvas.drawRect(left, top, right, bottom, mPaint);
            float waveDia = dp2px(5f);
            mWavePath.reset();
            mWavePath.moveTo(left, top);
            mWavePath.quadTo(left + waveDia / 2, top + (mBgRect.top - top) / 2, left, bottom);
            mWavePath.close();
            mPaint.setColor(mInnerCircleColor);
            canvas.drawPath(mWavePath, mPaint);
            mWavePath.reset();
            mWavePath.moveTo(right, bottom);
            mWavePath.quadTo(right - waveDia / 2, top + (mBgRect.top - top) / 2, right, top);
            mWavePath.close();
            canvas.drawPath(mWavePath, mPaint);
        }

        //画当前进度文字
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        if (deviceMode == Curtain_Wifi) {
            canvas.drawText((100 - getProgress()) + "",
                    leftCircleX + (rightCircleX - leftCircleX) / 2,
                    mBgRect.top + getProgressTextBaseLine(mTextPaint) - mCircleAnimDistance,
                    mTextPaint);
        } else {
            canvas.drawText(getProgress() + "", leftCircleX + (rightCircleX - leftCircleX) / 2,
                    mBgRect.top + getProgressTextBaseLine(mTextPaint) - mCircleAnimDistance,
                    mTextPaint);
        }

        //画control
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG//设置画笔抗锯齿
                | Paint.DITHER_FLAG//处理图像抖动
                | Paint.FILTER_BITMAP_FLAG));//过滤图像优化操作
        if (rightCircleX - leftCircleX > mInnerCircleHeight && circleY > mBgRect.top) {//如果右侧坐标减去左侧坐标比内圆大，并且圆球没有升起
            if (deviceMode == Curtain || deviceMode == Curtain_Wifi) {
                leftControlDrawable.setBounds(new Rect((int) leftCircleX - controlDrawableWidth / 2,//左侧圆左侧边界
                        (int) circleY - controlDrawableHeight / 2,//圆球顶部边界----该矩形为刚好包裹左侧圆的正方形
                        (int) leftCircleX + controlDrawableWidth / 2,//圆球右侧边界
                        (int) circleY + controlDrawableHeight / 2));//圆球底部边界
                leftControlDrawable.draw(canvas);
                rightControlDrawable.setBounds(
                        new Rect((int) rightCircleX - controlDrawableWidth / 2,
                                (int) circleY - controlDrawableHeight / 2,
                                (int) rightCircleX + controlDrawableWidth / 2,
                                (int) circleY + controlDrawableHeight / 2));
                rightControlDrawable.draw(canvas);
            } else if (deviceMode == Curtain_Right) {
                leftControlDrawable.setBounds(new Rect((int) leftCircleX - controlDrawableWidth / 2,
                        (int) circleY - controlDrawableHeight / 2,
                        (int) leftCircleX + controlDrawableWidth / 2,
                        (int) circleY + controlDrawableHeight / 2));
                leftControlDrawable.draw(canvas);
            } else if (deviceMode == Curtain_Left) {
                rightControlDrawable.setBounds(
                        new Rect((int) rightCircleX - controlDrawableWidth / 2,
                                (int) circleY - controlDrawableHeight / 2,
                                (int) rightCircleX + controlDrawableWidth / 2,
                                (int) circleY + controlDrawableHeight / 2));
                rightControlDrawable.draw(canvas);
            }
        }
    }

    private void drawCircle(Canvas canvas) {
        float circleX = mOutCircleHeight / 2 + dxProgress * progress;//球直径+左侧滑块进程
        float circleY = mBgRect.top + mOutCircleHeight / 2 - mCircleAnimDistance;//（初始高度-上升高度）
        //画圆
        mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
        canvas.drawCircle(circleX, circleY, mOutCircleHeight / 2, mPaint);//外圆

        mPaint.setColor(mInnerCircleColor);
        canvas.drawCircle(circleX, circleY, mInnerCircleHeight / 2, mPaint);//内圆

        //画圆弧
        float left = circleX - mOutCircleHeight / 4;//外圆位置减去四分之一直径
        float top = (float) (circleY + mOutCircleHeight * Math.cos(Math.PI / 6) / 2);//Math.cos（PI/6）:计算与y轴角度为30时外圆位置相对圆心的y坐标
        float right = circleX + mOutCircleHeight / 4;//圆心x轴往右四分之一直径同left
        float bottom = mBgRect.top;//进度条顶部高度为目标高度
        if (top < mBgRect.top) {//当top值比背景小，说明如果滑块部分已经升起马上要脱离进度条，需要绘制底座让滑块继续与进度条链接
            mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);//判断是否有单独设定的颜色，默认为进度条颜色蓝色
            canvas.drawRect(left, top, right, bottom, mPaint);//绘制矩形
            float waveDia = dp2px(5f);
            mWavePath.reset();
            mWavePath.moveTo(left, top);//移动到矩形左上角位置
            mWavePath.quadTo(left + waveDia / 2, top + (mBgRect.top - top) / 2, left, bottom);//从矩形左上角开始像正下方偏移，绘制曲线
            mWavePath.close();
            mPaint.setColor(mInnerCircleColor);
            canvas.drawPath(mWavePath, mPaint);
            mWavePath.reset();
            mWavePath.moveTo(right, bottom);
            mWavePath.quadTo(right - waveDia / 2, top + (mBgRect.top - top) / 2, right, top);
            mWavePath.close();
            canvas.drawPath(mWavePath, mPaint);
        }

        //画当前进度文字
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        int progress = getProgress();
        if (deviceMode == Angle) {
            progress = Math.abs(progress - 90);
        }
        canvas.drawText(progress + "", circleX,
                mBgRect.top + getProgressTextBaseLine(mTextPaint) - mCircleAnimDistance,
                mTextPaint);
    }

    private void drawTDBU(Canvas canvas) {//左侧球可滑倒右侧，右侧球同理
        float leftCircleX = mBgRect.left + dxProgress * progress + mOutCircleHeight / 2;//左侧圆球x坐标，滑动范围为全部
        float rightCircleX = mBgRect.right - dxProgress * (max - secondProgress) - mOutCircleHeight / 2;//右侧圆球x轴坐标，滑动范围整个x进度条
        float circleY = mBgRect.top + mOutCircleHeight / 2 - mCircleAnimDistance;//同其他模式y轴坐标计算按住时偏移量

        //画圆
        mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
        canvas.drawRect(leftCircleX, circleY - mOutCircleHeight / 2, rightCircleX,//以左右球心x轴为两边外圆的顶和底为边界
                circleY + mOutCircleHeight / 2, mPaint);
        canvas.drawCircle(leftCircleX, circleY, mOutCircleHeight / 2, mPaint);//绘制左侧圆球
        canvas.drawCircle(rightCircleX, circleY, mOutCircleHeight / 2, mPaint);//绘制右侧圆球

        mPaint.setColor(mInnerCircleColor);
        canvas.drawRect(leftCircleX, circleY - mInnerCircleHeight / 2, rightCircleX,//同其他模式内外圆
                circleY + mInnerCircleHeight / 2, mPaint);
        canvas.drawCircle(leftCircleX, circleY, mInnerCircleHeight / 2, mPaint);
        canvas.drawCircle(rightCircleX, circleY, mInnerCircleHeight / 2, mPaint);

        //画圆弧
        float left = leftCircleX - mOutCircleHeight / 4;
        float top = (float) (circleY + mOutCircleHeight * Math.cos(Math.PI / 6) / 2);
        float right = rightCircleX + mOutCircleHeight / 4;
        float bottom = mBgRect.top;
        if (top < mBgRect.top) {//底座绘制与其他模式无区别
            mPaint.setColor(isOtherBgColor ? mOtherBgColor : mBgColor);
            canvas.drawRect(left, top, right, bottom, mPaint);
            float waveDia = dp2px(5f);
            mWavePath.reset();
            mWavePath.moveTo(left, top);
            mWavePath.quadTo(left + waveDia / 2, top + (mBgRect.top - top) / 2, left, bottom);
            mWavePath.close();
            mPaint.setColor(mInnerCircleColor);
            canvas.drawPath(mWavePath, mPaint);
            mWavePath.reset();
            mWavePath.moveTo(right, bottom);
            mWavePath.quadTo(right - waveDia / 2, top + (mBgRect.top - top) / 2, right, top);
            mWavePath.close();
            canvas.drawPath(mWavePath, mPaint);
        }

        //画当前进度文字
        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(getProgress() + "," + getSecondProgress(), leftCircleX + (rightCircleX - leftCircleX) / 2,
                mBgRect.top + getProgressTextBaseLine(mTextPaint) - mCircleAnimDistance, mTextPaint);

        //画control
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
        if (rightCircleX - leftCircleX > mInnerCircleHeight && circleY > mBgRect.top) {//如果两球内圆边界未触碰
            leftControlDrawable.setBounds(new Rect((int) leftCircleX - controlDrawableWidth / 2,
                    (int) circleY - controlDrawableHeight / 2,
                    (int) leftCircleX + controlDrawableWidth / 2,
                    (int) circleY + controlDrawableHeight / 2));
            leftControlDrawable.draw(canvas);
            rightControlDrawable.setBounds(
                    new Rect((int) rightCircleX - controlDrawableWidth / 2,
                            (int) circleY - controlDrawableHeight / 2,
                            (int) rightCircleX + controlDrawableWidth / 2,
                            (int) circleY + controlDrawableHeight / 2));
            rightControlDrawable.draw(canvas);
        }
    }

    private String getLeftStatusText() {
        switch (deviceMode) {
            case Angle:
                return "///";
            case Curtain_Wifi:
                return getResources().getString(R.string.open);
            case Curtain:
                return getResources().getString(R.string.open);
            case Curtain_Right:
                return getResources().getString(R.string.open);
            case Curtain_Left:
                return getResources().getString(R.string.close);
            case Angle_Half:
                return "///";
            default:
                return getResources().getString(R.string.open);
        }
    }

    private String getLeftProgressText() {
        switch (deviceMode) {
            case Angle:
                return "90°";
            case Curtain_Wifi:
                return "0%";
            case Curtain:
                return "0%";
            case Curtain_Right:
                return "0%";
            case Curtain_Left:
                return "100%";
            case Angle_Half:
                return "0°";
            default:
                return "0%";
        }
    }

    private String getRightStatusText() {
        switch (deviceMode) {
            case Angle:
                return "\\\\\\";
            case Curtain_Wifi:
                return getResources().getString(R.string.open);
            case Curtain:
                return getResources().getString(R.string.open);
            case Curtain_Right:
                return getResources().getString(R.string.close);
            case Curtain_Left:
                return getResources().getString(R.string.open);
            case Angle_Half:
                return "\\\\\\";
            default:
                return getResources().getString(R.string.close);
        }
    }

    private String getRightProgressText() {
        switch (deviceMode) {
            case Angle:
                return "90°";
            case Curtain_Wifi:
                return "0%";
            case Curtain:
                return "0%";
            case Curtain_Right:
                return "100%";
            case Curtain_Left:
                return "0%";
            case Angle_Half:
                return "90°";
            default:
                return "100%";
        }
    }

    private String getCenterStatusText() {
        switch (deviceMode) {
            case Angle:
                return "";
            case Curtain_Wifi:
                return getResources().getString(R.string.close);
            case Curtain:
                return getResources().getString(R.string.close);
            default:
                return "";
        }
    }

    private String getCenterProgressText() {
        switch (deviceMode) {
            case Angle:
                return "0°";
            case Curtain_Wifi:
                return "100%";
            case Curtain:
                return "100%";
            default:
                return "";
        }
    }

    private void measure() {
        mBgRect = new Rect(0, mOutCircleHeight + mTextMargin, getWidth(),
                mOutCircleHeight + mTextMargin + mBgHeight);
        switch (deviceMode) {
            case Curtain:
            case Curtain_Wifi:
                dxProgress = (float) (mBgRect.right / 2 - mOutCircleHeight / 2) / max;//进程只有一半
                break;
            default:
                dxProgress = (float) (mBgRect.right - mOutCircleHeight) / max;//满屏幕
                break;
        }
        //
        int width = (mBgRect.right - mBgRect.left - dp2px(24)) / 3;//一段比背景三分之一宽度略小的长度
        if (hidePause) {
            closeRect = new Rect(mBgRect.left + width / 2, mBgRect.top,//左：约六分之一屏幕，右约半个屏幕
                    mBgRect.left + width / 2 + width, mBgRect.bottom);
            pauseRect = new Rect(0, 0, 0, 0);
            openRect = new Rect(mBgRect.left + width + dp2px(12) + width + dp2px(12) - width / 2,
                    mBgRect.top,
                    mBgRect.left + width + dp2px(12) + width + dp2px(12) + width
                            - width / 2, mBgRect.bottom);
        } else {
            closeRect = new Rect(mBgRect.left, mBgRect.top, mBgRect.left + width, mBgRect.bottom);//左侧直接关闭按钮的绘制区域
            pauseRect = new Rect(mBgRect.left + width + dp2px(12), mBgRect.top,
                    mBgRect.left + width + dp2px(12) + width, mBgRect.bottom);
            openRect = new Rect(mBgRect.left + width + dp2px(12) + width + dp2px(12), mBgRect.top,
                    mBgRect.left + width + dp2px(12) + width + dp2px(12) + width,
                    mBgRect.bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            height = MeasureSpec.makeMeasureSpec(
                    mOutCircleHeight + mTextMargin + mBgHeight + mTextMargin + getTextHeight(
                            mTextPaint), MeasureSpec.AT_MOST);
        } else {
            height = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, height);
    }

    private float touchDownProgress;
    private float touchDownSecondProgress;
    private Rect touchRect;
    private boolean isClickStatus = false;
    private boolean isMoveLeft = false;
    private boolean isMoveRight = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (offLine) {
            return false;
        }
        if (!isControl) {
            return false;
        }
        if (!hasPoint) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("deviceLog","nopint-Down");

                    if (closeRect.contains((int) event.getX(), (int) event.getY())) {
                        if (isCurtain()) {
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_close_pressed);
                        }else if(isAwning()){
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_close_press);
                        } else {
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close_pressed);
                        }
                        closeBackgroundColor = selectedColor;
                        touchRect = closeRect;
                    } else if (pauseRect.contains((int) event.getX(), (int) event.getY())) {
                        if (isCurtain()) {
                            pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
                        } else {
                            pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
                        }
                        pauseBackgroundColor = selectedColor;
                        touchRect = pauseRect;
                    } else if (openRect.contains((int) event.getX(), (int) event.getY())) {
                        if (isCurtain()) {
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_open_pressed);
                        }else if(isAwning()){
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_open_press);
                        } else {
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open_pressed);
                        }
                        openBackgroundColor = selectedColor;
                        touchRect = openRect;
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Log.d("deviceLog","nopoint-up");

                    if (isByProgress) {
                        if (touchRect == closeRect) {
                            if (progress != Constants.CmdData.Operation_Close) {
                                if (isCurtain()) {
                                    closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_close);
                                }else if(isAwning()){
                                    closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_close);
                                } else {
                                    closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close);
                                }
                                closeBackgroundColor = unSelectedColor;
                            }
                        } else if (touchRect == pauseRect) {
                            if (progress != Constants.CmdData.Operation_Stop) {
                                if (isCurtain()) {
                                    pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                                } else {
                                    pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                                }
                                pauseBackgroundColor = unSelectedColor;
                            }
                        } else if (touchRect == openRect) {
                            if (progress != Constants.CmdData.Operation_Open) {
                                if (isCurtain()) {
                                    openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_open);
                                }else if(isAwning()){
                                    openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_open);
                                } else {
                                    openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open);
                                }
                                openBackgroundColor = unSelectedColor;
                            }
                        }
                    } else {
                        if (isCurtain()) {
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_open);
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_close);
                            pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                        }else if(isAwning()){
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_open);
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_close);
                            pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                        } else {
                            openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open);
                            closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close);
                            pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                        }
                        closeBackgroundColor = unSelectedColor;
                        pauseBackgroundColor = unSelectedColor;
                        openBackgroundColor = unSelectedColor;
                    }
                    invalidate();
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (closeRect.contains((int) event.getX(), (int) event.getY())) {
                            if (onCloseListener != null) {
                                onCloseListener.onClick(this);
                            }
                        } else if (pauseRect.contains((int) event.getX(), (int) event.getY())) {
                            if (onPauseListener != null) {
                                onPauseListener.onClick(this);
                            }
                        } else if (openRect.contains((int) event.getX(), (int) event.getY())) {
                            if (onOpenListener != null) {
                                onOpenListener.onClick(this);
                            }
                        }
                    }
                    break;
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d("deviceLog","haspoint-Down");
                    mTouchDownX = event.getX();//手指刚按下时xy坐标
                    mTouchDownY = event.getY();
                    if (deviceMode == Curtain_Wifi) {//触摸点为两球中间部分
                        if (mTouchDownX > (mBgRect.right /    //触摸位置x轴不在左侧球的左边
                                - dxProgress * progress
                                - mOutCircleHeight / 2)
                                && mTouchDownX < (mBgRect.right / 2    //位置也不再右侧球的右侧
                                + dxProgress * progress
                                + mOutCircleHeight / 2)
                                && mTouchDownY > mBgRect.top        //y轴坐标不在背景的外围
                                && mTouchDownY < mBgRect.bottom) {
                            attemptClaimDrag();//拦截监听
                            touchDownProgress = progress;//获取当前进程用于滑动
                            startSlide();//开始滑动
                            if (onProgressChangedListener != null) {
                                onProgressChangedListener.onStartTrackingTouch(this);
                            }
                        } else if (statusMode == IMAGE &&   //如果按钮是图片模式 且左中右图片不为空，且触摸位置在左中右图片之一的的范围
                                (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                        rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                            isClickStatus = true;
                        } else {
                            return false;
                        }
                    } else if (deviceMode == Curtain) {
                        if (mTouchDownX > (mBgRect.right / 2        //综合计算判断触摸点在两球中间位置时执行
                                - dxProgress * (100 - progress)     //触摸点为左侧球右边
                                - mOutCircleHeight / 2)              //触摸点为右侧球左边
                                && mTouchDownX < (mBgRect.right / 2
                                + dxProgress * (100 - progress)
                                + mOutCircleHeight / 2)
                                && mTouchDownY > mBgRect.top           //触摸高度在背景范围内
                                && mTouchDownY < mBgRect.bottom) {
                            attemptClaimDrag();
                            touchDownProgress = progress;
                            startSlide();
                            if (onProgressChangedListener != null) {
                                onProgressChangedListener.onStartTrackingTouch(this);
                            }
                        } else if (statusMode == IMAGE &&
                                (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                        rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                            isClickStatus = true;
                        } else {
                            return false;
                        }
                    } else if (deviceMode == Curtain_Right) {
                        if (mTouchDownX > (mBgRect.right          //触摸点在背景板上但是没有按倒小球
                                - dxProgress * (100 - progress)                         //触摸点在左侧球左边
                                - mOutCircleHeight)
                                && mTouchDownX < (mBgRect.right - dxProgress * (100     //触摸点在
                                - progress))
                                && mTouchDownY > mBgRect.top
                                && mTouchDownY < mBgRect.bottom) {
                            attemptClaimDrag();
                            touchDownProgress = progress;
                            startSlide();
                            if (onProgressChangedListener != null) {
                                onProgressChangedListener.onStartTrackingTouch(this);
                            }
                        } else if (statusMode == IMAGE &&       //按在球上了
                                (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                        rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                            isClickStatus = true;
                        } else {
                            return false;
                        }
                    } else if (deviceMode == Curtain_Left) {
                        if (mTouchDownX > dxProgress * (100 - progress)
                                && mTouchDownX < (dxProgress
                                * (100 - progress) + mOutCircleHeight)
                                && mTouchDownY > mBgRect.top
                                && mTouchDownY < mBgRect.bottom) {
                            attemptClaimDrag();
                            touchDownProgress = progress;
                            startSlide();
                            if (onProgressChangedListener != null) {
                                onProgressChangedListener.onStartTrackingTouch(this);
                            }
                        } else if (statusMode == IMAGE &&
                                (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                        rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                            isClickStatus = true;
                        } else {
                            return false;
                        }//Tdbu模式
                    } else if (deviceMode == TDBU) {
                        if (isMoveAll) {//是否移动全部？？
                            if (mTouchDownX > dxProgress * progress             //触摸点在左侧滑块右侧
                                    && mTouchDownX < (dxProgress * secondProgress + mOutCircleHeight)//触摸点在右侧滑块左侧
                                    && mTouchDownY > mBgRect.top                   //触摸点没有超出背景
                                    && mTouchDownY < mBgRect.bottom) {
                                attemptClaimDrag();
                                touchDownProgress = progress;                   //
                                touchDownSecondProgress = secondProgress;
                                startSlide();
                                if (onProgressChangedListener != null) {
                                    onProgressChangedListener.onStartTrackingTouch(this);
                                }
                            } else if (statusMode == IMAGE &&
                                    (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                            rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                    centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                                isClickStatus = true;
                            } else {
                                return false;
                            }
                        } else {//是否不移动全部？
                            if (mTouchDownX > dxProgress * progress                     //右侧划款在左侧滑块左边且触摸点在右侧滑块上
                                    && mTouchDownX < (dxProgress * progress + mOutCircleHeight) //左侧滑块左边
                                    && mTouchDownX > dxProgress * secondProgress                //右侧滑块右边
                                    && mTouchDownX < (dxProgress * secondProgress + mOutCircleHeight)//右侧滑块右边界的左边
                                    && mTouchDownY > mBgRect.top
                                    && mTouchDownY < mBgRect.bottom) {

                            } else if (mTouchDownX > dxProgress * progress       //按钮在左侧滑块上                 //左侧滑块右边
                                    && mTouchDownX < (dxProgress * progress + mOutCircleHeight)    //左侧滑块右边界左边
                                    && mTouchDownY > mBgRect.top                                //触摸点在右侧滑块上
                                    && mTouchDownY < mBgRect.bottom) {
                                isMoveLeft = true;  //是否移动左边
                                attemptClaimDrag();
                                touchDownProgress = progress;
                                startSlide();
                                if (onProgressChangedListener != null) {
                                    onProgressChangedListener.onStartTrackingTouch(this);
                                }
                            } else if (mTouchDownX > dxProgress * secondProgress            //按钮在右侧滑块上
                                    && mTouchDownX < (dxProgress * secondProgress + mOutCircleHeight)//
                                    && mTouchDownY > mBgRect.top
                                    && mTouchDownY < mBgRect.bottom) {
                                isMoveRight = true;  //移动右侧滑块
                                attemptClaimDrag();
                                touchDownProgress = secondProgress;
                                startSlide();
                                if (onProgressChangedListener != null) {
                                    onProgressChangedListener.onStartTrackingTouch(this);
                                }
                            } else if (statusMode == IMAGE &&
                                    (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                            rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                    centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                                isClickStatus = true;
                            } else {
                                return false;
                            }
                        }

                    } else {
                        if (mTouchDownX > dxProgress * progress         //左滑块右侧
                                && mTouchDownX < (dxProgress            //左滑块右边界左侧
                                * progress + mOutCircleHeight)          //未超出边界
                                && mTouchDownY > mBgRect.top
                                && mTouchDownY < mBgRect.bottom) {
                            attemptClaimDrag();
                            touchDownProgress = progress;           //按住的条时左滑块的条
                            startSlide();
                            if (onProgressChangedListener != null) {
                                onProgressChangedListener.onStartTrackingTouch(this);
                            }
                        } else if (statusMode == IMAGE &&
                                (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY()) ||
                                        rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) ||
                                centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                            isClickStatus = true;
                        } else {
                            return false;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d("deviceLog","haspoint-move");

                    if (!isClickStatus) {  //处于点击状态
                        if (deviceMode == Curtain_Wifi) {
                            mMoveDistanceX = Math.abs(event.getX() - mBgRect.right / 2);//滑块移动相对于中心点的距离
                            progress = mMoveDistanceX / dxProgress;
                        } else if (deviceMode == Curtain) {
                            mMoveDistanceX = Math.abs(event.getX() - mBgRect.right / 2);
                            progress = 100 - mMoveDistanceX / dxProgress;
                        } else if (deviceMode == Curtain_Right) {
                            mMoveDistanceX = event.getX() - mTouchDownX;
                            float tempProgress = mMoveDistanceX / dxProgress;
                            progress = touchDownProgress + tempProgress;
                        } else if (deviceMode == Curtain_Left) {
                            mMoveDistanceX = event.getX() - mTouchDownX;
                            float tempProgress = mMoveDistanceX / dxProgress;
                            progress = touchDownProgress - tempProgress;
                        } else if (deviceMode == TDBU) {
                            mMoveDistanceX = event.getX() - mTouchDownX;
                            if (!isMoveLeft && !isMoveRight && !isMoveAll) {
                                if (mMoveDistanceX < 0) {
                                    isMoveLeft = true;
                                    attemptClaimDrag();
                                    touchDownProgress = progress;
                                    startSlide();
                                    if (onProgressChangedListener != null) {
                                        onProgressChangedListener.onStartTrackingTouch(this);
                                    }
                                }
                                if (mMoveDistanceX > 0) {
                                    isMoveRight = true;
                                    attemptClaimDrag();
                                    touchDownProgress = secondProgress;
                                    startSlide();
                                    if (onProgressChangedListener != null) {
                                        onProgressChangedListener.onStartTrackingTouch(this);
                                    }
                                }
                            }
                            float tempProgress = mMoveDistanceX / dxProgress;
                            if (isMoveLeft) {
                                progress = touchDownProgress + tempProgress;
                            } else if (isMoveRight) {
                                secondProgress = touchDownProgress + tempProgress;
                            } else if (isMoveAll) {
                                progress = touchDownProgress + tempProgress;
                                secondProgress = touchDownSecondProgress + tempProgress;
                            }
                        } else {
                            mMoveDistanceX = event.getX() - mTouchDownX;
                            float tempProgress = mMoveDistanceX / dxProgress;
                            progress = touchDownProgress + tempProgress;
                        }
                        if (progress > max) {
                            progress = max;
                        }
                        if (progress < 0) {
                            progress = 0;
                        }
                        if (secondProgress > max) {
                            secondProgress = max;
                        }
                        if (secondProgress < 0) {
                            secondProgress = 0;
                        }
                        if (isMoveRight && secondProgress < progress) {
                            secondProgress = progress;
                        }
                        if (isMoveLeft && progress > secondProgress) {
                            progress = secondProgress;
                        }
                        if (isMoveAll &&//如果原来的间距比现在的间距小
                                (secondProgress - progress) < (touchDownSecondProgress - touchDownProgress)) {
                            if (progress == 0) {//如果左滑块在最左边
                                secondProgress = progress + (touchDownSecondProgress - touchDownProgress);
                            } else if (secondProgress == max) {
                                progress = secondProgress - (touchDownSecondProgress - touchDownProgress);
                            }
                        }
                        if (onProgressChangedListener != null) {
                            onProgressChangedListener.onProgressChanged(this);
                        }
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    Log.d("deviceLog","haspoint-up");

                    if (!isClickStatus) {
                        isMoveLeft = false;
                        isMoveRight = false;
                        endSlide();
                        if (onProgressChangedListener != null) {
                            onProgressChangedListener.onStopTrackingTouch(this);
                        }
                    } else {
                        isClickStatus = false;//事件结束通过接口传值
                        if (event.getAction() == MotionEvent.ACTION_UP && statusMode == IMAGE) {
                            if (leftStatusDrawable != null && leftStatusRect.contains((int) event.getX(), (int) event.getY())) {
                                if (onLeftStatusListener != null) {
                                    onLeftStatusListener.onClick(this);
                                }
                            } else if (rightStatusDrawable != null && rightStatusRect.contains((int) event.getX(), (int) event.getY())) {
                                if (onRightStatusListener != null) {
                                    onRightStatusListener.onClick(this);
                                }
                            } else if (centerStatusDrawable != null && centerStatusRect.contains((int) event.getX(), (int) event.getY())) {
                                if (onCenterStatusListener != null) {
                                    onCenterStatusListener.onClick(this);
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }

    private void attemptClaimDrag() {
        //mParent = getParent();
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void startSlide() { //将滑块升到上方防止被手指遮住
        mStartAnim = ValueAnimator.ofFloat(0.0f, 1.1f, 1.0f);
        mStartAnim.setDuration(200);
        mStartAnim.setInterpolator(new LinearInterpolator());
        mStartAnim.addUpdateListener(animation -> {
            mCircleAnimDistance = mOutCircleHeight * (float) animation.getAnimatedValue();
            invalidate();
        });


        mStartAnim.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                mSlide = true;
                invalidate();
            }
        });
        mStartAnim.start();
    }

    private void endSlide() {//结束滑动，将滑块降回下方
        mEndAnim = ValueAnimator.ofFloat(1.0f, 0.0f);
        mEndAnim = ValueAnimator.ofFloat(0f,1f,0.5f,1f);
        mEndAnim.setDuration(200);
        mEndAnim.setInterpolator(new LinearInterpolator());
        mEndAnim.addUpdateListener(animation -> {
            mCircleAnimDistance = mOutCircleHeight * (float) animation.getAnimatedValue();
            invalidate();
        });
        mEndAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlide = false;
                invalidate();
            }
        });
        mEndAnim.start();
    }

    public int getProgress() {
        if (deviceMode == Curtain) {//向上取整，解决100%不好选取的情况
            return (int) Math.ceil(progress);
        }
        return (int) progress;
    }

    public void setProgress(int progress) {
        if (!hasPoint) {
            if (isByProgress) {
                if (progress == Constants.CmdData.Operation_Close) {
                    if (isCurtain()) {
                        closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_close_pressed);
                    } else {
                        closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close_pressed);
                    }
                    openBackgroundColor = unSelectedColor;
                    closeBackgroundColor = selectedColor;
                    pauseBackgroundColor = unSelectedColor;
                } else if (progress == Constants.CmdData.Operation_Open) {
                    if (isCurtain()) {
                        openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_open_pressed);
                    } else {
                        openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open_pressed);
                    }
                    openBackgroundColor = selectedColor;
                    closeBackgroundColor = unSelectedColor;
                    pauseBackgroundColor = unSelectedColor;
                } else if (progress == Constants.CmdData.Operation_Stop) {
                    pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
                    openBackgroundColor = unSelectedColor;
                    closeBackgroundColor = unSelectedColor;
                    pauseBackgroundColor = selectedColor;
                }
            } else {
                openBackgroundColor = unSelectedColor;
                closeBackgroundColor = unSelectedColor;
                pauseBackgroundColor = unSelectedColor;
            }
        }
        if (progress != getProgress()) {
            this.progress = progress;
            if (progress < 0) {
                this.progress = 0;
            }
            if (progress > max) {
                this.progress = max;
            }
            invalidate();
        }
    }

    public int getSecondProgress() {
        if (deviceMode == Curtain) {//向上取整，解决100%不好选取的情况
            return (int) Math.ceil(secondProgress);
        }
        return (int) secondProgress;
    }

    public void setSecondProgress(int secondProgress) {
        if (hasPoint) {
            if (secondProgress != getSecondProgress()) {
                this.secondProgress = secondProgress;
                if (secondProgress < 0) {
                    this.secondProgress = 0;
                }
                if (secondProgress > max) {
                    this.secondProgress = max;
                }
                invalidate();
            }
        }
    }

    public int getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(int mode) {
        deviceMode = mode;
        if (deviceMode == Angle) {
            max = 180;
        }
        if (deviceMode == Angle_Half) {
            max = 90;
        }
        mDirty = true;
        invalidate();
    }

    public boolean isHasPoint() {
        return hasPoint;
    }

    public void setHasPoint(boolean hasPoint) {
        this.hasPoint = hasPoint;
        if (!hasPoint) {
            switch (deviceMode) {
                case Curtain:
                case Curtain_Right:
                case Curtain_Left:
                case Curtain_Wifi:
                    openDrawable = ContextCompat
                            .getDrawable(getContext(), R.drawable.ic_device_curtain_open);
                    closeDrawable = ContextCompat
                            .getDrawable(getContext(), R.drawable.ic_device_curtain_close);
                    pauseDrawable =
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                    break;
                case Awning:
                    openDrawable = ContextCompat
                            .getDrawable(getContext(), R.drawable.ic_device_awning_open);
                    closeDrawable = ContextCompat
                            .getDrawable(getContext(), R.drawable.ic_device_awning_close);
                    pauseDrawable =
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                    break;
                default:
                    openDrawable =
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open);
                    closeDrawable =
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close);
                    pauseDrawable =
                            ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause);
                    break;
            }
        }
        invalidate();
    }

    private boolean isCurtain() {
        switch (deviceMode) {
            case Curtain:
            case Curtain_Right:
            case Curtain_Left:
            case Curtain_Wifi:
                return true;
            default:
                return false;
        }
    }

    private boolean isAwning() {
        switch (deviceMode) {
            case Awning:
                return true;
            default:
                return false;
        }
    }

    public boolean isOffLine() {
        return offLine;
    }

    public void setOffLine(boolean offLine) {
        this.offLine = offLine;
        if (offLine) {
            mBgColor = ContextCompat.getColor(getContext(), R.color.deviceOffline);
            mProgressTextColor = ContextCompat.getColor(getContext(), R.color.deviceOffline);
            mStatusTextColor = ContextCompat.getColor(getContext(), R.color.deviceOffline);

            if (isCurtain()) {
                openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_open_pressed);
                closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_curtain_close_pressed);
                pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
            }else if(isAwning()){
                openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_open_press);
                closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_awning_close_press);
                pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
            } else {
                openDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_open_pressed);
                closeDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_close_pressed);
                pauseDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_device_pause_pressed);
            }
            openBackgroundColor = mStatusTextColor;
            closeBackgroundColor = mStatusTextColor;
            pauseBackgroundColor = mStatusTextColor;
        } else {
            if (deviceMode == Angle || deviceMode == Angle_Half) {
                mBgColor = ContextCompat.getColor(getContext(), R.color.textGrayColor);
            } else {
                mBgColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
            }
            mProgressTextColor = ContextCompat.getColor(getContext(), R.color.textBlackColor);
            mStatusTextColor = ContextCompat.getColor(getContext(), R.color.textGrayColor);
            if (isByProgress) {
                if (progress == Constants.CmdData.Operation_Close) {
                    openBackgroundColor = unSelectedColor;
                    closeBackgroundColor = selectedColor;
                    pauseBackgroundColor = unSelectedColor;
                } else if (progress == Constants.CmdData.Operation_Open) {
                    openBackgroundColor = selectedColor;
                    closeBackgroundColor = unSelectedColor;
                    pauseBackgroundColor = unSelectedColor;
                } else if (progress == Constants.CmdData.Operation_Stop) {
                    openBackgroundColor = unSelectedColor;
                    closeBackgroundColor = unSelectedColor;
                    pauseBackgroundColor = selectedColor;
                }
            } else {
                openBackgroundColor = unSelectedColor;
                closeBackgroundColor = unSelectedColor;
                pauseBackgroundColor = unSelectedColor;
            }
        }
        invalidate();
    }

    public void hidePause() {
        this.hidePause = true;
        mDirty = true;
        invalidate();
    }

    public OnClickListener getOnCloseListener() {
        return onCloseListener;
    }

    public void setOnCloseListener(OnClickListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    public OnClickListener getOnPauseListener() {
        return onPauseListener;
    }

    public void setOnPauseListener(OnClickListener onPauseListener) {
        this.onPauseListener = onPauseListener;
    }

    public OnClickListener getOnOpenListener() {
        return onOpenListener;
    }

    public void setOnOpenListener(OnClickListener onOpenListener) {
        this.onOpenListener = onOpenListener;
    }

    public OnSeekBarChangeListener getOnProgressChangedListener() {
        return onProgressChangedListener;
    }

    public void setOnProgressChangedListener(OnSeekBarChangeListener onProgressChangedListener) {
        this.onProgressChangedListener = onProgressChangedListener;
    }

    private float getProgressTextBaseLine(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return mBgHeight / 2 - fm.top / 2 - fm.bottom / 2;
    }

    private float getUnderTextBaseLine(Paint paint) {//基准线
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (getTextHeight(mTextPaint) + mTextMargin) / 2 - fm.top / 2 - fm.bottom / 2;
    }

    private int getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.bottom - fm.top);
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                getContext().getResources().getDisplayMetrics());
    }

    public void setControl(boolean control) {
        isControl = control;
    }

    public void setBtnBackgroundByProgress(boolean byProgress) {
        isByProgress = byProgress;
    }

    public void setOtherBgColor(int color) {
        if (color != -1) {
            isOtherBgColor = true;
            mOtherBgColor = color;
        } else {
            isOtherBgColor = false;
            mOtherBgColor = -1;
        }
        invalidate();
    }

    public void setStatusMode(int statusMode) {
        this.statusMode = statusMode;
        invalidate();
    }

    public void setLeftStatusDrawable(Drawable drawable) {
        if (drawable != null && this.leftStatusDrawable != drawable) {
            this.leftStatusDrawable = drawable;
            this.leftStatusDrawableWidth = this.leftStatusDrawable.getIntrinsicWidth();
            this.leftStatusDrawableHeight = this.leftStatusDrawable.getIntrinsicHeight();
            invalidate();
        }
    }

    public void setRightStatusDrawable(Drawable drawable) {
        if (drawable != null && this.rightStatusDrawable != drawable) {
            this.rightStatusDrawable = drawable;
            this.rightStatusDrawableWidth = this.rightStatusDrawable.getIntrinsicWidth();
            this.rightStatusDrawableHeight = this.rightStatusDrawable.getIntrinsicHeight();
            invalidate();
        }
    }

    public void setCenterStatusDrawable(Drawable drawable) {
        if (drawable != null && this.centerStatusDrawable != drawable) {
            this.centerStatusDrawable = drawable;
            this.centerStatusDrawableWidth = this.centerStatusDrawable.getIntrinsicWidth();
            this.centerStatusDrawableHeight = this.centerStatusDrawable.getIntrinsicHeight();
            invalidate();
        }
    }

    public OnClickListener getOnLeftStatusListener() {
        return onLeftStatusListener;
    }

    public void setOnLeftStatusListener(OnClickListener onLeftStatusListener) {
        this.onLeftStatusListener = onLeftStatusListener;
    }

    public OnClickListener getOnRightStatusListener() {
        return onRightStatusListener;
    }

    public void setOnRightStatusListener(OnClickListener onRightStatusListener) {
        this.onRightStatusListener = onRightStatusListener;
    }

    public OnClickListener getOnCenterStatusListener() {
        return onCenterStatusListener;
    }

    public void setOnCenterStatusListener(OnClickListener onCenterStatusListener) {
        this.onCenterStatusListener = onCenterStatusListener;
    }

    public boolean isMoveAll() {
        return isMoveAll;
    }

    public void setMoveAll(boolean moveAll) {
        isMoveAll = moveAll;
    }

    public interface OnSeekBarChangeListener {
        void onStartTrackingTouch(DeviceSeekBar seekBar);

        void onStopTrackingTouch(DeviceSeekBar seekBar);

        void onProgressChanged(DeviceSeekBar seekBar);
    }

}
