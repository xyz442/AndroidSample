package cz.androidsample.ui.widget.guide.adapter

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
class Page1(val context: Context){
    /**
     * 获得一个分页
     */
    fun getPage()=with(Page(context)){
        layout {
            image(R.mipmap.page1_text1){
                id="imageText1"
                lparams {
                    margin(left=dp(80),right=dp(80))
                    verticalPercent=0.12f
                    alignRule=CENTER_HORIZONTAL
                }
                animator {
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
            }
            image(R.mipmap.page1_text2){
                id="imageText2"
                lparams {
                    margin(left=dp(80),top=dp(8),right=dp(80))
                    align="imageText1"
                    alignRule=CENTER_HORIZONTAL or TOP_BOTTOM
                }
                animator {
                    translationX=-target.right*1f
                    translationXBy(target.right*1f)
                }
            }
            //横向导航线
            vline {
                id="line"
                lparams { verticalPercent=0.55f }
            }

            image(R.mipmap.page1_wallet){
                id="imageWallet"
                lparams {
                    align="line"
                    bottomMargin=dp(100)
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f).duration(600)).with(translationYBy(target.height*1f).duration(600))
                }
            }
            image(R.mipmap.page1_square){
                id="imageSquare"
                lparams {
                    margin(left=dp(-8),top=dp(8))
                    align="imageWallet"
                    alignRule=TOP_BOTTOM or LEFT
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
            }
            image(R.mipmap.page_girl2){
                id="imageGirl1"
                lparams {
                    align="imageSquare"
                    margin(bottom = dp(-12))
                    alignRule=BOTTOM_TOP or LEFT
                }
                animator {
                    alpha=0f
                    alpha(0f,1f)
                }
            }
            image(R.mipmap.page_girl1){
                lparams {
                    align="imageWallet"
                    margin(left=dp(-16),bottom = dp(-32))
                    alignRule=LEFT_RIGHT or BOTTOM
                }
                animator {
                    alpha=0f
                    translationX=target.width*1f
                    translationY=target.width*1f
                    translationX(0f,target.left*1f)
                    translationY(0f,target.top*1f)
                }
            }
            image(R.mipmap.page1_boy){
                lparams {
                    align="imageWallet"
                    margin(top=dp(20),right=dp(24))
                    alignRule=TOP_BOTTOM or RIGHT
                }
            }

            image(R.mipmap.page1_gold1){
                lparams {
                    align="imageGirl1"
                    margin(right=dp(-24),bottom= dp(-20))
                    alignRule=BOTTOM_TOP or RIGHT
                }
            }
            image(R.mipmap.page1_gold2){
                id="imageGold2"
                lparams {
                    align="imageWallet"
                    margin(top=dp(32),right=dp(16))
                    alignRule=TOP or RIGHT
                }
            }
            image(R.mipmap.page1_gold3){
                lparams {
                    align="imageGold2"
                    alignRule=TOP_BOTTOM or RIGHT
                }
            }
            image(R.mipmap.page1_gold4){
                lparams {
                    align="imageWallet"
                    margin(left=dp(-4),top=dp(-4))
                    alignRule=TOP or LEFT_RIGHT
                }
            }
            image(R.mipmap.page1_gold5){
                lparams {
                    align="imageWallet"
                    margin(left=dp(-4))
                }
            }
            animatorSet {
                play("imageText1").delay(600).
                        after("imageText2").after("imageWallet").
                        after("imageSquare")
            }
        }
    }
}