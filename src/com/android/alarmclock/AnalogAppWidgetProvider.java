package com.android.alarmclock;

import com.android.deskclock.AlarmClock;
import com.android.deskclock.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Simple widget to show analog clock.
 */
public class AnalogAppWidgetProvider extends BroadcastReceiver {
    static final String TAG = "AnalogAppWidgetProvider";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.analog_appwidget);

            views.setOnClickPendingIntent(R.id.analog_appwidget,
                    PendingIntent.getActivity(context, 0,
                        new Intent(context, AlarmClock.class), 0));

            int[] appWidgetIds = intent.getIntArrayExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS);

            AppWidgetManager gm = AppWidgetManager.getInstance(context);
            gm.updateAppWidget(appWidgetIds, views);
        }
    }
}

