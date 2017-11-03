package cz.androidsample.ui.widget.element

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.view.View
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.element.animator.ElementAnimatorSet
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.backgroundResource

/**
 * Created by cz on 2017/10/26.
 */

open class Element<V:View>{
    val INVALID =-1
    var id:String=String()
    //背景资源
    var backgroundResources:Int= INVALID
    //背景颜色
    var backgroundColor:Int= INVALID
    //背景Drawable
    var backgroundDrawable: Drawable?=null
    //背景边距
    var paddingLeft=0
    var paddingTop=0
    var paddingRight=0
    var paddingBottom=0
    var padding=0
    var layoutParams=ElementLayoutParams()
    //保留表达式,而不是直接初始化,此处非常关键
    var animator:(ElementAnimatorSet.()->Unit)?=null
    //布局映射后view
    lateinit var target:View
    //元素行为回调对象
    internal var viewClick:((View)->Unit)?=null
    internal var pageSelected:((View,Int)->Unit)?=null
    internal var pageScrolled:((View,Int,Float,Int,Boolean)->Unit)?=null

    fun padding(left:Int=0,top:Int=0,right:Int=0,bottom:Int=0){
        this.paddingLeft=left
        this.paddingTop=top
        this.paddingRight=right
        this.paddingBottom=bottom
    }
    /**
     * 创建view
     */
    open fun getView(context: Context):V{
        val view=convertToView(context)
        initView(view as V)
        return view
    }

    fun onClick(action:(View)->Unit){
        this.viewClick=action
    }

    /**
     * 分页选中
     */
    fun onSelected(action:(View,Int)->Unit){
        this.pageSelected=action
    }

    /**
     * 分页滑动
     */
    fun onScrolled(action:(View,Int,Float,Int,Boolean)->Unit){
        this.pageScrolled=action
    }

    /**
     * 转换控件
     */
    open protected fun convertToView(context:Context)=View(context)

    open fun initView(view:V){
        //记录操作view
        target=view
        //设置id
        view.id=System.identityHashCode(id)
        //设置背景
        setBackground(view)
        //设置内边距
        setPadding(view,padding,paddingLeft,paddingTop,paddingRight,paddingBottom)
        //设置layoutParams,目前只支持转换为ConstraintLayout.LayoutParams
        setLayoutParams(view,layoutParams)
        //设置点击
        if(null!=viewClick){
            view.setOnClickListener { viewClick?.invoke(it) }
        }
    }

    /**
     * 设置背景
     */
    private fun setBackground(view: View) {
        if (INVALID != backgroundResources) {
            //设置资源
            view.backgroundResource = backgroundResources
        } else if (null != backgroundDrawable) {
            //设置drawable
            view.backgroundDrawable = backgroundDrawable
        } else if(INVALID !=backgroundColor){
            //设置颜色
            view.backgroundColor = backgroundColor
        }
    }

    /**
     * 设置内边距
     */
    private fun setPadding(view:View,padding: Int, paddingLeft: Int, paddingTop: Int, paddingRight: Int, paddingBottom: Int) {
        view.setPadding(if(0== paddingLeft) padding else paddingLeft,
                if(0== paddingTop) padding else paddingTop,
                if(0== paddingRight) padding else paddingRight,
                if(0== paddingBottom) padding else paddingBottom)
    }

    /**
     * 设置控件LayoutParams
     */
    private fun setLayoutParams(view: V,layoutParams:ElementLayoutParams) {
        //映射为对应关系
        val constrainLayoutParams=ConstraintLayout.LayoutParams(layoutParams.width, layoutParams.height)
        //设定内边距
        constrainLayoutParams.leftMargin=if(0==layoutParams.leftMargin) layoutParams.margin else layoutParams.leftMargin
        constrainLayoutParams.topMargin=if(0==layoutParams.topMargin) layoutParams.margin else layoutParams.topMargin
        constrainLayoutParams.rightMargin=if(0==layoutParams.rightMargin) layoutParams.margin else layoutParams.rightMargin
        constrainLayoutParams.bottomMargin=if(0==layoutParams.bottomMargin) layoutParams.margin else layoutParams.bottomMargin
        //以tag计算id
        var align=layoutParams.PARENT
        if(layoutParams.PARENT_ID !=layoutParams.align){
            align=System.identityHashCode(layoutParams.align)
        }
        debugLog("setLayoutParams:$align align:${layoutParams.align}")
        if(layoutParams.width==layoutParams.MATCH_PARENT){
            constrainLayoutParams.leftToLeft=layoutParams.PARENT
            constrainLayoutParams.rightToRight=layoutParams.PARENT
        }
        if(layoutParams.height==layoutParams.MATCH_PARENT){
            constrainLayoutParams.leftToLeft=layoutParams.PARENT
            constrainLayoutParams.rightToRight=layoutParams.PARENT
        }
        //横向占比
        if(0f!=layoutParams.horizontalPercent){
            constrainLayoutParams.horizontalBias=layoutParams.horizontalPercent
            constrainLayoutParams.leftToLeft=layoutParams.PARENT
            constrainLayoutParams.rightToRight=layoutParams.PARENT
        }
        //纵向占比
        if(0f!=layoutParams.verticalPercent){
            constrainLayoutParams.verticalBias=layoutParams.verticalPercent
            constrainLayoutParams.topToTop=layoutParams.PARENT
            constrainLayoutParams.bottomToBottom=layoutParams.PARENT
        }
        //方向左
        if(0!=(layoutParams.LEFT and layoutParams.alignRule)){
            constrainLayoutParams.leftToLeft=align
        } else if(0!=(layoutParams.LEFT_RIGHT and layoutParams.alignRule)){
            constrainLayoutParams.leftToRight=align
        }
        //方向上
        if(0!=(layoutParams.TOP and layoutParams.alignRule)){
            constrainLayoutParams.topToTop=align
        } else if(0!=(layoutParams.TOP_BOTTOM and layoutParams.alignRule)){
            constrainLayoutParams.topToBottom=align
        }
        //方向右
        if(0!=(layoutParams.RIGHT and layoutParams.alignRule)){
            constrainLayoutParams.rightToRight=align
        } else if(0!=(layoutParams.RIGHT_LEFT and layoutParams.alignRule)){
            constrainLayoutParams.rightToLeft=align
        }
        //方向下
        if(0!=(layoutParams.BOTTOM and layoutParams.alignRule)){
            constrainLayoutParams.bottomToBottom=align
        } else if(0!=(layoutParams.BOTTOM_TOP and layoutParams.alignRule)){
            constrainLayoutParams.bottomToTop=align
        }
        constrainLayoutParams.validate()
        view.layoutParams=constrainLayoutParams
    }

    /**
     * 原始的lparams
     */
    fun Element<V>.lparams(width:Int=ElementLayoutParams.WRAP_CONTENT,
                           height:Int=ElementLayoutParams.WRAP_CONTENT,
                           source: (ElementLayoutParams.()->Unit)?=null) {
        layoutParams=ElementLayoutParams(width,height)
        if(null!=source){
            layoutParams.apply(source)
        }
    }

    /**
     * 原始的lparams
     */
    fun Element<V>.fillparams(source: (ElementLayoutParams.()->Unit)?=null) {
        layoutParams=ElementLayoutParams(ElementLayoutParams.MATCH_PARENT,ElementLayoutParams.MATCH_PARENT)
        if(null==source){
            //置为居中,方位才会生效
            layoutParams.alignRule=layoutParams.CENTER
        } else {
            layoutParams.apply(source)
        }
    }
}