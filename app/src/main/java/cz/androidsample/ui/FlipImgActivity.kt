package cz.androidsample.ui

import android.os.Bundle
import android.widget.SeekBar
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_flip_img.*


@ToolBar
class FlipImgActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flip_img)
        setTitle(intent.getStringExtra("title"))

        seekBar1.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                flipView.setRotateX(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        seekBar2.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                flipView.setRotateY(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }
}
