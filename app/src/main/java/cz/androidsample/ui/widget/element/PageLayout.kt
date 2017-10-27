package cz.androidsample.ui.widget.element

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet

/**
 * Created by cz on 2017/10/27.
 * 分页操作排版布局
 */
class PageLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr){
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)

    /**
     * 添加元素
     */
    fun addElementLayout(layout: ElementLayout) {
        //将元素属性映射为控件
        layout.elements.forEach { addView(it.getView(context)) }
        requestLayout()
    }
}