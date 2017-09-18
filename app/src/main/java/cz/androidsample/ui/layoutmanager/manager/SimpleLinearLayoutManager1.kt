package com.cz.sample.ui.layoutmanager

import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

/**
 * Created by cz on 2017/9/14.
 * 一个LinearLayoutManager最精简核心实现
 * 本示例演示实现最初铺满布局演示,最初铺满布局,应该当前有多少空间,初始化多少条目
 */
open class SimpleLinearLayoutManager1 : RecyclerView.LayoutManager {
    companion object {
        val DIRECTION_START = -1
        val DIRECTION_END = 1

        val HORIZONTAL = OrientationHelper.HORIZONTAL
        val VERTICAL = OrientationHelper.VERTICAL
    }
    protected lateinit var orientationHelper: OrientationHelper
    private val layoutState=LayoutState()
    /**
     * 当前排版方向
     * @see {@link .HORIZONTAL} or {@link .VERTICAL}
     */
    private var orientation: Int = LinearLayoutCompat.VERTICAL

    @JvmOverloads constructor(orientation: Int = LinearLayoutCompat.VERTICAL) {
        setOrientation(orientation)
    }

    override fun generateDefaultLayoutParams() =RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    /**
     * 设置方向
     * @param orientation
     */
    open fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw IllegalArgumentException("invalid orientation:" + orientation)
        }
        this.orientation = orientation
        if (OrientationHelper.HORIZONTAL == orientation) {
            orientationHelper = OrientationHelper.createHorizontalHelper(this)
        } else if (OrientationHelper.VERTICAL == orientation) {
            orientationHelper = OrientationHelper.createVerticalHelper(this)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if(0==itemCount||state.isPreLayout){
            //将当前所有的RecyclerView的ChildView进行回收
            detachAndScrapAttachedViews(recycler)
        } else if(state.didStructureChange()){
            detachAndScrapAttachedViews(recycler)
            //初始化布局各状态,因为起始是向下进行铺
            updateLayoutStateToFillEnd(0,0)
            //填充控件
            fill(recycler,state)
        }
    }

    private fun updateLayoutStateToFillEnd(itemPosition: Int, offset: Int) {
        layoutState.layoutOffset = offset
        layoutState.available = height - paddingBottom - offset
        layoutState.position = itemPosition
        layoutState.itemDirection = DIRECTION_END
    }

    /**
     * 填充控件
     */
    private fun fill(recycler: RecyclerView.Recycler, state: RecyclerView.State):Int {
        //记录起始可用空间
        val start=layoutState.available
        //记录可用空间,当available大于0时,代表有空余的可填充控件,一直循环铺满为止
        var remainingSpace=layoutState.available
        while(0<remainingSpace&&hasMore(state)){
            //循环排版子控件,直到塞满为止
            val consumed=layoutChildView(recycler,state)
            layoutState.layoutOffset +=consumed*layoutState.itemDirection
            layoutState.available-=consumed
            remainingSpace-=consumed
        }
        //返回排版后,所占用空间
        return start-layoutState.available
    }
    /**
     * 填充子控件
     */
    protected open fun layoutChildView(recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        val view=nextView(recycler,state)
        if (layoutState.itemDirection == DIRECTION_END) {
            addView(view)
        } else {
            addView(view, 0)
        }
        //测量控件
        measureChildWithMargins(view,0,0)
        /* orientationHelper.getDecoratedMeasurement(view)会获取当前方向控件计算尺寸
            如horizontal 取width作为计算长度+ insets:分隔线空间+控件margin值 为总计算高度 */
        val consumed = orientationHelper.getDecoratedMeasurement(view)
        var left: Int=paddingLeft
        val top: Int
        val right: Int
        val bottom: Int
        if (orientation == VERTICAL) {
            //width+分隔线+左右margin,控制右排版位置
            right = left + orientationHelper.getDecoratedMeasurementInOther(view)
            if (layoutState.itemDirection == DIRECTION_START) {
                bottom = layoutState.layoutOffset
                top = layoutState.layoutOffset - consumed
            } else {
                top = layoutState.layoutOffset
                bottom = layoutState.layoutOffset + consumed
            }
        } else {
            top = paddingTop
            bottom = top + orientationHelper.getDecoratedMeasurementInOther(view)
            if (layoutState.itemDirection == DIRECTION_START) {
                right = layoutState.layoutOffset
                left = layoutState.layoutOffset - consumed
            } else {
                left = layoutState.layoutOffset
                right = layoutState.layoutOffset + consumed
            }
        }
        layoutDecorated(view, left, top, right, bottom)
        //返回控件高度/宽
        return consumed
    }


    protected fun nextView(recycler: RecyclerView.Recycler, state: RecyclerView.State): View {
        //获取一个控件,会走完缓存->onCreateViewHolder()
        val view=recycler.getViewForPosition(layoutState.position)
        layoutState.position+=layoutState.itemDirection
        return view
    }

    protected fun hasMore(state: RecyclerView.State):Boolean{
        return layoutState.position in 0..state.itemCount-1
    }


    inner class LayoutState{
        /**
         * 当前有效空间,初始为控件本身大小,以后则为滑动到可以填充控件的位置
         */
        var available=0
        /**
         * 当前排版位置,排版
         */
        var layoutOffset =0
        /**
         * 当前位置
         */
        var position:Int=0
        /**
         * 当前操作条目方向
         */
        var itemDirection=0
    }


}