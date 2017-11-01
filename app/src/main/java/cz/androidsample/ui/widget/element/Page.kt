package cz.androidsample.ui.widget.element

import android.content.Context
import android.view.View

/**
 * Created by cz on 2017/10/26.
 */
open class Page(val context: Context){
    internal val pageLayout= PageLayout(context)
    private var pageScrolled:((Int,Float,Int)->Unit)?=null
    private var pageSelected:((Int)->Unit)?=null
    fun layout(init: ElementLayout.()->Unit): Page {
        val elementLayout = ElementLayout(context).apply(init)
        //初始化
        pageLayout.addElementLayout(elementLayout)
        return this
    }

    /**
     * 根据id 查找一个元素
     */
    fun view(id:String): View?=pageLayout.findElement(id)

    /**
     * 分页滑动偏移事件
     */
    fun onPageScrolled(pageScrolled:(Int,Float,Int)->Unit){
        this.pageScrolled=pageScrolled
    }

    /**
     * 分页选中
     */
    fun onPageSelected(pageSelected:(Int)->Unit){
        this.pageSelected=pageSelected
    }

}

