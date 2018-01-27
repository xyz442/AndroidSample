package cz.androidsample.ui.dynamic.adapter

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.cz.recyclerlibrary.adapter.BaseViewAdapter
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import kotlinx.android.synthetic.main.simple_text_item.*
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by cz on 2017/12/1.
 */
class DynamicPrefsAdapter(context: Context, items: List<PrefsListItem<*>>?) : BaseViewAdapter<PrefsListItem<*>>(context, items) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        //创建初始viewHolder
        return PrefsListItem.onCreateViewHolder(parent,viewType)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        //绑定数据
        val item=getItem(position)
        item.onBindViewHolder(holder,position)
    }

    fun updateItem(position: Int,data: Bundle){
        this[position].updateItem(data)
        notifyItemChanged(position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item.getItemViewType()
    }
}