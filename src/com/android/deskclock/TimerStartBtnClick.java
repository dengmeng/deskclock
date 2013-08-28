package com.android.deskclock;

import android.view.View;
import android.view.View.OnClickListener;

class TimerStartBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerStartBtnClick(TimerFragment timer) {
		mTimer = timer;
	}

  @Override
  public void onClick(View v) {
	  long time = 60L * (1000L * (TimerFragment.getWatchDial(mTimer).getValue() / 1000L / 60L));
	  OutputLog.timer("TimerFragment:Click start:time="+time);
	  if (TimerFragment.getTimerState(mTimer) == 0) {
	      TimerFragment.setTimerDuration(mTimer, time);
	      TimerFragment.setRemainedTime(mTimer, time);
	  }
	  TimerFragment.startTimer(mTimer, TimerFragment.getRemainedTime(mTimer));
  }
}