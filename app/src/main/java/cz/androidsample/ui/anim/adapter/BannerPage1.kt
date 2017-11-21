package cz.androidsample.ui.anim.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import cz.androidsample.R
import cz.androidsample.ui.widget.element.*

/**
 * Created by cz on 2017/11/20.
 */
class BannerPage1(val context: Context){

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
            relative {
                id="page1Layout"
                arcView(R.mipmap.banner1_arc){
                    id="imageArc"
                    arcFraction=0.1f
                    lparams{
                        margin(left=dp(27))
                        alignRule=RIGHT or CENTER_VERTICAL
                    }
//                animator {
//                    translationX(target.left*1f,-target.width*1f,1000)
//                }
                }
                image(R.mipmap.banner1_flag){
                    id="imageFlag"
                    lparams{
                        align="imageArc"
                        alignRule=CENTER
                    }
//                animator {
//                    x(target.left*1f,dp(27).toFloat(),1000)
//                }
                }
                image(R.mipmap.banner1_ball1){
                    id="imageBall1"
                    lparams{
                        align="imageArc"
                        arcAlign="imageArc"
                    }
                    animator {
                        arc("imageArc",0.1f,1000L,ValueAnimator.INFINITE)
                    }
                }
                image(R.mipmap.banner1_ball3){
                    id="imageBall2"
                    lparams{
                        align="imageFlag"
                        margin(top=dp(12),right=dp(8))
                        alignRule=TOP or RIGHT_LEFT
                    }
//                animator {
//                    x(target.left*1f,dp(27).toFloat(),1000)
//                    translationX(0f,-10f,1000,-1,ValueAnimator.REVERSE)
//                }

                }
                image(R.mipmap.banner1_ball2){
                    id="imageBall3"
                    lparams{
                        align="imageFlag"
                        margin(left=dp(-10),top=dp(-10))
                        alignRule=LEFT_RIGHT or TOP_BOTTOM
                    }
//                animator {
//                    x(target.left*1f,dp(27).toFloat(),1000)
//                    translationX(0f,-10f,1000,-1,ValueAnimator.REVERSE)
//                }
                }
                animator {
                    x(target.left*1f,dp(27)*1f,1000L).delay(500)
                }
                lparams {
                    width=dp(120)
                    height=MATCH_PARENT
                    margin(right=dp(20))
                    alignRule=RIGHT or CENTER_VERTICAL
                }
            }

            text{
                id="page1Text"
                text="信用钱包让有信用的人更有尊严，守护信用，守护尊严！"
                lparams {
                    align="page1Layout"
                    leftMargin=dp(43)
                    alignRule=LEFT_RIGHT or CENTER_VERTICAL
                }
                animator {
                    x(target.left*1f,dp(147)*1f,1000L).delay(500)
                }
            }

            animatorSet {
                play("textScore").with("textCreditInfo").with("textInfo").with("page1Layout").with("page1Text")
//                        with("imageArc").with("imageFlag").with("imageBall1").with("imageBall2").with("imageBall3")
            }

        }
    }
}