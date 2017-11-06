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
            }

            image(R.mipmap.page4_screen_text1){
                id="screenText1"
                lparams {
                    align="imagePhone"
                    alignRule=CENTER
                }
            }
            image(R.mipmap.page4_screen_text2){
                id="screenText2"
                lparams {
                    align="screenText1"
                    margin(bottom= dp(4))
                    alignRule=BOTTOM_TOP or CENTER_HORIZONTAL
                }
            }

            image(R.mipmap.page4_boy){
                id="boy"
                lparams {
                    align="imagePhone"
                    margin(right=dp(-12),bottom = dp(-42))
                    alignRule=RIGHT_LEFT or BOTTOM
                }
            }
            image(R.mipmap.page4_gold){
                lparams {
                    align="imagePhone"
                    margin(left = dp(-8),top=dp(8))
                    alignRule=LEFT or TOP_BOTTOM
                }
            }
            image(R.mipmap.page4_box1){
                lparams {
                    align="imagePhone"
                    margin(top=dp(-16))
                    alignRule=TOP_BOTTOM or RIGHT
                }
            }

            image(R.mipmap.page4_girl){
                id="imageGirl"
                lparams {
                    align="imagePhone"
                    margin(right=dp(16),bottom=dp(32))
                    alignRule=RIGHT or BOTTOM
                }
            }

            image(R.mipmap.page4_ladder){
                id="imageLadder"
                lparams {
                    align="imageGirl"
                    margin(left=dp(-20),top=dp(-42))
                    alignRule=LEFT_RIGHT or TOP_BOTTOM
                }
            }


        }
    }
}