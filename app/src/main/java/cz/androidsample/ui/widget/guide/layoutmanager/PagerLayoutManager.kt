package cz.androidsample.ui.widget.guide.layoutmanager

import android.content.Context
import android.view.View
import android.widget.Scroller

/**
 * Created by cz on 2017/11/1.
 * 按页排序对象,类似viewPager
 */
class PagerLayoutManager(context: Context): GuideLayoutManager(context) {
    private var viewFlinger =ViewFlinger(context)
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
            val viewPosition = getViewPosition(child)
            if (child.visibility != View.GONE&&-1!=viewPosition) {
                var childLeft = paddingLeft +  width * viewPosition
                child.layout(childLeft, paddingTop, childLeft + child.measuredWidth, paddingTop + child.measuredHeight)
            }
        }
    }

    override fun setCurrentItem(position: Int, smoothScroll: Boolean,velocity: Int) {
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
        val dx = position*width - scrollX
        if (dx == 0) {
            completeScrolled()
            setScrollState(SCROLL_STATE_IDLE)
        } else {
            setScrollState(SCROLL_STATE_SETTLING)
            //按标记设定滚动时间
            val duration:Int= getScrollDuration(dx,width/2 ,velocity)
            viewFlinger.startScroll(scrollX, 0, dx, 0, duration)
            post(viewFlinger)
            invalidate()
        }
    }

    override fun preScrollOffset(xOffset: Int, yOffset: Int, consumed: IntArray): Boolean {
        return true
    }

    override fun onScrolled(xOffset: Int, yOffset: Int) {
        scrollBy(xOffset,yOffset)
    }

    override fun getDecoratedScrollX(): Int =scrollX

    override fun getDecoratedScrollY(): Int =0
}
