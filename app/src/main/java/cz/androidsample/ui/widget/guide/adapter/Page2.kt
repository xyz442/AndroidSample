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
            }
            //横向导航线
            vline {
                id="line"
                lparams { verticalPercent=0.55f }
            }

            image(R.mipmap.page2_box1){
                id="box1"
                lparams {
                    align="imageCar"
                    bottomMargin=dp(24)
                    alignRule=CENTER_HORIZONTAL or BOTTOM_TOP
                }
            }

            image(R.mipmap.page2_box2){
                id="box2"
                lparams {
                    align="box1"
                    topMargin=dp(4)
                    alignRule=TOP_BOTTOM or LEFT_RIGHT
                }
            }

            image(R.mipmap.page2_box3){
                id="box3"
                lparams {
                    align="imageCar"
                    margin(top=dp(-8),right=dp(60))
                    alignRule=TOP or CENTER_HORIZONTAL
                }
            }

            image(R.mipmap.page2_box4){
                lparams {
                    align="imageCar"
                    bottomMargin=dp(48)
                    alignRule=CENTER
                }
            }
            image(R.mipmap.page2_car){
                id="imageCar"
                lparams {
                    align="line"
                    bottomMargin=dp(42)
                    alignRule=CENTER
                }
                animator {
                    alpha=0f
                    translationY=-target.height*1f
                    play(alpha(0f,1f).duration(600)).with(translationYBy(target.height*1f).duration(600))
                }
            }


            image(R.mipmap.page2_boy){
                id="imageBoy"
                lparams {
                    align="imageCar"
                    margin(bottom = dp(-8))
                    alignRule=LEFT or BOTTOM
                }
            }
            image(R.mipmap.page2_girl2){
                id="imageGirl1"
                lparams {
                    align="imageCar"
                    margin(left = dp(-48),bottom = dp(56))
                    alignRule=LEFT_RIGHT or BOTTOM
                }
            }
            image(R.mipmap.page2_girl1){
                id="imageGirl2"
                lparams {
                    align="imageCar"
                    margin(left=dp(-16),top=dp(-112))
                    alignRule=RIGHT or TOP_BOTTOM
                }
            }
            animatorSet { play("text1").after("text2")}
        }
    }
}