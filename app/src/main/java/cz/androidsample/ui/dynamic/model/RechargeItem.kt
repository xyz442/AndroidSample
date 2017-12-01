package cz.androidsample.ui.dynamic.model

/**
 * Created by cz on 2017/12/1.
 */
class RechargeItem{
    //充值标题
    var title:String?=null
    //充值分类
    var typeItems= mutableListOf<String>()
    //充值帐号
    var accountItems= mutableListOf<String>()
    //游戏区服
    var serverItems= mutableListOf<String>()
}