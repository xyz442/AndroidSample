package cz.androidsample.ui.widget.scroll

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.*
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.padding
import java.util.*

/**
 * Created by cz on 2017/12/1.
 */
class ScrollLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : FrameLayout(context, attrs, defStyleAttr){
    private var isBeingDragged: Boolean = false
    //滑动属性
    private var lastMotionX = 0f
    private var lastMotionY = 0f
    private var scroller= Scroller(context)

    private var touchSlop: Int = 0
    private var velocityTracker: VelocityTracker? = null
    private var minimumVelocity: Int = 0
    private var maximumVelocity: Int = 0

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    init {

        val viewConfiguration = ViewConfiguration.get(context)
        touchSlop=viewConfiguration.scaledTouchSlop
        minimumVelocity=viewConfiguration.scaledMinimumFlingVelocity
        maximumVelocity=viewConfiguration.scaledMaximumFlingVelocity
        val random= Random()
        (0..100).forEach{
            val textView= TextView(context)
            val color= Color.argb(0xff,random.nextInt(0xFF),random.nextInt(0xFF),random.nextInt(0xFF))
            val pressColor= Color.argb(0xff,Math.min(0xff, Color.red(color)+30),Math.min(0xff, Color.green(color)+30),Math.min(0xff, Color.blue(color)+30))
            val drawable= StateListDrawable()
            drawable.addState(intArrayOf(android.R.attr.state_empty), ColorDrawable(color))
            drawable.addState(intArrayOf(android.R.attr.state_pressed), ColorDrawable(pressColor))
            textView.backgroundDrawable=drawable
            textView.setOnClickListener {
                Toast.makeText(context,"点击${indexOfChild(it)}", Toast.LENGTH_SHORT).show()
            }
            textView.padding=36
            textView.text="Item$it"
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,20f)
            addView(textView,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT))
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var top=paddingTop
        (0..childCount-1).forEach{
            val childView=getChildAt(it)
            childView.layout(paddingLeft,top,width-paddingRight,top+childView.measuredHeight)
            top+=childView.measuredHeight
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if(!scroller.isFinished&&scroller.computeScrollOffset()){
            scrollTo(scroller.currX,scroller.currY)
            postInvalidate()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        when(action){
            MotionEvent.ACTION_DOWN->{
                lastMotionY=ev.y
                scroller.abortAnimation()
                postInvalidate()
            }
            MotionEvent.ACTION_MOVE->{
                val y=ev.y
                val offsetY=lastMotionY-y
                if(Math.abs(offsetY)>touchSlop){
                    isBeingDragged=true
                }
            }
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP->{

            }
        }
        return isBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        if(null==velocityTracker){
            velocityTracker= VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)
        when(action){
            MotionEvent.ACTION_DOWN->{
                lastMotionY=ev.y
            }
            MotionEvent.ACTION_MOVE->{
                val y=ev.y
                val offsetY=lastMotionY-y
                lastMotionY=y
                scrollBy(0,offsetY.toInt())
            }
            MotionEvent.ACTION_UP->{
                isBeingDragged=false
                var velocityTracker=velocityTracker
                if(null!=velocityTracker){
                    velocityTracker.computeCurrentVelocity(10000, maximumVelocity.toFloat())
                    val yVelocity = velocityTracker.yVelocity
                    if(Math.abs(yVelocity)>minimumVelocity){
                        scroller.fling(scrollX,scrollY,0, -yVelocity.toInt(),0,width,0,getChildAt(childCount-1).bottom)
                        ViewCompat.postInvalidateOnAnimation(this)
                    }
                    velocityTracker.recycle()
                }
                this.velocityTracker=null
            }
            MotionEvent.ACTION_CANCEL->{
                isBeingDragged=false
                velocityTracker?.recycle()
                velocityTracker=null
            }
        }
        return true
    }



}