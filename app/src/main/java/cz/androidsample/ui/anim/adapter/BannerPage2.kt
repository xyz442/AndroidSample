package cz.androidsample.ui.anim.adapter

import android.content.Context
import android.graphics.Color
import cz.androidsample.R
import cz.androidsample.ui.widget.element.*

/**
 * Created by cz on 2017/11/20.
 */
class BannerPage2(val context: Context){

    fun getPage()=with(Page(context)) {
        layout {
            text{
                id="textScore"
                font("450",sp(35),0xFFEEE4C6.toInt())
                lparams{
                    margin(left=dp(28),top=dp(33))
                    alignRule=TOP or LEFT
                }
                animator {
                    number(450,500,500)
                    x(target.left*1f,-target.width*1f,300L).delay(500)
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
                    x(target.left*1f,-target.width*1f,400L).delay(500)
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
                    x(target.left*1f,-target.width*1f,1000L).delay(500)
                }
            }
            image(R.mipmap.banner2_light){
                id="imageLight"
                lparams{
                    alignRule=RIGHT
                    rightMargin=dp(27)
                }
            }
            image(R.mipmap.banner2_bg){
                id="imageFlag"
                lparams{
                    align="imageLight"
                    topMargin=dp(-22)
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
            }
            image(R.mipmap.banner2_box1){
                id="imageBox1"
                lparams{
                    align="imageLight"
                    margin(top=dp(4))
                    alignRule=TOP or CENTER_HORIZONTAL
                }
            }
            image(R.mipmap.banner2_box2){
                id="imageBox2"
                lparams{
                    align="imageBox1"
                    alignRule=RIGHT_LEFT or TOP_BOTTOM
                }
            }
            image(R.mipmap.banner2_box3){
                id="imageBox3"
                lparams{
                    align="imageBox2"
                    margin(left=dp(12))
                    alignRule=LEFT_RIGHT or TOP_BOTTOM
                }
            }
            text{
                text="信用钱包让有信用的人更有尊严，守护信用，守护尊严！"
                lparams {
                    align="imageFlag"
                    leftMargin=dp(43)
                    alignRule=LEFT_RIGHT or CENTER_VERTICAL
                }
            }

        }
    }
}