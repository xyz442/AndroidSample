package cz.androidsample.ui.widget.guide.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.Page

/**
 * Created by cz on 2017/11/1.
 */
class Page3(val context: Context) {
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(2,context)){
        layout {
            text {
                id="text1"
                font= Font("分页3",sp(42), Color.RED)
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
            text {
                id="text2"
                font= Font("附加文本",sp(12), Color.WHITE)
                backgroundColor= Color.RED
                gravity= Gravity.LEFT
                lparams {
                    width=dp(100f)
                    align="text1"
                    //在text1的底部,并以它居中
                    alignRule= TOP_BOTTOM or CENTER_HORIZONTAL
                }
                animator {
                    translationX(0f,100f).duration(2000)
                }
            }
            animator { play("text1").after("text2") }
        }
    }
}