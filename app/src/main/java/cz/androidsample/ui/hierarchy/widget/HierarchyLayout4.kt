package cz.androidsample.ui.hierarchy.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.StateListDrawable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import cz.androidsample.DEBUG
import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.hierarchy.model.HierarchyNode
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by cz on 2017/10/16.
 * 此版本主要为了优化性能,主要有两个思路
 * 1:全局只给定一个view,所有信息,再设定一个信息包装对象来记录点击,背景,长按等.
 * 2:以类似listView控件复用原理,直接采取延持加载,以及控件复用机制.增强性能
 *
 * 本版本使用第二个方案,主要完成
 * 1:增加控件延持加载机制
 * 2:增加新的缩略图机制
 * 3:增加动画缩放,以及放大后,如果越界,自动回滚机制
 */
class HierarchyLayout4(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val viewFlinger=ViewFlinger()
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val tmpRect = Rect()
    private val linePaint=Paint(Paint.ANTI_ALIAS_FLAG).apply { style=Paint.Style.STROKE }
    private val linePath=Path()
    //这俩个字段,是为了确保弯曲线随比例放大缩小
    private var collectLineStrokeWidth =1f
    private var connectLineCornerPathEffect =0f
    private var connectLineEffectSize =0f
    //横纵向间隔空间
    private var horizontalSpacing:Float=0f
    private var verticalSpacing:Float=0f
    //所有节点信息
    private val hierarchyNoeItems= mutableListOf<HierarchyNode>()
    //当前屏幕显示区域
    private val screenRect= Rect()
    private var m = FloatArray(9)
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()
    private var hierarchyAdapter:HierarchyAdapter?=null
    private var horizontalRange=0
    private var verticalRange=0
    //是否为固定尺寸计算,如果是固定尺寸,会选择第0个控件的尺寸进行计算,这样在控件达到1000以上,性能相差会非常大
    //如果不为固定尺寸计算,会计算出每一个控件具体大小.有多少会控件会运算多少次measure,大概效果为4000 400毫秒,但onMeasure会回调多次
    private var hasFixSize=false

    //缩放限制区域
    private var hierarchyMaxScale=2.0f
    private var hierarchyMinScale=1.0f
    //缩放动画对象
    private var zoomAnimator:ValueAnimator?=null
    //缩放聚焦点,因为缩放过程中,会改变其值,为了体验平滑,记录最初值
    private var scaleFocusX =0f
    private var scaleFocusY =0f
    //预览图
    private val previewPaint=Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color=Color.RED
        strokeWidth=2f
        style=Paint.Style.STROKE
    }
    private lateinit var previewBitmap:Bitmap
    private var previewWidth=0f
    private var previewHeight=0f

    init {
        context.obtainStyledAttributes(attrs, R.styleable.HierarchyLayout4).apply {
            setHorizontalSpacing(getDimension(R.styleable.HierarchyLayout4_hl4_horizontalSpacing,0f))
            setVerticalSpacing(getDimension(R.styleable.HierarchyLayout4_hl4_verticalSpacing,0f))
            setConnectLineColor(getColor(R.styleable.HierarchyLayout4_hl4_connectLineColor,Color.WHITE))
            setConnectLineStrokeWidth(getDimension(R.styleable.HierarchyLayout4_hl4_connectLineStrokeWidth,0f))
            setConnectLineCornerPathEffect(getDimension(R.styleable.HierarchyLayout4_hl4_connectLineCornerPathEffect,0f))
            setConnectLineEffectSize(getDimension(R.styleable.HierarchyLayout4_hl4_connectLineEffectSize,0f))
            setPreViewWidth(getDimension(R.styleable.HierarchyLayout4_hl4_previewWidth,0f))
            setHierarchyMaxScale(getFloat(R.styleable.HierarchyLayout4_hl4_hierarchyMaxScale,2f))
            setHierarchyMinScale(getFloat(R.styleable.HierarchyLayout4_hl4_hierarchyMinScale,0.6f))
            setHasFixSize(getBoolean(R.styleable.HierarchyLayout4_hl4_setHasFixedSize,true))
            recycle()
        }
    }

    fun setHorizontalSpacing(spacing: Float) {
        this.horizontalSpacing=spacing
        requestLayout()
    }

    fun setVerticalSpacing(spacing: Float) {
        this.verticalSpacing=spacing
        requestLayout()
    }

    fun setConnectLineColor(color: Int) {
        linePaint.color=color
        invalidate()
    }

    fun setConnectLineStrokeWidth(strokeWidth: Float) {
        collectLineStrokeWidth =strokeWidth
        invalidate()
    }

    /**
     * 设置连接线曲线
     */
    fun setConnectLineCornerPathEffect(pathEffect: Float) {
        this.connectLineCornerPathEffect=pathEffect
        invalidate()
    }

    /**
     * 设置连接线曲线长度
     */
    fun setConnectLineEffectSize(effectSize: Float) {
        this.connectLineEffectSize=effectSize
        invalidate()
    }

    private fun setHierarchyMaxScale(hierarchyMaxScale: Float) {
        this.hierarchyMaxScale=hierarchyMaxScale
    }

    private fun setHierarchyMinScale(hierarchyMinScale: Float) {
        this.hierarchyMinScale=hierarchyMinScale
    }

    private fun setPreViewWidth(width: Float) {
        this.previewWidth=width
    }

    private fun setHasFixSize(hasFixSize: Boolean) {
        this.hasFixSize=hasFixSize
    }

    open fun setAdapter(adapter:HierarchyAdapter){
        //清空所有view
        if(null!=hierarchyAdapter){
            hierarchyAdapter =null
            views.clear()
            requestLayout()
        }
        //重新设置数据适配器
        hierarchyAdapter = adapter
        //清空所有节点
        hierarchyNoeItems.clear()
        //遍历节点树,并添加到hierarchyNoeItems内
        addHierarchyNodeView(adapter.root)
        /*
         添加第1个控件,此处第一个控件设计很复杂,因为如果需要做缩略图,一般来说是需要把整个控件树全都做出来,
         也就是第三个示例的做法,但里如果复用view的话,就不好采用那种方式,否则就必须一开始就取出所有控件,并排版了.这就没意义做复用了
         所以这里,以一个取巧的思路,全局一开始,会开始new出一个控件出来,以后的整个缩略图,完全以这个控件为原型初始化,实际效果一样的
        */
        addView(newViewWithMeasured(adapter.root))
        //使屏幕居中
        post { scrollTo(0,computeVerticalScrollRange()/2) }
    }

    /**
     * 遍历当前节点信息
     * @param node 当前操作节点
     */
    private fun addHierarchyNodeView(node:HierarchyNode){
        //排版空间树
        hierarchyNoeItems.add(node)
        //遍历子节点
        node.children.forEach(this::addHierarchyNodeView)
    }
    /**
     * 获得第一个基准控件,作长宽模板使用
     */
    fun getFirstView()= views[0]

    fun getChildAt(index:Int)= views[index]

    /**
     * 根据tag查找view,用于利用node逆向查找view,无法复写findViewWithTag方法,因为为final
     */
    private fun findViewByTag(tag:Any):View?{
        return views.find { it.tag==tag }
    }

    private fun addView(view: View) {
        if(null==view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        views.add(view)
    }

    override fun computeHorizontalScrollRange(): Int {
        return Math.max(0f,horizontalRange*getMatrixScaleX()-width).toInt()
    }

    override fun computeVerticalScrollRange(): Int {
        return Math.max(0f,verticalRange*getMatrixScaleY()-height).toInt()
    }

    private fun getMatrixScaleX(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_X]
    }

    private fun getMatrixScaleY(): Float {
        scaleMatrix.getValues(m)
        return m[Matrix.MSCALE_Y]
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val adapter=hierarchyAdapter?:return
        val st=System.currentTimeMillis()
        //测量己有控件大小
        views.forEach { measureChildWithMargins(it,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec)) }
        //测量当前屏幕尺寸大小,以一个控件为基准,测量出所有控件尺寸,以及可占用空间大小
        val view=getFirstView()//基准计算控件
        hierarchyNoeItems.forEachIndexed { _, node ->
            //绑定数据
            //在非固定尺寸情况下,计算控件不同信息大小
            if(!hasFixSize){
                adapter.bindView(view,node)
                //计算大小,此处测量耗时如果不测量,耗时3-4毫秒,测量耗时30-40毫秒,控件44个,所以为了性能考虑,应该让每一个控件大小一致,然后设置hasFixSize=true!
                measureChildWithMargins(view,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
            }
            val left=paddingLeft+node.level*horizontalSpacing+node.level*view.measuredWidth
            val centerDepth=node.startDepth+(node.endDepth-node.startDepth)/2
            val top=paddingTop+centerDepth*verticalSpacing+centerDepth *view.measuredHeight
            //记录view排版矩阵
            node.layoutRect.set(left.toInt(), top.toInt(), left.toInt()+view.measuredWidth, top.toInt()+view.measuredHeight)
            //记录最大节点右侧
            if(horizontalRange<node.layoutRect.right){
                horizontalRange=node.layoutRect.right+paddingRight
            }
            //记录最大节点底部位置
            if(verticalRange<node.layoutRect.bottom){
                verticalRange=node.layoutRect.bottom+paddingBottom
            }
        }
    }


    fun measureChildWithMargins(child: View, widthMode: Int, heightMode: Int) {
        val lp = child.layoutParams
        val widthSpec = getChildMeasureSpec(measuredWidth, widthMode, paddingLeft + paddingRight, lp.width)
        val heightSpec = getChildMeasureSpec(measuredHeight, heightMode, paddingTop + paddingBottom, lp.height)
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

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //绘制宽高
        val measuredWidth = computeHorizontalScrollRange() + width
        val measuredHeight = computeVerticalScrollRange() + height
        //此处按宽高比例重新设置previewHeight,因为配置比例与实际比例会有冲突,所以保留宽,高度自适应
        previewHeight=previewWidth/measuredWidth*measuredHeight
        //计算出预览图缩放比例
        previewBitmap = Bitmap.createBitmap(previewWidth.toInt(), previewHeight.toInt(), Bitmap.Config.ARGB_4444)
        //绘制预览图
        val matrixScaleX = previewWidth / measuredWidth
        val matrixScaleY = previewHeight / measuredHeight
        drawPreviewHierarchy(Canvas(previewBitmap),matrixScaleX,matrixScaleY)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        val adapter=hierarchyAdapter?:return
        val st=System.currentTimeMillis()
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //移除当前屏内所有控件
        screenRect.set(scrollX, scrollY, scrollX + width, scrollY + height)
        hierarchyNoeItems.forEach{ node ->
            val layoutRect=node.layoutRect
            //是否绘制,仅确定,当前控件所在矩阵,与当前显示矩阵是否相交,如果不相交,不进行绘制
            tmpRect.set((layoutRect.left * matrixScaleX).toInt(),
                    (layoutRect.top * matrixScaleY).toInt(),
                    (layoutRect.right * matrixScaleX).toInt(),
                    (layoutRect.bottom * matrixScaleY).toInt())
            if (intersectsRect(screenRect, tmpRect)) {
                var view=findViewByTag(node)
                if(null!=view){
                    adapter.bindView(view,node)
                } else{
                    //取一个新的控件,并运算
                    view=newViewWithMeasured(node)
                    //添加控件
                    addView(view)
                }
                view.tag=node
                //直接排,不排在指定矩阵内
                view.layout(layoutRect.left,layoutRect.top,layoutRect.right,layoutRect.bottom)
            }
        }
        debugLog("onScrollChanged:${System.currentTimeMillis()-st}")
    }


    /**
     * 绘制缩略图
     * @param canvas 绘制canvas对象,传入对象为预览canvas,则会绘制到bitmap上
     */
    private fun drawPreviewHierarchy(canvas: Canvas,matrixScaleX: Float, matrixScaleY: Float) {
        val firstView = getFirstView()
        hierarchyNoeItems.forEach{ node ->
            val layoutRect=node.layoutRect
            if(!hasFixSize){
                measureChildWithMargins(firstView,MeasureSpec.AT_MOST,MeasureSpec.AT_MOST)
            }
            firstView.layout(layoutRect.left,layoutRect.top,layoutRect.right,layoutRect.bottom)
            //记录节点信息
            firstView.tag=node
            //这里不用排版,不用顾忌不排版后点击错乱问题
            drawHierarchyView(canvas,firstView,matrixScaleX,matrixScaleY)
        }
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
            addListener(object :AnimatorListenerAdapter(){
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

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        cancelZoomAnimator()
        viewFlinger.abortAnimation()
        scaleFocusX =detector.focusX
        scaleFocusY =detector.focusY
        return true
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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val st=System.currentTimeMillis()
        //当前屏幕所占矩阵
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        //绘制视图
        forEachChild { drawHierarchyView(canvas,it,matrixScaleX, matrixScaleY) }
        //绘制预览图
        drawPreView(canvas)
        //绘制调试缩放中心点
        if(DEBUG){
            val paint=Paint()
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
        debugLog("onDraw:${System.currentTimeMillis()-st}")
    }

    /**
     * 绘制视图层级
     * @param canvas 绘制canvas对象,如果传入预览对象的canvas,则会绘制到bitmap上
     */
    private fun drawHierarchyView(canvas: Canvas,view:View, matrixScaleX: Float, matrixScaleY: Float) {
        val node=view.tag as HierarchyNode
        canvas.save()
        //按比例放大,并绘制
        canvas.scale(matrixScaleX, matrixScaleY)
        canvas.translate(node.layoutRect.left.toFloat(), node.layoutRect.top.toFloat())
        //进行绘制
        view.draw(canvas)
        canvas.restore()
        //绘连接线
        val layoutRect = node.layoutRect
        //检测父节点是否在当前屏幕内,不在也需要绘制连接线
        val parentNode = node.parent
        if (null != parentNode) {
            drawConnectLine(canvas, node, parentNode.layoutRect, matrixScaleX, matrixScaleY)
        }
        //绘制子连接线
        node.children.forEach {
            drawConnectLine(canvas, it, layoutRect, matrixScaleX, matrixScaleY)
        }
    }

    /**
     * 绘制预览图
     */
    private fun drawPreView(canvas: Canvas) {
        //绘制预览bitmap
        canvas.drawBitmap(previewBitmap,scrollX*1f,scrollY*1f,null)
        //当前绘制区域大小
        val measuredWidth=computeHorizontalScrollRange()+width
        val measuredHeight=computeVerticalScrollRange()+height
        //预览尺寸比例
        val matrixScaleX = previewWidth/measuredWidth
        val matrixScaleY = previewHeight/measuredHeight
        //绘制起始位置
        val left=scrollX+scrollX*matrixScaleX
        val top=scrollY+scrollY*matrixScaleY
        //绘当前屏幕范围
        canvas.drawRect(left,top,left+width*matrixScaleX,top+height*matrixScaleY,previewPaint)
    }

    /**
     * 绘制连接线
     */
    private fun drawConnectLine(canvas: Canvas,it: HierarchyNode,  layoutRect: Rect, matrixScaleY: Float, matrixScaleX: Float) {
        val childRect = it.layoutRect
        //线宽按比例缩放
        val connectLineCornerPathEffect= connectLineCornerPathEffect *matrixScaleX
        val connectLineEffectSize = connectLineEffectSize * matrixScaleX
        linePaint.strokeWidth = collectLineStrokeWidth * matrixScaleX
        linePaint.pathEffect=CornerPathEffect(connectLineCornerPathEffect)
        linePath.reset()
        //移到绘制处
        val left=layoutRect.right * matrixScaleX
        val leftTop=(layoutRect.top + layoutRect.height() / 2f) * matrixScaleY
        val right=childRect.left * matrixScaleX
        val rightTop=(childRect.top + childRect.height() / 2f) * matrixScaleY
        linePath.moveTo(left,leftTop)
        linePath.lineTo(left+connectLineEffectSize,leftTop)
        linePath.lineTo(right-connectLineEffectSize,rightTop)
        linePath.lineTo(right,rightTop)
        canvas.drawPath(linePath,linePaint)
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
        viewFlinger.abortAnimation()
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
        debugLog("onFling:${zoomAnimatorRunning()}")
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
            setChildPress(it,false)
            if(it.isLongClickable){
                it.performLongClick()
            }
        }
    }

    fun forEachChild(action:(View)->Unit)=views.forEach(action)

    fun newViewWithMeasured(node:HierarchyNode):View{
        val adapter= hierarchyAdapter ?:throw NullPointerException("获取View时Adapter不能为空!")
        val view=adapter.getView(parent as ViewGroup)
        adapter.bindView(view,node)
        measureChildWithMargins(view, MeasureSpec.AT_MOST, MeasureSpec.AT_MOST)
        view.tag=node
        return view
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
            ViewCompat.postOnAnimation(this@HierarchyLayout4, this)
        }
    }

    /**
     * Created by cz on 2017/10/13.
     * 视图数据适配器
     */
    open abstract class HierarchyAdapter(val root:HierarchyNode) {
        var horizontalDepth=0
        init {
            //分析出控件深度信息树,层级关系
            forEachHierarchy(root)
            //分析出横向纵深
            forEachHierarchyNodeHorizontalDepth(root)
        }

        private fun forEachHierarchy(node:HierarchyNode){
            val stack= LinkedList<HierarchyNode>()
            stack.add(node)
            while(!stack.isEmpty()){
                val child=stack.pollFirst()
                stack.addAll(child.children)
                //遍历深度
                forEachHierarchyDepth(child,child)
                child.startDepth=child.parent?.startDepth?:0
                //记录排序起始深度
                child.parent?.children?.takeWhile { it!=child }?.forEach { child.startDepth +=it.childDepth }
                //排列完成后,同列内当前节点前的节点的排列深度
                child.endDepth=child.startDepth+child.childDepth
            }
        }

        /**
         * 遍历节点深度
         */
        private fun forEachHierarchyDepth(node:HierarchyNode,eachNode:HierarchyNode){
            if(eachNode.children.isEmpty()){
                node.childDepth++
            } else {
                eachNode.children.forEach{ forEachHierarchyDepth(node,it) }
            }
        }

        /**
         * 遍历出横向纵深
         */
        private fun forEachHierarchyNodeHorizontalDepth(node:HierarchyNode){
            if(horizontalDepth<node.level){
                horizontalDepth=node.level
            }
            //递归遍历
            node.children.forEach(this::forEachHierarchyNodeHorizontalDepth)
        }

        /**
         * 获得绘制节点view
         */
        abstract fun getView(parent:ViewGroup): View

        /**
         * 绑定数据
         */
        abstract fun bindView(view:View,node: HierarchyNode)
    }


}