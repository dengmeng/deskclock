package com.android.deskclock;

import android.os.Handler;

class TimerTrailingThread extends Thread
{
  private final double GRADIENT_RATE = 0.15D;
  private boolean mRunning;
  private final int THREAD_DELAY = 70;
  TimerTailCirque mTailCirque;

  public TimerTrailingThread(TimerTailCirque tailCirque) {
	  mTailCirque = tailCirque;
  }

  public void setState(boolean state) {
	  OutputLog.timer("TimerTrailingThread:setState:mRunning="+mRunning);
	  mRunning = state;
  }

  public void run() {
	  OutputLog.timer("TimerTrailingThread:run");
	  do {
		  if (mRunning) {
			  double DegreeGap = Degrees.degreeGap(TimerTailCirque.getHeadDegree(mTailCirque), TimerTailCirque.getTailDegree(mTailCirque));
			  //OutputLog.timer("TimerTrailingThread:run:DegreeGap="+DegreeGap);
			  //OutputLog.timer("speed:run:DegreeGap="+DegreeGap+", mHeadDegree="+TimerTailCirque.getHeadDegree(mTailCirque));
			  if (DegreeGap >= 10.0D) {//夹角小于10度时，逐渐缩短光晕
				  //OutputLog.timer("TimerTrailingThread:DegreeGap >= 10.0D");
				  DegreeGap = DegreeGap * GRADIENT_RATE;
			  } else {
				  OutputLog.timer("TimerTrailingThread:return");
				  TimerTailCirque.setTailDegree(mTailCirque, TimerTailCirque.getHeadDegree(mTailCirque));
				  TimerTailCirque.setTrailingThread(mTailCirque, null);
				  break;
			  }

			  if (TimerTailCirque.getRotation(mTailCirque)) {//顺时针旋转
				  TimerTailCirque.incTailDegree(mTailCirque, DegreeGap);
			      if (TimerTailCirque.getTailDegree(mTailCirque) > 360.0D) {
			    	  TimerTailCirque.decTailDegree(mTailCirque, 360.0D);
			      }
			  } else {
				  TimerTailCirque.decTailDegree(mTailCirque, DegreeGap);
			      if (TimerTailCirque.getTailDegree(mTailCirque) < 0.0D) {
			    	  TimerTailCirque.incTailDegree(mTailCirque, 360.0D);
			      }
			  }
			  mTailCirque.mWatchDial.mHandler.sendEmptyMessage(1);
		      try {
		    	  Thread.sleep(THREAD_DELAY);
		      } catch (InterruptedException localInterruptedException) {
		    	  OutputLog.a("Error when sleep in thread", localInterruptedException);
		      }
		  } else {
			  //TimerTailCirque.setTrailingThread(mTailCirque, null);
			  break;
		  }
	  }while (true);
  }
}