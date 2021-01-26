package com.example.learnkotlin.view

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.BR


/**
 * Created by libra on 2017/6/15
 */

abstract class BaseBindingViewHolder(
        private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    /**
     * 绑定数据
     *
     * @param obj obj
     */
    open fun bindViewHolder(position: Int, obj: Any?) {
        binding.setVariable(BR.xmlmodel, initXmlModel(position, obj))
        binding.executePendingBindings()
    }

    open fun bindViewHolder(position: Int, obj: Any?, payloads: MutableList<Any>?) {
    }

    open fun getBinding(): ViewDataBinding = binding

    abstract fun initXmlModel(position: Int, any: Any?): BaseXmlModel
}
