package cz.androidsample.ui.widget.element.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import cz.androidsample.ui.widget.ArcTargetView
import cz.androidsample.ui.widget.element.PageLayout

/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
abstract class ElementAnimator(var duration: Long=300,repeatCount:Int=0,repeatMode:Int=ValueAnimator.REVERSE):Animator(repeatCount,repeatMode){
    //动画插值器
    var interpolator:Interpolator= LinearInterpolator()
}

/**
 * 透明转换
 */
class AlphaElementAnimator(val start:Float,val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode){
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "alpha",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 旋转
 */
class RotationElementAnimator(val start:Float,val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotation",start,end)
        setElementAnimator(this,animator)
        return animator
    }
}


/**
 * 旋转
 */
class RotationXElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotationX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 旋转
 */
class RotationYElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotationY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 百分比平移
 */
class TranslationXPercentAnimator(val end:Float, val start:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationX",parent.width* start, parent.width*end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 百分比平移
 */
class TranslationYPercentAnimator(val end:Float, val start:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationY", parent.height* start,parent.height*end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 横向平移
 */
class TranslationXElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 横向平移
 */
class TranslationXByElementAnimator(val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationX",target.translationX+end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 纵向平移
 */
class TranslationYElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount:Int=0,repeatMode:Int= ValueAnimator.REVERSE) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 相对纵向平移
 */
class TranslationYByElementAnimator(val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationY",target.translationY+end)
        setElementAnimator(this,animator)
        return animator
    }
}

class ScaleElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int):ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animatorSet= AnimatorSet()
        animatorSet.startDelay=delay
        val animator1 = ObjectAnimator.ofFloat(target, "scaleX",start, end)
        animator1.duration=duration
        animator1.interpolator=interpolator
        animator1.repeatMode=repeatMode
        animator1.repeatCount=repeatCount
        val animator2 = ObjectAnimator.ofFloat(target, "scaleY",start, end)
        animator2.duration=duration
        animator2.interpolator=interpolator
        animator2.repeatMode=repeatMode
        animator2.repeatCount=repeatCount
        animatorSet.playTogether(animator1,animator2)
        return animatorSet
    }
}

/**
 * 横向缩放
 */
class ScaleXElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int):ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "scaleX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 纵向缩放
 */
class ScaleYElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int):ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "scaleY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 纵向缩放
 */
class NumberElementAnimator(val start:Int, val end:Int,duration:Long,repeatCount: Int,repeatMode: Int,val action:((Int)->String)?=null):ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener {
            if(target is TextView){
                target.text= action?.invoke(it.animatedValue as Int) ?: it.animatedValue.toString()
            }
        }
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * arc轨道前进动画
 */
class ArcElementAnimator(val targetId:String,val fraction:Float,duration:Long,repeatCount: Int,repeatMode: Int,val action:((Int)->String)?=null):ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val elementView= parent.findElement(targetId)
        if(null==elementView){
            throw NullPointerException("Target View is null! elementId:$targetId")
        } else if(elementView !is ArcTargetView){
            throw NullPointerException("Target View must be ArcTargetView! elementId:$targetId")
        } else {
            //设置轨道进度
            elementView.setArcFraction(fraction)
            //设置起始位置
            setTargetArc(elementView, target)
            val animator1 = ObjectAnimator.ofFloat(target,"arcFraction",fraction,1f)
            val animator2 = ObjectAnimator.ofFloat(target,"arcFraction",0f,1f)
            animator1.addUpdateListener { setTargetArc(elementView, target) }
            animator2.addUpdateListener { setTargetArc(elementView, target) }
            setElementAnimator(this,animator2)
            val animatorSet=AnimatorSet()
            animatorSet.play(animator1).before(animator2)
            return animatorSet
        }
    }

    private fun setTargetArc(elementView: ArcTargetView, target: View) {
        val arcDegrees = elementView.getArcDegrees()
        val x = elementView.getArcX()
        val y = elementView.getArcY()
        //设置角度以及旋转位置
        target.rotation = arcDegrees
        target.x = elementView.left + x
        target.y = elementView.top + y
    }
}

/**
 * x移动
 */
class XElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "x",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}


/**
 * y移动
 */
class YElementAnimator(val start:Float, val end:Float,duration: Long,repeatCount: Int,repeatMode: Int) : ElementAnimator(duration,repeatCount,repeatMode) {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "y",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}


private inline fun setElementAnimator(elementAnimator: ElementAnimator,animator: ValueAnimator) {
    animator.repeatCount = elementAnimator.repeatCount
    animator.repeatMode = elementAnimator.repeatMode
    animator.duration = elementAnimator.duration
    animator.startDelay = elementAnimator.delay
    animator.interpolator = elementAnimator.interpolator
}