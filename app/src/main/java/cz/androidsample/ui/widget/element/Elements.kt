package cz.androidsample.ui.widget.element

import android.animation.ValueAnimator
import android.view.animation.Interpolator
import cz.androidsample.ui.widget.element.animator.*
import cz.androidsample.ui.widget.guide.GuideLayout

/**
 * Created by cz on 2017/10/26.
 */
val TAG="Guide"

/**
 * 扩展text元素
 */
inline fun ElementLayout.text(init:TextElement.()->Unit){
    addElement(TextElement().apply(init))
}

/**
 * 扩展图片元素
 */
inline fun ElementLayout.image(resource:Int=-1, init: ImageElement.()->Unit){
    addElement(ImageElement(resource).apply(init))
}


/**
 * 横向线元素,可用做导航线
 */
inline fun ElementLayout.hline(init: LineElement.()->Unit){
    addElement(LineElement(LineElement.HORIZONTAL).apply(init))
}

/**
 * 纵向线元素,可用做导航线
 */
inline fun ElementLayout.vline(init: LineElement.()->Unit){
    addElement(LineElement(LineElement.VERTICAL).apply(init))
}

/**
 * 扩展元素动画组控制
 */
fun ElementLayout.animator(init:ElementLayoutAnimatorSet.()->Unit){
//    animator=ElementLayoutAnimatorSet(this).apply(init)
    animatorInit=init
}

/**
 * 扩展图片元素
 */
fun Element<*>.animator(init: ElementAnimatorSet.()->Unit){
    animatorInit=init
}

/**
 * 扩展引导布局前景
 */
inline fun GuideLayout.foregroundLayout(init: ElementLayout.()->Unit){
    val pageLayout= PageLayout(context)
    val elementLayout =ElementLayout(context).apply(init)
    //转换动画
    val animator = elementLayout.animator?.convert(pageLayout, pageLayout)
    //初始化
    pageLayout.addElementLayout(elementLayout)
    //设置背景布局
    setGuideForegroundLayout(pageLayout)
    //执行动画
    post { animator?.start() }
}

/**
 * 扩展引导背景布局
 */
inline fun GuideLayout.backgroundLayout(init: ElementLayout.()->Unit){
    val pageLayout= PageLayout(context)
    val elementLayout = ElementLayout(context).apply(init)
    //转换动画
    val animator = elementLayout.animator?.convert(pageLayout, pageLayout)
    //初始化
    pageLayout.addElementLayout(elementLayout)
    //设置背景布局
    setGuideBackgroundLayout(pageLayout)
    //执行动画
    post { animator?.start() }
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

inline fun ElementAnimatorSet.alpha(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=AlphaElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotation(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=RotationElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotationX(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=RotationXElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.rotationY(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=RotationYElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

/**
 * 百分比横向平移
 * 如果from为0f,默认为0f,则从原地移到目录位置
 */
inline fun ElementAnimatorSet.translationXPercent(end:Float, from:Float=0f,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationYPercentAnimator(end, from,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

/**
 * 百分比纵向平移
 * 如果from为0f,默认为0f,则从原地移到目录位置
 */
inline fun ElementAnimatorSet.translationYPercent(end:Float, from:Float=0f,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationYPercentAnimator(end, from,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationX(start: Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationXElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationXBy(end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationXByElementAnimator(end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationY(start: Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationYElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.translationYBy(end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=TranslationYByElementAnimator(end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scale(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=ScaleElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scaleX(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=ScaleXElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}

inline fun ElementAnimatorSet.scaleY(start:Float,end:Float,repeatCount:Int=0,repeatMode:Int= ValueAnimator.RESTART):ElementAnimator{
    val animator=ScaleYElementAnimator(start,end,repeatCount,repeatMode)
    addAnimator(animator)
    return animator
}
