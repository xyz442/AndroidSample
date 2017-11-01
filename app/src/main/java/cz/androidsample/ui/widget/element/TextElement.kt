package cz.androidsample.ui.widget.element

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by cz on 2017/10/26.
 */
class TextElement: Element<TextView>() {
    var textRes:Int= INVALID
    var text:String?=null
//    文字颜色
    var textColor:Int= Color.BLACK
//    文字尺寸
    var textSize:Float=0f
//    文本方向
    var gravity:Int=Gravity.CENTER
    //文字样式
    var typeFace:Typeface=Typeface.DEFAULT
    //字体字式
    var font:Font?=null

    override fun convertToView(context: Context): View = TextView(context)

    override fun initView(view: TextView) {
        super.initView(view)
        //设置文字
        if(INVALID!=textRes){
            view.setText(textRes)
        } else if(null!=text){
            view.text = text
        }
        //设置字体属性
        val font=font
        if(null!=font){
            view.text = font.text
            view.setTextColor(font.textColor)
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,font.textSize)
        } else {
            //设置字体颜色
            view.setTextColor(textColor)
            //设置字体大小
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
        }
        view.typeface=typeFace
        //设置文字方向
        view.gravity=gravity
    }
}