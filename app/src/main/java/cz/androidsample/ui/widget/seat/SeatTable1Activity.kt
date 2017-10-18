package cz.androidsample.ui.hierarchy

import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.widget.seat.MySeatTableAdapter1
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_seat_table1.*

@ToolBar
class SeatTable1Activity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_table1)
        setTitle(intent.getStringExtra("title"))
        seatTable.setAdapter(MySeatTableAdapter1(this, seatTable, 25, 40))
    }

}
