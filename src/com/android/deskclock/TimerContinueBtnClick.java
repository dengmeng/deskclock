package com.android.deskclock;

import android.view.View;
import android.view.View.OnClickListener;

class TimerContinueBtnClick
  implements View.OnClickListener {
	TimerFragment mTimer;
	TimerContinueBtnClick(TimerFragment timer) {
		mTimer = timer;
  }

  @Override
  public void onClick(View v) {
	  OutputLog.timer("TimerFragment:Click continue");
	  TimerFragment.onClickContinue(mTimer);
  }
}