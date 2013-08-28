package com.android.deskclock;

import android.os.Handler;

class TimerTailRingThread extends Thread
{
  private boolean mRunning;
  private final int THREAD_DELAY = 50;
  private final double GRADIENT_DEGREES = 10.0D;
  private final double DEGREE_GAP_MAX = 100.0D;
  TimerTailCirque mTailCirque;

  static public void setRunningState(TimerTailRingThread thread, boolean state) {
	  thread.mRunning = state;
  }
  
  public TimerTailRingThread(TimerTailCirque tailCirque, boolean running)
  {
	  mTailCirque = tailCirque;
	  mRunning = running;
  }

  public void run() {
	  mTailCirque.setRotation(true);//˳ʱ����ת
	  OutputLog.timer("TimerTailRingThread:run:start="+mRunning);
	  do {
		  if (mRunning) {
			  mTailCirque.mWatchDial.mHandler.sendEmptyMessage(1);
			  if (TimerTailCirque.getHeadDegree(mTailCirque) < 360.0D) {//��ʼʱͷ��10�ȵ���
		          TimerTailCirque.incHeadDegree(mTailCirque, GRADIENT_DEGREES);
			  }
			  
			  if ((TimerTailCirque.getHeadDegree(mTailCirque) - TimerTailCirque.getTailDegree(mTailCirque) >= DEGREE_GAP_MAX) && (TimerTailCirque.getHeadDegree(mTailCirque) < 360.0D)) {
				  TimerTailCirque.setTailDegree(mTailCirque, TimerTailCirque.getHeadDegree(mTailCirque) - DEGREE_GAP_MAX);//������ͷβ����100�ȼн�
			  }
			  if (TimerTailCirque.getHeadDegree(mTailCirque) >= 359.999999D) {//����ʱͷ��10�ȵ���
				  TimerTailCirque.incTailDegree(mTailCirque, GRADIENT_DEGREES);
			  }
			  
			  if ((TimerTailCirque.getHeadDegree(mTailCirque) >= 360.0D) && (TimerTailCirque.getTailDegree(mTailCirque) >= 360.0D)) {//��������λ
				  TimerTailCirque.setHeadDegree(mTailCirque, 0.0D);
			      TimerTailCirque.setTailDegree(mTailCirque, 0.0D);
			      OutputLog.timer("TimerTailRingThread:run:break");
			      break;
			  }
	
		      try
		      {
		        Thread.sleep(THREAD_DELAY);
		      }
		      catch (InterruptedException localInterruptedException)
		      {
		    	  OutputLog.a("Error when sleep in thread", localInterruptedException);
		      }
		  } else {
			  break;
		  }
	  }while (true);

  }
}