package cz.androidsample.imageloader.callback

import android.graphics.Bitmap

interface OnDownloadImageListener {
    fun callback(url: String, bitmap: Bitmap)
}