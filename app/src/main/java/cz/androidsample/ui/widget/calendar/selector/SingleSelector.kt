package cz.androidsample.ui.widget.calendar.selector

/**
 * Created by cz on 2018/1/27.
 */
class SingleSelector: ItemSelector<Int>() {

    var position: Int= NONE

    override fun onItemSelected(position: Int) {
        this.position=position
    }

    override fun setSelectedItem(position: Int) {
        this.position=position
    }

    override fun clearSelected() {
        position=NONE
    }

    override fun getSelectedItem()=position

}