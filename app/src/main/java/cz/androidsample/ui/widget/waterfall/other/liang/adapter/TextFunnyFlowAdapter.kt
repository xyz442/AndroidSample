package cz.androidsample.ui.widget.waterfall.other.liang.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.androidsample.R
import org.jetbrains.anko.find

/**
 * Created by Administrator on 2017/12/2.
 */
class TextFunnyFlowAdapter(items: List<String>) : FunnyFlowAdapter<String>(items) {
    override fun inflaterView(parent: ViewGroup, position: Int): View {
        return LayoutInflater.from(parent.context).inflate(R.layout.child_text_flow_layout, parent, false)
    }

    override fun bindView(view: View, position: Int) {
        val showText = view.find<TextView>(R.id.adapterText)
        showText.text = getViewItem(position)
    }
}