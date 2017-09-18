package cz.kotlinwidget

import android.app.Activity
import cz.androidsample.ui.FlipImgActivity
import cz.kotlinwidget.model.SampleItem
import cz.layoutmanagersample.ui.Sample1Activity
import cz.layoutmanagersample.ui.Sample2Activity
import cz.layoutmanagersample.ui.Sample3Activity

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
                item {
                    id = 2
                    title = "自定义LayoutManager"
                    desc = "以三个示例,分步骤,介绍一个最简单的LinearLayoutManager实现"

                    item {
                        pid=2
                        title = "自定义第一步"
                        desc = "初始化时,铺满整个列表"
                        clazz=Sample1Activity::class.java
                    }
                    item {
                        pid=2
                        title = "自定义第二步"
                        desc = "铺满列表后,随着滑动.自动加载控件"
                        clazz= Sample2Activity::class.java
                    }
                    item {
                        pid=2
                        title = "自定义第三步"
                        desc = "滑动后,回收剩余超出控件"
                        clazz= Sample3Activity::class.java
                    }
                }
            }
        }
    }
}