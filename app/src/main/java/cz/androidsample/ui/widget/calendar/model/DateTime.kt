package cz.androidsample.ui.widget.calendar.model

import android.os.Parcel
import android.os.Parcelable

import java.util.Calendar
import java.util.TimeZone

/**
 * Created by cz on 16/8/29.
 * 日期对象
 */
class DateTime : Cloneable, Parcelable {
    private var calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var isSFestival: Boolean = false
    var solarFestivalName: String? = null//公历节日
    var solar24Term: String? = null//24节气
    //获得当前时间
    val timeMillis: Long get()=calendar.timeInMillis

    constructor(timeMillis: Long) {
        calendar.timeInMillis = timeMillis
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH) + 1
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    constructor(dateTime: DateTime) {
        year = dateTime.year
        month = dateTime.month
        day = dateTime.day
    }

    constructor(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        calendar.set(year,month-1,day)
    }

    operator fun set(year: Int, month: Int, day: Int) {
        this.year = year
        this.month = month
        this.day = day
        calendar.set(year,month-1,day)
    }

    override fun hashCode(): Int {
        return compare(DateTime(1970, 0, 1))
    }

    fun compare(dateTime: DateTime): Int {
        val calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        val calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        calendar1.timeInMillis = calendar2.timeInMillis
        calendar1.set(year, month - 1, day)
        calendar2.set(dateTime.year, dateTime.month - 1, dateTime.day)
        return ((calendar1.timeInMillis - calendar2.timeInMillis) / 1000 / 60 / 60 / 24).toInt()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val dateTime = o as DateTime?
        return year == dateTime!!.year && month == dateTime.month && day == dateTime.day
    }

    override fun toString(): String {
        return year.toString() + " 年 " + month + " 月 " + day + " 日"
    }

    public override fun clone(): DateTime {
        return DateTime(this)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.year)
        dest.writeInt(this.month)
        dest.writeInt(this.day)
    }

    private constructor(`in`: Parcel) {
        this.year = `in`.readInt()
        this.month = `in`.readInt()
        this.day = `in`.readInt()
    }

    companion object {

        val CREATOR: Parcelable.Creator<DateTime> = object : Parcelable.Creator<DateTime> {
            override fun createFromParcel(source: Parcel): DateTime {
                return DateTime(source)
            }

            override fun newArray(size: Int): Array<DateTime?> = arrayOfNulls(size)
        }
    }
}