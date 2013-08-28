package com.android.deskclock;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class DeskClockPager extends ViewPager
{
  public static boolean scrollForbited;

  public DeskClockPager(Context context)
  {
    super(context);
  }

  public DeskClockPager(Context context, AttributeSet paramAttributeSet)
  {
    super(context, paramAttributeSet);
  }

  
  @Override
  protected boolean canScroll(View arg0, boolean arg1, int arg2, int arg3, int arg4) {
	  OutputLog.debug("DeskClockPager:onTouch:canScroll:scrollForbited="+scrollForbited);
	  if (scrollForbited) {
		  return true;
	  } else {
		  return super.canScroll(arg0, arg1, arg2, arg3, arg4);
	  }
  }
}
