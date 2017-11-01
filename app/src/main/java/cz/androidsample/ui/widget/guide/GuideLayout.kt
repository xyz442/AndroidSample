package cz.androidsample.ui.widget.guide

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import cz.androidsample.ui.widget.element.dsl.Page
import org.jetbrains.anko.imageView

/**
 * Created by cz on 2017/10/26.
 * 引导页操作布局
 */
class GuideLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)



    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }

}