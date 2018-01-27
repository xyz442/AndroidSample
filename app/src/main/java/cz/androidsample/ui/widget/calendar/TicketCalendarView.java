package cz.androidsample.ui.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import cz.androidsample.ui.widget.calendar.model.DateTime;

/**
 * Created by cz on 10/19/16.
 */
public class TicketCalendarView extends CalendarView {
    private static final int MAX_SELECT_DAY = 60;
    private final TextPaint tipPaint;
    private final Rect textRect;
    private Drawable selectDrawable;
    private Drawable todayDrawable;
    private Drawable tipDrawable;
    private DateTime selectDay;
    private int tipTextHorizontalPadding;
    private int tipTextVerticalPadding;
    private int drawableVerticalPadding;
    private int drawableHorizontalPadding;
    private int maxBookDay;
    private String tipText;
    private OnSelectItemListener listener;
    private int tipDays;

    public TicketCalendarView(Context context) {
        this(context,null,0);
    }

    public TicketCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TicketCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        maxBookDay =MAX_SELECT_DAY;
        textRect=new Rect();
        tipPaint =new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TicketCalendarView);
//        setItemSelectDrawable(a.getDrawable(R.styleable.TicketCalendarView_tv_itemSelectDrawable));
//        setTodayDrawable(a.getDrawable(R.styleable.TicketCalendarView_tv_todayDrawable));
//        setItemTipDrawable(a.getDrawable(R.styleable.TicketCalendarView_tv_itemTipDrawable));
//        setItemTipTextHorizontalPadding((int) a.getDimension(R.styleable.TicketCalendarView_tv_itemTipTextHorizontalPadding,0));
//        setItemTipTextVerticalPadding((int) a.getDimension(R.styleable.TicketCalendarView_tv_itemTipTextVerticalPadding,0));
//        setItemTipText(a.getString(R.styleable.TicketCalendarView_tv_itemTipText));
//        setItemTipTextColor(a.getColor(R.styleable.TicketCalendarView_tv_itemTipTextColor, Color.WHITE));
//        setItemTipTextSize(a.getDimension(R.styleable.TicketCalendarView_tv_itemTipTextSize, 0));
//        setItemDrawableHorizontalPadding((int) a.getDimension(R.styleable.TicketCalendarView_tv_itemDrawableHorizontalPadding,0));
//        setItemDrawableVerticalPadding((int) a.getDimension(R.styleable.TicketCalendarView_tv_itemDrawableVerticalPadding,0));
//        setItemTipDays(a.getInteger(R.styleable.TicketCalendarView_tv_itemTipDays,0));
//        a.recycle();
    }

    public void setItemTipDays(int days) {
        this.tipDays=days;
        invalidate();
    }

    public void setItemTipDrawable(Drawable drawable) {
        this.tipDrawable=drawable;
        invalidate();
    }

    public void setItemTipTextHorizontalPadding(int padding) {
        this.tipTextHorizontalPadding =padding;
        invalidate();
    }

    public void setItemTipTextVerticalPadding(int padding) {
        this.tipTextVerticalPadding=padding;
        invalidate();
    }

    public void setItemTipText(String text) {
        this.tipText=text;
        invalidate();
    }

    public void setItemTipTextColor(int color) {
        tipPaint.setColor(color);
        invalidate();
    }

    public void setItemTipTextSize(float textSize) {
        tipPaint.setTextSize(textSize);
        invalidate();
    }

    public void setItemDrawableHorizontalPadding(int padding) {
        this.drawableHorizontalPadding=padding;
        invalidate();
    }

    public void setItemDrawableVerticalPadding(int padding) {
        this.drawableVerticalPadding=padding;
        invalidate();
    }

    public void setSelectDay(DateTime selectDay){
        this.selectDay=selectDay;
        invalidate();
    }

    public void setTodayDrawable(Drawable drawable) {
        this.todayDrawable=drawable;
        invalidate();
    }

    public void setItemSelectDrawable(Drawable drawable) {
        this.selectDrawable=drawable;
    }

    @Override
    public void onPreDrawRect(Canvas canvas, TextPaint textPaint, Paint labelPaint, int column, int row, DateTime day, int dayCode) {
        super.onPreDrawRect(canvas, textPaint,labelPaint, column, row, day,dayCode);
        int itemWidth = Math.round(getItemWidth());
        if(null!=todayDrawable&&calendarToday.equals(day)){
            todayDrawable.setBounds(itemWidth* column +drawableHorizontalPadding,
                    this.itemHeight * row +drawableVerticalPadding,
                    itemWidth*(column +1)-drawableHorizontalPadding,
                    this.itemHeight *(row +1)-drawableVerticalPadding);
            todayDrawable.draw(canvas);
        }
        if(null!=selectDrawable&&day.equals(selectDay)){
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setColor(selectTextColor);
            labelPaint.setColor(selectTextColor);
            selectDrawable.setBounds(itemWidth* column +drawableHorizontalPadding,
                    this.itemHeight * row +drawableVerticalPadding,
                    itemWidth*(column +1)-drawableHorizontalPadding,
                    this.itemHeight *(row +1)-drawableVerticalPadding);
            selectDrawable.draw(canvas);
        }
    }

    @Override
    public void onPostDrawRect(Canvas canvas, TextPaint textPaint, Paint labelPaint, int column, int row, DateTime day, int dayCode) {
        super.onPostDrawRect(canvas, textPaint, labelPaint, column, row, day, dayCode);
        //检测是否在 tip day 范围内
        int todayCode = calendarToday.hashCode();
        int endCode=todayCode+ maxBookDay +tipDays;
        if(null!=tipDrawable&&
                !TextUtils.isEmpty(tipText)&&
                todayCode+ maxBookDay <=dayCode&&dayCode<endCode){
            int textWidth = (int) tipPaint.measureText(tipText);
            int itemWidth = (int) getItemWidth();
            tipPaint.getTextBounds(tipText,0,tipText.length(),textRect);
            int textHeight=textRect.height();
            int right=itemWidth*(column +1);
            int top=itemHeight * row;
            tipDrawable.setBounds(right-textWidth- tipTextHorizontalPadding *2,
                    top,
                    right,
                    top+textHeight+tipTextVerticalPadding*2);
            tipDrawable.draw(canvas);
            //绘文字
            canvas.drawText(tipText,right-textWidth-tipTextHorizontalPadding,top+textHeight+tipTextVerticalPadding,tipPaint);
        }
    }

    public void setMaxBookDay(int day){
        this.maxBookDay =day;
    }
    /**
     * 最大选中天数60天
     * @return
     */
    public int getMaxBookDay() {
        return maxBookDay +tipDays;
    }

    @Override
    protected void onSelectDay(DateTime day) {
        super.onSelectDay(day);
            //在今天与最大数之间
            int todayCode = calendarToday.hashCode();
            int endCode=todayCode+ maxBookDay +tipDays;
            int selectCode = day.hashCode();
            if(todayCode<=selectCode&&selectCode<endCode){
                if(null!=listener){
                    listener.onSelectDay(day);
                }
                selectDay=day;
                invalidate();
            }
    }

    public void setOnSelectItemListener(OnSelectItemListener listener){
        this.listener=listener;
    }

    public interface OnSelectItemListener{
        void onSelectDay(DateTime day);
    }
}
