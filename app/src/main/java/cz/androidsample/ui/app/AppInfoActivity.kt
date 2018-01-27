package cz.androidsample.ui.app

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_app_info.*
import java.util.*

@ToolBar
class AppInfoActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)
        setTitle(intent.getStringExtra("title"))

        textInfo.text = "总内存大小:${Tools.getTotalMemory(this)}\n"+
                "可用内存大小:${Tools.getAvailMemory(this)}\n"+
                "内部存储大小:${Tools.getExternalStorageTotalSize(this)}\n"+
                "可用存储大小:${Tools.getExternalStorageAvailableSize(this)}\n"+
                "手机分率率:${Tools.getScreenResolution(this)}\n"+
                "手机cpu型号:${Tools.getCpuInfo()}\n"
    }
}
