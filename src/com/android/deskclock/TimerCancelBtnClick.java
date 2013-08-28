package com.android.deskclock;

import android.view.View;
import android.view.View.OnClickListener;

class TimerCancelBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerCancelBtnClick(TimerFragment timer) {
		mTimer = timer;
  }

	
  @Override
  public void onClick(View v) {
	  OutputLog.timer("TimerFragment:Click cancel");
	  TimerFragment.onClickCancel(mTimer);
  }
}