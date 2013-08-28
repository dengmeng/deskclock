package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

class TimerSlipBlock extends ImageView
{
  private double mDegrees;
  private FrameLayout.LayoutParams mFrameLayout;
  private final int mRadiusInner;
  private final int mRadiusOuter;
  private final int mRadius;
  private WatchDial mWatchDial;

  public TimerSlipBlock(WatchDial watchDial, Context context)
  {
    super(context);
    mWatchDial = watchDial;
    mRadiusInner = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.slipcirque_radiusinner);
    mRadiusOuter = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.slipcirque_radiusouter);
    mRadius = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.slipblock_radius);
    setBackgroundResource(R.drawable.timer_slip_block);
    mFrameLayout = new FrameLayout.LayoutParams(-2, -2);
    setClickable(true);
    setFocusable(true);
    setFocusableInTouchMode(true);
    setImageResource(R.drawable.timer_slip_block_pointer);
    setLayoutParams(mFrameLayout);
  }

  public void setDegrees(double degrees)
  {
    mDegrees = degrees;
  }

  public boolean isVolidPoint(double x, double y)
  {
    double xGap = x - WatchDial.getCenterX(mWatchDial);
    double yGap = y - WatchDial.getCenterY(mWatchDial);
    double zGap = Math.sqrt(xGap * xGap + yGap * yGap);
    if ((zGap > mRadiusInner) && (zGap < mRadiusOuter)) {
    	return true;
    } else {
    	return false;
    }
  }

  public void setPressed(boolean pressed)
  {
    if (pressed) {
    	setBackgroundResource(R.drawable.timer_slip_block_pressed);
    } else {
    	setBackgroundResource(R.drawable.timer_slip_block);
    }
  }

  protected void onDraw(Canvas canvas)
  {
	  canvas.rotate((float)mDegrees, mRadius, mRadius);
	  super.onDraw(canvas);
  }

  public void setLayout()
  {
    double d1 = Degrees.degree2Radian(mDegrees);
    double d2 = WatchDial.getCenterX(mWatchDial) + Math.sin(d1) * WatchDial.getCenterR(mWatchDial);
    double d3 = WatchDial.getCenterY(mWatchDial) - Math.cos(d1) * WatchDial.getCenterR(mWatchDial);
    double d4 = d2 - mRadius;
    double d5 = d3 - mRadius;
    mFrameLayout.leftMargin = (int)d4;
    mFrameLayout.topMargin = (int)d5;
    setLayoutParams(mFrameLayout);
  }

  public double getDegrees()
  {
    return mDegrees;
  }
}