package com.android.deskclock;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

class StopwatchResCursorAdapter extends ResourceCursorAdapter
{

  @SuppressWarnings("deprecation")
  public StopwatchResCursorAdapter(StopwatchFragment sf, Context context, Cursor cursor) {
    super(context, R.layout.stopwatch_lap_item, cursor);
  }

  
  @Override
  public void bindView(View view, Context context, Cursor cursor) {
	StopwatchLapListItem item = (StopwatchLapListItem)view.getTag();
    long total_elapsed = cursor.getLong(1);
    long lap_elapsed = cursor.getLong(2);
    OutputLog.stopwatch("StopwatchResCursorAdapter:bindView:total_elapsed="+total_elapsed+"  lap_elapsed="+lap_elapsed);
    item.mIndexTime.setText(String.valueOf(cursor.getCount() - cursor.getPosition()));
    item.mElapsedTime.setText(StopwatchFragment.formatElapsedTime(total_elapsed));
    item.mLapElapsedTime.setText(context.getString(R.string.elapsed_time_format, StopwatchFragment.formatElapsedTime(lap_elapsed)));
  }

  
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View view = super.newView(context, cursor, parent);
    view.setTag(new StopwatchLapListItem(view));
    return view;
  }
}