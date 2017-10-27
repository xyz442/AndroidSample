package cz.androidsample.ui.widget.element

import android.support.constraint.ConstraintLayout
import cz.androidsample.ui.widget.element.animator.ElementAnimator
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet

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
 * 动画组扩展为执行完执行下一个
 */
inline fun ElementLayout.animator(init: ElementAnimator.()->Unit){
//    nextAnimator(ElementAnimator().apply(init))
}

/**
 * 动画组扩展为执行完执行下一个
 */
inline fun ElementAnimatorSet.next(init: ElementAnimator.()->Unit){
    nextAnimator(ElementAnimator().apply(init))
}

/**
 * 动画组扩展执行一个关联动画
 */
inline fun ElementAnimatorSet.with(init: ElementAnimator.()->Unit){
    withAnimator(ElementAnimator().apply(init))
}