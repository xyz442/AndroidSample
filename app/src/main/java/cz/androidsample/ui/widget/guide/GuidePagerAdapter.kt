package cz.androidsample.ui.widget.guide

import android.view.View
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.element.PageLayout

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
     * 创建分页
     */
    abstract fun onCreatePage(page:Page, layout: PageLayout, position:Int)

    /**
     * 开始执行分页动画
     */
    open fun onStartPageAnimator(page:Page, layout:PageLayout,position: Int)=Unit
    /**
     * 获得分页
     */
    abstract fun getPage(parent: View,position:Int): Page
}