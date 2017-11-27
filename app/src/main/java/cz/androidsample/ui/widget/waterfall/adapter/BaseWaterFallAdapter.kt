package cz.androidsample.ui.widget.waterfall.adapter


/**
 * Created by cz on 2017/11/26.
 */
abstract class BaseWaterFallAdapter<T>(val items:List<T>): WaterFallAdapter() {

    override fun getCount(): Int =items.size

    open fun getItem(position:Int)=items[position]
}