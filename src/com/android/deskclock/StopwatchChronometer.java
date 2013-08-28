package com.android.deskclock;

//import android.R;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class StopwatchChronometer extends LinearLayout {
  private static final int[] mNumImg = { 
	  R.drawable.time_large_0, R.drawable.time_large_1, R.drawable.time_large_2, 
	  R.drawable.time_large_3, R.drawable.time_large_4, R.drawable.time_large_5, 
	  R.drawable.time_large_6, R.drawable.time_large_7, R.drawable.time_large_8, 
	  R.drawable.time_large_9};
  private boolean mIsAttachedToWindow = false;
  private long mTime;
  private StopwatchChronometerInterface mInterface;
  private ImageView first_digital;
  private ImageView second_digital;
  private ImageView third_digital;
  private ImageView fourth_digital;
  private ImageView fifth_digital;
  private TextView mHours;
  private Handler mHandler = new StopwatchHandler(this);
  private boolean mRunning;
  private boolean mStarted;
  private boolean mVisible;
  private Context mContext;

  static public boolean getState(StopwatchChronometer chronometer) {
	  //OutputLog.stopwatch("StopwatchChronometer:getState="+((chronometer.mVisible) && (chronometer.mStarted)));
	  return (chronometer.mVisible) && (chronometer.mStarted);
  }
  
  static public void refresh(StopwatchChronometer chronometer, long time) {
	  //OutputLog.stopwatch("StopwatchChronometer:refresh");
	  chronometer.display(time);
  }
  
  public StopwatchChronometer(Context context) {
	  this(context, null);
	  mContext = context;
  }

  public StopwatchChronometer(Context context, AttributeSet paramAttributeSet) {
    super(context, paramAttributeSet);
    mContext = context;
  }

	private void stateChanged() {
		OutputLog.stopwatch("StopwatchChronometer:stateChanged:mVisible="
				+ mVisible);
		OutputLog.stopwatch("StopwatchChronometer:stateChanged:mStarted="
				+ mStarted);
		if ((mVisible) && (mStarted)) {
			if (true != mRunning) {
				display(System.currentTimeMillis());
				dispatchChronometerTick();
				mHandler.sendMessageDelayed(Message.obtain(mHandler, 2), 100L);
				mRunning = true;
			} else {
				mHandler.removeMessages(2);
				mRunning = false;
			}
		} else {
			mHandler.removeMessages(2);
			mRunning = false;
		}

	}

  private void display(long currTime)
  {
	  //OutputLog.stopwatch("StopwatchChronometer:display");
	  //monitorenter;
	  synchronized (this) {
		  long displayTime = currTime - mTime;
		  int aHours = (int)(displayTime / 3600000L);
		  int aMin = (int)(displayTime % 3600000L / 60000L);
		  int aSec = (int)(displayTime % 60000L / 1000L);
		  int aMs = (int)(displayTime % 1000L / 100L);

		  first_digital.setImageResource(mNumImg[(aMin / 10)]);
		  second_digital.setImageResource(mNumImg[(aMin % 10)]);
		  third_digital.setImageResource(mNumImg[(aSec / 10)]);
		  fourth_digital.setImageResource(mNumImg[(aSec % 10)]);
		  fifth_digital.setImageResource(mNumImg[aMs]);
		  if (aHours > 0) {
			  mHours.setVisibility(View.VISIBLE);
			  mHours.setText(mContext.getString(R.string.hours, Integer.valueOf(aHours)));
			  //return;
		  } else {
			  mHours.setVisibility(View.INVISIBLE);
		  }
	  } 
	  //finally {
		  //monitorexit;
		  //Monitor.Exit(displaylock);
		  //OutputLog.stopwatch("StopwatchChronometer:display:error");
    //}
  }

  private void init() {
	  OutputLog.stopwatch("StopwatchChronometer:init");
	  mTime = System.currentTimeMillis();
	  display(mTime);
  }

  void dispatchChronometerTick() {
	  //OutputLog.stopwatch("StopwatchChronometer:dispatchChronometerTick");
	  if (mInterface == null)
		  return;
	  mInterface.b(this);
  }

  public long getBase(){
	  OutputLog.stopwatch("StopwatchChronometer:getBase:mTime="+mTime);
	  return mTime;
  }

  
  @Override
  protected void onAttachedToWindow() {
	  OutputLog.stopwatch("StopwatchChronometer:onAttachedToWindow");
	  super.onAttachedToWindow();
	  if (!mIsAttachedToWindow) {
		  mIsAttachedToWindow = true;
		  init();
	  }
	  //mVisible = true;//dengmeng add
  }

  
  @Override
  protected void onDetachedFromWindow() {
	  OutputLog.stopwatch("StopwatchChronometer:onDetachedFromWindow");
	  super.onDetachedFromWindow();
	  mIsAttachedToWindow = false;
	  mVisible = false;
	  stateChanged();
  }

  
  @Override
  protected void onFinishInflate() {
	  OutputLog.stopwatch("StopwatchChronometer:onFinishInflate");
	  super.onFinishInflate();
	  first_digital = ((ImageView)findViewById(R.id.first_digital));
	  second_digital = ((ImageView)findViewById(R.id.second_digital));
	  third_digital = ((ImageView)findViewById(R.id.third_digital));
	  fourth_digital = ((ImageView)findViewById(R.id.fourth_digital));
	  fifth_digital = ((ImageView)findViewById(R.id.fifth_digital));
	  mHours = ((TextView)findViewById(R.id.hours));
  }

  
  @Override
  protected void onWindowVisibilityChanged(int visibility) {
	  super.onWindowVisibilityChanged(visibility);
	  if (visibility == View.VISIBLE){
		  mVisible = true;
	  } else {
		  mVisible = false;
	  }
	  OutputLog.stopwatch("StopwatchChronometer:onWindowVisibilityChanged:mVisible="+mVisible);
	  stateChanged();
  }

  public void setBase(long time) {
	  OutputLog.stopwatch("StopwatchChronometer:setBase");
	  mTime = time;
	  dispatchChronometerTick();
	  display(System.currentTimeMillis());
  }

  public void start() {
	  OutputLog.stopwatch("StopwatchChronometer:start");
	  mStarted = true;
	  stateChanged();
  }

  public void stop() {
	  OutputLog.stopwatch("StopwatchChronometer:stop");
	  mStarted = false;
	  stateChanged();
  }
}
