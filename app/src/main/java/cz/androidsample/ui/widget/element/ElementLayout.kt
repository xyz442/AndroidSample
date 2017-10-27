package cz.androidsample.ui.widget.element

import android.content.Context
import android.util.TypedValue

/**
 * Created by cz on 2017/10/26.
 */
class ElementLayout(val context:Context){
    val elements = mutableListOf<Element<*>>()
    /**
     * 添加一个元素
     */
    fun addElement(element:Element<*>){
        elements.add(element)
    }

    fun dp(value:Float):Int=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.resources.displayMetrics).toInt()
    fun sp(value:Float)=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value,context.resources.displayMetrics)
}