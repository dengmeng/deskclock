package com.android.deskclock;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

/**
 * AlarmClock application.
 */
public class AlarmClock extends Fragment
	implements AdapterView.OnItemClickListener, DeskClockInterface {
//public class AlarmClock extends Activity implements OnItemClickListener {

    static final String PREFERENCES = "AlarmClock";
    static final String NEAREST_ALARM_PREFERENCES = "NearestAlarm";

    /** This must be false for production.  If true, turns on logging,
        test code, etc. */
    static final boolean DEBUG = false;

    private LayoutInflater mFactory;
    private ListView mAlarmsList;
    private Cursor mCursor;
    private LinearLayout mAddAlarm;
    private Activity mActivity;//dengmeng
    private View mRootView;
    private AlarmTimeAdapter mAdapter;

    private void updateAlarm(boolean enabled,
            Alarm alarm) {
        Alarms.enableAlarm(mActivity, alarm.id, enabled);
        if (enabled) {
            SetAlarm.popAlarmSetToast(mActivity, alarm.hour, alarm.minutes,
                    alarm.daysOfWeek);
        }
    }

    private class AlarmTimeAdapter extends CursorAdapter {
        public AlarmTimeAdapter(Context context, Cursor cursor) {
            super(context, cursor);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View ret = mFactory.inflate(R.layout.alarm_time, parent, false);

            DigitalClock digitalClock =
                    (DigitalClock) ret.findViewById(R.id.digitalClock);
            digitalClock.setLive(false);
            return ret;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final Alarm alarm = new Alarm(cursor);

            View indicator = view.findViewById(R.id.indicator);

            // Set the initial state of the clock "checkbox"
            final CheckBox clockOnOff =
                    (CheckBox) indicator.findViewById(R.id.clock_onoff);
            clockOnOff.setChecked(alarm.enabled);

            // Clicking outside the "checkbox" should also change the state.
            indicator.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    clockOnOff.toggle();
                    updateAlarm(clockOnOff.isChecked(), alarm);
                }
            });

            DigitalClock digitalClock =
                    (DigitalClock) view.findViewById(R.id.digitalClock);

            // set the alarm text
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, alarm.hour);
            c.set(Calendar.MINUTE, alarm.minutes);
            digitalClock.updateTime(c);

            // Set the repeat text or leave it blank if it does not repeat.
            TextView daysOfWeekView =
                    (TextView) digitalClock.findViewById(R.id.daysOfWeek);
            final String daysOfWeekStr =
                    alarm.daysOfWeek.toString(mActivity, false);
            if (daysOfWeekStr != null && daysOfWeekStr.length() != 0) {
                daysOfWeekView.setText(daysOfWeekStr);
                daysOfWeekView.setVisibility(View.VISIBLE);
            } else {
                daysOfWeekView.setVisibility(View.GONE);
            }

            // Display the label
            TextView labelView =
                    (TextView) view.findViewById(R.id.label);
            if (alarm.label != null && alarm.label.length() != 0) {
                labelView.setText(alarm.label);
                labelView.setVisibility(View.VISIBLE);
            } else {
                labelView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info =
                (AdapterContextMenuInfo) item.getMenuInfo();
        final int id = (int) info.id;
        // Error check just in case.
        if (id == -1) {
            return super.onContextItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.delete_alarm: {
                // Confirm that the alarm will be deleted.
                new AlertDialog.Builder(mActivity)
                        .setTitle(getString(R.string.delete_alarm))
                        .setMessage(getString(R.string.delete_alarm_confirm))
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d,
                                            int w) {
                                        Alarms.deleteAlarm(mActivity, id);
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            }

            case R.id.enable_alarm: {
                final Cursor c = (Cursor) mAlarmsList.getAdapter()
                        .getItem(info.position);
                final Alarm alarm = new Alarm(c);
                Alarms.enableAlarm(mActivity, alarm.id, !alarm.enabled);
                if (!alarm.enabled) {
                    SetAlarm.popAlarmSetToast(mActivity, alarm.hour, alarm.minutes,
                            alarm.daysOfWeek);
                }
                return true;
            }

            case R.id.edit_alarm: {
                final Cursor c = (Cursor) mAlarmsList.getAdapter()
                        .getItem(info.position);
                final Alarm alarm = new Alarm(c);
                Intent intent = new Intent(mActivity, SetAlarm.class);
                intent.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
                startActivity(intent);
                return true;
            }

            default:
                break;
        }
        return super.onContextItemSelected(item);
    }
    

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	mActivity = getActivity();
    	mFactory = LayoutInflater.from(mActivity);
    	mCursor = Alarms.getAlarmsCursor(mActivity.getContentResolver());
    	

    	mRootView = inflater.inflate(R.layout.alarm_clock, container, false);
    	mAddAlarm = (LinearLayout) mRootView.findViewById(R.id.new_alarm);
    	mAddAlarm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	addNewAlarm();
            }
        });
    	mAlarmsList = (ListView) mRootView.findViewById(R.id.alarms_list);
    	mAdapter = new AlarmTimeAdapter(mActivity, mCursor);
    	mAlarmsList.setAdapter(mAdapter);
        mAlarmsList.setVerticalScrollBarEnabled(true);
        mAlarmsList.setOnItemClickListener(this);
        mAlarmsList.setOnCreateContextMenuListener(this);
        mAlarmsList.setEmptyView(mRootView.findViewById(R.id.alarm_empty));
    	
		return mRootView;
	}

    private void showSettingMenu() {
        startActivity(new Intent(mActivity, SettingsActivity.class));
    }

    private void addNewAlarm() {
        startActivity(new Intent(mActivity, SetAlarm.class));
    }

    
    
    @Override
	public void handler() {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
        ToastMaster.cancelToast();
        if (mCursor != null) 
		{
            mCursor.close();
        }
        super.onDestroy();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view,
            ContextMenuInfo menuInfo) {
        // Inflate the menu from xml.
    	mActivity.getMenuInflater().inflate(R.menu.context_menu, menu);

        // Use the current item to create a custom view for the header.
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        final Cursor c =
                (Cursor) mAlarmsList.getAdapter().getItem((int)info.position);
        final Alarm alarm = new Alarm(c);

        // Construct the Calendar to compute the time.
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, alarm.hour);
        cal.set(Calendar.MINUTE, alarm.minutes);
        final String time = Alarms.formatTime(mActivity, cal);

        // Inflate the custom view and set each TextView's text.
        final View v = mFactory.inflate(R.layout.context_menu_header, null);
        TextView textView = (TextView) v.findViewById(R.id.header_time);
        textView.setText(time);
        textView = (TextView) v.findViewById(R.id.header_label);
        textView.setText(alarm.label);

        // Set the custom view on the menu.
        menu.setHeaderView(v);
        // Change the text based on the state of the alarm.
        if (alarm.enabled) {
            menu.findItem(R.id.enable_alarm).setTitle(R.string.disable_alarm);
        }
    }



    @Override
    public void onItemClick(AdapterView parent, View v, int pos, long id) {
        final Cursor c = (Cursor) mAlarmsList.getAdapter().getItem(pos);
        final Alarm alarm = new Alarm(c);
        Intent intent = new Intent(mActivity, SetAlarm.class);
        intent.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
        startActivity(intent);
    }
}