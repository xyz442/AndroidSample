package cz.androidsample.ui.widget

import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import cz.androidsample.R
import kotlinx.android.synthetic.main.activity_my_image_view.*

class MyImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_image_view)
        val bitmap=BitmapFactory.decodeResource(resources,R.mipmap.timg)
        imageView.setImageBitmap(bitmap)
    }
}
