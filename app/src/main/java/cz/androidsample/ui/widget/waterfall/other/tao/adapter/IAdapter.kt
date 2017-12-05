package cz.androidsample.ui.widget.waterfall.other.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Created by woodys on 2017/12/1.
 */
interface IAdapter<E> {
    val items: List<E>
    fun getItem(position: Int): E
    fun getCount(): Int
    fun getView(parent: ViewGroup, position: Int): View
    fun bindView(view: View, position: Int)
}