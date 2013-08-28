package com.android.deskclock;

import android.os.Handler;
import android.os.Message;

class TimerHandler extends Handler {
	private TimerFragment mTimer;
	TimerHandler(TimerFragment timer) {
		OutputLog.timer("TimerFragment:TimerHandler:init");
		mTimer = timer;
  }

	//a(1):getWatchDial a(2):setTimerDuration
	//b(1):getTimerState b(2):setRemainedTime 
	//c(1):getRemainedTime c(2):startTimer
	//i(1):getEndTime
	//j(1):expireTimer
  @Override
  public void handleMessage(Message msg) {
	  //OutputLog.timer("TimerFragment:TimerHandler:handleMessage:msg="+msg.what);
	  switch (msg.what) {
	  	default:
	  		break;
	  	case 1:
	  		TimerFragment.setRemainedTime(mTimer, TimerFragment.getEndTime(mTimer) - System.currentTimeMillis());
	  		if ((TimerFragment.getRemainedTime(mTimer) > 0L) && (TimerFragment.getTimerState(mTimer) == TimerFragment.STATE_RUNNING)) {
	  			TimerFragment.getWatchDial(mTimer).setTimeValue(TimerFragment.getRemainedTime(mTimer));
	  			TimerFragment.getWatchDial(mTimer).refresh();
	  		}
	  		if (TimerFragment.getRemainedTime(mTimer) <= 0L) {
	  			TimerFragment.expireTimer(mTimer);
	  		}
	  		break;
	  }
  }
}