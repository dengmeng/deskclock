package com.android.deskclock;

import android.view.View;
import android.widget.TextView;

final class WorldclockListItem {
  public final WorldclockDigitalClock mDigitalClock;
  public final TimezoneAnalogClock mAnalogClock;
  public final TextView mTimezoneDate;
  public final TextView mTimezoneName;
  public int databaseId;

  public WorldclockListItem(View paramView) {
	  mDigitalClock = ((WorldclockDigitalClock)paramView.findViewById(R.id.timezone_time));
	  mAnalogClock = ((TimezoneAnalogClock)paramView.findViewById(R.id.analogClock));
	  mTimezoneDate = ((TextView)paramView.findViewById(R.id.timezone_date));
      mTimezoneName = ((TextView)paramView.findViewById(R.id.timezone_name));
  }
}