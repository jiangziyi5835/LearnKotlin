package com.example.learnkotlin.xmlmodel

import android.view.View
import android.widget.SeekBar
import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.adapters.TextViewBindingAdapter
import androidx.lifecycle.ViewModel

class MainViewModel : BaseObservable() {
    var textContent=ObservableField("默认值")
    var nameClick: View.OnClickListener? =null
    var textInput=ObservableField("")
    var hour=ObservableField("")

    var inputListener:TextViewBindingAdapter.OnTextChanged?=null
    var timeHour=ObservableField<Int>()
    var timeMinute=ObservableField<Int>()

    var selectHour=ObservableField<Int>()
    var selectMinute=ObservableField<Int>()

    var progress=ObservableField<Int>(0)
    var seekBarChangeListener: SeekBar.OnSeekBarChangeListener?=null


}