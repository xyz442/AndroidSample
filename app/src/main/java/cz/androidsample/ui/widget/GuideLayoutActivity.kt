package cz.androidsample.ui.widget

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.widget.element.*
import cz.androidsample.ui.widget.element.dsl.Page
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_guide_layout.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundResource

@ToolBar
class GuideLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_layout)
        setTitle(intent.getStringExtra("title"))
        val page=getPage()
        guideContainer.addView(page,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun backgroundLayout()=ElementLayout(this).apply{
        image{
            backgroundColor=Color.GREEN
            padding=dp(20f)
            lparams {
                //占比100%
                width=MATCH_PARENT
                margin=dp(10f)
                //在parent内,完全居中
                alignRule=TOP
            }
        }
        image{
            backgroundColor=Color.BLUE
            padding=dp(20f)
            lparams {
                //占比100%
                width=MATCH_PARENT
                margin=dp(10f)
                //在parent内,完全居中
                alignRule=BOTTOM
            }
        }
    }

    /**
     * 获得一个分页
     */
    private fun getPage()=with(Page(this)){
//        include(backgroundLayout())
        layout {
            text {
                id=R.id.text1
                text="演示文本1"
                textSize=sp(16f)
                padding=dp(12f)
                gravity=Gravity.LEFT
                textColor= Color.RED
                backgroundColor=Color.GREEN
                lparams {
                    width=MATCH_PARENT
                    height=MATCH_PARENT
                    alignRule=CENTER_HORIZONTAL
                }
            }
            /*text {
                id=R.id.text2
                text="演示文本2"
                padding=dp(16f)
                textColor= Color.RED
                backgroundColor=Color.GREEN
                gravity=Gravity.LEFT
                lparams {
                    align=R.id.text1
                    margin=dp(10f)
                    //在text1的底部,并以它居中
                    alignRule= BOTTOM or CENTER_HORIZONTAL
                }
            }
            image{
                drawableResource=R.mipmap.ic_launcher
                padding=dp(16f)
                lparams {
                    margin=dp(10f)
                    //在parent内,完全居中
                    alignRule=CENTER
                }
            }*/
        }
    }
}
