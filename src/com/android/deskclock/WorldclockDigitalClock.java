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
	private ContentObserver mContentObserver;//���ڼ��������Ƿ����仯������ʱ���ʽ����
	private boolean mRunning = true;
	private TextView mTimeDisplay;//ʱ���ִ�
	private DigitalClockAmPm mAmPm;//am\pm�ִ�
  	private Calendar mCalendar;//��ȡ��ǰʱ��
  	private String mFormat;//ʱ����ʾ��ʽ
  	private final Handler mHandler = new Handler();
  	private final BroadcastReceiver mIntentReceiver = new DigitalClockReceiver(this);//����ϵͳʱ����صĹ㲥
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

  	//����ʱ����ʾ��ʽ
  	private void setTimeFormat() {
  		if (WorldclockDigitalClock.is24HourFormat(mContext)) {
  			mFormat = "kk:mm";
  			mAmPm.setShowAmPm(false);
  		} else {
  			mFormat = "hh:mm";
  			mAmPm.setShowAmPm(true);
  		}
 	}

  	//ˢ��ʱ����ʾ
  	private void updateTime() {
  		if (mRunning){
  			mCalendar.setTimeInMillis(java.lang.System.currentTimeMillis());
  		}
  		//��ʽ����ʾʱ���ִ�
  		CharSequence charSequence = DateFormat.format(mFormat, mCalendar);
  		mTimeDisplay.setText(charSequence);
  		//��ʾam/pm�ִ�
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
  				//ע�����ϵͳʱ����صĹ㲥������ʱ��仯��ÿ�룩�������µ�ʱ�䡢ʱ���仯
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

  	//���ݲ�ͬʱ������ʱ��
  	void updateTime(Calendar calendar) {
	    mCalendar = calendar;
	    updateTime();
  	}
}