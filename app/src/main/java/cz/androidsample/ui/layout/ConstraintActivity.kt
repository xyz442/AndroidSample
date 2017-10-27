package cz.androidsample.ui.layout

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import cz.androidsample.R
import kotlinx.android.synthetic.main.activity_constraint.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class ConstraintActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraint)
        val layoutParams=button1.layoutParams
        println(layoutParams)
        button5.onClick {
            button4.visibility=View.GONE
        }
    }
}
