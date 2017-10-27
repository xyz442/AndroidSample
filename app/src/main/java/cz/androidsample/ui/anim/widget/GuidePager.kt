package cz.androidsample.ui.anim.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by cz on 2017/10/26.
 */
class GuidePager(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr),GestureDetector.OnGestureListener{
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    //当前页数
    private var currentPage=0

    private val gestureDetector = GestureDetector(context, this)

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean =false

    override fun onDown(e: MotionEvent?): Boolean =true

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        translationX
        return true
    }

    override fun onLongPress(e: MotionEvent?) =Unit

}