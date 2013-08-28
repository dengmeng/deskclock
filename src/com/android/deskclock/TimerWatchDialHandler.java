package com.android.deskclock;

import android.os.Handler;
import android.os.Message;

class TimerWatchDialHandler extends Handler
{
	WatchDial mWatchDial;
	
	TimerWatchDialHandler(WatchDial watchDial) {
	  mWatchDial = watchDial;
	}

	
	@Override
	public void handleMessage(Message msg) {
    switch (msg.what) {
	    default:
	    	break;
	    case 1:
	    	WatchDial.getTailCirque(mWatchDial).invalidate();
	    }
  }
}