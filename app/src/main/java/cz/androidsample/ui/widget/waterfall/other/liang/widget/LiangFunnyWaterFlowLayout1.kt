package cz.androidsample.ui.widget.waterfall.other.liang.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import cz.androidsample.R
import cz.androidsample.ui.widget.waterfall.other.liang.adapter.FunnyFlowAdapter

/**
 * Created by Administrator on 2017/12/2.
 * 瀑布流布局
 */
class LiangFunnyWaterFlowLayout1(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {
    private var columnNum = 0
    private var layoutAdapter: FunnyFlowAdapter<*>?=null
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    init {
        context.obtainStyledAttributes(attrs, R.styleable.LiangFunnyWaterFlowLayout1).apply {
            setColumnNum(getInt(R.styleable.LiangFunnyWaterFlowLayout1_fwfl_columnNum,3))
            recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for(index in 0 until childCount){
            val childView = getChildAt(index)
            val childWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight) / columnNum
            val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth , MeasureSpec.EXACTLY)
            //图片的话需要调整高度的高宽比，暂时没有增加
            val childHeightMeasureSpec = childView.layoutParams.height
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val leftArray = IntArray(columnNum)
        var left = paddingLeft
        val itemWidth = (width - paddingLeft - paddingRight) / columnNum
        for (index in 0 until columnNum) {
            leftArray[index] = left
            left += if(index != columnNum -1) {
                itemWidth
            } else {0}
        }

        //这部分还是没太想明白，借鉴了别人的思路
        var column=0
        val topArray= IntArray(columnNum){paddingTop}
        for(index in 0 until childCount) {
            val childView = getChildAt(index)
            val childWidth=childView.measuredWidth
            val childHeight=childView.measuredHeight
            childView.layout(leftArray[column],topArray[column],leftArray[column]+childWidth,topArray[column]+childHeight)
            topArray[column]+=childHeight
            column=topArray.indexOf(topArray.min()?:0)
        }
    }

    private fun setColumnNum(columnNum : Int) {
        this.columnNum = columnNum
        requestLayout()
    }

    public fun setAdapter(adapter: FunnyFlowAdapter<*>){
        //参考了别人的写法
        this.layoutAdapter = adapter
        val adapterNum = adapter.getNum()
        for(position in (0 until adapterNum)){
            val view = adapter.inflaterView(this, position)
            adapter.bindView(view, position)
            addView(view)
        }
        requestLayout()
    }

    //未完成手势部分
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
    }
}