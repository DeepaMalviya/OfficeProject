package com.daffodil.officeproject.LoginModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daffodil.officeproject.R;
import com.daffodil.officeproject.base.AppConstants;
import com.daffodil.officeproject.base.AppController;
import com.daffodil.officeproject.base.SessionManager;
import com.daffodil.officeproject.utilities.ConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;

import static com.daffodil.officeproject.base.AppConstants.BASEURL;

public class LoginnActivity extends AppCompatActivity {
    private static final String TAG = "LoginnActivity";
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private TelephonyManager mTelephonyManager;
    private String userid, imei;
    private Spinner spinner;
    private EditText editText;
    SharedPreferences sharedpref;
    SharedPreferences.Editor ed;
    Button button;
    private ConnectionDetector connectionDetector;
    private boolean isNetworkAvailable = false;
    SessionManager session;
    String urlData;
    int PERMISSION_ALL = 1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BATTERY_STATS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginn);
        if (checkAndRequestPermissions()) {
            Log.e(TAG, "onCreate: checkAndRequestPermissions");
        }else {
            Log.e(TAG, "onCreate: checkAndRequestPermissions");
        }

        initView();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                getDeviceImei();
            }
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            // Log.e("msg", " ======imei====" + imei);
        }*/
       /* String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        telephonyManager.getDeviceId();
        imei = telephonyManager.getDeviceId();*/
        // Toast.makeText(this, "" + telephonyManager.getDeviceId(), Toast.LENGTH_SHORT).show();
        //    Log.e(TAG, "onCreate: telephonyManager.getDeviceId()" + telephonyManager.getDeviceId());
        //    Log.e(TAG, "onCreate: androidID==" + androidID);
        //getImEI();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
                Log.e(TAG, "initView:  if permission");
            } else {
                Log.e(TAG, "initView:  else permission");
                getDeviceImei();
            }
        }*/


    }

    private boolean checkAndRequestPermissions() {
        int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int permissionProcessOutGogingCalls = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionProcessReadContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionProcessReadCallLog = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        int permissionWriteStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionReadStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (permissionProcessOutGogingCalls != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionProcessReadContacts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (permissionProcessReadCallLog != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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

    private void initView() {

        editText = findViewById(R.id.editTextMobile);
        button = findViewById(R.id.buttonContinuew);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ");

                String number = editText.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editText.setError("Valid number is required");
                    editText.requestFocus();
                    return;
                }

                String phoneNumber = number;
                validate();
               /* Intent intent = new Intent(LoginnActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);*/

            }
        });
    }

    private void getImEI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        PERMISSIONS_REQUEST_READ_PHONE_STATE);
            } else {
                getDeviceImei();
            }
        } else {
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
            // Log.e("msg", " ======imei====" + imei);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getDeviceImei();
        }
        if (grantResults.length == 0 || grantResults == null) {
            /*If result is null*/
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            /*If We accept permission*/
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            /*If We Decline permission*/
        }
    }

    @SuppressLint("NewApi")
    private void getDeviceImei() {

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
        Log.e("msg", "DeviceImei ==========" + imei);
    }


    private void validate() {
        final String username = editText.getText().toString();

        if (AppConstants.isBlank(username)) {
            AppConstants.showToast(LoginnActivity.this, getString(R.string.mobile_is_required));
        } else {
            loginSevice(username);
            /*if (AppConstants.isInternetAvailable(LoginnActivity.this)) {

            } else {
                Toast.makeText(LoginnActivity.this, "Internet Connection Required", Toast.LENGTH_SHORT).show();


            }
*/

        }
    }

    private void loginSevice(final String username) {
        final ProgressDialog pDialog = new ProgressDialog(LoginnActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progress_bar);
         /*http://staggingapi.opark.in/index.php/v1/user/login?username=9999999999&password=1234*/
         /*http://staggingapi.opark.in/index.php/v1/user/login?username=9999999999&password=1234*/
        //String urlData = AppConstants.BASEURL + "user/login?username=" + username;
        if (imei != null) {
            urlData = BASEURL + "user/sendotp?mobile=" + username + "&imei=" + imei;
            //urlData = BASEURL + "user/sendotp?mobile=" + username + "&imei=";

        } else {
            urlData = BASEURL + "user/sendotp?mobile=" + username + "&imei=";


        }
        //urlData = BASEURL + "user/sendotp?mobile=" + username + "&imei=";

        Log.e(TAG, "loginSevice:urlData=== " + urlData);
        // String urlData = BASEURL + "sendotp?mobile=" + username + "&imei=" + "123456789";

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, urlData, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.d(TAG, response.toString());

                try {
                    // System.out.println("JSON RETURN " + response.toString());

                    String data = String.valueOf(response.get("data"));
                    String message = String.valueOf(response.get("message"));
                    int status = response.getInt("status");

                    if (status == 0) {
                        Toast.makeText(LoginnActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                        JSONObject loginresponce = response.getJSONObject("data");
                        editText.setText("");


                        LoginModel loginModel = new LoginModel();

                        loginModel.setMobile(loginresponce.getString("mobile"));
                        loginModel.setOtp(loginresponce.getString("otp"));
                        loginModel.setUserId(loginresponce.getString("user_id"));


                        sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
                        ed = sharedpref.edit();
                        ed.putString("mobile", loginModel.getMobile());
                        ed.putString("user_id", loginModel.getUserId());
                        ed.putString("otp", loginModel.getOtp());

                        ed.apply();
                        ed.commit();
                        String userRole = "Normal Agent";

                        Intent intentTow = new Intent(LoginnActivity.this, VerifyPhoneActivity.class);
                        intentTow.putExtra("mobile", loginModel.getMobile());
                        intentTow.putExtra("user_id", loginModel.getUserId());
                        intentTow.putExtra("otp", loginModel.getOtp());
                        startActivity(intentTow);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();


                        pDialog.dismiss();


                    } else {
                        Toast.makeText(LoginnActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    pDialog.dismiss();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(LoginnActivity.this, "Unexpected Error...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(LoginnActivity.this, "Technical Error...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // VolleyLog.d(TAG, "Error: " + error.getMessage());
                //com.android.volley.TimeoutError
                String er = "com.android.volley.TimeoutError";

                // com.android.volley.TimeoutError
                if (er.equals("com.android.volley.TimeoutError")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginnActivity.this);
                    builder.setMessage("Time Out");
                    builder.setPositiveButton("ReTry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (AppConstants.isInternetAvailable(LoginnActivity.this)) {
                                loginSevice(username);
                            } else {
                                Toast.makeText(LoginnActivity.this, "Internet Connection Required", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    //  sendError(error.toString(), "user/login?username=");
                    Toast.makeText(LoginnActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                }

                // hide the progress dialog
                pDialog.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    //sendError(error.toString(), "user/login?username=");

                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                } else if (error instanceof ServerError) {
                    // sendError(error.toString(), "user/login?username=");

                    message = "The server could not be found. Please try again after some time!!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                } else if (error instanceof AuthFailureError) {
                    // sendError(error.toString(), "user/login?username=");

                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                } else if (error instanceof NoConnectionError) {
                    //sendError(error.toString(), "user/login?username=");

                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                } else if (error instanceof TimeoutError) {
                    //  sendError(error.toString(), "user/login?username=");

                    message = "Connection TimeOut! Please check your internet connection.";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                } else if (error instanceof ParseError) {
                    //  sendError(error.toString(), "user/login?username=");

                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
}
