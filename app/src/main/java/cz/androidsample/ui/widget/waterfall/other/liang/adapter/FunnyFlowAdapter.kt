package cz.androidsample.ui.widget.waterfall.other.liang.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Created by Administrator on 2017/12/3.
 */
abstract class FunnyFlowAdapter<out T>(private val items:List<T>) {
    fun getNum():Int = items.size
    abstract fun inflaterView(parent: ViewGroup, position:Int): View
    abstract fun bindView(view: View, position: Int)
    open fun getViewItem(position: Int) = items[position]
}