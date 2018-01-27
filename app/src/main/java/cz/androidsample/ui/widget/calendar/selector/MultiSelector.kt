package cz.androidsample.ui.widget.calendar.selector

/**
 * Created by cz on 2018/1/27.
 * @param max 最大可选个数,超出则不可选
 */
class MultiSelector(val max:Int=-1): ItemSelector<List<Int>>() {
    //选中集
    private val selectedPositions = mutableListOf<Int>()

    override fun onItemSelected(position: Int) {
        //作最大数据限制
        if(NONE==max||this.selectedPositions.size<max){
            this.selectedPositions.add(position)
        }
    }

    override fun setSelectedItem(items: List<Int>) {
        this.selectedPositions.clear()
        this.selectedPositions.addAll(items)
    }

    override fun clearSelected() {
        selectedPositions.clear()
    }

    override fun getSelectedItem()= selectedPositions

}