package com.daffodil.officeproject.utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.daffodil.officeproject.R;
import com.daffodil.officeproject.dto.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Daffodil on 12/19/2019.
 */

public class GpsLocationReceiver extends BroadcastReceiver {
    private SharePreferanceWrapperSingleton objSPS;
    private Context context;
    private String punchOutLocation = "", totalWorkingTime = "0";
    GPSTracker gps;
    private PendingIntent pendingIntent;

    SharedPreferences pref, spf_user_id;
    private JSONObject json, json2;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        objSPS = SharePreferanceWrapperSingleton.getSingletonInstance();
        objSPS.setPref(context);
        spf_user_id = context.getSharedPreferences("userid", Context.MODE_PRIVATE);
        pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 -
        Intent alarmIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
           /* Toast.makeText(context, "in GpsLocationReceiver \nandroid.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show(); */
            String str1 = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (str1 != null && !str1.equals("")) {
                //Toast.makeText(context, "Can get Location :Yes Providers: " + str1,
                // Toast.LENGTH_SHORT).show();

            } else {


                // Toast.makeText(context, "Can get Location : No Providers: " + str1,
                // Toast.LENGTH_SHORT).show();

                String str = SharedPreferenceManager.instance().getValueFromShared_Pref("ChronometerText");
                if (str.equals("Punch In                00:00:00")) {

                    //objSPS.setValueToSharedPref("PunchIn", 0);
                    //MainActivity.this.finish();

                    //alarm.CancelAlarm(this.getApplicationContext());

                } else {
                    totalWorkingTime = getWorkingHours();

                   // new Post_Punch_Out_Details().execute();
                }
            }


        }
    }

    public String getWorkingHours() {
        int oldHrs = 0, oldMins = 0, oldSecs = 0;
        int Hours = 0, Mins = 0, sec = 0;

        long mills = 0;

        try {
           /* String string2 = "2013-02-27 06:06:30 AM";
            String string1 = "2013-02-27 06:06:40 AM";*/

            String currentTime = getCurrentTimeDate();
            String PouseTime = SharedPreferenceManager.instance().getValueFromShared_Pref("PouseTime");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date Date1 = format.parse(currentTime);


            Date Date2 = format.parse(PouseTime);
            //Date Date2 = format.parse(objSPS.getValueFromShared_Pref("PunchInTime"));
            mills = Date1.getTime() - Date2.getTime();


            Log.v("Data1", "" + Date1.getTime());
            Log.v("Data2", "" + Date2.getTime());


            Hours = Math.abs((int) (mills / (1000 * 60 * 60)));
            Mins = Math.abs((int) (mills / (1000 * 60)) % 60);
            sec = Math.abs((int) (mills / (1000)) % 60);
            String ChronometerText = SharedPreferenceManager.instance().getValueFromShared_Pref("ChronometerText");
            String[] arrayText = ChronometerText.split(" ");
            String Time = arrayText[arrayText.length - 1];
            String[] arrayChText = Time.split(":");

            if (!arrayChText[0].equals("00")) {

                oldHrs = Integer.parseInt(arrayChText[0]);
            }
            if (!arrayChText[1].equals("00")) {
                oldMins = Integer.parseInt(arrayChText[1]);
            }
            if (!arrayChText[2].equals("00")) {
                oldSecs = Integer.parseInt(arrayChText[2]);
            }
            Hours = Hours + oldHrs;
            Mins = Mins + oldMins;
            sec = sec + oldSecs;


        } catch (Exception e) {
            e.printStackTrace();
        }


        return Hours + ":" + Mins + ":" + sec;
    }

    private class Post_Punch_Out_Details extends AsyncTask<String, String, String> {
        JSONArray items_list;


        @Override
        protected String doInBackground(String... params) {
            try {
                String punchInLocation = "";

                // String[] text = btnPunch.getText().toString().split(" ");
                //String totalTime = text[text.length - 1];
                String punchInTime = SharedPreferenceManager.instance().getValueFromShared_Pref("PunchInTime");
                String punchOutTime = SharedPreferenceManager.instance().getValueFromShared_Pref("PunchOutTime");
                String PunchInLocation = SharedPreferenceManager.instance().getValueFromShared_Pref("PunchInLocation");
                String attendance_id = SharedPreferenceManager.instance().getValueFromShared_Pref("attendance_id");

                json.put("parent_id", pref.getString("parent_id", ""));
                json.put("user_id", spf_user_id.getString("userid", ""));
                json.put("attendance_id", attendance_id);

                json.put("punchout_time", punchOutTime);

                //json.put("working_hrs", totalTime);
                json.put("working_hrs", totalWorkingTime);
                json.put("reason", "Location access disabled");
                json.put("punchout_location", punchOutLocation);


                JSONArray postjson = new JSONArray();
                postjson.put(json);
                json2.put("params", postjson);
                System.out.println(json2.toString());
            } catch (JSONException e) {

                e.printStackTrace();
            }

            //String url = "http://43.225.53.248:8080/infibizcrm/rest/comp_details/getcompanydetail";
           // String url2 = context.getResources().getString(R.string.url) + "/infibizcrm/rest/LoginWebService/insert_punchout_details";

            //String response = WebServiceConnection.getData(url2, json2.toString());


            //return response.trim();
            return "";
        }

        @Override
        protected void onPreExecute() {

            json = new JSONObject();
            json2 = new JSONObject();
            SharedPreferenceManager.instance().setValueToSharedPref("PunchOutTime", getCurrentTimeDate());
            gps = new GPSTracker(context);
            if (gps.canGetLocation()) {
                punchOutLocation = gps.getLatitude() + "," + gps.getLongitude();
                gps = new GPSTracker(context, "Alarm Manager");
            }
            super.onPreExecute();


        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("PunchOut Response:   " + result);
            // Toast.makeText(getApplicationContext(), "PunchOut Response" + result, LENGTH_LONG).show();


            if (result.equalsIgnoreCase("true")) {
                try {
                    SharedPreferenceManager.instance().setValueToSharedPref("ChronometerText", "Punch In                00:00:00");
                    //alarm.CancelAlarm(getApplicationContext());
                    AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    manager.cancel(pendingIntent);
                    //Toast.makeText(getApplicationContext(), "Alarm Canceled", Toast.LENGTH_SHORT).show();
                    SharedPreferenceManager.instance().setValueToSharedPref("status", "stop");
                    //  Toast.makeText(getApplicationContext(), " Total Time:  " + text[text.length - 1], LENGTH_LONG).show();
                    //btnPunch.setBase(SystemClock.elapsedRealtime());

                    //btnPunch.stop();

                    SharedPreferenceManager.instance().setValueToSharedPref("PunchOutTime", getCurrentTimeDate());
                 /*   btnPunch.setText("Punch In                 " + "00:00:00");


                    StateListDrawable states = new StateListDrawable();
                    states.addState(new int[]{android.R.attr.state_pressed},
                            getResources().getDrawable(R.drawable.punch_btn_select));
                    states.addState(new int[]{android.R.attr.state_checked},
                            getResources().getDrawable(R.drawable.punch_btn));
                    states.addState(new int[]{},
                            getResources().getDrawable(R.drawable.punch_btn));
                    btnPunch.setBackground(states);
                    if (dialogPunchout.isShowing()) {
                        dialogPunchout.dismiss();
                    }*/

                } catch (NullPointerException ne) {
                    ne.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            } else {
                System.out.println("Data Could Not Send");
                // Toast.makeText(getApplicationContext(), "Data could not send", LENGTH_LONG).show();
            }


        }
    }

    private String getCurrentTimeDate() {

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //SimpleDateFormat formatter = new SimpleDateFormat(" hh:mm:ss aa");

        return formatter.format(calendar.getTime());
    }
}
