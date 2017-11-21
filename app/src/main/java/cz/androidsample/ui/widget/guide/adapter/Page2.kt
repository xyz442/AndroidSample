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
class Page2(val context: Context){
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(context)){
        layout {
            image(R.mipmap.page2_text1){
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
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.translationX=view.right*-offset
                    } else {
                        view.translationX=view.right*(1f-offset)
                    }
                }
            }
            image(R.mipmap.page2_text2){
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
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.translationX=view.right*(-offset*1.5f)
                    } else {
                        view.translationX=view.right*(1f-offset)*1.5f
                    }
                }
            }
            //横向导航线
            vline {
                id="line2"
                lparams { verticalPercent=0.55f }
            }

            image(R.mipmap.page2_box1){
                id="box1"
                lparams {
                    align="imageCar"
                    bottomMargin=dp(24)
                    alignRule=CENTER_HORIZONTAL or BOTTOM_TOP
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f,600)).
                            after(translationY(0f,-20f,1200,-1,ValueAnimator.REVERSE).delay(1000))
                }
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationY=view.height*-offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationY=view.height*(1f-offset)
                    }
                }
            }

            image(R.mipmap.page2_box2){
                id="box2"
                lparams {
                    align="box1"
                    topMargin=dp(4)
                    alignRule=TOP_BOTTOM or LEFT_RIGHT
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f,600)).
                            after(translationY(0f,-20f,1400,-1,ValueAnimator.REVERSE).delay(1000))
                }
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationY=view.height*-offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationY=view.height*(1f-offset)
                    }
                }
            }

            image(R.mipmap.page2_box3){
                id="box3"
                lparams {
                    align="imageCar"
                    margin(top=dp(-8),right=dp(60))
                    alignRule=TOP or CENTER_HORIZONTAL
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f,600)).
                            after(translationY(0f,-20f,1600,-1,ValueAnimator.REVERSE).delay(1000))
                }
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationY=view.height*-offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationY=view.height*(1f-offset)
                    }
                }
            }

            image(R.mipmap.page2_box4){
                id="box4"
                lparams {
                    align="imageCar"
                    bottomMargin=dp(48)
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f,600))
                }
                scrolled { view, position, offset, _, current ->
                    debugLog("Page2:$position $offset $current")
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationY=view.height*-offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationY=view.height*(1f-offset)
                    }
                }
            }
            image(R.mipmap.page2_car){
                id="imageCar"
                lparams {
                    align="line2"
                    bottomMargin=dp(42)
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f).duration(600)).with(translationYBy(target.height*1f).duration(600))
                }
                scrolled { view, _, offset, _, current ->
                    if(current){
                        view.alpha=1f-offset*4
                        view.translationX=view.width*-offset
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                        view.translationX=view.width*(1f-offset)
                    }
                }
            }

            image(R.mipmap.page2_boy){
                id="imageBoy"
                lparams {
                    align="imageCar"
                    margin(bottom = dp(-8))
                    alignRule=LEFT or BOTTOM
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page2_girl2){
                id="imageGirl1"
                lparams {
                    align="imageCar"
                    margin(left = dp(-48),bottom = dp(56))
                    alignRule=LEFT_RIGHT or BOTTOM
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page2_girl1){
                id="imageGirl2"
                lparams {
                    align="imageCar"
                    margin(left=dp(-16),top=dp(-112))
                    alignRule=RIGHT or TOP_BOTTOM
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            animatorSet {
                animatorDuration=5*1000
                play("imageCar").after("text1").after("text2").
                        after("box1").with("box2").with("box3").
                        with("box4").after("imageBoy").with("imageGirl1").after("imageGirl2")
            }
        }
    }
}