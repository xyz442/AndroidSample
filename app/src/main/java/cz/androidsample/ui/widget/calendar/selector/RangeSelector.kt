package cz.androidsample.ui.widget.calendar.selector

/**
 * Created by cz on 2018/1/27.
 */
class RangeSelector: ItemSelector<RangeSelector.SelectRange>() {

    //选中集
    private val range= SelectRange(NONE, NONE)

    override fun onItemSelected(position: Int) {
        if(NONE==range.start){
            range.start=position
        } else if(NONE==range.end){
            range.end=position
        } else {
            range.start= NONE
            range.end= NONE
        }
    }

    override fun setSelectedItem(item: SelectRange) {
        range.start=item.start
        range.end=item.end
    }

    override fun clearSelected() {
        range.start= NONE
        range.end= NONE
    }

    override fun getSelectedItem()=range


    class SelectRange(var start:Int,var end:Int)

}