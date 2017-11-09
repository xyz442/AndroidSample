package cz.androidsample.ui.widget.element

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import cz.androidsample.DEBUG
import org.jetbrains.anko.Orientation
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource

/**
 * Created by cz on 2017/10/26.
 */
class LineElement(val orientation: Int) : Element<View>() {
    companion object {
        val VERTICAL=0
        val HORIZONTAL=1
    }

    override fun convertToView(context: Context): View=View(context)

    override fun initView(view: View) {
        //自动设置属性集
        when(orientation){
            HORIZONTAL->{
                layoutParams.width=1
                layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
            }
            VERTICAL->{
                layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height=1
            }
        }
        //初始化控件
        super.initView(view)
        if(DEBUG){
            view.backgroundColor=Color.RED
        } else {
            //使其没有背景
            view.backgroundDrawable=null
        }
    }
}
