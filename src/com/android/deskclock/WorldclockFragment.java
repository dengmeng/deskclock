package com.android.deskclock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class WorldclockFragment extends Fragment 
	implements DeskClockInterface {

	private static final String MTKWORLDCLOCKCHOOSE = "mtkworldclockchoose";
	private ClockCityInfo mCityInfo = new ClockCityInfo();
	private int mAppwidgetId;
	private static final String ON_CLICK_APPWIDGETID = "onClickAppWidgetId";
	private ArrayList<String> mTimeZoneArray = new ArrayList<String>();
	private static final int TIMEZONE_ID = 0;
	private static final int WEATHER_ID = 1;
	private static final String TAG = "MTKWORLDCHOOSE";
	private static final String DELETE_INTENT = "android.intent.action.mtk.worldclock.deleteIntent";
	private ArrayList<String> mCityNameArrayBak = new ArrayList<String>();

	private ArrayList<String> mWeatherIDArray = new ArrayList<String>();

	private ArrayList<String> mAdapterCityArray = new ArrayList<String>();

	private ArrayList<String> mAdapterLocalCityArray = new ArrayList<String>();
	private int mCityNumberInXml;
	private static final int MENU_ADD = Menu.FIRST;
	private static final int MENU_UPDATE = Menu.FIRST + 1;
	private static final int CHOOSEACTIVITYREQUESTCODE = 10;
	private static final int AUTOCOMPLETECHOOSEACTIVITYRESULTCODE = 10;
	private ListView lv;
	private TextView mNoCity;
	private TextView mAddTimeZone;
	private SimpleAdapter mSimpleAdapter;
	private ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	private Activity mActivity;//dengmeng
    private View mRootView;

    
    
	@Override
	public void handler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		OutputLog.worldclock("WorldclockFragment:onCreateView");
		mActivity = getActivity();
		Bundle bundle = mActivity.getIntent().getExtras();
		if (bundle != null) {
			// Xlog.d(TAG, "bundle != null");
			mAppwidgetId = bundle.getInt(ON_CLICK_APPWIDGETID);

		}
		mRootView = inflater.inflate(R.layout.choose, container, false);
		mAddTimeZone = (TextView)mRootView.findViewById(R.id.add_timezone);
		mAddTimeZone.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	addNewTimeZone();
            }
        });
		lv = (ListView) mRootView.findViewById(R.id.mtkchooselistviewid);
		mNoCity = (TextView) mRootView.findViewById(R.id.no_city);
		
		return mRootView;
	}
	
	private void addNewTimeZone() {
        Intent intentAutoCompleteTextView = new Intent(mActivity, AutoCompleteTextViewActivity.class);
        startActivityForResult(intentAutoCompleteTextView, CHOOSEACTIVITYREQUESTCODE);
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		OutputLog.worldclock("WorldclockFragment:requestCode="+requestCode);
		OutputLog.worldclock("WorldclockFragment:resultCode="+resultCode);
		if (requestCode == CHOOSEACTIVITYREQUESTCODE
				&& resultCode == AUTOCOMPLETECHOOSEACTIVITYRESULTCODE) 


				{
			String citynamefromautoweatherids = data.getExtras().getString(
					"citynamefromautoweatherid");		
			getData(citynamefromautoweatherids);
		}
	}

	private void getData(String citynamefromautoweatherids){
		OutputLog.worldclock("WorldclockFragment:getData");
		if (data != null || data.size() > 0) {
			data.clear();
		}
		if(null!=citynamefromautoweatherids){
			SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
					"chooseshared", Context.MODE_PRIVATE);
			String chooses = sharedPreferences.getString(MTKWORLDCLOCKCHOOSE,
					"first");
			// delete same city
			String sac[] = chooses.split(",");
			for (int i = 0; i < sac.length; i++) {
				if (citynamefromautoweatherids.equals(sac[i])) {
					// also exsit ,don't add it
					citynamefromautoweatherids = "";
				}

			}
			if (citynamefromautoweatherids.equals("")) {

			} else {
				Editor editor = sharedPreferences.edit();
				editor.putString(MTKWORLDCLOCKCHOOSE, chooses +"," + citynamefromautoweatherids);
				editor.commit();
			}	
			chooses = sharedPreferences.getString(MTKWORLDCLOCKCHOOSE, "first");

			if (chooses != null && chooses.length() > 5) {
				String[] datass = chooses.split(",");
			
				for (int i = 1; i < datass.length; i++) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("chooselistviewimageview",
							R.drawable.mtkchooseimage);
					String cityNameFromWeatherid = getCityNameBYWeatherId(datass[i]);
					hashMap.put("chooselistviewtextview", cityNameFromWeatherid);
					hashMap.put("chooselistviewweatherid", datass[i]);
					data.add(hashMap);
				}

			}
		}
	}

	private  String  getCityNameBYWeatherId(String weatherId) {
		// TODO Auto-generated method stub
		OutputLog.worldclock("WorldclockFragment:getCityNameBYWeatherId");
		String cityName =null;
		for(int i=0;i<mWeatherIDArray.size();i++){
			if(weatherId.equals(mWeatherIDArray.get(i))){
				cityName = mCityNameArrayBak.get(i);
				break;
			}
		}
		return cityName;
	}

	public void getZones() {
		OutputLog.worldclock("WorldclockFragment:getZones");
		XmlResourceParser xrp = null;
		String localCity = getLocalGMTString();
		try {
			xrp = getResources().getXml(R.xml.timezones);
			while (xrp.next() != XmlResourceParser.START_TAG) {
				continue;
			}
			xrp.next();
			int readCount = 0;
			String tempCitys[] = new String[ClockCityUtils.MAX_CITY_SIZE];
			String tempZones[] = new String[ClockCityUtils.MAX_CITY_SIZE];
			String tempWeahterID[] = new String[ClockCityUtils.MAX_CITY_SIZE];
			while (xrp.getEventType() != XmlResourceParser.END_TAG) {
				while (xrp.getEventType() != XmlResourceParser.START_TAG) {
					if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
						return;
					}
					xrp.next();
				}
				if (xrp.getName().equals(ClockCityUtils.XML_TAG_TIME_ZONE)) {
					String id = xrp.getAttributeValue(TIMEZONE_ID);
					String weatherID = xrp.getAttributeValue(WEATHER_ID);
					String displayName = xrp.nextText();
					if (readCount < ClockCityUtils.MAX_CITY_SIZE) {
						mCityNameArrayBak.add(displayName);
						mAdapterCityArray.add(displayName);
						mTimeZoneArray.add(id);
						if (id.equals(localCity)) {
							mAdapterLocalCityArray.add(displayName);
						}
						mWeatherIDArray.add(weatherID);
						readCount++;
					}
				}

				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					xrp.next();
				}
				xrp.next();
			}
			mCityNumberInXml = readCount;
			xrp.close();
		} catch (XmlPullParserException xppe) {
			Log.i("aaa", "Ill-formatted timezones.xml file");
		} catch (java.io.IOException ioe) {
			Log.i("bbb", "Unable to read timezones.xml file");
		} finally {
			if (null != xrp) {
				xrp.close();
			}
		}
	}

	private String getLocalGMTString() {
		TimeZone tz = TimeZone.getTimeZone(TimeZone.getDefault().getID());
		int offset = tz.getOffset(Calendar.getInstance().getTimeInMillis());
		int p = Math.abs(offset);
		StringBuilder name = new StringBuilder();
		name.append("GMT");

		if (offset < 0) {
			name.append('-');
		} else {
			name.append('+');
		}
		int hour = p / (3600000);
		if (hour < 10) {
			name.append('0');
			name.append(hour);
		} else {
			name.append(hour);
		}
		name.append(':');

		int min = p / 60000;
		min %= 60;

		if (min < 10) {
			name.append('0');
		}
		name.append(min);
		return name.toString();
	}

	@Override
	public void onStart() {
		super.onStart();
		if (mTimeZoneArray.isEmpty()) 
		{
			Log.i(TAG, "mTimeZoneArray.isEmpty()");
			getZones();
		}

	}
	
	
	
	
	@Override
	public void onResume() {
		super.onResume();

		getData("");
		// lv.setDividerHeight(20);
		mSimpleAdapter = new SimpleAdapter(mActivity, data, R.layout.chooselistview,
				new String[] { "chooselistviewimageview",
						"chooselistviewtextview", "chooselistviewweatherid" },
				new int[] { R.id.chooseactivityimageviewid,
						R.id.chooseactivitytextviewid,
						R.id.chooselistviewweatherid });
		if (mSimpleAdapter.getCount() != 0) {
			mNoCity.setVisibility(View.GONE);
		} else {
			mNoCity.setVisibility(View.VISIBLE);
		}
		lv.setAdapter(mSimpleAdapter);
		/*
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				LinearLayout ll = (LinearLayout) arg1;
				String cityName = null;
				String weatherID = null;
				LinearLayout lll = (LinearLayout) ll.getChildAt(1);

				TextView tv = (TextView) lll.getChildAt(2);
				weatherID = String.valueOf(tv.getText().toString());
				mCityInfo = setCityInfoByWeatherID(weatherID);
				String timezone = null;
				if (mCityInfo.getTimeZone() == null) {
					// no right city 
					Toast.makeText(mActivity,
							R.string.select_right_city, Toast.LENGTH_SHORT)
							.show();
					return;
				} else {
				}
				final Context context = mActivity;
				AppWidgetManager appWidgetManager = AppWidgetManager
						.getInstance(context);
				ClockCityUtils.initPreference(context);
				ClockCityUtils.savePreferences(appWidgetManager, mAppwidgetId,
						mCityInfo);
				WorldClockWidgetProvider.updateCity(context, mAppwidgetId,
						mCityInfo);
				finish();
			}
		});
		*/
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				// delete item
				LinearLayout ll = (LinearLayout) arg1;
				LinearLayout lll = (LinearLayout) ll.getChildAt(1);

				TextView tv = (TextView) lll.getChildAt(1);
				final String cityName = String.valueOf(tv.getText().toString());
				TextView tvweather = (TextView) lll.getChildAt(2);
				final String weatherName = String.valueOf(tvweather.getText()
						.toString());

				new AlertDialog.Builder(mActivity).setTitle(
						R.string.delete_city).setMessage(
						R.string.delete_city_info).setPositiveButton(R.string.deleteok,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

								// delete item
								SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
										"chooseshared", Context.MODE_PRIVATE);
								String chooses = sharedPreferences.getString(
										MTKWORLDCLOCKCHOOSE, "first");
								chooses = chooses.replace( "," + weatherName, "");
								Log.i("nihao", chooses);

								sharedPreferences.edit().putString(
										MTKWORLDCLOCKCHOOSE, chooses).commit();
							
								if(weatherName!=null&&(!"".equals(weatherName.trim()))){
									Intent intent = new Intent(DELETE_INTENT);
									intent.putExtra("weatherNameDelete", weatherName);
									mActivity.sendBroadcast(intent);
								}
								onResume();
							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();
				return true;
			}
		});
	}

	private ClockCityInfo setCityInfoByWeatherID(String weatherID) {
		ClockCityInfo cityInfo = new ClockCityInfo();
		cityInfo.setWeatherID(weatherID);
		for (int i = 0; i < mWeatherIDArray.size(); i++) {	
			if (mWeatherIDArray.get(i).equals(weatherID)) {
				String timezone = mTimeZoneArray.get(i);
				String cityName = mCityNameArrayBak.get(i);
				cityInfo.setTimeZone(timezone);
				cityInfo.setIndex(String.valueOf(i));
				cityInfo.setCityName(cityName);
				break;
			}
		}
		return cityInfo;
	}	
	
}
