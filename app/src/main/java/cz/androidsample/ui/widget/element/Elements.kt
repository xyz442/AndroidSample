package cz.androidsample.ui.widget.element

import android.view.animation.Interpolator
import cz.androidsample.ui.widget.element.animator.*

/**
 * Created by cz on 2017/10/26.
 */
/**
 * 扩展text元素
 */
inline fun ElementLayout.text(init:TextElement.()->Unit){
    addElement(TextElement().apply(init))
}

/**
 * 扩展图片元素
 */
inline fun ElementLayout.image(init: ImageElement.()->Unit){
    addElement(ImageElement().apply(init))
}

/**
 * 扩展元素动画组控制
 */
inline fun ElementLayout.animator(init:ElementLayoutAnimatorSet.()->Unit){
    animator=ElementLayoutAnimatorSet(this).apply(init)
}

/**
 * 扩展图片元素
 */
inline fun Element<*>.animator(init: ElementAnimatorSet.()->Unit){
    val animatorSet = ElementAnimatorSet().apply(init)
    //记录id
    animatorSet.elementId=id
    //赋予动画元素
    animator=animatorSet
}


inline fun ElementAnimator.duration(time:Long):ElementAnimator{
    this.duration=time
    return this
}

inline fun ElementAnimator.delay(time:Long):ElementAnimator{
    this.delay=time
    return this
}

inline fun ElementAnimator.interpolator(interpolator: Interpolator):ElementAnimator{
    this.interpolator=interpolator
    return this
}

inline fun ElementAnimatorSet.alpha(start:Float,end:Float):ElementAnimator{
    val animator=AlphaElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotation(start:Float,end:Float):ElementAnimator{
    val animator=RotationElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotationX(start:Float,end:Float):ElementAnimator{
    val animator=RotationXElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotationY(start:Float,end:Float):ElementAnimator{
    val animator=RotationYElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

/**
 * 百分比横向平移
 * 如果from为0f,默认为0f,则从原地移到目录位置
 */
inline fun ElementAnimatorSet.translationXPercent(end:Float, from:Float=0f):ElementAnimator{
    val animator=TranslationYPercentAnimator(end, from)
    addAnimator(animator)
    return animator
}

/**
 * 百分比纵向平移
 * 如果from为0f,默认为0f,则从原地移到目录位置
 */
inline fun ElementAnimatorSet.translationYPercent(end:Float, from:Float=0f):ElementAnimator{
    val animator=TranslationYPercentAnimator(end, from)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationX(start: Float,end:Float):ElementAnimator{
    val animator=TranslationXElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationY(start: Float,end:Float):ElementAnimator{
    val animator=TranslationYElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scale(start:Float,end:Float):ElementAnimator{
    val animator=ScaleElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scaleX(start:Float,end:Float):ElementAnimator{
    val animator=ScaleXElementAnimator(start,end)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scaleY(start:Float,end:Float):ElementAnimator{
    val animator=ScaleYElementAnimator(start,end)
    addAnimator(animator)
    return animator
}
