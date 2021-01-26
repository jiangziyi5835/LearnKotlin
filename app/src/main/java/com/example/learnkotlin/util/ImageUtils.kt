package com.example.learnkotlin.util

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import pub.devrel.easypermissions.EasyPermissions
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class ImageUtils {
    /**
     * 获取Uri
     */
    companion object {

        fun Context.getUri(file: File?): Uri? {
            if (file == null) return null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
                Log.e("相机权限", "系统版本大于7.0")
                return FileProvider.getUriForFile(this, "$packageName.provider", file)
            }
            //7.0以下使用这种方式创建一个Uri
            return Uri.fromFile(file)
        }

        fun saveImage(context: Context, file: File?) {
            val path = file?.absolutePath
            if (EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            ) {

            }
        }

        /**
         * 保存图片
         * @param context
         * @param bitmap
         * @param folderName
         *
         */
        private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
                values.put(MediaStore.Images.Media.IS_PENDING, true)
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                val uri: Uri? = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                if (uri != null) {
                    saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    context.contentResolver.update(uri, values, null, null)
                }
            } else {
                val directory = File(
                    Environment.getExternalStorageDirectory().toString() + separator + folderName
                )
                // getExternalStorageDirectory is deprecated in API 29

                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val fileName = System.currentTimeMillis().toString() + ".png"
                val file = File(directory, fileName)
                saveImageToStream(bitmap, FileOutputStream(file))
                if (file.absolutePath != null) {
                    val values = contentValues()
                    values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                    // .DATA is deprecated in API 29
                    context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )
                }
            }
        }

        private fun contentValues(): ContentValues {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            return values
        }

        private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
            if (outputStream != null) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


        @JvmStatic
        fun getPrivateFilePath(
            context: Context,
            url: String
        ): String? {
            val dir = context.getExternalFilesDir(url)
            return if ("mounted" == Environment.getExternalStorageState() && dir != null) dir.absolutePath else context.filesDir
                .toString() + File.separator + url
        }
    }

    /**
     *
    load  传入原图
    filter	设置开启压缩条件
    ignoreBy	不压缩的阈值，单位为K
    setFocusAlpha	设置是否保留透明通道
    setTargetDir	缓存压缩图片路径
    setCompressListener	压缩回调接口
    setRenameListener	压缩前重命名接口
     */
    fun lubanCompress(photo: Uri, context: Context) {
        Luban.with(context).load(photo).ignoreBy(150)
            .setCompressListener(object : OnCompressListener {
                override fun onSuccess(file: File?) {

                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                }

                override fun onStart() {

                }

            })

    }


}