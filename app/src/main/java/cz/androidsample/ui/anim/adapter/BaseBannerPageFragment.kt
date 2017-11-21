package cz.androidsample.ui.anim.adapter

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import cz.androidsample.ui.widget.ArcTargetView
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Administrator on 2017/11/21.
 */
abstract class BaseBannerPageFragment:Fragment(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.onClick { startPageAnimator() }
        view.post { startPageAnimator() }
    }
    /**
     * 启动分页动画
     */
    abstract fun startPageAnimator()

    /**
     * 动画前置准备
     */
    abstract fun prePageAnimator()


    fun numberChange(textView: TextView, start:Int, end:Int): Animator {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener {
            val value=it.animatedValue as Int
            textView.text=value.toString()
        }
        return valueAnimator
    }

    fun transitionX(view:View,toX:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"translationX",view.x,toX*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun x(view:View,toX:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"x",view.x,toX*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }
    fun y(view:View,toY:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"y",view.y,toY*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun scaleX(view:View,scale:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"scaleX",view.scaleX,scale*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }
    fun scaleY(view:View,scale:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"scaleY",view.scaleY,scale*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun alpha(view:View,alpha:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"alpha",view.alpha,alpha)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun arc(view:View, targetView: ArcTargetView, duration: Long=1000, repeatCount:Int=0, repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ValueAnimator.ofFloat(1f)
        valueAnimator.interpolator= LinearInterpolator()
        valueAnimator.duration=duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        valueAnimator.addUpdateListener {
            targetView.setArcFraction(it.animatedFraction)
            setTargetArc(targetView,view)
        }
        return valueAnimator
    }

    fun translationDrawable(imageView: ImageView, start:Float, to:Float, duration: Long=2000, repeatCount:Int=0, repeatMode:Int= ValueAnimator.RESTART): Animator {
        val drawable=imageView.drawable
        val valueAnimator = ValueAnimator.ofFloat(start,to)
        if(null!=drawable&&drawable is GradientDrawable){
            valueAnimator.interpolator= LinearInterpolator()
            valueAnimator.duration=duration
            valueAnimator.repeatCount=repeatCount
            valueAnimator.repeatMode=repeatMode
            valueAnimator.addUpdateListener {
                val animatedValue = it.animatedValue as Float
                drawable.setGradientCenter(animatedValue,0.2f)
                drawable.invalidateSelf()
            }
        }
        return valueAnimator
    }

    private fun setTargetArc(elementView: ArcTargetView, target: View) {
        val arcDegrees = elementView.getArcDegrees()
        val x = elementView.getArcX()
        val y = elementView.getArcY()
        //设置角度以及旋转位置
        target.rotation = arcDegrees
        target.x = elementView.left + x-target.width/2
        target.y = elementView.top + y-target.height/2
    }

}