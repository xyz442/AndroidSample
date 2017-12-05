package cz.androidsample.ui.widget.waterfall.other.tao

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity

@ToolBar
class TaoFlowLayout1Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tao_flow1)
        setTitle(intent.getStringExtra("title"))
    }
}
