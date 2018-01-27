package cz.androidsample.ui.calendar;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by cz on 16/8/29.
 * 日历对象
 */
public class CalendarDay implements Cloneable, Parcelable {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public boolean isSFestival;
    public String solarFestivalName;//公历节日
    public String solar24Term;//24节气

    public CalendarDay() {
    }

    public CalendarDay(long timeMillis) {
        Calendar calendar= Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTimeInMillis(timeMillis);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public long getTimeMillis(){
        Calendar calendar= Calendar.getInstance();
        calendar.set(year,month,day);
        return calendar.getTimeInMillis();
    }

    public CalendarDay(CalendarDay calendarDay) {
        year = calendarDay.year;
        month = calendarDay.month;
        day = calendarDay.day;
    }

    public CalendarDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    // WARNING: Dates before Oct. 1582 are inaccurate
    private int solarToInt(int y, int m, int d) {
        m = (m + 9) % 12;
        y = y - m / 10;
        return 365 * y + y / 4 - y / 100 + y / 400 + (m * 306 + 5) / 10 + (d - 1);
    }

    public void set(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getRealMonth(){
        return month+1;
    }

    @Override
    public int hashCode() {
        return compare(new CalendarDay(1970,0,1));
    }

    public int compare(CalendarDay calendarDay){
        Calendar calendar2 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        Calendar calendar1 = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar1.setTimeInMillis(calendar2.getTimeInMillis());
        calendar1.set(year, month, day);
        calendar2.set(calendarDay.year,calendarDay.month,calendarDay.day);
        return (int) ((calendar1.getTimeInMillis()-calendar2.getTimeInMillis())/1000/60/60/24);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDay calendarDay = (CalendarDay) o;
        return year == calendarDay.year && month == calendarDay.month && day == calendarDay.day;
    }

    @Override
    public String toString() {
        return year + " 年 " + (month + 1) + " 月 " + day + " 日";
    }

    @Override
    public CalendarDay clone() {
        return new CalendarDay(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.year);
        dest.writeInt(this.month);
        dest.writeInt(this.day);
        dest.writeInt(this.hour);
    }

    protected CalendarDay(Parcel in) {
        this.year = in.readInt();
        this.month = in.readInt();
        this.day = in.readInt();
        this.hour = in.readInt();
    }

    public static final Creator<CalendarDay> CREATOR = new Creator<CalendarDay>() {
        public CalendarDay createFromParcel(Parcel source) {
            return new CalendarDay(source);
        }

        public CalendarDay[] newArray(int size) {
            return new CalendarDay[size];
        }
    };
}