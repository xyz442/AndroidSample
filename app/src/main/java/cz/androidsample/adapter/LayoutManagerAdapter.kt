package com.cz.sample.adapter

import android.content.Context
import android.support.annotation.ArrayRes
import android.support.annotation.LayoutRes
import android.view.ViewGroup
import android.widget.TextView
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.R


import java.util.Arrays

/**
 * Created by cz on 16/1/23.
 */
class LayoutManagerAdapter<E>(context: Context, @param:LayoutRes private val layout: Int, items: List<E>) : BaseViewAdapter<E>(context, items) {

    companion object {
        fun createFromResource(context: Context, @ArrayRes res: Int): LayoutManagerAdapter<*> {
            return LayoutManagerAdapter(context, context.resources.getStringArray(res))
        }
    }
    private var callback:((String)->Unit)?=null

    constructor(context: Context, items: Array<E>) : this(context, R.layout.simple_text_item, Arrays.asList(*items)) {}

    constructor(context: Context, @LayoutRes layout: Int, items: Array<E>) : this(context, layout, Arrays.asList(*items)) {}

    constructor(context: Context, items: List<E>) : this(context, R.layout.simple_text_item, items) {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        callback?.invoke("onCreateViewHolder")
        return BaseViewHolder(inflateView(parent, layout))
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        callback?.invoke("onBindViewHolder:" + position)
        val textView = holder.itemView as TextView
        val item = getItem(position)
        if (null != item) {
            textView.text = item.toString()
        }
    }

    override fun onViewRecycled(holder: BaseViewHolder) {
        super.onViewRecycled(holder)
        callback?.invoke("onViewRecycled:" + holder.adapterPosition)
    }

    fun onLogCallback(callback:(String)->Unit){
        this.callback=callback
    }

}
