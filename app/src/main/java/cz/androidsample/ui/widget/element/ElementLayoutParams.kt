package cz.androidsample.ui.widget.element

/**
 * Created by cz on 2017/10/27.
 */
class ElementLayoutParams(var width:Int=WRAP_CONTENT, var height:Int=WRAP_CONTENT) {
    companion object {
        val MATCH_PARENT=0
        val WRAP_CONTENT=-2
    }
    val MATCH_PARENT=0
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
    val CENTER= LEFT or TOP or RIGHT or BOTTOM
    // 横向百分比
    var horizontalPercent=0f
    // 纵向百分比
    var verticalPercent=0f
    //边距
    var margin=0
    var leftMargin =0
    var topMargin =0
    var rightMargin =0
    var bottomMargin =0
    //相对关系,默认依赖为父容器
    var align= PARENT_ID
    var alignRule =LEFT or TOP
}
