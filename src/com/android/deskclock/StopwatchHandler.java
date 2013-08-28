package com.android.deskclock;

import android.os.Handler;
import android.os.Message;

class StopwatchHandler extends Handler
{
	StopwatchChronometer mChronometer;
	StopwatchHandler(StopwatchChronometer stopwatchChronometer){
		mChronometer = stopwatchChronometer;
	}

	
  @Override
	public void handleMessage(Message msg) {
	  //OutputLog.debug("StopwatchHandler:handleMessage:msg="+msg.what);
    if (!StopwatchChronometer.getState(mChronometer))
      return;
    StopwatchChronometer.refresh(mChronometer, System.currentTimeMillis());
    mChronometer.dispatchChronometerTick();
    sendMessageDelayed(Message.obtain(this, 2), 100L);
  }
}