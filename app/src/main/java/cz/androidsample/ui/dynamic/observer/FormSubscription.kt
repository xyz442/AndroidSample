package cz.androidsample.ui.dynamic.observer

import cz.androidsample.ui.dynamic.adapter.PrefsListItem
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.functions.Action0
import rx.functions.Action1

/**
 * Created by cz on 2017/12/1.
 * 表单变化监听
 */
class FormSubscription(val items:MutableList<PrefsListItem<*>>){
    private val subscribe: Subscription
    private var onNext: Action1<in Boolean>?=null
    private var onError: Action1<Throwable>?=null
    private var onCompleted: Action0?=null
    private var invalidText:String?=null
    init {
        val observerItems = items.map { it.getObservable() }
        //这里监控只要有一个发送,联动发送所有变化
        items.forEach {
            it.onFormChanged { item->
                items.filter { it!=item }.forEach(PrefsListItem<*>::onNext)
            }
        }
        //汇总结果
        subscribe = Observable.combineLatest(observerItems, {
            it.map { it as Boolean }.reduce { acc, b -> acc and b }
        }).subscribe(object : Observer<Boolean> {
            override fun onError(e: Throwable?) {
                onError?.call(e)
            }

            override fun onNext(t: Boolean) {
                onNext?.call(t)
            }

            override fun onCompleted() {
                onCompleted?.call()
            }
        })
    }

    fun onSubscribe(onNext: Action1<in Boolean>){
        this.onNext=onNext
    }

    fun onError(onError: Action1<Throwable>){
        this.onError=onError
    }

    fun onComplete(onCompleted: Action0){
        this.onCompleted=onCompleted
    }

    fun unsubscribe()=subscribe.unsubscribe()

    fun isUnsubscribed()=subscribe.isUnsubscribed

    /**
     * 当前表单是否校验通过
     */
    fun isValid():Boolean{
        val invalidItem = items.find { !it.isValid() }
        //记录无效条目
        invalidText=invalidItem?.invalidText()
        //返回是否检测通过
        return null==invalidItem
    }

    /**
     * 无效提示信息
     */
    fun inValidText():String?=invalidText
}