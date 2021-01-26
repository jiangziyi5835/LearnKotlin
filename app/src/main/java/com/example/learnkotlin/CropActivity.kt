package com.example.learnkotlin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.learnkotlin.databinding.ActivityCropBinding
import com.example.learnkotlin.util.Constants
import com.example.learnkotlin.xmlmodel.CropViewModel
import java.net.URI
import java.net.URL

class CropActivity : AppCompatActivity() {
    companion object {
        const val RESULTCODE = 0x010
        fun start(activity: Activity, uri: URI) {
            val intent = Intent(activity, CropActivity::class.java)
            intent.putExtra("uri", uri)
            activity.startActivityForResult(intent, RESULTCODE)
        }
    }
//
//    private val scaneResult: BroadcastReceiver by lazy {
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                var action = intent?.action
//                if (action != null && action == (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//                    stopScan()
//                    initRecyclerViewData()
//                }
//            }
//        }
//    }
    val cropViewModel: CropViewModel = CropViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initXmlModel()
    }

    private fun initXmlModel() {
        val databinding =
            DataBindingUtil.setContentView<ActivityCropBinding>(this, R.layout.activity_crop)
        cropViewModel.apply {
            cropViewModel.finishClick = View.OnClickListener { doFinish() }
        }
        databinding.xmlmodel = cropViewModel
    }

    private fun doFinish() {


    }
}