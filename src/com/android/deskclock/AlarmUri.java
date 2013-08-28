package com.android.deskclock;

import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmUri
  implements BaseColumns {
	public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarm");
	static final String[] ac = { "_id", "hour", "minutes", "daysofweek", "alarmtime", "enabled", "vibrate", "message", "alert", "type" };
}