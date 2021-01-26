package com.example.learnkotlin.xmlmodel

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.example.learnkotlin.view.DeviceSeekBar

class SecondViewModel : BaseObservable() {
    var jumpClicklistener:View.OnClickListener?=null
    var name=ObservableField<String>("")
    var choosePhoto=ObservableField<String>()
    var chooseClick:View.OnClickListener?=null
    var progress=ObservableField<Int>()
    var hasPoint=ObservableBoolean(false)
    var deviceMode = ObservableInt(0)
    var OpenListener:View.OnClickListener?=null
    var closeListener:View.OnClickListener?=null
    var pauseListener:View.OnClickListener?=null
    var onLeftStatusClick: View.OnClickListener? = null
    var onRightStatusClick: View.OnClickListener? = null
    var onCenterStatusClick: View.OnClickListener? = null
    var onSeekBarChange: DeviceSeekBar.OnSeekBarChangeListener? = null
    var leftStatusDrawable = ObservableField<Drawable>()
    var rightStatusDrawable = ObservableField<Drawable>()
    var centerStatusDrawable = ObservableField<Drawable>()
    var statusMode = ObservableInt(0)
    var image=ObservableField<Drawable>()
    var imageUrl=ObservableField<Any>()
    var input=ObservableField("")


}