package cz.androidsample.ui.calendar.model;

/**
 * Created by joybar on 9/12/16.
 */
public class Lunar {
    public boolean isleap;
    public int lunarDay;
    public int lunarMonth;
    public int lunarYear;
    public boolean isLFestival;
    public String lunarFestivalName;//农历节日


    final static String chineseNumber[] =
            {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    final static String chineseTen[] = {"初", "十", "廿", "卅"};

    public static String getChinaDayString(int day) {
        String value;
        if (day > 30){
            value="";
        } else if (day == 10){
            value="初十";
        } else {
            int n = day % 10 == 0 ? 9 : day % 10 - 1;
            value=chineseTen[day / 10] + chineseNumber[n];
        }
        return value;
    }

    @Override
    public String toString() {
        return lunarYear+"-"+lunarMonth+"-"+lunarDay+" "+lunarFestivalName;
    }
}
