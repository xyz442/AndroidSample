package cz.androidsample

import android.app.Application
import cz.androidsample.imageloader.ImageLoaderManager

/**
 * Created by cz on 2017/11/27.
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        ImageLoaderManager.init(this)
    }

}