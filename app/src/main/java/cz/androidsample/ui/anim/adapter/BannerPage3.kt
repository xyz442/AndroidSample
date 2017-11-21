package cz.androidsample.ui.anim.adapter

import android.content.Context
import cz.androidsample.R
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.element.PageLayoutParams
import cz.androidsample.ui.widget.element.image
import cz.androidsample.ui.widget.element.text

/**
 * Created by cz on 2017/11/20.
 */
class BannerPage3(val context: Context){

    fun getPage()=with(Page(context)) {
        layout {
            image(R.mipmap.banner3_border){
                id="imageBorder2"
                lparams{
                    alignRule=RIGHT
                    rightMargin=dp(27)
                }
            }
            image(R.mipmap.banner3_bg){
                id="imageFlag"
                lparams{
                    align="imageBorder2"
                    margin(left=dp(-12),top=dp(4))
                    alignRule=LEFT or TOP
                }
            }
            image(R.mipmap.banner3_gap){
                id="imageGap"
                lparams{
                    align="imageFlag"
                    margin(top=dp(-12))
                    alignRule=TOP_BOTTOM or CENTER_HORIZONTAL
                }
            }
            image(R.mipmap.banner3_border){
                id="imageBorder1"
                lparams{
                    align="imageFlag"
                    margin(left=dp(-12),top=dp(8))
                    alignRule=LEFT or TOP
                }
            }
            image(R.mipmap.banner3_point){
                id="imageBox2"
                lparams{
                    align="imageFlag"
                    alignRule=CENTER
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