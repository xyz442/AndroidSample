package cz.androidsample.ui.dynamic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager

import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.dynamic.adapter.*
import kotlinx.android.synthetic.main.activity_dynamic_config_list.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import rx.Observable
import rx.Observer
import rx.Subscriber
import rx.functions.Action1
import rx.subjects.ReplaySubject
import java.util.*
import rx.functions.Func2
import rx.android.view.ViewActions.setEnabled
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log


class DynamicConfigListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dynamic_config_list)

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
            items.add(ServerPrefsItem("Items1"))
        }
        //汇总结果
        Observable.combineLatest(items.map { it.getObservable() }) {
            //汇总结果
            items.map { it.isValid() }.reduce { acc, item -> acc and item }
        }.subscribe({
            applyButton.isEnabled=it
        },{},{})
        recyclerView.layoutManager=LinearLayoutManager(this)
        recyclerView.adapter= DynamicPrefsAdapter(this,items)

    }
}
