package cz.androidsample.ui.widget.seat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cz.androidsample.R
import cz.androidsample.debugLog
import cz.androidsample.ui.widget.SeatTable1
import cz.androidsample.ui.widget.SeatTable2
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.find
import java.text.DecimalFormat

/**
 * Created by cz on 2017/10/14.
 */
class MySeatTableAdapter2(val context: Context, table: SeatTable2, val row:Int, val column:Int): SeatTable2.SeatTableAdapter(table) {
    val decimalFormat=DecimalFormat("00")
    val layoutInflater:LayoutInflater =LayoutInflater.from(context)

    override fun getHeaderSeatLayout(parent:ViewGroup): View {
        return layoutInflater.inflate(R.layout.seat_table_header_layout,parent,false)
    }

    override fun getHeaderScreenView(parent:ViewGroup): View {
        return layoutInflater.inflate(R.layout.seat_table_screen_layout,parent,false)
    }

    override fun getSeatNumberView(parent:ViewGroup): View {
        return layoutInflater.inflate(R.layout.seat_table_number_item,parent,false)
    }

    override fun bindSeatNumberView(view: View, row: Int) {
        super.bindSeatNumberView(view, row)
        view.find<TextView>(R.id.numberText).text=decimalFormat.format(row+1)
    }

    override fun bindNumberLayout(numberLayout: ViewGroup) {
        super.bindNumberLayout(numberLayout)
        numberLayout.backgroundResource=R.drawable.number_indicator_shape
    }

    override fun getSeatView(parent:ViewGroup,row: Int, column: Int):View {
        return layoutInflater.inflate(R.layout.seat_table_item,parent,false)
    }

    override fun bindSeatView(parent: ViewGroup, view: View, row: Int, column: Int) {
        view.setOnClickListener {
            setSeatItemViewSelect(it,!isSeatItemViewSelected(it))
            Toast.makeText(context,"Row:$row Column:$column",Toast.LENGTH_SHORT).show()
        }
        view.setOnLongClickListener {
            Toast.makeText(context,"长按:$row Column:$column",Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun getSeatColumnCount(): Int {
        return column
    }

    override fun getSeatRowCount(): Int {
        return row
    }

    override fun getHorizontalSpacing(column: Int):Int {
        if(0==row%20||3==row){
            return 60
        }
        return 20
    }

    override fun getVerticalSpacing(row: Int):Int {
        if(0==row%100||6==row){
            return 108
        }
        return 36
    }

    override fun isSeatVisible(row: Int, column: Int): Boolean {
        //设定第10排,4->10个座位不可见
        return !(10==row&&(column in 4..10))
    }

}