package com.android.deskclock;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;

class OutputLog
{
	static private boolean debug = true;
  static void a(String paramString, Exception paramException)
  {
    Log.e("AlarmClock", paramString, paramException);
  }

  static void e(String paramString)
  {
    Log.e("AlarmClock", paramString);
  }

  static String formatTime(long longTime)
  {
    return new SimpleDateFormat("HH:mm:ss.SSS aaa").format(new Date(longTime));
  }

  static void h(String str)
  {
    Log.i("AlarmClock", str);
  }

  static void i(String str)
  {
    Log.wtf("AlarmClock", str);
  }

  static void v(String str)
  {
    Log.v("AlarmClock", str);
  }
  
  static void debug(String str)
  {
	  if (!debug)
		  return;
	  Log.v("moon", str);
  }
  
  static void stopwatch(String str)
  {
	  if (!debug)
		  return;
	  Log.v("stopwatch", str);
	  debug(str);
  }
  
  static void timer(String str)
  {
	  if (!debug)
		  return;
	  Log.v("timer", str);
	  debug(str);
  }
  
  static void worldclock(String str)
  {
	  if (!debug)
		  return;
	  Log.v("worldclock", str);
	  debug(str);
  }
  
  static void alarm(String str)
  {
	  if (!debug)
		  return;
	  Log.v("alarm", str);
	  debug(str);
  }
  
  static void moon(String str)
  {
	  if (!debug)
		  return;
	  Log.v("debug", str);
  }
}