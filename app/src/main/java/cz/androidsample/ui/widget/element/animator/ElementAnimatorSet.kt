package cz.androidsample.ui.widget.element.animator

import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.util.Log
import android.view.View
import cz.androidsample.ui.widget.element.PageLayout


/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
open class ElementAnimatorSet:Animator(){
    //一个子元素根节点
    private var root = Node(null)
    //父布局宽高
    var pwidth=0
    var pheight=0
    //初始动画属性
    var alpha=1f
    var scaleX=1f
    var scaleY=1f
    var rotation=0f
    var rotationX=0f
    var rotationY=0f
    var translationX=0f
    var translationY=0f

    /**
     * 直接操作的动画元素
     */
    fun addAnimator(animator:ElementAnimator){
        //子元素动画记录父元素id
        animator.elementId=elementId
        root.child.add(Node(animator,root))
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
    protected fun forEachAnimatorNode(parent: PageLayout,target: View, animatorSet:AnimatorSet,node:Animator.Node){
        //遍历子节点,每个孩子节点,都要等当前节点执行完再执行
        if(!node.child.isEmpty()){
            val childAnimatorSet=AnimatorSet()
            //排序子节点动画
            var lastAnimator:android.animation.Animator?=null
            node.child.forEach {
                val itemAnimator=getNodeAnimator(it, parent)
                animatorSet.playTogether(itemAnimator)
                //遍历子节点
                forEachAnimatorNode(parent,target,childAnimatorSet,it)
                //记录子节点
                lastAnimator=itemAnimator
            }
            animatorSet.play(lastAnimator).before(childAnimatorSet)
        }
    }

    /**
     * 根据节点获取到指定的
     */
    private fun getNodeAnimator(node: Node, parent: PageLayout): android.animation.Animator? {
        var itemAnimator:android.animation.Animator?=null
        //初始化动画信息
        val animator = node.animator
        if (null != animator&&null!=animator.elementId) {
            val target = parent.findElement(animator.elementId)
            if(null==target){
                Log.w("Guide","Can't find element:${animator.elementId}")
            } else {
                //转换动画
                itemAnimator = node.animator.convert(parent, target)
                //添加动画结束监听
                if(null!=node.animatorEnd){
                    itemAnimator?.addListener(object: AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: android.animation.Animator?) {
                            super.onAnimationEnd(animation)
                            node.animatorEnd?.invoke()
                        }
                    })
                }
            }
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
            //记录新的节点
            node.parent=current
            current.child.add(node)
            current=node
            return this
        }

        /**
         * 与当前节点同级
         */
        fun with(animator:ElementAnimator):Builder{
            var node = getAnimatorNode(animator)
            node.parent=current.parent
            current.parent?.child?.add(node)
            current=node
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