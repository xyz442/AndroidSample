package cz.androidsample.ui.widget.waterfall

import android.os.Bundle
import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.debugLog
import cz.volunteerunion.ui.ToolBarActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executors

/**
 * Created by cz on 2017/11/26.
 */
@ToolBar
class WaterfallActivity:ToolBarActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_fall)
        setTitle(intent.getStringExtra("title"))
        //请求百度获得一批图片
        requestGirlImages()
    }

    /**
     * 请求美女图片
     */
    private fun requestGirlImages() {
        doAsync {
            val urlItems = LinkedList(mutableListOf("http://image.baidu.com/search/index?ct=201326592&cl=2&st=-1&lm=-1&nc=1&ie=utf-8&tn=baiduimage&ipn=r&rps=1&pv=&fm=rs2&word=%E8%87%AA%E7%84%B6%E7%BE%8E%E5%A5%B3&oriquery=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&ofr=%E7%BE%8E%E5%A5%B3%E5%9B%BE%E7%89%87&hs=2&sensitive=0",
                    "http://image.baidu.com/search/index?ct=201326592&cl=2&st=-1&lm=-1&nc=1&ie=utf-8&tn=baiduimage&ipn=r&rps=1&pv=&fm=rs5&word=%E7%9C%9F%E5%AE%9E%E7%BE%8E%E5%A5%B3&oriquery=%E8%87%AA%E7%84%B6%E7%BE%8E%E5%A5%B3&ofr=%E8%87%AA%E7%84%B6%E7%BE%8E%E5%A5%B3&hs=2&sensitive=80",
                    "http://image.baidu.com/search/index?ct=201326592&cl=2&st=-1&lm=-1&nc=1&ie=utf-8&tn=baiduimage&ipn=r&rps=1&pv=&fm=rs9&word=%E6%BC%82%E4%BA%AE%E7%BE%8E%E5%A5%B3%E7%94%9F%E6%B4%BB%E7%85%A7&oriquery=%E7%9C%9F%E5%AE%9E%E7%9B%B8%E7%89%87%E7%BE%8E%E5%A5%B3%E7%94%9F%E6%B4%BB%E7%85%A7&ofr=%E7%9C%9F%E5%AE%9E%E7%9B%B8%E7%89%87%E7%BE%8E%E5%A5%B3%E7%94%9F%E6%B4%BB%E7%85%A7&hs=2&sensitive=0"))
            val imageUrls = mutableListOf<String>()
            while (!urlItems.isEmpty()) {
                val url = urlItems.pollFirst()
                val content = getHtmlContent(url)
                val matcher = "\"([^\"]+)\":\"(https?://[^\"]+(?=(.jpg)|(.png))(\\2|\\3))".toPattern().matcher(content)
                //百度前6张图无效
                var index=1
                while (matcher.find()) {
                    //取小图
                    if ("middleURL" == matcher.group(1)&&index++>6) {
                        imageUrls.add(matcher.group(2))
                    }
                }
            }
            uiThread {
                //加载图片
            }
        }
    }

    @Throws(Exception::class)
    fun getHtmlContent(path: String): String {
        // 通过网络地址创建URL对象
        val url = URL(path)
        // 根据URL
        // 打开连接，URL.openConnection函数会根据URL的类型，返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
        val conn = url.openConnection() as HttpURLConnection
        // 设定URL的请求类别，有POST、GET 两类
        conn.requestMethod = "GET"
        //设置从主机读取数据超时（单位：毫秒）
        conn.connectTimeout = 5000
        //设置连接主机超时（单位：毫秒）
        conn.readTimeout = 5000
        // 通过打开的连接读取的输入流,获取html数据
        val inStream = conn.inputStream
        // 得到html的二进制数据
        val data = readInputStream(inStream)
        // 是用指定的字符集解码指定的字节数组构造一个新的字符串
        return String(data, Charset.forName("utf-8"))
    }

    /**
     * 读取输入流，得到html的二进制数据

     * @param inStream
     * *
     * @return
     * *
     * @throws Exception
     */
    @Throws(Exception::class)
    fun readInputStream(inStream: InputStream): ByteArray {
        val outStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len =inStream.read(buffer)
        while (len != -1) {
            outStream.write(buffer, 0, len)
            len =inStream.read(buffer)
        }
        inStream.close()
        return outStream.toByteArray()
    }
}