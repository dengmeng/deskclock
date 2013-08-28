package com.android.deskclock;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
//import miui.os.Build;

class AlarmSQLite extends SQLiteOpenHelper {
  private Context mContext;
  private static final int VERSION = 9;

  public AlarmSQLite(Context context)
  {
    super(context, "alarms.db", null, VERSION);
    mContext = context;
  }

  private void a(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS worldclocks;");
    paramSQLiteDatabase.execSQL("CREATE TABLE worldclocks (_id INTEGER PRIMARY KEY,cityid INTEGER);");
    b(paramSQLiteDatabase);
  }

  private void b(SQLiteDatabase paramSQLiteDatabase)
  {
    //if (Build.IS_INTERNATIONAL_BUILD)
      //return;
    paramSQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid) VALUES (0);");
    paramSQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid) VALUES (30);");
    paramSQLiteDatabase.execSQL("INSERT INTO worldclocks (cityid) VALUES (15);");
  }

  Uri AlarmInsert(ContentValues values) {
	  long result = getWritableDatabase().insert("alarms", "message", values);
	  if (result < 0L) {
		  throw new SQLException("Failed to insert row");
	  }
	  return ContentUris.withAppendedId(AlarmUri.CONTENT_URI, result);
  }

  
  Uri commonInsert(ContentValues values) {
      SQLiteDatabase db = getWritableDatabase();
      long rowId = db.insert("alarms", Alarm.Columns.MESSAGE, values);
      if (rowId < 0) {
          throw new SQLException("Failed to insert row");
      }
      if (Log.LOGV) Log.v("Added alarm rowId = " + rowId);

      return ContentUris.withAppendedId(Alarm.Columns.CONTENT_URI, rowId);
  }
  
  @Override
  public void onCreate(SQLiteDatabase db) {
	  db.execSQL("CREATE TABLE worldclocks (_id INTEGER PRIMARY KEY,cityid INTEGER);");
	  db.execSQL("CREATE TABLE stopwatchs (_id INTEGER PRIMARY KEY,total_elapsed LONG,lap_elapsed LONG);");
	  //db.execSQL("CREATE TABLE alarms (_id INTEGER PRIMARY KEY,hour INTEGER, minutes INTEGER, daysofweek INTEGER, alarmtime INTEGER, enabled INTEGER, vibrate INTEGER, message TEXT, alert TEXT, type INTEGER DEFAULT 0);");
	  db.execSQL("CREATE TABLE alarms (_id INTEGER PRIMARY KEY,hour INTEGER, minutes INTEGER, daysofweek INTEGER, alarmtime INTEGER, enabled INTEGER, vibrate INTEGER, message TEXT, alert TEXT);");
	  // insert default alarms
      String insertMe = "INSERT INTO alarms " +
              "(hour, minutes, daysofweek, alarmtime, enabled, vibrate, " +
              " message, alert) VALUES ";
      db.execSQL(insertMe + "(8, 30, 31, 0, 0, 1, '', '');");
      db.execSQL(insertMe + "(9, 00, 96, 0, 0, 1, '', '');");
	  b(db);
  }

  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < 5)
    {
    	db.execSQL("DROP TABLE IF EXISTS alarms");
    	onCreate(db);
      return;
    }
    if (oldVersion == 5)
    {
    	db.execSQL("INSERT INTO alarms (hour, minutes, daysofweek, alarmtime, enabled, vibrate,  message, alert) VALUES (7, 00, 31, 0, 0, 1, '" + mContext.getString(2131427442) + "', '');");
    	db.execSQL("INSERT INTO alarms (hour, minutes, daysofweek, alarmtime, enabled, vibrate,  message, alert) VALUES (8, 00, 31, 0, 0, 1, '" + mContext.getString(2131427442) + "', '');");
    }
    for (int i = oldVersion + 1; ; i = oldVersion)
    {
      if (i == 6)
        ++i;
      if (i == 7)
      {
    	  db.execSQL("ALTER TABLE alarms ADD COLUMN type INTEGER DEFAULT 0;");
    	  db.execSQL("CREATE TABLE worldclocks (_id INTEGER PRIMARY KEY,cityid INTEGER);");
    	  db.execSQL("CREATE TABLE stopwatchs (_id INTEGER PRIMARY KEY,total_elapsed LONG,lap_elapsed LONG);");
        ++i;
      }
      if (i == 8)
      {
        a(db);
        ++i;
      }
      if (i != newVersion);
      throw new IllegalStateException("Upgrade alarm database to version " + newVersion + "fails");
    }
  }

}