package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet
import cz.androidsample.ui.widget.element.animator.ElementLayoutAnimatorSet

/**
 * Created by cz on 2017/10/26.
 */
class ElementRelativeLayout : Element<PageLayout>(){
    var rotation=0f
    internal val elements = mutableListOf<Element<*>>()
    override fun convertToView(context: Context): View{
        val pageLayout=PageLayout(context)
        val elementLayout=ElementLayout(context)
        elements.forEach { elementLayout.addElement(it) }
        pageLayout.addElementLayout(elementLayout)
        return pageLayout
    }

    /**
     * 扩展text元素
     */
    inline fun ElementRelativeLayout.text(init:TextElement.()->Unit){
        addElement(TextElement().apply(init))
    }

    /**
     * 扩展text元素
     */
    inline fun ElementRelativeLayout.relative(init:ElementRelativeLayout.()->Unit){
        addElement(ElementRelativeLayout().apply(init))
    }

    /**
     * 扩展图片元素
     */
    inline fun ElementRelativeLayout.image(resource:Int=-1, init: ImageElement.()->Unit){
        addElement(ImageElement(resource).apply(init))
    }

    /**
     * 扩展图片元素
     */
    inline fun ElementRelativeLayout.arcView(resource:Int=-1, init: ArcElement.()->Unit){
        addElement(ArcElement(resource).apply(init))
    }

    /**
     * 横向线元素,可用做导航线
     */
    inline fun ElementRelativeLayout.hline(init: LineElement.()->Unit){
        addElement(LineElement(LineElement.HORIZONTAL).apply(init))
    }

    /**
     * 纵向线元素,可用做导航线
     */
    inline fun ElementRelativeLayout.vline(init: LineElement.()->Unit){
        addElement(LineElement(LineElement.VERTICAL).apply(init))
    }

    /**
     * 添加一个元素
     */
    fun addElement(element: Element<*>) {
        this.elements.add(element)
    }

}