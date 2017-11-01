package cz.androidsample.ui.widget.guide.adapter

import android.view.View
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.guide.GuidePagerAdapter

/**
 * Created by cz on 2017/11/1.
 */
class MyGuideAdapter(items: List<Page>) : GuidePagerAdapter(items) {

    override fun getPage(parent: View, position: Int): Page {
        return items[position]
    }
}