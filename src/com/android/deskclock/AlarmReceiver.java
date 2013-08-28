/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.deskclock;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.PowerManager.WakeLock;
import android.sax.StartElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.os.ServiceManager;
import com.android.internal.telephony.ITelephony;
import android.os.SystemProperties;
import android.preference.PreferenceManager;


/**
 * Glue class: connects AlarmAlert IntentReceiver to AlarmAlert
 * activity.  Passes through Alarm ID.
 */
public class AlarmReceiver extends BroadcastReceiver {

    /** If the alarm is older than STALE_WINDOW, ignore.  It
        is probably the result of a time or timezone change */
    private final static int STALE_WINDOW = 30 * 60 * 1000;
    private ITelephony mTelephonyService;
    private static final int GIMINI_SIM_1 = 0;
	private static final int GIMINI_SIM_2 = 1;
	private boolean mIsOp01;
	private static final String ALARM_PHONE_LISTENER = "com.android.deskclock.ALARM_PHONE_LISTENER";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final PendingResult result = goAsync();
        final WakeLock wl = AlarmAlertWakeLock.createPartialWakeLock(context);
        wl.acquire();
        AsyncHandler.post(new Runnable() {
            @Override public void run() {
                handleIntent(context, intent);
                result.finish();
                wl.release();
            }
        });
    }

    private void handleIntent(Context context, Intent intent) {
    	
    	OutputLog.alarm("AlarmReceiver:handleIntent");
    	
        if (Alarms.ALARM_KILLED.equals(intent.getAction())) {
            // The alarm has been killed, update the notification
            updateNotification(context, (Alarm)
                    intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA),
                    intent.getIntExtra(Alarms.ALARM_KILLED_TIMEOUT, -1));
            return;
        } else if (Alarms.CANCEL_SNOOZE.equals(intent.getAction())) {
            Alarm alarm = null;
            if (intent.hasExtra(Alarms.ALARM_INTENT_EXTRA)) {
                // Get the alarm out of the Intent
                alarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
            }

            if (alarm != null) {
                Alarms.disableSnoozeAlert(context, alarm.id);
                Alarms.setNextAlert(context);
            } else {
                // Don't know what snoozed alarm to cancel, so cancel them all.  This
                // shouldn't happen
                Log.wtf("Unable to parse Alarm from intent.");
                Alarms.saveSnoozeAlert(context, Alarms.INVALID_ALARM_ID, -1);
            }
            return;
        } else if (TimerFragment.ACTION_START_TIMER.equals(intent.getAction())) {
        	OutputLog.alarm("AlarmReceiver:handleIntent:ACTION_START_TIMER");
        	long endTime = intent.getLongExtra("extra_endtime", 0);
        	startTimer(context, endTime);
        	return;
        } else if (TimerFragment.ACTION_STOP_TIMER.equals(intent.getAction())) {
        	OutputLog.alarm("AlarmReceiver:handleIntent:ACTION_STOP_TIMER");
        	stopTimer(context);
        	return;
        } else if (TimerFragment.ACTION_TIMER_TIMEOUT.equals(intent.getAction())) {
        	OutputLog.alarm("AlarmReceiver:handleIntent:ACTION_TIMER_TIMEOUT");
        	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
            String ringToneStr = pref.getString("Ringtone", null);
            Uri uri = null;
            Alarm timerAlarm = null;
            
            if (ringToneStr != null) {
      		  	if (ringToneStr.equals("")) {
      		  		uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
      		  	} else {
      		  		uri = Uri.parse(ringToneStr);
      		  	}
      	  	} else {
      	  		uri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI;
      	  	}
            
            
            
            long during = pref.getLong("duration", 0L);
            int hour = (int)(during / 1000L / 60L / 60L);
            int min = (int)(during / 1000L / 60L % 60L);
            timerAlarm = new Alarm();
            timerAlarm.id = -2;
            timerAlarm.enabled = true;
            timerAlarm.hour = hour;
            timerAlarm.minutes = min;
            timerAlarm.vibrate = false;
            timerAlarm.alert = uri;
            timerAlarm.silent = false;
            
            Intent intentService = new Intent(Alarms.ALARM_ALERT_ACTION);
            intentService.putExtra(Alarms.ALARM_INTENT_EXTRA, timerAlarm);
            context.startService(intentService);
            Intent intentAlert = new Intent(context, AlarmAlertFullScreen.class);
            intentAlert.putExtra(Alarms.ALARM_INTENT_EXTRA, timerAlarm);
			boolean Kmlocked = ((KeyguardManager)context.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
            intentAlert.putExtra("intent.extra.alarm.show_when_locked", Kmlocked);
            intentAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION);//268697600
            //intentAlert.setFlags(268697600);//268697600
            context.startActivity(intentAlert);
            return;
        } else if (!Alarms.ALARM_ALERT_ACTION.equals(intent.getAction())) {
            // Unknown intent, bail.
            return;
        }

        Alarm alarm = null;
        // Grab the alarm from the intent. Since the remote AlarmManagerService
        // fills in the Intent to add some extra data, it must unparcel the
        // Alarm object. It throws a ClassNotFoundException when unparcelling.
        // To avoid this, do the marshalling ourselves.
		if (intent.getBooleanExtra("setNextAlert", true)) {
			final byte[] data = intent.getByteArrayExtra(Alarms.ALARM_RAW_DATA);
			if (data != null) {
				Parcel in = Parcel.obtain();
				in.unmarshall(data, 0, data.length);
				in.setDataPosition(0);
				alarm = Alarm.CREATOR.createFromParcel(in);
			}

			if (alarm == null) {
				Log.wtf("Failed to parse the alarm from the intent");
				// Make sure we set the next alert if needed.
				Alarms.setNextAlert(context);
				return;
			}

			// Disable the snooze alert if this alarm is the snooze.
			Alarms.disableSnoozeAlert(context, alarm.id);
			// Disable this alarm if it does not repeat.
			if (!alarm.daysOfWeek.isRepeatSet()) {
				Alarms.enableAlarm(context, alarm.id, false);
			} else {
				// Enable the next alert if there is one. The above call to
				// enableAlarm will call setNextAlert so avoid calling it twice.
				Alarms.setNextAlert(context);
			}
		} else {
			if (intent.hasExtra(Alarms.ALARM_INTENT_EXTRA)) {
				// Get the alarm out of the Intent
				alarm = intent.getParcelableExtra(Alarms.ALARM_INTENT_EXTRA);
			}
		}
		final String optr = SystemProperties.get("ro.operator.optr");
        mIsOp01 = (optr != null && optr.equals("OP01"));
		if (mIsOp01) {
			try {
				mTelephonyService = ITelephony.Stub.asInterface(ServiceManager
						.getService(Context.TELEPHONY_SERVICE));
				int mCurrentCallState = mTelephonyService.getPreciseCallState();
				if (mCurrentCallState != TelephonyManager.CALL_STATE_IDLE) {
					Log.v("mCurrentCallState != TelephonyManager.CALL_STATE_IDLE and mCurrentCallState = "
									+ mCurrentCallState);
					Intent phoneListener = new Intent(ALARM_PHONE_LISTENER);
					phoneListener.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
					context.startService(phoneListener);
					return;
				}
			} catch (RemoteException ex) {
				Log.v("Catch exception when getPreciseCallState: ex = "
						+ ex.getMessage());
			}
		}
        
        // Intentionally verbose: always log the alarm time to provide useful
        // information in bug reports.
        long now = System.currentTimeMillis();
        Log.v("Recevied alarm set for " + Log.formatTime(alarm.time));

        // Always verbose to track down time change problems.
        if (now > alarm.time + STALE_WINDOW) {
            Log.v("Ignoring stale alarm");
            return;
        }
        OutputLog.alarm("AlarmReceiver:handleIntent:111");
        // Maintain a cpu wake lock until the AlarmAlert and AlarmKlaxon can
        // pick it up.
        AlarmAlertWakeLock.acquireCpuWakeLock(context);

        /* Close dialogs and window shade */
        Intent closeDialogs = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeDialogs);

        // Decide which activity to start based on the state of the keyguard.
        Class c = AlarmAlert.class;
        KeyguardManager km = (KeyguardManager) context.getSystemService(
                Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            // Use the full screen activity for security.
            c = AlarmAlertFullScreen.class;
        }

        // Play the alarm alert and vibrate the device.
        Intent playAlarm = new Intent(Alarms.ALARM_ALERT_ACTION);
        playAlarm.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
        context.startService(playAlarm);

        // Trigger a notification that, when clicked, will show the alarm alert
        // dialog. No need to check for fullscreen since this will always be
        // launched from a user action.
        Intent notify = new Intent(context, c);//AlarmAlert.class);
        notify.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
        PendingIntent pendingNotify = PendingIntent.getActivity(context,
                alarm.id, notify, 0);

        // Use the alarm's label or the default label as the ticker text and
        // main text of the notification.
        String label = alarm.getLabelOrDefault(context);
        Notification n = new Notification(R.drawable.stat_notify_alarm,
                label, alarm.time);
        n.setLatestEventInfo(context, label,
                context.getString(R.string.alarm_notify_text),
                pendingNotify);
        n.flags |= Notification.FLAG_SHOW_LIGHTS
                | Notification.FLAG_ONGOING_EVENT;
        n.defaults |= Notification.DEFAULT_LIGHTS;

        // NEW: Embed the full-screen UI here. The notification manager will
        // take care of displaying it if it's OK to do so.
        Intent alarmAlert = new Intent(context, c);
        alarmAlert.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
        alarmAlert.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        // Must set the flag to FLAG_UPDATE_CURRENT to update the extra information, especially for the label.
        n.fullScreenIntent = PendingIntent.getActivity(context, alarm.id, alarmAlert, PendingIntent.FLAG_UPDATE_CURRENT);

        // Send the notification using the alarm id to easily identify the
        // correct notification.
        NotificationManager nm = getNotificationManager(context);
        nm.cancel(alarm.id);
        nm.notify(alarm.id, n);
    }
    
    private void startTimer(Context context, long time) {
    	if ((time == 0L) || (time <= System.currentTimeMillis()))
    		return;
    	Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.setAction(TimerFragment.ACTION_TIMER_TIMEOUT);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    	((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).set(0, time, pendingIntent);
    }
    
    private void stopTimer(Context context) {
    	Intent intent = new Intent(context, AlarmReceiver.class);
    	intent.setAction(TimerFragment.ACTION_TIMER_TIMEOUT);
    	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    	((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).cancel(pendingIntent);
    }

    private NotificationManager getNotificationManager(Context context) {
        return (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void updateNotification(Context context, Alarm alarm, int timeout) {
        NotificationManager nm = getNotificationManager(context);

        // If the alarm is null, just cancel the notification.
        if (alarm == null) {
            if (Log.LOGV) {
                Log.v("Cannot update notification for killer callback");
            }
            return;
        }

        // Launch SetAlarm when clicked.
        Intent viewAlarm = new Intent(context, SetAlarm.class);
        viewAlarm.putExtra(Alarms.ALARM_INTENT_EXTRA, alarm);
        PendingIntent intent =
                PendingIntent.getActivity(context, alarm.id, viewAlarm, 0);

        // Update the notification to indicate that the alert has been
        // silenced.
        String label = alarm.getLabelOrDefault(context);
        Notification n = new Notification(R.drawable.stat_notify_alarm,
                label, alarm.time);
        n.setLatestEventInfo(context, label,
                context.getString(R.string.alarm_alert_alert_silenced, timeout),
                intent);
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        // We have to cancel the original notification since it is in the
        // ongoing section and we want the "killed" notification to be a plain
        // notification.
        nm.cancel(alarm.id);
        nm.notify(alarm.id, n);
    }
}
