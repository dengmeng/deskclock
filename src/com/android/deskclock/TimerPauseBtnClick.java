package com.android.deskclock;

import android.view.View;
import android.view.View.OnClickListener;

class TimerPauseBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerPauseBtnClick(TimerFragment timer) {
		mTimer = timer;
  }

	
  @Override
  public void onClick(View v) {
	  OutputLog.timer("TimerFragment:Click pause");
	  TimerFragment.onClickPause(mTimer);
  }
}