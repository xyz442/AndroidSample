package cz.androidsample.ui.widget.element.animator

import android.animation.ValueAnimator
import android.view.View
import cz.androidsample.ui.widget.element.PageLayout

/**
 * Created by cz on 2017/10/31.
 */
abstract class Animator(val repeatCount:Int=0,val repeatMode:Int=ValueAnimator.REVERSE){
    //所属元素id
    var elementId:String?=null
    //延持时间
    var delay:Long=0
    //动画结束事件
    internal var animatorEnd:(()->Unit)?=null

    /**
     * 转换为元素动画
     */
    abstract fun convert(parent: PageLayout, target: View):android.animation.Animator

    /**
     * 动画结束
     */
    fun onAnimatorEnd(animatorEnd:()->Unit){
        this.animatorEnd=animatorEnd
    }

    /**
     * 动画组节点
     */
    class Node(val animator:Animator?,var parent:Node?=null){
        var animatorEnd:(()->Unit)?=null
        val child= mutableListOf<Node>()
    }
}

