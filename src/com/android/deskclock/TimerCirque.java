package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

class TimerCirque extends ImageView
{
  private Bitmap mBitmapOrigin;
  private Bitmap mBitmapPaint;
  private double mDegrees;
  private final double mRadiusOuter;
  private final double mWidth;
  private final double mRadiusCenter;
  private final int mStartX;
  private final int mStartY;
  private final double bw;
  private final double bx;
  private final double by = 2.5D;
  private Canvas mCanvas;
  private Paint mPaint;

  public TimerCirque(WatchDial watchDial, Context context) {
	  super(context);
	  OutputLog.timer("TimerCirque:TimerCirque");
	  mRadiusOuter = WatchDial.getResources(watchDial).getDimension(R.dimen.timercirque_radiusouter);
	  mWidth = WatchDial.getResources(watchDial).getDimension(R.dimen.timercirque_width);
	  mRadiusCenter = (mRadiusOuter - mWidth / 2.0D);
	  mStartX = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.timercirque_startx);
	  mStartY = (int)WatchDial.getResources(watchDial).getDimension(R.dimen.timercirque_starty);
	  this.bw = (mWidth / 2.0D);
	  this.bx = (mWidth / 2.0D);
	  mBitmapOrigin = BitmapFactory.decodeResource(getResources(), R.drawable.timer_cirque);
	  setImageBitmap(mBitmapOrigin);
	  FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(-2, -2);
	  localLayoutParams.leftMargin = mStartX;
	  localLayoutParams.topMargin = mStartY;
	  mBitmapPaint = Bitmap.createBitmap(mBitmapOrigin.getWidth(), mBitmapOrigin.getHeight(), Bitmap.Config.ARGB_8888);
	  mCanvas = new Canvas(mBitmapPaint);
	  mPaint = new Paint(1);
	  setLayoutParams(localLayoutParams);
  }

  public void setDegree(double degree) {
	  OutputLog.timer("TimerCirque:setDegree:degree="+degree);
	  mDegrees = degree;
  }

  public void refresh() {
	  //OutputLog.timer("TimerCirque:refresh");
	  double d1 = 355.0D * (360.0D - mDegrees) / 360.0D;
	  mBitmapPaint.eraseColor(0);
	  mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
	  mPaint.setColor(-1);
	  mPaint.setStyle(Paint.Style.STROKE);
	  mPaint.setStrokeWidth((float)mWidth);
	  mCanvas.drawArc(new RectF((float)this.bw, (float)this.bx, (float)(this.bw + 2.0D * mRadiusCenter), (float)(this.bx + 2.0D * mRadiusCenter)), (float)-87.5D, (float)d1, false, mPaint);
	  double d2 = mRadiusOuter + Math.sin(Degrees.degree2Radian(-92.5D)) * mRadiusCenter;
	  double d3 = mRadiusOuter - Math.cos(Degrees.degree2Radian(-92.5D)) * mRadiusCenter;
	  double d4 = mRadiusOuter + Math.sin(Degrees.degree2Radian(2.5D + d1)) * mRadiusCenter;
	  double d5 = mRadiusOuter - Math.cos(Degrees.degree2Radian(d1 + 2.5D)) * mRadiusCenter;
	  mPaint.setStyle(Paint.Style.FILL);
	  mCanvas.drawCircle((float)d3, (float)d2, (float)(mWidth / 2.0D), mPaint);
	  mCanvas.drawCircle((float)d4, (float)d5, (float)(mWidth / 2.0D), mPaint);
	  mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	  mCanvas.drawBitmap(mBitmapOrigin, 0.0F, 0.0F, mPaint);
	  setImageBitmap(mBitmapPaint);
  }
}