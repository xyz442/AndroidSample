package cz.kotlinwidget

import android.app.Activity
import cz.androidsample.ui.FlipImgActivity
import cz.androidsample.ui.anim.Animator1Activity
import cz.androidsample.ui.anim.ArcAnimatorActivity
import cz.androidsample.ui.anim.BannerActivity
import cz.androidsample.ui.app.AppInfoActivity
import cz.androidsample.ui.dynamic.DynamicConfigListActivity
import cz.androidsample.ui.hierarchy.*
import cz.androidsample.ui.layout.ConstraintActivity
import cz.androidsample.ui.widget.GuideLayoutActivity
import cz.androidsample.ui.widget.MyImageViewActivity
import cz.androidsample.ui.widget.scroll.ViewScrollActivity
import cz.androidsample.ui.widget.seat.SeatTable2Activity
import cz.androidsample.ui.widget.waterfall.WaterfallActivity
import cz.androidsample.ui.widget.waterfall.other.bao.WaterFull1Activity
import cz.androidsample.ui.widget.waterfall.other.hui.TestFlowLayoutActivity
import cz.androidsample.ui.widget.waterfall.other.liang.LiangFunnyWaterFlowActivity1
import cz.androidsample.ui.widget.waterfall.other.liang.widget.LiangFunnyWaterFlowLayout1
import cz.androidsample.ui.widget.waterfall.other.tao.TaoFlowLayout1Activity
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
                item {
                    id = 3
                    title = "视图树控件"
                    desc = "检索出当前所有的控件信息"

                    item {
                        pid=3
                        title = "层级示例1"
                        desc = "以自定义View进行绘制,并处理排版,点击效果"
                        clazz= Hierarchy1Activity::class.java
                    }
                    item {
                        pid=3
                        title = "层级示例2"
                        desc = "以自定义View完成视图层绘制,并处理Zoom手势效果"
                        clazz= Hierarchy2Activity::class.java
                    }
                    item {
                        pid=3
                        title = "层级示例3"
                        desc = "自定义控件样式,以及排版"
                        clazz= Hierarchy3Activity::class.java
                    }
                    item {
                        pid=3
                        title = "层级示例4"
                        desc = "自定义控件样式,以及排版"
                        clazz= Hierarchy4Activity::class.java
                    }
                }
                item {
                    id = 4
                    title = "动画演示"
                    desc = "部分动画实例演示"
                    item {
                        pid=4
                        title = "多层级示例4"
                        clazz= Animator1Activity::class.java
                    }
                    item {
                        pid=4
                        title = "带旋转角度椭圆轨道动画"
                        clazz= ArcAnimatorActivity::class.java
                    }
                }

                item {
                    id = 5
                    title = "布局演示"
                    desc = "各种ViewGroup演示"

                    item {
                        pid=5
                        title = "ConstraintLayout"
                        desc = "演示ConstraintLayout使用"
                        clazz= ConstraintActivity::class.java
                    }
                    item {
                        pid=5
                        title = "GuideLayout"
                        desc = "演示GuideLayout使用,一个用dsl,简化的元素配置"
                        clazz= GuideLayoutActivity::class.java
                    }
                    item {
                        pid=5
                        title = "Banner"
                        desc = "演示GuideLayout制作一处banner实例"
                        clazz= BannerActivity::class.java
                    }
                    item {
                        pid=5
                        title = "动态表单"
                        desc = "用列表做一个动态表单,并校验"
                        clazz= DynamicConfigListActivity::class.java
                    }
                }
                item {
                    id = 6
                    title = "控件演示"
                    desc = "各种非正式控件演示列"

                    item {
                        pid=6
                        title = "ZoomImageView"
                        desc = "来源于FBReader的最精简的可缩放的图像控件演示"
                        clazz= MyImageViewActivity::class.java
                    }
                    item {
                        pid=6
                        title = "SeatTableView1"
                        desc = "一个电影选座控件,实现基本功能"
                        clazz= SeatTable1Activity::class.java
                    }
                    item {
                        pid=6
                        title = "SeatTableView2"
                        desc = "一个电影选座控件,进行了很多运算优化,用以测试亿级数据"
                        clazz= SeatTable2Activity::class.java
                    }
                    item {
                        pid=6
                        title = "控件流动"
                        desc = "控件滚动基础分享"
                        clazz= ViewScrollActivity::class.java
                    }
                    item {
                        id=60
                        pid=6
                        title = "瀑布流演示"
                        desc = "以一个瀑布流示例,演示列表涉及功能"

                        item {
                            pid=60
                            title = "瀑布流示例1"
                            desc = "演示控件排版,以及列表滚动"
                            clazz= WaterfallActivity::class.java
                        }
                        item {
                            id=660
                            pid=60
                            title = "其他成员演示"
                            desc = "其他成员瀑布滚演示"

                            item {
                                id=6660
                                pid=660
                                title = "bao-sample"
                                desc = "演示控件排版,以及列表滚动"
                                item{
                                    pid=6660
                                    title = "第一版"
                                    desc = "控件排版,测量,滚动"
                                    clazz=WaterFull1Activity::class.java
                                }
                            }
                            item {
                                id=6661
                                pid=660
                                title = "tao-sample"
                                desc = "演示控件排版,以及列表滚动"

                                item{
                                    pid=6661
                                    title = "第一版"
                                    desc = "控件排版,测量,滚动"
                                    clazz= TaoFlowLayout1Activity::class.java
                                }
                            }
                            item {
                                id=6662
                                pid=660
                                title = "liang-sample"
                                desc = "演示控件排版,以及列表滚动"
                                item{
                                    pid=6662
                                    title = "第一版"
                                    desc = "控件排版,测量,滚动"
                                    clazz= LiangFunnyWaterFlowActivity1::class.java
                                }
                            }
                            item {
                                id=6663
                                pid=660
                                title = "hui-sample"
                                desc = "演示控件排版,以及列表滚动"

                                item{
                                    pid=6663
                                    title = "第一版"
                                    desc = "控件排版,测量,滚动"
                                    clazz=TestFlowLayoutActivity::class.java
                                }
                            }
                        }

                    }
                }
                item {
                    id = 7
                    title = "应用信息"
                    desc = "检测应用信息,如cpu,内存等"
                    clazz=AppInfoActivity::class.java
                }
            }
        }
    }
}