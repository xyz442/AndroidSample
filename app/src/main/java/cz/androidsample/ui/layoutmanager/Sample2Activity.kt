package cz.layoutmanagersample.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cz.sample.adapter.LayoutManagerAdapter
import com.cz.sample.ui.layoutmanager.SimpleLinearLayoutManager2
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import kotlinx.android.synthetic.main.activity_sample2.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar(R.string.sample2)
class Sample2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample2)
        recyclerView.layoutManager= SimpleLinearLayoutManager2()
        val adapter= LayoutManagerAdapter(this,(0..30).map { "Item:$it" })
        adapter.onLogCallback { list2Text.append("$it\n") }
        recyclerView.adapter= adapter

        cleanButton.onClick { list2Text.text=null }
    }
}
