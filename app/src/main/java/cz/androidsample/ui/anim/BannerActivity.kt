package cz.androidsample.ui.anim

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.anim.adapter.*
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.guide.adapter.BannerGuideAdapter
import cz.androidsample.ui.widget.guide.layoutmanager.StackLayoutManager
import kotlinx.android.synthetic.main.activity_banner.*

class BannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner)

        if(null==savedInstanceState){
            supportFragmentManager.beginTransaction().add(R.id.bannerContainer,BannerPage1Fragment()).commit()
        }
    }
}
