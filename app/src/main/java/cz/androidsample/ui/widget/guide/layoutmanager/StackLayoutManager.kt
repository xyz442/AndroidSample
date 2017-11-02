package cz.androidsample.ui.widget.guide.layoutmanager

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.widget.Scroller

/**
 * Created by cz on 2017/11/2.
 * 重叠排序对象
 */
class StackLayoutManager(context: Context): GuideLayoutManager(context) {
    private var viewFlinger =ViewFlinger(context)
    //堆栈的平移距离
    private var stackScrollX=0
    /**
     * 终止滚动
     */
    override fun abortAnimation(){
        //移除滚动事件
        viewFlinger.abortAnimation()
    }
    /**
     * 按页排版
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val position = getViewPosition(child)
                if(-1!=position){
                    child.layout(paddingLeft, paddingTop, width-paddingRight, height-paddingBottom)
                }
            }
        }
    }

    override fun setCurrentItem(position: Int, smoothScroll: Boolean,velocity: Int) {
        //目标位置不能与滚动位置一致
        if (smoothScroll) {
            smoothScrollTo(position, velocity)
        } else {
            completeScrolled()
            pageScrollTo(width* position,0)
        }
    }

    internal fun smoothScrollTo(position: Int, velocity: Int) {
        if (childCount == 0) return
        var velocity = velocity
        val dx = position*width - stackScrollX
        if (dx == 0) {
            completeScrolled()
            setScrollState(SCROLL_STATE_IDLE)
        } else {
            setScrollState(SCROLL_STATE_SETTLING)
            //按标记设定滚动时间
            val duration:Int= getScrollDuration(dx,width/2 ,velocity)
            viewFlinger.startScroll(stackScrollX, 0, dx, 0, duration)
            post(viewFlinger)
            invalidate()
        }
    }

    override fun getDecoratedScrollX(): Int =stackScrollX

    override fun getDecoratedScrollY(): Int =0

    /**
     * 不滚动
     */
    override fun preScrollOffset(xOffset: Int, yOffset: Int, consumed: IntArray): Boolean {
        stackScrollX+=xOffset
        return false
    }

    /**
     * 不滚动
     */
    override fun onScrolled(xOffset: Int, yOffset: Int) =Unit

}
