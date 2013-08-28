package com.android.deskclock;

import java.util.Locale;

public class TimezoneInfo {
	public String mTimezone;
	public String mNameEN;
	public String mNameCN;
	public String mNameTW;
	public String mPinYin;
	public int mId;

	public TimezoneInfo(int paramInt, String String1, String String2, String String3, String String4, String String5) {
		mId = paramInt;
		mTimezone = String1;
		mNameEN = String2;
		mNameCN = String3;
		mNameTW = String4;
		mPinYin = String5;
	}

	public String getCityName(boolean isShort) {
		String str;
		if (Locale.getDefault().equals(Locale.TAIWAN)){
			if ((isShort) && (mNameTW.indexOf(",") >= 0)) {
				str = mNameTW.split(",")[1];
			} else {
				str = mNameTW;
			}
		} else if ((Locale.getDefault().equals(Locale.CHINESE)) || (Locale.getDefault().equals(Locale.CHINA))) {
			if ((isShort) && (mNameCN.indexOf(",") >= 0)) {
				str = mNameCN.split(",")[1];
			} else {
				str = mNameCN;
			}
		} else {
			if ((isShort) && (mNameEN.indexOf(",") >= 0)) {
				str = mNameEN.split(",")[1];
			} else {
				str = mNameEN;
			}
		}
		
		return str;
	}
}