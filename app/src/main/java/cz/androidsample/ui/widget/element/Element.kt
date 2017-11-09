package cz.androidsample.ui.widget.element

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
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
    var layoutParams= PageLayoutParams()
    //保留表达式,而不是直接初始化,此处非常关键
    internal var animatorInit:(ElementAnimatorSet.()->Unit)?=null
    internal var animator:ElementAnimatorSet?=null
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
        //设置layoutParams
        view.layoutParams=layoutParams
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
     * 原始的lparams
     */
    fun Element<V>.lparams(width:Int= PageLayoutParams.WRAP_CONTENT,
                           height:Int= PageLayoutParams.WRAP_CONTENT,
                           source: (PageLayoutParams.()->Unit)?=null) {
        layoutParams= PageLayoutParams(width,height)
        if(null!=source){
            layoutParams.apply(source)
        }
    }

    /**
     * 原始的lparams
     */
    fun Element<V>.fillparams(source: (PageLayoutParams.()->Unit)?=null) {
        layoutParams= PageLayoutParams(PageLayoutParams.MATCH_PARENT, PageLayoutParams.MATCH_PARENT)
        if(null==source){
            //置为居中,方位才会生效
            layoutParams.alignRule=layoutParams.CENTER
        } else {
            layoutParams.apply(source)
        }
    }
}