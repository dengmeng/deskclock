package com.android.deskclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class TimezoneAnalogClock extends View {
	private Time mTime;//��ȡ��ǰ�ľ�ȷʱ��
	private Drawable mClockHandHour;//ʱ��
	private Drawable mClockHandMin;//����
	private Drawable mClockHandSec;//����
	private Drawable mClockDial;//����
	private int mClockDialWidth;//���̿��
	private int mClockDialHeight;//���̸߶�

	private float mMinute;//��ǰʱ�����ֵ
	private float mHour;//��ǰʱ��ʱ��ֵ
	private float mSecond;//��ǰʱ�䳮��ֵ
	private boolean mChanged;//״̬�Ƿ�ı�
	private Runnable mRunnable;//����ʵʱˢ��ʱ����ʾ
	private boolean mStop;//�Ƿ��ڼ���״̬
	private Handler mHandler;//����ʵʱˢ��ʱ����ʾ
	Context mContext;
	
	static public boolean getRunningState(TimezoneAnalogClock mAnalogClock) {
		return !mAnalogClock.mStop;
	}
	
	static public Runnable getRunnable(TimezoneAnalogClock mAnalogClock) {
		return mAnalogClock.mRunnable;
	}
	
	static public Handler getHandler(TimezoneAnalogClock mAnalogClock) {
		return mAnalogClock.mHandler;
	}
	
	static public void onTimeChanged(TimezoneAnalogClock mAnalogClock) {
		mAnalogClock.onTimeChanged();
	}

	public TimezoneAnalogClock(Context context) {
		this(context, null);
		mContext = context;
	}

	public TimezoneAnalogClock(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
		mContext = context;
	}

	public TimezoneAnalogClock(Context context, AttributeSet attributeSet, int paramInt) {
		super(context, attributeSet, paramInt);
		mContext = context;
		//��ȡͼƬ��Դ
		Resources resources = context.getResources();
	    mClockDial = resources.getDrawable(R.drawable.clock_dial);
	    mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour);
	    mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute);
	    mClockHandSec = resources.getDrawable(R.drawable.clock_hand_second);
	    mTime = new Time();
	    //��ȡ���̴�С��Ϣ
	    mClockDialWidth = mClockDial.getIntrinsicWidth();
	    mClockDialHeight = mClockDial.getIntrinsicHeight();
	}

	//����ʱ��UI���
	private void updateDial(Time time) {
		setContentDescription(DateUtils.formatDateTime(mContext, time.toMillis(false), 129));
		Resources resources = mContext.getResources();
    
		//��ҹ��ʾ��ͬ�ķ��
		if ((mHour < 6.0F) || (mHour >= 18.0F)) {
			mClockDial = resources.getDrawable(R.drawable.clock_dial_black);
			mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour_black);
			mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute_black);
		} else {
			mClockDial = resources.getDrawable(R.drawable.clock_dial);
			mClockHandHour = resources.getDrawable(R.drawable.clock_hand_hour);
			mClockHandMin = resources.getDrawable(R.drawable.clock_hand_minute);
		}
		mClockHandSec = resources.getDrawable(R.drawable.clock_hand_second);
	}

  
  	private void onTimeChanged() {
  		//����Ϊ��ǰʱ��
  		mTime.setToNow();
  		int hour = mTime.hour;
  		int minute = mTime.minute;
  		int second = mTime.second;
  		mSecond = second;
  		mMinute = (minute + second / 60.0F);
  		mHour = (hour + mMinute / 60.0F);
  		mChanged = true;
  		updateDial(mTime);
  	}

  	//����ʱ����ȡʱ��
  	public void setTimezone(String timezone) {
  		if (TextUtils.isEmpty(timezone))
  			return;
  		mTime = new Time(timezone);
  		onTimeChanged();
  		invalidate();
  	}

  
  	@Override
  	protected void onAttachedToWindow() {
  		super.onAttachedToWindow();
  		mStop = false;
  		mHandler = new Handler();
  		mRunnable = new TimezoneAnalogClockRunnable(this);
  		mRunnable.run();
  		onTimeChanged();
  	}

  	
  	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();

	    mStop = true;
  	}

  	
  @Override
	protected void onDraw(Canvas canvas) {
	    int scaled = 0;
	    super.onDraw(canvas);
	    boolean flag = mChanged;
	    if (flag)
	      mChanged = false;
	    int width = this.mRight - this.mLeft;//�������
	    int height = this.mBottom - this.mTop;//�����߶�
	    int CenterX = width / 2;//�������ĵ������
	    int Center�� = height / 2;//�������ĵ������
	    Drawable clockdial = mClockDial;
	    int dialWidth = clockdial.getIntrinsicWidth();//����ͼƬ���
	    int dialHeight = clockdial.getIntrinsicHeight();//����ͼƬ�߶�
	    if ((width < dialWidth) || (height < dialHeight)) {
	    	scaled = 1;
	    	//�������ͬ������С��ȡ����Сֵ
	    	float dialScale = Math.min(width / dialWidth, height / dialHeight);
	    	canvas.save();
	    	canvas.scale(dialScale, dialScale, CenterX, Center��);
	    }
	    
	    //������
	    if (flag) {
	    	//�������ĵ�ȷ����ʾλ��
	    	clockdial.setBounds(CenterX - dialWidth / 2, Center�� - dialWidth / 2, CenterX + dialWidth / 2, Center�� + dialHeight / 2);
	    }
	    clockdial.draw(canvas);
	    
	    //��ʱ��
	    canvas.save();
	    canvas.rotate(360.0F * (mHour / 12.0F), CenterX, Center��);//����ʱ����ת
	    Drawable clockHour = mClockHandHour;
	    if (flag) {
	    	int hourWidth = clockHour.getIntrinsicWidth();
	    	int hourHeight = clockHour.getIntrinsicHeight();
	    	//�������ĵ�ȷ�������ϵ�λ��
	    	clockHour.setBounds(CenterX - hourWidth / 2, Center�� - hourHeight / 2, CenterX + hourWidth / 2, Center�� + hourHeight / 2);
	    }
	    clockHour.draw(canvas);
	    canvas.restore();
	    
	    //������
	    canvas.save();
	    canvas.rotate(360.0F * (mMinute / 60.0F), CenterX, Center��);//����ʱ����ת
	    Drawable clockMinute = mClockHandMin;
	    if (flag) {
	    	int minuteWidth = clockMinute.getIntrinsicWidth();
	    	int minuteHeight = clockMinute.getIntrinsicHeight();
	    	//�������ĵ�ȷ�������ϵ�λ��
	    	clockMinute.setBounds(CenterX - minuteWidth / 2, Center�� - minuteHeight / 2, CenterX + minuteWidth / 2, Center�� + minuteHeight / 2);
	    }
	    clockMinute.draw(canvas);
	    canvas.restore();
	    
	    //������
	    canvas.save();
	    canvas.rotate(360.0F * (mSecond / 60.0F), CenterX, Center��);//����ʱ����ת
	    Drawable clockSec = mClockHandSec;
	    if (flag) {
	    	int secWidth = clockSec.getIntrinsicWidth();
	    	int secHeight = clockSec.getIntrinsicHeight();
	    	//�������ĵ�ȷ�������ϵ�λ��
	    	clockSec.setBounds(CenterX - secWidth / 2, Center�� - secHeight / 2, CenterX + secWidth / 2, Center�� + secHeight / 2);
	    }
	    clockSec.draw(canvas);
	    canvas.restore();
	    
	    if (scaled != 0) 
	    	canvas.restore();
  	}

  	//����ʵ������ڷſؼ�Ԫ��
  	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	    float scaleH = 1.0F;
	    float scaleW = 1.0F;
	    int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

	    //�ο����ò��������ȵ���С����
	    if ((widthMode != MeasureSpec.UNSPECIFIED) && (widthSize < mClockDialWidth)) {
	    	scaleW = widthSize / mClockDialWidth;
	    }	
	    //�ο����ò�������߶ȵ���С����
	    if ((heightMode != MeasureSpec.UNSPECIFIED) && (heightSize < mClockDialHeight)) {
	    	scaleH = heightSize / mClockDialHeight;
	    }
	    //ȷ������ͬ������С��ȡ����Сֵ
	    float scale = Math.min(scaleW, scaleH);
	    //�Ƚ���ͼ��������С����һ�����ʵ�ֵ���������տ���
	    setMeasuredDimension(resolveSizeAndState((int)(scale * mClockDialWidth), widthMeasureSpec, 0), resolveSizeAndState((int)(scale * mClockDialHeight), heightMeasureSpec, 0));
  	}

  	//��ȡ�仯״̬
  	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
  		super.onSizeChanged(w, h, oldw, oldh);
  		mChanged = true;
  	}
}