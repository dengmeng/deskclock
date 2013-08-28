package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;

public class WatchDial extends FrameLayout
  implements View.OnTouchListener {
  private boolean mRunning;
  private TimerSlipBlock mSlipBlock;
  private TimerTailCirque mTailCirque;
  private TimerCirque mTimerCirque;
  private TimerTimeLinearLayout mTimerTime;
  private final long MAX_TIME_VALUE = 360000000L;//倒计时最大值,100小时
  private int mLap;
  private long mTotaltime;
  private TimerInterface mTimerInterface;
  private long mPreTime;
  private long mCurrentTime;
  private double mPreDegree;
  private final long SECOND = 1000L;
  private final long HOUR = 3600L;
  private final long MINUTE = 60L;
  private final long W = 1800000L;
  private final int mCenterR;
  private final int mCenterX;
  private final int MIN_DEGREE_GAP = 30;
  private final int ab = 1;
  private final int mCenterY;
  Handler mHandler = new TimerWatchDialHandler(this);
  private Resources mResources;
  private long mValue;
  private Context mContext;
  static public boolean STATE_IDLE = false;
  static public boolean STATE_RUNNING = true;

  
  static public int getCenterX(WatchDial watchdial) {
	  return watchdial.mCenterX;
  }
  
  static public int getCenterY(WatchDial watchdial) {
	  return watchdial.mCenterY;
  }
  
  static public int getCenterR(WatchDial watchdial) {
	  return watchdial.mCenterR;
  }
  
  static public Resources getResources(WatchDial watchdial) {
	  return watchdial.mResources;
  }
  
  static public TimerTailCirque getTailCirque(WatchDial watchdial) {
	  return watchdial.mTailCirque;
  }
  
  static public Handler getHandler(WatchDial watchdial) {
	  return watchdial.mHandler;
  }
  
  public WatchDial(Context context)
  {
    this(context, null);
    mContext = context;
  }

  public WatchDial(Context context, AttributeSet attrs)
  {
    this(context, attrs, 0);
    mContext = context;
  }

  public WatchDial(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    OutputLog.timer("WatchDial:WatchDial");
    mContext = context;
    mResources = context.getResources();
    mCenterX = (int)mResources.getDimension(R.dimen.watchdial_centerx);
    mCenterY = (int)mResources.getDimension(R.dimen.watchdial_centery);
    mCenterR = (int)mResources.getDimension(R.dimen.watchdial_radius);
    mTimerTime = new TimerTimeLinearLayout(context);
    mSlipBlock = new TimerSlipBlock(this, context);
    mSlipBlock.setDegrees(0.0D);
    mSlipBlock.setLayout();
    mSlipBlock.setOnTouchListener(this);
    mTailCirque = new TimerTailCirque(this, context);
    mTimerCirque = new TimerCirque(this, context);
    mTimerInterface = null;
    updateView(WatchDial.STATE_IDLE);
    addView(mTailCirque);
    mTailCirque.setVisibility(View.VISIBLE);
    addView(mSlipBlock);
    addView(mTimerCirque);
    addView(mTimerTime);
    updateTimerTime(false);
  }

  //相对圆心的弧度值，取值范围0-2PI
  private double getRadianOfPoint(double x, double y) {
	  OutputLog.timer("WatchDial:getRadianOfPoint");
	  double dyR = -mCenterR * (y - mCenterY);
	  double radian;
	  if ((dyR > -1.0E-006D) && (dyR < 1.0E-006D)) { //y值极接近圆心y值
		  if (x < mCenterX) {//坐标轴3,4区
			  radian = Degrees.PI * 1.5D;//270度位置
		  } else {
			  radian = Degrees.PI / 2;//90度位置
		  }
	  } else {
		  double R = mCenterR;
		  double dx = x - mCenterX;//邻边
		  double dy = y - mCenterY;//对角边
		  double dz = Math.sqrt(dx * dx + dy * dy);//斜边
		  double cosRadian = dyR / R / dz;//y/z得到的cos值
		  if (x < mCenterX) {//坐标轴3,4区
			  radian = Degrees.PI * 2 - Math.acos(cosRadian);
		  } else {
			  radian = Math.acos(cosRadian);
		  }
	  }
	  return radian;
  }

  private void updateTimerTime(boolean enable) {
	  OutputLog.timer("WatchDial:updateTimerTime");
	  mTimerTime.setTime(getHour(), getMinute(), getSecond());
	  mTimerTime.setStart(enable);
	  mTimerTime.updateViewRes();
  }

  private void layoutTime() {
	  OutputLog.timer("WatchDial:layoutTime");
	  int i = mCenterX - mTimerTime.getVolidWidth() / 2;
	  int j = mCenterY - mTimerTime.getVolidHeight() / 2;
	  mTimerTime.layout(i, j, i + mTimerTime.getMeasuredWidth(), j + mTimerTime.getMeasuredHeight());
  }

  public void setTimerInterface(TimerInterface instance){
	  OutputLog.timer("WatchDial:setTimerInterface");
	  mTimerInterface = instance;
  }

  public void updateView(boolean running) {
	  OutputLog.timer("WatchDial:updateView:running="+running);
	  mRunning = running;
	  if (mRunning) {
		  mSlipBlock.setVisibility(View.GONE);
		  mTailCirque.setVisibility(View.GONE);
		  mTimerCirque.setVisibility(View.VISIBLE);
	  } else {
		  mSlipBlock.setVisibility(View.VISIBLE);
		  mTailCirque.setVisibility(View.VISIBLE);
		  mTimerCirque.setVisibility(View.GONE);
	  }
  }

  public void setTimeValue(long time)
  {
	  OutputLog.timer("WatchDial:setTimeValue:time="+time+";mTotaltime="+mTotaltime+";mRunning="+mRunning);
	  if (mRunning) {
		  if (mTotaltime <= 0) {
			  OutputLog.timer("WatchDial:setTimeValue:ERROR-------mTotaltime="+mTotaltime);
		  } else {
			  mTimerCirque.setDegree(360.0D * (mTotaltime - time) / mTotaltime);
		  }
	  }
	  mValue = time;
	  if ((mRunning) || (mTimerInterface == null))
		  return;
	  mTimerInterface.onTimeChanged(getHour(), getMinute(), getSecond());
  }

  public void setTotalTime(long time) {
	  OutputLog.timer("WatchDial:setTotalTime:time="+time);
	  mTotaltime = time;
  }

  public void refresh() {
	  //OutputLog.timer("WatchDial:refresh:mRunning="+mRunning);
	  if (mRunning) {
		  mTimerCirque.refresh();
		  updateTimerTime(mRunning);
	  } else {
		  mSlipBlock.setLayout();
		  mTailCirque.invalidate();
	  }
  }

  public int getHour(){
	  return (int)(mValue / SECOND / HOUR);
  }

  public int getMinute() {
	  return (int)(mValue / SECOND % HOUR / MINUTE);
  }

  public int getSecond() {
	  return (int)(mValue / SECOND % HOUR % MINUTE);
  }

  public long getValue() {
	  return mValue;
  }

  public void reset() {
	  OutputLog.timer("WatchDial:reset");
	  mRunning = WatchDial.STATE_IDLE;
	  mSlipBlock.setDegrees(0.0D);
	  mTailCirque.setHeadDegree(0.0D);
	  mTailCirque.setTailDegree(0.0D);
	  setTimeValue(0L);
	  updateTimerTime(false);//added by dengmeng
	  mLap = 0;
  }

  public void showTailRing() {//整圈光晕
	  OutputLog.timer("WatchDial:showTailRing");
	  mTailCirque.startRing();
  }

   
  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
	  OutputLog.timer("WatchDial:onLayout");
	  super.onLayout(changed, left, top, right, bottom);
	  layoutTime();
  }

