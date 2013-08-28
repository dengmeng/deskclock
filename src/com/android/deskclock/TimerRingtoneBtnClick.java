package com.android.deskclock;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;

class TimerRingtoneBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerRingtoneBtnClick(TimerFragment timer){
		mTimer = timer;
  }

	
	@Override
	public void onClick(View v) {
		// Intent intent = new Intent("android.intent.action.RINGTONE_PICKER");
		// intent.setClassName("com.android.thememanager",
		// "com.android.thememanager.activity.ThemeTabActivity");

		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_ALARM);
		String str = TimerFragment.getPref(mTimer).getString("Ringtone", null);
		Uri uri;
		if (str != null) {
			if (str.equals("")) {
				uri = null;
			} else {
				uri = Uri.parse(str);
			}
		} else {
			uri = null;
		}
		// intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, );
		if (uri != null) {
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					uri);
		} else {
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
					(Uri) null);
		}
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);

		mTimer.startActivityForResult(intent, 1);
	}
}