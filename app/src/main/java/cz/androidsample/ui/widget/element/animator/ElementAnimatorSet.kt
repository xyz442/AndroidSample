package cz.androidsample.ui.widget.element.animator

import android.animation.AnimatorSet
import android.view.View
import cz.androidsample.ui.widget.element.PageLayout


/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
open class ElementAnimatorSet:Animator(){
    //一个子元素根节点
    private var root = Node(null)

    /**
     * 直接操作的动画元素
     */
    fun addAnimator(animator:ElementAnimator){
        root.child.add(Node(animator))
    }

    /**
     * 转换元素节点组
     */
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator= AnimatorSet()
        forEachAnimatorNode(parent,target,animator,root)
        return animator
    }

    /**
     * 遍历动画组节点
     */
    protected fun forEachAnimatorNode(parent: PageLayout,target: View,
                                    animatorSet:AnimatorSet,node:Animator.Node){
        //上一次执行节点
        var lastFriendAnimator:android.animation.Animator?=null
        //遍历朋友节点
        node.friend.forEach {
            val itemAnimator = getNodeAnimator(it, parent, target)
            animatorSet.playTogether(itemAnimator)
            lastFriendAnimator=itemAnimator
            //递归遍历所有节点
            forEachAnimatorNode(parent,target,animatorSet,it)
        }
        //遍历子节点,每个孩子节点,都要等当前节点执行完再执行
        var lastAnimator:android.animation.Animator?=null
        if(!node.child.isEmpty()){
            val childAnimatorSet=AnimatorSet()
            node.child.forEach {
                val itemAnimator=getNodeAnimator(it, parent, target)
                if(null==lastFriendAnimator){
                    animatorSet.playTogether(itemAnimator)
                } else {
                    animatorSet.play(lastFriendAnimator).before(itemAnimator)
                }
                lastAnimator=itemAnimator
                //遍历子节点
                forEachAnimatorNode(parent,target,childAnimatorSet,it)
            }
            //编排子节点执行顺序
            animatorSet.play(lastAnimator).before(childAnimatorSet)
        }
    }

    /**
     * 根据节点获取到指定的
     */
    protected open fun getNodeAnimator(node: Node, parent: PageLayout, target: View):android.animation.Animator? {
        var itemAnimator:android.animation.Animator?=null
        //初始化动画信息
        if (null != node.animator) {
            //转换动画
            itemAnimator = node.animator.convert(parent, target)
        }
        return itemAnimator
    }


    inline fun play(animator: ElementAnimator): Builder=Builder(animator)

    inner class Builder(animator:ElementAnimator){
        //从根节点取此节点
        var current:Node=getAnimatorNode(animator)
        init {
            //添加到根节点
            root.child.add(current)
        }
        /**
         * 记录到当前节点下
         */
        fun after(animator:ElementAnimator):Builder{
            //从集内移除
            var node=getAnimatorNode(animator)
            current.child.add(node)
            current=node
            return this
        }

        /**
         * 与当前节点同级
         */
        fun with(animator:ElementAnimator):Builder{
            var node = getAnimatorNode(animator)
            current.friend.add(node)
            return this
        }

        fun delay(time:Long): Builder {
            current.animator?.delay=time
            return this
        }

        /**
         * 从根节点获取此节点view,因为所有节点,默认都会添加到根节点内
         */
        private fun getAnimatorNode(animator: ElementAnimator): Node {
            var node = root.child.find { it.animator == animator }
            if (null != node) {
                root.child.remove(node)
            } else {
                node = Node(animator)
            }
            return node
        }
    }


}