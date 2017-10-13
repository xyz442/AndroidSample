package cz.androidsample.ui.hierarchy

import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity

@ToolBar
class Hierarchy2Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy2)
        setTitle(intent.getStringExtra("title"))
    }


}
