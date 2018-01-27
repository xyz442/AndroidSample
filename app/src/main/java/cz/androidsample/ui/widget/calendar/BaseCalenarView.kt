package cz.androidsample.ui.widget.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import cz.androidsample.R
import cz.androidsample.ui.widget.calendar.adapter.CalendarAdapter
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Created by cz on 2018/1/27.
 * 1:实现常规月排版
 * 2:抽离数据适配器,取一个模板控件,对所有布局进行绘制.并回调点击事件.以及点击效果
 * 3:封装滚动效果.
 * 4:封装复杂的选择模式(块选,多选,点选)
 * 5:封装部分块变化
 */
class BaseCalenarView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : View(context, attrs, defStyleAttr), GestureDetector.OnGestureListener,ViewManager{
    companion object{
        const val NONE=0x00
        const val LEFT=0x01
        const val TOP=0x02
        const val RIGHT=0x04
        const val BOTTOM=0x08
        const val HORIZONTAL=0x10
        const val VERTICAL=0x20
    }
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    //处理手势
    private val gestureDetector = GestureDetector(context, this)
    private val scroller= ScrollerCompat.create(context)
    //添加view集
    private val views= mutableListOf<View>()
    private val dividerPaint =Paint(Paint.ANTI_ALIAS_FLAG)
    private var dividerDrawable:Drawable?=null
    private var adapter: CalendarAdapter?=null
    private var dividerGravity: Int=NONE
    private var dividerSize: Float=0f
    private var horizontalSpacing: Float=0f
    private var verticalSpacing: Float=0f
    val childCount:Int get() = views.size

    init {
        context.obtainStyledAttributes(attrs, R.styleable.BaseCalendarView).apply{
            //设置分隔线尺寸
            setDividerSize(getDimension(R.styleable.BaseCalendarView_bv_dividerSize,0f))
            //设置分隔线drawable
            setDividerDrawable(getDrawable(R.styleable.BaseCalendarView_bv_dividerDrawable))
            //设置分隔线可显示方向
            setDividerGravity(getInt(R.styleable.BaseCalendarView_bv_divideGravity, NONE))
            //设置横向分隔空间
            setHorizontalSpacing(getDimension(R.styleable.BaseCalendarView_bv_horizontalSpacing,0f))
            //设置纵向分隔空间
            setVerticalSpacing(getDimension(R.styleable.BaseCalendarView_bv_verticalSpacing,0f))
            recycle()
        }
    }
    private fun setHorizontalSpacing(spacing: Float) {
        this.horizontalSpacing=spacing
        invalidate()
    }


    private fun setVerticalSpacing(spacing: Float) {
        this.verticalSpacing=spacing
        invalidate()
    }

    /**
     * 设置分隔线方位
     */
    private fun setDividerGravity(gravity: Int) {
        this.dividerGravity=gravity
        invalidate()
    }

    fun setAdapter(adapter:CalendarAdapter){
        this.adapter=adapter
    }

    fun setDividerSize(dividerSize:Float){
        this.dividerSize=dividerSize
        invalidate()
    }
    fun setDividerResources(resource:Int){
        setDividerDrawable(ContextCompat.getDrawable(context,resource))
    }

    fun setDividerColor(color:Int)=setDividerDrawable(ColorDrawable(color))

    fun setDividerDrawable(drawable:Drawable){
        this.dividerDrawable=drawable
        invalidate()
    }

    override fun addView(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams=params
        views.add(view)
    }

    override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams=params
        view.requestLayout()
    }

    override fun removeView(view: View) {
        views.remove(view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val adapter=adapter
        var measuredWidth=paddingLeft+paddingRight
        var measuredHeight=paddingTop+paddingBottom
        if(0==childCount&&null!=adapter){
            //获取一个子控件
            val itemView=adapter.getItemView(context,this)
            //动态测试其大小
            measureChild(itemView,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
            //添加到控件集
            addView(itemView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT))
        }
        //TODO 动态计算当前容器大小
        //设置当前整体大小
        setMeasuredDimension(measuredWidth,measuredHeight)
    }

    private fun measureChild(child: View, widthMode: Int, heightMode: Int) {
        val lp = child.layoutParams
        val widthSpec = getChildMeasureSpec(measuredWidth-(paddingLeft + paddingRight), widthMode, lp.width)
        val heightSpec = getChildMeasureSpec(measuredHeight-(paddingTop + paddingBottom), heightMode, lp.height)
        child.measure(widthSpec, heightSpec)
    }

    @SuppressLint("WrongConstant")
    private fun getChildMeasureSpec(parentSize: Int, parentMode: Int, childDimension: Int): Int {
        val size = Math.max(0, parentSize)
        var resultSize:Int
        var resultMode:Int
        //此处描述子控件占满全屏,所以直接采用wrap_content,或者给定固定尺寸计算
        if (childDimension >= 0) {
            resultSize = childDimension
            resultMode = View.MeasureSpec.EXACTLY
        } else {
            resultSize = size
            if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                resultMode = View.MeasureSpec.AT_MOST
            } else {
                resultMode = View.MeasureSpec.UNSPECIFIED
            }
        }
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val adapter=adapter?:return

    }


    //--------------------------------------------------------------------------------------------------
    //点击以及滑动手势
    //--------------------------------------------------------------------------------------------------
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDown(e: MotionEvent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLongPress(e: MotionEvent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}