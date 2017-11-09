package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.solver.widgets.ConstraintWidget
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet
import cz.androidsample.ui.widget.element.animator.ElementLayoutAnimatorSet

/**
 * Created by cz on 2017/10/27.
 * 分页操作排版布局
 */
class PageLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(context, attrs, defStyleAttr){

    companion object{
        private val weightField=ConstraintLayout.LayoutParams::class.java.getDeclaredField("widget")
        init {
            weightField.isAccessible=true
        }
    }
    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)
    constructor(context: Context):this(context,null,0)
    internal val pageScrollItems= mutableListOf<OnPageScrollListener>()
    internal val pageSelectItems= mutableListOf<OnPageSelectListener>()
    internal val elementLayoutItems= mutableListOf<ElementLayout>()
    //初始化元素动画,此标记在于让所有控件添加完,并计算后,再初始化动画.确保动画内每个控件位置大小直接使用
    /**
     * 添加元素
     */
    fun addElementLayout(layout: ElementLayout) {
        //将元素属性映射为控件
        layout.elements.forEach {
            //元素映射view
            val view = it.getView(context)
            //回调子元素,在此处包装两个回调对象
            val pageScrolled = it.pageScrolled
            if(null!=pageScrolled){
                pageScrollItems.add(OnPageScrollListener(view,pageScrolled))
            }
            val pageSelected = it.pageSelected
            if(null!=pageSelected){
                pageSelectItems.add(OnPageSelectListener(view,pageSelected))
            }
            //添加view
            addView(view)
        }
        //添加布局元素集,因为此时还不能转换控件动画,须在onMeasured后,初始化
        elementLayoutItems.add(layout)
    }

    /**
     * 获得页动画集
     */
    internal fun getPageAnimatorSet():AnimatorSet{
        val animatorSet=AnimatorSet()
        elementLayoutItems.forEach {
            val pageAnimator = getPageAnimator(it)
            animatorSet.playTogether(pageAnimator)
        }
        return animatorSet
    }

    /**
     * 获得布局元素转换后动画体
     */
    internal fun getPageAnimator(layout: ElementLayout):Animator{
        //初始化所有子元素
        layout.elements.forEach(this::initElementAnimator)
        //初始化布局动画
        initElementLayoutAnimator(layout)
        //返回动画组
        val animator=layout.animator
        return if(null!=animator){
            animator.convert(this, this)
        } else {
            Log.w(TAG,"当前分页没有使用定义动画组,并行执行所有元素动画!")
            //没有控制组,直接执行全部动画
            val animator=AnimatorSet()
            layout.elements.forEach {
                if(null!=it.id&&null!=it.animator){
                    val target=findElement(it.id)
                    if(null!=target){
                        animator.playTogether(it.animator?.convert(this,target))
                    }
                }
            }
            animator
        }
    }

    private fun initElementLayoutAnimator(layout: ElementLayout) {
        val animatorInit = layout.animatorInit
        if(null!=animatorInit){
            val layoutAnimatorSet=ElementLayoutAnimatorSet(layout).apply(animatorInit)
            layout.animator=layoutAnimatorSet
            //设置初始化控件动画属性
            initElementAnimatorAttribute(this,layoutAnimatorSet)
        }
    }

    /**
     * 初始化子元素动画
     */
    private fun initElementAnimator(it: Element<*>) {
        val animatorInit = it.animatorInit
        if (null != animatorInit) {
            val animator = ElementAnimatorSet()
            //记录宽高
            animator.pwidth=measuredWidth
            animator.pheight=measuredHeight
            //记录id
            animator.elementId = it.id
            animator.apply(animatorInit)
            //赋予动画元素
            it.animator = animator
            //设置初始化控件动画属性
            initElementAnimatorAttribute(it.target, animator)
        }
    }

    /**
     * 初始化布局元素初始属性
     */
    private fun initElementAnimatorAttribute(target:View, animator: ElementAnimatorSet) {
        target.alpha = animator.alpha
        target.scaleX = animator.scaleX
        target.scaleY = animator.scaleY
        target.rotation = animator.rotation
        target.rotationX = animator.rotationX
        target.rotationY = animator.rotationY
        target.translationX = animator.translationX
        target.translationY = animator.translationY
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for(i in 0..childCount-1){
            val childView=getChildAt(i)
            val layoutParams = childView.layoutParams
            if(layoutParams is PageLayoutParams){
                val childWidthMeasureSpec = getChildWidthMeasureSpec(layoutParams, widthMeasureSpec)
                val childHeightMeasureSpec = getChildHeightMeasureSpec(layoutParams, heightMeasureSpec)
                childView.measure(childWidthMeasureSpec,childHeightMeasureSpec)
            }
        }
    }
    /**
     * 获取子孩子宽度的measureSpec
     */
    private fun getChildWidthMeasureSpec(layoutParams: PageLayoutParams,widthMeasureSpec:Int):Int {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode=MeasureSpec.getMode(widthMeasureSpec)
        return if(0<layoutParams.widthPercent){
            MeasureSpec.makeMeasureSpec(((measuredWidth-paddingLeft-paddingRight)*layoutParams.widthPercent).toInt(),MeasureSpec.EXACTLY)
        } else when (layoutParams.width) {
            LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(width-Math.max(0,layoutParams.leftMargin)-Math.max(0,layoutParams.rightMargin),MeasureSpec.EXACTLY)
            LayoutParams.WRAP_CONTENT-> MeasureSpec.makeMeasureSpec(layoutParams.width,MeasureSpec.AT_MOST)
            else ->{
                if(MeasureSpec.UNSPECIFIED==widthMode){
                    MeasureSpec.makeMeasureSpec(layoutParams.width,MeasureSpec.UNSPECIFIED)
                } else {
                    MeasureSpec.makeMeasureSpec(layoutParams.width,MeasureSpec.EXACTLY)
                }
            }
        }
    }

    /**
     * 获取子孩子高度的measureSpec
     */
    private fun getChildHeightMeasureSpec(layoutParams: PageLayoutParams,heightMeasureSpec:Int):Int {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode=MeasureSpec.getMode(heightMeasureSpec)
        return if(0<layoutParams.heightPercent){
            MeasureSpec.makeMeasureSpec(((measuredHeight-paddingTop-paddingBottom)*layoutParams.heightPercent).toInt(),MeasureSpec.EXACTLY)
        } else when (layoutParams.height) {
            LayoutParams.MATCH_PARENT -> MeasureSpec.makeMeasureSpec(height-Math.max(0,layoutParams.topMargin)-Math.max(0,layoutParams.bottomMargin),MeasureSpec.EXACTLY)
            LayoutParams.WRAP_CONTENT-> MeasureSpec.makeMeasureSpec(layoutParams.height,MeasureSpec.AT_MOST)
            else -> {
                if(MeasureSpec.UNSPECIFIED==heightMode){
                    MeasureSpec.makeMeasureSpec(layoutParams.height,MeasureSpec.UNSPECIFIED)
                } else {
                    MeasureSpec.makeMeasureSpec(layoutParams.height,MeasureSpec.EXACTLY)
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for(i in 0..childCount-1) {
            val childView = getChildAt(i)
            if(!isChildRequested(childView)){
                layoutChild(childView)
            }
        }
    }

    private fun isChildRequested(childView:View):Boolean{
        val layoutParams=childView.layoutParams
        return layoutParams is PageLayoutParams&&layoutParams.isLayoutRequested
    }


    private fun layoutChild(childView:View):Boolean{
        val layoutParams=childView.layoutParams
        if(layoutParams is PageLayoutParams){
            var align=layoutParams.PARENT
            if(layoutParams.PARENT_ID !=layoutParams.align){
                align=System.identityHashCode(layoutParams.align)
            }
            if(PageLayoutParams.PARENT==align){
                //依赖父容器控件,直接排版,为防止父容器left/right排版位置问题,此处直接传大小
                layoutAlignView(childView,this,paddingLeft,measuredWidth-paddingRight)
            } else {
                //依赖其他控件,继续操作
                val alignView=findViewById(align)
                if(null==alignView){
                    throw NullPointerException("Can't find align:$align")
                } else {
                    if(isChildRequested(alignView)){
                        //直接排版
                        layoutAlignView(childView,alignView,alignView.left,alignView.right)
                    } else {
                        val layoutChild = layoutChild(alignView)
                        if(layoutChild){
                            //排版自己
                            layoutAlignView(childView,alignView,alignView.left,alignView.right)
                        }
                    }
                }
            }
        }
        return true
    }

    /**
     * 排版依赖控件
     * @param alignLeft 依赖控件左侧位置
     * @param alignRight 依赖控件的右侧位置
     */
    private fun layoutAlignView(childView:View,alignView:View,alignLeft:Int,alignRight:Int){
        val layoutParams=childView.layoutParams
        if(layoutParams is PageLayoutParams){
            //横向占比
            var left=paddingLeft
            var top=paddingTop
            //横向占比
            if(0f!=layoutParams.horizontalPercent&&layoutParams.horizontalPercent in 0f..1f){
                left = ((measuredWidth-childView.measuredWidth)*layoutParams.horizontalPercent).toInt()
            } else if(0!=(layoutParams.LEFT and layoutParams.alignRule)&&
                    0!=(layoutParams.RIGHT and layoutParams.alignRule)){
                //居中
                left=(alignLeft+(alignView.width-childView.measuredWidth)/2)+layoutParams.leftMargin-layoutParams.rightMargin
            } else if(0!=(layoutParams.LEFT and layoutParams.alignRule)){
                //方向左
                left=alignLeft+layoutParams.leftMargin
            } else if(0!=(layoutParams.LEFT_RIGHT and layoutParams.alignRule)){
                left=alignRight+layoutParams.leftMargin
            } else if(0!=(layoutParams.RIGHT and layoutParams.alignRule)){
                //方向右,减去margin,而不是加
                left=(alignRight-childView.measuredWidth)-layoutParams.rightMargin
            } else if(0!=(layoutParams.RIGHT_LEFT and layoutParams.alignRule)){
                left=alignLeft-childView.measuredWidth-layoutParams.rightMargin
            }
            //纵向占比
            if(0f!=layoutParams.verticalPercent&&layoutParams.verticalPercent in 0f..1f){
                top = ((measuredHeight-childView.measuredHeight)*layoutParams.verticalPercent).toInt()
            } else if(0!=(layoutParams.TOP and layoutParams.alignRule)&&
                    0!=(layoutParams.BOTTOM and layoutParams.alignRule)){
                top=(alignView.top+(alignView.height-childView.measuredHeight)/2)+layoutParams.topMargin-layoutParams.bottomMargin
            } else if(0!=(layoutParams.TOP and layoutParams.alignRule)){
                //方向上
                top=alignView.top+layoutParams.topMargin
            } else if(0!=(layoutParams.TOP_BOTTOM and layoutParams.alignRule)){
                top=alignView.bottom+layoutParams.topMargin
            } else if(0!=(layoutParams.BOTTOM and layoutParams.alignRule)){
                //方向下,减去margin,而不是加
                top=alignView.bottom-childView.measuredHeight-layoutParams.bottomMargin
            } else if(0!=(layoutParams.BOTTOM_TOP and layoutParams.alignRule)){
                top=alignView.top-childView.measuredHeight-layoutParams.bottomMargin
            }
            //设置排版完成
            layoutParams.isLayoutRequested=true
            //排版控件
            childView.layout(left,top,left+childView.measuredWidth,top+childView.measuredHeight)
            Log.e(TAG,"layoutView:$left $top ${left+childView.measuredWidth} ${top+childView.measuredHeight}")
        }
    }


    override fun onDetachedFromWindow() {
        pageSelectItems.clear()
        pageScrollItems.clear()
        super.onDetachedFromWindow()
    }

    /**
     * 查找元素控件
     */
    fun findElement(id:String?): View?=findViewById(System.identityHashCode(id))

    /**
     * 回调滚动事件
     */
    internal inline fun onPageScrolled(position: Int, offset: Float, offsetPixels:Int,current:Boolean){
        pageScrollItems.forEach{ it.call(position,offset,offsetPixels,current) }
    }

    /**
     * 回调选中事件
     */
    internal inline fun onPageSelected(position: Int){
        pageSelectItems.forEach{ it.call(position) }
    }

    /**
     * 页滚动变化
     */
    inner class OnPageScrollListener(val v:View,val listener:(View,Int,Float,Int,Boolean)->Unit){
        fun call(position:Int,offset:Float,OffsetPixels:Int,current:Boolean){
            listener(v,position,offset,OffsetPixels,current)
        }
    }

    /**
     * 页选中变化
     */
    inner class OnPageSelectListener(val v:View,val listener:(View,Int)->Unit){
        fun call(position:Int){
            //回调变化
            listener(v,position)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return LayoutParams(context,attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}