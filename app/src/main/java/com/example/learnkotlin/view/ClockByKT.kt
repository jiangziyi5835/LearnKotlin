package com.example.learnkotlin.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.learnkotlin.R

class ClockByKT : View {
    var dishColor: Int = 0
    var hourColor: Int = 0
    var minuteColor: Int = 0
    val dishPaint: Paint by lazy { Paint() }
    val hourPaint: Paint by lazy { Paint() }
    val minutePaint: Paint by lazy { Paint() }
    var hour: Int = 0
    var minute: Int = 0


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    fun init(context: Context, attributeSet: AttributeSet?) {
        var typedArray: TypedArray = context.obtainStyledAttributes(attributeSet,
            R.styleable.Clock
        )
        dishColor = typedArray.getColor(R.styleable.Clock_dishColor, Color.BLACK)
        hourColor = typedArray.getColor(R.styleable.Clock_hourcolor, Color.BLACK)
        minuteColor = typedArray.getColor(R.styleable.Clock_mimuteColor, Color.BLACK)
        hour = typedArray.getInt(R.styleable.Clock_hour, 0)
        minute = typedArray.getInt(R.styleable.Clock_minute, 0)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        if (canvas != null) {
            drawCircle(canvas)
            drawScale(canvas)
            drawHour(canvas)
            drawMinute(canvas)
        }
    }

    fun drawMinute(canvas: Canvas) {
        var minuteDgree :Int = minute % 60 *6
        minutePaint.color = minuteColor
        minutePaint.strokeWidth = 5f
        minutePaint.style = Paint.Style.STROKE
        minutePaint.isAntiAlias = true
        canvas.drawLine(
            0f, 0f, getPointX(minuteDgree, measuredHeight / 4),
            -getPointY(minuteDgree, measuredHeight / 4), minutePaint
        )
    }


    fun drawHour(canvas: Canvas) {
        var hourDegree :Int= hour % 12 * 30
        hourPaint.color = hourColor
        hourPaint.strokeWidth = 7f
        hourPaint.style = Paint.Style.STROKE
        hourPaint.isAntiAlias = true
        canvas.drawLine(
            0f, 0f, getPointX(hourDegree, measuredHeight / 5),
            -getPointY(hourDegree, measuredHeight /5), hourPaint
        )
    }

    fun drawCircle(canvas: Canvas) {
        dishPaint.color = dishColor
        dishPaint.strokeWidth = 5F
        dishPaint.isAntiAlias = true
        dishPaint.style = Paint.Style.STROKE
        canvas.translate((measuredWidth / 2 + 5).toFloat(), (measuredHeight / 2 + 5).toFloat())
        canvas.drawCircle(0f, 0f, (measuredHeight / 2 - 10).toFloat(), dishPaint)


    }

    fun drawScale(canvas: Canvas) {
        for (i in 0..11) {
            if (i % 3 == 0) {
                canvas.drawLine(
                    0f, (measuredHeight / 2 - 30).toFloat(), 0f,
                    (measuredHeight / 2 - 55).toFloat(), dishPaint
                )
            } else {
                canvas.drawCircle(0f, (measuredHeight/2-35).toFloat(),3f,dishPaint)
//                canvas.drawLine(
//                    0f, (measuredHeight / 2 - 20).toFloat(), 0f,
//                    (measuredHeight / 2 - 30).toFloat(), dishPaint
//                )
            }
            canvas.rotate(30f)
        }
    }

    fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
        invalidate()

    }

//    fun getPointX(angle: Int, diameter: Int) = Math.sin(Math.toRadians(angle.toDouble()) )* diameter
//    fun getPointY(angle: Int, diameter: Int) = Math.cos(Math.toRadians(angle.toDouble()) )* diameter
    fun getPointX(angle: Int, diameter: Int) :Float= (Math.sin(angle * Math.PI / 180) * diameter).toFloat()
    fun getPointY(angle: Int, diameter: Int) :Float= (Math.cos(angle * Math.PI / 180) * diameter).toFloat()


}

