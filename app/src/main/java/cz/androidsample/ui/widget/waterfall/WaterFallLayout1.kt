package cz.androidsample.ui.widget.waterfall

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.EdgeEffectCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.waterfall.adapter.WaterFallAdapter

/**
 * Created by cz on 2017/11/26.
 * 第一版功能,
 * 控件排版
 * 布局滚动
 */
class WaterFallLayout1(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private var adapter: WaterFallAdapter?=null
    private var columnCount =0
    private var horizontalSpacing=0f
    private var verticalSpacing=0f

    private val scroller: ScrollerCompat = ScrollerCompat.create(context)
    //边缘阴影
    private var startEdge: EdgeEffectCompat
    private var endEdge: EdgeEffectCompat
    private var isBeingDragged: Boolean = false
    private var touchSlop: Int = 0
    //滑动属性
    private var lastMotionX = 0f
    private var lastMotionY = 0f

    private var velocityTracker: VelocityTracker? = null
    private var minimumVelocity: Int = 0
    private var maximumVelocity: Int = 0
    init {
        //设置是否可以渲染,此设置影响边缘效果
        setWillNotDraw(View.OVER_SCROLL_NEVER!=overScrollMode)
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        isFocusable = true

        startEdge = EdgeEffectCompat(context)
        endEdge = EdgeEffectCompat(context)
        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop
        minimumVelocity = configuration.scaledMinimumFlingVelocity
        maximumVelocity = configuration.scaledMaximumFlingVelocity

        context.obtainStyledAttributes(attrs, R.styleable.WaterFallLayout1).apply {
            setColumnCount(getInt(R.styleable.WaterFallLayout1_wl_columnCount,2))
            setHorizontalSpacing(getDimension(R.styleable.WaterFallLayout1_wl_horizontalSpacing,0f))
            setVerticalSpacing(getDimension(R.styleable.WaterFallLayout1_wl_verticalSpacing,0f))
            recycle()
        }
    }

    private fun setColumnCount(column:Int) {
        this.columnCount = column
        requestLayout()
    }

    private fun setHorizontalSpacing(spacing: Float) {
        this.horizontalSpacing=spacing
        requestLayout()
    }

    private fun setVerticalSpacing(spacing: Float) {
        this.verticalSpacing=spacing
        requestLayout()
    }

    fun setAdapter(adapter: WaterFallAdapter){
        val oldAdapter=this.adapter
        //todo adapter changed
        this.adapter=adapter

        val count=adapter.getCount()
        for(position in (0..count-1)){
            //获取数据
            val view=adapter.getView(this,position)
            //绑定数据
            adapter.bindView(view,position)
            //添加数据
            addView(view)
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for(index in 0..childCount-1){
            val childView=getChildAt(index)
            val childWidthMeasureSpec = getChildWidthMeasureSpec(childView.layoutParams, widthMeasureSpec)
            //子控件高度计算为自适应,无论什么尺寸,都为自适应.使外部设置无效
            val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childView.layoutParams.height,MeasureSpec.AT_MOST)
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec)
        }
        //todo 自量测
    }

    /**
     * 获取子孩子宽度的measureSpec
     */
    private fun getChildWidthMeasureSpec(layoutParams: ViewGroup.LayoutParams,widthMeasureSpec:Int):Int {
        //此处父容器宽除row
        val width = (MeasureSpec.getSize(widthMeasureSpec)-paddingLeft-paddingRight-(columnCount -1)*horizontalSpacing.toInt())/ columnCount
        val parentMode=MeasureSpec.getMode(widthMeasureSpec)
        return when (layoutParams.width) {
            LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(width,MeasureSpec.EXACTLY)
            LayoutParams.WRAP_CONTENT-> {
                val resultMode:Int
                if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                    resultMode = View.MeasureSpec.AT_MOST
                } else {
                    resultMode = View.MeasureSpec.UNSPECIFIED
                }
                MeasureSpec.makeMeasureSpec(layoutParams.width,resultMode)
            }
            else ->{
                //限定最大宽度
                if(MeasureSpec.UNSPECIFIED==parentMode){
                    MeasureSpec.makeMeasureSpec(Math.min(width,layoutParams.width),MeasureSpec.UNSPECIFIED)
                } else {
                    MeasureSpec.makeMeasureSpec(Math.min(width,layoutParams.width),MeasureSpec.EXACTLY)
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var leftArray= IntArray(columnCount)
        //计算每一列左边距离
        var left=paddingLeft
        val itemWidth=(width-paddingLeft-paddingRight-(columnCount -1)*horizontalSpacing.toInt())/ columnCount
        for(i in (0..columnCount -1)){
            leftArray[i]=left
            if(i== columnCount -1){
                left+=horizontalSpacing.toInt()
            } else {
                left+=(horizontalSpacing.toInt()+itemWidth)
            }
        }
        //排版子控件
        var column=0
        var topArray= IntArray(columnCount){paddingTop}
        for(index in 0..childCount-1) {
            val childView = getChildAt(index)
            val childWidth=childView.measuredWidth
            val childHeight=childView.measuredHeight
            childView.layout(leftArray[column],topArray[column],leftArray[column]+childWidth,topArray[column]+childHeight)
            //顶部位置往下移,这里考虑一个图片特别大情况,另一边则一直积累
            topArray[column]+=childHeight+verticalSpacing.toInt()
            //始终查找当前列最小元素位置
            column=topArray.indexOf(topArray.min()?:0)
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if(!scroller.isFinished&&scroller.computeScrollOffset()){
            scrollTo(scroller.currX,scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action and MotionEventCompat.ACTION_MASK
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            endDrag()
            return false
        }
        if (action != MotionEvent.ACTION_DOWN&&isBeingDragged) {
            return true
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y
                val dy = y - lastMotionY
                if (dy > touchSlop) {
                    isBeingDragged = true
                }
            }
            MotionEvent.ACTION_DOWN -> {
                lastMotionX = ev.x
                lastMotionY = ev.y
            }
        }

        if (!isBeingDragged) {
            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker?.addMovement(ev)
        }
        return isBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)
        val action = ev.action
        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                lastMotionX = ev.x
                lastMotionY = ev.y
                scroller.abortAnimation()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y
                var yDiff = y - lastMotionY
                if (!isBeingDragged&&Math.abs(yDiff) > touchSlop) {
                    isBeingDragged = true
                    lastMotionX = x
                    lastMotionY = y
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                if (isBeingDragged) {
                    lastMotionX =x
                    lastMotionY =y
                    //最大滚动区间
                    val verticalScrollRange = computeVerticalScrollRange()
                    //这里做边界处理
                    if(0>scrollY-yDiff){
                        yDiff=scrollY*1f
                    } else if(verticalScrollRange<scrollY-yDiff){
                        yDiff=scrollY-verticalScrollRange*1f
                    }
                    scrollBy(0, -yDiff.toInt())
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> if (isBeingDragged) {
                val velocityTracker = velocityTracker
                velocityTracker?.computeCurrentVelocity(1000,maximumVelocity.toFloat())
                val initialVelocityY = VelocityTrackerCompat.getYVelocity(velocityTracker, 0).toInt()
                //这里限定整个滚动区域位置,如果复用,则需要动态计算
                //这里先取最后columnCount个view值,做计算,后面会动态计算
                val verticalScrollRange = computeVerticalScrollRange()
                scroller.fling(scrollX,scrollY,0,-initialVelocityY,0,0,0,verticalScrollRange)
                postInvalidate()
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> if(isBeingDragged){
                endDrag()
            }
        }
        return true
    }

    override fun computeVerticalScrollExtent(): Int {
        return (childCount-columnCount..childCount-1).map{ getChildAt(it).bottom }.maxBy { it }?:0
    }

    override fun computeVerticalScrollRange(): Int {
        val computeVerticalScrollExtent = computeVerticalScrollExtent()
        return computeVerticalScrollExtent-height
    }

    private fun endDrag() {
        isBeingDragged = false
        if (velocityTracker != null) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
    }


}