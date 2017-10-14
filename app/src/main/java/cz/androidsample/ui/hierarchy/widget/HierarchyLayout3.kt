package cz.androidsample.ui.hierarchy.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.StateListDrawable
import android.support.v4.view.ViewCompat
import android.support.v4.widget.ScrollerCompat
import android.util.AttributeSet
import android.view.*
import cz.androidsample.DEBUG
import cz.androidsample.R
import cz.androidsample.ui.hierarchy.model.HierarchyNode
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by cz on 2017/10/11.
 * 1:完成视图树绘制
 */
class HierarchyLayout3(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        View(context, attrs, defStyleAttr) ,ViewManager, ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private var MAX_SCALE=3.0f
    private var MIN_SCALE=0.2f
    private val scroller=ScrollerCompat.create(context)
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val tmpRect = Rect()
    private val linePaint=Paint(Paint.ANTI_ALIAS_FLAG)
    private var collectLineStrokeWidth =1f
    private var horizontalSpacing:Float=0f
    private var verticalSpacing:Float=0f
    //当前屏幕显示区域
    private val screenRect= Rect()
    private var m = FloatArray(9)
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()
    private var hierarchyAdapter:HierarchyAdapter?=null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.HierarchyLayout3).apply {
            setHorizontalSpacing(getDimension(R.styleable.HierarchyLayout3_hl_horizontalSpacing,0f))
            setVerticalSpacing(getDimension(R.styleable.HierarchyLayout3_hl_verticalSpacing,0f))
            setConnectLineColor(getColor(R.styleable.HierarchyLayout3_hl_connectLineColor,Color.WHITE))
            setConnectLineStrokeWidth(getDimension(R.styleable.HierarchyLayout3_hl_connectLineStrokeWidth,0f))
            recycle()
        }
    }

    fun setConnectLineColor(color: Int) {
        linePaint.color=color
        invalidate()
    }

    fun setConnectLineStrokeWidth(strokeWidth: Float) {
        collectLineStrokeWidth =strokeWidth
        invalidate()
    }

    fun setHorizontalSpacing(spacing: Float) {
        this.horizontalSpacing=spacing
        requestLayout()
    }
    fun setVerticalSpacing(spacing: Float) {
        this.verticalSpacing=spacing
        requestLayout()
    }

    open fun setAdapter(adapter:HierarchyAdapter){
        //清空所有view
        if(null!=hierarchyAdapter){
            hierarchyAdapter =null
            views.clear()
            requestLayout()
        }
        //重新排版控件树
        hierarchyAdapter = adapter
        //遍历节点树
        addHierarchyNodeView(adapter.root)
    }

    /**
     * 遍历当前节点信息
     * @param node 当前操作节点
     */
    private fun addHierarchyNodeView(node:HierarchyNode){
        //排版空间树
        val view=nextHierarchyView(node)
        if(null!=view){
            //添加并排版
            addView(view,null)
            //遍历子节点
            node.children.forEach(this::addHierarchyNodeView)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        views.forEach { measureChildWithMargins(it,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec)) }
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for(view in views){
            val node=view.tag as HierarchyNode
            val left=paddingLeft+node.level*horizontalSpacing+node.level*view.measuredWidth
            val centerDepth=node.startDepth+(node.endDepth-node.startDepth)/2
            val top=paddingTop+centerDepth*verticalSpacing+centerDepth *view.measuredHeight
            view.layout(left.toInt(), top.toInt(), left.toInt()+view.measuredWidth, top.toInt()+view.measuredHeight)
            //记录view排版矩阵
            node.layoutRect.set(left.toInt(), top.toInt(), left.toInt()+view.measuredWidth, top.toInt()+view.measuredHeight)
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

    fun getChildCount()=views.size

    fun getChildAt(index:Int)=views[index]

    fun indexOfChild(view:View)=views.indexOf(view)

    override fun addView(view: View, params: ViewGroup.LayoutParams?) {
        if(null!=params){
            view.layoutParams=params
        }
        views.add(view)
        requestLayout()
    }

    override fun updateViewLayout(view: View, params: ViewGroup.LayoutParams?) {
        view.layoutParams=params
        requestLayout()
    }

    override fun removeView(view: View) {
        views.remove(view)
        requestLayout()
    }

    override fun computeHorizontalScrollRange(): Int {
        val adapter=hierarchyAdapter?:return 0
        val depth=adapter.horizontalDepth
        var horizontalScrollRange=paddingLeft+paddingRight
        val childCount = getChildCount()
        if(0<childCount){
            val childView=getChildAt(0)
            horizontalScrollRange+= (depth*horizontalSpacing+(depth+1)*childView.measuredWidth).toInt()
        }
        return Math.max(0,(horizontalScrollRange*getMatrixScaleX()-width).toInt())
    }

    override fun computeVerticalScrollRange(): Int {
        val adapter=hierarchyAdapter?:return 0
        var verticalScrollRange=paddingTop+paddingBottom
        val childCount = getChildCount()
        if(0<childCount){
            val childView=getChildAt(0)
            val depth=adapter.root.childDepth
            verticalScrollRange= ((depth+1)*verticalSpacing+depth*childView.measuredHeight).toInt()
        }
        return Math.max(0,(verticalScrollRange*getMatrixScaleY()-height).toInt())
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
        //当前屏幕所占矩阵
        val matrixScaleX = getMatrixScaleX()
        val matrixScaleY = getMatrixScaleY()
        screenRect.set(scrollX,scrollY,scrollX+width,scrollY+height)
        forEachIndexed { index,childView->
            //是否绘制,仅确定,当前控件所在矩阵,与当前显示矩阵是否相交,如果不相交,不进行绘制
            tmpRect.set((childView.left*matrixScaleX).toInt(),
                    (childView.top*matrixScaleY).toInt(),
                    (childView.right*matrixScaleX).toInt(),
                    (childView.bottom*matrixScaleY).toInt())
            if(intersectsRect(screenRect, tmpRect)){
                canvas.save()
                //按比例放大,并绘制
                canvas.scale(matrixScaleX,matrixScaleY)
                canvas.translate(childView.left.toFloat(), childView.top.toFloat())
                childView.draw(canvas)
                canvas.restore()

                //绘连接线
                val node=childView.tag as HierarchyNode
                val layoutRect=node.layoutRect
                //检测父节点是否在当前屏幕内,不在也需要绘制连接线
                val parentNode=node.parent
                if(null!=parentNode){
                    drawConnectLine(canvas,node, parentNode.layoutRect, matrixScaleX,matrixScaleY)
                }
                //绘制子连接线
                node.children.forEach {
                    drawConnectLine(canvas,it, layoutRect, matrixScaleX,matrixScaleY)
                }
            }
        }

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
    }

    /**
     * 绘制连接线
     */
    private fun drawConnectLine(canvas: Canvas,it: HierarchyNode,  layoutRect: Rect, matrixScaleY: Float, matrixScaleX: Float) {
        val childRect = it.layoutRect
        //线宽按比例缩放
        linePaint.strokeWidth = collectLineStrokeWidth * matrixScaleX
        canvas.drawLine(layoutRect.right * matrixScaleX,
                (layoutRect.top + layoutRect.height() / 2f) * matrixScaleY,
                childRect.left * matrixScaleX,
                (childRect.top + childRect.height() / 2f) * matrixScaleY, linePaint)
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

    private fun nextHierarchyView(hierarchyNode:HierarchyNode):View?{
        val adapter= hierarchyAdapter ?:return null
        //装载父类,只用于inflate时,记录信息的父类,无其他作用
        val view=adapter.getView(parent as ViewGroup)
        adapter.bindView(view,hierarchyNode)
        if(null==view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        //记录node信息,因为这些view完全内维护,外围无法操作,所以不必操作tag信息
        view.tag=hierarchyNode
        return view
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