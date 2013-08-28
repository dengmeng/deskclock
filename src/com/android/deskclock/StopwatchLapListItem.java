package com.android.deskclock;

import android.view.View;
import android.widget.TextView;

final class StopwatchLapListItem
{
  public final TextView mIndexTime;
  public final TextView mElapsedTime;
  public final TextView mLapElapsedTime;

  public StopwatchLapListItem(View view)
  {
    this.mIndexTime = ((TextView)view.findViewById(R.id.indexTime));
    this.mElapsedTime = ((TextView)view.findViewById(R.id.elapsedTime));
    this.mLapElapsedTime = ((TextView)view.findViewById(R.id.lapElapsedTime));
  }
}