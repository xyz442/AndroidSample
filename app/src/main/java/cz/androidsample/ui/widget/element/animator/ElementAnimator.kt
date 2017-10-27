package cz.androidsample.ui.widget.element.animator

import android.view.animation.LinearInterpolator

/**
 * Created by cz on 2017/10/26.
 * 元素动画配置
 */
class ElementAnimator{
    companion object {
        val ALPHA=0x01
        val TRANSLATION_X=0x02
        val TRANSLATION_Y=0x04
        val TRANSLATION_Z=0x08
        val ROTATION=0x10
        val ROTATION_X=0x20
        val ROTATION_Y=0x40
        val SCALE_X=0x80
        val SCALE_Y=0x100
    }
    private var flag=0
    var duration=300
    var delay=0
    var alpha=1f
        set(value) {
            field=value
            flag+= ALPHA
        }
    var translationX:Float=0f
        set(value) {
            field=value
            flag+= TRANSLATION_X
        }
    var translationY:Float=0f
        set(value) {
            field=value
            flag+= TRANSLATION_Y
        }
    var translationZ:Float=0f
        set(value) {
            field=value
            flag+= TRANSLATION_Z
        }
    var rotation:Float=0f
        set(value) {
            field=value
            flag+= ROTATION
        }
    var rotationX:Float=0f
        set(value) {
            field=value
            flag+= ROTATION_X
        }
    var rotationY:Float=0f
        set(value) {
            field=value
            flag+= ROTATION_Y
        }
    var scaleX:Float=1f
        set(value) {
            field=value
            flag+= SCALE_X
        }
    var scaleY:Float=1f
        set(value) {
            field=value
            flag+= SCALE_Y
        }
    var interpolator= LinearInterpolator()
}