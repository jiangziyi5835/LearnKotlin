package com.example.learnkotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract


import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.learnkotlin.constract.ActivityResult
import com.example.learnkotlin.interfaces.Api
import com.example.learnkotlin.interfaces.TextcallBack
import com.example.learnkotlin.databinding.ActivityMainBinding
import com.example.learnkotlin.model.RandomUserDemo
import com.example.learnkotlin.view.BaseBindingViewHolder
import com.example.learnkotlin.view.BaseXmlModel
import com.example.learnkotlin.view.ReAdapter
import com.example.learnkotlin.xmlmodel.MainViewModel
import com.example.learnkotlin.view.TimeSelectView
import com.example.learnkotlin.xmlmodel.ItemModel
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var tvShow: TextView
    lateinit var tvText: TextView
    val TAG="Tagger"
        val activityResultContract=registerForActivityResult(ActivityResult()){
        Log.d(TAG, ": $it")
    }

    private val mainViewModel: MainViewModel =
        MainViewModel()

    companion object {
        var textcallBack: TextcallBack? = null
        fun start(context: Context, data1: String) {
            val starter = Intent(context, MainActivity::class.java)
            starter.putExtra("params1", data1)
            context.startActivity(starter)
        }
    }

    var num: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        initData()
        val intent1 = intent.getStringExtra("params1")
        var drawable: Drawable = resources.getDrawable(R.drawable.bg_seekbar_new2)
        binding.seekBar?.progressDrawable = drawable

        /*binding.tvShow.addTextChangedListener(){
            Log.d("textchang", it.toString() )
        }*/
        mainViewModel.timeHour.set(3)
        mainViewModel.timeMinute.set(30)
        mainViewModel.selectHour.set(5)
        mainViewModel.selectMinute.set(45)

        mainViewModel.seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mainViewModel.textContent.set(progress.toString())
                if (progress > 80) {
                    var drawable: Drawable = resources.getDrawable(R.drawable.bg_seekbar_new2)
                    seekBar?.progressDrawable = drawable
                } else {
                    var drawable = resources.getDrawable(R.drawable.bg_seekbar_new)
                    seekBar?.progressDrawable = drawable
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mainViewModel.textInput.set(seekBar?.progress.toString())
            }

        }
        binding.tvShow.setText("从secondactivity获取到了${intent1}")

        binding.etText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
           }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        mainViewModel.textContent.set(intent1 ?: "点击将下方选择时间更新到上部表盘")
        binding.timeSelect.setOnTimeSelectListener { deviceSeekBar -> { deviceSeekBar.selectHour } }
        binding.timeSelect.setOnTimeSelectListener(TimeSelectView.TimeSelectLitener {
            mainViewModel.timeHour.set(it.selectHour)
            mainViewModel.timeMinute.set(it.selectMinute)
        })
        mainViewModel.nameClick = View.OnClickListener {
activityResultContract.launch("akhajsfk")
//            openGallery()
//            SecondAcvtivity.start(this) {
//                updateText(it)
//            }
//gotoWifi()
//          mainViewModel.timeHour.set(mainViewModel.selectHour.get())
//           mainViewModel.timeMinute.set(mainViewModel.selectMinute.get())
//            binding.clock. setTime(mainViewModel.hour.get(),56)
//            mainViewModel.textContent.set(mainViewModel.textInput.get()?.trim())
//            num++

        }
//        mainViewModel.textInput
        var recyclerView: RecyclerView = binding.recycleview
        recyclerView.layoutManager = LinearLayoutManager(this)
        baseAdapter = object : ReAdapter() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BaseBindingViewHolder {
                return object : BaseBindingViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(this@MainActivity),
                        R.layout.item_lll,
                        parent,
                        false
                    )
                ) {
                    override fun initXmlModel(position: Int, any: Any?): BaseXmlModel {
                        var xmlModel=ItemModel()
                        if (any is HashMap<*, *>){
                            xmlModel.title.set(any["title"] as String)
                            xmlModel.contents.set(any["contents"] as String)
                        }
                        return xmlModel
                    }

                   }
            }

        }
        recyclerView.adapter=baseAdapter
        (baseAdapter as ReAdapter).setDate(listData)

        binding.mianModel = mainViewModel
//        retrofitInit()
//        retrofit.create(Api::class.java)
//        getNetData()
    }
    private fun getNetData(){
        GlobalScope.launch (Dispatchers.Main){
            var bean: RandomUserDemo? =null
            withContext(Dispatchers.IO){
//                 bean= retrofit.create(Api::class.java).Api().await()
            }
            Log.d("TAG", "getNetData: ${bean?.info?.seed}")
        }
    }

    private fun retrofitInit() {
//        retrofit=Retrofit.Builder()
//            .addConverterFactory(GsonConverterFactory.create())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .baseUrl("https://randomuser.me")
//            .build()
    }

    private var listData= ArrayList<HashMap<*,*>>()

    private fun initData() {
        for (a in 0..10){
           var hashMap=HashMap<String,String>()
            hashMap["title"]="title$a"
            hashMap["contents"]="this is a text for $a"
            listData.add(hashMap)

        }
    }

    var baseAdapter: ReAdapter? = null

    override fun onStart() {
        super.onStart()
        textcallBack = object : TextcallBack {
            override var ok: (() -> Unit)? = { ok() }
            override var fail: ((e: String?) -> Unit)? = { fail(it) }
        }
    }

    fun fail(msg: String?) {
    }

    fun ok() {

    }

    fun gotoWifi() {
        try {
            val intent = Intent()
            intent.component =
                ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            this.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            this.startActivity(intent)
        }
    }

    fun updateText(text: String) {
        if (text.isNullOrEmpty()) {
            SecondAcvtivity.funCallBack?.fail("此处取值不能为空")
            return
        }
        SecondAcvtivity.funCallBack?.ok()
//        SecondAcvtivity.callback?.ok?.invoke()
        mainViewModel.textContent.set(text)
    }



}
