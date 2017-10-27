package cz.androidsample.ui.widget.element.animator

import android.view.animation.LinearInterpolator

/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
class ElementAnimatorSet{
    private var root:Node?=null
    lateinit var current:Node
    fun nextAnimator(animator:ElementAnimator){
        ensureNode(animator)
    }

    fun withAnimator(animator:ElementAnimator){
        ensureNode(animator)
    }

    /**
     * 确认初始节点
     */
    private fun ensureNode(animator: ElementAnimator) {
        val node = Node(animator)
        if (null == root) {
            root = node
        }
        if (null == current) {
            current = node
        }
    }

    /**
     * 动画组节点
     */
    inner class Node(val animator:ElementAnimator?){
        val friend= mutableListOf<Node>()
        val child= mutableListOf<Node>()
    }
}