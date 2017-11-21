package cz.androidsample.ui.widget.element

import android.content.Context
import android.view.View
import cz.androidsample.ui.widget.ArcTargetView

/**
 * Created by cz on 2017/10/26.
 */
class ArcElement(drawableResource:Int= -1) : ImageElement(drawableResource){
    var degrees=0f
    var animatorDuration=3000L
    var arcFraction =0f
    override fun convertToView(context: Context): View=ArcTargetView(context)

    override fun initView(view: android.widget.ImageView) {
        super.initView(view)
        if(view is ArcTargetView){
            view.setDegrees(degrees)
            view.setArcFraction(arcFraction)
            view.setAnimatorDuration(animatorDuration)
        }

    }
}