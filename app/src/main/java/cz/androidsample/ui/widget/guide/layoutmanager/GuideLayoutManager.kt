package cz.androidsample.ui.widget.guide.layoutmanager

import android.content.Context
import android.view.View
import android.widget.Scroller
import cz.androidsample.ui.widget.guide.GuideLayout

/**
 * Created by cz on 2017/11/1.
 * 分页布局管理器
 */
abstract class GuideLayoutManager(val context: Context) {
    private val MAX_SETTLE_DURATION = 600 // ms
    val SCROLL_STATE_IDLE = 0
    val SCROLL_STATE_DRAGGING = 1
    val SCROLL_STATE_SETTLING = 2
    private lateinit var layout:GuideLayout
    val paddingLeft:Int
        get() = layout.paddingLeft
    val paddingTop:Int
        get() = layout.paddingTop
    val paddingRight:Int
        get() = layout.paddingRight
    val paddingBottom:Int
        get() = layout.paddingBottom
    val childCount:Int
        get() = layout.childCount
    val width:Int
        get() = layout.width
    val height:Int
        get() = layout.height
    val scrollX:Int
        get() = layout.scrollX
    val scrollY:Int
        get() = layout.scrollX
    val currentPosition:Int
        get() = layout.getCurrentPosition()
    val pageCount:Int
        get() = layout.getPageCount()
    /**
     * 获得指定位置控件
     */
    protected fun getChildAt(index:Int)=layout.getChildAt(index)

    /**
     * 滚动到指定位置
     */
    protected fun scrollTo(x:Int, y:Int)=layout.scrollTo(x,y)
    /**
     * 滚动相对量
     */
    protected fun scrollBy(xOffset:Int, yOffset:Int)=layout.scrollBy(xOffset,yOffset)

    protected fun post(action:Runnable)=layout.post(action)

    protected fun postDelayed(action:Runnable,time:Long)=layout.postDelayed(action,time)

    protected fun removeCallbacks(action:Runnable)=layout.removeCallbacks(action)

    protected fun invalidate()=layout.invalidate()
    /**
     * 设置滑动状态
     */
    protected fun setScrollState(state:Int)=layout.setScrollState(state)

    /**
     * 获得view对应位置
     */
    protected fun getViewPosition(child: View)=layout.getViewPosition(child)

    /**
     * 控件分页滚动到指定位置
     */
    protected fun pageScrollTo(x:Int, y:Int)=layout.pageScrollTo(x,y)

    /**
     * 计算控件滚动
     */
    protected fun completeScrolled()=layout.completeScrolled()
    /**
     * 装载进布局
     */
    internal fun onAttachedToWindow(layout:GuideLayout) {
        this.layout=layout
    }

    /**
     * 分离出布局
     */
    fun onDetachedFromWindow(layout:GuideLayout)=Unit

    open fun abortAnimation()=Unit
    /**
     * 布局排版
     */
    abstract fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int)
    /**
     * 滚动前置处理代码
     */
    abstract fun preScrollOffset(xOffset:Int, yOffset:Int, consumed:IntArray):Boolean
    /**
     * 滚动处理
     */
    abstract fun onScrolled(xOffset:Int,yOffset:Int)

    /**
     * 获取滚动的横向计算值
     */
    abstract fun getDecoratedScrollX():Int

    /**
     * 获得滚动的横向计算值
     */
    abstract fun getDecoratedScrollY():Int

    /**
     * 滚动到指定位置
     */
    abstract fun setCurrentItem(position: Int, smoothScroll: Boolean, velocity: Int)
    /**
     * 计算滚动时间
     */
    protected fun getScrollDuration(offset:Int, halfSize:Int, velocity: Int):Int{
        var duration:Int
        val velocity = Math.abs(velocity)
        val distanceRatio = Math.min(1f, 1.0f * Math.abs(offset) / width)
        val distance = halfSize + halfSize * distanceInfluenceForSnapDuration(distanceRatio)
        if (velocity > 0) {
            duration = (4 * Math.round(1000 * Math.abs(distance / velocity)))
        } else {
            val pageDelta = Math.abs(offset).toFloat() / width
            duration = ((pageDelta + 1) * 100).toInt()
        }
        return Math.min(duration, MAX_SETTLE_DURATION)
    }

    private fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= (0.3f * Math.PI / 2.0f).toFloat()
        return Math.sin(f.toDouble()).toFloat()
    }

    /**
     * 惯性滑动事件
     */
    inner class ViewFlinger(val context:Context) :Runnable{
        private val scroller: Scroller = Scroller(context)
        fun abortAnimation(){
            //移除滚动事件
            removeCallbacks(this)
            //中止滚动事件
            scroller.abortAnimation()
        }
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

        fun startScroll(startX:Int,startY:Int,dx:Int,dy:Int,duration:Int) {
            scroller.startScroll(startX, startY, dx, dy,duration)
        }

    }
}