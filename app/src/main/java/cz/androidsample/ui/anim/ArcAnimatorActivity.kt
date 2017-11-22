package cz.androidsample.ui.anim

import android.os.Bundle
import android.text.TextUtils
import android.widget.SeekBar

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_arc_animator.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar
class ArcAnimatorActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arc_animator)
        setTitle(intent.getStringExtra("title"))

        buttonStart.onClick {
            var duration=3000L
            if(!TextUtils.isEmpty(durationEditor.text)){
                duration=durationEditor.text.toString().toLong()
            }
            targetView.setAnimatorDuration(duration)
            targetView.startArcTestAnimator()
        }
        targetView.addFilterFractions(60,90)
        seekBar1.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentText.text=getString(R.string.current_degrees_value,progress)
                targetView.setDegrees(progress*1f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)=Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?)=Unit

        })

        seekBar2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentProgressText.text=getString(R.string.current_progress_value,progress)
                targetView.setArcFraction((progress*1f/seekBar.max))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)=Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?)=Unit
        })
        seekBar3.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                currentProgressText.text=getString(R.string.current_degrees_value,progress)
                targetView.rotation=progress*1f
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)=Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?)=Unit

        })
    }
}
