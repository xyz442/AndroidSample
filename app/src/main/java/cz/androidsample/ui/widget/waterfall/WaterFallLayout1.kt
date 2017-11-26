package cz.androidsample.ui.widget.waterfall

import android.content.Context
import android.support.v4.widget.EdgeEffectCompat
import android.util.AttributeSet
import android.view.*
import android.widget.Scroller
import cz.androidsample.R
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
    private var column =0
    private var horizontalSpacing=0f
    private var verticalSpacing=0f

    private val scroller: Scroller = Scroller(context)
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
            setColumn(getInt(R.styleable.WaterFallLayout1_wl_row,2))
            setHorizontalSpacing(getDimension(R.styleable.WaterFallLayout1_wl_horizontalSpacing,0f))
            setVerticalSpacing(getDimension(R.styleable.WaterFallLayout1_wl_verticalSpacing,0f))
            recycle()
        }
    }

    private fun setColumn(column:Int) {
        this.column = column
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
        val width = (MeasureSpec.getSize(widthMeasureSpec)-paddingLeft-paddingRight-(column -1)*horizontalSpacing.toInt())/ column
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
        var leftArray= IntArray(column)
        //计算每一列左边距离
        var left=paddingLeft
        val itemWidth=(width-paddingLeft-paddingRight-(column -1)*horizontalSpacing.toInt())/ column
        for(i in (0..column-1)){
            leftArray[i]=left
            if(i==column-1){
                left+=horizontalSpacing.toInt()
            } else {
                left+=(horizontalSpacing.toInt()+itemWidth)
            }
        }
        //排版子控件
        var topArray= IntArray(column){paddingTop}
        for(index in 0..childCount-1) {
            val column=index% column
            val childView = getChildAt(index)
            val childWidth=childView.measuredWidth
            val childHeight=childView.measuredHeight
            childView.layout(leftArray[column],topArray[column],leftArray[column]+childWidth,topArray[column]+childHeight)
            //顶部位置往下移,这里考虑一个图片特别大情况,另一边则一直积累
            topArray[column]+=childHeight+verticalSpacing.toInt()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

}