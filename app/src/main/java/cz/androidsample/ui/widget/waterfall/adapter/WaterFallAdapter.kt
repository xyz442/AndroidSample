package cz.androidsample.ui.widget.waterfall.adapter

import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/11/26.
 * 瀑布流数据适配器
 */
abstract class WaterFallAdapter{
    abstract fun getCount():Int

    /**
     * 获取数据
     */
    abstract fun getView(parent: ViewGroup, position:Int): View

    /**
     * 绑定数据
     */
    abstract fun bindView(view: View, position: Int)
}