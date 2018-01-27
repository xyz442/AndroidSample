package cz.androidsample.ui.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cz.androidsample.ui.widget.calendar.model.DateTime;

/**
 * Created by cz on 10/19/16.
 */
public class HotelCalendarView extends CalendarView {
    private static final int MAX_SELECT_DAY =30;
    private static final int MAX_RANGE_DAY =20;
    private final Paint selectPaint;
    private float itemSelectRoundRadii;
    private int itemSelectTabColor;
    private int itemSelectRangeTextColor;
    private int itemSelectRangeColor;
    private DateTime selectCalendar;
    private final List<DateTime> selectCalendarItems;
    private OnCalendarItemClickListener listener;
    public HotelCalendarView(Context context) {
        this(context,null,0);
    }

    public HotelCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HotelCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        selectPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        selectCalendarItems =new ArrayList<>();
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HotelCalendarView);
//        setItemSelectTabColor(a.getColor(R.styleable.HotelCalendarView_cv_itemSelectTabColor, Color.BLUE));
//        setItemSelectRangeColor(a.getColor(R.styleable.HotelCalendarView_cv_itemSelectRangeColor, Color.DKGRAY));
//        setItemSelectRangeTextColor(a.getColor(R.styleable.HotelCalendarView_cv_itemSelectRangeTextColor, Color.BLUE));
//        setItemSelectRoundRadii(a.getDimension(R.styleable.HotelCalendarView_cv_itemSelectRoundRadii, 0));
//        a.recycle();
    }

    public void setItemSelectTabColor(int color) {
        this.itemSelectTabColor=color;
        invalidate();
    }

    public void setItemSelectRangeTextColor(int color) {
        this.itemSelectRangeTextColor=color;
        invalidate();
    }

    public void setItemSelectRoundRadii(float radius) {
        this.itemSelectRoundRadii=radius;
        invalidate();
    }

    public void setItemSelectRangeColor(int color) {
        this.itemSelectRangeColor=color;
        invalidate();
    }

    public void setSelectCalendarDay(DateTime newSelectDay){
        this.selectCalendar=newSelectDay;
        this.selectCalendarItems.add(newSelectDay);
        invalidate();
    }

    public void clearSelectCalendar() {
        this.selectCalendar=null;
        this.selectCalendarItems.clear();
        invalidate();
    }

    public int getMaxBookDay() {
        return MAX_SELECT_DAY;
    }

    @Override
    public void onPreDrawRect(Canvas canvas, TextPaint textPaint, Paint labelPaint, int column, int row, DateTime newDay, int dayCode) {
        super.onPreDrawRect(canvas,textPaint,labelPaint, column, row,newDay,dayCode);
        float itemWidth = getItemWidth();
        float itemHeight = getItemHeight();
        int newDayCode = newDay.hashCode();
        int todayCode = calendarToday.hashCode();
        //draw touch rect
        if(!selectCalendarItems.isEmpty()){
            int index = selectCalendarItems.indexOf(newDay);
            if(0==index){
                selectPaint.setColor(itemSelectTabColor);
                canvas.drawPath(getRoundRectPath(new RectF(itemWidth* column,itemHeight* row,itemWidth*(column +1),itemHeight*(row +1)), itemSelectRoundRadii, 0, itemSelectRoundRadii, 0), selectPaint);
            } else if(1==index){
                selectPaint.setColor(itemSelectTabColor);
                canvas.drawPath(getRoundRectPath(new RectF(itemWidth* column,itemHeight* row,itemWidth*(column +1),itemHeight*(row +1)), 0, itemSelectRoundRadii,0,itemSelectRoundRadii), selectPaint);
            }
            int size = selectCalendarItems.size();
            if(1==size){
                DateTime selectStartDay = selectCalendarItems.get(0);
                int selectCode = selectStartDay.hashCode();
                int endCode = todayCode+MAX_SELECT_DAY;
                if(endCode-selectCode>MAX_RANGE_DAY){
                    endCode=selectCode+MAX_RANGE_DAY;
                }
                if(newDayCode>selectCode&&newDayCode<endCode){
                    textPaint.setColor(textColor);
                } else {
                    textPaint.setColor(textDisableColor);
                }
            } else if(2==size){
                DateTime selectStartDay = selectCalendarItems.get(0);
                DateTime selectEndDay = selectCalendarItems.get(1);
                if(newDayCode>selectStartDay.hashCode()&&newDayCode<selectEndDay.hashCode()){
                    textPaint.setColor(itemSelectRangeTextColor);
                    selectPaint.setColor(itemSelectRangeColor);
                    canvas.drawRect(new RectF(itemWidth* column,itemHeight* row,itemWidth*(column +1),itemHeight*(row +1)),selectPaint);
                }
            }
        }
    }

    public Path getRoundRectPath(RectF rect, float tl, float tr, float bl, float br){
        Path path=new Path();
        path.moveTo(rect.left + tl ,rect.top);
        path.lineTo(rect.right - tr,rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + tr);
        path.lineTo(rect.right ,rect.bottom - br);
        path.quadTo(rect.right ,rect.bottom, rect.right - br, rect.bottom);
        path.lineTo(rect.left + bl, rect.bottom);
        path.quadTo(rect.left,rect.bottom,rect.left, rect.bottom - bl);
        path.lineTo(rect.left,rect.top + tl);
        path.quadTo(rect.left,rect.top, rect.left + tl, rect.top);
        path.close();
        return path;
    }

    @Override
    protected void onSelectDay(DateTime dateTime) {
        super.onSelectDay(dateTime);
        int size = selectCalendarItems.size();
        int todayCode = calendarToday.hashCode();
        int endCode=todayCode+MAX_SELECT_DAY;
        int selectCode = dateTime.hashCode();
        boolean touchInRect=false;
        if(0==size){
            //点击范围:当天-30
            touchInRect=selectCode>=todayCode&&selectCode<endCode;
        } else if(1==size){
            int startCode = selectCalendarItems.get(0).hashCode();
            touchInRect=selectCode>=todayCode&&selectCode<endCode&&selectCode-startCode<MAX_RANGE_DAY;
        } else if(2==size){
            touchInRect=true;
        }
        if(touchInRect){
            //同年/同月,日期小于当天,并小于下月初  or 同年/下一月
            if(2== selectCalendarItems.size()){
                selectCalendar=null;
                selectCalendarItems.clear();
                if(null!=listener){
                    listener.onItemClick(null, null, true);
                }
            } else if(null==selectCalendar){
                selectCalendarItems.clear();
                selectCalendar= dateTime;
                selectCalendarItems.add(dateTime);
                if(null!=listener){
                    listener.onItemClick(dateTime, null, false);
                }
            } else if(dateTime.equals(selectCalendar)){
                selectCalendarItems.remove(selectCalendar);
                selectCalendar=null;
                if(null!=listener){
                    listener.onItemClick(null,null,true);
                }
            } else if(0> dateTime.hashCode()-selectCalendar.hashCode()){
                Toast.makeText(getContext(), "选择日期异常!", Toast.LENGTH_SHORT).show();
            } else {
                selectCalendarItems.add(dateTime);
                if(null!=listener){
                    listener.onItemClick(selectCalendar, dateTime,false);
                }
            }
        }
        invalidate();
    }


    public void setOnCalendarItemClickListener(OnCalendarItemClickListener listener){
        this.listener=listener;
    }

    public interface OnCalendarItemClickListener{
        void onItemClick(DateTime dateTime1, DateTime dateTime2, boolean cancle);

    }
}
