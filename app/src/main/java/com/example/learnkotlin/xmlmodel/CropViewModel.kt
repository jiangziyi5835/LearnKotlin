package com.example.learnkotlin.xmlmodel

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField

class CropViewModel : BaseObservable() {
    val name=ObservableField("")
    var finishClick:View.OnClickListener?=null


}