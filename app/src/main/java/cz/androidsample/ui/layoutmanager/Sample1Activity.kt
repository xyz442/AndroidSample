package cz.layoutmanagersample.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cz.sample.adapter.LayoutManagerAdapter
import com.cz.sample.ui.layoutmanager.SimpleLinearLayoutManager1
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar

import kotlinx.android.synthetic.main.activity_sample1.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar(R.string.sample1)
class Sample1Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample1)
        recyclerView.layoutManager= SimpleLinearLayoutManager1()
        val adapter= LayoutManagerAdapter(this,(0..30).map { "Item:$it" })
        adapter.onLogCallback { list1Text.append("$it\n") }
        recyclerView.adapter=adapter

        cleanButton.onClick { list1Text.text=null }
    }
}
