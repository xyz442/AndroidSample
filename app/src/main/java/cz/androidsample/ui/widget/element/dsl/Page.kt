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
        val layout=ElementLayout(context).apply(init)
        pageLayout.addElementLayout(layout)
        return pageLayout
    }

    fun include(layout:ElementLayout){
        pageLayout.addElementLayout(layout)
    }

}

