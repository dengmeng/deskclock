package com.android.deskclock;

import android.os.Handler;

class TimerThread extends Thread
{
  private boolean mEnable;
  private final int DELAY = 60;
  private TimerFragment mTimer;

  public TimerThread(TimerFragment timer, boolean enable) {
	  OutputLog.timer("TimerFragment:TimerThread:init");
	  mEnable = enable;
	  mTimer = timer;
  }

  public void joinThread() {
	  mEnable = false;
	  try {
		  join();
		  return;
	  } catch (InterruptedException localInterruptedException) {
    	OutputLog.a("Error when stop thread", localInterruptedException);
	  }
  }

  public void run() {
	  OutputLog.timer("TimerFragment:TimerThread:run");
	  do {
		  if(mEnable) {
			  OutputLog.timer("TimerFragment:TimerThread:run:sendEmptyMessage");
			  TimerFragment.getHandler(mTimer).sendEmptyMessage(1);
		  } else {
			  break;
		  }
		  try {
			  Thread.sleep(DELAY);
		  } catch (InterruptedException localInterruptedException) {
	    	OutputLog.a("Error when sleep thread", localInterruptedException);
		  }
	  } while (true);
  }
}