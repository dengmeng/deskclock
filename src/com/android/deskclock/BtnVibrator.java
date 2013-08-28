package com.android.deskclock;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Vibrator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class BtnVibrator {
  private static BtnVibrator bm;
  private static ArrayList bn = new ArrayList();
  private Vibrator mVibrator;

  private BtnVibrator(Context context) {
    mVibrator = ((Vibrator)context.getSystemService("vibrator"));
  }

  public static BtnVibrator getBtnVibrator(Context context) {
    if (bm == null)
    {
      bm = new BtnVibrator(context);
      readVibratorConfig(context);
    }
    return bm;
  }

  private static void readVibratorConfig(Context context) {
	  /*
	  OutputLog.debug("BtnVibrator:readVibratorConfig");
	  try {
		  BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open("vibrator_file")));
		  if (localBufferedReader == null)
			  return;
		  String str = localBufferedReader.readLine();
		  if (str == null)
			  return;
		  String[] arrayOfString = str.split(",");
		  int i = arrayOfString.length;
		  byte[] arrayOfByte = new byte[i];
		  for (int j = 0; j < i; ++j)
			  arrayOfByte[j] = Byte.parseByte(arrayOfString[j]);
		  bn.add(arrayOfByte);
    } catch (IOException localIOException) {
    	OutputLog.a("read vibrator file error", localIOException);
    }
    */
  }

  public void vibratorConfig(int index) {
	  /*
	  OutputLog.debug("BtnVibrator:vibratorConfig:index="+index);
	  if ((index < 0) || (index >= bn.size()))
		  return;
	  vibrateArray((byte[])bn.get(index));
	  */
  }
  
  public void vibrateArray(byte[] aArrayOfByte) {
	  /*
	  OutputLog.debug("BtnVibrator:vibrateArray");
	  int size = aArrayOfByte.length;
	  for (int j = 0; j < size; ++j)
		  mVibrator.vibrate(aArrayOfByte[j]);
	   */
  }
}