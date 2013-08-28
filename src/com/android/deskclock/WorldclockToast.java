package com.android.deskclock;

import android.widget.Toast;
public class WorldclockToast {
	private static Toast mToast = null;

	public static void start(Toast paramToast){
		if (mToast != null)
			mToast.cancel();
		mToast = paramToast;
	}

	public static void stop() {
		if (mToast != null)
			mToast.cancel();
		mToast = null;
	}
}