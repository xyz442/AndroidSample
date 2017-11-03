package cz.androidsample.ui.hierarchy.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import android.widget.Toast
import cz.androidsample.debugLog
import org.jetbrains.anko.backgroundDrawable
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by cz on 2017/10/10.
 */
class HierarchyLayout1(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr) ,ViewManager, ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val scroller=ScrollerCompat.create(context)
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val rect= Rect()
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()
    init {
        val random=Random()
        (0..100).forEach {
            val view=View(context)
            val color=Color.argb(0xff,random.nextInt(0xFF),random.nextInt(0xFF),random.nextInt(0xFF))
            val pressColor=Color.argb(0xff,Math.min(0xff,Color.red(color)+30),Math.min(0xff,Color.green(color)+30),Math.min(0xff,Color.blue(color)+30))
            val drawable=StateListDrawable()
            drawable.addState(intArrayOf(android.R.attr.state_empty),ColorDrawable(color))
            drawable.addState(intArrayOf(android.R.attr.state_pressed),ColorDrawable(pressColor))
            view.backgroundDrawable=drawable
            view.setOnClickListener {
                Toast.makeText(context,"点击${indexOfChild(it)}",Toast.LENGTH_SHORT).show()
            }
            addView(view,ViewGroup.LayoutParams(300,300))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for(view in views){
            measureChildWithMargins(view,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
        }
    }

    fun measureChildWithMargins(child: View, widthMode: Int, heightMode: Int) {
        val lp = child.layoutParams as ViewGroup.LayoutParams
        val widthSpec = getChildMeasureSpec(width, widthMode, paddingLeft + paddingRight, lp.width)
        val heightSpec = getChildMeasureSpec(height, heightMode, paddingTop + paddingBottom, lp.height)
        child.measure(widthSpec, heightSpec)
    }


    fun getChildMeasureSpec(parentSize: Int, parentMode: Int, padding: Int, childDimension: Int): Int {
        val size = Math.max(0, parentSize - padding)
        var resultSize = 0
        var resultMode = 0
        if (childDimension >= 0) {
            resultSize = childDimension
            resultMode = View.MeasureSpec.EXACTLY
        } else {
            if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                resultSize = size
                resultMode = parentMode
            } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                resultSize = size
                if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                    resultMode = View.MeasureSpec.AT_MOST
                } else {
                    resultMode = View.MeasureSpec.UNSPECIFIED
                }
            }
        }
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val value=8
        (0..getChildCount()-1).forEach {
            val row=(it/value)
            val column=it%value
            val childView=getChildAt(it)
            debugLog("onLayout index:$it row:$row column:$column")
            childView.layout((column*300), (row*300), ((column+1)*300), ((row+1)*300))
            setChildPress(childView,false)
        }
    }

    private fun setChildPress(childView:View,press:Boolean){
        childView.isPressed = press
        val background=childView.background
        if(null!=background&&background is StateListDrawable){
            background.state = if(press) intArrayOf(android.R.attr.state_pressed) else intArrayOf(android.R.attr.state_empty)
            background.jumpToCurrentState()
            invalidate()
        }
    }


    fun getChildCount()=views.size

    fun getChildAt(index:Int)=views[index]

    fun indexOfChild(view:View)=views.indexOf(view)

    override fun addView(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams=params
        views.add(view)
    }

    override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams=params
    }

    override fun removeView(view: View) {
        views.remove(view)
    }

    fun getScale(){

    }

    override fun computeHorizontalScrollRange(): Int {
        val value=8
        return value*300-width
    }

    override fun computeVerticalScrollRange(): Int {
        val value=8
        val childCount=getChildCount()
        val row=if(0==childCount%value) childCount/value else childCount/value+1
        return row*300-height
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) {

    }


    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor=detector.scaleFactor
        scaleMatrix.setScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
        debugLog("onScale:$scaleFactor")
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val value=8
        (0..getChildCount()-1).forEach {
            val row=(it/value)
            val column=it%value
            val childView=getChildAt(it)
            canvas.save()
            canvas.translate((column*300).toFloat(), (row*300).toFloat())
            childView.draw(canvas)
            canvas.restore()
        }
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    /**
     * 取消按下控件状态
     */
    private fun releasePressView(){
        views.find { it.isPressed }?.let { setChildPress(it,false) }
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        views.find { it.isPressed }?.let {
            Toast.makeText(context,"Click:${indexOfChild(it)}",Toast.LENGTH_SHORT).show()
        }
        releasePressView()
        return true
    }


    override fun onDown(e: MotionEvent): Boolean {
        scroller.abortAnimation()
        val x=scrollX+e.x.toInt()
        val y=scrollY+e.y.toInt()
        views.forEach{ view ->
            rect.set(view.left,view.top,view.right,view.bottom)
            if(rect.contains(x,y)) run {
                setChildPress(view,true)
            }
        }
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        scroller.fling(scrollX,scrollY,-velocityX.toInt(),-velocityY.toInt(),0,computeHorizontalScrollRange(),0,computeVerticalScrollRange())
        invalidate()
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if(!scroller.isFinished&&scroller.computeScrollOffset()){
            scrollTo(scroller.currX,scroller.currY)
            invalidate()
        }
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        val horizontalScrollRange = computeHorizontalScrollRange()
        val verticalScrollRange = computeVerticalScrollRange()
        //横轨滚动越界
        var distanceX=distanceX.toInt()
        if(0>scrollX||scrollX>horizontalScrollRange){
            //横向直接越界
            distanceX=0
        } else if(scrollX<-distanceX){
            //横向向左滚动阀值越界
            distanceX = -scrollX
        } else if(scrollX+distanceX>horizontalScrollRange){
            //横向向右越界
            distanceX = horizontalScrollRange-scrollX
        }
        //纵向滚动越界
        var distanceY=distanceY.toInt()
        if(0>scrollY||scrollY>verticalScrollRange){
            distanceY=0
        } else if(scrollY<-distanceY){
            distanceY = -scrollY
        } else if(scrollY+distanceY>verticalScrollRange){
            distanceY = verticalScrollRange-scrollY
        }
        scrollBy(distanceX,distanceY)
        //释放按下控件状态
        releasePressView()
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        releasePressView()
    }



}