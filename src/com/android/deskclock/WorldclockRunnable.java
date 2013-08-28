package com.android.deskclock;

import android.os.Handler;
import android.os.SystemClock;

class WorldclockRunnable implements Runnable {
	WorldclockFragmentNew mWorldclockFragment;
	WorldclockRunnable(WorldclockFragmentNew worldclockFragment) {
		mWorldclockFragment = worldclockFragment;
	}

	
	@Override
	public void run() {
		long l1, l2;
		//while (true) {
			if (!WorldclockFragmentNew.isDestory(mWorldclockFragment)) {
				l1 = SystemClock.uptimeMillis();
				l2 = l1 + (1000L - l1 % 1000L);
				WorldclockFragmentNew.getHandler(mWorldclockFragment).postAtTime(WorldclockFragmentNew.getRunnable(mWorldclockFragment), l2);
			} //else {
				//break;
			//}
		//}
	}
}