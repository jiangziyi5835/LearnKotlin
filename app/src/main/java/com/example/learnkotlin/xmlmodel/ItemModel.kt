package com.example.learnkotlin.xmlmodel

import androidx.databinding.ObservableField
import com.example.learnkotlin.view.BaseXmlModel

class ItemModel: BaseXmlModel() {
    var title=ObservableField<String>()
    var contents=ObservableField<String>()
}