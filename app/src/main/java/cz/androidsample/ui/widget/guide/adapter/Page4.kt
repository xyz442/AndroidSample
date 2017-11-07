package cz.androidsample.ui.widget.guide.adapter

import android.content.Context
import cz.androidsample.R
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.Page

/**
 * Created by cz on 2017/11/1.
 */
class Page4(val context: Context){
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(context)){
        init {
            alpha=0f
        }
        layout {
            image(R.mipmap.page4_text1){
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
            image(R.mipmap.page4_text2){
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
                lparams { verticalPercent=0.62f }
            }

            image(R.mipmap.page4_phone){
                id="imagePhone"
                lparams {
                    align="line"
                    alignRule=CENTER_HORIZONTAL or BOTTOM
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f)).with(translationYBy(target.height*1f).duration(600))
                }
                scrolled { view, _, offset, _,current ->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            image(R.mipmap.page4_screen_text1){
                id="screenText1"
                lparams {
                    align="imagePhone"
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }
            image(R.mipmap.page4_screen_text2){
                id="screenText2"
                lparams {
                    align="screenText1"
                    margin(bottom= dp(4))
                    alignRule=BOTTOM_TOP or CENTER_HORIZONTAL
                }
                animator {
                    alpha=0f
                    translationX=-target.right*1f
                    play(alpha(0f,1f)).with(translationXBy(target.right*1f))
                }
                scrolled { view, _, offset, _ ,current->
                    if(current){
                        view.alpha=1f-offset*4
                    } else {
                        view.alpha=1f-(1f-offset)*4f
                    }
                }
            }

            image(R.mipmap.page4_boy){
                id="boy"
                lparams {
                    align="imagePhone"
                    margin(right=dp(-12),bottom = dp(-42))
                    alignRule=RIGHT_LEFT or BOTTOM
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
            image(R.mipmap.page4_gold){
                id="imageGold"
                lparams {
                    align="imagePhone"
                    margin(left = dp(-8),top=dp(8))
                    alignRule=LEFT or TOP_BOTTOM
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
            image(R.mipmap.page4_box1){
                id="imageBox"
                lparams {
                    align="imagePhone"
                    margin(top=dp(-16))
                    alignRule=TOP_BOTTOM or RIGHT
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

            image(R.mipmap.page4_girl){
                id="imageGirl"
                lparams {
                    align="imagePhone"
                    margin(right=dp(16),bottom=dp(32))
                    alignRule=RIGHT or BOTTOM
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

            image(R.mipmap.page4_ladder){
                id="imageLadder"
                lparams {
                    align="imageGirl"
                    margin(left=dp(-20),top=dp(-42))
                    alignRule=LEFT_RIGHT or TOP_BOTTOM
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

            animatorSet {
                play("imagePhone").after("screenText2").after("screenText1").
                        after("text1").with("text2").after("boy").with("imageBox").
                        with("imageGold").with("imageGirl").with("imageLadder")
            }
            pageSelected { view, position ->
                view.alpha=1f
            }
        }
    }
}