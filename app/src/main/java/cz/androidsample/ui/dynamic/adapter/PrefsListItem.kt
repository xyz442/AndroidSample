package cz.androidsample.ui.dynamic.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.debugLog
import rx.Observable
import rx.subjects.ReplaySubject



/**
 * Created by cz on 2017/12/1.
 * 抽象的列表对象,负责单个RecyclerView条目结果通知,以及数据适始化
 */
abstract class PrefsListItem<E>{

    companion object {
        const val TITLE_ITEM=0
        const val TYPE_ITEM=1
        const val SERVER_ITEM=2
        const val EDIT_ITEM=3

        //静态工厂,负责创建viewHolder
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder{
            return when(viewType){
                TITLE_ITEM->TitlePrefsItem().onCreateViewHolder(parent)
                TYPE_ITEM->TypePrefsItem().onCreateViewHolder(parent)
                SERVER_ITEM->ServerPrefsItem("Item").onCreateViewHolder(parent)
                else ->EditPrefsItem().onCreateViewHolder(parent)
            }
        }
    }
    private val subject: ReplaySubject<Boolean> = ReplaySubject.create<Boolean>()
    //表单变化监听
    private var formChange:((PrefsListItem<*>)->Unit)?=null
    private var item:E?=null

    fun setItem(item:E?){
        this.item=item
    }

    fun getObservable():Observable<Boolean> = subject

    fun onFormChanged(action:(PrefsListItem<*>)->Unit){
        this.formChange=action
    }

    /**
     * 通知表表单变化
     */
    protected fun notifyFormChanged(){
        onNext()
        formChange?.invoke(this)
    }

    fun onNext()=subject.onNext(isValid())

    protected fun inflaterView(parent:ViewGroup,layoutId:Int): View {
        return LayoutInflater.from(parent.context).inflate(layoutId,parent,false)
    }

    /**
     * 获得分类
     */
    abstract fun getItemViewType():Int

    abstract fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder

    open fun updateItem(data: Bundle)=Unit

    open fun onItemClick(view:View,item:PrefsListItem<E>,position: Int):Boolean=false
    /**
     * 本条目是否校验通过
     */
    abstract fun isValid():Boolean

    /**
     * 无效提示信息
     */
    abstract fun invalidText():String?

    /**
     * 绑定viewHolder对象
     */
    fun onBindViewHolder(holder: BaseViewHolder, position: Int){
        onBindViewHolder(holder,item,position)
    }

    /**
     * 绑定viewHolder对象
     */
    abstract fun onBindViewHolder(holder: BaseViewHolder,item:E?, position: Int)
}