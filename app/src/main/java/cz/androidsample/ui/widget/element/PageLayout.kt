package cz.androidsample.ui.widget.element

import android.animation.Animator
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.Resources
import android.support.constraint.ConstraintLayout
import android.support.constraint.solver.widgets.ConstraintWidget
import android.util.AttributeSet
import android.util.Log
import android.view.View
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet
import cz.androidsample.ui.widget.element.animator.ElementLayoutAnimatorSet

/**
 * Created by cz on 2017/10/27.
 * 分页操作排版布局
 */
class PageLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr){
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
            //转换为对象动画
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
            layout.animator= ElementLayoutAnimatorSet(layout).apply(animatorInit)
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
            val target=it.target
            target.alpha=animator.alpha
            target.scaleX=animator.scaleX
            target.scaleY=animator.scaleY
            target.rotation=animator.rotation
            target.rotationX=animator.rotationX
            target.rotationY=animator.rotationY
            target.translationX=animator.translationX
            target.translationY=animator.translationY
        }
    }

    override fun requestLayout() {
        for(i in 0..childCount-1) {
            val childView = getChildAt(i)
            val layoutParams = childView.layoutParams
            if (layoutParams is LayoutParams) {
                layoutParams.validate()
            }
        }
        super.requestLayout()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setChildrenMarginOffset()
//    }
//
//    /**
//     * 设置margin值,为确保ConstraintLayout.margin值为负值不生效
//     */
//    private fun setChildrenMarginOffset() {
//        for(i in 0..childCount-1){
//            val childView=getChildAt(i)
//            val layoutParams=childView.layoutParams
//            if (layoutParams is LayoutParams) {
//                layoutParams.validate()
//                val weight = weightField.get(layoutParams)
//                if (null != weight && weight is ConstraintWidget) {
//                    weight.anchors[0].margin = layoutParams.leftMargin
//                    weight.anchors[1].margin = layoutParams.topMargin
//                    weight.anchors[2].margin = layoutParams.rightMargin
//                    weight.anchors[3].margin = layoutParams.bottomMargin
//                }
//            }
//        }
//    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //使margin 负值生效
        (0..childCount-1).map { getChildAt(it) }.forEach {
            val layoutParams=it.layoutParams
            if(layoutParams is ConstraintLayout.LayoutParams){
                //横向偏移
                var horizontalOffset=0
                if(0>layoutParams.leftMargin){
                    horizontalOffset=layoutParams.leftMargin
                } else if(0>layoutParams.rightMargin){
                    horizontalOffset=-layoutParams.rightMargin
                }
                //纵向偏移
                var verticalOffset=0
                if(0>layoutParams.topMargin){
                    verticalOffset=layoutParams.topMargin
                } else if(0>layoutParams.bottomMargin){
                    verticalOffset=-layoutParams.bottomMargin
                }
                if(0!=horizontalOffset||0!=verticalOffset){
                    it.layout(it.left+horizontalOffset,it.top+verticalOffset,it.right+horizontalOffset,it.bottom+verticalOffset)
                }
            }
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
    internal inline fun onPageScrolled(position: Int, offset: Float, offsetPixels:Int, isSelectItem:Boolean){
        pageScrollItems.forEach{ it.call(position,offset,offsetPixels,isSelectItem) }
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
        fun call(position:Int,offset:Float,OffsetPixels:Int,isSelectItem:Boolean){
            listener(v,position,offset,OffsetPixels,isSelectItem)
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
}