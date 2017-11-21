package cz.androidsample.ui.layout

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout

import cz.androidsample.R
import cz.androidsample.ui.widget.element.*
import kotlinx.android.synthetic.main.activity_constraint.*

class ConstraintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraint)
        val layout = with(Page(this)) {
            layout {
                /*vline {
                    id="line"
                    lparams { verticalPercent=0.55f }
                }
                hline {
                    lparams { horizontalPercent=0.5f }
                }*/

                text {
                    id = "button1"
                    font("Button1", sp(16), Color.RED)
                    padding = dp(12)
                    backgroundColor=Color.GREEN
                    lparams(width = 400) {
                        alignRule = CENTER_HORIZONTAL
                    }
                }
                text {
                    id = "button2"
                    font("Button2", sp(16), Color.RED)
                    backgroundColor=Color.BLUE
                    padding = dp(12)
                    lparams {
                        align = "button1"
                        alignRule = TOP_BOTTOM or CENTER_HORIZONTAL
                    }
                }
//                text {
//                    id="button3"
//                    font("Button3", sp(16), Color.RED)
//                    backgroundColor=Color.YELLOW
//                    padding = dp(12)
//                    lparams {
//                        align = "button2"
//                        margin(left = dp(20),top=dp(-10))
//                        alignRule = LEFT or TOP_BOTTOM
//                    }
//                }
//                image{
//                    id="boy"
//                    padding=dp(20)
//                    backgroundColor=Color.RED
//                    lparams {
//                        align="button3"
//                        margin(top = dp(-12))
//                        alignRule=RIGHT or TOP_BOTTOM
//                    }
//                    click {
//                        target.requestLayout()
//                    }
//                }
            }
        }
        frameContainer.addView(layout.layout,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT)

    }
}
