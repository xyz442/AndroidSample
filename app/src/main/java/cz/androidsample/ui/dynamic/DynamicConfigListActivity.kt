package cz.androidsample.ui.dynamic

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

import cz.androidsample.R
import cz.androidsample.ui.dynamic.adapter.*
import kotlinx.android.synthetic.main.activity_dynamic_config_list.*
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.dynamic.observer.FormSubscription
import cz.volunteerunion.ui.ToolBarActivity
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast
import rx.functions.Action1


@ToolBar
class DynamicConfigListActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_config_list)
        setTitle(intent.getStringExtra("title"))
        //组建基础数据
        val items= mutableListOf<PrefsListItem<*>>()
        //分类1:标题
        items.add(TitlePrefsItem())
        //分类2:编辑框
        items.add(EditPrefsItem())
        //分类3:分类
        val typePrefsItem = TypePrefsItem()
        typePrefsItem.setItem(mutableListOf("分类1","分类2","分类3"))
        items.add(typePrefsItem)
        //分类4:服务地址
        for(index in (0..10)){
            items.add(ServerPrefsItem("Items$index"))
        }
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter= DynamicPrefsAdapter(this,items)

        //表单校验对象,管理所有变化操作,包括动态监听,手动校验.等
        val formSubscription= FormSubscription(items)
        //动态变化
        formSubscription.onSubscribe(Action1<Boolean> {
            applyButton2.isEnabled=it
        })
        //手动检测
        applyButton1.setOnClickListener {
            if(formSubscription.isValid()){
                Toast.makeText(this@DynamicConfigListActivity,"校验通过",Toast.LENGTH_SHORT).show()
            } else {
                val inValidText = formSubscription.inValidText()
                Toast.makeText(this@DynamicConfigListActivity,inValidText,Toast.LENGTH_SHORT).show()
            }
        }
    }
}
