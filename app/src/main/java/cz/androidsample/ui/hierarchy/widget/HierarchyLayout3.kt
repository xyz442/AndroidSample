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
import cz.androidsample.debugLog
import cz.androidsample.ui.hierarchy.model.HierarchyNode
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
    private var MIN_SCALE=1f
    private val scroller=ScrollerCompat.create(context)
    private val scaleGestureDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, this)
    private val tmpRect = Rect()
    private var horizontalSpacing:Float=0f
    private var verticalSpacing:Float=0f
    //当前屏幕显示区域
    private val screenRect= Rect()
    private var m = FloatArray(9)
    private val scaleMatrix=Matrix()
    private val views=ArrayList<View>()
    private var adapter:HierarchyAdapter?=null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.HierarchyLayout3).apply {
            setHorizontalSpacing(getDimension(R.styleable.HierarchyLayout3_hl_horizontalSpacing,0f))
            setVerticalSpacing(getDimension(R.styleable.HierarchyLayout3_hl_verticalSpacing,0f))
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

    open fun setAdapter(adapter:HierarchyAdapter){
        //清空所有view
        this.adapter=null
        views.clear()
        requestLayout()
        val item=adapter.item
        //重新排版控件树
        this.adapter=adapter
        //遍历节点树
        forEachHierarchyNode(adapter,item.root,0,item.verticalLevel)
    }

    /**
     * 遍历当前节点信息
     * @param adapter 当前数据适配器
     * @param node 当前操作节点
     * @param startLevel 当前节点下子节点起始层级
     * @param endLevel 当前节点下节点结束层级
     */
    private fun forEachHierarchyNode(adapter:HierarchyAdapter, node:HierarchyNode, startLevel:Int, endLevel:Int){
        //排版空间树
        val view=nextHierarchyView(node)
        if(null!=view){
            //添加并排版
            addView(view,null)
            val depth=endLevel-startLevel
            //记录此节点深度
            node.depth=depth/2
            //子节点深度
            val itemDepth=depth/ node.children.size
            //遍历子节点
            node.children.forEachIndexed { index, node ->
                forEachHierarchyNode(adapter,node,index*itemDepth,(index+1)*itemDepth)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for(view in views){
            measureChildWithMargins(view,MeasureSpec.getMode(widthMeasureSpec),MeasureSpec.getMode(heightMeasureSpec))
        }
    }

    fun measureChildWithMargins(child: View, widthMode: Int, heightMode: Int) {
        val lp = child.layoutParams as ViewGroup.LayoutParams
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
            view.layout(0,0,view.measuredWidth,measuredHeight)
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
        val value=8
        return ((value*300*getMatrixScaleX()-width)).toInt()
    }

    override fun computeVerticalScrollRange(): Int {
        val value=8
        val childCount=getChildCount()
        val row=if(0==childCount%value) childCount/value else childCount/value+1
        return ((row*300*getMatrixScaleX()-height)).toInt()
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
                //按比例放大
                canvas.scale(matrixScaleX,matrixScaleY)
                canvas.translate(childView.left.toFloat(), childView.top.toFloat())
                childView.draw(canvas)
                canvas.restore()
                debugLog("onDraw index:$index rect:$screenRect rect:$tmpRect")
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
        val adapter=adapter?:return null
        val view=adapter.getView()
        adapter.bindView(view,hierarchyNode)
        if(null==view.layoutParams){
            view.layoutParams=ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        measureChildWithMargins(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        //记录node信息,因为这些view完全内维护,外围无法操作,所以不必操作tag信息
        view.tag=hierarchyNode
        return view
    }

    /**
     * 视图对象
     */
    class HierarchyItem(val root:HierarchyNode){
        //横向级
        var horizontalLevel=0
        //纵向级
        var verticalLevel=0
    }

    /**
     * Created by cz on 2017/10/13.
     * 视图数据适配器
     */
    open abstract class HierarchyAdapter(node:HierarchyNode) {
        internal val item:HierarchyItem=HierarchyItem(node.children[0])

        init {
            //分析出控件信息树,层级关系
            forEachHierarchyDepth(node)
        }

        private fun forEachHierarchyDepth(node:HierarchyNode){
            //横向层级判断
            if(item.horizontalLevel<node.level){
                item.horizontalLevel=node.level
            }
            //纵向层级++
            if(1==node.children.size){
                item.verticalLevel++
            } else if(0!=node.children.size){
                item.verticalLevel+=node.children.size+1
            }
            node.children.forEach(this::forEachHierarchyDepth)
        }
        /**
         * 获得绘制节点view
         */
        abstract fun getView(): View

        /**
         * 绑定数据
         */
        abstract fun bindView(view:View,node: HierarchyNode)
    }


}