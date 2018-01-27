package cz.androidsample.ui.app

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import android.util.DisplayMetrics
import android.util.Log

import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException

/**
 * Created by Administrator on 2017/7/1.
 */
object Tools {

    fun getCpuInfo():String?{
        var cpuInfo:String?=null
        try {
            val lines=File("/proc/cpuinfo").readLines()
            val line=lines.find { it.startsWith("Hardware") }
            if(null!=line){
                val matcher="Hardware\\s+:\\s+(.+)".toPattern().matcher(line)
                if(matcher.find()){
                    cpuInfo=matcher.group(1)
                }
            }
        } catch (e: Exception) {
        }
        return cpuInfo
    }

    fun getAvailMemory(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return Formatter.formatFileSize(context, mi.availMem)
    }

    fun getTotalMemory(context: Context): String {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        return Formatter.formatFileSize(context, mi.totalMem)
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    fun getExternalStorageTotalSize(context: Context): String {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val (blockSize,totalBlocks)=if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR2){
            stat.blockSize.toLong() to stat.blockCount.toLong()
        } else {
            stat.blockSizeLong to stat.blockCountLong
        }
        return Formatter.formatFileSize(context, blockSize * totalBlocks)
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    fun getExternalStorageAvailableSize(context: Context): String {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val (blockSize,availableBlocks)=if(Build.VERSION.SDK_INT<Build.VERSION_CODES.JELLY_BEAN_MR2){
            stat.blockSize.toLong() to stat.availableBlocks.toLong()
        } else {
            stat.blockSizeLong to stat.availableBlocksLong
        }
        return Formatter.formatFileSize(context, blockSize * availableBlocks)
    }

    fun getScreenResolution(context: Context): String {
        val dm = context.resources.displayMetrics
        return dm.widthPixels.toString() + " * " + dm.heightPixels
    }
}