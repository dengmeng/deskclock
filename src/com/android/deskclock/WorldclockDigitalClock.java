package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.System;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;

public class WorldclockDigitalClock extends LinearLayout {
	private boolean mOnAttached;
	private ContentObserver mContentObserver;//用于监听设置是否发生变化，例如时间格式设置
	private boolean mRunning = true;
	private TextView mTimeDisplay;//时间字串
	private DigitalClockAmPm mAmPm;//am\pm字串
  	private Calendar mCalendar;//获取当前时间
  	private String mFormat;//时间显示格式
  	private final Handler mHandler = new Handler();
  	private final BroadcastReceiver mIntentReceiver = new DigitalClockReceiver(this);//接收系统时间相关的广播
  	private Context mContext;
  	
  	static public boolean getState(WorldclockDigitalClock digitalClock) {
  		return digitalClock.mRunning;
  	}
  	
  	static public Handler getHandler(WorldclockDigitalClock digitalClock) {
  		return digitalClock.mHandler;
  	}
  	
  	static public void updateTime(WorldclockDigitalClock digitalClock) {
  		digitalClock.updateTime();
  	}
  	
  	static public void setTimeFormat(WorldclockDigitalClock digitalClock) {
  		digitalClock.setTimeFormat();
  	}
  	
  	static public void updateTime(WorldclockDigitalClock digitalClock, Calendar calendar) {
  		digitalClock.updateTime(calendar);
  	}

  	public WorldclockDigitalClock(Context context) {
  		this(context, null);
  	}

  	public WorldclockDigitalClock(Context context, AttributeSet attributeSet) {
  		super(context, attributeSet);
  		mContext = context;
  	}
  	
  	static boolean is24HourFormat(Context context) {
  		return DateFormat.is24HourFormat(context);
    }

  	//设置时间显示格式
  	private void setTimeFormat() {
  		if (WorldclockDigitalClock.is24HourFormat(mContext)) {
  			mFormat = "kk:mm";
  			mAmPm.setShowAmPm(false);
  		} else {
  			mFormat = "hh:mm";
  			mAmPm.setShowAmPm(true);
  		}
 	}

  	//刷新时间显示
  	private void updateTime() {
  		if (mRunning){
  			mCalendar.setTimeInMillis(java.lang.System.currentTimeMillis());
  		}
  		//格式化显示时间字串
  		CharSequence charSequence = DateFormat.format(mFormat, mCalendar);
  		mTimeDisplay.setText(charSequence);
  		//显示am/pm字串
	    if (mCalendar.get(Calendar.AM_PM) == 0) {
	    	mAmPm.setIsMorning(true);
	    } else {
	    	mAmPm.setIsMorning(false);
	    }
  	}

  	void setState(boolean enable){
  		mRunning = enable;
  	}

  
  	@Override
  	protected void onAttachedToWindow() {
  		super.onAttachedToWindow();
  		if (!mOnAttached) {
  			mOnAttached = true;
  			if (mRunning) {
  				//注册接收系统时间相关的广播：正常时间变化（每秒）、设置新的时间、时区变化
  				IntentFilter intentFilter = new IntentFilter();
  				intentFilter.addAction("android.intent.action.TIME_TICK");
  				intentFilter.addAction("android.intent.action.TIME_SET");
  				intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
  				getContext().registerReceiver(this.mIntentReceiver, intentFilter);
  	      }
  	      mContentObserver = new DigitalClockContentObserver(this);
  	      getContext().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, mContentObserver);
  	      updateTime();
  		}
  }

  	
  @Override
	protected void onDetachedFromWindow() {
	  super.onDetachedFromWindow();
	  if (mOnAttached) {
		  mOnAttached = false;
	      if (mRunning) {
	    	  getContext().unregisterReceiver(this.mIntentReceiver);
	      }
	      getContext().getContentResolver().unregisterContentObserver(mContentObserver);
	  }
  }

  
  	@Override
  	protected void onFinishInflate() {
	    super.onFinishInflate();
	    mTimeDisplay = ((TextView)findViewById(R.id.timeDisplay));
	    mAmPm = new DigitalClockAmPm(this);
	    mCalendar = Calendar.getInstance();
	    setTimeFormat();
  	}

  	//根据不同时区更新时间
  	void updateTime(Calendar calendar) {
	    mCalendar = calendar;
	    updateTime();
  	}
}