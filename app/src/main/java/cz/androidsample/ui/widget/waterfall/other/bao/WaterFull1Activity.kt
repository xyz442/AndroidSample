package cz.androidsample.ui.widget.waterfall.other.bao

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_bao_waterfull1.*

@ToolBar
class WaterFull1Activity : ToolBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bao_waterfull1)

        val inputStream=assets.open("txt/text1.txt")
        val items=inputStream.bufferedReader().readLines()
        items.forEach {
            val textView= TextView(this)
            textView.text = it
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15f)
            textView.gravity=Gravity.CENTER
            waterFallLayout.addView(textView)
        }
    }
}
