package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

class TimerTailCirque extends ImageView
{
  private double mHeadDegree;
  private double mTailDegree;
  private Bitmap mBitmap;
  private BitmapShader mBitmapShader;
  private SweepGradient mSweepGradient;//使用SweepGradient画颜色均匀渐变的图形 
  private ComposeShader mComposeShader;//混合渲染
  private float[] mPositions;
  private boolean mRotation;//旋转方向，true为顺时针
  private TimerTrailingThread mTrailingThread;//尾随光晕线程
  private TimerTailRingThread mRingThread;//整圈光晕线程
  private final int bN = 90;
  private final int mTailcirqueStartX;
  private final int mTailcirqueStartY;
  private final int mCenterX;
  private final int mCenterY;
  private final int mRadius;
  private final int[] colorR = { Color.WHITE, 16777215 };
  private final int[] colorL = { 16777215, Color.WHITE };
  private final int MAX_DEGREE_VALUE = 100;//光晕夹角最大100度
  private final int MIN_DEGREE_VALUE = 10;//光晕夹角限值
  private Paint mPaint;
  public WatchDial mWatchDial;
  
  static public void setTrailingThread(TimerTailCirque tailCirque, TimerTrailingThread trailingThread) {//a
	  tailCirque.mTrailingThread = trailingThread;
  }
  
  static public double getHeadDegree(TimerTailCirque tailCirque) {//a
    return tailCirque.mHeadDegree;
  }

  static public double getTailDegree(TimerTailCirque tailCirque) {//b
	  return tailCirque.mTailDegree;
  }
  
  static public boolean getRotation(TimerTailCirque tailCirque) {//c
	  return tailCirque.mRotation;
  }
  
  static public void setTailDegree(TimerTailCirque tailCirque, double degree) {//a
	  tailCirque.setTailDegree(degree);
  }
  
  static public void incTailDegree(TimerTailCirque tailCirque, double degree) {//b
	  tailCirque.setTailDegree(tailCirque.mTailDegree + degree);
  }
  
  static public void incHeadDegree(TimerTailCirque tailCirque, double degree) {//c
	  tailCirque.setHeadDegree(tailCirque.mHeadDegree + degree);
  }
  
  static public void setHeadDegree(TimerTailCirque tailCirque, double degree) {//d
	  tailCirque.setHeadDegree(degree);
  }
  
  static public void decTailDegree(TimerTailCirque tailCirque, double degree) {//e
	  tailCirque.setTailDegree(tailCirque.mTailDegree - degree);
  }

  public TimerTailCirque(WatchDial watchDial, Context context) {
    super(context);
    mWatchDial = watchDial;
    mTailcirqueStartX = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.tailcirque_startx);
    mTailcirqueStartY = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.tailcirque_starty);
    mCenterX = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.center_x);
    mCenterY = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.center_y);
    mRadius = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.radius);
    mRotation = true;
    mPositions = new float[2];
    mPositions[1] = 1.0F;
    mPaint = new Paint(1);
    mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tail_cirque);
    mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
    FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(mBitmap.getWidth(), mBitmap.getHeight());
    localLayoutParams.leftMargin = mTailcirqueStartX;
    localLayoutParams.topMargin = mTailcirqueStartY;
    setLayoutParams(localLayoutParams);
  }

  public void setHeadDegree(double degree) {
    mHeadDegree = degree;
  }

  public void setTailDegree(double degree) {
    mTailDegree = degree;
  }
  

  public void setRotation(boolean flag) {
	  OutputLog.timer("TimerTailCirque:setRotation");
	  OutputLog.timer("speed:setRotation:Rotation="+flag+", degreeGap="+Degrees.degreeGap(mHeadDegree, mTailDegree));
	  OutputLog.timer("speed:setRotation:mHeadDegree="+mHeadDegree);
    if (Degrees.degreeGap(mHeadDegree, mTailDegree) >= MIN_DEGREE_VALUE)
      return;
    OutputLog.timer("TimerTailCirque:setRotation:mRotation="+mRotation);
    mRotation = flag;
  }

  
  @Override
  protected void onDraw(Canvas canvas) {
	  Matrix matrix = new Matrix();//获取矩阵对象
	  int[] arrayColor;
	  double degreeGap = Degrees.degreeGap(mHeadDegree, mTailDegree);
	  if (mRotation) {//顺时针方向
		  arrayColor = colorR;
		  if (degreeGap > MAX_DEGREE_VALUE) {
			  mTailDegree = (mHeadDegree - MAX_DEGREE_VALUE);
			  if (mTailDegree < 0.0D) {
				  mTailDegree = (360.0D + mTailDegree);
			  }
		  }
		  mPositions[0] = (float)(1.0D - degreeGap / 360.0D);
	      mPositions[1] = 1.0F;
	  } else {
		  arrayColor = colorL;
		  if (degreeGap > MAX_DEGREE_VALUE) {
			  mTailDegree = (MAX_DEGREE_VALUE + mHeadDegree);
			  if (mTailDegree >= 360.0D) {
				  mTailDegree -= 360.0D;
			  }
		  }
		  mPositions[0] = 0.0F;
	      mPositions[1] = (float)(degreeGap / 360.0D);
	  }
	  
	  matrix.setRotate((float)(-90.0D + mHeadDegree), mCenterX, mCenterY);
      mSweepGradient = new SweepGradient(mCenterX, mCenterY, arrayColor, mPositions);
      mSweepGradient.setLocalMatrix(matrix);
      mComposeShader = new ComposeShader(mBitmapShader, mSweepGradient, PorterDuff.Mode.DST_OUT);
      mPaint.setShader(mComposeShader);
      canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
      super.onDraw(canvas);
  }

  public double getTailDegreeGap() {
    return Degrees.degreeGap(mHeadDegree, mTailDegree);
  }

  public void startRing() {
    if ((mRingThread != null) && (mRingThread.isAlive()))
      return;
    OutputLog.timer("WatchDial:startRing");
    mRingThread = new TimerTailRingThread(this, true);
    mRingThread.start();
  }

  public void stopRing() {
    if ((mRingThread != null) && (mRingThread.isAlive())) {
    	TimerTailRingThread.setRunningState(mRingThread, false);
	    try
	    {
	      mRingThread.join();
	      mHeadDegree = 0.0D;
	      mTailDegree = 0.0D;
	      return;
	    }
	    catch (InterruptedException localInterruptedException)
	    {
	    	OutputLog.a("Error when stop mRingThread", localInterruptedException);
	    }
    }
  }

  public void starTrailing() {
    if (mTrailingThread != null)
      return;
    OutputLog.moon("speed:starTrailing");
    mTrailingThread = new TimerTrailingThread(this);
    mTrailingThread.setState(true);
    mTrailingThread.start();
  }
  
  	public void stopTrailing() {
	    if ((mTrailingThread != null) && (mTrailingThread.isAlive())) {
		    mTrailingThread.setState(false);
		    try
		    {
		    	mTrailingThread.join();
		    	mHeadDegree = 0.0D;
		    	mTailDegree = 0.0D;
		    	mTrailingThread = null;
		    	return;
		    }
		    catch (InterruptedException localInterruptedException)
		    {
		    	OutputLog.a("Error when stop mTrailingThread", localInterruptedException);
		    }
	    }
  	}
  	
  	public void enableTrailing(boolean flag){
  		if (flag) {
  			mTrailingThread.setState(true);
  		} else {
  			mTrailingThread.setState(false);
  		}
  	}
}