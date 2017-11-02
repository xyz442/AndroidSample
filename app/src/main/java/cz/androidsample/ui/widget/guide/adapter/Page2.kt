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
class Page2(val context: Context){
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(1,context)){
        layout {
            text {
                id="text1"
                font= Font("分页2",sp(42), Color.RED)
                padding=dp(12f)
                gravity= Gravity.CENTER
                backgroundColor= Color.GREEN
                lparams {
                    width=WRAP_CONTENT
                    height=WRAP_CONTENT
                    alignRule=CENTER
                }
                animator {
                    play(alpha(0f,1f).duration(1000)).
                            after(translationX(0f,200f).delay(3000).duration(1000)).
                            after(translationY(0f,200f).duration(1000))
                }
            }
            image{
                id="image1"
                drawableResource= R.mipmap.ic_launcher
                padding=dp(16f)
                lparams {
                    align="text1"
                    topMargin=dp(12)
                    //在parent内,完全居中
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
                animator {
                    scaleX(0.8f,1.2f).duration(1000)
                }
            }
            animator { play("text1").after("image1")}
        }
    }
}