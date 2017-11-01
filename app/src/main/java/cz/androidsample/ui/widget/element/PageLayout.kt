package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View

/**
 * Created by cz on 2017/10/27.
 * 分页操作排版布局
 */
class PageLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr){
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)

    private var pageAnimator: Animator?=null
    /**
     * 添加元素
     */
    fun addElementLayout(layout: ElementLayout) {
        //将元素属性映射为控件
        layout.elements.forEach {
            //元素映射view
            val view = it.getView(context)
            //添加view
            addView(view)
        }
        //在消息队列中执行,确保控件的尺寸运算无误
        post {
            //执行当前页所有动画元素
            val elementAnimator = layout.animator
            //记录分页动画
            pageAnimator=if(null!=elementAnimator){
                //转换为对象动画
                elementAnimator.convert(this, this)
            } else {
                Log.w(TAG,"当前分页没有使用定义动画组,并行执行所有元素动画!")
                //没有控制组,直接执行全部动画
                val animator=AnimatorSet()
                layout.elements.forEach {
                    if(null!=it.id&&null!=it.animator){
                        val target=findElement(it.id)
                        if(null!=target){
                            animator.playTogether(it.animator?.convert(this,target))
                        }
                    }
                }
                animator
            }
        }
    }

    /**
     * 查找元素控件
     */
    fun findElement(id:String?): View?=findViewById(System.identityHashCode(id))

}