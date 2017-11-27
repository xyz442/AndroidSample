package cz.androidsample.imageloader

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import com.facebook.drawee.view.SimpleDraweeView
import cz.androidsample.R
import cz.androidsample.imageloader.callback.OnImageDisplayListener

/**
 * Created by cz on 6/30/17.
 */
class WrapperView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : SimpleDraweeView(context, attrs, defStyle){
    fun displayLocalRes(@DrawableRes res: Int) {
        display("res://" + context.packageName + "/" + res)
    }

    fun display(url: String?, aspectRatio: Float=0f, listener: OnImageDisplayListener?=null, @DrawableRes res: Int= R.mipmap.ic_crop_original_grey600) {
        if (null!=url&& ImageLoaderManager.interceptDisplay()) {
            ImageLoaderManager.displayImage(this,url,aspectRatio,listener)
        } else {
            ImageLoaderManager.displayImage(this,"res://" + context.packageName + "/" + res,aspectRatio,listener)
        }
    }
}
