package com.adam.sk.workingtimemanager.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.service.LocationService;
import com.adam.sk.workingtimemanager.service.UpdaterService;

public class BootReciever extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    public static final int UPDATE_SERVICE_ID = 1001;
    public static final int LOCATION_SERVICE_ID = 1002;

    public BootReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive()");

        setAlarmService(context, new Intent(context, UpdaterService.class));

    }

    private void setAlarmService(Context context, Intent actIntent) {

        startUpdateService(context, UPDATE_SERVICE_ID, new Intent(context, UpdaterService.class), UpdaterService.ACTION_ALARM_RECEIVER);
        startUpdateService(context, LOCATION_SERVICE_ID, new Intent(context, LocationService.class), LocationService.ACTION_ALARM_RECEIVER);
    }

    public void startUpdateService(Context context, int serviceID, Intent intent, String actionAlarmReceiver) {
        Toast.makeText(context.getApplicationContext(), "Service start", Toast.LENGTH_SHORT).show();
        long aroundInterval = 5000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent.setAction(actionAlarmReceiver);//my custom string action name
        PendingIntent pendingIntent = PendingIntent.getService(context, serviceID, intent, PendingIntent.FLAG_CANCEL_CURRENT);//used unique ID as 1001
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), aroundInterval, pendingIntent);//first start will start asap
    }
}
