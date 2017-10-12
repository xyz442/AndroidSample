package cz.androidsample.ui.hierarchy

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity

@ToolBar
class HierarchyActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hierarchy)
        setTitle(intent.getStringExtra("title"))

    }
}
