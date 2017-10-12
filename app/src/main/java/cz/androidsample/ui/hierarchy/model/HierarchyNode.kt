package cz.androidsample.ui.hierarchy.model

import android.graphics.Rect
import android.view.View

/**
 * Created by cz on 2017/10/12.
 * 视图节点
 */
class HierarchyNode(val level:Int,name:String){
    //资源id
    var id:Int= View.NO_ID
    //id文本
    var entryName:String?=null
    //屏幕矩阵
    var rect= Rect()
    //描述
    var description:String?=null
}