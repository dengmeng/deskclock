package com.android.deskclock;

import android.os.Handler;
import android.os.SystemClock;

class TimezoneAnalogClockRunnable
  implements Runnable {
	TimezoneAnalogClock mAnalogClock; 
	TimezoneAnalogClockRunnable(TimezoneAnalogClock timezoneAnalogClock) {
		mAnalogClock = timezoneAnalogClock;
	}

	
	@Override
	public void run() {
		long now, runtime;
		if (TimezoneAnalogClock.getRunningState(mAnalogClock)) {
			//每秒种刷新一次
			now= SystemClock.uptimeMillis();
			runtime = now + (1000L - now % 1000L);
		    TimezoneAnalogClock.getHandler(mAnalogClock).postAtTime(TimezoneAnalogClock.getRunnable(mAnalogClock), runtime);
		    TimezoneAnalogClock.onTimeChanged(mAnalogClock);
		    mAnalogClock.invalidate();
		}
	}
}