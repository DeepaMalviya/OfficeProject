package com.daffodil.officeproject.HomeModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daffodil.officeproject.R;
import com.daffodil.officeproject.base.AppConstants;
import com.daffodil.officeproject.base.AppController;
import com.daffodil.officeproject.dto.SharedPreferenceManager;
import com.daffodil.officeproject.dto.TrackUser;
import com.daffodil.officeproject.utilities.AlarmManagerBroadcastReceiver;
import com.daffodil.officeproject.utilities.ConnectionDetector;
import com.daffodil.officeproject.utilities.DBAdapter;
import com.daffodil.officeproject.utilities.GPSTracker;
import com.daffodil.officeproject.utilities.SharePreferanceWrapperSingleton;
import com.daffodil.officeproject.utilities.WebServiceConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission_group.CAMERA;

public class o extends AppCompatActivity
        implements /*NavigationView.OnNavigationItemSelectedListener,*/ View.OnClickListener/*, NavigationView.OnNavigationItemSelectedListener*/ {
    Button btnClientList, btnPunch, btnRevisit, btnAddFollowup, btnAddClient;
    private static final String TAG = "Main2Activity";
    String mobile, user_id, otp, user_role, user_name, company_id, company_name;
    String time, date;
    String pTime, pLocation, cellIdShort = "", cellIdLong = "", lac = "", mnc = "", mcc = "", device_type = "";
    int level = 0;
    String shutdownTime = "", restartTime = "", signalStrenght = "", networkSubType = "";
    SharedPreferences sharedpref;
    String operatorType, profileType;
    boolean isAirplaneMode;
    private PendingIntent pendingIntent;
    SharedPreferences.Editor editor1;
    String[] perms = {"android.permission.FINE_LOCATION", "android.permission.CAMERA"};
    private static final int PERMISSION_REQUEST_CODE = 200;
    private AlarmManagerBroadcastReceiver alarm;
    int permsRequestCode = 200;
    //requestPermissions(perms, permsRequestCode);
    String time_out, time_in;
    TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main2);
        if (!checkPermission()) {

            requestPermission();

        } else {
            Toast.makeText(o.this, "Permission already granted.", Toast.LENGTH_SHORT).show();
            // Snackbar.make(view, "Permission already granted.", Snackbar.LENGTH_LONG).show();

        }
        initView();

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA}, PERMISSION_REQUEST_CODE);

    }

    private void initView() {
        tvUserName = findViewById(R.id.tvUserName);
        btnClientList = findViewById(R.id.btnClientList);
        btnRevisit = findViewById(R.id.btnRevisit);
        btnAddFollowup = findViewById(R.id.btnAddFollowup);
        btnAddClient = findViewById(R.id.btnAddClient);
        btnPunch = findViewById(R.id.btnPunch);

      /*  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_vieww);
        navigationView.setNavigationItemSelectedListener(this);
*/
        objSPS = SharePreferanceWrapperSingleton.getSingletonInstance();
        objSPS.setPref(this);
        //alistCompanyInfo = new ArrayList<Companydto>();
        spf_user_id = getApplicationContext().getSharedPreferences("userid", Context.MODE_PRIVATE);
        pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE); // 0 -
        // mode
        editor1 = pref.edit();

       /* try {
            dbAdapter = new DBAdapter(Main2Activity.this);

            dbAdapter.openDataBase();
            dbAdapter.showDataBaseOfDevice(getApplicationContext());
        } catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();

        } catch (SQLiteException sqe) {
            sqe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }
*/
        alarm = new AlarmManagerBroadcastReceiver();
        gps = new GPSTracker(getApplicationContext());
        pref = getSharedPreferences("MyPref", 0);


        editor1 = pref.edit();
        parentid = pref.getString("parent_id", "");

        gps = new GPSTracker(o.this);
        spf_user_id = getSharedPreferences("userid", MODE_PRIVATE);
        json = new JSONObject();
        json2 = new JSONObject();
        objSPS = SharePreferanceWrapperSingleton.getSingletonInstance();
        objSPS.setPref(this);
        sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
        mobile = sharedpref.getString("mobile", "");
        user_id = sharedpref.getString("user_id", "");
        otp = sharedpref.getString("otp", "");
        user_role = sharedpref.getString("user_role", "");
        user_name = sharedpref.getString("user_name", "");
        company_id = sharedpref.getString("company_id", "");
        company_name = sharedpref.getString("company_name", "");
        getStatusAPi();
        //  btnPunch.setText("Punch Out");

        time_in = sharedpref.getString("time_in", "");
        time_out = sharedpref.getString("time_out", "");
        if (!user_name.equals("")) {
            tvUserName.setText("" + user_name);
        }
        if (!time_in.equals("")) {
            btnPunch.setText("Punch Out");

        }
        if (!time_out.equals("")) {
            btnPunch.setText("Punch In");

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        btnPunch.setOnClickListener(this);
        btnAddClient.setOnClickListener(this);
        btnAddFollowup.setOnClickListener(this);
        btnRevisit.setOnClickListener(this);
        btnClientList.setOnClickListener(this);
        setSupportActionBar(toolbar);

        Intent alarmIntent = new Intent(o.this, AlarmManagerBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(o.this, 0, alarmIntent, 0);

        /*try {
            dbAdapter = new DBAdapter(Main2Activity.this);

            dbAdapter.openDataBase();
            dbAdapter.showDataBaseOfDevice(getApplicationContext());
        }  catch (SQLiteCantOpenDatabaseException e) {
            e.printStackTrace();

        } catch (SQLiteException sqe) {
            sqe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }*/
     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
*/

        String ctime = getCurrentTimeDate();
        pTime = ctime;
        GPSTracker gps = new GPSTracker(o.this);
        String location = "";
        if (gps.canGetLocation()) {
            location = gps.getLatitude() + "," + gps.getLongitude();
        }
        pLocation = location;
        // SharedPreferenceManager.instance().setValueToSharedPref("PunchInTime", ctime);
        // SharedPreferenceManager.instance().setValueToSharedPref("PunchInLocation", location);
        profileType = getProfileType();
        isAirplaneMode = isAirplaneMode();
        operatorType = getOperatorType();
         try {
            signalStrenght = ConnectionDetector.getSignalStrenght(o.this);
        } catch (Exception e) {
            e.printStackTrace();
            //  fillDataInDatabase("");


        }
        try {
            networkSubType = ConnectionDetector.getNetworkSubType(o.this);
        } catch (Exception e) {
            e.printStackTrace();
            // fillDataInDatabase("");

        }

        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batt = getApplicationContext().registerReceiver(null, filter);
            // Default to some unknown/wild value
// registerReceiver method call could return null, so check that!
            if (batt != null) {
                level = batt.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            }


            // Format formatter = new SimpleDateFormat("hh:mm:ss a");
            Format formatter = new SimpleDateFormat("MM-dd-yyyy");
            String date = formatter.format(new Date());
            //msgStr.append(formatter.format(new Date()));
            formatter = new SimpleDateFormat("hh:mm");
            String time = formatter.format(new Date());
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            CellLocation cellLoc = telephonyManager.getCellLocation();
            if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {

                device_type = "GSM";
                String networkOperator = telephonyManager.getNetworkOperator();
                if (!networkOperator.equals("")) {
                    mcc = networkOperator.substring(0, 3);
                    mnc = networkOperator.substring(3);
                }

                CellLocation.requestLocationUpdate();
                if (cellLoc instanceof GsmCellLocation) {
                    GsmCellLocation cellLocation = (GsmCellLocation) cellLoc;
                    cellIdLong = cellLocation.getCid() + "";
                    short cIdShort = 0;
                    if (!cellIdLong.equals("")) {
                        cIdShort = (short) Integer.parseInt(cellIdLong);
                    }
                    cellIdShort = cIdShort + "";

                    lac = cellLocation.getLac() + "";
                    // do work
                   }
            } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                device_type = "CDMA";
                CdmaCellLocation cellLocation = (CdmaCellLocation) cellLoc;

                basestation_id = cellLocation.getBaseStationId() + "";
                basestation_latitude = cellLocation.getBaseStationLatitude() + "";
                //int lat = ((L * 90) / 1296000);
                basestation_longitude = cellLocation.getBaseStationLongitude() + "";
            }


            sendToServer();
              /*  makJsonObject(company_idd,user_idd,user_latitudee,user_longitudee,track_datee,track_timee,battery_persentagee,
                    network_typee,profile_typee,operator_typee,cellid_shortt,cellid_longg,lac,mcc,mnc,device_type,basestation_id,
                    basestation_latitude,basestation_longitude,shutdown_timee,restart_timee,signal_strengthh,network_subtypee);
           // dummyData();
            trackApiCallng();*/


        } catch (Exception e) {
            e.printStackTrace();
            cellIdShort = "";
            cellIdLong = "";
            mcc = "";
            mnc = "";
            lac = "";
            device_type = "GSM";
            basestation_id = "";
            basestation_latitude = "";
            basestation_longitude = "";


        }
    }

    private void getStatusAPi() {
        final ProgressDialog pDialog = new ProgressDialog(o.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progress_bar);
        String urlData = AppConstants.BASEURL + "user/attendence?company_id=" + company_id + "&user_id=" + user_id;
        Log.e(TAG, "loginSevice:urlData=== " + urlData);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlData, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response===========" + response.toString());

                try {
                    String data = String.valueOf(response.get("data"));
                    String message = String.valueOf(response.get("message"));
                    int status = response.getInt("status");

                    if (status == 0) {
                        // Toast.makeText(Main2Activity.this, "" + message, Toast.LENGTH_SHORT).show();
                        JSONObject loginresponce = response.getJSONObject("data");

                        loginresponce.getString("user_id");
                        loginresponce.getString("company_id");
                        loginresponce.getString("time_in");
                        loginresponce.getString("time_out");
                        sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
                        editor1 = sharedpref.edit();
                        editor1.putString("time_in", loginresponce.getString("time_in"));
                        editor1.putString("time_out", loginresponce.getString("time_out"));
                        editor1.apply();
                        editor1.commit();

                        pDialog.dismiss();
                        time_in = sharedpref.getString("time_in", "");
                        time_out = sharedpref.getString("time_out", "");
                        if (time_in.equals("")) {
                            btnPunch.setText("Punch Out");
                            btnRevisit.setEnabled(true);
                            btnAddFollowup.setEnabled(true);
                            btnAddClient.setEnabled(true);
                            btnRevisit.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));
                            btnAddFollowup.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));
                            btnAddClient.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));
                        }
                        if (time_out.equals("")) {
                            btnPunch.setText("Punch In");
                            btnRevisit.setEnabled(false);
                            btnAddFollowup.setEnabled(false);
                            btnAddClient.setEnabled(false);
                            btnRevisit.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));
                            btnAddFollowup.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));
                            btnAddClient.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));

                        }
                        if (!time_in.equals("")) {
                            btnPunch.setText("Punch Out");
                            btnRevisit.setEnabled(true);
                            btnAddFollowup.setEnabled(true);
                            btnAddClient.setEnabled(true);

                            btnRevisit.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));
                            btnAddFollowup.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));
                            btnAddClient.setBackground(getResources().getDrawable(R.drawable.bg_white_rounded));

                        }
                        if (!time_out.equals("")) {
                            btnPunch.setText("Punch In");
                            btnRevisit.setEnabled(false);
                            btnAddFollowup.setEnabled(false);
                            btnAddClient.setEnabled(false);
                            btnRevisit.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));
                            btnAddFollowup.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));
                            btnAddClient.setBackground(getResources().getDrawable(R.drawable.bg_white_ractangle_gray));

                        }
                        /*if (!time_in.equals("")) {
                            btnPunch.setText("Punch Out");
                            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);
                            TvTimeIn.setEnabled(true);
                            TvTimeIn.setClickable(true);
                        }
                        if (!time_out.equals("")) {
                            btnPunch.setText("Time In");
                            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_gray);
                            TvTimeIn.setEnabled(true);
                            TvTimeIn.setClickable(true);
                        }

                        if (time_in.equals("")) {
                            btnPunch.setText("Time In");

                            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);
                            TvTimeIn.setEnabled(true);
                            TvTimeIn.setClickable(true);
                        }
                        if (!time_in.equals("")&&!time_out.equals("")) {
                            Log.e(TAG, "onResponse: --------=-------------" );
                            btnPunch.setText("Time In ");
                            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_gray);
                            TvTimeIn.setEnabled(false);
                            TvTimeIn.setClickable(false);
                        }

                        if (time_in.equals("")) {
                            TvTimeIn.setText("Time In");

                            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);
                            TvTimeIn.setEnabled(true);
                            TvTimeIn.setClickable(true);
                        }*/
                    } else {
                        Toast.makeText(o.this, message, Toast.LENGTH_SHORT).show();
                    }
                    pDialog.dismiss();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(o.this, "Unexpected Error...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(o.this, "Technical Error...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG,
                // "Error: " + error.getMessage());
                //com.android.volley.TimeoutError
                String er = "com.android.volley.TimeoutError";

                // com.android.volley.TimeoutError
                if (er.equals("com.android.volley.TimeoutError")) {

                } else {
                    //  sendError(error.toString(), "user/login?username=");
                    Toast.makeText(o.this, "Server Error", Toast.LENGTH_SHORT).show();
                }


                pDialog.dismiss();

            }
        });


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

           /* case 200:

                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                break;*/
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                    }
                    // Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
                    else {

                        // Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;

        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(o.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void sendToServer() {

        new Insert_Geo_Location_Details().execute();
    }

    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 5;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        // Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    public void makJsonObject(ArrayList<String> company_idd, String[] user_idd, String[] user_latitudee, String[] user_longitude, String[] track_date, String[] track_time, String[] battery_persentage, String[] network_type, String[] profile_type, String[] operator_type, String[] cellid_short, String[] cellid_long, String lac, String mcc, String mnc, String device_type, String basestation_id, String basestation_latitude, String basestation_longitude, String[] shutdown_time, String[] restart_time, String[] signal_strength, String[] network_subtype)
            throws JSONException {
        JSONObject obj = null;
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < company_idd.size(); i++) {
            obj = new JSONObject();
            try {
                obj.put("id", company_idd.get(i));
               /* obj.put("name", name[i]);
                obj.put("year", year[i]);
                obj.put("curriculum", curriculum[i]);
                obj.put("birthday", birthday[i]);*/

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }

        JSONObject finalobject = new JSONObject();
        finalobject.put("student", jsonArray);
        Log.e(TAG, "makJsonObject:finalobject " + finalobject);
        Log.e(TAG, "makJsonObject:jsonArray " + jsonArray);
        // return finalobject;
    }

    private void dummyData() {
        JSONObject student1 = new JSONObject();
        try {
            student1.put("id", "3");
            student1.put("name", "NAME OF STUDENT");
            student1.put("year", "3rd");
            student1.put("curriculum", "Arts");
            student1.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSONObject student2 = new JSONObject();
        try {
            student2.put("id", "2");
            student2.put("name", "NAME OF STUDENT2");
            student2.put("year", "4rd");
            student2.put("curriculum", "scicence");
            student2.put("birthday", "5/5/1993");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        JSONArray jsonArray = new JSONArray();

        jsonArray.put(student1);
        jsonArray.put(student2);

        JSONObject studentsObj = new JSONObject();
        try {
            studentsObj.put("Students", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String jsonStr = studentsObj.toString();
        Log.e(TAG, "dummyData:======== " + jsonStr);
        System.out.println("jsonString: " + jsonStr);
    }

    GPSTracker gps;
    String basestation_id = "", basestation_latitude = "", basestation_longitude = "";
    private TrackUser trackUser;
    private DBAdapter dbAdapter;

    public boolean isAirplaneMode() {
        boolean airplaneMode = Settings.System.getInt(this.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        return airplaneMode;
    }

    String lat, log;

    public void fillDataInDatabase(String sstring) {
        // Toast.makeText(context, "fillDataInDatabase", Toast.LENGTH_LONG).show();
        dbAdapter = new DBAdapter(o.this);
        long rowid = 0;


        gps = new GPSTracker(o.this);
        //if (gps.canGetLocation()) {
        Location loc = gps.getLocation();


        //if (loc != null) {
        if (loc != null) {
            lat = loc.getLatitude() + "";
            log = loc.getLongitude() + "";
        } else {
            lat = loc.getLatitude() + "";
            log = loc.getLongitude() + "";
            /*lat = "0";
            log = "0";*/
        }
        trackUser = new TrackUser();
        trackUser.setParent_id(company_id);
        trackUser.setUser_id(user_id);
        trackUser.setLatitude(lat);
        trackUser.setLongitude(log);
        trackUser.setBattery_persentage(String.valueOf(level));
        trackUser.setDate(date);
        trackUser.setTime(time);
        trackUser.setNetwork_type(ConnectionDetector.network_type);
        trackUser.setCellIdShort(cellIdShort);

        trackUser.setCellIdLong(cellIdLong);
        trackUser.setLac(lac);
        trackUser.setMcc(mcc);
        trackUser.setMnc(mnc);

        trackUser.setDevice_type(device_type);
        trackUser.setBasestation_id(basestation_id);
        trackUser.setBasestation_latitude(basestation_latitude);
        trackUser.setBasestation_longitude(basestation_longitude);


        try {
            trackUser.setProfileType(objSPS.getValueFromShared_Pref("profileType"));
            trackUser.setOperatorType(objSPS.getValueFromShared_Pref("operatorType"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        trackUser.setShutdownTime(shutdownTime);
        trackUser.setRestartTime(restartTime);
        trackUser.setSignalStrenght(signalStrenght);
        trackUser.setNetworkSubType(networkSubType);
        trackUser.setSentStatus("");

        //  rowid = dbAdapter.insertTrackUserInfo(trackUser);
        // Log.d(" Inserte offlinerowid: ", rowid + "");
        //  ArrayList<TrackUser> trackUserArrayList = dbAdapter.getTrackUserInfo();
        // Log.d(TAG, "trackUserArrayList size:" + trackUserArrayList.size());
        //  Log.d(TAG, "trackUserArrayList size:" + trackUserArrayList.get(0).getNetwork_type());

        // }


        //  }
    }

    JSONObject jsonParam, jsonParam1;
    JSONArray array = null;

    private void trackApiCallng() throws JSONException {
        String URL = AppConstants.BASEURL + "user/add_bulk_track";
        // new Insert_Geo_Location_Details().execute();
        //  String URL = "www.myposturl.com/data";

        RequestQueue queue = Volley.newRequestQueue(this);
        array = new JSONArray();
        //Create json array for filter
        try {
            jsonParam = new JSONObject();

            array = new JSONArray("params");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Create json objects for two filter Ids
        jsonParam1 = new JSONObject();

        try {
            //Add string params
            jsonParam1.put("company_id", company_id);
            jsonParam1.put("user_id", user_id);
            jsonParam1.put("user_latitude", lat);
            jsonParam1.put("user_longitude", log);
            jsonParam1.put("track_date", date);
            jsonParam1.put("track_time", time);
            jsonParam1.put("battery_persentage", String.valueOf(level));
            jsonParam1.put("network_type", ConnectionDetector.network_type);
            jsonParam1.put("profile_type", profileType);
            jsonParam1.put("operator_type", operatorType);
            jsonParam1.put("cellid_short", cellIdShort);
            jsonParam1.put("cellid_long", cellIdLong);
            jsonParam1.put("lac", lac);
            jsonParam1.put("mcc", mcc);
            jsonParam1.put("mnc", mnc);
            jsonParam1.put("device_type", device_type);
            jsonParam1.put("basestation_id", basestation_id);
            jsonParam1.put("basestation_latitude", basestation_latitude);
            jsonParam1.put("basestation_longitude", basestation_longitude);
            jsonParam1.put("shutdown_time", shutdownTime);
            jsonParam1.put("restart_time", restartTime);
            jsonParam1.put("signal_strength", signalStrenght);
            jsonParam1.put("network_subtype", networkSubType);
            Log.e(TAG, "trackApiCallng:jsonParam1 " + jsonParam1);
            Log.e(TAG, "trackApiCallng: URL===" + URL);
            // array.put(jsonParam1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        array.put(jsonParam1);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonParam1);
        JSONArray jsonArray2 = new JSONArray();
        jsonArray2.put(json2);

        Log.e(TAG, "trackApiCallng: array===" + array);
        Log.e(TAG, "params=jsonArray===\n=" + jsonArray);
        Log.e(TAG, "params=jsonArray2==\n==" + jsonArray2);
        Log.e(TAG, "params=jsonParam1==\n==" + jsonParam1);


        JSONArray js = new JSONArray(array.toString());
        JSONObject obj2 = new JSONObject();
        obj2.put("params", js.toString());
        Log.e(TAG, "trackApiCallng: obj2==0000000====" + obj2.toString());
        Log.e(TAG, "trackApiCallng: js==00000====" + js);


        Map<String, String> params = new HashMap<String, String>();
        params.put("tag", "test");
        params.put("company_id", company_id);
        params.put("user_id", user_id);
        params.put("user_latitude", lat);
        params.put("user_longitude", log);
        params.put("track_date", date);
        params.put("track_time", time);
        params.put("battery_persentage", String.valueOf(level));
        params.put("network_type", ConnectionDetector.network_type);
        params.put("profile_type", profileType);
        params.put("operator_type", operatorType);
        params.put("cellid_short", cellIdShort);
        params.put("cellid_long", cellIdLong);
        params.put("lac", lac);
        params.put("mcc", mcc);
        params.put("mnc", mnc);
        params.put("device_type", device_type);
        params.put("basestation_id", basestation_id);
        params.put("basestation_latitude", basestation_latitude);
        params.put("basestation_longitude", basestation_longitude);
        params.put("shutdown_time", shutdownTime);
        params.put("restart_time", restartTime);
        params.put("signal_strength", signalStrenght);
        params.put("network_subtype", networkSubType);
        JSONArray jsonObj = new JSONArray(params);
        // JSONArray js = new JSONArray(array.toString());
        JSONObject obj22 = new JSONObject();
        obj22.put("params", jsonObj.toString());
        Log.e(TAG, "trackApiCallng:obj22----------------\n " + obj22);
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.POST, URL, obj22, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

// Add the request to the RequestQueue.
        queue.add(jsonObjRequest);
    }

    private void processJsonObjectt(JSONObject response) {
        if (response != null) {
            Log.d("Response", response + "");

            try {
                String data = String.valueOf(response.get("data"));
                String message = String.valueOf(response.get("message"));
                int status = response.getInt("status");
                if (status == 0) {
                    //  alertDialoge(message);

                } else {
                    //  alertDialoge(message);

                }
                //Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
                // String responce = json.getJSONArray("RESPONSE");
            } catch (NullPointerException e) {
                e.printStackTrace();
                // pDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private JSONObject json, json2;
    String parentid, userid;
    private SharePreferanceWrapperSingleton objSPS;

   /* @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/

  /*  @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }*/

    /*private void alertDialoge(final String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("" + message);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(ParkingList.this, message, Toast.LENGTH_LONG).show();
                        getParkingList(parkingType);
                    }
                });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
*/
    private class Insert_Geo_Location_Details extends AsyncTask<String, String, String> {
        JSONArray items_list;

        //ProgressDialog dialog = new ProgressDialog(context);
        //  ProgressDialog dialog = new ProgressDialog(Main2Activity.this);
        @Override
        protected String doInBackground(String... params) {
            String serverResponse = "";
            try {
                try {
                    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                    Intent batt = getApplicationContext().registerReceiver(null, filter);
                    int level = 0; // Default to some unknown/wild value
// registerReceiver method call could return null, so check that!
                    if (batt != null) {
                        level = batt.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    }

                    // Format formatter = new SimpleDateFormat("hh:mm:ss a");
                    Format formatter = new SimpleDateFormat("MM-dd-yyyy");
                    String date = formatter.format(new Date());
                    //msgStr.append(formatter.format(new Date()));
                    formatter = new SimpleDateFormat("hh:mm");
                    String time = formatter.format(new Date());

                    if (gps.canGetLocation()) {
                        Log.d("canGetLocation", "canGetLocation yes");
                        Location loc = gps.getLocation();

                        userid = spf_user_id.getString("userid", "");
                        parentid = pref.getString("parent_id", "");

                        json.put("parent_id", parentid);
                        json.put("user_id", userid);
                        if (loc != null) {
                            json.put("Latitude", loc.getLatitude());
                            json.put("Longitude", loc.getLongitude());
                        } else {
                            json.put("Latitude", "0");
                            json.put("Longitude", "0");
                        }

                        json.put("date", date);
                        json.put("time", time);
                        json.put("battery_persentage", level);


                        json.put("network_type", ConnectionDetector.network_type);


                    } else {
                        Log.d("Status: ", "Could not find Location");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


                // JSONArray postjson = prepareJSONData();
                try {
                    // if (canGetLocation()) {

                    jsonParam1 = new JSONObject();

                    jsonParam1.put("company_id", company_id);
                    jsonParam1.put("user_id", user_id);
                    jsonParam1.put("user_latitude", lat);
                    jsonParam1.put("user_longitude", log);
                    jsonParam1.put("track_date", date);
                    jsonParam1.put("track_time", time);
                    jsonParam1.put("battery_persentage", String.valueOf(level));
                    jsonParam1.put("network_type", ConnectionDetector.network_type);
                    jsonParam1.put("profile_type", profileType);
                    jsonParam1.put("operator_type", operatorType);
                    jsonParam1.put("cellid_short", cellIdShort);
                    jsonParam1.put("cellid_long", cellIdLong);
                    jsonParam1.put("lac", lac);
                    jsonParam1.put("mcc", mcc);
                    jsonParam1.put("mnc", mnc);
                    jsonParam1.put("device_type", device_type);
                    jsonParam1.put("basestation_id", basestation_id);
                    jsonParam1.put("basestation_latitude", basestation_latitude);
                    jsonParam1.put("basestation_longitude", basestation_longitude);
                    jsonParam1.put("shutdown_time", shutdownTime);
                    jsonParam1.put("restart_time", restartTime);
                    jsonParam1.put("signal_strength", signalStrenght);
                    jsonParam1.put("network_subtype", networkSubType);
                    System.out.println(jsonParam1.toString());
                    Log.e(TAG, "doInBackground:===jsonParam1=== " + jsonParam1.toString());
                    //  }


                } catch (JSONException je) {
                    je.printStackTrace();

                }

                JSONArray postjson = new JSONArray();
                postjson.put(jsonParam1);
                json2.put("params", postjson);
                System.out.println(json2.toString());
                Log.e(TAG, "doInBackground:===json2=== " + json2.toString());


            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            try {
                /*http://infibizapi.daffodillab.in/index.php/user/inserttrackbulk*/
                //String url2 = "http://infibizapi.daffodillab.in/index.php/user/inserttrack";
                String url2 = "http://daffodillab.in/trackerpgpl/index.php/v1/user/add_bulk_track";
                serverResponse = WebServiceConnection.getData(url2, json2.toString());
                Log.e(TAG, "doInBackground: ==serverResponse=" + serverResponse);
                System.out.println(" Server response *********** serverResponse" + serverResponse);

            } catch (Exception e) {

                e.printStackTrace();
                fillDataInDatabase("");
            }


            return serverResponse.trim();
        }

        @Override
        protected void onPreExecute() {

            // dialog.setMessage("Please wait...");
            //  dialog.setIndeterminate(true);
            //  dialog.setCancelable(false);
            //  dialog.show();
            json = new JSONObject();
            json2 = new JSONObject();


            String ctime = getCurrentTimeDate();
            String[] cDateArray = ctime.split(" ");
            String cDate = cDateArray[0];
            String yesturdayDate = getYesterdayDateString();
            pTime = ctime;
            // turnGPSOn();
            GPSTracker gps = new GPSTracker(getApplicationContext());
            String location = "";
            if (gps.canGetLocation()) {
                location = gps.getLatitude() + "," + gps.getLongitude();
            }
            pLocation = location;
            SharedPreferenceManager.instance().setValueToSharedPref("PunchInTime", ctime);
            SharedPreferenceManager.instance().setValueToSharedPref("PunchInLocation", location);
            ArrayList<TrackUser> arrlistTrackUser = dbAdapter.getTrackUserInfo();


            if (arrlistTrackUser.size() > 0) {
                for (int i = 0; i < arrlistTrackUser.size(); i++) {
                    TrackUser trackUser = arrlistTrackUser.get(i);
                    String date = trackUser.getDate();
                    //date = "2015-04-16";// + " " + trackUser.getTime();
                    try {
                        if (!date.equals(cDate) && !date.equals(yesturdayDate)) {
                            try {
                                dbAdapter.deleteRow("TrackUser", "date", trackUser.getDate());

                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    } catch (NullPointerException ne) {
                        ne.printStackTrace();

                    }
                }
            }

            super.onPreExecute();
            // Toast.makeText(mContext, "onPreExecute", Toast.LENGTH_LONG).show();


        }

        private String getYesterdayDateString() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();

            cal.add(Calendar.DATE, -1);
            return dateFormat.format(cal.getTime());
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (!result.equals("") && !result.equals(null)) {
                if (result.equalsIgnoreCase("true")) {
                    // Toast.makeText(mContext, "onPostExecute", Toast.LENGTH_LONG).show();
                    fillDataInDatabase("SENT");

                    Log.d("User track", "data sent successfully");
                } else {
                    try {
                        fillDataInDatabase("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                Log.d("User track", "data could not send successfully");
                try {
                    fillDataInDatabase("");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

            // stopUsingGPS();


        }
    }

    private SharedPreferences pref, spf_user_id;

    public JSONArray prepareJSONData() {
        String lat, log;
        int i = 0;
        //dbAdapter.delete_trackUser();
        //  JSONObject json=new JSONObject();
        JSONArray postjson = new JSONArray();


        ArrayList<TrackUser> arrlistTrackUser = null;
        try {
            arrlistTrackUser = dbAdapter.getTrackUserInfo();
            if (arrlistTrackUser.size() > 0) {
                for (i = 0; i < arrlistTrackUser.size(); i++) {
                    TrackUser trackUser = arrlistTrackUser.get(i);
                    if (trackUser.getNetwork_type().equals("") && !trackUser.getSentStatus().equals("SENT")) {
                        try {

                            Log.d("canGetLocation", "canGetLocation yes");

                            json = new JSONObject();

                            json.put("parent_id", trackUser.getParent_id());
                            json.put("user_id", trackUser.getUser_id());
                            json.put("user_latitude", trackUser.getLatitude());
                            json.put("user_longitude", trackUser.getLongitude());

                            json.put("track_date", trackUser.getDate());
                            json.put("track_time", trackUser.getTime());
                            json.put("battery_persentage", trackUser.getBattery_persentage());


                            json.put("network_type", trackUser.getNetwork_type());

                            json.put("profile_type", trackUser.getProfileType());
                            json.put("operator_type", trackUser.getOperatorType());

                            json.put("cellid_short", trackUser.getCellIdShort());
                            json.put("cellid_long", trackUser.getCellIdLong());
                            json.put("lac", trackUser.getLac());
                            json.put("mcc", trackUser.getMcc());
                            json.put("mnc", trackUser.getMnc());

                            json.put("device_type", trackUser.getDevice_type());
                            json.put("basestation_id", trackUser.getBasestation_id());
                            json.put("basestation_latitude", trackUser.getBasestation_latitude());
                            json.put("basestation_longitude", trackUser.getBasestation_longitude());


                            json.put("shutdown_time", trackUser.getShutdownTime());
                            json.put("restart_time", trackUser.getRestartTime());
                            json.put("signal_strength", trackUser.getSignalStrenght());
                            json.put("network_subtype", trackUser.getNetworkSubType());


                            postjson.put(json);
                            dbAdapter.deleteRow("TrackUser", "time", trackUser.getTime());
                            ArrayList<TrackUser> aList = dbAdapter.getTrackUserInfo();

                            Log.d("Status: ", "Could not find Location");

                        } catch (JSONException je) {
                            je.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Having Network
                    }

                }
                //dbAdapter.delete_trackUser();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            shutdownTime = objSPS.getValueFromShared_Pref("ShutdownTime");

            //restartTime = objSPS.getValueFromShared_Pref("RestartTime");
            restartTime = SharedPreferenceManager.instance().getValueFromShared_Pref("RestartTime");
            System.out.println("shutdownTime : " + shutdownTime + "    restartTime: " + restartTime);

            signalStrenght = ConnectionDetector.getSignalStrenght(o.this);
            networkSubType = ConnectionDetector.getNetworkSubType(o.this);

            IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batt = getApplicationContext().registerReceiver(null, filter);
            String level = "0"; // Default to some unknown/wild value
// registerReceiver method call could return null, so check that!
            if (batt != null) {
                level = batt.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "";
            }

            // Format formatter = new SimpleDateFormat("hh:mm:ss a");
            Format formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(new Date());
            //msgStr.append(formatter.format(new Date()));
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            formatter = new SimpleDateFormat("HH:mm:ss");
            //  String time = formatter.format(new Date());
            String time = dateFormat.format(new Date());
            try {
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                @SuppressLint("MissingPermission") CellLocation cellLoc = telephonyManager.getCellLocation();
                if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {

                    device_type = "GSM";
                    String networkOperator = telephonyManager.getNetworkOperator();
                    if (!networkOperator.equals("")) {
                        mcc = networkOperator.substring(0, 3);
                        mnc = networkOperator.substring(3);
                    }

                    CellLocation.requestLocationUpdate();
                    if (cellLoc instanceof GsmCellLocation) {
                        GsmCellLocation cellLocation = (GsmCellLocation) cellLoc;
                        cellIdLong = cellLocation.getCid() + "";
                        short cIdShort = 0;
                        if (!cellIdLong.equals("")) {
                            cIdShort = (short) Integer.parseInt(cellIdLong);
                        }
                        cellIdShort = cIdShort + "";
                        lac = cellLocation.getLac() + "";
                        // do work
                    }


                } else if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
                    device_type = "CDMA";
                    CdmaCellLocation cellLocation = (CdmaCellLocation) cellLoc;

                    basestation_id = cellLocation.getBaseStationId() + "";
                    basestation_latitude = cellLocation.getBaseStationLatitude() + "";
                    //int lat = ((L * 90) / 1296000);
                    basestation_longitude = cellLocation.getBaseStationLongitude() + "";
                }


            } catch (Exception e) {
                e.printStackTrace();
                cellIdShort = "";
                cellIdLong = "";
                mcc = "";
                mnc = "";
                lac = "";
                device_type = "GSM";
                basestation_id = "";
                basestation_latitude = "";
                basestation_longitude = "";


            }

            if (gps.canGetLocation()) {
                Log.d("canGetLocation", "canGetLocation yes");
                Location loc = gps.getLocation();
              /*  try {
                    lat = loc.getLatitude() + "";
                    log = loc.getLongitude() + "";
                } catch (NullPointerException ne) {
                    ne.printStackTrace();
                    lat = "0";
                    log = "0";
                }*/
                if (loc != null) {
                    lat = loc.getLatitude() + "";
                    log = loc.getLongitude() + "";
                } else {
                    lat = "0";
                    log = "0";
                }

                userid = spf_user_id.getString("userid", "");
                parentid = pref.getString("parent_id", "");
                json = new JSONObject();
                try {
                    json.put("parent_id", parentid);
                    json.put("user_id", userid);
                    json.put("user_latitude", lat);
                    json.put("user_longitude", log);

                    json.put("track_date", date);
                    json.put("track_time", time);
                    json.put("battery_persentage", level);


                    json.put("network_type", ConnectionDetector.network_type);
                    json.put("profile_type", objSPS.getValueFromShared_Pref("profileType"));
                    json.put("operator_type", objSPS.getValueFromShared_Pref("operatorType"));

                    json.put("cellid_short", cellIdShort);
                    json.put("cellid_long", cellIdLong);
                    json.put("lac", lac);
                    json.put("mcc", mcc);
                    json.put("mnc", mnc);

                    json.put("device_type", device_type);
                    json.put("basestation_id", basestation_id);
                    json.put("basestation_latitude", basestation_latitude);
                    json.put("basestation_longitude", basestation_longitude);
                    json.put("shutdown_time", shutdownTime);

                    json.put("restart_time", restartTime);
                    json.put("signal_strength", signalStrenght);
                    json.put("network_subtype", networkSubType);


                    postjson.put(json);



               /* json2.put("params", postjson);
                System.out.println(json2.toString());*/


          /*  */

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            } else {
                Log.d("Status: ", "Could not find Location");
            }
        } catch (Exception e) {
        }
        return postjson;
    }

    public String getOperatorType() {
        String operatorType = "";
        TelephonyManager telephonyManager = ((TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE));
        operatorType = telephonyManager.getNetworkOperatorName();
        return operatorType;
    }

    public String getProfileType() {
        String profileType = "";
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.i("MyApp", "Silent mode");
                profileType = "Silent";
                //Toast.makeText(Main2Activity.this, "MyApp Silent mode", Toast.LENGTH_LONG).show();
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.i("MyApp", "Vibrate mode");
                profileType = "Vibrate";
                //Toast.makeText(Main2Activity.this, "MyApp Vibrate mode", Toast.LENGTH_LONG).show();
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.i("MyApp", "Normal mode");
                profileType = "Normal";
                //Toast.makeText(Main2Activity.this, "MyApp Normal mode", Toast.LENGTH_LONG).show();
                break;

        }


        // String operatorName = telephonyManager.getSimOperatorName();
        return profileType;
    }

    private String getCurrentTimeDate() {

        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //SimpleDateFormat formatter = new SimpleDateFormat(" hh:mm:ss aa");

        return formatter.format(calendar.getTime());
    }

   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }*/

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }*/


    @Override
    protected void onStart() {
        super.onStart();
        // setPunchInTime();

    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


   /* @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cameraa) {
            // Handle the camera action
        } else if (id == R.id.nav_galleryy) {

        } else if (id == R.id.nav_slideshoww) {

        } else if (id == R.id.nav_managee) {

        } else if (id == R.id.nav_sharee) {

        } else if (id == R.id.nav_sendd) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    public void onBackPressed() {
        //  super.onBackPressed();
        AlertDialog.Builder ab = new AlertDialog.Builder(o.this);
        ab.setTitle("" + getResources().getString(R.string.app_name));
        ab.setMessage("are you sure to exit?");
        ab.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //if you want to kill app . from other then your main avtivity.(Launcher)
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);

                //if you want to finish just current activity

                finish();
            }
        });
        ab.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        ab.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnPunch:
                startActivity(new Intent(this, PunchInActivity.class));
                break;
            case R.id.btnClientList:
                startActivity(new Intent(this, ClientListActivity.class));
                break;
            case R.id.btnRevisit:
                startActivity(new Intent(this, RevisitActivity.class));
                break;
            case R.id.btnAddFollowup:
                startActivity(new Intent(this, AddFollowupActivity.class));
                break;
            case R.id.btnAddClient:
                startActivity(new Intent(this, AddClientActivity.class));
                break;
        }
    }
}