//参数：弧度*半径/时间 ，即在孤线上移动的速度，
  private void speed(double speed)
  {
    int i = 50;
    int j = (int)(speed / 40.0D);
    if (j > i);
    
    //OutputLog.timer("speed:speed="+j);
    //while (true)
    //{
      //BtnVibrator.getBtnVibrator(mContext).vibrateArray(new byte[] { 0, i, 10, -5, 10, 0, 5 });
      //return;
      //i = j;
    //}
  }
  
  @Override
  public boolean onTouch(View v, MotionEvent event) {
	  if (v == mSlipBlock) {
		  OutputLog.timer("WatchDial:onTouch:mSlipBlock");
		  switch (event.getAction()){
    		default:
    			break;
    		case MotionEvent.ACTION_DOWN:
    			OutputLog.timer("WatchDial:onTouch:ACTION_DOWN");
    			mCurrentTime = mPreTime = System.currentTimeMillis();
    			DeskClockPager.scrollForbited = true;
    	        mSlipBlock.setPressed(true);
    	        BtnVibrator.getBtnVibrator(mContext).vibratorConfig(18);
    			break;
    		case MotionEvent.ACTION_MOVE:
    			OutputLog.timer("WatchDial:onTouch:ACTION_MOVE");
    			OutputLog.moon("WatchDial:onTouch:ACTION_MOVE");
    			double pointX, pointY;
    			mTailCirque.stopRing();
    	        mTailCirque.starTrailing();
    	        pointX = event.getX() + mSlipBlock.getLeft();
    	        pointY = event.getY() + mSlipBlock.getTop();
    	        mPreTime = mCurrentTime;
    	        mCurrentTime = System.currentTimeMillis();
    	        if (mSlipBlock.isVolidPoint(pointX, pointY)) {
    	        	double currDegree, pointDegreeGap;
    	        	long time;
    	        	currDegree = Degrees.radian2Degree(getRadianOfPoint(pointX, pointY));//当前point的角度
    	        	//mPreDegree = mSlipBlock.getDegrees();
    	        	pointDegreeGap = Degrees.degreeGap(currDegree, mSlipBlock.getDegrees());//当前point与上个point的角度差
    	        	
    	        	//speed(1000.0D * (Degrees.degree2Radian(Math.abs(currDegree - mPreDegree)) * mCenterR) / (mCurrentTime - mPreTime));
    	        	//mPreDegree = currDegree;

	        		//复位按钮到原点
	        		if (((pointY <= mCenterY) && (mLap == 0) && (mSlipBlock.getDegrees() < 180.0D) && (currDegree > 180.0D))//回到原点情况
	        			|| ((mSlipBlock.getDegrees() <= 0.0001D) && (currDegree >= 30.0D) && (mLap == 0)) //防止第一圈出现逆时针情况
	        			|| (mLap < 0) || (mValue < 0L)) {
	        			currDegree = 0.0D;
	        		}
	        		
	        		//mTailCirque.setHeadDegree(currDegree);
	        		if (mSlipBlock.getDegrees() < currDegree) {
	        			OutputLog.timer("WatchDial:onTouch:ACTION_MOVE:right:mPreDegree="+mSlipBlock.getDegrees()+",currDegree="+currDegree);
	        			
	        			if ((pointY < mCenterY) && (mSlipBlock.getDegrees() < 180.0D) && (currDegree > 180.0D)) {
	        				OutputLog.timer("speed:ACTION_MOVE:setRotation---1");
	        				mTailCirque.setRotation(false);
	        			} else {
	        				OutputLog.timer("speed:ACTION_MOVE:setRotation---2");
	        				mTailCirque.setRotation(true);
	        			}
	        		} else if (mSlipBlock.getDegrees() > currDegree) {
	        			OutputLog.timer("WatchDial:onTouch:ACTION_MOVE:left:mPreDegree="+mSlipBlock.getDegrees()+",currDegree="+currDegree);
	        			
	        			if ((pointY < mCenterY) && (mSlipBlock.getDegrees() > 180.0D) && (currDegree < 180.0D)) {
	        				OutputLog.timer("speed:ACTION_MOVE:setRotation---3");
	        				mTailCirque.setRotation(true);
	        			} else {
	        				OutputLog.timer("speed:ACTION_MOVE:setRotation---4");
	        				mTailCirque.setRotation(false);
	        			}
	        		}
    	        		
	        		if (pointY < mCenterY) {
	        	        if ((mSlipBlock.getDegrees() < 180.0D) && (currDegree > 180.0D)) {
	        	        	mLap = (-1 + mLap);
	        	        } else if ((mSlipBlock.getDegrees() > 180.0D) && (currDegree < 180.0D)) {
	        	        	mLap = (1 + mLap);
	        	        }
	        	    }
	        		//if (pointDegreeGap > 30.0D + mTailCirque.getTailDegreeGap()) {//移动的弧度足够大才判断是否需要调整光晕方向
	        			//;
	        		

	        		//}
	        		//mPreDegree = currDegree;//待确认
    	        		
	        		time = (long)(1800000.0D * (currDegree / 360.0D + mLap));
	        		if (time >= MAX_TIME_VALUE) {
	        			time = MAX_TIME_VALUE;
	        		}
    	        		
	        		mSlipBlock.setDegrees(currDegree);
	        	    mSlipBlock.setLayout();
	        	    setTimeValue(time);
	        	    mTailCirque.setHeadDegree(currDegree);
	        	    mTailCirque.invalidate();
	        	    updateTimerTime(false);
	        	}
    			break;
    		case MotionEvent.ACTION_UP:
    			OutputLog.timer("WatchDial:onTouch:ACTION_UP");
    			DeskClockPager.scrollForbited = false;
    		    mSlipBlock.setPressed(false);
    		    //mTailCirque.stopTrailing();
    		    BtnVibrator.getBtnVibrator(mContext).vibratorConfig(19);
    			break;
		  }
		  return true;
	  }
	  switch (event.getAction()){
	  	default:
	  		break;
		case MotionEvent.ACTION_UP:
			OutputLog.timer("WatchDial:onTouch:ACTION_UP");
			if (DeskClockPager.scrollForbited) {
				DeskClockPager.scrollForbited = false;
			    mSlipBlock.setPressed(false);
			    BtnVibrator.getBtnVibrator(mContext).vibratorConfig(19);
			}
			break;
	  }
	  return false;
  }
}