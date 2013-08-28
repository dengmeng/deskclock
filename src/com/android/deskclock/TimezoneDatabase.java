package com.android.deskclock;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class TimezoneDatabase {
	private static TimezoneDatabase mDatabase;
	private ArrayList mArray = new ArrayList();

	private TimezoneDatabase(Context context) {
		init(context);
	}

  public static TimezoneDatabase getDatabase(Context context) {
    if (mDatabase == null)
      mDatabase = new TimezoneDatabase(context);
    return mDatabase;
  }

  private void init(Context paramContext) {
	  try {
		  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(paramContext.getAssets().open("city_timezone")));
		  if (bufferedReader != null) {
			  String[] arrayOfString;
			  String str;
			  do {
				  str = bufferedReader.readLine();
				  if (str == null)
					  return;
				  arrayOfString = str.split("\t");
				  if ((arrayOfString == null) || (arrayOfString.length != 6)) {
					  break;
				  } else {
					  mArray.add(
							  new TimezoneInfo(
							  Integer.parseInt(arrayOfString[0]), 
							  arrayOfString[1], 
							  arrayOfString[2], 
							  arrayOfString[3], 
							  arrayOfString[4], arrayOfString[5]));
				  }
			  } while (true);
		  }
    }
    catch (IOException localIOException)
    {
      OutputLog.a("parse city timezone error", localIOException);
    }
  }

  	public TimezoneInfo getInfoByCityId(int cityId){//a
  		Iterator iterator = mArray.iterator();
	  	TimezoneInfo timezoneInfo;
	  	do {
		  	if (iterator.hasNext()) {
			  	timezoneInfo = (TimezoneInfo)iterator.next();
			  	if (timezoneInfo.mId == cityId) {
				  	return timezoneInfo;
			  	}
		  	} else {
		  		break;
		  	}
	  	}while(true);
	  	return null;
  	}

  	public ArrayList search(String paramString) {//a
	    ArrayList arrayList = new ArrayList();
	    String str = paramString.toLowerCase();
	    Iterator localIterator = mArray.iterator();
	    while (localIterator.hasNext())
	    {
	    	TimezoneInfo localas = (TimezoneInfo)localIterator.next();
	    	if ((localas.mNameEN.toLowerCase().contains(str)) 
	    			|| (localas.mNameCN.toLowerCase().contains(str)) 
	    			|| (localas.mNameTW.toLowerCase().contains(str)) 
	    			|| (localas.mPinYin.toLowerCase().contains(str))) {
	    		arrayList.add(localas);
	    	}
	    }
	    return arrayList;
  	}
  	
  	public int getCityIdByName(String name) {
	    String str = name.toLowerCase();
	    Iterator localIterator = mArray.iterator();
	    while (localIterator.hasNext())
	    {
	    	TimezoneInfo localas = (TimezoneInfo)localIterator.next();
	    	if ((localas.mNameEN.toLowerCase().contains(str)) 
	    			|| (localas.mNameCN.toLowerCase().contains(str)) 
	    			|| (localas.mNameTW.toLowerCase().contains(str)) 
	    			|| (localas.mPinYin.toLowerCase().contains(str))) {
	    		return localas.mId;
	    	}
	    }
	    return -1;
  	}
}