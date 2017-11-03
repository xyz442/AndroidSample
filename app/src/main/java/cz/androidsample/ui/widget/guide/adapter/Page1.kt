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
                    verticalPercent=0.10f
                    alignRule=CENTER_HORIZONTAL
                }
                animator {
                    translationX(-target.right*1f,0f).duration(600)
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
                    translationXBy(target.right*1f).delay(100).duration(600)
                }
            }
            //横向导航线
            vline {
                id="line"
                lparams { verticalPercent=0.55f }
            }

            image(R.mipmap.page1_wallet){
                lparams {
                    align="line"
                    bottomMargin=dp(48)
                    alignRule=CENTER
                }
            }
            animator {
                play("imageText1").after("imageText2")
            }
        }
    }
}