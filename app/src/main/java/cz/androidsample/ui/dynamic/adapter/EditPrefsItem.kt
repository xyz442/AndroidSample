package cz.androidsample.ui.dynamic.adapter

import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.ViewGroup
import android.widget.EditText
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.R
import org.jetbrains.anko.find

/**
 * Created by cz on 2017/12/1.
 */
class EditPrefsItem : PrefsListItem<String>() {
    private var text1=String()
    private var text2=String()
    override fun isValid(): Boolean {
        //条目一在6-10之间,文本2为号码
        return text1.length in (6..10) && Patterns.PHONE.matcher(text2).matches()
    }

    override fun getItemViewType(): Int = EDIT_ITEM

    override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(inflaterView(parent, R.layout.list_edit_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder,item:String, position: Int) {
        //假定编辑框只限定输入个数
        val editor1=holder.itemView.find<EditText>(R.id.editor1)
        val editor2=holder.itemView.find<EditText>(R.id.editor2)
        editor1.setText(text1)
        editor1.setText(text2)
        editor1.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?)=Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)=Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //记录文本,并通知变化
                text1=s.toString()
                notifyFormChanged()
            }
        })
        editor2.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?)=Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)=Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                text2=s.toString()
                notifyFormChanged()
            }
        })
    }
}