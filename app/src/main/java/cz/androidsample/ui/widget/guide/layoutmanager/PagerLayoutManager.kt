package cz.androidsample.ui.widget.guide.layoutmanager

import android.content.Context
import android.view.View
import android.widget.Scroller

/**
 * Created by cz on 2017/11/1.
 * 按页排序对象,类似viewPager
 */
class PagerLayoutManager(context: Context): GuideLayoutManager(context) {

    private val scroller: Scroller = Scroller(context)
    private val flingAction=FlingAction()
    /**
     * 终止滚动
     */
    override fun abortAnimation(){
        //移除滚动事件
        removeCallbacks(flingAction)
        //中止滚动事件
        scroller.abortAnimation()
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
                val viewPosition = getViewPosition(child)
                if (0<=viewPosition) {
                    var childLeft = paddingLeft +  width * viewPosition
                    child.layout(childLeft, paddingTop, childLeft + child.measuredWidth, paddingTop + child.measuredHeight)
                }
            }
        }
    }

    override fun setCurrentItem(position: Int, smoothScroll: Boolean, velocity: Int) {
        val destStart= width* position
        val destEnd= 0
        if (smoothScroll) {
            smoothScrollTo(destStart,destEnd, velocity)
            //设置分页选中
            setPageSelected(position)
        } else {
            //设置分页选中
            setPageSelected(position)
            completeScrolled()
            pageScrollTo(destStart,destEnd)
        }
    }

    internal fun smoothScrollTo(x: Int, y: Int, velocity: Int) {
        var velocity = velocity
        if (childCount == 0) {
            // Nothing to do.
            return
        }
        val sx = scrollX
        val sy = scrollY
        val dx = x - sx
        val dy = y - sy
        if (dx == 0 && dy == 0) {
            completeScrolled()
            setScrollState(SCROLL_STATE_IDLE)
        } else {
            setScrollState(SCROLL_STATE_SETTLING)
            //按标记设定滚动时间
            val duration:Int= getScrollDuration(dx,width/2 ,velocity)
            scroller.startScroll(sx, sy, dx, dy, duration)
            post(flingAction)
            invalidate()
        }
    }

    override fun onPageScrolled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isScrolling(): Boolean=!scroller.isFinished&&scroller.computeScrollOffset()

    override fun preScrollOffset(xOffset: Int, yOffset: Int, consumed: IntArray): Boolean {
        return true
    }

    override fun onScrolled(xOffset: Int, yOffset: Int) {
        scrollBy(xOffset,yOffset)
    }

    /**
     * 惯性滑动事件
     */
    inner class FlingAction :Runnable{
        override fun run() {
            if(!scroller.isFinished&&scroller.computeScrollOffset()){
                //此处计算滚动速率
                pageScrollTo(scroller.currX,scroller.currY)
                postDelayed(this,16)
            } else {
                //滑动完成后,计算
                completeScrolled()
            }
        }

    }

}
