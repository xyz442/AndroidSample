package cz.androidsample.ui.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.IntDef;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import cz.androidsample.R;

/**
 * Created by cz on 8/29/16.
 */
public class CalendarView extends View {
    private static final String TAG = "CalendarView";


    private static final int WEEK_DAY_COUNT=7;
    private static final int AUTO_ITEM_HEIGHT=0;
    public static final int NONE=0x01;
    public static final int LEFT=0x02;
    public static final int TOP=0x04;
    public static final int RIGHT=0x08;
    public static final int BOTTOM=0x10;
    public static final int HORIZONTAL_DIVIDE=0x20;
    public static final int VERTICAL_DIVIDE=0x40;


    @IntDef(value={NONE,LEFT,TOP,RIGHT,BOTTOM,HORIZONTAL_DIVIDE,VERTICAL_DIVIDE})
    public @interface Gravity{
    }

    private final int NEXT_TOUCH=3;

    private final int[] WEEK_DAY=new int[]{7,1,2,3,4,5,6};
    private final HashMap<CalendarDay,String> calendarInfos;
    private final Calendar calendar;

    protected final Paint dividePaint;
    protected final TextPaint labelPaint;
    protected final TextPaint textPaint;
    protected CalendarDay calendarDay;
    protected final CalendarDay calendarToday;
    protected float itemPadding;
    protected int divideGravity;

    protected int todayColor;
    protected int selectTextColor;
    protected int textDisableColor;
    private int labelTextColor;
    protected int textColor;
    protected int itemHeight;
    private final String todayValue;
    private final int touchPosition;


