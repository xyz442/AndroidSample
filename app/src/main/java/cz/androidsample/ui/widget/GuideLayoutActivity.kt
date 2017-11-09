package cz.androidsample.ui.widget

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import cz.androidsample.R
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.guide.adapter.*
import cz.androidsample.ui.widget.guide.layoutmanager.StackLayoutManager
import kotlinx.android.synthetic.main.activity_guide_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class GuideLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_layout)

        val pageItems= mutableListOf<Page>()
        val page = Page1(this).getPage()
        val text1=page.view("text1")
        text1?.onClick {
            toast("点击!")
        }
        pageItems.add(page)
        pageItems.add(Page2(this).getPage())
        pageItems.add(Page3(this).getPage())
        pageItems.add(Page4(this).getPage())
        val adapter=MyGuideAdapter(pageItems)
        guideLayout.setLayoutManager(StackLayoutManager(this))
        guideLayout.setAdapter(adapter)
        //初始化引导背景
        initGuideBackground()
    }

    /**
     * 初始化引导背景
     */
    private fun initGuideBackground() {
        //添加背景布局
        guideLayout.backgroundLayout {
            //背景
            image(R.mipmap.guide_background) {
                id="image1"
                fillparams()
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
            }
            //横向导航线
            vline {
                id="line"
                lparams { verticalPercent=0.55f }
            }
            //背景房子
            image(R.mipmap.house_background) {
                id="image2"
                lparams(PageLayoutParams.MATCH_PARENT) {
                    align="line"
                    alignRule=BOTTOM_TOP
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
            }
            //中间传送带
            image {
                id="image3"
                backgroundResources = R.mipmap.road
                lparams(PageLayoutParams.MATCH_PARENT) {
                    align="line"
                    margin(left=dp(20),right=dp(20))
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
                animator {
                    alpha=0f
                    play(scale(0.2f,1f)).with(alpha(0f,1f))
                }
            }
            //左右云
            image(R.mipmap.cloud_1) {
                id="image4"
                lparams { verticalPercent = 0.1f }
                animator {
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
            }
            image(R.mipmap.cloud_1) {
                id="image5"
                lparams {
                    horizontalPercent = 1f
                    verticalPercent = 0.1f
                }
                animator {
                    translationX=target.width*1f
                    translationXBy(-target.width*1f)
                }
            }
            animatorSet {
                play("image1").delay(300).after("image2").delay(600).after("image3").after("image4").with("image5")
            }
        }
        //添加前景布局
        guideLayout.foregroundLayout {
            text {
                font("全新启航", sp(20), Color.WHITE)
                padding(left=dp(16),top=dp(12),right=dp(16),bottom = dp(12))
                backgroundResources = R.drawable.guide_button_selector
                lparams {
                    alignRule=CENTER_HORIZONTAL
                    verticalPercent=0.85f
                }
                animator {
                    alpha=0f
                    target.isClickable=false
                }
                //点击
                click { toast("点击开始!") }
                //Page滚动
                scrolled { v, position, offset, _,_->
                    v.alpha=0f
                    v.isClickable=false
                    if(position+1==guideLayout.pageCount-1||position==guideLayout.pageCount-1){
                        v.alpha=if(position==guideLayout.pageCount-1) 1f else offset
                        v.isClickable=true
                    }
                }
            }
        }
    }
}
