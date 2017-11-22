package cz.androidsample.ui.anim.adapter

import android.animation.Animator
import android.animation.AnimatorSet
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
import kotlinx.android.synthetic.main.fragment_banner_page2.*
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


    fun numberChange(textView: TextView, start:Int, end:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener {
            val value=it.animatedValue as Int
            textView.text=value.toString()
        }
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun transitionX(view:View,toX:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator = ObjectAnimator.ofFloat(view,"translationX",view.x,toX*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun to(view:View,toX:Int,toY:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val animator1 = ObjectAnimator.ofFloat(view,"x",view.x,toX*1f)
        animator1.duration = duration
        animator1.repeatCount=repeatCount
        animator1.repeatMode=repeatMode
        val animator2 = ObjectAnimator.ofFloat(view,"y",view.y,toY*1f)
        animator2.duration = duration
        animator2.repeatCount=repeatCount
        animator2.repeatMode=repeatMode
        val animatorSet= AnimatorSet()
        animatorSet.playTogether(animator1,animator2)
        return animatorSet
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

    fun scale(view:View,scale:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART): Animator {
        val valueAnimator1 = ObjectAnimator.ofFloat(view,"scaleX",view.scaleX,scale*1f)
        val valueAnimator2 = ObjectAnimator.ofFloat(view,"scaleY",view.scaleY,scale*1f)
        valueAnimator1.duration = duration
        valueAnimator1.repeatCount=repeatCount
        valueAnimator1.repeatMode=repeatMode

        valueAnimator2.duration = duration
        valueAnimator2.repeatCount=repeatCount
        valueAnimator2.repeatMode=repeatMode
        val animatorSet=AnimatorSet()
        animatorSet.playTogether(valueAnimator1,valueAnimator2)
        return animatorSet
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
        val arcFraction = targetView.getArcFraction()
        val animator1 = ValueAnimator.ofFloat(arcFraction,1f)
        animator1.interpolator= LinearInterpolator()
        animator1.duration= (duration*(1f-arcFraction)).toLong()
        animator1.addUpdateListener {
            targetView.setArcFraction(it.animatedValue as Float)
            setTargetArc(targetView,view)
        }
        val animator2 = ValueAnimator.ofFloat(0f,1f)
        animator2.interpolator= LinearInterpolator()
        animator2.duration=duration
        animator2.repeatCount=repeatCount
        animator2.repeatMode=repeatMode
        animator2.addUpdateListener {
            targetView.setArcFraction(it.animatedValue as Float)
            setTargetArc(targetView,view)
        }
        val animatorSet=AnimatorSet()
        animatorSet.playSequentially(animator1,animator2)
        return animatorSet
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

    fun setTargetArcFraction(elementView: ArcTargetView, target: View,fraction:Float) {
        elementView.setArcFraction(fraction)
        //设置角度以及旋转位置
        target.rotation = elementView.getArcDegrees()
        val x = elementView.getArcX()
        val y = elementView.getArcY()
        target.x = elementView.left + x-target.width/2
        target.y = elementView.top + y-target.height/2
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

    fun cancelAnimator(animator:AnimatorSet?){
        val animator=animator?:return
        removeAnimatorAllListeners(animator)
        animator.cancel()
    }

    /**
     * 移除动画体所有监听事件
     */
    private fun removeAnimatorAllListeners(animatorSet: AnimatorSet){
        for (childAnimation in animatorSet.childAnimations) {
            childAnimation.removeAllListeners()
            if(childAnimation is AnimatorSet){
                removeAnimatorAllListeners(childAnimation)
            } else if(childAnimation is ValueAnimator){
                childAnimation.removeAllUpdateListeners()
            }
        }
    }

}