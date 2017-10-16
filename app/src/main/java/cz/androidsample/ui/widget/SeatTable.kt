package cz.androidsample.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.StateListDrawable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import android.widget.*
import cz.androidsample.DEBUG
import cz.androidsample.R
import cz.androidsample.debugLog
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
    private var MAX_SCALE=3.0f
    private var MIN_SCALE=1f
    private val scroller=ScrollerCompat.create(context)
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
            setHierarchyMaxScale(getFloat(R.styleable.HierarchyLayout4_hl4_hierarchyMaxScale,2f))
            setHierarchyMinScale(getFloat(R.styleable.HierarchyLayout4_hl4_hierarchyMinScale,0.6f))
            setHasFixSize(getBoolean(R.styleable.HierarchyLayout4_hl4_setHasFixedSize,true))
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
        //这个控件计算尺寸,忽略边距,因为他要铺满
        measureChildWithMargins(seatLayout,widthMode,heightMode,ignorePadding = true)
        measureChildWithMargins(screenView,widthMode,heightMode)
        for(view in views){
            measureChildWithMargins(view,widthMode,heightMode)
        }
        //计算整个电影院大小
        val itemView=getFirstView()
        var left=paddingLeft
        var top=paddingTop+seatLayout.measuredHeight+screenView.measuredHeight
        seatArray.forEachIndexed { row, array ->
            array.forEachIndexed { column, item->
                //排版同列控件
                item.layoutRect.set(left,top,left+itemView.measuredWidth,top+itemView.measuredHeight)
                left += adapter.getHorizontalSpacing(column)+itemView.measuredWidth
            }
            //整行换行,计算左,上偏移信息
            left=paddingLeft
            top+=itemView.measuredHeight+adapter.getVerticalSpacing(row)
        }
        horizontalRange=paddingLeft+paddingRight
        verticalRange=paddingTop+paddingBottom+screenView.measuredHeight
        (0..adapter.getSeatColumnCount()-1).forEach {
            horizontalRange += adapter.getHorizontalSpacing(it)+itemView.measuredWidth
        }
        (0..adapter.getSeatRowCount()-1).forEach {
            verticalRange += adapter.getVerticalSpacing(it)+itemView.measuredHeight
        }
        //测量指示器
        for(index in 0..numberLayout.childCount-1){
            val childView = numberLayout.getChildAt(index)
            measureChildWithMargins(childView,widthMode,heightMode)
        }
        measureChildWithMargins(numberLayout,widthMode,heightMode)
        debugLog("onMeasure:${seatLayout.measuredWidth} ${seatLayout.measuredHeight} ${numberLayout.measuredWidth} ${numberLayout.measuredHeight} $horizontalRange $verticalRange")
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        adapter?:return
        //排版顶部描述
        seatLayout.layout(0,0,seatLayout.measuredWidth,seatLayout.measuredHeight)
        val top=seatLayout.measuredHeight+paddingTop
        //排版屏幕位置
        screenView.layout((horizontalRange-screenView.measuredWidth)/2, top,
                (horizontalRange+screenView.measuredWidth)/2, top+screenView.measuredHeight)
        debugLog("onLayout:${numberLayout.measuredWidth} ${numberLayout.measuredHeight}")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
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
        drawSeatRange(Canvas(previewBitmap),matrixScaleX,matrixScaleY,true)
        //横向滚到中间
        scrollTo(computeHorizontalScrollRange()/2,0)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        //滚动时检测当前屏需要控件
        val adapter=adapter?:return
        val st=System.currentTimeMillis()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //移除当前屏内所有控件
        val parent=parent as ViewGroup
        //此处优化细节为,直接通过缩放比,以及控件尺寸,确定当前屏幕在,整个二维节点内的行与列,减少大量数量时的大量运算,仅适用于电影票选座
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)

        seatArray.forEachIndexed{ row, array->
            array.forEachIndexed { column,item->
                val layoutRect=item.layoutRect
                //是否绘制,仅确定,当前控件所在矩阵,与当前显示矩阵是否相交,如果不相交,不进行绘制
                tmpRect.set((layoutRect.left * matrixScaleX).toInt(),
                        (layoutRect.top * matrixScaleY).toInt(),
                        (layoutRect.right * matrixScaleX).toInt(),
                        (layoutRect.bottom * matrixScaleY).toInt())
                if (intersectsRect(screenRect, tmpRect)) {
                    var view = findViewByTag(item)
                    if (null != view) {
                        adapter.bindSeatView(parent,view, row,column)
                    } else {
                        //取一个新的控件,并运算
                        view = recyclerBin.newViewWithMeasured(item)
                        //添加控件
                        addView(view)
                    }
                    view.tag = item
                    //直接排,不排在指定矩阵内
                    view.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom)
                }
            }
        }
        debugLog("onScrollChanged:${System.currentTimeMillis()-st}")
    }
    private fun setChildPress(childView:View,press:Boolean){
        childView.isPressed = press
        val background=childView.background
        if(null!=background&&background is StateListDrawable){
            background.state = if(press) intArrayOf(android.R.attr.state_pressed) else intArrayOf(android.R.attr.state_empty)
            background.jumpToCurrentState()
            invalidate()
        }
    }
    /**
     * 获得第一个基准控件,作长宽模板使用
     */
    fun getFirstView()= views[0]

    fun getChildCount()=views.size

    fun getChildAt(index:Int)=views[index]

    fun indexOfChild(view:View)=views.indexOf(view)

    /**
     * 根据tag查找view,用于利用node逆向查找view,无法复写findViewWithTag方法,因为为final
     */
    private fun findViewByTag(tag:Any):View?{
        return views.find { it.tag==tag }
    }

    private fun addView(view: View) {
        if(null!=view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        views.add(view)
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

    override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector?) =Unit

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var scaleFactor=detector.scaleFactor
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        if(MIN_SCALE>scaleFactor*matrixScaleX){
            scaleFactor=MIN_SCALE/matrixScaleX
        } else if(MAX_SCALE<scaleFactor*matrixScaleX){
            scaleFactor=MAX_SCALE/matrixScaleX
        }
        scaleMatrix.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
        //计算出放大中心点
        val scrollX=((scrollX+detector.focusX)/matrixScaleX*getMatrixScaleX())
        val scrollY=((scrollY+detector.focusY)/matrixScaleY*getMatrixScaleY())
        //动态滚动至缩放中心点
        scrollTo(((scrollX-detector.focusX)).toInt(), ((scrollY-detector.focusY)).toInt())
        ViewCompat.postInvalidateOnAnimation(this)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val st=System.currentTimeMillis()
        //当前屏幕所占矩阵
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //绘制座位整体信息
        drawSeatRange(canvas, matrixScaleX, matrixScaleY)
        //绘当前座位描述
        drawSeatLayout(canvas)
        //绘缩略图
//        drawPreView(canvas,matrixScaleX,matrixScaleY)
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
     * 绘制座位整体信息,也用于制作缩略图
     */
    private fun drawSeatRange(canvas: Canvas, matrixScaleX: Float, matrixScaleY: Float,drawPreview:Boolean=false) {
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)
        //绘电影院座位
        drawSeat(canvas, screenRect, matrixScaleX, matrixScaleY,drawPreview)
        //绘屏幕
        drawScreen(canvas, screenRect, matrixScaleX, matrixScaleY)
        //绘左侧指示器
        drawNumberIndicator(canvas, matrixScaleX, matrixScaleY)
    }

    /**
     * 绘屏幕
     */
    private fun drawScreen(canvas: Canvas,screenRect:Rect,matrixScaleX: Float, matrixScaleY: Float) {
        tmpRect.set((screenView.left * matrixScaleX).toInt(),
                (screenView.top * matrixScaleY).toInt(),
                (screenView.right * matrixScaleX).toInt(),
                (screenView.bottom * matrixScaleY).toInt())
        if (intersectsRect(screenRect, tmpRect)) {
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
        canvas.translate(numberLayout.left.toFloat(), numberLayout.top.toFloat())
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
    private fun drawSeat(canvas: Canvas,screenRect:Rect,matrixScaleX: Float, matrixScaleY: Float,drawPreview:Boolean=false) {
        forEachChild {  childView ->
            //是否绘制,仅确定,当前控件所在矩阵,与当前显示矩阵是否相交,如果不相交,不进行绘制
            tmpRect.set((childView.left * matrixScaleX).toInt(),
                    (childView.top * matrixScaleY).toInt(),
                    (childView.right * matrixScaleX).toInt(),
                    (childView.bottom * matrixScaleY).toInt())
            if (drawPreview||intersectsRect(screenRect, tmpRect)) {
                canvas.save()
                //按比例放大
                canvas.scale(matrixScaleX, matrixScaleY)
                canvas.translate(childView.left.toFloat(), childView.top.toFloat())
                childView.draw(canvas)
                canvas.restore()
            }
        }
    }

    /**
     * 绘制预览图
     */
    private fun drawPreView(canvas: Canvas, matrixScaleX: Float, matrixScaleY: Float) {
        //绘制预览bitmap
        canvas.drawBitmap(previewBitmap,scrollX*1f,scrollY*1f,null)
        //当前绘制区域大小
        val measuredWidth=computeHorizontalScrollRange()+width
        val measuredHeight=computeVerticalScrollRange()+height
        //预览尺寸比例
        val matrixScaleX = previewWidth/measuredWidth
        //绘制起始位置
        val left=scrollX+scrollX*matrixScaleX
        val top=scrollY+scrollY*matrixScaleY
        //绘当前屏幕范围
        canvas.drawRect(left,top,left+width*matrixScaleX,top+height*matrixScaleY,previewPaint)
        debugLog("drawPreView:$left $top $matrixScaleX $matrixScaleY")
    }


    private fun intersectsRect(rect1:Rect,rect2:Rect):Boolean{
        return rect1.left < rect2.right && rect2.left < rect1.right && rect1.top < rect2.bottom && rect2.top < rect1.bottom;
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
        views.find { it.isPressed }?.let { setChildPress(it,false) }
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        views.find { it.isPressed }?.let { it.performClick() }
        releasePressView()
        return true
    }


    override fun onDown(e: MotionEvent): Boolean {
        scroller.abortAnimation()
        val x=scrollX+e.x.toInt()
        val y=scrollY+e.y.toInt()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        forEachChild { view->
            tmpRect.set((view.left*matrixScaleX).toInt(),
                    (view.top*matrixScaleY).toInt(),
                    (view.right*matrixScaleX).toInt(),
                    (view.bottom*matrixScaleY).toInt())
            if(tmpRect.contains(x,y)) run {
                setChildPress(view,true)
            }
        }
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        scroller.fling(scrollX,scrollY,-velocityX.toInt(),-velocityY.toInt(),0,computeHorizontalScrollRange(),0,computeVerticalScrollRange())
        invalidate()
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if(!scroller.isFinished&&scroller.computeScrollOffset()){
            scrollTo(scroller.currX,scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
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
            setChildPress(it,false)
            if(it.isLongClickable){
                it.performLongClick()
            }
        }
    }

    fun forEachChild(action:(View)->Unit)=views.forEach(action)

    fun forEachIndexed(action:(Int,View)->Unit)=views.forEachIndexed(action)

    inner class SeatNodeInfo(val row:Int,val column:Int){
        val layoutRect=Rect()
    }

    inner class RecyclerBin{
        val scrapViews= mutableListOf<View>()

        fun addScarpView(view:View){
            scrapViews.add(view)
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

    }
    /**
     *
     */
    open abstract class SeatTableAdapter{
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

    }


}