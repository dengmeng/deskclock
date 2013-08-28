package com.android.deskclock;

import android.database.ContentObserver;
import android.os.Handler;

class DigitalClockContentObserver extends ContentObserver {
	WorldclockDigitalClock mDigitalClock;
	public DigitalClockContentObserver(WorldclockDigitalClock digitalClock) {
		super(new Handler());
		mDigitalClock = digitalClock;
	}

	public void onChange(boolean paramBoolean) {
	  WorldclockDigitalClock.setTimeFormat(mDigitalClock);
	  WorldclockDigitalClock.updateTime(mDigitalClock);
	}
}