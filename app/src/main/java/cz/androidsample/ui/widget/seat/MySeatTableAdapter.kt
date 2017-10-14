package cz.androidsample.ui.widget.seat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import cz.androidsample.R
import cz.androidsample.ui.widget.SeatTable
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Created by cz on 2017/10/14.
 */
class MySeatTableAdapter(val context: Context): SeatTable.SeatTableAdapter() {
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
        view.find<TextView>(R.id.numberText).text=decimalFormat.format(row)
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
            Toast.makeText(context,"Row:$row Column:$column",Toast.LENGTH_SHORT).show()
        }
    }

    override fun getSeatColumnCount(): Int {
        return 20
    }

    override fun getSeatRowCount(): Int {
        return 20
    }

    override fun getHorizontalSpacing(column: Int):Int {
        if(3==column){
            return 60
        }
        return 20
    }

    override fun getVerticalSpacing(row: Int):Int {
        if(6==row){
            return 108
        }
        return 36
    }

}