package com.android.deskclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
//import android.widget.EditableListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class WorldclockFragmentNew extends Fragment
  implements View.OnClickListener, DeskClockInterface {
	private static String[] mDayArray;
	//private EditableListView e;
	private ListView mListView;
	private WorldclockListAdapter mAdapter;
	private Runnable mRunnable;
	private boolean isDestory = false;
	private Activity mActivity;
	private Handler mHandler;
	private View mRootView;
	private LinearLayout mAddTimeZone;

  
	static public boolean isDestory(WorldclockFragmentNew worldclockFragment){
		if (worldclockFragment.isDestory) {
			return true;
		} else {
			return false;
		}
	}
	
	static public Handler getHandler(WorldclockFragmentNew worldclockFragment){
		return worldclockFragment.mHandler;
	}
	
	static public Runnable getRunnable(WorldclockFragmentNew worldclockFragment){
		return worldclockFragment.mRunnable;
	}
	
	static public String[] getDayArray(){
		return WorldclockFragmentNew.mDayArray;
	}
  
  @Override
  public void handler() {
	// TODO Auto-generated method stub
	
  }

  private boolean checkCityExist(Context context, int index) {
	  ContentResolver contentResolver = context.getContentResolver();
	  Uri uri = WorldclockUri.CONTENT_URI;
	  String[] arrayOfString1 = WorldclockUri.PROJECTION;
	  String[] arrayOfString2 = new String[1];
	  arrayOfString2[0] = String.valueOf(index);
	  Cursor cursor = contentResolver.query(uri, arrayOfString1, "cityid=?", arrayOfString2, null);
	  boolean result;
	  if (cursor != null) {
		  result = cursor.moveToFirst();
		  cursor.close();
	  } else {
		  result = false;
	  }
      return result;
  }

  private void insertItem(Context paramContext, int paramInt)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("cityid", Integer.valueOf(paramInt));
    paramContext.getContentResolver().insert(WorldclockUri.CONTENT_URI, localContentValues);
  }

  
  	@Override
  	public void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);

  		if ((requestCode == 100) && (resultCode == -1)) {
  			int index = data.getIntExtra("android.intent.extra.TEXT", -1);
  			if ((index < 0) || (checkCityExist(mActivity, index))) {
  				Toast toast = Toast.makeText(mActivity, R.string.timezone_exist_error_message, 0);
  				WorldclockToast.start(toast);
  				toast.show();
  			} else {
  				insertItem(mActivity, index);
  			}
    }
  }

   
  @Override
  public void onClick(View v) {
	  switch (v.getId()) {
	  default:
		  break;
	  case R.id.add_timezone_new:
		  startActivityForResult(new Intent(mActivity, TimezoneSearchView.class), 100);
		  break;
	  }
  }
  
  class ListOnItemLongClick implements DialogInterface.OnClickListener {
	  private Context mContext;
	  private int mId;
	  ListOnItemLongClick(Context context, int id) {
		  mContext = context;
		  mId = id;
	  }

	  public void onClick(DialogInterface paramDialogInterface, int paramInt) {
		  OutputLog.worldclock("ListOnItemLongClick:cityId="+mId);
		  if (mId < 0)
			  return;
		  
		  ContentResolver contentResolver = mContext.getContentResolver();
		  contentResolver.delete(ContentUris.withAppendedId(WorldclockUri.CONTENT_URI, mId), null, null);
	  }
}

  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
    mActivity = getActivity();
    mRootView = inflater.inflate(R.layout.worldclock, container, false);
    mDayArray = getResources().getStringArray(R.array.days);
    mAddTimeZone = (LinearLayout)mRootView.findViewById(R.id.add_timezone_new);
    mAddTimeZone.setOnClickListener(this);
    mListView = ((ListView)mRootView.findViewById(android.R.id.list));
    mAdapter = new WorldclockListAdapter(this, mActivity, mActivity.managedQuery(WorldclockUri.CONTENT_URI, WorldclockUri.PROJECTION, null, null, null));
    mListView.setAdapter(mAdapter);
    mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			WorldclockListItem item = (WorldclockListItem)view.getTag();
			OutputLog.worldclock("onItemLongClick:position="+position);
			//int cityId = TimezoneDatabase.getDatabase(mActivity).getCityIdByName(item.mTimezoneName.getText().toString());
			OutputLog.worldclock("onItemLongClick:databaseId="+item.databaseId);
			new AlertDialog.Builder(mActivity).setTitle(
					R.string.delete_city).setMessage(
					R.string.delete_city_info).setPositiveButton(R.string.deleteok,
					new ListOnItemLongClick(mActivity, item.databaseId)).setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();
			return true;
		}
    });
    mListView.setEmptyView(mRootView.findViewById(R.id.worldclock_empty));
    setHasOptionsMenu(true);
    isDestory = false;
    mHandler = new Handler();
    mRunnable = new WorldclockRunnable(this);
    mRunnable.run();
    return mRootView;
  }
  
  

  public void onDestroyView()
  {
    isDestory = true;
    super.onDestroyView();
  }



  @Override
  public void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
  }
}