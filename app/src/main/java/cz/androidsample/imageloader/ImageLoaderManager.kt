package cz.androidsample.imageloader

import android.app.Application
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.AbsListView
import com.facebook.binaryresource.FileBinaryResource
import com.facebook.cache.common.SimpleCacheKey
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import cz.androidsample.imageloader.callback.OnDownloadImageListener
import cz.androidsample.imageloader.callback.OnImageDisplayListener
import cz.androidsample.imageloader.impl.FrescoConfig
import cz.androidsample.imageloader.impl.FrescoLoader
import java.io.File


/**
 * Created by cz on 9/8/16.

 * 20170428 wanghui 新增参数res，添加默认占位图
 */
object ImageLoaderManager {
    private val loader: ILoader
    private val config: IConfig<ImagePipelineConfig>
    init {
        loader = FrescoLoader()
        config = FrescoConfig()
    }

    fun  init(application: Application, t: ImagePipelineConfig? = null) = config.init(application, t)

    fun downloadImage(context: Context, url: String, listener: OnDownloadImageListener) {
        loader.downloadImage(context, url, listener)
    }

    fun attrchToListView(listView: AbsListView, listener: AbsListView.OnScrollListener?) {
        listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                listener?.onScrollStateChanged(view, scrollState)
                when (scrollState) {
                    AbsListView.OnScrollListener.SCROLL_STATE_IDLE -> loader.resume()
                    AbsListView.OnScrollListener.SCROLL_STATE_FLING -> loader.pause()
                    AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL -> {}
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                listener?.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
            }
        })
    }

    fun attachToRecyclerView(recyclerView: RecyclerView, listener: RecyclerView.OnScrollListener?) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                listener?.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_SETTLING -> loader.pause()
                    RecyclerView.SCROLL_STATE_IDLE -> loader.resume()
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                listener?.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    //    public Bitmap getCacheBitmap(String url){
    //        FileBinaryResource resource = (FileBinaryResource) Fresco.getImagePipelineFactory().getMainDiskStorageCache().getResource(new SimpleCacheKey(url));
    //        return BitmapUtils.getBitmap(resource.getFile(),-1,-1);
    //    }

    fun getCacheFile(url: String): File? {
        var file: File? = null
        val resource = Fresco.getImagePipelineFactory().mainFileCache.getResource(SimpleCacheKey(url)) as FileBinaryResource
        if (null != resource) {
            file = resource.file
        }
        return file
    }

    fun interceptDisplay()=config.interceptDisplay()

    fun displayImage(view: WrapperView, url: String, aspectRatio: Float, listener: OnImageDisplayListener?){
        view.aspectRatio = aspectRatio
        loader.display(view, url) { lifeCycle, url, view ->
            ImageLoaderHandler.forEach { it.callback(lifeCycle,url,view) }
            when(lifeCycle){
                DisplayLifeCycle.START->listener?.callback(lifeCycle,url,view)
                DisplayLifeCycle.COMPLETE->listener?.callback(lifeCycle,url,view)
                DisplayLifeCycle.FAILED->listener?.callback(lifeCycle,url,view)
            }
        }
    }

}
