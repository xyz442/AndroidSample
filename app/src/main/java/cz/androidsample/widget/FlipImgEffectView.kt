package cz.androidsample.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import cz.androidsample.R
import cz.androidsample.debugLog

class FlipImgEffectView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private lateinit var showBmp: Bitmap
    private val cameraMatrix= Matrix() //作用矩阵
    private val camera= Camera()
    private var centerX: Int = 0
    private var centerY: Int = 0 //图片中心点
    private var rotateX:Float=0f
    private var rotateY:Float=0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        showBmp = BitmapFactory.decodeResource(resources, R.mipmap.timg).apply {
            centerX = this.width / 2
            centerY = this.height / 2
        }
    }

    fun setRotateX(x:Float){
        this.rotateX=x
        invalidate()
        debugLog("rotateX:$rotateX")
    }
    fun setRotateY(y:Float){
        this.rotateY=y
        invalidate()
        debugLog("rotateY:$rotateY")
    }

    override fun onDraw(canvas: Canvas) {
        camera.save()
        //绕X轴翻转
        camera.rotateX(-rotateY)
        //绕Y轴翻转
        camera.rotateY(rotateX)
        debugLog("rotateX:$rotateX rotateY:$rotateY")
        //设置camera作用矩阵
        camera.getMatrix(cameraMatrix)
        camera.restore()
        //设置翻转中心点
        cameraMatrix.preTranslate((-this.centerX).toFloat(), (-this.centerY).toFloat())
        cameraMatrix.postTranslate(this.centerX.toFloat(), this.centerY.toFloat())
        canvas.drawBitmap(showBmp, cameraMatrix, null)
    }

}  