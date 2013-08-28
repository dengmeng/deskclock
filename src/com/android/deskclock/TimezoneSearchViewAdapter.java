package com.android.deskclock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

class TimezoneSearchViewAdapter extends BaseAdapter {
	private ArrayList mArrayList;
	private Context mContext;
	private LayoutInflater mInflater;
	TimezoneSearchView mTimezoneSearchView;

	public TimezoneSearchViewAdapter(TimezoneSearchView timezoneSearchView, Context context, boolean flag) {
	  mTimezoneSearchView = timezoneSearchView;
	  mContext = context;
	  mInflater = LayoutInflater.from(mContext);
	  mArrayList = TimezoneDatabase.getDatabase(mContext).search("");
	}

	private String format(String paramString) {
	    int i = TimeZone.getTimeZone(paramString).getOffset(Calendar.getInstance().getTimeInMillis());
	    int j = Math.abs(i);
	    StringBuilder localStringBuilder = new StringBuilder();
	    localStringBuilder.append("GMT");
	    if (i < 0) {
	    	localStringBuilder.append('-');
	    }
	    else {
	    	localStringBuilder.append('+');
	    }

	    localStringBuilder.append(j / 3600000L);
	    localStringBuilder.append(':');
	    long l = j / 60000L % 60L;
	    if (l < 10L)
	    	localStringBuilder.append('0');
	    localStringBuilder.append(l);
	    return localStringBuilder.toString();
	}

	public void search(String str) {
		mArrayList = TimezoneDatabase.getDatabase(mContext).search(str);
	}

  	public int getCount() {
  		return mArrayList.size();
  	}

  	public Object getItem(int index) {
  		return mArrayList.get(index);
  	}

  	public long getItemId(int index) {
  		return 0L;
  	}

  	@Override
  	public View getView(int paramInt, View view, ViewGroup paramViewGroup) {
  		if (view == null)
  			view = mInflater.inflate(R.layout.timezone_searchview_item, paramViewGroup, false);
  		TimezoneInfo Info = (TimezoneInfo)getItem(paramInt);
  		((TextView)view.findViewById(R.id.city_name)).setText(Info.getCityName(false));
  		((TextView)view.findViewById(R.id.city_timezone)).setText(format(Info.mTimezone));
  		return view;
  	}
}