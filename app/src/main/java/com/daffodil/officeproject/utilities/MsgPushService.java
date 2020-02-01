package com.daffodil.officeproject.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.daffodil.officeproject.R;
import com.daffodil.officeproject.SplashModule.SplashActivity;
import com.daffodil.officeproject.dto.SharedPreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Daffodil on 12/19/2019.
 */

public class MsgPushService extends Service {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_EX = 1;
    private AlarmManagerBroadcastReceiver alarm;
    private SharePreferanceWrapperSingleton objSPS;
    private PendingIntent pendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("BootCompleteReceiver called**************" + getCurrentTimeDate());
        objSPS = SharePreferanceWrapperSingleton.getSingletonInstance();
        objSPS.setPref(MsgPushService.this);
        Intent alarmIntent = new Intent(MsgPushService.this, AlarmManagerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MsgPushService.this, 0, alarmIntent, 0);

        //objSPS.setValueToSharedPref("RestartTime", getCurrentTimeDate());
        SharedPreferenceManager.instance().setValueToSharedPref("RestartTime", getCurrentTimeDate());
        //Toast.makeText(this, "PGPL Connected", Toast.LENGTH_LONG).show();
        createNotification();
        String Str = SharedPreferenceManager.instance().getValueFromShared_Pref("ChronometerText");
        //Toast.makeText(getApplicationContext(), Str, Toast.LENGTH_LONG).show();

        if (SharedPreferenceManager.instance().getValueFromShared_Pref("ChronometerText").equals("Punch In                00:00:00")) {
            //startRepeatingTimer()
            // ;\
            // Toast.makeText(getApplicationContext(), "if", Toast.LENGTH_LONG).show();

        } else {
            //Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_LONG).show();
            start();
        }


        return Service.START_STICKY;
    }

    private String getCurrentTimeDate() {

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //SimpleDateFormat formatter = new SimpleDateFormat(" hh:mm:ss aa");

        return formatter.format(calendar.getTime());
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 5;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void startRepeatingTimer() {
        Context context = this.getApplicationContext();
        alarm = new AlarmManagerBroadcastReceiver();
        if (alarm != null) {
            alarm.SetAlarm(context);
            //btnPunch.setText("Punch Out               00:00:00");
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method  Add App to Notification tray
     */
    public void createNotification() {
        try {

            notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            int icon = R.drawable.ic_logo;
            CharSequence tickerText = "infibiz Connected";
            long when = System.currentTimeMillis();

            Notification notification = new Notification(icon, tickerText, when);
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.priority = Notification.PRIORITY_HIGH;
            Context context = getApplicationContext();
            CharSequence contentTitle = "infibiz";
            CharSequence contentText = "Connected";

            Intent notificationIntent = new Intent(this, SplashActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0, notificationIntent, 0);

           /* notification.setLatestEventInfo(context, contentTitle,
                    contentText, contentIntent);
*/
            notificationManager.notify(NOTIFICATION_EX, notification);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}