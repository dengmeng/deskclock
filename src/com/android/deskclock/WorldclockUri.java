package com.android.deskclock;

import android.net.Uri;

public class WorldclockUri {
  public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/worldclock");
  public static final String[] PROJECTION = { "_id", "cityid" };
}