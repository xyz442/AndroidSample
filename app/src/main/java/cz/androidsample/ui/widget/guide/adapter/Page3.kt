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
            }
            image(R.mipmap.page3_credit_card){
                id="imageCard"
                lparams {
                    align="line"
                    bottomMargin=dp(42)
                    alignRule=CENTER
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
            }
            image(R.mipmap.page3_wheel_bg){
                id="imageWheel"
                lparams {
                    align="imageScoreBg"
                    margin(left=dp(-32),bottom = dp(-8))
                    alignRule=CENTER
                }
            }
            image(R.mipmap.page3_credit_flag){
                id="imageWheelFlag"
                lparams {
                    align="imageScoreBg"
                    margin(left=dp(-32),bottom = dp(-8))
                    alignRule=CENTER
                }
            }
            text{
                id="imageCreditScore"
                font("500",sp(24),Color.RED)
                lparams {
                    align="imageScoreBg"
                    margin(left=dp(-36))
                    alignRule=CENTER
                }
                animator {
                    number(500,750,duration = 1000)
                }
            }

            text{
                id="imageCreditInfo"
                font("信用优秀",sp(9),Color.RED)
                lparams {
                    align="imageCreditScore"
                    margin(left=dp(-36))
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
            }

            image(R.mipmap.page3_doc){
                id="imageDoc"
                lparams {
                    align="imageCard"
                    margin(right=dp(-12),bottom = dp(-8))
                    alignRule=BOTTOM or RIGHT
                }
            }

            image(R.mipmap.page3_girl1){
                id="imageGirl1"
                lparams {
                    align="imageCard"
                    margin(top=dp(-28),left = dp(-24))
                    alignRule=TOP_BOTTOM or LEFT_RIGHT
                }
            }

            image(R.mipmap.page3_girl2){
                id="imageGirl2"
                lparams {
                    align="imageCard"
                    margin(top=dp(-12),right=dp(8))
                    alignRule=RIGHT_LEFT or TOP_BOTTOM
                }
            }

            image(R.mipmap.page3_girl3){
                id="imageGirl3"
                lparams {
                    align="imageCard"
                    alignRule=LEFT_RIGHT or CENTER_VERTICAL
                }
            }


        }
    }
}