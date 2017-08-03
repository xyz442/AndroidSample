package cz.kotlinwidget

import android.app.Activity
import cz.androidsample.ui.FlipImgActivity
import cz.kotlinwidget.model.SampleItem

/**
 * Created by Administrator on 2017/6/8.
 */
class FuncTemplate {
    companion object {
        val items = mutableListOf<SampleItem<Activity>>()
        val groupItems = mutableMapOf<Int, List<SampleItem<Activity>>>()
        fun item(closure: SampleItem<Activity>.() -> Unit) {
            items.add(SampleItem<Activity>().apply(closure))
        }

        //分组模板
        fun group(closure: () -> Unit) {
            closure.invoke()
            groupItems += items.groupBy { it.pid }
        }

        operator fun get(id: Int?) = groupItems[id]

        operator fun contains(id: Int?) = groupItems.any { it.key == id }

        init {
            group {
                item {
                    id = 1
                    title = "测试Bitmap翻转效果"
                    desc = "测试让指定绘制片断翻转的动画"
                    clazz=FlipImgActivity::class.java
                }
            }
        }
    }
}