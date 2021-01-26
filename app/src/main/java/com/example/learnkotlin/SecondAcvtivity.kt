package com.example.learnkotlin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.learnkotlin.constract.ActivityResult
import com.example.learnkotlin.interfaces.FunCallBack
import com.example.learnkotlin.interfaces.TextcallBack
import com.example.learnkotlin.databinding.ActivitySecondBinding
import com.example.learnkotlin.util.ImageUtils
import com.example.learnkotlin.util.ImageUtils.Companion.getUri
import com.example.learnkotlin.util.PermissionUtils
import com.example.learnkotlin.view.DeviceSeekBar
import com.example.learnkotlin.xmlmodel.SecondViewModel
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class SecondAcvtivity : AppCompatActivity(), View.OnClickListener {
    var activityResult = registerForActivityResult(ActivityResult()) {

    }

    companion object {
        var callback: TextcallBack? = null
        var funCallBack: FunCallBack? = null
        private var updataText: ((text: String) -> Unit)? = null
        fun start(context: Context, done: ((text: String) -> Unit)) {
            val intent = Intent(context, SecondAcvtivity::class.java)
            context.startActivity(intent)
            updataText = done
        }

    }

    private val secondViewModel: SecondViewModel by lazy { SecondViewModel() }
    private val REQUEST_CODE_TAKE_PHOTO = 0x100
    private val RRQUEST_CODE_CHOOSE_PHOTO = 0x200
    private val CODE_RESULT = 12121;
    private var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initXmlModel    ()
//        var b=getDrawable(R.drawable.bg_seekbar_new2)
        var a = ContextCompat.getDrawable(this, R.drawable.bg_seekbar_new2)
    }

    fun initXmlModel() {
        val binding: ActivitySecondBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_second)
        ( binding.etStart as EditText)
        secondViewModel.apply {
//            secondViewModel.leftStatusDrawable.set(ContextCompat.getDrawable(this@SecondAcvtivity,R.drawable.ic_device_awning_open))
            hasPoint.set(false)
            progress.set(5)
            deviceMode.set(0)
            name.set("take Photo")
            choosePhoto.set("choose Photo")
            statusMode.set(1)

            chooseClick = View.OnClickListener {
                //                callback?.fail?.invoke("放心没事，我就路过吐个泡泡")
                var intent = Intent()
                intent.putExtra("name", "这是个返回的文本Second")
                setResult(CODE_RESULT, intent)
                finish()
            }
            jumpClicklistener = View.OnClickListener { DoJump() }
            onCenterStatusClick = View.OnClickListener { centerStatusListener(it) }
            onLeftStatusClick = View.OnClickListener { leftStatusListener(it) }
            onRightStatusClick = View.OnClickListener { rightStatusListener(it) }
            closeListener = View.OnClickListener { closeMethod(it) }
            OpenListener = View.OnClickListener { openMethod(it) }
            pauseListener = View.OnClickListener { pauseMethod(it) }
            onSeekBarChange = object : DeviceSeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: DeviceSeekBar?) {
//                Log.d("deviceTag", "the progress is changed to${seekBar?.progress}")
                }

                override fun onStartTrackingTouch(seekBar: DeviceSeekBar?) {
//                Log.d("deviceTag", "the progress is start to${seekBar?.progress}")
                }

                override fun onStopTrackingTouch(seekBar: DeviceSeekBar?) {
                    Log.d("deviceTag", "the progress is stoptouch${seekBar?.progress}")
                }
            }
        }
        secondViewModel.leftStatusDrawable.set(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_device_awning_open
            )
        )
        secondViewModel.rightStatusDrawable.set(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_device_awning_open
            )
        )
        binding.secondmodel = secondViewModel

    }


    override fun onStart() {
        super.onStart()
        callback = object : TextcallBack {

            override var ok: (() -> Unit)? = { ok() }
            override var fail: ((e: String?) -> Unit)? = { failure(it) }
        }
        funCallBack = object : FunCallBack {
            override fun ok() {
                finish()
            }

            override fun fail(msg: String) {
                showText(msg)
            }

        }
    }

    fun ok() {
        finish()
    }

    fun failure(e: String?) {
        showText(e)
    }


    fun showText(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun centerStatusListener(it: View?) {
        if (it is DeviceSeekBar) {
            var deviceSeekBar = it as DeviceSeekBar
            Log.d("deviceTag", "center ${deviceSeekBar.progress}")
        }

    }

    private fun leftStatusListener(it: View?) {
        if (it is DeviceSeekBar) {
            var deviceSeekBar = it as DeviceSeekBar
            Log.d("deviceTag", "left ${deviceSeekBar.progress}")
        }

    }

    private fun rightStatusListener(it: View?) {
        if (it is DeviceSeekBar) {
            var deviceSeekBar = it as DeviceSeekBar
            Log.d("deviceTag", "right ${deviceSeekBar.progress}")
        }

    }

    private fun openMethod(it: View) {

        Log.d("deviceTag", "open")
        val deviceSeekBar = it as DeviceSeekBar
        secondViewModel.progress.set(1)
    }

    private fun closeMethod(it: View) {
        Log.d("deviceTag", "close")
        val deviceSeekBar = it as DeviceSeekBar
        secondViewModel.progress.set(0)
    }

    private fun pauseMethod(it: View) {

        Log.d("deviceTag", "pause")
        val deviceSeekBar = it as DeviceSeekBar
        secondViewModel.progress.set(2)
    }


    private fun DoJump() {
//        if (secondViewModel.input.get().isNullOrEmpty()) {
//            funCallBack?.fail("取值不能为空")
////            callback?.fail?.invoke("取值不能为空")
//            return
//        }
        if (updataText != null)
            secondViewModel.input.get()?.let { updataText?.invoke(it) }
//        MainActivity.start(this,"this message is from SecondAvtivity")
//        openGallery()
//        takePhoto()
    }


    override fun onClick(v: View?) {

    }

    private fun takePhoto() {
        var file: File? = null
        if (EasyPermissions.hasPermissions(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) && EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                file = File(
                    ImageUtils.getPrivateFilePath(this, Environment.DIRECTORY_PICTURES) +
                            File.separator + System.currentTimeMillis() + ".png"
                )
            } else {
                file = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    ).toString() + File.separator + System.currentTimeMillis() + ".png"
                )
            }
        } else {
            PermissionUtils.requestPermissions(
                this,
                102,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                null
            )

        }
        if (file != null) {
            imageUri = getUri(file)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RRQUEST_CODE_CHOOSE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                val uri = data?.data ?: ""
                secondViewModel.imageUrl.set(uri)

//               secondViewModel .image.set()
            } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
                secondViewModel.imageUrl.set(imageUri)
                Log.d("ImageTag", imageUri.toString())
            }
        }

    }


}