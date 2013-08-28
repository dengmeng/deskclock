package com.android.deskclock;

import android.app.Activity;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class StopwatchFragment extends ListFragment
  implements View.OnClickListener, View.OnTouchListener, DeskClockInterface
{
  private View mStartBtn;
  private View mPauseBtn;
  private View mResetBtn;
  private View mLapBtn;
  private boolean mRunning;
  private long mTotalElapsed;
  private long mLastTotalElapsed;
  private Activity mActivity;
  private View mRootView;
  private StopwatchChronometer mElapsedTime;
  private ListView mLapList;
  

  private static String longToString(long longtime) {
	String strTime;
	//OutputLog.stopwatch("StopwatchFragment:longToString:longtime="+longtime);
	if (longtime < 10L) {
		strTime = "0" + longtime;
	} else {
		strTime = new Long(longtime).toString();
	}
	//OutputLog.stopwatch("StopwatchFragment:longToString:"+strTime);
	return strTime;
  }

  private void changeBtnState() {
	  OutputLog.stopwatch("StopwatchFragment:changeBtnState:mRunning="+mRunning);
	  if (mRunning) {
	      mStartBtn.setVisibility(View.GONE);
	      mPauseBtn.setVisibility(View.VISIBLE);
	      mResetBtn.setVisibility(View.GONE);
	      mLapBtn.setVisibility(View.VISIBLE);
	  } else {
	      mStartBtn.setVisibility(View.VISIBLE);
	      mPauseBtn.setVisibility(View.GONE);
	      mResetBtn.setVisibility(View.VISIBLE);
	      mLapBtn.setVisibility(View.GONE);
	  }
  }

  public static String formatElapsedTime(long longtime) {
	  //OutputLog.stopwatch("StopwatchFragment:formatElapsedTime:longtime="+longtime);
	  long tempMinute = longtime / 60000L;
	  long tempSecond = longtime % 60000L / 1000L;
	  long tempMillisecond = longtime % 1000L / 100L;
	  long resultSecond = tempSecond;
	  long resultMinute = tempMinute;
	  long resultMillisecond = tempMillisecond;
	  if (longtime % 100L >= 50L) {
		  resultMillisecond += 1L;
		  if (resultMillisecond >= 10L) {
			  resultSecond += 1L;
			  resultMillisecond = 0L;
			  if (resultSecond >= 60L) {
				  resultMinute += 1L;
				  resultSecond = 0L;
			  }
		  }
	  }

	  //OutputLog.stopwatch("StopwatchFragment:formatElapsedTime:resultMinute="+resultMinute);
	  //OutputLog.stopwatch("StopwatchFragment:formatElapsedTime:resultSecond="+resultSecond);
	  //OutputLog.stopwatch("StopwatchFragment:formatElapsedTime:resultMillisecond="+resultMillisecond);
	  StringBuilder localStringBuilder = new StringBuilder();
	  localStringBuilder.append(longToString(resultMinute));
	  localStringBuilder.append(":");
	  localStringBuilder.append(longToString(resultSecond));
	  localStringBuilder.append(".");
	  localStringBuilder.append(String.valueOf(resultMillisecond));
	  
	  OutputLog.stopwatch("StopwatchFragment:formatElapsedTime:"+localStringBuilder.toString());
	  return localStringBuilder.toString();
  }

  public void handler() {
  }

  
  @Override
  public void onClick(View v) {
	  OutputLog.stopwatch("StopwatchFragment:onClick");
	  SharedPreferences.Editor editor;
	  editor = PreferenceManager.getDefaultSharedPreferences(mActivity).edit();
	  
	  switch (v.getId()) {
	  default:
		  break;
	  case R.id.start_btn:
		  OutputLog.stopwatch("StopwatchFragment:onClick:start_btn");
	      mRunning = true;
	      mElapsedTime.setBase(System.currentTimeMillis() - mTotalElapsed);
	      mElapsedTime.start();
		  break;
	  case R.id.pause_btn:
		  OutputLog.stopwatch("StopwatchFragment:onClick:pause_btn");
    	  mRunning = false;
    	  mTotalElapsed = (System.currentTimeMillis() - mElapsedTime.getBase());
    	  mElapsedTime.stop();
		  break;
	  case R.id.reset_btn:
		  OutputLog.stopwatch("StopwatchFragment:onClick:reset_btn");
    	  mRunning = false;
	      mElapsedTime.stop();
	      mElapsedTime.setBase(System.currentTimeMillis());
	      mTotalElapsed = 0L;
	      mLastTotalElapsed = 0L;
	      mActivity.getContentResolver().delete(StopwatchUri.CONTENT_URI, null, null);
		  break;
	  case R.id.lap_btn:
		  OutputLog.stopwatch("StopwatchFragment:onClick:lap_btn");
		  mTotalElapsed = (System.currentTimeMillis() - mElapsedTime.getBase());
		  OutputLog.stopwatch("StopwatchFragment:onClick:lap_btn:mTotalElapsed="+mTotalElapsed);
	      ContentValues localContentValues = new ContentValues();
	      localContentValues.put("total_elapsed", Long.valueOf(mTotalElapsed));
	      localContentValues.put("lap_elapsed", Long.valueOf(mTotalElapsed - mLastTotalElapsed));
	      mActivity.getContentResolver().insert(StopwatchUri.CONTENT_URI, localContentValues);
	      mLastTotalElapsed = mTotalElapsed;
		  break;
	  }
	  editor.putLong("stopwatch_base_time_pref", mElapsedTime.getBase());
	  editor.putLong("stopwatch_lap_elapsed_time_pref", mLastTotalElapsed);
	  editor.putLong("stopwatch_elapsed_time_pref", mTotalElapsed);
	  
	  editor.putBoolean("stopwatch_state_running_pref", mRunning);
	  editor.commit();
	  changeBtnState();
  }

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	  OutputLog.stopwatch("StopwatchFragment:onCreateView");
	  mRootView = inflater.inflate(R.layout.stopwatch, container, false);
	  mActivity = getActivity();
	  mLapList = (ListView)mRootView.findViewById(android.R.id.list);
	  mLapList.setAdapter(new StopwatchResCursorAdapter(this, mActivity, mActivity.getContentResolver().query(StopwatchUri.CONTENT_URI, StopwatchUri.PROJECTION, null, null, "_id DESC")));
	  mElapsedTime = ((StopwatchChronometer)mRootView.findViewById(R.id.elapsed_time));
	  mStartBtn = mRootView.findViewById(R.id.start_btn);
	  mPauseBtn = mRootView.findViewById(R.id.pause_btn);
	  mResetBtn = mRootView.findViewById(R.id.reset_btn);
	  mLapBtn = mRootView.findViewById(R.id.lap_btn);
	  mStartBtn.setOnTouchListener(this);
	  mPauseBtn.setOnTouchListener(this);
	  mResetBtn.setOnTouchListener(this);
	  mLapBtn.setOnTouchListener(this);
	  mStartBtn.setOnClickListener(this);
	  mPauseBtn.setOnClickListener(this);
	  mResetBtn.setOnClickListener(this);
	  mLapBtn.setOnClickListener(this);
	  return mRootView;
  }

  
  @Override
  public void onPause() {
	  super.onPause();
	  OutputLog.stopwatch("StopwatchFragment:onPause");
	  if (mRunning) {
		  mElapsedTime.stop();
	  }
  }

  @Override
  public void onResume() {
	  OutputLog.stopwatch("StopwatchFragment:onResume");
	  super.onResume();
	  SharedPreferences editor = PreferenceManager.getDefaultSharedPreferences(mActivity);
	  mRunning = editor.getBoolean("stopwatch_state_running_pref", false);
	  if (mRunning) {
		  long baseTime = editor.getLong("stopwatch_base_time_pref", System.currentTimeMillis());
		  mElapsedTime.setBase(baseTime);
		  mTotalElapsed = (System.currentTimeMillis() - baseTime);
		  mElapsedTime.start();
	  } else {
		  mTotalElapsed = editor.getLong("stopwatch_elapsed_time_pref", 0L);
		  mElapsedTime.setBase(System.currentTimeMillis() - mTotalElapsed);
	  }
    
	  mLastTotalElapsed = editor.getLong("stopwatch_lap_elapsed_time_pref", mTotalElapsed);
	  changeBtnState();
  }

  
  @Override
  public boolean onTouch(View v, MotionEvent event) {
	  int btnindex = 0;
	  switch (v.getId()){
	  	default:
	  		OutputLog.stopwatch("StopwatchFragment:onTouch:default");
	  		return false;
	  	case R.id.start_btn:
	  		OutputLog.stopwatch("StopwatchFragment:onTouch:start_btn");
		  	btnindex = 0;
			break;
	  	case R.id.pause_btn:
	  		OutputLog.stopwatch("StopwatchFragment:onTouch:pause_btn");
		  	btnindex = btnindex + 2;
			break;
	  	case R.id.reset_btn:
	  		OutputLog.stopwatch("StopwatchFragment:onTouch:reset_btn");
		  	btnindex = btnindex + 4;
			break;
	  	case R.id.lap_btn:
	  		OutputLog.stopwatch("StopwatchFragment:onTouch:lap_btn");
		  	btnindex = btnindex + 6;
			break;
	  }
	  
	  if (event.getAction() == MotionEvent.ACTION_DOWN) {
		  BtnVibrator.getBtnVibrator(mActivity).vibratorConfig(btnindex);
	  } else {
		  BtnVibrator.getBtnVibrator(mActivity).vibratorConfig(btnindex + 1);
	  }
	  return false;
    }
}
