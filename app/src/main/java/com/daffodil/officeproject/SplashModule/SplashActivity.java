package com.daffodil.officeproject.SplashModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import com.daffodil.officeproject.HomeModule.Main2Activity;
import com.daffodil.officeproject.LoginModule.LoginnActivity;
import com.daffodil.officeproject.R;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    /**
     * Duration of wait
     **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private TextView loading_tv2;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private TelephonyManager mTelephonyManager;
    private String userid, imei;
    SharedPreferences sharedpref;
    int PERMISSION_ALL = 1;

    /*  String[] PERMISSIONS = {
              android.Manifest.permission.ACCESS_FINE_LOCATION,
              android.Manifest.permission.ACCESS_COARSE_LOCATION,
              android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
              Manifest.permission.READ_EXTERNAL_STORAGE,
              android.Manifest.permission.CAMERA
      };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.splashScreenTheme);
        setContentView(R.layout.activity_splash);
        // Log.e(TAG, "onCreate:-===== "+!hasPermissions(this, PERMISSIONS) );
       /* if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }*/
        initView();


    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

       /* if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                //doPermissionGrantedStuffs();
            } else {
                alertAlert("permissions_not_granted_read_phone_state");
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                //doPermissionGrantedStuffs();
            } else {
                alertAlert("permissions_not_granted_read_phone_state");
            }
        }*/
      /*  if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        } *//*if (requestCode == PERMISSION_ALL
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }*/
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(SplashActivity.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                // .setIcon(R.drawable.onlinlinew_warning_sign)
                .show();
    }


    String mobile, time_in, time_out, user_id, otp, user_role, user_name, company_id, company_name;

    private void initView() {
        Log.e(TAG, "initView: ");

        sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
        mobile = sharedpref.getString("mobile", "");
        user_id = sharedpref.getString("user_id", "");
        otp = sharedpref.getString("otp", "");
        user_role = sharedpref.getString("user_role", "");
        user_name = sharedpref.getString("user_name", "");
        company_id = sharedpref.getString("company_id", "");
        company_name = sharedpref.getString("company_name", "");
        Log.e(TAG, "initView:user_id" + user_id);
        time_in = sharedpref.getString("time_in", "");
        time_out = sharedpref.getString("time_out", "");

        Log.e(TAG, "initView:time_in  " + time_in);
        Log.e(TAG, "initView:time_out  " + time_out);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                Log.e(TAG, "initView:  if permission");
            } else {
                Log.e(TAG, "initView:  else permission");
                getDeviceImei();
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (user_id.equals("")) {
                        Log.e(TAG, "run1" + user_id.equals(""));
                        Intent intentSplash = new Intent(SplashActivity.this, LoginnActivity.class);
                        startActivity(intentSplash);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else if (user_name.equals("") && user_id.equals("") && company_name.equals("") && user_name.equals("")) {
                        Log.e(TAG, "run2:user_name ");
                        Intent intentSplash = new Intent(SplashActivity.this, LoginnActivity.class);
                        startActivity(intentSplash);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else if (!user_id.equals("")) {
                        Log.e(TAG, "run7: else" + !user_id.equals(""));
                        Log.e(TAG, "run7: else user_id" + user_id);
                        Intent intentSplash = new Intent(SplashActivity.this, Main2Activity.class);
                        startActivity(intentSplash);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        Log.e(TAG, "run: final else" );
                    }


                    //}


                } catch (Exception err) {
                    initView();
                    Intent mainIntent = new Intent(SplashActivity.this, LoginnActivity.class);
                    startActivity(mainIntent);
                    finish();
                    err.printStackTrace();
                }
            }

        }, SPLASH_DISPLAY_LENGTH);
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }
    }*/

    @SuppressLint("NewApi")
    private void getDeviceImei() {
        Log.e(TAG, "getDeviceImei: ");
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        imei = mTelephonyManager.getDeviceId();
        Log.e("msg", "DeviceImei " + imei);
    }


}


