package cz.androidsample.ui.widget

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.dsl.Page
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_guide_layout.*

@ToolBar
class GuideLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_layout)
        setTitle(intent.getStringExtra("title"))
        val page=getPage()
        guideContainer.addView(page,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        val view = page.findElement("text1")

//        //动画层级测试,dsl动画组控制原理也是如此
//        view.post {
//            val animator1=AnimatorSet()
//            val animator2=AnimatorSet()
//            val animator3=AnimatorSet()
//
//            val objectAnimator1=ObjectAnimator.ofFloat(view,"alpha",0f,1f)
//            objectAnimator1.duration = 3000
//
//            val objectAnimator2=ObjectAnimator.ofFloat(view,"translationX",200f)
//            objectAnimator2.duration = 2000
//            val objectAnimator3=ObjectAnimator.ofFloat(view,"translationY",200f)
//            objectAnimator3.duration = 2000
//
//            animator1.playSequentially(animator2)
//            animator2.playSequentially(animator3)
//            animator3.play(objectAnimator1).before(objectAnimator2).before(objectAnimator3)
//            animator1.start()
//
//        }
    }

    /**
     * 获得一个分页
     */
    private fun getPage()=with(Page(this)){
        layout {
            text {
                id="text1"
                font=Font("演示文本1",sp(26),Color.RED)
                padding=dp(12f)
                gravity=Gravity.CENTER
                backgroundColor=Color.GREEN
                lparams {
                    width=MATCH_PARENT
                    height=WRAP_CONTENT
                    leftMargin=dp(20f)
                    rightMargin=dp(20f)
                    topMargin=dp(20)
                    alignRule=CENTER
                }
                animator {
                    play(alpha(0f,1f).duration(3000)).
                            with(translationX(0f,200f).duration(2000)).
                            after(translationY(0f,200f).duration(2000))
                }
            }
            text {
                id="text2"
                font=Font("演示文本2",sp(12),Color.WHITE)
                backgroundColor=Color.RED
                gravity=Gravity.LEFT
                lparams {
                    width=dp(100f)
                    align="text1"
                    //在text1的底部,并以它居中
                    alignRule= TOP_BOTTOM or RIGHT
                }
                animator {
                    translationX(0f,100f).duration(2000)
                }
            }
            image{
                id="image1"
                drawableResource=R.mipmap.ic_launcher
                padding=dp(16f)
                lparams {
                    align="text2"
                    //在parent内,完全居中
                    alignRule=TOP_BOTTOM or LEFT
                }
                animator {
                    scaleX(0.8f,1.2f).duration(1000)
                }
            }
            animator { play("text1").after("text2").after("image1") }
        }
    }
}
