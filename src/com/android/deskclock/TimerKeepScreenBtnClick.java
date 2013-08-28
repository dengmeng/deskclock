package com.android.deskclock;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

class TimerKeepScreenBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerKeepScreenBtnClick(TimerFragment timer) {
		mTimer = timer;
  }

  @Override
	public void onClick(View v) {
    boolean enabelKeepScreen = false;
    CheckBox checkBox = TimerFragment.getCheckBox(mTimer);
    if (!TimerFragment.getCheckBox(mTimer).isChecked()) {
        checkBox.setChecked(true);
    } else {
    	checkBox.setChecked(false);
    }
    SharedPreferences.Editor editor = TimerFragment.getPref(mTimer).edit();
    editor.putBoolean("KeepScreen", TimerFragment.getCheckBox(mTimer).isChecked());
    editor.commit();
    if (TimerFragment.getTimerState(mTimer) != TimerFragment.STATE_IDLE)
    	enabelKeepScreen = TimerFragment.getCheckBox(mTimer).isChecked();
    TimerFragment.ebableWakeLock(mTimer, enabelKeepScreen);
  }
}