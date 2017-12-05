package cz.androidsample.ui.widget.waterfall.other.hui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_hui_flow_layout1.*
import org.jetbrains.anko.backgroundColor


@ToolBar
class TestFlowLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hui_flow_layout1)
        setTitle(intent.getStringExtra("title"))
        setFlowContent()
    }

    private fun setFlowContent() {
//        (0 until 20).map {
//                    TextView(this).apply {
//                        text = "这是测试$it"
//                        gravity = Gravity.CENTER
//                        backgroundColor = ContextCompat.getColor(context,R.color.colorAccent)
//                        layoutParams = if (it == 1){
//                            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , 300 + it * 600)
//                        }else{
//                            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , 300 + it * 50)
//                        }
//                    }
//                }
//                .forEach { flowLayout.addView(it) }
        val inputStream=assets.open("txt/text1.txt")
        val items=inputStream.bufferedReader().readLines()
        items.forEach {
            val textView= TextView(this)
            textView.text = it
            textView.backgroundColor = ContextCompat.getColor(this,R.color.colorAccent)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15f)
            textView.gravity=Gravity.CENTER
            flowLayout.addView(textView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }
}
