package cz.androidsample.ui.widget.seat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_seat_table2.*

@ToolBar
class SeatTable2Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_table2)
        showSelectDialog()
    }

    /**
     * 弹出一个选择菜单
     */
    private fun showSelectDialog() {
        AlertDialog.Builder(this).
                setTitle(R.string.seat_table_info).
                setCancelable(false).
                setItems(resources.getStringArray(R.array.seat_array),{_, which ->
                    when(which){
                        0->seatTable.setAdapter(MySeatTableAdapter2(this, seatTable, 40000, 60000))
                        1->seatTable.setAdapter(MySeatTableAdapter2(this, seatTable, 4000, 6000))
                        2->seatTable.setAdapter(MySeatTableAdapter2(this, seatTable, 400, 600))
                        3->seatTable.setAdapter(MySeatTableAdapter2(this, seatTable, 40, 60))
                    }
                }).setPositiveButton(android.R.string.cancel, { dialog, _ -> dialog.dismiss()}).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_seat_table, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_item1){
            showSelectDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
