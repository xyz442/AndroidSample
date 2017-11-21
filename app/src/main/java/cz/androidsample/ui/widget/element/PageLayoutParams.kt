package cz.androidsample.ui.widget.element

import android.view.ViewGroup

/**
 * Created by cz on 2017/10/27.
 */
class PageLayoutParams(width:Int=WRAP_CONTENT,height:Int=WRAP_CONTENT): ViewGroup.MarginLayoutParams(width, height) {
    companion object {
        val PARENT=0
        val MATCH_PARENT=-1
        val WRAP_CONTENT=-2
    }
    val MATCH_PARENT=-1
    val WRAP_CONTENT=-2
    val PARENT_ID ="parent"
    val PARENT=0

    val LEFT=0x01
    val LEFT_RIGHT=0x02
    val TOP=0x04
    val TOP_BOTTOM=0x08
    val RIGHT=0x10
    val RIGHT_LEFT=0x20
    val BOTTOM=0x40
    val BOTTOM_TOP=0x80
    val CENTER_HORIZONTAL= LEFT or RIGHT
    val CENTER_VERTICAL=TOP or BOTTOM
    val CENTER= CENTER_HORIZONTAL or CENTER_VERTICAL
    // 横向百分比
    var horizontalPercent=0f
    // 纵向百分比
    var verticalPercent=0f
    //宽百分比
    var widthPercent=0f
    //高百分比
    var heightPercent=0f
    //边距
    var margin=0
        set(value) { margin(value,value,value,value) }
    //相对关系,默认依赖为父容器
    var align= PARENT_ID
    //轨迹依赖
    var arcAlign:String?=null
    var alignRule =LEFT or TOP
    //是否排版完成
    internal var isLayoutRequested=false

    fun margin(left:Int=0,top:Int=0,right:Int=0,bottom:Int=0){
        this.leftMargin =left
        this.topMargin =top
        this.rightMargin =right
        this.bottomMargin =bottom
    }
}
