package cz.androidsample.ui.hierarchy

import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity

@ToolBar
class Hierarchy1Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy1)
        setTitle(intent.getStringExtra("title"))

    }
}
