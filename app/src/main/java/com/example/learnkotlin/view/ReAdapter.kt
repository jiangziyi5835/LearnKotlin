package com.example.learnkotlin.view

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

abstract class ReAdapter : RecyclerView.Adapter<BaseBindingViewHolder>() {

    private var data: ArrayList<in Any>? = null

    init {
        data = ArrayList()
    }


    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        holder.bindViewHolder(position, getItem(position))
        holder.getBinding()
    }

    open fun setDate(data: ArrayList<out Any>) {
        this.data?.clear()
        this.data?.addAll(data)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun getItem(position: Int): Any ?{
        return if (data == null) null else data!![position]
    }

    fun isContain(o:Any){
        data?.contains(o)
    }

    fun insert(o: Any,position: Int){
        data?.add(position,o)
        notifyDataSetChanged()
    }

    fun clearData(){
        data?.clear()
        notifyDataSetChanged()
    }





}