package cz.androidsample.ui.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import android.widget.*
import cz.androidsample.DEBUG
import cz.androidsample.R
import cz.androidsample.debugLog
import org.jetbrains.anko.doAsync
import java.util.*

/**
 * Created by Administrator on 2017/10/14.
 */

/**
 * Created by cz on 2017/10/10.
 */
class SeatTable(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr) , ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val viewFlinger=ViewFlinger()
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val tmpRect = Rect()
    //当前屏幕显示区域
    private val screenRect= Rect()
    private var m = FloatArray(9)
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()
    //座位信息布局
    private lateinit var seatLayout:View
    //屏幕信息
    private lateinit var screenView:View
    //列指示器
    private val numberLayout= FrameLayout(context).apply {
        layoutParams= ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    //可滚动区域大小,涉及动态计算,所以保存值
    private var horizontalRange=0
    private var verticalRange=0
    //所有座位信息
    private lateinit var seatArray:Array<Array<SeatNodeInfo>>
    private val recyclerBin=RecyclerBin()
    private var adapter:SeatTableAdapter?=null
    //是否为固定尺寸计算,如果是固定尺寸,会选择第0个控件的尺寸进行计算,这样在控件达到1000以上,性能相差会非常大
    //如果不为固定尺寸计算,会计算出每一个控件具体大小.有多少会控件会运算多少次measure,大概效果为4000 400毫秒,但onMeasure会回调多次
    private var hasFixSize=false

    //缩放限制区域
    private var hierarchyMaxScale=2.0f
    private var hierarchyMinScale=1.0f
    //缩放动画对象
    private var zoomAnimator: ValueAnimator?=null
    //缩放聚焦点,因为缩放过程中,会改变其值,为了体验平滑,记录最初值
    private var scaleFocusX =0f
    private var scaleFocusY =0f
    //缩略图背景
    private var thumbBackgroundDrawable:Drawable?=null
    //缩略图
    private val previewPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=Color.RED
        strokeWidth=2f
        style=Paint.Style.STROKE
    }
    private lateinit var previewBitmap:Bitmap
    private var previewWidth=0f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.SeatTable).apply {
            setPreViewWidth(getDimension(R.styleable.SeatTable_st_previewWidth,0f))
            setHierarchyMaxScale(getFloat(R.styleable.SeatTable_st_hierarchyMaxScale,2f))
            setHierarchyMinScale(getFloat(R.styleable.SeatTable_st_hierarchyMinScale,0.8f))
            setHasFixSize(getBoolean(R.styleable.SeatTable_st_setHasFixedSize,true))
            setThumbBackgroundDrawable(getDrawable(R.styleable.SeatTable_st_thumbBackgroundDrawable))
            recycle()
        }
    }

    private fun setPreViewWidth(width: Float) {
        this.previewWidth=width
    }

    private fun setHierarchyMaxScale(hierarchyMaxScale: Float) {
        this.hierarchyMaxScale=hierarchyMaxScale
    }

    private fun setHierarchyMinScale(hierarchyMinScale: Float) {
        this.hierarchyMinScale=hierarchyMinScale
    }

    private fun setHasFixSize(hasFixSize: Boolean) {
        this.hasFixSize=hasFixSize
    }

    private fun setThumbBackgroundDrawable(drawable: Drawable?) {
        this.thumbBackgroundDrawable=drawable
        invalidate()
    }


    /**
     * 设置数据适配器
     */
    fun setAdapter(newAdapter: SeatTableAdapter){
        if(null!=adapter){
            adapter=null
            views.clear()
            requestLayout()
        }
        adapter= newAdapter
        //屏幕附加信息
        seatLayout = newAdapter.getHeaderSeatLayout(parent as ViewGroup)
        //屏幕布局
        screenView = newAdapter.getHeaderScreenView(parent as ViewGroup)
        //执行计算,获得矩阵前信息/屏幕信息/座位以及整个影院大小信息
        val columnCount = newAdapter.getSeatColumnCount()
        val rowCount = newAdapter.getSeatRowCount()
        seatArray = Array(rowCount){ row->
            //添加序列信息
            val numberView=newAdapter.getSeatNumberView(parent as ViewGroup)
            newAdapter.bindSeatNumberView(numberView,row)
            numberLayout.addView(numberView)
            //添加节点信息
            (0..columnCount-1).map {SeatNodeInfo(row,it) }.toTypedArray()
        }

        val seatView = recyclerBin.newViewWithMeasured(seatArray[0][0])
        newAdapter.bindSeatView(parent as ViewGroup,seatView,0,0)
        addView(seatView)
        newAdapter.bindNumberLayout(numberLayout)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val adapter=adapter?:return
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        for(view in views){
            measureChildWithMargins(view,widthMode,heightMode)
        }
        //测量排版屏幕
        //这个控件计算尺寸,忽略边距,因为他要铺满
        measureChildWithMargins(seatLayout, widthMode, heightMode, ignorePadding = true)
        measureChildWithMargins(screenView, widthMode, heightMode)
        measureChildWithMargins(numberLayout, widthMode, heightMode)
        //排版顶部描述
        seatLayout.layout(0, 0, seatLayout.measuredWidth, seatLayout.measuredHeight)
        //排版屏幕位置
        val screenTop = seatLayout.measuredHeight + paddingTop
        screenView.layout((horizontalRange - screenView.measuredWidth) / 2, screenTop,
                (horizontalRange + screenView.measuredWidth) / 2, screenTop + screenView.measuredHeight)

        //排版指示器布局
        numberLayout.layout(paddingLeft,
                screenTop + screenView.measuredHeight,
                paddingLeft+numberLayout.measuredWidth,
                verticalRange-paddingBottom)
        val rowCount = adapter.getSeatRowCount()
        val columnCount = adapter.getSeatColumnCount()
        //计算整个电影院大小
        val itemView = getFirstView()
        measureSeatItem(adapter,itemView)
        measureRange(adapter, itemView,rowCount,columnCount)
        debugLog("onMeasure:${seatLayout.measuredWidth} ${seatLayout.measuredHeight} ${numberLayout.measuredWidth} ${numberLayout.measuredHeight} $horizontalRange $verticalRange")
    }

    private fun measureSeatItem(adapter: SeatTableAdapter,itemView:View): View {
        var left = paddingLeft+numberLayout.measuredWidth
        var top = paddingTop + seatLayout.measuredHeight + screenView.measuredHeight
        seatArray.forEachIndexed { row, array ->
            array.forEachIndexed { column, item ->
                //排版同列控件
                item.layoutRect.set(left, top, left + itemView.measuredWidth, top + itemView.measuredHeight)
                left += adapter.getHorizontalSpacing(column) + itemView.measuredWidth
            }
            //排序序列
            val numberView = numberLayout.getChildAt(row)
            numberView.layout((numberView.measuredWidth-numberLayout.measuredWidth)/2,
                    top-numberLayout.top+(numberView.measuredHeight-itemView.measuredHeight)/2,
                    (numberView.measuredWidth+numberLayout.measuredWidth)/2,
                    top-numberLayout.top+(numberView.measuredHeight+itemView.measuredHeight)/2)
            //整行换行,计算左,上偏移信息
            left = paddingLeft+numberLayout.measuredWidth
            top += itemView.measuredHeight + adapter.getVerticalSpacing(row)
        }
        return itemView
    }

    /**
     * 测量矩阵尺寸
     */
    private fun measureRange(adapter: SeatTableAdapter, itemView: View,rowCount:Int,columnCount:Int) {
        horizontalRange = paddingLeft + paddingRight+numberLayout.measuredWidth
        verticalRange = paddingTop + paddingBottom + seatLayout.measuredHeight + screenView.measuredHeight
        (0..columnCount - 1).forEach {
            if (it != columnCount - 1) {
                horizontalRange += adapter.getHorizontalSpacing(it)
            }
            horizontalRange += itemView.measuredWidth
        }
        (0..rowCount - 1).forEach {
            if (it != rowCount - 1) {
                verticalRange += adapter.getVerticalSpacing(it)
            }
            verticalRange += itemView.measuredHeight
        }
    }

    fun measureChildWithMargins(child: View, widthMode: Int, heightMode: Int,ignorePadding:Boolean=false) {
        val lp = child.layoutParams
        val widthSpec = getChildMeasureSpec(measuredWidth, widthMode, if(ignorePadding) 0 else paddingLeft + paddingRight, lp.width)
        val heightSpec = getChildMeasureSpec(measuredHeight, heightMode,if(ignorePadding) 0 else paddingTop + paddingBottom, lp.height)
        child.measure(widthSpec, heightSpec)
    }

    fun getChildMeasureSpec(parentSize: Int, parentMode: Int, padding: Int, childDimension: Int): Int {
        val size = Math.max(0, parentSize - padding)
        var resultSize = 0
        var resultMode = 0
        if (childDimension >= 0) {
            resultSize = childDimension
            resultMode = View.MeasureSpec.EXACTLY
        } else {
            if (childDimension == ViewGroup.LayoutParams.MATCH_PARENT) {
                resultSize = size
                resultMode = parentMode
            } else if (childDimension == ViewGroup.LayoutParams.WRAP_CONTENT) {
                resultSize = size
                if (parentMode == View.MeasureSpec.AT_MOST || parentMode == View.MeasureSpec.EXACTLY) {
                    resultMode = View.MeasureSpec.AT_MOST
                } else {
                    resultMode = View.MeasureSpec.UNSPECIFIED
                }
            }
        }
        return View.MeasureSpec.makeMeasureSpec(resultSize, resultMode)
    }


    /**
     * 在尺寸变化后,重新制作缩略图
     * 制作缩略图在数据量大时,非常耗时.这里,无论更新缩略图,都采取局变更新策略.不应该直接重绘整张缩略图
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //这里异步更新
        doAsync {
            val st=System.currentTimeMillis()
            //绘制宽高
            val measuredWidth = computeHorizontalScrollRange() + width
            val measuredHeight = computeVerticalScrollRange() + height
            //此处按宽高比例重新设置previewHeight,因为配置比例与实际比例会有冲突,所以保留宽,高度自适应
            val previewHeight=previewWidth/measuredWidth*measuredHeight
            //计算出预览图缩放比例
            previewBitmap = Bitmap.createBitmap(previewWidth.toInt(), previewHeight.toInt(), Bitmap.Config.ARGB_4444)
            //绘制预览图
            val matrixScaleX = previewWidth / measuredWidth
            val matrixScaleY = previewHeight / measuredHeight
            drawPreviewSeatRange(Canvas(previewBitmap),matrixScaleX,matrixScaleY)
            //绘完毕后,通知刷新
            postInvalidate()
            debugLog("onSizeChanged:${System.currentTimeMillis()-st}")
        }
        //横向滚到中间
        scrollTo(computeHorizontalScrollRange()/2,0)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //滚动时检测当前屏需要控件
        val adapter=adapter?:return
        var st=System.currentTimeMillis()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //移除当前屏内所有控件
        val parent=parent as ViewGroup
        //此处优化细节为,直接通过缩放比,以及控件尺寸,确定当前屏幕在,整个二维节点内的行与列,减少大量数量时的大量运算,仅适用于电影票选座
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)
        //快速查找到需要绘制的矩阵条目起始,结束位置
        //起始纵向矩阵
        val startRange=findScreenRange(seatArray.map { it[0] }.toTypedArray()){
            tmpRect.set((it.left * matrixScaleX).toInt(),(it.top * matrixScaleY).toInt(),(it.right * matrixScaleX).toInt(), (it.bottom * matrixScaleY).toInt())
            intersetsVerticalRect(screenRect,tmpRect)
        }
        //横向查
        val endRange=findScreenRange(seatArray[0]){
            tmpRect.set((it.left * matrixScaleX).toInt(),(it.top * matrixScaleY).toInt(),(it.right * matrixScaleX).toInt(), (it.bottom * matrixScaleY).toInt())
            intersetsHorizontalRect(screenRect,tmpRect)
        }
        recyclerBin.detachAndScrapAttachedViews()
        val stepTime=System.currentTimeMillis()-st
        startRange.forEach { row->
            endRange.forEach { column->
                //限制指定条目是否展示
                if(adapter.isSeatVisible(row,column)){
                    val item=seatArray[row][column]
                    val layoutRect = item.layoutRect
                    //取一个新的控件,并运算
                    val view = recyclerBin.getView(item)
                    //添加控件
                    addView(view)
                    adapter.bindSeatView(parent,view,item.row,item.column)
                    view.tag = item
                    //直接排,不排在指定矩阵内
                    view.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom)
                }
            }
        }
        debugLog("onScrollChanged:$stepTime ${System.currentTimeMillis()-st} $startRange $endRange")
    }

    /**
     * 查找屏幕内起始计算矩阵,因为当数据量非常大时,不快速找到起始遍历位置,会非常慢
     */
    private fun findScreenRange(array:Array<SeatNodeInfo>,predicate:(Rect)->Boolean):IntRange{
        var (start,end)=-1 to -1
        //纵向查
        run{ array.forEachIndexed { row,node ->
                val intersects=predicate(node.layoutRect)
                if(-1==start&&intersects){
                    start=row//记录头
                } else if(-1!=start&&!intersects){
                    end=row
                    return@run
                }
            }
        }
        //检测最后结果
        if(-1==end){
            end=array.size-1
        }
        return IntRange(start,end)
    }

    /**
     * 绘制缩略图
     * @param canvas 绘制canvas对象,传入对象为预览canvas,则会绘制到bitmap上
     */
    private fun drawPreviewSeatRange(canvas: Canvas,matrixScaleX: Float, matrixScaleY: Float) {
        val adapter=adapter?:return
        //画背景
        thumbBackgroundDrawable?.setBounds(0,0,canvas.width,canvas.height)
        thumbBackgroundDrawable?.draw(canvas)
        //绘序列
        drawNumberIndicator(canvas,matrixScaleX,matrixScaleY)
        //绘屏幕
        drawScreen(canvas,screenRect,matrixScaleX,matrixScaleY,true)
        //绘所有座位描述
        val firstView = getFirstView()
        seatArray.forEach{
            it.forEach { node ->
                if(adapter.isSeatVisible(node.row,node.column)){
                    drawPreviewNode(canvas,firstView,node, matrixScaleX, matrixScaleY)
                }
            }
        }
    }

    /**
     * 绘制一个预览节点信息
     */
    private fun drawPreviewNode(canvas: Canvas,firstView: View,node: SeatNodeInfo,matrixScaleX: Float, matrixScaleY: Float) {
        val layoutRect = node.layoutRect
        if (!hasFixSize) {
            measureChildWithMargins(firstView, MeasureSpec.AT_MOST, MeasureSpec.AT_MOST)
        }
        firstView.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom)
        //记录节点信息
        firstView.tag = node
        //这里不用排版,不用顾忌不排版后点击错乱问题
        drawSeatView(canvas, firstView, matrixScaleX, matrixScaleY)
    }

    /**
     * 获得第一个基准控件,作长宽模板使用
     */
    fun getFirstView()= views[0]

    fun getChildCount()=views.size

    fun getChildAt(index:Int)=views[index]

    fun indexOfChild(view:View)=views.indexOf(view)

    /**
     * 选中一个指定位置数据,选中后,涉及预览图更新
     */
    fun setItemSelected(row:Int,column:Int,select:Boolean)=setItemSelected(seatArray[row][column],select)

    /**
     * 选中一个条目数据,选中后,涉及预览图更新
     */
    fun setItemSelected(item:SeatNodeInfo, selected:Boolean){
        val itemView=getFirstView()
        item.select= selected
        itemView.isSelected=selected
        val matrixScaleX=previewWidth*1f/horizontalRange
        //重绘座位信息
        drawPreviewNode(Canvas(previewBitmap),itemView,item,matrixScaleX,matrixScaleX)
        //重绘界面
        postInvalidate()
        debugLog("setItemSelected:${itemView.isSelected} ${itemView.isPressed}")
    }

    private fun getSeatNodeByView(v:View):SeatNodeInfo= v.tag as SeatNodeInfo

    private fun addView(view: View) {
        if(null!=view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        views.add(view)
    }

    /**
     * 设置当前屏幕缩放值
     */
    fun setHierarchyScale(scale:Float)=setHierarchyScaleInner(scale,width/2f,height/2f)

    fun setHierarchyScaleInner(scale: Float,focusX: Float,focusY: Float,postAction:(()->Unit)?=null){
        cancelZoomAnimator()
        zoomAnimator = ValueAnimator.ofFloat(getMatrixScaleX(), scale).apply {
            duration=200
            addUpdateListener { animation ->
                val matrixScale=getMatrixScaleX()
                val animatedValue=animation.animatedValue as Float
                scaleMatrix.setScale(animatedValue,animatedValue)
                scaleHierarchyScroll(matrixScale,focusX,focusY)
            }
            addListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    postAction?.invoke()
                }
            })
            start()
        }
    }

    /**
     * 终止缩放动画
     */
    private fun cancelZoomAnimator() {
        val animator=zoomAnimator?:return
        animator.removeAllUpdateListeners()
        animator.removeAllListeners()
        animator.cancel()
    }

    /**
     * 缩放动画是否动行,用于与手势onFling区分状态
     */
    private fun zoomAnimatorRunning():Boolean{
        var isRunning=false
        if(null!=zoomAnimator){
            isRunning=zoomAnimator?.isRunning?:false
        }
        return isRunning
    }

    override fun computeHorizontalScrollRange(): Int {
        return (horizontalRange*getMatrixScaleX()-width).toInt()
    }

    override fun computeVerticalScrollRange(): Int {
        return (verticalRange*getMatrixScaleY()-height).toInt()
    }

    private fun getMatrixScaleX(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_X]
    }

    private fun getMatrixScaleY(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_Y]
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        cancelZoomAnimator()
        viewFlinger.abortAnimation()
        scaleFocusX =detector.focusX
        scaleFocusY =detector.focusY
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector){
        //回弹缩放
        val matrixScaleX = getMatrixScaleX()
        if(hierarchyMinScale>matrixScaleX){
            setHierarchyScaleInner(hierarchyMinScale, scaleFocusX,scaleFocusY,this::checkBorderAndScroll)
        } else if(hierarchyMaxScale<matrixScaleX){
            setHierarchyScaleInner(hierarchyMaxScale, scaleFocusX,scaleFocusY,this::checkBorderAndScroll)
        } else {
            checkBorderAndScroll()
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        //原始缩放比例
        val matrixScaleX = getMatrixScaleX()
        //缩放矩阵
        scaleMatrix.postScale(detector.scaleFactor, detector.scaleFactor, scaleFocusX,scaleFocusY)
        //传入原始比例,进行计算,一定需要上面原始比例
        scaleHierarchyScroll(matrixScaleX, scaleFocusX,scaleFocusY)
        return true
    }

    /**
     * 检测边界,并自动滚动到正常边界
     */
    private fun checkBorderAndScroll(){
        //缩放完毕后,检测当前屏幕位置,如果有一部分在范围外,自动滚动到范围内
        var destX=0
        var destY=0
        val horizontalScrollRange = computeHorizontalScrollRange()
        val verticalScrollRange = computeVerticalScrollRange()
        if(0>scrollX){
            destX=-scrollX
        } else if(horizontalScrollRange<scrollX){
            destX=horizontalScrollRange-scrollX
        }
        if(0>scrollY){
            destY=-scrollY
        } else if(verticalScrollRange<scrollY){
            destY=verticalScrollRange-scrollY
        }
        //开始滚动
        viewFlinger.startScroll(scrollX,scrollY,destX,destY)
    }

    /**
     * 缩放视图时,自动滚动位置
     */
    private fun scaleHierarchyScroll(matrixScale:Float, focusX:Float, focusY:Float) {
        //计算出放大中心点
        val scrollX = ((scrollX + focusX) / matrixScale * getMatrixScaleX())
        val scrollY = ((scrollY + focusY) / matrixScale * getMatrixScaleY())
        //动态滚动至缩放中心点
        scrollTo(((scrollX - focusX)).toInt(), ((scrollY - focusY)).toInt())
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val st=System.currentTimeMillis()
        //当前屏幕所占矩阵
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //绘制座位整体信息
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)
        //绘电影院座位
        forEachChild { drawSeatView(canvas, it, matrixScaleX, matrixScaleY) }
        //绘屏幕
        drawScreen(canvas, screenRect, matrixScaleX, matrixScaleY)
        //绘左侧指示器
        drawNumberIndicator(canvas, matrixScaleX, matrixScaleY)
        //绘当前座位描述
        drawSeatLayout(canvas)
        //绘缩略图
        drawPreView(canvas)
        debugLog("onDraw:${System.currentTimeMillis()-st}")

        if(DEBUG){
            val paint= Paint()
            paint.color=Color.RED
            paint.strokeWidth=4f
            //画焦点横线
            canvas.drawLine(scrollX+scaleGestureDetector.focusX-20,
                    scrollY+scaleGestureDetector.focusY,
                    scrollX+scaleGestureDetector.focusX+20,
                    scrollY+scaleGestureDetector.focusY,paint)
            //画焦点竖线
            canvas.drawLine(scrollX+scaleGestureDetector.focusX,
                    scrollY+scaleGestureDetector.focusY-20,
                    scrollX+scaleGestureDetector.focusX,
                    scrollY+scaleGestureDetector.focusY+20,paint)

        }
    }

    /**
     * 绘屏幕
     */
    private fun drawScreen(canvas: Canvas,screenRect:Rect,matrixScaleX: Float, matrixScaleY: Float,drawPreview:Boolean=false) {
        tmpRect.set((screenView.left * matrixScaleX).toInt(),
                (screenView.top * matrixScaleY).toInt(),
                (screenView.right * matrixScaleX).toInt(),
                (screenView.bottom * matrixScaleY).toInt())
        if (drawPreview||intersectsRect(screenRect, tmpRect)) {
            //如果屏幕在当前显示范围内,进行绘制
            canvas.save()
            canvas.scale(matrixScaleX, matrixScaleY)
            canvas.translate(screenView.left*1f,screenView.top*1f)
            screenView.draw(canvas)
            canvas.restore()
        }
    }

    /**
     * 绘左侧指示器
     */
    private fun drawNumberIndicator(canvas: Canvas, matrixScaleX: Float, matrixScaleY: Float) {
        canvas.save()
        canvas.scale(matrixScaleX, matrixScaleY)
        canvas.translate(scrollX/matrixScaleX+numberLayout.left, numberLayout.top.toFloat())
        numberLayout.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘座位信息布局
     */
    private fun drawSeatLayout(canvas: Canvas) {
        canvas.save()
        canvas.translate(scrollX*1f,scrollY*1f)
        seatLayout.draw(canvas)
        canvas.restore()
    }
    /**
     * 绘制当前屏幕内座位
     */
    private fun drawSeatView(canvas: Canvas,childView:View, matrixScaleX: Float, matrixScaleY: Float) {
        canvas.save()
        //按比例放大
        canvas.scale(matrixScaleX, matrixScaleY)
        canvas.translate(childView.left.toFloat(), childView.top.toFloat())
        val item=childView.tag as SeatNodeInfo
        childView.isSelected=item.select
        childView.draw(canvas)
        canvas.restore()
    }

    /**
     * 绘制预览图
     */
    private fun drawPreView(canvas: Canvas) {
        //绘制预览bitmap
        canvas.save()
        val offsetHeight=seatLayout.height*1f
        canvas.translate(0f, offsetHeight)
        canvas.drawBitmap(previewBitmap,scrollX*1f,scrollY*1f,null)
        //当前绘制区域大小
        val measuredWidth=computeHorizontalScrollRange()+width
        //预览尺寸比例
        val matrixScaleX = previewWidth/measuredWidth
        //绘制起始位置
        val left=scrollX+scrollX*matrixScaleX
        val top=scrollY+(scrollY+offsetHeight)*matrixScaleX
        //绘当前屏幕范围
        canvas.drawRect(left,top,left+width*matrixScaleX,top+(height-offsetHeight)*matrixScaleX,previewPaint)
        canvas.restore()
    }


    private fun intersectsRect(rect1:Rect,rect2:Rect):Boolean{
        return rect1.left < rect2.right && rect2.left < rect1.right && rect1.top < rect2.bottom && rect2.top < rect1.bottom;
    }

    private fun intersetsVerticalRect(rect1:Rect, rect2:Rect):Boolean{
        return rect1.top < rect2.bottom && rect2.top < rect1.bottom;
    }

    private fun intersetsHorizontalRect(rect1:Rect, rect2:Rect):Boolean{
        return rect1.left < rect2.right && rect2.left < rect1.right
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    /**
     * 取消按下控件状态
     */
    private fun releasePressView(){
        views.find { it.isPressed }?.let { it.isPressed=false }
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        views.find { it.isPressed }?.let { it.performClick() }
        releasePressView()
        return true
    }


    override fun onDown(e: MotionEvent): Boolean {
        viewFlinger.abortAnimation()
        val x=scrollX+e.x.toInt()
        val y=scrollY+e.y.toInt()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        run {
            forEachChild { view->
                tmpRect.set((view.left*matrixScaleX).toInt(),
                        (view.top*matrixScaleY).toInt(),
                        (view.right*matrixScaleX).toInt(),
                        (view.bottom*matrixScaleY).toInt())
                //选中状态下,不设置按下状态
                if(tmpRect.contains(x,y)){
                    debugLog("onDown:${view.isSelected}")
                    view.isPressed=true
                }
            }
        }
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        if(zoomAnimatorRunning()){
            return false
        } else {
            viewFlinger.onFling(velocityX,velocityY)
            return true
        }
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        //当正在进行缩放时,不触发滚动
        if(scaleGestureDetector.isInProgress){
            return false
        } else {
            scrollRange(distanceX, distanceY)
            return true
        }
    }

    /**
     * 滚动视图
     */
    private fun scrollRange(distanceX: Float, distanceY: Float) {
        val horizontalScrollRange = computeHorizontalScrollRange()
        val verticalScrollRange = computeVerticalScrollRange()
        //横轨滚动越界
        var distanceX = distanceX.toInt()
        if (0 > scrollX || scrollX > horizontalScrollRange) {
            //横向直接越界
            distanceX = 0
        } else if (scrollX < -distanceX) {
            //横向向左滚动阀值越界
            distanceX = -scrollX
        } else if (scrollX + distanceX > horizontalScrollRange) {
            //横向向右越界
            distanceX = horizontalScrollRange - scrollX
        }
        //纵向滚动越界
        var distanceY = distanceY.toInt()
        if (0 > scrollY || scrollY > verticalScrollRange) {
            distanceY = 0
        } else if (scrollY < -distanceY) {
            distanceY = -scrollY
        } else if (scrollY + distanceY > verticalScrollRange) {
            distanceY = verticalScrollRange - scrollY
        }
        scrollBy(distanceX, distanceY)
        invalidate()
        //释放按下控件状态
        releasePressView()
    }

    override fun onLongPress(e: MotionEvent) {
        //触发长按事件
        views.find { it.isPressed }?.let {
            if(it.isLongClickable){
                it.performLongClick()
            }
            it.isPressed=false
            invalidate()
        }
    }

    fun forEachChild(action:(View)->Unit)=views.forEach(action)

    fun forEachIndexed(action:(Int,View)->Unit)=views.forEachIndexed(action)

    inner class SeatNodeInfo(val row:Int,val column:Int){
        //节点排版顺序
        val layoutRect=Rect()
        //条目是否选中
        var select=false
    }

    inner class RecyclerBin{
        val scrapViews= LinkedList<View>()

        fun addScarpView(view:View){
            scrapViews.add(view)
        }

        fun detachAndScrapAttachedViews(){
            views.forEach(this::addScarpView)
            views.clear()
        }

        fun newViewWithMeasured(node: SeatNodeInfo):View{
            val adapter= adapter ?:throw NullPointerException("获取View时Adapter不能为空!")
            val parent=parent as ViewGroup
            val view=adapter.getSeatView(parent,node.row,node.column)
            adapter.bindSeatView(parent,view,node.row,node.column)
            measureChildWithMargins(view, MeasureSpec.AT_MOST, MeasureSpec.AT_MOST)
            view.tag=node
            return view
        }

        fun getView(node: SeatNodeInfo):View{
            val adapter= adapter ?:throw NullPointerException("获取View时Adapter不能为空!")
            val parent=parent as ViewGroup
            val view:View
            if(scrapViews.isEmpty()){
                view=adapter.getSeatView(parent,node.row,node.column)
                measureChildWithMargins(view,MeasureSpec.AT_MOST,MeasureSpec.AT_MOST)
            } else {
                view=scrapViews.pollFirst()
            }
            view.tag=node
            return view
        }

    }

    inner class ViewFlinger:Runnable{
        private val scroller=ScrollerCompat.create(context)

        override fun run() {
            if(!scroller.isFinished&&scroller.computeScrollOffset()){
                scrollTo(scroller.currX,scroller.currY)
                postInvalidate()
                postOnAnimation()
            }
        }

        fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            scroller.startScroll(startX, startY, dx, dy)
            postOnAnimation()
        }

        /**
         * 终止滚动
         */
        fun abortAnimation(){
            scroller.abortAnimation()
            postInvalidate()
        }

        fun onFling(velocityX: Float, velocityY: Float) {
            debugLog("onFling:$velocityX $velocityY")
            scroller.fling(scrollX,scrollY,-velocityX.toInt(),-velocityY.toInt(),0,computeHorizontalScrollRange(),0,computeVerticalScrollRange())
            postOnAnimation()
        }

        fun postOnAnimation() {
            removeCallbacks(this)
            ViewCompat.postOnAnimation(this@SeatTable, this)
        }
    }
    /**
     *
     */
    abstract class SeatTableAdapter(val table:SeatTable){
        /**
         * 获得顶部座位
         */
        abstract fun getHeaderSeatLayout(parent:ViewGroup):View
        /**
         * 获得屏幕控件
         */
        abstract fun getHeaderScreenView(parent:ViewGroup):View

        /**
         * 获得座位排左侧指示控件
         */
        abstract fun getSeatNumberView(parent:ViewGroup):View

        /**
         * 绑定座位序列
         */
        open fun bindSeatNumberView(view:View,row:Int)=Unit
        /**
         * 绑定序号列数据
         */
        open fun bindNumberLayout(numberLayout:ViewGroup)=Unit
        /**
         * 获得座位号
         */
        abstract fun getSeatView(parent:ViewGroup,row:Int,column:Int):View

        /**
         * 绑定座位数据
         */
        abstract fun bindSeatView(parent:ViewGroup,view:View,row:Int,column:Int)

        /**
         * 获得座位列数
         */
        abstract fun getSeatColumnCount():Int

        /**
         * 获得座位排数
         */
        abstract fun getSeatRowCount():Int

        /**
         * 获得横向多余空间
         */
        abstract fun getHorizontalSpacing(column:Int):Int

        /**
         * 获得纵向多余空间
         */
        abstract fun getVerticalSpacing(row:Int):Int

        /**
         * 某个座位是否可见
         */
        open fun isSeatVisible(row:Int,column:Int)=true

        /**
         * 获得当前座位节点信息
         */
        fun getSeatNodeItem(row:Int,column:Int)=table.seatArray[row][column]

        /**
         * 选中一个条目
         */
        fun setItemSelected(row:Int,column:Int,select:Boolean){
            table.setItemSelected(row,column,select)
        }

        fun setItemSelected(item:SeatNodeInfo,select:Boolean){
            table.setItemSelected(item,select)
        }

        fun getSeatNodeByView(v:View)=table.getSeatNodeByView(v)


    }


}