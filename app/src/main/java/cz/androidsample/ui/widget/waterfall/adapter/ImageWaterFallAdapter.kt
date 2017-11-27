package cz.androidsample.ui.widget.waterfall.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.androidsample.R
import cz.androidsample.imageloader.WrapperView

/**
 * Created by cz on 2017/11/27.
 */
class ImageWaterFallAdapter(val context: Context, items: List<String>) : BaseWaterFallAdapter<String>(items) {
    private val layoutInflater=LayoutInflater.from(context)

    override fun getView(parent: ViewGroup, position: Int): View {
        return layoutInflater.inflate(R.layout.image_item,parent,false)
    }

    override fun bindView(view: View, position: Int) {
        val wrapperView=view as WrapperView
        val url=getItem(position)
//        wrapperView.display(url)
        wrapperView.setImageURI(Uri.parse(url))
    }

}