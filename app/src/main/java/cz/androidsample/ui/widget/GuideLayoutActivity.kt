package cz.androidsample.ui.widget

import android.os.Bundle
import android.support.v4.view.ViewPager

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.guide.adapter.*
import cz.androidsample.ui.widget.guide.layoutmanager.PagerLayoutManager
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_guide_layout.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

@ToolBar
class GuideLayoutActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide_layout)
        setTitle(intent.getStringExtra("title"))

        val pageItems= mutableListOf<Page>()
        val page = Page1(this).getPage()
        val text1=page.view("text1")
        text1?.onClick {
            toast("点击!")
        }
        pageItems.add(page)
        pageItems.add(Page2(this).getPage())
        pageItems.add(Page3(this).getPage())
        pageItems.add(Page4(this).getPage())
        val adapter=MyGuideAdapter(pageItems)
        guideLayout.setLayoutManager(PagerLayoutManager(this))
        guideLayout.setAdapter(adapter)

        guideLayout.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }

        })

    }
}
