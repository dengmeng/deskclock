package com.android.deskclock;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import java.util.Calendar;
import java.util.TimeZone;

class WorldclockListAdapter extends ResourceCursorAdapter {
	@SuppressWarnings("deprecation")
  	public WorldclockListAdapter(WorldclockFragmentNew paramb, Context paramContext, Cursor paramCursor) {
		super(paramContext, R.layout.timezone_item, paramCursor);
  	}

	public void bindView(View view, Context context, Cursor cursor) {
		WorldclockListItem item = (WorldclockListItem)view.getTag();
		int cityId = cursor.getInt(1);
		OutputLog.worldclock("WorldclockListAdapter:bindView:cityId="+cityId);
		TimezoneInfo info = TimezoneDatabase.getDatabase(context).getInfoByCityId(cityId);
		String TimezoneStr = info.mTimezone;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TimezoneStr));
		item.mDigitalClock.updateTime(calendar);
		item.mAnalogClock.setTimezone(TimezoneStr);
		int datyGap = 1 + (calendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
		if (datyGap >= 0 && datyGap < WorldclockFragmentNew.getDayArray().length) {
			item.mTimezoneDate.setText(WorldclockFragmentNew.getDayArray()[datyGap]);
		}
		item.mTimezoneName.setText(info.getCityName(true));
		item.databaseId = cursor.getInt(0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = super.newView(context, cursor, parent);
		view.setTag(new WorldclockListItem(view));
		return view;
	}
}