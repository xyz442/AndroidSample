package cz.androidsample.ui.hierarchy

import android.os.Bundle

import cz.androidsample.R
import cz.androidsample.annotation.ToolBar
import cz.androidsample.ui.widget.seat.MySeatTableAdapter
import cz.volunteerunion.ui.ToolBarActivity
import kotlinx.android.synthetic.main.activity_seat_table.*

@ToolBar
class SeatTableActivity : ToolBarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_table)
        setTitle(intent.getStringExtra("title"))
        seatTable.setAdapter(MySeatTableAdapter(this))
    }
}
