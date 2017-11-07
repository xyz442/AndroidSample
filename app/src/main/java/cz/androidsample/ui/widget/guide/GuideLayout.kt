package cz.androidsample.ui.widget.guide

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.VelocityTrackerCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.*
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.Page
import cz.androidsample.ui.widget.element.PageLayout
import cz.androidsample.ui.widget.element.TAG
import cz.androidsample.ui.widget.guide.layoutmanager.GuideLayoutManager
import java.util.*

/**
 * Created by cz on 2017/10/26.
 * 引导页操作布局
 */
class GuideLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr) {
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    private val MIN_DISTANCE_FOR_FLING = 25 // dips\
    //滑动状态--------
    val SCROLL_STATE_IDLE = 0
    val SCROLL_STATE_DRAGGING = 1
    val SCROLL_STATE_SETTLING = 2
    private val items= mutableListOf<ItemInfo>()
    private var scrollState = SCROLL_STATE_IDLE
    private var pageChangeListenerItems:MutableList<ViewPager.OnPageChangeListener>?=null
    private var pagerAdapter: GuidePagerAdapter?=null
    private val recyclerBin=RecyclerBin()
    private var curPosition:Int=0
    private var scrollPosition:Int=0
    private var populatePending:Boolean=false
    private var offscreenPageLimit:Int=1
    //当前布局管理对象
    private var layoutManager:GuideLayoutManager?=null
    //手势拖动
    private var isBeingDragged: Boolean = false
    private var isUnableToDrag: Boolean = false
    private var touchSlop: Int = 0
    private var initialMotionX: Float = 0.toFloat()
    private var initialMotionY: Float = 0.toFloat()
    private var lastMotionX = 0f
    private var lastMotionY = 0f
    private var scrolling: Boolean = false

    private var velocityTracker: VelocityTracker? = null
    private var flingDistance: Int = 0
    private var minimumVelocity: Int = 0
    private var maximumVelocity: Int = 0
    //前景/背景
    private var backLayout:PageLayout?=null
    private var foreLayout:PageLayout?=null
    //是否启用滚动
    private var isScrollEnable=true
    //修饰滚动类
    private val decoratedScrollX:Int
        get() = layoutManager?.getDecoratedScrollX()?:0
    private val decoratedScrollY:Int
        get() = layoutManager?.getDecoratedScrollY()?:0
    val pageCount:Int
        get()= pagerAdapter?.count?:0

    init {
        val configuration = ViewConfiguration.get(context)
        touchSlop = configuration.scaledTouchSlop
        maximumVelocity = configuration.scaledMaximumFlingVelocity
        minimumVelocity = configuration.scaledMinimumFlingVelocity
        val density = context.resources.displayMetrics.density
        flingDistance = (MIN_DISTANCE_FOR_FLING * density).toInt()
    }

