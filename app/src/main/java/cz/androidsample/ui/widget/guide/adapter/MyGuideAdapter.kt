package cz.androidsample.ui.widget.guide.adapter

import android.view.View
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.element.PageLayout
import cz.androidsample.ui.widget.guide.GuidePagerAdapter

/**
 * Created by cz on 2017/11/1.
 */
class MyGuideAdapter(items: List<Page>) : GuidePagerAdapter(items) {
    override fun onCreatePage(page: Page, layout:PageLayout, position: Int) {
        layout.alpha=0f
    }

    override fun onStartPageAnimator(page: Page,layout: PageLayout, position: Int) {
        super.onStartPageAnimator(page,layout, position)
        debugLog("onStartPageAnimator:$position")
        layout.alpha=1f
    }

    override fun getPage(parent: View, position: Int): Page {
        return items[position]
    }
}