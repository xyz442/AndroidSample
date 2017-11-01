package cz.androidsample.ui.widget.guide

import android.view.View
import cz.androidsample.ui.widget.element.Page

/**
 * Created by cz on 2017/11/1.
 * 引导数据适配器
 */
abstract class GuidePagerAdapter(val items:List<Page>){
    /**
     * 获得分页个数
     */
    val count:Int
        get() = items.size
    /**
     * 获得分页
     */
    abstract fun getPage(parent: View,position:Int): Page
}