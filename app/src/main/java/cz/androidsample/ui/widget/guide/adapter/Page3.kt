package cz.androidsample.ui.widget.guide.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.Page

/**
 * Created by cz on 2017/11/1.
 */
class Page3(val context: Context) {
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(context)){
        layout {
            image(R.mipmap.page3_text1){
                id="text1"
                lparams {
                    margin(left=dp(80),right=dp(80))
                    verticalPercent=0.12f
                    alignRule=CENTER_HORIZONTAL
                }
                animator {
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.translationX=view.right*-offset
                    } else {
                        view.translationX=view.right*(1f-offset)
                    }
                }
            }
            image(R.mipmap.page3_text2){
                id="text2"
                lparams {
                    margin(left=dp(80),top=dp(8),right=dp(80))
                    align="text1"
                    alignRule=CENTER_HORIZONTAL or TOP_BOTTOM
                }
                animator {
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.translationX=view.right*(-offset*1.5f)
                    } else {
                        view.translationX=view.right*(1f-offset)*1.5f
                    }
                }
            }
            //横向导航线
            vline {
                id="line"
                lparams { verticalPercent=0.55f }
            }

            //卡片背景
            image(R.mipmap.page3_yellow_card){
                id="imageYellowCard"
                lparams {
                    align="imageCard"
                    margin(right = dp(20),bottom = dp(-46))
                    alignRule=CENTER_HORIZONTAL or BOTTOM_TOP
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f,600)).with(rotation(0f,180f,180))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.rotation=90*offset
                        view.alpha=1f-offset*4
                    } else {
                        view.rotation=90*(1f-offset)
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page3_credit_card){
                id="imageCard"
                lparams {
                    align="line"
                    bottomMargin=dp(42)
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _,current ->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            //信用分元素
            image(R.mipmap.page3_credit_bg){
                id="imageScoreBg"
                lparams {
                    align="imageCard"
                    margin(left=dp(-32),bottom = dp(-8))
                    alignRule=LEFT or BOTTOM
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page3_wheel_bg){
                id="imageWheel"
                lparams {
                    align="imageScoreBg"
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page3_credit_flag){
                id="imageWheelFlag"
                lparams {
                    align="imageScoreBg"
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600)).after(rotation(360f,0f,3000,-1,ValueAnimator.RESTART))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            text{
                id="imageCreditScore"
                font("500",sp(24),Color.RED)
                lparams {
                    align="imageScoreBg"
                    margin(left=dp(-4),top=dp(-8))
                    alignRule=CENTER
                }
                animator {
                    number(500,750,duration = 1000)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            text{
                id="imageCreditInfo"
                font("信用优秀",sp(9),Color.RED)
                lparams {
                    align="imageCreditScore"
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            image(R.mipmap.page3_doc){
                id="imageDoc"
                lparams {
                    align="imageCard"
                    margin(right=dp(-12),bottom = dp(-8))
                    alignRule=BOTTOM or RIGHT
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationY=view.height*offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationY=view.height*(1f-offset)
                    }
                }
            }

            image(R.mipmap.page3_girl1){
                id="imageGirl1"
                lparams {
                    align="imageCard"
                    margin(top=dp(-28),left = dp(-24))
                    alignRule=TOP_BOTTOM or LEFT_RIGHT
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            image(R.mipmap.page3_girl2){
                id="imageGirl2"
                lparams {
                    align="imageCard"
                    margin(top=dp(-12),right=dp(8))
                    alignRule=RIGHT_LEFT or TOP_BOTTOM
                }
                animator {
                    alpha=0f
                    play(alpha(0f,1f).duration(600))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            image(R.mipmap.page3_girl3) {
                id = "imageGirl3"
                lparams {
                    align = "imageCard"
                    alignRule = LEFT_RIGHT or CENTER_VERTICAL
                }
                animator {
                    alpha = 0f
                    play(alpha(0f, 1f).duration(600))
                }
                scrolled { view, _, offset, _, current ->
                    if (current) {
                        view.alpha = 1f - offset * 4
                    } else {
                        view.alpha = 1f - (1f - offset) * 4f
                    }
                }
            }
        }
    }
}