package cz.androidsample.ui.widget.calendar.selector

/**
 * Created by cz on 2018/1/27.
 */
abstract class ItemSelector<T>{
    companion object{
        const val NONE=-1
    }
    /**
     * 一个条目位置选中
     */
    abstract fun onItemSelected(position:Int)

    /**
     * 设置选中条目
     */
    abstract fun setSelectedItem(item:T)

    /**
     * 清除选中状态
     */
    abstract fun clearSelected()

    /**
     * 获得选中条目
     */
    abstract fun getSelectedItem():T
}