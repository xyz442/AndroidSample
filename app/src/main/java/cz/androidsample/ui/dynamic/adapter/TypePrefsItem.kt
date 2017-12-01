package cz.androidsample.ui.dynamic.adapter

import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.R
import org.jetbrains.anko.find
import rx.Observable

/**
 * Created by cz on 2017/12/1.
 */
class TypePrefsItem : PrefsListItem<List<String>>() {
    private var selectIndex=-1
    override fun isValid(): Boolean =-1!=selectIndex

    override fun getItemViewType(): Int = TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(inflaterView(parent, R.layout.list_type_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder,item:List<String>, position: Int) {
        val context=holder.itemView.context
        val typeLayout=holder.itemView.find<RadioGroup>(R.id.typeLayout)
        //初始化分类
        item.forEachIndexed { index, item ->
            val button=RadioButton(context)
            button.id=index
            button.text=item
            typeLayout.addView(button)
        }
        if(-1!=selectIndex){
            typeLayout.check(typeLayout.getChildAt(selectIndex).id)
        }
        typeLayout.setOnCheckedChangeListener { group, checkedId ->
            selectIndex=group.indexOfChild(group.find(checkedId))
            notifyFormChanged()
        }
    }
}