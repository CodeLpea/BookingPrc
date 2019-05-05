package com.example.lp.myapplication.View.ClockView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lp.myapplication.View.BaseView;

import java.lang.ref.WeakReference;

import java.util.TimeZone;

public class ClockView extends BaseView {
    private Paint ClockPaint;
    private Paint TextPaint;
    //View的默认大小，dp
    private static final int DEFAULT_SIZE=320;

    //表盘边缘的颜色
    private int aroundCloclor= Color.parseColor("#083476");

    //表盘中心点的颜色
    private int clockCenterColor=Color.parseColor("#008577");

    //表盘边缘线的宽度
    private int aroundStockWidth=12;

    //字体的大小
    private int TextSize=28;

    //时间
    private volatile Time time;

    private float hour;
    private float minute;
    private float second;

    private Rect rect = new Rect();

    private TimerHandler timerHandler;
    private static final int MSG_INVALIDATE = 10;

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initClockPaint();//初始化时钟
        initTextPaint();//初始化文字
        time = new Time();
        timerHandler = new TimerHandler(this);

    }

    private void initClockPaint() {
        ClockPaint=new Paint();
        ClockPaint.setStyle(Paint.Style.STROKE);
        ClockPaint.setAntiAlias(true);
        ClockPaint.setDither(true);
        ClockPaint.setStrokeWidth(aroundStockWidth);
    }

    private void initTextPaint() {
        TextPaint=new Paint();
        TextPaint.setStyle(Paint.Style.FILL);
        TextPaint.setAntiAlias(true);
        TextPaint.setDither(true);
        TextPaint.setStrokeWidth(12);
        TextPaint.setTextAlign(Paint.Align.CENTER);
        TextPaint.setTextSize(TextSize);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultSize = dp2px(DEFAULT_SIZE);
        int widthSize = getSize(widthMeasureSpec, defaultSize);
        int heightSize = getSize(heightMeasureSpec, defaultSize);
        widthSize = heightSize = Math.min(widthSize, heightSize);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //中心点的横纵坐标
        float pointWH=getWidth()/2.0f;
        //内圆的半径
        float radiusIn=pointWH-aroundStockWidth;

        canvas.translate(pointWH,pointWH);

        //绘制表盘
        if(aroundStockWidth>0){
            ClockPaint.setStrokeWidth(aroundStockWidth);
            ClockPaint.setStyle(Paint.Style.STROKE);
            ClockPaint.setColor(aroundCloclor);
            canvas.drawCircle(0,0,pointWH-aroundStockWidth/2.0f,ClockPaint);
        }
        ClockPaint.setStyle(Paint.Style.FILL);
        ClockPaint.setColor(Color.WHITE);

        //绘制小短线
        canvas.save();
        canvas.rotate(-90);

        float longLineLength = radiusIn / 16.0f;
        float longStartY = radiusIn - longLineLength;
        float longStopY = longStartY - longLineLength;
        float longStockWidth = 2;
        float temp = longLineLength / 4.0f;
        float shortStartY = longStartY - temp;
        float shortStopY = longStopY + temp;
        float shortStockWidth = longStockWidth / 2.0f;
        ClockPaint.setColor(Color.BLACK);
        float degrees = 6;
        for (int i = 0; i <= 360; i += degrees) {
            if (i % 30 == 0) {
                ClockPaint.setStrokeWidth(longStockWidth);
                canvas.drawLine(0, longStartY, 0, longStopY, ClockPaint);
            } else {
                ClockPaint.setStrokeWidth(shortStockWidth);
                canvas.drawLine(0, shortStartY, 0, shortStopY, ClockPaint);
            }
            canvas.rotate(degrees);
        }
        canvas.restore();

        //绘制时钟数字
        if (TextSize > 0) {
            float x, y;
            for (int i = 1; i <= 12; i += 1) {
                TextPaint.getTextBounds(String.valueOf(i), 0, String.valueOf(i).length(), rect);
                float textHeight = rect.height();
                float distance = radiusIn - 2 * longLineLength - textHeight;
                double tempVa = i * 30.0f * Math.PI / 180.0f;
                x = (float) (distance * Math.sin(tempVa));
                y = (float) (-distance * Math.cos(tempVa));
                canvas.drawText(String.valueOf(i), x, y + textHeight / 3, TextPaint);
            }
        }

        canvas.rotate(-90);

        ClockPaint.setStrokeWidth(2);
        //绘制时针
        canvas.save();
        canvas.rotate(hour / 12.0f * 360.0f);
        canvas.drawLine(-30, 0, radiusIn / 2.0f, 0, ClockPaint);
        canvas.restore();
        //绘制分针
        canvas.save();
        canvas.rotate(minute / 60.0f * 360.0f);
        canvas.drawLine(-30, 0, radiusIn * 0.7f, 0, ClockPaint);
        canvas.restore();
        //绘制秒针
        ClockPaint.setColor(Color.parseColor("#fff2204d"));
        canvas.save();
        canvas.rotate(second / 60.0f * 360.0f);
        canvas.drawLine(-30, 0, radiusIn * 0.85f, 0, ClockPaint);
        canvas.restore();
        //绘制中心小圆点
        ClockPaint.setStyle(Paint.Style.FILL);
        ClockPaint.setColor(clockCenterColor);
        canvas.drawCircle(0, 0, radiusIn / 20.0f, ClockPaint);



    }

    private void onTimeChanged() {
        time.setToNow();
        minute = time.minute;
        hour = time.hour + minute / 60.0f;
        second = time.second;
    }


    /*设置表盘的颜色*/
    public void setAroundColor(int ranColor) {
        this.aroundCloclor = ranColor;
        invalidate();
    }
    /*表盘边缘线的宽度*/
    public void setAroundStockWidth(int ranColor) {
        this.aroundStockWidth=ranColor;
        invalidate();

    }
    /*设置字体的大小*/
    public void setClockTextSize(int ranColor) {
        this.TextSize=ranColor;
        TextPaint.setTextSize(TextSize);
        invalidate();
        invalidate();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(TAG, "onDetachedFromWindow");
        stopTimer();
        unregisterTimezoneAction();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.e(TAG, "onVisibilityChanged visibility: " + visibility);
        if (visibility == View.VISIBLE) {
            registerTimezoneAction();
            startTimer();
        } else {
            stopTimer();
            unregisterTimezoneAction();
        }
    }

    private void startTimer() {
        Log.e(TAG, "startTimer 开启定时任务");
        timerHandler.removeMessages(MSG_INVALIDATE);
        timerHandler.sendEmptyMessage(MSG_INVALIDATE);
    }

    private void stopTimer() {
        Log.e(TAG, "stopTimer 停止定时任务");
        timerHandler.removeMessages(MSG_INVALIDATE);
    }

    private void registerTimezoneAction() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(timerBroadcast, filter);
    }

    private void unregisterTimezoneAction() {
        getContext().unregisterReceiver(timerBroadcast);
    }

    private final BroadcastReceiver timerBroadcast =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(action!=null){
                switch (action){
                    //监听时区的变化
                    case Intent.ACTION_TIME_CHANGED: {
                        time = new Time(TimeZone.getTimeZone(intent.getStringExtra("time-zone")).getID());
                        break;
                    }

                }
            }
        }
    };


    private static final class TimerHandler extends android.os.Handler {

        private WeakReference<ClockView> clockViewWeakReference;

        private TimerHandler(ClockView clockView) {
            clockViewWeakReference = new WeakReference<>(clockView);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVALIDATE: {
                    ClockView view = clockViewWeakReference.get();
                    if (view != null) {
                        view.onTimeChanged();//获取时间
                        view.invalidate();
                        sendEmptyMessageDelayed(MSG_INVALIDATE, 1000);
                    }
                    break;
                }
            }
        }
    }
}
