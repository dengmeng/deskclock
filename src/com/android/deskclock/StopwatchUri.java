package com.android.deskclock;

import android.net.Uri;

public class StopwatchUri
{
  public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/stopwatch");
  public static final String[] PROJECTION = { "_id", "total_elapsed", "lap_elapsed" };
}