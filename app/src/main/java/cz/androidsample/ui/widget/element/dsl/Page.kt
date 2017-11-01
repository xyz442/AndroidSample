package cz.androidsample.ui.widget.element.dsl

import android.content.Context
import cz.androidsample.ui.widget.element.ElementLayout
import cz.androidsample.ui.widget.element.PageLayout

/**
 * Created by cz on 2017/10/26.
 */
class Page(val context: Context){
    private val pageLayout= PageLayout(context)

    fun layout(init: ElementLayout.()->Unit):PageLayout{
        val elementLayout = ElementLayout(context).apply(init)
        //初始化
        pageLayout.addElementLayout(elementLayout)
        return pageLayout
    }

    /**
     * 分页滑动偏移事件
     */
    fun onPageOffset(){

    }

    /**
     * 分页选中
     */
    fun onPageSelected(position:Int){

    }

}

