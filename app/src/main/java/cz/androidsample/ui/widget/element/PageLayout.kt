package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cz.androidsample.ui.widget.element.animator.ElementAnimator

/**
 * Created by cz on 2017/10/27.
 * 分页操作排版布局
 */
class PageLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr){
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    internal val pageScrollItems= mutableListOf<OnPageScrollListener>()
    internal val pageSelectItems= mutableListOf<OnPageSelectListener>()
    internal val elementLayoutItems= mutableListOf<ElementLayout>()
    /**
     * 添加元素
     */
    fun addElementLayout(layout: ElementLayout) {
        //将元素属性映射为控件
        layout.elements.forEach {
            //元素映射view
            val view = it.getView(context)
            //回调子元素,在此处包装两个回调对象
            val pageScrolled = it.pageScrolled
            if(null!=pageScrolled){
                pageScrollItems.add(OnPageScrollListener(view,pageScrolled))
            }
            val pageSelected = it.pageSelected
            if(null!=pageSelected){
                pageSelectItems.add(OnPageSelectListener(view,pageSelected))
            }
            //添加view
            addView(view)
        }
        //添加布局元素集,因为此时还不能转换控件动画,须在onMeasured后,初始化
        elementLayoutItems.add(layout)
    }

    /**
     * 获得页动画集
     */
    internal fun getPageAnimatorSet():AnimatorSet{
        val animatorSet=AnimatorSet()
        elementLayoutItems.forEach {
            val pageAnimator = getPageAnimator(it)
            animatorSet.playTogether(pageAnimator)
        }
        return animatorSet
    }

    /**
     * 获得布局元素转换后动画体
     */
    private fun getPageAnimator(layout: ElementLayout):Animator{
        val animator=layout.animator
        return if(null!=animator){
            //转换为对象动画
            animator.convert(this, this)
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

    override fun onDetachedFromWindow() {
        pageSelectItems.clear()
        pageScrollItems.clear()
        super.onDetachedFromWindow()
    }

    /**
     * 查找元素控件
     */
    fun findElement(id:String?): View?=findViewById(System.identityHashCode(id))

    /**
     * 回调滚动事件
     */
    internal inline fun onPageScrolled(position: Int, offset: Float, offsetPixels:Int, isSelectItem:Boolean){
        pageScrollItems.forEach{ it.call(position,offset,offsetPixels,isSelectItem) }
    }

    /**
     * 回调选中事件
     */
    internal inline fun onPageSelected(position: Int){
        pageSelectItems.forEach{ it.call(position) }
    }

    /**
     * 页滚动变化
     */
    inner class OnPageScrollListener(val v:View,val listener:(View,Int,Float,Int,Boolean)->Unit){
        fun call(position:Int,offset:Float,OffsetPixels:Int,isSelectItem:Boolean){
            listener(v,position,offset,OffsetPixels,isSelectItem)
        }
    }

    /**
     * 页选中变化
     */
    inner class OnPageSelectListener(val v:View,val listener:(View,Int)->Unit){
        fun call(position:Int){
            //回调变化
            listener(v,position)
        }
    }
}