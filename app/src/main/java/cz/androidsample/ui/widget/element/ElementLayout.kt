package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.content.Context
import android.util.TypedValue
import android.view.View
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet
import cz.androidsample.ui.widget.element.animator.ElementLayoutAnimatorSet

/**
 * Created by cz on 2017/10/26.
 */
class ElementLayout(val context:Context){
    internal val elements = mutableListOf<Element<*>>()
    var animator:(ElementLayoutAnimatorSet.()->Unit)?=null
    /**
     * 添加一个元素
     */
    fun addElement(element:Element<*>){
        this.elements.add(element)
    }

    /**
     * 根据id 查找一个元素
     */
    fun findElement(id:String):Element<*>?{
        return elements.find { it.id==id }
    }

    /**
     * 转换动画
     */
    fun convertAnimator(target: View):Animator?{
        val animator=animator?:return null
        return ElementLayoutAnimatorSet(this).apply(animator)
    }


    fun dp(value:Float):Int=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,context.resources.displayMetrics).toInt()
    fun dp(value:Int):Int=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value.toFloat(),context.resources.displayMetrics).toInt()

    fun sp(value:Float)=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value,context.resources.displayMetrics)
    fun sp(value:Int)=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,value.toFloat(),context.resources.displayMetrics)
}