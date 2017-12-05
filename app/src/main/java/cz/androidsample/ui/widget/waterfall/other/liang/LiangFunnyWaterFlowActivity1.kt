package cz.androidsample.ui.widget.waterfall.other.liang

import android.app.Activity
import android.os.Bundle
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.widget.waterfall.other.liang.adapter.TextFunnyFlowAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_liang_water_flow_layout1.*

/**
 * Created by Administrator on 2017/12/1.
 */
@ToolBar
class LiangFunnyWaterFlowActivity1 : ToolBarActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liang_water_flow_layout1)
        setTitle(intent.getStringExtra("title"))
        val inputStream=assets.open("txt/text1.txt")
        val items=inputStream.bufferedReader().readLines()
        funnyLayout.setAdapter(TextFunnyFlowAdapter(items))
    }
}