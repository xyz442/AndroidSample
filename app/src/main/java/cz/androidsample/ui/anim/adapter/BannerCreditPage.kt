package cz.androidsample.ui.anim.adapter

import android.content.Context
import android.graphics.Typeface
import cz.androidsample.R
import cz.androidsample.ui.widget.element.*

/**
 * Created by cz on 2017/11/20.
 */
class BannerCreditPage(val context: Context){

    fun getPage()=with(Page(context)) {
        layout {
            text{
                id="textScore"
                font("500",sp(35),0xFFEEE4C6.toInt())
                lparams{
                    margin(left=dp(28),top=dp(33))
                    alignRule=TOP or LEFT
                }
                animator {
                    number(450,500,500)
                    translationX(target.left*1f,-target.width*1f,600L).delay(500)
                }
            }
//            text{
//                id="login"
//                backgroundResources= R.drawable.banner_item_selector
//                padding(dp(8),dp(12),dp(8),dp(12))
//                typeFace= Typeface.DEFAULT_BOLD
//                font("登陆查看信用分",sp(14),0xFFEEE4C6.toInt())
//                lparams{
//                    margin(left=dp(28))
//                }
//            }
            text{
                id="textCreditInfo"
                font("信用一般",sp(13),0xFFEEE4C6.toInt())
                lparams {
                    align="textScore"
                    margin(left=dp(2))
                    alignRule=LEFT or TOP_BOTTOM
                }
                animator {
                    translationX(target.left*1f,-target.width*1f,600L).delay(500)
                }

            }
            text{
                id="textInfo"
                font("信用分越高 额度越高 费率越低",sp(11),0xFFEEE4C6.toInt())
                lparams {
                    align="textCreditInfo"
                    topMargin=dp(10)
                    alignRule=LEFT or TOP_BOTTOM
                }
                animator {
                    translationX(target.left*1f,-target.width*1f,600L).delay(500)
                }
            }

        }
    }
}