    //---------------------------------------------------------
    // 测量,排版
    //---------------------------------------------------------
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec),
                View.getDefaultSize(0, heightMeasureSpec))
        val childWidthSize = measuredWidth - paddingLeft - paddingRight
        val childHeightSize = measuredHeight - paddingTop - paddingBottom
        //测量前景/背景
        backLayout?.measure(widthMeasureSpec,heightMeasureSpec)
        foreLayout?.measure(widthMeasureSpec,heightMeasureSpec)
        //测量子控件
        (0..childCount - 1)
                .map { getChildAt(it) }
                .filter { it.visibility != View.GONE }
                .forEach { it.measure(View.MeasureSpec.makeMeasureSpec(childWidthSize, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(childHeightSize, View.MeasureSpec.EXACTLY)) }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //排版前景/背景
        backLayout?.layout(0,0,measuredWidth,measuredHeight)
        foreLayout?.layout(0,0,measuredWidth,measuredHeight)
        //排版子控件
        layoutManager?.onLayout(changed,l,t,r,b)
    }

    /**
     * 设置布局管理器
     */
    fun setLayoutManager(layoutManager: GuideLayoutManager){
        this.layoutManager=layoutManager
        layoutManager.onAttachedToWindow(this)
    }

    /**
     * 设置背景布局
     */
    fun setGuideBackgroundLayout(layout: PageLayout) {
        this.backLayout=layout
        //添加控件
        addView(layout,0,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
    }

    /**
     * 设置前景布局
     */
    fun setGuideForegroundLayout(layout: PageLayout) {
        this.foreLayout=layout
        addView(layout,childCount,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
    }

    override fun onDetachedFromWindow() {
        layoutManager?.onDetachedFromWindow(this)
        super.onDetachedFromWindow()
    }

    /**
     * 设置数据适配器
     */
    fun setAdapter(adapter: GuidePagerAdapter){
        if(null==layoutManager){
            Log.w(TAG,"Guide Layout Must set layoutManager!")
        } else {
            pagerAdapter?.let {
                scrollTo(0,0)
                curPosition =0
                recyclerBin.clearRecyclerBin()
            }
            pagerAdapter=adapter
            if (pagerAdapter != null) {
                //此处设置最小循环个数,判定其是否可以循环
                populatePending = false
                populate()
            }
        }
    }

    /**
     * 获得当前位置
     */
    fun getCurrentPosition()=curPosition

    /**
     * 获得当前滚动位置
     */
    fun getScrollPosition()= scrollPosition

    //---------------------------------------------------------
    // 准备数据
    //---------------------------------------------------------

    fun populate(){
        val adapter=pagerAdapter
        if (adapter == null||populatePending) {
            return
        }

        val N = adapter.count
        val pageLimit = offscreenPageLimit
        val startPos = Math.max(0, curPosition - pageLimit)
        val endPos = Math.min(N - 1, curPosition + pageLimit)

        //删除范围外条目
        items.filter { (it.position >= startPos || it.position <= endPos) && it.scrolling }.forEach {
            items.remove(it)
            destroyItem(it.position,it.page)
        }
        (startPos..endPos).forEach { pos->
            if(items.none { it.position==pos }){
                addNewItem(pos)
            }
        }
        Collections.sort<ItemInfo>(items){l1,l2->l1.position - l2.position}
    }

    private fun addNewItem(position: Int) {
        //此处检测缓存内是否有当前条目内容
        var itemInfo:ItemInfo
        var scrapPage = recyclerBin.getScrapPage(position)
        if(null==scrapPage){
            //获取一个新的页面
            scrapPage = pagerAdapter?.getPage(this, position) ?: throw NullPointerException("view is null!")
            itemInfo=ItemInfo(scrapPage,position)
        } else {
            //从缓存取出来分页己不需要初始化
            itemInfo=ItemInfo(scrapPage,position)
            itemInfo.init=true
        }
        //添加分页
        items.add(itemInfo)
        //如果添加了前景,则添加顺序往前-1
        var index=if(null!=foreLayout) childCount-1 else childCount
        debugLog("addNewItem:$position $curPosition ${scrapPage?.pageLayout}")
        //添加控件
        addView(scrapPage.pageLayout,index)
        //初始化
        scrapPage.pageInit?.invoke(scrapPage.pageLayout)
        //回调事件,将所有置为初始状态
        if(curPosition==position){
            setPageSelected(position)
        }
    }

    private fun startPageAnimator(itemInfo:ItemInfo){
        //执行初始化动画
        //执行起始动画,并在执行完成后,设置为可滑动
        val animatorSet=AnimatorSet()
        val pageAnimator = itemInfo.page.pageLayout.getPageAnimatorSet()
        if(null!=pageAnimator){
            //禁止操作
            isScrollEnable=false
            if(0==itemInfo.position){
                //第一次执行,将前景背景一并执行
                val backAnimatorSet = backLayout?.getPageAnimatorSet()
                val foreAnimatorSet = foreLayout?.getPageAnimatorSet()
                if(null!=backAnimatorSet){
                    animatorSet.play(backAnimatorSet).before(pageAnimator)
                }
                if(null!=foreAnimatorSet){
                    animatorSet.play(pageAnimator).before(foreAnimatorSet)
                }
            } else {
                //其他页操作
                animatorSet.playTogether(pageAnimator)
            }
            //执行动画,并在执行完后置为可滚动
            animatorSet.addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    isScrollEnable=true
                }
            })
            animatorSet.start()
            debugLog("addNewItem:$curPosition startAnim")
        }
    }

    /**
     * 获得当前元素在布局位置
     */
    fun getViewPosition(child:View):Int{
        val item: ItemInfo?=items.find { it.page.pageLayout==child }
        return item?.position?:-1
    }

    /**
     * 移除指定位置view
     */
    fun destroyItem(position:Int,page: Page?){
        val page=page?:return
        removeView(page.pageLayout)//移除控件
        recyclerBin.addScrapView(position,page)
    }

    /**
     * 设置当前页滚动到指定位置
     */
    internal fun pageScrollTo(x: Int, y: Int) {
        val layoutManager = layoutManager ?: return
        val consumed = IntArray(2)
        //此处以layoutManager处理滚动
        val offsetX=x-decoratedScrollX
        val offsetY=y-decoratedScrollY
        if(layoutManager.preScrollOffset(offsetX, offsetY, consumed)){
            //处理滚动
            layoutManager.onScrolled(offsetX+consumed[0],offsetY+consumed[1])
        }
        //回调滚动速率
        pageScrolled()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**
     * 页滚动回调
     */
    private fun pageScrolled() {
        //计算速率,并回传
        var position:Int=decoratedScrollX / width
        var offsetPixels=Math.abs(decoratedScrollX % width)
        val offset = Math.abs(offsetPixels.toFloat() / width)
        setPageScrolled(position,offset, offsetPixels)
    }


    /**
     * 滚动后重新计算
     */
    internal fun completeScrolled() {
        //终止动画
        var needPopulate=scrolling
        if(needPopulate){
            //终止动画
            layoutManager?.abortAnimation()
            setScrollState(SCROLL_STATE_IDLE)
        }
        populatePending = false
        for (i in items.indices) {
            val ii = items[i]
            if (ii.scrolling) {
                needPopulate = true
                ii.scrolling = false
            }
        }
        scrolling = false
        if(scrollPosition !=curPosition){
            scrollPosition =curPosition
            setPageSelected(curPosition)
        }
        if (needPopulate) {
            populate()
        }
    }

    /**
     * 设置页滚动速率
     */
    internal fun setPageScrolled(position:Int,pageOffset:Float,offsetPixels:Int) {
        debugLog("setPageScrolled:$position $pageOffset $offsetPixels")
        //回调前景背景变化
        foreLayout?.onPageScrolled(position,pageOffset,offsetPixels,false)
        backLayout?.onPageScrolled(position,pageOffset,offsetPixels,false)
        //回调对象变化
        val currentItem=items.find { it.position==position }
        val nextItem=items.find { it.position==position+1 }
        if(null!=currentItem&&currentItem.init){
            //回调当前页,与下一页
            currentItem.page?.onPageScrolled(position, pageOffset, offsetPixels,true)
        }
        if(null!=nextItem&&nextItem.init){
            nextItem.page?.onPageScrolled(position + 1, pageOffset, offsetPixels,false)
        }
        pageChangeListenerItems?.forEach { it.onPageScrolled(position, pageOffset, offsetPixels) }
    }

    internal fun setPageSelected(position: Int){
        debugLog("setPageSelected:$position")
        //执行初始动画
        if(position==curPosition){
            val itemInfo=items.find { it.position==position }
            if(null!=itemInfo&&!itemInfo.init){
                itemInfo.init=true
                post{startPageAnimator(itemInfo)}
            }
        }
        //回调前景背景元素
        foreLayout?.onPageSelected(position)
        backLayout?.onPageSelected(position)
        //回调选中事件
        val currentItem=items.find { it.position==position }
        currentItem?.page?.onPageSelected(position)
        pageChangeListenerItems?.forEach { it.onPageSelected(position) }
    }

    internal fun setScrollState(newState: Int) {
        if (scrollState != newState) {
            scrollState = newState
            pageChangeListenerItems?.forEach { it.onPageScrollStateChanged(newState) }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action and MotionEventCompat.ACTION_MASK
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            setCurrentItemInternal(curPosition, true,0)
            endDrag()
            return false
        }
        if (action != MotionEvent.ACTION_DOWN) {
            if (isBeingDragged) {
                return true
            }
            if (isUnableToDrag) {
                return false
            }
        }

        when (action) {
            MotionEvent.ACTION_MOVE -> {
                val x = ev.x
                val y = ev.y
                val dx = x - lastMotionX
                val startDiff =Math.abs(dx)
                val endDiff =Math.abs(dx)
                if (startDiff > touchSlop) {
                    isBeingDragged = true

                    if(!ViewCompat.canScrollHorizontally(this, (-dx).toInt())){
                        //滑动超过阀值时,拦截事件.不让子类处理,否则分引起滑到边界,而触发子类点击情况
                        return isBeingDragged
                    } else {
                        setScrollState(SCROLL_STATE_DRAGGING)
                        lastMotionX = if (dx > 0)
                            initialMotionX + touchSlop
                        else
                            initialMotionX - touchSlop
                        lastMotionY = y
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                } else if (endDiff > touchSlop){
                    isUnableToDrag = true
                }
            }
            MotionEvent.ACTION_DOWN -> {
                initialMotionX = ev.x
                initialMotionY = ev.y
                lastMotionX = initialMotionX
                lastMotionY = initialMotionY
                //此处问题,当滑动状态为滑动惯性状态时,且最后距离边距为拖动阀值外,表明正在滑动.否则则为初次滑动
                //此处关键点为,快速滑动时.当ViewPager嵌套ViewPager,子ViewPager滑出界,这时候如果子控件处于滑动惯性时.在最短距离内.让不让父ViewPager接管事件
                if (scrollState == SCROLL_STATE_SETTLING) {
                    isBeingDragged = true
                    isUnableToDrag = false
                    setScrollState(SCROLL_STATE_DRAGGING)
                } else {
                    completeScrolled()
                    isBeingDragged = false
                    isUnableToDrag = false
                }
            }
        }

        if (!isBeingDragged) {
            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker?.addMovement(ev)
        }
        return isBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //是否禁用事件
        if(!isScrollEnable) return false
        if (ev.action == MotionEvent.ACTION_DOWN && ev.edgeFlags != 0) {
            return false
        }
        val adapter=pagerAdapter
        val layoutManager=layoutManager
        if (adapter == null || adapter.count == 0||null==layoutManager) {
            return false
        }
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(ev)
        val action = ev.action
        when (action and MotionEventCompat.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                completeScrolled()
                // Remember where the motion event started
                initialMotionX = ev.x
                initialMotionY = ev.y
                lastMotionX = initialMotionX
                lastMotionY = initialMotionY
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isBeingDragged) {
                    val x = ev.x
                    val y = ev.y
                    val xDiff = Math.abs(x - lastMotionX)
                    val yDiff = Math.abs(y - lastMotionY)
                    val diff=xDiff
                    parent.requestDisallowInterceptTouchEvent(xDiff>=yDiff)
                    if (diff > touchSlop) {
                        isBeingDragged = true
                        parent.requestDisallowInterceptTouchEvent(true)
                        lastMotionX = x
                        lastMotionY = y
                        setScrollState(SCROLL_STATE_DRAGGING)
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                if (isBeingDragged) {
                    // Scroll to follow the motion event
                    val x = ev.x
                    val y = ev.y
                    var decoratedScrollX:Float = decoratedScrollX + (lastMotionX - x)
                    var decoratedScrollY = decoratedScrollY.toFloat()
                    lastMotionX = x
                    lastMotionY = y
                    val startWidthWithMargin = width

                    val itemCount = adapter.count - 1
                    val startBound = Math.max(0, curPosition - 1) * startWidthWithMargin.toFloat()
                    val endBound = Math.min(curPosition + 1,itemCount) * startWidthWithMargin.toFloat()
                    //限制滑动范围
                    if (decoratedScrollX < startBound) {
                        decoratedScrollX = startBound
                    } else if (decoratedScrollX > endBound) {
                        decoratedScrollX = endBound
                    }
                    lastMotionX += decoratedScrollX - decoratedScrollX.toInt()
                    pageScrollTo(decoratedScrollX.toInt(), decoratedScrollY.toInt())
                }
            }
            MotionEvent.ACTION_UP -> if (isBeingDragged) {
                val velocityTracker = velocityTracker
                velocityTracker?.computeCurrentVelocity(1000,maximumVelocity.toFloat())
                val initialVelocity= VelocityTrackerCompat.getXVelocity(velocityTracker, 0).toInt()
                populatePending = true
                //如果反向为负数,则加上-1,因为-110/500 = 0 而实际页数为-1
                val pageOffset = Math.abs(lastMotionX-initialMotionX) / width
                val x = ev.x
                val totalDelta = (x - initialMotionX).toInt()
                val nextPage = determineTargetPage(curPosition, pageOffset, initialVelocity, totalDelta)
                //计算出需要滚到的位置
                setCurrentItemInternal(nextPage, true, initialVelocity)
                endDrag()
            }
            MotionEvent.ACTION_CANCEL -> if(isBeingDragged){
                setCurrentItemInternal(curPosition, true,0)
                endDrag()
            }
        }
        return true
    }

    private fun determineTargetPage(currentPage: Int, pageOffset: Float, velocity: Int, deltaX: Int): Int {
        val targetPage: Int
        if (Math.abs(deltaX) > flingDistance && Math.abs(velocity) > minimumVelocity) {
            targetPage = if (velocity > 0) currentPage-1 else currentPage + 1
        } else {
            targetPage = (currentPage.toFloat() + pageOffset + 0.5f).toInt()
        }
        return targetPage
    }

    private fun setCurrentItemInternal(position: Int, smoothScroll: Boolean, velocity: Int) {
        var position = position
        val adapter=pagerAdapter
        val layoutManager=layoutManager
        if (adapter == null || adapter.count <= 0||null==layoutManager) {
            return
        }
        //限定访问边界
        if (position < 0) {
            position = 0
        } else if (position >= adapter.count) {
            position = adapter.count-1
        }
        val pageLimit = offscreenPageLimit
        if (position > curPosition + pageLimit || position < curPosition - pageLimit) {
            for (i in items.indices) {
                items[i].scrolling = true
            }
        }
        //记录操作前位置
        scrollPosition =curPosition
        //记录当前操作位置
        curPosition=position
        //记录开始滚动
        scrolling = true
        //记录位置
        populate()
        //滚动到指定位置
        layoutManager.setCurrentItem(position,smoothScroll,velocity)
    }

    private fun endDrag() {
        isBeingDragged = false
        isUnableToDrag = false
        if (velocityTracker != null) {
            velocityTracker?.recycle()
            velocityTracker = null
        }
    }


    //---------------------------------------------------------
    // 数据
    //---------------------------------------------------------
    class ItemInfo(var page: Page, var position: Int = 0){
        var scrolling: Boolean = false
        var init:Boolean =false
    }

    inner class RecyclerBin {
        val scrapItems= SparseArray<Page>()

        fun addScrapView(position: Int, page: Page) {
            scrapItems.put(position,page)
        }

        fun getScrapPage(position: Int): Page? {
            return scrapItems.get(position)
        }

        fun clearRecyclerBin() {
            this.scrapItems.clear()
        }
    }

    fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener){
        if(null== pageChangeListenerItems){
            pageChangeListenerItems = mutableListOf<ViewPager.OnPageChangeListener>()
        }
        pageChangeListenerItems?.add(listener)
    }

    fun removeOnPageChangeListener(listener: ViewPager.OnPageChangeListener){
        if(null!= pageChangeListenerItems){
            pageChangeListenerItems?.remove(listener)
        }
    }


    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

}