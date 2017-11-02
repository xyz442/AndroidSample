package cz.androidsample.ui.widget.guide.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import cz.androidsample.R
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.Page

/**
 * Created by cz on 2017/11/1.
 */
class Page1(val context: Context){
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(0,context)){
        layout {
            text {
                id="text1"
                font= Font("演示文本1",sp(26), Color.RED)
                padding=dp(12f)
                gravity= Gravity.CENTER
                backgroundColor= Color.GREEN
                lparams {
                    width=MATCH_PARENT
                    height=WRAP_CONTENT
                    leftMargin=dp(20f)
                    rightMargin=dp(20f)
                    topMargin=dp(20)
                    alignRule=CENTER
                }
                animator {
                    play(alpha(0f,1f).duration(1000)).
                            after(translationX(0f,200f).delay(3000).duration(1000)).
                            after(translationY(0f,200f).duration(1000))
                }
            }
            text {
                id="text2"
                font= Font("演示文本2",sp(12), Color.WHITE)
                backgroundColor= Color.RED
                gravity= Gravity.LEFT
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
                drawableResource= R.mipmap.ic_launcher
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
            animator { play("text1").after("text2").with("image1") }
        }
    }
}