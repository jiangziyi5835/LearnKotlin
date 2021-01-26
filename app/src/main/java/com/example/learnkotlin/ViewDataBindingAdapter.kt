package com.example.learnkotlin

import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.learnkotlin.view.Clock
import com.example.learnkotlin.view.ClockByKT
import com.example.learnkotlin.view.DeviceSeekBar
import com.example.learnkotlin.view.TimeSelectView


@BindingAdapter("onSeekBarChanged")
fun setSeekBarChanged(seekBar: SeekBar,onSeekBarChangeListener: SeekBar.OnSeekBarChangeListener){
    seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener)
}

@BindingAdapter("imageUrl")
fun setImage(imageView: ImageView,imageUrl:Any?){
    Glide.with(imageView.context).load(imageUrl).into(imageView)
}

@BindingAdapter("hour","minute")
fun Clock(clock: Clock, hour:Int, minute:Int){
    clock.setTime(hour,minute)
}

@BindingAdapter("hour","minute")
fun setKtClockTime(clock: ClockByKT, hour: Int, minute: Int){
    clock.setTime(hour,minute)
}
@BindingAdapter("selectHour","selectMinute")
fun timeSelectView(timeSelectView: TimeSelectView, selectHour: Int, selectMinutte: Int){
    timeSelectView.selectHour=selectHour
    timeSelectView.selectMinute=selectMinutte

}
@BindingAdapter(
    "progress", "onSeekBarChanged", "deviceMode", "offline", "hasPoint", "hidePause",
    "onOpenListener", "onCloseListener", "onPauseListener",
    "onLeftStatusListener", "onRightStatusListener", "onCenterStatusListener",
    "secondProgress", "isMoveAll", "isControl", requireAll = false
)
fun deviceSeekBar(
    seekBar: DeviceSeekBar, progress: Int,
    onSeekBarChanged: DeviceSeekBar.OnSeekBarChangeListener?, deviceMode: Int?,
    offline: Boolean?, hasPoint: Boolean?, hidePause: Boolean?,
    onOpenListener: View.OnClickListener?, onCloseListener: View.OnClickListener?, onPauseListener: View.OnClickListener?,
    onLeftStatusListener: View.OnClickListener?, onRightStatusListener: View.OnClickListener?, onCenterStatusListener: View.OnClickListener?,
    secondProgress: Int, isMoveAll: Boolean?,
    isControl: Boolean?
) {
    if (deviceMode != null) {
        seekBar.deviceMode = deviceMode
    }
    if (false == hasPoint) {
        seekBar.isHasPoint = false
        if (true == hidePause) {
            seekBar.hidePause()
        }
        seekBar.progress = progress
        seekBar.onOpenListener = onOpenListener
        seekBar.onCloseListener = onCloseListener
        seekBar.onPauseListener = onPauseListener
    } else {
        seekBar.isHasPoint = true
        seekBar.progress = progress
        seekBar.secondProgress = secondProgress
        seekBar.isMoveAll = isMoveAll ?: false
        seekBar.onProgressChangedListener = onSeekBarChanged
        seekBar.onLeftStatusListener = onLeftStatusListener
        seekBar.onRightStatusListener = onRightStatusListener
        seekBar.onCenterStatusListener = onCenterStatusListener
    }
    seekBar.isOffLine = offline ?: false
    seekBar.setControl(isControl ?: true)
}

