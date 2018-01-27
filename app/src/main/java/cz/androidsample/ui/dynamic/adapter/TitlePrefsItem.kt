package cz.androidsample.ui.dynamic.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.R
import rx.Observable

/**
 * Created by cz on 2017/12/1.
 */
class TitlePrefsItem : PrefsListItem<String>() {

    override fun getItemViewType(): Int = TITLE_ITEM

    override fun isValid(): Boolean=true
    override fun invalidText(): String? =null

    override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(inflaterView(parent, R.layout.list_title_item))
    }


    override fun onBindViewHolder(holder: BaseViewHolder,item:String?, position: Int) {
        holder.itemView
    }

    override fun onItemClick(view: View, item: PrefsListItem<String>, position: Int): Boolean {
        Toast.makeText(view.context,"Clicked!",Toast.LENGTH_SHORT).show()
        return true
    }
}