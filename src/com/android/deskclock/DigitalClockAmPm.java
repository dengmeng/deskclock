package com.android.deskclock;

import android.view.View;
import android.widget.TextView;
import java.text.DateFormatSymbols;

class DigitalClockAmPm {
	private TextView mAmPm;
	private String am;
	private String pm;

	DigitalClockAmPm(View view) {
	    mAmPm = ((TextView)view.findViewById(R.id.am_pm));
	    String[] arrayOfString = new DateFormatSymbols().getAmPmStrings();
	    am = arrayOfString[0];
	    pm = arrayOfString[1];
	}

	void setIsMorning(boolean enable){
	    if (enable){
	    	mAmPm.setText(am);
	    } else {
	    	mAmPm.setText(pm);
	    }
	}

	void setShowAmPm(boolean enable) {
	    if (enable) {
	    	mAmPm.setVisibility(View.VISIBLE);
	    } else {
	    	mAmPm.setVisibility(View.GONE);
	    }

	}
}