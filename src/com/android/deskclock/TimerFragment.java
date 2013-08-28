package com.android.deskclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class TimerFragment extends Fragment
  implements View.OnTouchListener, TimerInterface, DeskClockInterface
{
  private final int cl = 1;
  private View mStartBtn;
  private View mCancelBtn;
  private View mPauseBtn;
  private View mKeepScreenBtn;
  private View mContinueBtn;
  private CheckBox mCheckBox;
  private WatchDial mWatchDial;
  private View mRingtoneBtn;
  private TextView mRingtone;
  private Ringtone ringtone;
  private AlertDialog mRingToneAlertDialog;
  private SharedPreferences mPref;
  private int mTimerState;
  private long mTimerRemained;
  private TimerThread mThread;
  private boolean mSelected;
  private Activity mActivity;
  private long mDuration;
  private long mEndTime;
  private Handler mHandler = new TimerHandler(this);
  private View mRootView;
  private PowerManager.WakeLock mWakeLock;
  
  static public final int STATE_IDLE = 0;
  static public final int STATE_RUNNING = 1;
  static public final int STATE_PAUSED = 2;
  static public final String ACTION_TIMER_TIMEOUT = "action.timer_off";
  static public final String ACTION_START_TIMER = "action.timer.start";
  static public final String ACTION_STOP_TIMER = "action.timer.stop";
  

  static public WatchDial getWatchDial(TimerFragment timer) {
	  return timer.mWatchDial;
  }
  
  /*
  static public WatchDial getWatchDial(TimerFragment timer, long time) {
	  timer.mWatchDial.setTimeValue(time);
	  return timer.mWatchDial;
  }*/
  
  static public long getEndTime(TimerFragment timer) {
	  return timer.mEndTime;
  }
  
  static public long getRemainedTime(TimerFragment timer) {
	  return timer.mTimerRemained;
  }
  
  static public void setRemainedTime(TimerFragment timer, long time) {
	  OutputLog.timer("TimerFragment:setRemainedTime:time="+time);
	  timer.mTimerRemained = time;
  }
  
  static public void setTimerDuration(TimerFragment timer, long time) {
	  timer.mDuration = time;
  }
  
  static public int getTimerState(TimerFragment timer) {
	  return timer.mTimerState;
  }
  
  static public CheckBox getCheckBox(TimerFragment timer) {
	  return timer.mCheckBox;
  }
  
  static public SharedPreferences getPref(TimerFragment timer) {
	  return timer.mPref;
  }
  
  static public void startTimer(TimerFragment timer, long time) {
	  OutputLog.timer("TimerFragment:startTimer:time="+time);
	  timer.startTimer(time);
  }
  
  static public void onClickPause(TimerFragment timer) {
	  OutputLog.timer("TimerFragment:onClickPause:mTimerState="+timer.mTimerState);
	  timer.pauseOrContinueTimer();
  }
  
  static public void onClickContinue(TimerFragment timer) {
	  OutputLog.timer("TimerFragment:onClickContinue:mTimerState="+timer.mTimerState);
	  timer.pauseOrContinueTimer();
  }
  
  static public void onClickCancel(TimerFragment timer) {
	  OutputLog.timer("TimerFragment:onClickCancel:mTimerState="+timer.mTimerState);
	  if (timer.mTimerState != TimerFragment.STATE_IDLE) {
		  timer.cancelTimer();
	  }
  }
  
  static public Handler getHandler(TimerFragment timer) {
	  return timer.mHandler;
  }
  
  //倒计时结束，铃声提醒
  static public void expireTimer(TimerFragment timer) {
	  OutputLog.alarm("TimerFragment:expireTimer:sendBroadcast");
	  //Intent intent = new Intent(timer.mActivity, AlarmReceiver.class);
	  //intent.setAction(ACTION_TIMER_TIMEOUT);
	  //timer.mActivity.sendBroadcast(intent);
	  timer.stopTimer();
  }
  
  static public void ebableWakeLock(TimerFragment timer, boolean enable) {
	  OutputLog.alarm("TimerFragment:ebableWakeLock");
	  timer.ebableWakeLock(enable);
  }
  
  private void pauseOrContinueTimer() {
	  OutputLog.timer("TimerFragment:pauseOrContinueTimer");
	  if (mTimerState == TimerFragment.STATE_RUNNING) {
		  mTimerRemained = (mEndTime - java.lang.System.currentTimeMillis());
		  updateState(TimerFragment.STATE_PAUSED);
		  if (mThread != null)
			  mThread.joinThread();
		  Intent intent = new Intent(mActivity, AlarmReceiver.class);
		  intent.setAction(ACTION_STOP_TIMER);
		  mActivity.sendBroadcast(intent);
	  } else if (mTimerState == TimerFragment.STATE_PAUSED) {
		  startTimer(mTimerRemained);
	  }
  }

  private void stopTimer() {
	  OutputLog.timer("TimerFragment:stopTimer");
	  if (mThread != null)
		  mThread.joinThread();
	  mWatchDial.reset();
	  mWatchDial.refresh();
	  updateState(TimerFragment.STATE_IDLE);
  }

  private void setRingTone(Uri uri) {
	  String str;
	  int i;
	  OutputLog.timer("TimerFragment:setRingTone");
	  if (uri == null) {
		  //uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
		  uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	  }
	  
	  ringtone = RingtoneManager.getRingtone(mActivity, uri);
	  if (ringtone == null) {
		  //uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
		  uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		  ringtone = RingtoneManager.getRingtone(mActivity, uri);
	  }
	  
	  if (ringtone != null) {
		  str = ringtone.getTitle(mActivity);
		  OutputLog.timer("TimerFragment:setRingTone:str="+str);
		  mRingtone.setText(str);
	  } else {
		  mRingtone.setText(R.raw.fallbackring);
	  }
	  /*
	  if (uri != null) {
		  ringtone = RingtoneManager.getRingtone(mActivity, uri);
		  if (ringtone != null) {
			  str = ringtone.getTitle(mActivity);
			  OutputLog.timer("TimerFragment:setRingTone:str="+str);
			  //i = str.lastIndexOf(".");
			  //OutputLog.timer("TimerFragment:setRingTone:i="+i);
			  //if (i != -1) {
				  //str = str.substring(0, i);
				  mRingtone.setText(str);
			  //} else {
				  //mRingtone.setText(R.raw.fallbackring);
			  //}

		  } else {
    		mRingtone.setText(R.raw.fallbackring);
		  }
	  } else {
		  
		  mRingtone.setText(R.raw.fallbackring);
	  }*/
  }

  private void updateState(int state) {
	  OutputLog.timer("TimerFragment:updateState:state="+state);
	  switch (state) {
	  	default:
	  		OutputLog.timer("TimerFragment:updateState----ERROR-STATE="+state);
	  		break;
	  	case TimerFragment.STATE_IDLE:
	        mCancelBtn.setVisibility(View.GONE);
	        mPauseBtn.setVisibility(View.GONE);
	        mContinueBtn.setVisibility(View.GONE);
	        mStartBtn.setVisibility(View.VISIBLE);
	        mWatchDial.updateView(WatchDial.STATE_IDLE);
	        break;
	  	case TimerFragment.STATE_RUNNING:
	        mContinueBtn.setVisibility(View.GONE);
	        mPauseBtn.setVisibility(View.VISIBLE);
	        mCancelBtn.setVisibility(View.VISIBLE);
	        mStartBtn.setVisibility(View.GONE);
	        mWatchDial.updateView(WatchDial.STATE_RUNNING);
	        mWatchDial.setTotalTime(mDuration);
	        mWatchDial.setTimeValue(mTimerRemained);
	        break;
	  	case TimerFragment.STATE_PAUSED:
	        mContinueBtn.setVisibility(View.VISIBLE);
	        mPauseBtn.setVisibility(View.GONE);
	        mCancelBtn.setVisibility(View.VISIBLE);
	        mStartBtn.setVisibility(View.GONE);
	        mWatchDial.updateView(WatchDial.STATE_RUNNING);
	        mWatchDial.setTotalTime(mDuration);
	        mWatchDial.setTimeValue(mTimerRemained);
	        break;
	  }

	  mTimerState = state;
	  SharedPreferences.Editor editor = mPref.edit();
	  editor.putInt("timestate", mTimerState);
	  editor.putLong("endtime", mEndTime);
	  editor.putLong("timerremained", mTimerRemained);
	  editor.putLong("duration", mDuration);
	  editor.commit();
  }

  private void startTimer(long time) {
	  OutputLog.timer("TimerFragment:startTimer:time="+time);
	  if (time == 0L)
		  return;
	  //((DeskClockTabActivity)this.mActivity).o().trackEvent("Timer");
	  mTimerRemained = time;
	  mWatchDial.setTotalTime(mDuration);
	  mWatchDial.updateView(WatchDial.STATE_RUNNING);
	  mWatchDial.setTimeValue(mTimerRemained);
	  mEndTime = (java.lang.System.currentTimeMillis() + mTimerRemained);
	  mThread = new TimerThread(this, true);
	  mThread.start();
	  updateState(TimerFragment.STATE_RUNNING);
	  ebableWakeLock(mCheckBox.isChecked());
	  Intent intent = new Intent(this.mActivity, AlarmReceiver.class);
	  intent.setAction(ACTION_START_TIMER);
	  intent.putExtra("extra_endtime", mEndTime);
	  mActivity.sendBroadcast(intent);
  }

  private void ebableWakeLock(boolean enable) {
	  OutputLog.timer("TimerFragment:ebableWakeLock="+enable);
	  if (enable) {
		  if (!mWakeLock.isHeld())
			  mWakeLock.acquire();
	  } else {
		  if (mWakeLock.isHeld())
			  mWakeLock.release();
	  }
  }

  private void enableStartBtn(boolean enable) {
	  OutputLog.timer("TimerFragment:enableStartBtn="+enable);
	  mStartBtn.setEnabled(enable);
  }

  private void cancelTimer() {
	  OutputLog.timer("TimerFragment:cancelTimer");
	  updateState(TimerFragment.STATE_IDLE);
	  ebableWakeLock(false);
	  if (mThread != null)
		  mThread.joinThread();
	  mWatchDial.reset();
	  mWatchDial.refresh();
	  Intent intent = new Intent(mActivity, AlarmReceiver.class);
	  intent.setAction(ACTION_STOP_TIMER);
	  mActivity.sendBroadcast(intent);
  }

  public void onSelected() {
	  OutputLog.timer("TimerFragment:onSelected");
	  mSelected = true;
	  if ((mWatchDial != null) && (mWatchDial.getHour() <= 0) && (mWatchDial.getMinute() <= 0))
		  mWatchDial.showTailRing();
	  if (mPref != null) {
		  boolean enableKeepScreen = mPref.getBoolean("KeepScreen", false);
		  if (mTimerState != TimerFragment.STATE_IDLE) {
			  ebableWakeLock(enableKeepScreen);
		  }
	  }
  }

  public void onUnSelected() {
	  OutputLog.timer("TimerFragment:onUnSelected");
	  mSelected = false;
	  if (mWakeLock == null)
		  return;
	  ebableWakeLock(false);
  }

  public void handler()
  {
  }

  public void onTimeChanged(int hour, int minute, int second) {
	  OutputLog.timer("TimerFragment:onTimeChanged");
	  if ((hour > 0) || (minute > 0)) {
		  enableStartBtn(true);
	  } else {
		  enableStartBtn(false);
	  }
  }

  public void playAlertSound(Context context) {
	  Uri uri;
	  String str = mPref.getString("Ringtone", null);
	  if (str != null) {
		  if (str.equals("")) {
			  uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
		  } else {
			  uri = Uri.parse(str);
		  }
	  } else {
		  uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
	  }
	  
      if (uri != null) {
    	  ringtone = RingtoneManager.getRingtone(context, uri);
          if (ringtone != null) {
        	  ringtone.setStreamType(AudioManager.STREAM_SYSTEM);
        	  ringtone.play();
          }
      }
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	  OutputLog.timer("TimerFragment:onActivityResult:requestCode="+requestCode+"resultCode="+resultCode);
	  
	  super.onActivityResult(requestCode, resultCode, data);
	  switch (requestCode) {
	  	default:
	  		break;
	  	case 1:
	  		if ((resultCode == -1) && (data != null)) {
	  			Uri uri = (Uri)data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	  			SharedPreferences.Editor editor = mPref.edit();
	  			if (uri != null) {
	  				String str = uri.toString();
	  				OutputLog.timer("TimerFragment:onActivityResult:str="+str);
	  				if (!str.equals("")) {
	  					editor.putString("Ringtone", str);
	  					editor.commit();
	  					setRingTone(uri);
	  				}
	  			}
	  		}
	  }
  }

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	  OutputLog.timer("TimerFragment:onCreateView");
	  mActivity = getActivity();
	  mPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
	  mTimerState = mPref.getInt("timestate", TimerFragment.STATE_IDLE);
	  mEndTime = mPref.getLong("endtime", 0L);
	  mDuration = mPref.getLong("duration", 0L);
	  mTimerRemained = mPref.getLong("timerremained", 0L);
	  if ((mTimerRemained <= 0L) || ((mTimerState == TimerFragment.STATE_RUNNING) && (java.lang.System.currentTimeMillis() >= mEndTime)))
		  mTimerState = TimerFragment.STATE_IDLE;
	  OutputLog.timer("TimerFragment:onCreateView:mTimerState="+mTimerState);
	  mRootView = inflater.inflate(R.layout.timer, container, false);
	  mWatchDial = ((WatchDial)mRootView.findViewById(R.id.watch_dial));
	  mWatchDial.setTimerInterface(this);
	  mRingtone = ((TextView)mRootView.findViewById(R.id.txt_ringtone));
	  String str = mPref.getString("Ringtone", null);
	  Uri localUri;
	  if (str != null) {
		  if (str.equals("")) {
			  localUri = null;
		  } else {
			  localUri = Uri.parse(str);
		  }
	  } else {
		  //localUri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
		  localUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	  }
	  setRingTone(localUri);

	  mStartBtn = mRootView.findViewById(R.id.start_timer);
	  mStartBtn.setOnClickListener(new TimerStartBtnClick(this));
	  enableStartBtn(false);
	  mCancelBtn = mRootView.findViewById(R.id.cancel_timer);
	  mCancelBtn.setOnClickListener(new TimerCancelBtnClick(this));
	  mPauseBtn = mRootView.findViewById(R.id.pause_timer);
	  mPauseBtn.setOnClickListener(new TimerPauseBtnClick(this));
	  mContinueBtn = mRootView.findViewById(R.id.continue_timer);
	  mContinueBtn.setOnClickListener(new TimerContinueBtnClick(this));
	  mStartBtn.setOnTouchListener(this);
	  mPauseBtn.setOnTouchListener(this);
	  mCancelBtn.setOnTouchListener(this);
	  mContinueBtn.setOnTouchListener(this);
	  mWakeLock = ((PowerManager)getActivity().getSystemService("power")).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TimerFragment.class.getName());
	  boolean enableKeepScreen = mPref.getBoolean("KeepScreen", false);
	  mCheckBox = ((CheckBox)mRootView.findViewById(R.id.check_box));
	  mCheckBox.setChecked(enableKeepScreen);
	  if (mTimerState == TimerFragment.STATE_IDLE) {
		  enableKeepScreen = false;
	  }
	  ebableWakeLock(enableKeepScreen);
	  mKeepScreenBtn = mRootView.findViewById(R.id.keep_screen);
	  mKeepScreenBtn.setOnClickListener(new TimerKeepScreenBtnClick(this));
	  mRingtoneBtn = mRootView.findViewById(R.id.ringtone);
	  mRingtoneBtn.setOnClickListener(new TimerRingtoneBtnClick(this));
	  return mRootView;
  }

  
  @Override
  public void onPause() {
	  OutputLog.timer("TimerFragment:onPause");
	  super.onPause();
	  if (mThread == null)
		  return;
	  mThread.joinThread();
  }

  
  @Override
  public void onResume() {
	  OutputLog.timer("TimerFragment:onResume:mTimerState="+mTimerState);
	  boolean enableKeepScreen = false;
	  super.onResume();
	  if (mTimerState == TimerFragment.STATE_RUNNING) {
		  mWatchDial.setTotalTime(mDuration);
		  mThread = new TimerThread(this, true);
		  mThread.start();
	  } else if (mTimerState == TimerFragment.STATE_PAUSED) {
		  mWatchDial.setTotalTime(mDuration);
		  mWatchDial.updateView(WatchDial.STATE_RUNNING);
		  mWatchDial.setTimeValue(mTimerRemained);
		  mWatchDial.refresh();
	  } else {
		  if ((mWatchDial.getHour() <= 0) && (mWatchDial.getMinute() <= 0)){
			  mWatchDial.showTailRing();
		  }
	  }
    
	  if (mSelected) {
		  if (mTimerState != TimerFragment.STATE_IDLE) {
			  enableKeepScreen = mPref.getBoolean("KeepScreen", false);
		  }
	  }
	  ebableWakeLock(enableKeepScreen);
	  updateState(mTimerState);
  }

  
  @Override
  public void onStop() {
	  OutputLog.timer("TimerFragment:onStop");
	  super.onStop();
	  ebableWakeLock(false);
  }

  
  @Override
  public boolean onTouch(View v, MotionEvent event) {
	  OutputLog.timer("TimerFragment:onTouch");
	  int btnindex = 8;
	  switch (v.getId()) {
	    default:
	    	return false;
	    case R.id.start_timer:
	    	OutputLog.timer("TimerFragment:onTouch:start_timer");
	    	btnindex = 8;
	    	break;
	    case R.id.cancel_timer:
	    	OutputLog.timer("TimerFragment:onTouch:cancel_timer");
	    	btnindex = 12;
	    	break;
	    case R.id.pause_timer:
	    	OutputLog.timer("TimerFragment:onTouch:pause_timer");
	    	btnindex = 10;
	    	break;
	    case R.id.continue_timer:
	    	OutputLog.timer("TimerFragment:onTouch:continue_timer");
	    	btnindex = 14;
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