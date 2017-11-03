package cz.androidsample.ui.widget.element

import android.content.Context
import android.view.View
import android.widget.ImageView
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource

/**
 * Created by cz on 2017/10/26.
 */
class ImageElement(var drawableResource:Int= -1) : Element<ImageView>() {
    //背景颜色
    var drawableColor= INVALID

    var scaleType:ImageView.ScaleType=ImageView.ScaleType.CENTER

    override fun convertToView(context: Context): View=ImageView(context)

    override fun initView(view: ImageView) {
        super.initView(view)
        view.scaleType=scaleType
        if(INVALID !=drawableResource){
            view.backgroundResource=drawableResource
        } else if(INVALID !=drawableColor){
            view.backgroundColor=drawableColor
        }
    }
}
