package cz.androidsample.ui.widget.waterfall.other.bao.widget


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import cz.androidsample.R

/**
 * Created by yuanbaobao on 2017/12/1.
 */
class WaterfullLayout : ViewGroup {
    // 列数
    private var columns = 2
    // 行数
    private var rows = 0
    // 边距
    private var margin = 10
    // 子视图数量
    private var count = 0
    private var mMaxChildWidth = 0
    private var mMaxChildHeight = 0

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.BaoGridLayout1)
            columns = a.getInteger(R.styleable.BaoGridLayout1_numColumns, 2)
            margin = a.getInteger(R.styleable.BaoGridLayout1_itemMargin, 2)
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        // TODO Auto-generated constructor stub
    }

    constructor(context: Context) : super(context) {
        // TODO Auto-generated constructor stub
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        mMaxChildWidth = 0
        mMaxChildHeight = 0

        count = childCount
        if (count == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        rows = if (count % columns == 0) count / columns else count / columns + 1// 行数
        val top = IntArray(columns)
        for (i in 0 until rows) {// 遍历行
            for (j in 0 until columns) {// 遍历每一行的元素
                val child = this.getChildAt(i * columns + j) ?: break
                val lp = child.layoutParams

                if (child.visibility == View.GONE) {
                    continue
                }

                child.measure(View.MeasureSpec.makeMeasureSpec(
                        View.MeasureSpec.getSize(widthMeasureSpec),
                        View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                        lp.height, View.MeasureSpec.AT_MOST))
                top[j] += lp.height + margin
                mMaxChildWidth = Math.max(mMaxChildWidth,
                        child.measuredWidth)
            }

        }

        setMeasuredDimension(View.resolveSize(mMaxChildWidth, widthMeasureSpec),
                View.resolveSize(getMax(top) + margin, heightMeasureSpec))

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        // TODO Auto-generated method stub
        val width = r - l
        if (count == 0)
            return
        val gridW = (width - margin * (columns + 1)) / columns//宽度
        var gridH = 0

        var left = 0
        val top = IntArray(columns)

        for (i in 0 until rows) {// 遍历行
            for (j in 0 until columns) {// 遍历每一行的元素
                val child = this.getChildAt(i * columns + j) ?: return
                val lp = child.layoutParams

                child.measure(View.MeasureSpec.makeMeasureSpec(gridW,
                        View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                        lp.height, View.MeasureSpec.AT_MOST))

                gridH = lp.height
                left = j * gridW + margin * (j + 1)
                top[j] += margin
                child.layout(left, top[j], left + gridW, top[j] + gridH)
                top[j] += gridH
            }

        }
    }

    /** 计算整体布局高度，为了在嵌套在scrollview中能显示出来  */
    private fun getMax(array: IntArray): Int {
        var max = array[0]
        array.indices
                .asSequence()
                .filter { max < array[it] }
                .forEach { max = array[it] }
        return max
    }

}
