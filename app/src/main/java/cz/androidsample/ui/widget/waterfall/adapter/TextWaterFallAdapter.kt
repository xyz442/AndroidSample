package cz.androidsample.ui.widget.waterfall.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.androidsample.R

/**
 * Created by cz on 2017/11/27.
 */
class TextWaterFallAdapter(context:Context,items: List<String>) : BaseWaterFallAdapter<String>(items) {
    private val layoutInflater= LayoutInflater.from(context)
    override fun getView(parent: ViewGroup, position: Int): View {
        return layoutInflater.inflate(R.layout.text_item,parent,false)
    }

    override fun bindView(view: View, position: Int) {
        val item=getItem(position)
        val textView=view as TextView
        textView.text=item
    }

}