package cz.androidsample.ui.dynamic.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
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

    val textWatcher1=object :TextWatcher{
        override fun afterTextChanged(s: Editable?)=Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)=Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //记录文本,并通知变化
            text1=s.toString()
            notifyFormChanged()
        }
    }

    val textWatcher2=object :TextWatcher{
        override fun afterTextChanged(s: Editable?)=Unit
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)=Unit
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            text2=s.toString()
            notifyFormChanged()
        }
    }
    override fun isValid(): Boolean {
        //条目一在6-10之间,文本2为号码
        return text1.length in (6..10) && Patterns.PHONE.matcher(text2).matches()
    }

    override fun invalidText(): String? {
        var text:String?=null
        if(TextUtils.isEmpty(text1)){
            text="文本1为空!"
        } else if(TextUtils.isEmpty(text2)){
            text="文本2为空!"
        } else if(text1.length !in (6..10)){
            text="文本1必须大于6个,且小于10个元素!"
        } else if(!Patterns.PHONE.matcher(text2).matches()){
            text="文本2必须为电话号码!"
        }
        return text
    }

    override fun getItemViewType(): Int = EDIT_ITEM

    override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(inflaterView(parent, R.layout.list_edit_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder,item:String?, position: Int) {
        //假定编辑框只限定输入个数
        val editor1=holder.itemView.find<EditText>(R.id.editor1)
        val editor2=holder.itemView.find<EditText>(R.id.editor2)
        removeTextWatcher(editor1)
        removeTextWatcher(editor2)

        editor1.setText(text1)
        editor1.setSelection(text1.length)
        editor1.tag=textWatcher1
        editor1.addTextChangedListener(textWatcher1)

        editor2.setText(text2)
        editor2.setSelection(text2.length)
        editor2.tag=textWatcher1
        editor2.addTextChangedListener(textWatcher2)
    }

    private fun removeTextWatcher(editor: EditText) {
        val watcher = editor.tag as? TextWatcher
        if (null != watcher) {
            editor.removeTextChangedListener(watcher)
        }
    }
}