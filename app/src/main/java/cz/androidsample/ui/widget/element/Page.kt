package cz.androidsample.ui.widget.element

import android.content.Context
import android.view.View

/**
 * Created by cz on 2017/10/26.
 */
open class Page(val context: Context) {
    companion object {
        var pageIndex = 0
    }
    internal val layout = PageLayout(context)
    internal var pageScrolled: ((View,Int, Float, Int) -> Unit)? = null
    internal var pageSelected: ((View,Int) -> Unit)? = null
    //当前分页位置
    val index: Int

    init {
        //分页自增
        index = pageIndex++
    }

    fun layout(init: ElementLayout.() -> Unit): Page {
        val elementLayout = ElementLayout(context).apply(init)
        //初始化
        layout.addElementLayout(elementLayout)
        return this
    }

    /**
     * 根据id 查找一个元素
     */
    fun view(id: String): View? = layout.findElement(id)

    /**
     * 分页滚动
     */
    internal fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int,current:Boolean) {
        //回调子元素
        layout.onPageScrolled(position,positionOffset,positionOffsetPixels,current)
        //回调当前页事件
        pageScrolled?.invoke(layout,position,positionOffset,positionOffsetPixels)
    }

    /**
     * 分页选中
     */
    internal fun onPageSelected(position: Int) {
        layout.onPageSelected(position)
        pageSelected?.invoke(layout,position)
    }
}