    public CalendarView(Context context) {
        this(context, null, 0);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchPosition=-1;
        float[] fractions = new float[5];

        calendarInfos=new HashMap<>();
        dividePaint=new Paint(Paint.ANTI_ALIAS_FLAG);

        labelPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint=new TextPaint(Paint.ANTI_ALIAS_FLAG);
        calendarDay = new CalendarDay(System.currentTimeMillis());
        calendarToday = new CalendarDay(System.currentTimeMillis());
        calendar= Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(calendarDay.year, calendarDay.month, 1);
        todayValue="今天";


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        setItemHeight(a.getLayoutDimension(R.styleable.CalendarView_cv_itemHeight, R.styleable.CalendarView_cv_itemHeight));
        setItemTextColor(a.getColor(R.styleable.CalendarView_cv_itemTextColor, Color.BLACK));
        setItemTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemTextSize, 0));
        setItemTodayColor(a.getColor(R.styleable.CalendarView_cv_itemToDayColor, Color.WHITE));
        setItemLabelColor(a.getColor(R.styleable.CalendarView_cv_itemLabelTextColor, Color.WHITE));
        setItemLabelTextSize(a.getDimensionPixelSize(R.styleable.CalendarView_cv_itemLabelTextSize, 0));
        setItemPadding(a.getDimension(R.styleable.CalendarView_cv_itemPadding, 0));
        setItemSelectTextColor(a.getColor(R.styleable.CalendarView_cv_itemSelectTextColor, Color.WHITE));
        setItemDisableTextColor(a.getColor(R.styleable.CalendarView_cv_itemDisableTextColor, Color.GRAY));
        setItemDivideColor(a.getColor(R.styleable.CalendarView_cv_itemDivideColor, Color.DKGRAY));
        setItemDivideSize(a.getDimension(R.styleable.CalendarView_cv_itemDivideSize, 0));
        setDivideGravityInner(a.getInt(R.styleable.CalendarView_cv_divideGravity, NONE));
        a.recycle();
    }

    public void setItemTodayColor(int color) {
        this.todayColor=color;
        invalidate();
    }


    public void setItemLabelTextSize(int textSize) {
        this.labelPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemLabelColor(int color) {
        this.labelTextColor=color;
        this.labelPaint.setColor(color);
        invalidate();
    }


    public void setItemDivideSize(float size) {
        this.dividePaint.setStrokeWidth(size);
        invalidate();
    }

    public CalendarDay getCalendarDay(){
        return calendarDay;
    }


    public void setCalendarDay(CalendarDay day){
        calendarDay =day;
        calendar.set(day.year,day.month,1);
        requestLayout();
    }


    public void setItemHeight(int height) {
        this.itemHeight=height;
        requestLayout();
    }

    public void setItemPadding(float padding) {
        this.itemPadding=padding;
        invalidate();
    }

    public void setItemSelectTextColor(int color) {
        this.selectTextColor =color;
        invalidate();
    }

    public void setItemTextColor(int color) {
        this.textColor=color;
        invalidate();
    }

    public void setItemDisableTextColor(int color) {
        this.textDisableColor=color;
        invalidate();
    }

    public void setItemTextSize(int textSize) {
        this.textPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemDivideColor(int color) {
        dividePaint.setColor(color);
    }

    public void setDivideGravityInner(int gravity) {
        this.divideGravity=gravity;
        invalidate();
    }

    public void setDivideGravity(@Gravity  int gravity) {
        setDivideGravity(gravity);
    }

    public void addCalendarInfo(CalendarDay day,String text){
        addCalendarInfo(day.year, day.month, day.day, text);
    }

    public void addCalendarInfo(int year,int month,int day,String text){
        calendarInfos.put(new CalendarDay(year, month, day), text);
        invalidate();
    }


    public float getItemWidth() {
        return getMeasuredWidth()*1.0f/WEEK_DAY_COUNT;
    }

    public float getItemHeight(){
        return itemHeight;
    }

    public float getItemPadding(){
        return itemPadding;
    }


    public void clearCalendarInfo() {
        calendarInfos.clear();
        invalidate();
    }


    @Override
    public void invalidate() {
        if(hasWindowFocus()) super.invalidate();
    }

    @Override
    public void postInvalidate() {
        if(hasWindowFocus()) super.postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int column = getCalendarRow();
        if(MeasureSpec.EXACTLY==mode){
            //exactly height
            int measuredHeight = getMeasuredHeight();
            itemHeight=measuredHeight/column;
            setMeasuredDimension(getMeasuredWidth(),measuredHeight);
        } else {
            if(AUTO_ITEM_HEIGHT==itemHeight){
                //auto height
                itemHeight=getMeasuredWidth()/WEEK_DAY_COUNT;
            }
            setMeasuredDimension(getMeasuredWidth(), resolveSize(itemHeight*column, heightMeasureSpec));
        }
    }

    private int getCalendarRow() {
        calendar.set(calendarDay.year, calendarDay.month, 1);
        int totalDay= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+ WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        return (0 ==totalDay%WEEK_DAY_COUNT) ? totalDay / WEEK_DAY_COUNT:totalDay/WEEK_DAY_COUNT+1;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long l = System.currentTimeMillis();
        int row = getCalendarRow();
        drawDivide(canvas, row);
        drawCalendarText(canvas, row);
        Log.e(TAG,"time:"+(System.currentTimeMillis()-l));
    }

    private void drawCalendarText(Canvas canvas, int totalColumn) {
        calendar.set(calendarDay.year, calendarDay.month, 1);
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        int monthDays=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH,-startDay);
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;

        int total=WEEK_DAY_COUNT*totalColumn;
        int calendarCode=calendarDay.hashCode();
        int todayCode = calendarToday.hashCode();
        CalendarDay newDay;
        for(int i=1;i<=total;i++,calendar.add(Calendar.DAY_OF_MONTH,1)){
            newDay=new CalendarDay(calendar.getTimeInMillis());
            Lunar lunar = LunarSolarConverter.SolarToLunar(newDay);
            int column=(i-1)/WEEK_DAY_COUNT;
            int row=(i-1)%WEEK_DAY_COUNT;
            float startX=row*itemWidth;
            float startY=column*itemHeight;
            String text;
            int state;
            int newDayCode = newDay.hashCode();

            int CURRENT_MONTH = 1;
            if(newDayCode<calendarCode){
                //previous month
                int PREVIOUS_MONTH = 0;
                state= PREVIOUS_MONTH;
                textPaint.setColor(textDisableColor);
                text= String.valueOf(newDay.day);
            } else if(newDayCode>=calendarCode&&newDayCode<calendarCode+monthDays){
                //current days
                state= CURRENT_MONTH;
                if(todayCode==newDayCode){
                    text=todayValue;
                } else {
                    text= String.valueOf(newDay.day);
                }
                if(todayCode>newDayCode){
                    textPaint.setColor(textDisableColor);
                    labelPaint.setColor(textDisableColor);
                } else if(todayCode==newDayCode){
                    textPaint.setColor(todayColor);
                    labelPaint.setColor(labelTextColor);
                } else if(newDayCode-todayCode< getMaxBookDay()){
                    textPaint.setColor(textColor);
                    labelPaint.setColor(labelTextColor);
                } else {
                    textPaint.setColor(textDisableColor);
                    labelPaint.setColor(textDisableColor);
                }
            } else {
                //next days
                int NEXT_MONTH = 2;
                state= NEXT_MONTH;
                textPaint.setColor(textDisableColor);
                text= String.valueOf(newDay.day);
            }


            if(-1!=touchPosition&&touchPosition==column*WEEK_DAY_COUNT+row){
                canvas.drawCircle(startX+itemWidth/2,startY+itemHeight/2, Math.min(itemWidth,itemHeight)/2,dividePaint);
            }

            if(CURRENT_MONTH ==state){
                onPreDrawRect(canvas,textPaint,labelPaint,row,column,newDay,newDayCode);

                String calendarInfo;
                if(calendarInfos.containsKey(newDay)){
                    textPaint.setColor(selectTextColor);
                    calendarInfo = calendarInfos.get(newDay);
                } else if(!TextUtils.isEmpty(newDay.solarFestivalName)){
                    calendarInfo = newDay.solarFestivalName;
                } else if(!TextUtils.isEmpty(lunar.lunarFestivalName)){
                    calendarInfo = lunar.lunarFestivalName;
                } else if(!TextUtils.isEmpty(newDay.solar24Term)){
                    calendarInfo =  newDay.solar24Term;
                }  else {
                    calendarInfo = Lunar.getChinaDayString(lunar.lunarDay);
                }

                float textWidth =textPaint.measureText(text, 0, text.length());
                if(TextUtils.isEmpty(calendarInfo)){
                    canvas.drawText(text,startX+(itemWidth-textWidth)/2,
                            startY+(itemHeight - (textPaint.descent() + textPaint.ascent())) / 2,textPaint);
                } else {
                    canvas.drawText(text,startX+(itemWidth-textWidth)/2,startY+itemHeight/2-itemPadding,textPaint);

                    float textInfoWidth=labelPaint.measureText(calendarInfo, 0, calendarInfo.length());
                    float textHeight=-(labelPaint.descent() + labelPaint.ascent());
                    canvas.drawText(calendarInfo,startX+(itemWidth-textInfoWidth)/2,startY+itemHeight/2+textHeight+itemPadding,labelPaint);
                }
                onPostDrawRect(canvas,textPaint,labelPaint,row,column,newDay,newDayCode);
            }
        }
    }

    public int getMaxBookDay(){
        return 0;
    }

    public void onPreDrawRect(Canvas canvas, TextPaint textPaint, Paint labelPaint, int column, int row, CalendarDay day, int dayCode){
    }

    public void onPostDrawRect(Canvas canvas, TextPaint textPaint, Paint labelPaint, int column, int row, CalendarDay day, int dayCode){
    }

    private void drawDivide(Canvas canvas, int column) {
        int width = getWidth();
        int height = getHeight();
        float itemWidth=width*1.0f/WEEK_DAY_COUNT;

        int strokeWidth = (int) dividePaint.getStrokeWidth();
        int start= Math.round(strokeWidth * 1.0f / 2);
        //draw left divide
        if(0!=(divideGravity&LEFT)){
            canvas.drawLine(start,0,start,height,dividePaint);
        }
        //draw top divide
        if(0!=(divideGravity&TOP)){
            canvas.drawLine(0,start,width,start,dividePaint);
        }
        //draw right divide
        if(0!=(divideGravity&RIGHT)){
            canvas.drawLine(width-start,0,width-start,height,dividePaint);
        }
        //draw bottom divide
        if(0!=(divideGravity&BOTTOM)){
            canvas.drawLine(0,height-start,width,height-start,dividePaint);
        }
        //draw horizontal divide
        if(0!=(divideGravity&HORIZONTAL_DIVIDE)){
            for(int i=1;i<column;i++){
                canvas.drawLine(0,itemHeight*i,width,itemHeight*i,dividePaint);
            }
        }
        //draw vertical divide
        if(0!=(divideGravity&VERTICAL_DIVIDE)){
            for(int i=1;i<WEEK_DAY_COUNT;i++){
                canvas.drawLine(itemWidth*i,0,itemWidth*i,height,dividePaint);
            }
        }
    }

    private RectF getSelectRect(float x, float y){
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;
        int row= (int) (y/itemHeight);
        int column= (int) (x/itemWidth);
        return new RectF(column*itemWidth,row*itemHeight,(column+1)*itemWidth,(row+1)*itemHeight);
    }

    private int getSelectPosition(float x,float y){
        float itemWidth=getWidth()*1.0f/WEEK_DAY_COUNT;
        int row= (int) (y/itemHeight);
        int column= (int) (x/itemWidth);
        return row*WEEK_DAY_COUNT+column;
    }

    private boolean isInWindowRect(float x, float y) {
        int width = getWidth();
        int height = getHeight();
        return 0<=x&&x<=width&&0<=y&&y<=height;
    }

    public CalendarDay getCalendarByPosition(int position){
        calendar.set(calendarDay.year, calendarDay.month, 1);
        int startDay = WEEK_DAY[calendar.get(Calendar.DAY_OF_WEEK)-1]-1;
        if(position<startDay){
            //previous month
            calendar.add(Calendar.DAY_OF_MONTH, -(startDay-position));
        } else {
            //next days
            calendar.add(Calendar.DAY_OF_MONTH, position-startDay);
        }
        return new CalendarDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y =  event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                touchPosition=getSelectPosition(x,y);
//                startAnimator(ANIM_TOUCH,false,null);
//                RectF touchRect=getSelectRect(x,y);
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                //click item
//                final RectF rect = getSelectRect(x, y);
//                if(isInWindowRect(x,y)&&!rect.equals(touchRect)){
//                    //start new rect animator
//                    touchRect = rect;
//                    invalidate();
//                }
                break;
            case MotionEvent.ACTION_UP:
                int position = getSelectPosition(x, y);
                calendar.set(calendarDay.year, calendarDay.month, 1);
                onSelectDay(getCalendarByPosition(position));
                break;
        }
        return true;
    }

    protected void onSelectDay(CalendarDay day){
    }



}
