package com.android.deskclock;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import java.util.List;

public class AlarmProvider extends ContentProvider
{
  private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  private AlarmSQLite mSQLite;
  
  private static final int ALARM = 1;
  private static final int ALARM_ID = 2;
  private static final int WORLDCLOCK = 3;
  private static final int WORLDCLOCK_ID = 4;  
  private static final int STOPWATCH = 5;
  private static final int STOPWATCH_ID = 6;

  static
  {
	  OutputLog.alarm("AlarmProvider:static");
	  mUriMatcher.addURI("com.android.deskclock", "alarm", ALARM);
	  mUriMatcher.addURI("com.android.deskclock", "alarm/#", ALARM_ID);
	  mUriMatcher.addURI("com.android.deskclock", "worldclock", WORLDCLOCK);
	  mUriMatcher.addURI("com.android.deskclock", "worldclock/#", WORLDCLOCK_ID);
	  mUriMatcher.addURI("com.android.deskclock", "stopwatch", STOPWATCH);
	  mUriMatcher.addURI("com.android.deskclock", "stopwatch/#", STOPWATCH_ID);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
	  int count;
	  String id;
	  SQLiteDatabase db = mSQLite.getWritableDatabase();
	  OutputLog.alarm("AlarmProvider:delete");
	  switch (mUriMatcher.match(uri)) {
	  	case ALARM:
	  		count = db.delete("alarms", selection, selectionArgs);
	  		break;
		case ALARM_ID:
			id = (String)uri.getPathSegments().get(1);
			count = db.delete("alarms", "_id"  
					+ "="  
					+ id  
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection  
					+ ")" : ""), selectionArgs);
			break;
		case WORLDCLOCK:
			count = db.delete("worldclocks", selection, selectionArgs);
			break;
		case WORLDCLOCK_ID:
			
			id = (String)uri.getPathSegments().get(1);
			OutputLog.worldclock("AlarmProvider:delete:WORLDCLOCK_ID:id = "+id);
			count = db.delete("worldclocks", "_id"  
					+ "="  
					+ id  
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection  
					+ ")" : ""), selectionArgs);
			break;
		case STOPWATCH:
			count = db.delete("stopwatchs", selection, selectionArgs);
			break;
		case STOPWATCH_ID:
			id = (String)uri.getPathSegments().get(1);
			count = db.delete("stopwatchs", "_id"  
					+ "="  
					+ id  
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection  
					+ ")" : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Cannot delete from URL: " + uri);
	  }
	  getContext().getContentResolver().notifyChange(uri, null);
	  return count;
  }

  @Override
  public String getType(Uri uri) {
	  switch (mUriMatcher.match(uri)) {
	  	default:
	  		throw new IllegalArgumentException("Unknown URL");
	  	case ALARM:
	  		return "vnd.android.cursor.dir/alarms";
	  	case ALARM_ID:
	  		return "vnd.android.cursor.item/alarms";
	  	case WORLDCLOCK:
	  		return "vnd.android.cursor.dir/worldclocks";
	  	case WORLDCLOCK_ID:
	  		return "vnd.android.cursor.item/worldclocks";
	  	case STOPWATCH:
	  		return "vnd.android.cursor.dir/stopwatchs";
	  	case STOPWATCH_ID:
	  		return "vnd.android.cursor.item/stopwatchs";
	  }
  }

  
  @Override
  public Uri insert(Uri uri, ContentValues values) {
	  Uri resultUri;
	  long result;
	  OutputLog.alarm("AlarmProvider:insert:match(uri)="+mUriMatcher.match(uri));
	  switch (mUriMatcher.match(uri)) {
	  	case ALARM:
		case ALARM_ID:
			//resultUri = mSQLite.AlarmInsert(values);
			resultUri = mSQLite.commonInsert(values);
		    getContext().getContentResolver().notifyChange(resultUri, null);
			break;
		case WORLDCLOCK:
		case WORLDCLOCK_ID:
			result = mSQLite.getWritableDatabase().insert("worldclocks", null, values);
		    if (result < 0L) {
		    	throw new SQLException("Failed to insert row");
		    }
		    resultUri = ContentUris.withAppendedId(WorldclockUri.CONTENT_URI, result);
		    getContext().getContentResolver().notifyChange(resultUri, null);
			break;
		case STOPWATCH:
		case STOPWATCH_ID:
			result = mSQLite.getWritableDatabase().insert("stopwatchs", null, values);
		    if (result < 0L) {
		        throw new SQLException("Failed to insert row");
		    }
		    resultUri = ContentUris.withAppendedId(StopwatchUri.CONTENT_URI, result);
		    getContext().getContentResolver().notifyChange(resultUri, null);
			break;
		default:
		    throw new UnsupportedOperationException("Cannot insert URL: " + uri);
	  }
	  return resultUri;
  }

  
  @Override
  public boolean onCreate() {
	  mSQLite = new AlarmSQLite(getContext());
	  return true;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
		String[] selectionArgs, String sortOrder) {
	  SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
	  Cursor cursor;
	  switch (mUriMatcher.match(uri)) {
	  	default:
	  		throw new IllegalArgumentException("Unknown URL " + uri);
	  	case ALARM:
	  		qBuilder.setTables("alarms");
	  		break;
	  	case ALARM_ID:
	  		qBuilder.setTables("alarms");
	  		qBuilder.appendWhere("_id=");
	  		qBuilder.appendWhere((CharSequence)uri.getPathSegments().get(1));
	        break;
	  	case WORLDCLOCK:
	  		qBuilder.setTables("worldclocks");
	  		break;
	  	case WORLDCLOCK_ID:
	  		qBuilder.setTables("worldclocks");
	  		qBuilder.appendWhere("_id=");
	  		qBuilder.appendWhere((CharSequence)uri.getPathSegments().get(1));
	        break;
	  	case STOPWATCH:
	  		qBuilder.setTables("stopwatchs");
	  		break;
	  	case STOPWATCH_ID:
	  		qBuilder.setTables("stopwatchs");
	  		qBuilder.appendWhere("_id=");
	  		qBuilder.appendWhere((CharSequence)uri.getPathSegments().get(1));
	        break;
	  }
	  cursor = qBuilder.query(mSQLite.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
	  cursor.setNotificationUri(getContext().getContentResolver(), uri);
	  return cursor;
  }

  
  @Override
  public int update(Uri uri, ContentValues values, String selection,
		String[] selectionArgs) { 
	  int count;
	  long data;
	  SQLiteDatabase db = mSQLite.getWritableDatabase();
	  switch (mUriMatcher.match(uri)) {
		case ALARM_ID:
			data = Long.parseLong((String)uri.getPathSegments().get(1));
			count = db.update("alarms", values, "_id=" + data, null);
			break;
		case WORLDCLOCK_ID:
			data = Long.parseLong((String)uri.getPathSegments().get(1));
			count = db.update("worldclocks", values, "_id=" + data, null);
			break;
		case STOPWATCH_ID:
			data = Long.parseLong((String)uri.getPathSegments().get(1));
			count = db.update("stopwatchs", values, "_id=" + data, null);
			break;
		case ALARM:
		case WORLDCLOCK:
		case STOPWATCH:
		default:
		    throw new UnsupportedOperationException("Cannot update URL: " + uri);
	  }
	  getContext().getContentResolver().notifyChange(uri, null);
	  return count;
  }
}