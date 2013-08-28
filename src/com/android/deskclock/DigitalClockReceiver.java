package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import java.util.Calendar;

class DigitalClockReceiver extends BroadcastReceiver {
	WorldclockDigitalClock mDigitalClock;
	DigitalClockReceiver(WorldclockDigitalClock digitalClock) {
		mDigitalClock = digitalClock;
	}

	public void onReceive(Context paramContext, Intent intent) {
		if ((WorldclockDigitalClock.getState(mDigitalClock)) && (intent.getAction().equals("android.intent.action.TIMEZONE_CHANGED"))) {
			WorldclockDigitalClock.updateTime(mDigitalClock, Calendar.getInstance());
		}
	    WorldclockDigitalClock.getHandler(mDigitalClock).post(new Runnable() {
			@Override
			public void run() {
				WorldclockDigitalClock.updateTime(mDigitalClock);
			}
	    });
	}
}