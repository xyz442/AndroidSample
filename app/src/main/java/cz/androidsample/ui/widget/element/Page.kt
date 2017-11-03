package cz.androidsample.ui.widget.element

import android.content.Context
import android.view.View
import cz.androidsample.ui.widget.element.animator.ElementAnimator

/**
 * Created by cz on 2017/10/26.
 */
open class Page(val context: Context) {
    companion object {
        var pageIndex = 0
    }
    internal val pageLayout = PageLayout(context)
    private var pageScrolled: ((Int, Float, Int) -> Unit)? = null
    private var pageSelected: ((Int) -> Unit)? = null
    //当前分页位置
    val index: Int

    init {
        //分页自增
        index = pageIndex++
    }

    fun layout(init: ElementLayout.() -> Unit): Page {
        val elementLayout = ElementLayout(context).apply(init)
        //初始化
        pageLayout.addElementLayout(elementLayout)
        return this
    }

    /**
     * 根据id 查找一个元素
     */
    fun view(id: String): View? = pageLayout.findElement(id)

    /**
     * 分页滑动偏移事件
     */
    fun onPageScrolled(pageScrolled: (Int, Float, Int) -> Unit) {
        this.pageScrolled = pageScrolled
    }

    /**
     * 分页选中
     */
    fun onPageSelected(pageSelected: (Int) -> Unit) {
        this.pageSelected = pageSelected
    }

    /**
     * 分页滚动
     */
    internal fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int,isSelectItem:Boolean) {
        //回调子元素
        pageLayout.onPageScrolled(position,positionOffset,positionOffsetPixels,isSelectItem)
        if(isSelectItem){
            pageLayout.alpha = 1f-positionOffset
        } else {
            pageLayout.alpha = positionOffset
        }
    }

    /**
     * 分页选中
     */
    internal fun onPageSelected(position: Int) {
        pageLayout.onPageSelected(position)
    }
}

