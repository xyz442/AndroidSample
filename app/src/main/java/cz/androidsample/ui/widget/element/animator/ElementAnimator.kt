package cz.androidsample.ui.widget.element.animator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import cz.androidsample.ui.widget.element.PageLayout

/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
abstract class ElementAnimator:Animator(){
    //动画执行时间
    var duration:Long=300
    //动画插值器
    var interpolator:Interpolator= LinearInterpolator()
}

/**
 * 透明转换
 */
class AlphaElementAnimator(val start:Float,val end:Float) : ElementAnimator(){
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "alpha",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 旋转
 */
class RotationElementAnimator(val start:Float,val end:Float) : ElementAnimator() {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotation",start,end)
        setElementAnimator(this,animator)
        return animator
    }
}


/**
 * 旋转
 */
class RotationXElementAnimator(val start:Float, val end:Float) : ElementAnimator() {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotationX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 旋转
 */
class RotationYElementAnimator(val start:Float, val end:Float) : ElementAnimator() {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "rotationY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 百分比平移
 */
class TranslationXPercentAnimator(val end:Float, val start:Float) : ElementAnimator() {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationX",parent.width* start, parent.width*end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 百分比平移
 */
class TranslationYPercentAnimator(val end:Float, val start:Float) : ElementAnimator() {
    override fun convert(parent:PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationY", parent.height* start,parent.height*end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 横向平移
 */
class TranslationXElementAnimator(val start:Float, val end:Float) : ElementAnimator() {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 纵向平移
 */
class TranslationYElementAnimator(val start:Float, val end:Float) : ElementAnimator() {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "translationY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

class ScaleElementAnimator(val start:Float, val end:Float):ElementAnimator() {
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
class ScaleXElementAnimator(val start:Float, val end:Float):ElementAnimator() {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "scaleX",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

/**
 * 纵向缩放
 */
class ScaleYElementAnimator(val start:Float, val end:Float):ElementAnimator() {
    override fun convert(parent: PageLayout, target: View): android.animation.Animator {
        val animator = ObjectAnimator.ofFloat(target, "scaleY",start, end)
        setElementAnimator(this,animator)
        return animator
    }
}

private inline fun setElementAnimator(elementAnimator: ElementAnimator,animator: ObjectAnimator) {
    animator.repeatCount = elementAnimator.repeatCount
    animator.repeatMode = elementAnimator.repeatMode
    animator.duration = elementAnimator.duration
    animator.startDelay = elementAnimator.delay
    animator.interpolator = elementAnimator.interpolator
}