package com.example.learnkotlin.constract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.example.learnkotlin.SecondAcvtivity

class ActivityResult: ActivityResultContract<String, String>() {
    val TAG ="Tagger"
    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, SecondAcvtivity::class.java).putExtra("name", input)

    }

    override fun parseResult(resultCode: Int, intent: Intent?): String {
        var result=intent?.getStringExtra("name")
        Log.d(TAG, "parseResult:$result ")
        return result?:"虽然我回来了，但是我啥都没有"
    }
}