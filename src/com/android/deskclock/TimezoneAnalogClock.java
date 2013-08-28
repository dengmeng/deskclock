package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class TimezoneAnalogClock extends View {
	private Time mTime;//获取当前的精确时间
	private Drawable mClockHandHour;//时针
	private Drawable mClockHandMin;//分针
	private Drawable mClockHandSec;//秒针
	private Drawable mClockDial;//表盘
	private int mClockDialWidth;//表盘宽度
	private int mClockDialHeight;//表盘高度

	private float mMinute;//当前时间分针值
	private float mHour;//当前时间时针值
	private float mSecond;//当前时间钞针值
	private boolean mChanged;//状态是否改变
	private Runnable mRunnable;//用于实时刷新时间显示
	private boolean mStop;//是否处于激活状态
	private Handler mHandler;//用于实时刷新时间显示
	Context mContext;
	
	static public boolean getRunningState(TimezoneAnalogClock mAnalogClock) {
		return !mAnalogClock.mStop;
	}
	
	static public Runnable getRunnable(TimezoneAnalogClock mAnalogClock) {
		return mAnalogClock.mRunnable;
	}
	
	static public Handler getHandler(TimezoneAnalogClock mAnalogClock) {
		return mAnalogClock.mHandler;
	}
	
	static public void onTimeChanged(TimezoneAnalogClock mAnalogClock) {
		mAnalogClock.onTimeChanged();
	}

	public TimezoneAnalogClock(Context context) {
		this(context, null);
		mContext = context;
	}

	public TimezoneAnalogClock(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
		mContext = context;
	}

	public TimezoneAnalogClock(Context context, AttributeSet attributeSet, int paramInt) {
		super(context, attributeSet, paramInt);
		mContext = context;
		//获取图片资源
		Resources resources = context.getResources();
	    mClockDial = resources.getDrawable(R.drawable.clock_dial);
	    mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour);
	    mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute);
	    mClockHandSec = resources.getDrawable(R.drawable.clock_hand_second);
	    mTime = new Time();
	    //获取表盘大小信息
	    mClockDialWidth = mClockDial.getIntrinsicWidth();
	    mClockDialHeight = mClockDial.getIntrinsicHeight();
	}

	//更新时钟UI风格
	private void updateDial(Time time) {
		setContentDescription(DateUtils.formatDateTime(mContext, time.toMillis(false), 129));
		Resources resources = mContext.getResources();
    
		//昼夜显示不同的风格
		if ((mHour < 6.0F) || (mHour >= 18.0F)) {
			mClockDial = resources.getDrawable(R.drawable.clock_dial_black);
			mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour_black);
			mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute_black);
		} else {
			mClockDial = resources.getDrawable(R.drawable.clock_dial);
			mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour);
			mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute);
		}
		mClockHandSec = resources.getDrawable(R.drawable.clock_hand_second);
	}

  
  	private void onTimeChanged() {
  		//更新为当前时间
  		mTime.setToNow();
  		int hour = mTime.hour;
  		int minute = mTime.minute;
  		int second = mTime.second;
  		mSecond = second;
  		mMinute = (minute + second / 60.0F);
  		mHour = (hour + mMinute / 60.0F);
  		mChanged = true;
  		updateDial(mTime);
  	}

  	//根据时区获取时间
  	public void setTimezone(String timezone) {
  		if (TextUtils.isEmpty(timezone))
  			return;
  		mTime = new Time(timezone);
  		onTimeChanged();
  		invalidate();
  	}

  
  	@Override
  	protected void onAttachedToWindow() {
  		super.onAttachedToWindow();
  		mStop = false;
  		mHandler = new Handler();
  		mRunnable = new TimezoneAnalogClockRunnable(this);
  		mRunnable.run();
  		onTimeChanged();
  	}

  	
  	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();

	    mStop = true;
  	}

  	
  @Override
	protected void onDraw(Canvas canvas) {
	    int scaled = 0;
	    super.onDraw(canvas);
	    boolean flag = mChanged;
	    if (flag)
	      mChanged = false;
	    int width = this.mRight - this.mLeft;//画布宽度
	    int height = this.mBottom - this.mTop;//画布高度
	    int CenterX = width / 2;//画布中心点Ｘ坐标
	    int CenterＹ = height / 2;//画布中心点Ｙ坐标
	    Drawable clockdial = mClockDial;
	    int dialWidth = clockdial.getIntrinsicWidth();//表盘图片宽度
	    int dialHeight = clockdial.getIntrinsicHeight();//表盘图片高度
	    if ((width < dialWidth) || (height < dialHeight)) {
	    	scaled = 1;
	    	//整体宽、高同比例缩小，取二者小值
	    	float dialScale = Math.min(width / dialWidth, height / dialHeight);
	    	canvas.save();
	    	canvas.scale(dialScale, dialScale, CenterX, CenterＹ);
	    }
	    
	    //画表盘
	    if (flag) {
	    	//根据中心点确定显示位置
	    	clockdial.setBounds(CenterX - dialWidth / 2, CenterＹ - dialWidth / 2, CenterX + dialWidth / 2, CenterＹ + dialHeight / 2);
	    }
	    clockdial.draw(canvas);
	    
	    //画时针
	    canvas.save();
	    canvas.rotate(360.0F * (mHour / 12.0F), CenterX, CenterＹ);//根据时间旋转
	    Drawable clockHour = mClockHandHour;
	    if (flag) {
	    	int hourWidth = clockHour.getIntrinsicWidth();
	    	int hourHeight = clockHour.getIntrinsicHeight();
	    	//根据中心点确定画布上的位置
	    	clockHour.setBounds(CenterX - hourWidth / 2, CenterＹ - hourHeight / 2, CenterX + hourWidth / 2, CenterＹ + hourHeight / 2);
	    }
	    clockHour.draw(canvas);
	    canvas.restore();
	    
	    //画分针
	    canvas.save();
	    canvas.rotate(360.0F * (mMinute / 60.0F), CenterX, CenterＹ);//根据时间旋转
	    Drawable clockMinute = mClockHandMin;
	    if (flag) {
	    	int minuteWidth = clockMinute.getIntrinsicWidth();
	    	int minuteHeight = clockMinute.getIntrinsicHeight();
	    	//根据中心点确定画布上的位置
	    	clockMinute.setBounds(CenterX - minuteWidth / 2, CenterＹ - minuteHeight / 2, CenterX + minuteWidth / 2, CenterＹ + minuteHeight / 2);
	    }
	    clockMinute.draw(canvas);
	    canvas.restore();
	    
	    //画秒针
	    canvas.save();
	    canvas.rotate(360.0F * (mSecond / 60.0F), CenterX, CenterＹ);//根据时间旋转
	    Drawable clockSec = mClockHandSec;
	    if (flag) {
	    	int secWidth = clockSec.getIntrinsicWidth();
	    	int secHeight = clockSec.getIntrinsicHeight();
	    	//根据中心点确定画布上的位置
	    	clockSec.setBounds(CenterX - secWidth / 2, CenterＹ - secHeight / 2, CenterX + secWidth / 2, CenterＹ + secHeight / 2);
	    }
	    clockSec.draw(canvas);
	    canvas.restore();
	    
	    if (scaled != 0) 
	    	canvas.restore();
  	}

  	//根据实际情况摆放控件元素
  	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    float scaleH = 1.0F;
	    float scaleW = 1.0F;
	    int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

	    //参考配置参数计算宽度的缩小比例
	    if ((widthMode != MeasureSpec.UNSPECIFIED) && (widthSize < mClockDialWidth)) {
	    	scaleW = widthSize / mClockDialWidth;
	    }	
	    //参考配置参数计算高度的缩小比例
	    if ((heightMode != MeasureSpec.UNSPECIFIED) && (heightSize < mClockDialHeight)) {
	    	scaleH = heightSize / mClockDialHeight;
	    }
	    //确保宽、高同比例缩小，取二者小值
	    float scale = Math.min(scaleW, scaleH);
	    //比较视图的期望大小返回一个合适的值，设置最终宽、高
	    setMeasuredDimension(resolveSizeAndState((int)(scale * mClockDialWidth), widthMeasureSpec, 0), resolveSizeAndState((int)(scale * mClockDialHeight), heightMeasureSpec, 0));
  	}

  	//获取变化状态
  	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
  		super.onSizeChanged(w, h, oldw, oldh);
  		mChanged = true;
  	}
}