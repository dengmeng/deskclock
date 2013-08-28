package com.android.deskclock;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TimerTimeLinearLayout extends LinearLayout
{
  private boolean mRunning;
  private int[] mNumMediumImg = { 
		  R.drawable.time_medium_0, R.drawable.time_medium_1, R.drawable.time_medium_2, 
		  R.drawable.time_medium_3, R.drawable.time_medium_4, R.drawable.time_medium_5, 
		  R.drawable.time_medium_6, R.drawable.time_medium_7, R.drawable.time_medium_8, 
		  R.drawable.time_medium_9, R.drawable.time_medium_colon};
  private int[] mNumLargeImg = { 
  		  R.drawable.time_large_0, R.drawable.time_large_1, R.drawable.time_large_2, 
  		  R.drawable.time_large_3, R.drawable.time_large_4, R.drawable.time_large_5, 
  		  R.drawable.time_large_6, R.drawable.time_large_7, R.drawable.time_large_8, 
  		  R.drawable.time_large_9, R.drawable.time_large_colon};
  private ImageView[] mNumView;
  private TextView mMinuteText;
  private TextView mHourText;
  private TextView mStartHintText;
  private int mHour;
  private int mMinute;
  private int mSecond;

  
  public TimerTimeLinearLayout(Context context)
  {
    this(context, null);
  }

  public TimerTimeLinearLayout(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
  }

  public TimerTimeLinearLayout(Context context, AttributeSet attrs, int defStyle)
  {
    super(context, attrs, defStyle);
    OutputLog.timer("TimerTimeLinearLayout:TimerTimeLinearLayout");
    mStartHintText = new TextView(context);
    mStartHintText.setTextAppearance(context, R.style.TutorialTextStyle);
    addView(mStartHintText);//顺时针滑动提示
    mStartHintText.setText(R.string.timer_tutorial);
    mNumView = new ImageView[8];
    setOrientation(0);
    setGravity(80);
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -2);
    //"分钟"字串
    mMinuteText = new TextView(context);
    mMinuteText.setLayoutParams(localLayoutParams);
    mMinuteText.setText(R.string.timer_minute);
    mMinuteText.setTextAppearance(context, R.style.NumberViewTextStyle);
    //"小时"字串
    mHourText = new TextView(context);
    mHourText.setLayoutParams(localLayoutParams);
    mHourText.setText(R.string.timer_hour);
    mHourText.setTextAppearance(context, R.style.NumberViewTextStyle);
    //添加数字及字串
    for (int i=0; i<8; i++) {
    	if (i == 2) {
    		addView(mHourText);
    	}
    	if (i == 5) {
    		addView(mMinuteText);
    	}
    	mNumView[i] = new ImageView(context);
    	mNumView[i].setLayoutParams(localLayoutParams);
    	addView(mNumView[i]);
    }
    mNumView[3].setVisibility(View.VISIBLE);
    mNumView[4].setVisibility(View.VISIBLE);
  }

  private void updateView() {
	  OutputLog.timer("TimerTimeLinearLayout:updateView:mRunning="+mRunning);
	  if (mRunning) {
		  mStartHintText.setVisibility(View.GONE);
		  mHourText.setVisibility(View.GONE);
		  mMinuteText.setVisibility(View.GONE);
		  if (mHour > 0) {
			  
			  mNumView[0].setVisibility(View.VISIBLE);
			  mNumView[1].setVisibility(View.VISIBLE);
			  mNumView[2].setVisibility(View.VISIBLE);
			  mNumView[3].setVisibility(View.VISIBLE);
			  mNumView[4].setVisibility(View.VISIBLE);
			  mNumView[5].setVisibility(View.GONE);
			  mNumView[6].setVisibility(View.GONE);
			  mNumView[7].setVisibility(View.GONE);
		  } else {
			  mNumView[0].setVisibility(View.GONE);
			  mNumView[1].setVisibility(View.GONE);
			  mNumView[2].setVisibility(View.GONE);
			  mNumView[3].setVisibility(View.VISIBLE);
			  mNumView[4].setVisibility(View.VISIBLE);
			  mNumView[5].setVisibility(View.VISIBLE);
			  mNumView[6].setVisibility(View.VISIBLE);
			  mNumView[7].setVisibility(View.VISIBLE);
		  }
	  } else {
		  if ((mHour > 0) || (mMinute > 0)) {
			  mStartHintText.setVisibility(View.GONE);
			  
			  if (mHour > 0) {
				  mNumView[0].setVisibility(View.VISIBLE);
				  mNumView[1].setVisibility(View.VISIBLE);
				  mHourText.setVisibility(View.VISIBLE);
			  } else {
				  mNumView[0].setVisibility(View.GONE);
				  mNumView[1].setVisibility(View.GONE);
				  mHourText.setVisibility(View.GONE);
			  }
			  mNumView[3].setVisibility(View.VISIBLE);
			  mNumView[4].setVisibility(View.VISIBLE);
			  mMinuteText.setVisibility(View.VISIBLE);
			  
			  mNumView[2].setVisibility(View.GONE);
			  mNumView[5].setVisibility(View.GONE);
			  mNumView[6].setVisibility(View.GONE);
			  mNumView[7].setVisibility(View.GONE);
		  } else {
			  mStartHintText.setVisibility(View.VISIBLE);
			  mMinuteText.setVisibility(View.GONE);
			  mHourText.setVisibility(View.GONE);
			  for (int i=0; i < 8; i++) {
				  mNumView[i].setVisibility(View.GONE);
			  }
		  }
	  }
  }

  public void updateViewRes()
  {
	  int[] arrayOfImg;
	  if (mHour > 0) {
		  arrayOfImg = mNumMediumImg;
	  } else {
		  arrayOfImg = mNumLargeImg;
	  }
	  
      mNumView[2].setImageResource(arrayOfImg[10]);
      mNumView[5].setImageResource(arrayOfImg[10]);
      mNumView[0].setImageResource(arrayOfImg[(mHour / 10)]);
      mNumView[1].setImageResource(arrayOfImg[(mHour % 10)]);
      mNumView[3].setImageResource(arrayOfImg[(mMinute / 10)]);
      mNumView[4].setImageResource(arrayOfImg[(mMinute % 10)]);
      mNumView[6].setImageResource(arrayOfImg[(mSecond / 10)]);
      mNumView[7].setImageResource(arrayOfImg[(mSecond % 10)]);
  }

  public int getVolidWidth()
  {
    if ((mRunning) || (mHour > 0) || ((mHour <= 0) && (mMinute <= 0)))
    	return getMeasuredWidth();
    else
    	return getMeasuredWidth() - mMinuteText.getMeasuredWidth();
  }

  public int getVolidHeight()
  {
    return getMeasuredHeight();
  }

  public void setStart(boolean enable)
  {
    mRunning = enable;
    updateView();
  }

  public void setTime(int hour, int minute, int second)
  {
    mHour = hour;
    if (mHour > 99) {
      mHour %= 100;
    }
    mMinute = minute;
    mSecond = second;
  }
}
