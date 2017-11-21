package cz.androidsample.ui.widget.element

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
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

    fun font(text:String?=null, textSize:Float=0f, textColor: Int =0){
        if(!TextUtils.isEmpty(text)){
            this.text=text
        }
        if(0f!=textSize){
            this.textSize=textSize
        }
        if(0!=textColor){
            this.textColor=textColor.toInt()
        }
    }


    override fun convertToView(context: Context): View = TextView(context)

    override fun initView(view: TextView) {
        super.initView(view)
        //设置文字
        if(INVALID!=textRes){
            view.setText(textRes)
        } else if(null!=text){
            view.text = text
        }
        //设置字体颜色
        view.setTextColor(textColor)
        //设置字体大小
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize)
        view.typeface=typeFace
        //设置文字方向
        view.gravity=gravity
    }
}