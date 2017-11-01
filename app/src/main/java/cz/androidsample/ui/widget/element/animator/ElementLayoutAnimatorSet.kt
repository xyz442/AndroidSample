package cz.androidsample.ui.widget.element.animator

import android.animation.AnimatorSet
import android.view.View
import cz.androidsample.ui.widget.element.ElementLayout
import cz.androidsample.ui.widget.element.PageLayout

/**
 * Created by cz on 2017/10/30.
 * 元素组的动画控制,主要控制每个控件的动画执行顺序,以及延持回调等
 */
class ElementLayoutAnimatorSet(val layout: ElementLayout):ElementAnimatorSet() {
    internal var root=Animator.Node(null)
    /**
     * 获取一个元素动画对象
     * @param elementId:元素id
     */
    fun play(elementId:String):Builder{
        val animator = findElementAnimator(elementId)
        return Builder(animator)
    }

    /**
     * 转换元素节点组
     */
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator=AnimatorSet()
        forEachAnimatorNode(parent,target,animator,root)
        return animator
    }


    override fun getNodeAnimator(node: Node, parent: PageLayout, target: View): android.animation.Animator? {
        var itemAnimator:android.animation.Animator?=null
        //初始化动画信息
        val animator = node.animator
        if (null != animator&&null!=animator.elementId) {
            val target = parent.findElement(animator.elementId)
            //转换动画
            itemAnimator = node.animator.convert(parent, target)
        }
        return itemAnimator
    }


    private fun findElementAnimator(elementId: String): ElementAnimatorSet {
        val element = layout.findElement(elementId)
        return element?.animator?:throw NullPointerException("Element animator is null!")
    }

    inner class Builder(animator:Animator){
        //从根节点取此节点
        var current=Node(animator)
        init {
            //添加到根节点
            root.child.add(current)
        }
        //从根节点取此节点
        /**
         * 记录到当前节点下
         */
        fun after(elementId:String):Builder{
            //从集内移除
            val elementAnimator = findElementAnimator(elementId)
            val node=Animator.Node(elementAnimator)
            current.child.add(node)
            current=node
            return this
        }

        /**
         * 与当前节点同级
         */
        fun with(elementId:String):Builder{
            val elementAnimator = findElementAnimator(elementId)
            val node=Animator.Node(elementAnimator)
            current.friend.add(node)
            current=node
            return this
        }

        fun delay(time:Long):Builder{
            current.animator?.delay=time
            return this
        }

        fun onAnimatorEnd(animatorEnd:()->Unit){
            current.animatorEnd=animatorEnd
        }
    }
}