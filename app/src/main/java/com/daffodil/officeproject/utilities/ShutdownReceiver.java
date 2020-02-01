package com.daffodil.officeproject.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ShutdownReceiver extends BroadcastReceiver {
    private SharePreferanceWrapperSingleton objSPS;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("ShutdownReceiver called**************" + getCurrentTimeDate());
        objSPS = SharePreferanceWrapperSingleton.getSingletonInstance();
        objSPS.setPref(context);

        objSPS.setValueToSharedPref("ShutdownTime", getCurrentTimeDate());
    }

    private String getCurrentTimeDate() {

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //SimpleDateFormat formatter = new SimpleDateFormat(" hh:mm:ss aa");

        return formatter.format(calendar.getTime());
    }

}