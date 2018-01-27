package cz.androidsample.ui.widget.calendar.adapter

import android.content.Context
import android.view.View
import cz.androidsample.ui.widget.calendar.model.DateTime

/**
 * Created by cz on 2018/1/27.
 */
abstract class CalendarAdapter{
    /**
     * 获取calendar子条目控件
     */
    abstract fun getItemView(context: Context, parent: View):View
    /**
     * 获取当前的日历天,此处可以更变日期信息
     */
    open fun getCalendarDay(item: DateTime)=item
    /**
     * 获取纵向边距空间
     */
    open fun getHorizontalSpacing(row:Int,spacing:Float)=spacing
    /**
     * 获取纵向边距空间
     */
    open fun getVerticalSpacing(column:Int,spacing:Float)=spacing
    /**
     * 绑定calendarDay
     */
    abstract fun bindCalendarDay(itemView:View,item: DateTime)
}