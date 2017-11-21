package cz.androidsample.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import cz.androidsample.DEBUG
import cz.androidsample.debugLog

/**
 * Created by cz on 2017/11/20.
 * 一个椭圆轨道目标控件,用于设定一些控件,按其轨道旋转
 * 绘制一个轨迹
 */
class ArcTargetView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : ImageView(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context?):this(context,null,0)
    private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
    private val path= Path()
    private val pathMeasure=PathMeasure(path,false)
    private val pos = FloatArray(2)
    private val tan = FloatArray(2)
    private var degrees=0f
    private var animatorDuration=3000L
    private var arcFraction =0f
    private var arcDegrees =0f
    init {
        paint.strokeWidth=1f
        paint.style=Paint.Style.STROKE
        paint.color= Color.RED
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path.reset()
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP){
            path.addOval(RectF(paddingLeft*1f,paddingTop*1f,(w-paddingRight)*1f,(h-paddingBottom)*1f),Path.Direction.CW)
        } else {
            path.addOval(paddingLeft*1f,paddingTop*1f,(w-paddingRight)*1f,(h-paddingBottom)*1f,Path.Direction.CW)
        }
        pathMeasure.setPath(path,false)
        //重新设置进度
        setArcFraction(arcFraction)
    }

    fun setDegrees(degrees:Float){
        if(degrees in (0f..360f)){
            this.degrees=degrees
            invalidate()
        }
    }

    fun getDegrees()=this.degrees

    fun getArcDegrees()=this.arcDegrees


    fun setArcDegrees(degrees:Float){
        this.arcDegrees=degrees
        invalidate()
    }


    fun setArcFraction(fraction:Float){
        this.arcFraction=fraction
        //当前行走进度
        val distance=pathMeasure.length* arcFraction
        pathMeasure.getPosTan(distance,pos,tan)
        //设置旋转角度
        val degrees=(Math.atan2(tan[1]*1.0, tan[0]*1.0) * 180 / Math.PI).toFloat()
        setArcDegrees(degrees)
    }

    fun getArcFraction()=arcFraction

    fun getArcX()=pos[0]

    fun getArcY()=pos[1]

    /**
     * 启动一个椭圆轨道测试动画
     */
    fun startArcTestAnimator(){
        val valueAnimator = ValueAnimator.ofFloat(1f)
        valueAnimator.interpolator=LinearInterpolator()
        valueAnimator.duration=animatorDuration
        valueAnimator.addUpdateListener {
            arcFraction =it.animatedFraction
            invalidate()
        }
        valueAnimator.start()
    }

    /**
     * 设置动画时间
     */
    fun setAnimatorDuration(duration:Long){
        this.animatorDuration=duration
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(DEBUG){
            canvas.save()
//            //绘测试轨道
            canvas.rotate(degrees,width/2f,height/2f)
            canvas.drawPath(path,paint)

            //绘制小球,沿轨道旋转
            val paint=Paint()
            paint.color=Color.GREEN

            //内层元素旋转
            val x=pos[0]
            val y=pos[1]
            val saveCount = canvas.saveLayer(x - 10, y - 5, x + 10, y + 5, paint, Canvas.ALL_SAVE_FLAG)
            canvas.rotate(arcDegrees,x,y)
            canvas.drawRect(x-10,y-5,x+10,y+5,paint)
            canvas.restoreToCount(saveCount)

            canvas.restore()
        }
    }
}