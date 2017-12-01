package cz.androidsample.ui.dynamic.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import com.cz.recyclerlibrary.adapter.BaseViewHolder
import cz.androidsample.R
import org.jetbrains.anko.find
import rx.Observable

/**
 * Created by cz on 2017/12/1.
 */
class ServerPrefsItem : PrefsListItem<String>() {
    private var text=String()

    override fun isValid(): Boolean =4<=text.length

    override fun getItemViewType(): Int = SERVER_ITEM

    override fun onCreateViewHolder(parent: ViewGroup): BaseViewHolder {
        return BaseViewHolder(inflaterView(parent, R.layout.list_server_item))
    }

    override fun onBindViewHolder(holder: BaseViewHolder,item:String, position: Int) {
        val editor = holder.itemView.find<EditText>(R.id.editor)
        editor.setText(text)
        editor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?)=Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)=Unit
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                text=s.toString()
                notifyFormChanged()
            }
        })
    }
}
