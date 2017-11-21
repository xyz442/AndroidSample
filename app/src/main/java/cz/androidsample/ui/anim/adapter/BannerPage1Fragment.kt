package cz.androidsample.ui.anim.adapter

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import cz.androidsample.R
import cz.androidsample.ui.widget.ArcTargetView
import cz.androidsample.ui.widget.element.PageLayout
import kotlinx.android.synthetic.main.fragment_banner_page1.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

/**
 * Created by cz on 2017/11/21.
 */
class BannerPage1Fragment:Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banner_page1,container,false)
    }
    var x1:Float=0f

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            //编排动画
            //动态计划显示文本宽度
            val radialView=activity.find<ImageView>(R.id.radialView)
            //文本变化
            val animator1=numberChange(textScore,450,500)
            val animator2=arc(ball1,arcImage,2000,-1)
            val animator3=x(ball2,ball2.left+10,1000,-1,ValueAnimator.REVERSE)
            val animator4=x(ball3,ball3.left+10,1200,-1,ValueAnimator.REVERSE)
            //平移两个控件
            val animator5=x(textScore,-textScore.width,300)
            val animator6=x(textCreditInfo,-textCreditInfo.width,400)
            val animator7=x(textInfo,-textInfo.width,1000)
            val animator8=x(pageLayout1, page1Space.left)
            val animator9=scaleX(pageLayout1,1.2f)
            val animator10=scaleY(pageLayout1, 1.2f)
            val animator11=x(page1Text, (pageLayout1.left-page1Space.left+pageLayout1.width*0.2f).toInt())
            val animator12=translationDrawable(radialView,0.9f,0.1f,1000)
            val animator13=translationDrawable(radialView,0.1f,0.9f)

            //重新计算显示文本大小
            page1Text.layoutParams.width=page1Space.width
            page1Text.requestLayout()

            val animatorSet=AnimatorSet()
            animatorSet.play(animator1).with(animator3)
            animatorSet.play(animator1).with(animator4)
            animatorSet.play(animator2).with(animator5)
            animatorSet.play(animator1).before(animator5)
            animator11.startDelay=200
            animatorSet.play(animator5).with(animator6)
            animatorSet.play(animator5).with(animator7)
            animatorSet.play(animator5).with(animator8)
            animatorSet.play(animator5).with(animator9)
            animatorSet.play(animator5).with(animator10)
            animatorSet.play(animator5).with(animator11)
            animator12.startDelay=1000
            animatorSet.play(animator11).before(animator12)
            animatorSet.play(animator12).before(animator13)
            animatorSet.start()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }


    fun numberChange(textView:TextView,start:Int,end:Int): Animator {
        val valueAnimator = ValueAnimator.ofInt(start, end)
        valueAnimator.duration = 1000
        valueAnimator.addUpdateListener {
            val value=it.animatedValue as Int
            textView.text=value.toString()
        }
        return valueAnimator
    }

    fun x(view:View,toX:Int,duration:Long=1000,repeatCount:Int=0,repeatMode:Int=ValueAnimator.RESTART):Animator{
        val valueAnimator = ObjectAnimator.ofFloat(view,"x",view.x,toX*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun scaleX(view:View,scale:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int=ValueAnimator.RESTART):Animator{
        val valueAnimator = ObjectAnimator.ofFloat(view,"scaleX",view.scaleX,scale*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }
    fun scaleY(view:View,scale:Float,duration:Long=1000,repeatCount:Int=0,repeatMode:Int=ValueAnimator.RESTART):Animator{
        val valueAnimator = ObjectAnimator.ofFloat(view,"scaleY",view.scaleY,scale*1f)
        valueAnimator.duration = duration
        valueAnimator.repeatCount=repeatCount
        valueAnimator.repeatMode=repeatMode
        return valueAnimator
    }

    fun arc(view:View,targetView: ArcTargetView,duration: Long=1000,repeatCount:Int=0,repeatMode:Int=ValueAnimator.RESTART):Animator{
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

    fun translationDrawable(imageView:ImageView,start:Float,to:Float,duration: Long=2000,repeatCount:Int=0,repeatMode:Int=ValueAnimator.RESTART):Animator{
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