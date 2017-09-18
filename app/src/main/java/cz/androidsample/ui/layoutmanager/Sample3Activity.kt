package cz.layoutmanagersample.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.cz.sample.adapter.LayoutManagerAdapter
import com.cz.sample.ui.layoutmanager.SimpleLinearLayoutManager3
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import kotlinx.android.synthetic.main.activity_sample3.*
import org.jetbrains.anko.sdk25.coroutines.onClick

@ToolBar(R.string.sample3)
class Sample3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample3)
        recyclerView.layoutManager= SimpleLinearLayoutManager3()
        val adapter= LayoutManagerAdapter(this,(0..30).map { "Item:$it" })
        adapter.onLogCallback { list3Text.append("$it\n") }
        recyclerView.adapter= adapter

        cleanButton.onClick { list3Text.text=null }
    }
}
