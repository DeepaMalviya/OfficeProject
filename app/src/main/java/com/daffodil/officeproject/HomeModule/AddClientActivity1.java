package com.daffodil.officeproject.HomeModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daffodil.officeproject.HomeModule.adapter.CustomAdapter;
import com.daffodil.officeproject.LocationModule.GPSTracker;
import com.daffodil.officeproject.R;
import com.daffodil.officeproject.base.AppConstants;
import com.daffodil.officeproject.base.AppController;
import com.daffodil.officeproject.base.VolleyMultipartRequest;
import com.daffodil.officeproject.base.VolleySingleton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static com.daffodil.officeproject.base.AppConstants.BASEURL;
import static com.daffodil.officeproject.base.AppConstants.getDataColumn;
import static com.daffodil.officeproject.base.AppConstants.isDownloadsDocument;
import static com.daffodil.officeproject.base.AppConstants.isExternalStorageDocument;
import static com.daffodil.officeproject.base.AppConstants.isMediaDocument;

public class AddClientActivity1 extends AppCompatActivity {
    private static final String TAG = "AddClientActivity";
    String[] countryNameList = {"India", "Company", "Company1", "Company2", "China", "Australia", "New Zealand", "England", "Pakistan"};
    String[] mobileArray = {"Client 1", "Client 2", "Client 3", "Client 4", "Client 5"};
    Button btnSubmit;
    ImageView imageCompany;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    Uri imageUri;
    Uri imageUri1 = Uri.parse("");
    private String selectedPath = "";
    EditText simpleAutoCompleteTextView;
    SharedPreferences sharedpref;
    String mobile, user_id, otp, user_role, user_name, company_id, company_name;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final long UPDATE_INTERVAL = 60000; // Every 60 seconds.
    private static final long FASTEST_UPDATE_INTERVAL = 30000; // Every 30 seconds
    private static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 5; // Every 5 minutes.
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    String stringLatitude, stringLongitude, country, city, postalCode, addressLine;
    ListView listView;
    List<ClientListModel> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);
        initData();
        initView();

    }

    private void initData() {
        sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
        mobile = sharedpref.getString("mobile", "");
        user_id = sharedpref.getString("user_id", "");
        otp = sharedpref.getString("otp", "");
        user_role = sharedpref.getString("user_role", "");
        user_name = sharedpref.getString("user_name", "");
        company_id = sharedpref.getString("company_id", "");
        company_name = sharedpref.getString("company_name", "");
        if (!checkPermissions()) {
            requestPermissions();
        }
        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);

            country = gpsTracker.getCountryName(this);

            city = gpsTracker.getLocality(this);

            postalCode = gpsTracker.getPostalCode(this);

            addressLine = gpsTracker.getAddressLine(this);
            Log.e(TAG, "initData:stringLatitude " + stringLatitude);
            Log.e(TAG, "initData:stringLongitude " + stringLongitude);

        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
        createLocationRequest();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageCompany = findViewById(R.id.imageCompany);
        imageCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraIntent();
            }
        });
        simpleAutoCompleteTextView = findViewById(R.id.simpleAutoCompleteTextView);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (simpleAutoCompleteTextView.getText().toString().equals("")) {
                    simpleAutoCompleteTextView.requestFocus();
                    Toast.makeText(AddClientActivity1.this, "Enter Company name", Toast.LENGTH_SHORT).show();
                } else if (imageUri == null) {
                    Toast.makeText(AddClientActivity1.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                } else {
                    AddClientApi();
                }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Client");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
       /*  simpleAutoCompleteTextView =  findViewById(R.id.simpleAutoCompleteTextView);

        String AutoCompleteTextViewValue = simpleAutoCompleteTextView.getText().toString();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNameList);

        simpleAutoCompleteTextView.setAdapter(adapter);
        simpleAutoCompleteTextView.setThreshold(1);//start searching from 1 character
        simpleAutoCompleteTextView.setAdapter(adapter);
       *//* ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, mobileArray);*//*
*/
        listView = (ListView) findViewById(R.id.mobile_list);
        getCompanyListData();
       /* CustomAdapter customAdapter = new CustomAdapter(AddClientActivity.this, modelList);
        listView.setAdapter(customAdapter);*/
    }

    private void getCompanyListData() {
        String URL = BASEURL + "client/list?company_id=" + company_id+"&user_id="+user_id;
        final ProgressDialog pDialog = new ProgressDialog(AddClientActivity1.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.show();
        pDialog.setContentView(R.layout.custom_progress_bar);

        Log.e(TAG, "loginSevice:urlData=== " + URL);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, response.toString());

                try {
                    // System.out.println("JSON RETURN " + response.toString());

                    String data = String.valueOf(response.get("data"));
                    String message = String.valueOf(response.get("message"));
                    int status = response.getInt("status");

                    if (status == 0) {
                        final JSONArray arrayObj = new JSONArray(data);


                        //   Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < arrayObj.length(); i++) {

                            JSONObject jsonObject = arrayObj.getJSONObject(i);

//                          JSONObject checkINresponce = response.getJSONObject("data");
                            ClientListModel loginModel = new ClientListModel();
                            loginModel.setName(jsonObject.getString("name"));
                            loginModel.setImage(jsonObject.getString("image"));
                            loginModel.setClient_id(jsonObject.getString("client_id"));
                            modelList.add(loginModel);
                        }


                        CustomAdapter customAdapter = new CustomAdapter(AddClientActivity1.this, modelList, user_id);
                        listView.setAdapter(customAdapter);


                       /* Intent intentTow = new Intent(AddClientActivity.this, VerifyPhoneActivity.class);
                        intentTow.putExtra("name", loginModel.getName());
                        intentTow.putExtra("client_id", loginModel.getClient_id());
                        startActivity(intentTow);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
*/

                        pDialog.dismiss();


                    } else {
                        Toast.makeText(AddClientActivity1.this, message, Toast.LENGTH_SHORT).show();
                    }
                    pDialog.dismiss();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(AddClientActivity1.this, "Unexpected Error...", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    //sendError(e.toString(), "user/login?username=");
                    Toast.makeText(AddClientActivity1.this, "Technical Error...", Toast.LENGTH_SHORT).show();
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(AddClientActivity1.this);
                    builder.setMessage("Time Out");
                    builder.setPositiveButton("ReTry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (AppConstants.isInternetAvailable(AddClientActivity1.this)) {
                                // loginSevice(username);
                            } else {
                                Toast.makeText(AddClientActivity1.this, "Internet Connection Required", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AddClientActivity1.this, "Server Error", Toast.LENGTH_SHORT).show();
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

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        // Note: apps running on "O" devices (regardless of targetSdkVersion) may receive updates
        // less frequently than this interval when the app is no longer in the foreground.
        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);
    }


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(AddClientActivity1.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.

            } else {

            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (data != null) {
                    onCaptureImageResult(data);
                } else {
                    Toast.makeText(AddClientActivity1.this, "Take ID Image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Uri selectedImageUri = getImageUri(getActivity(), thumbnail);
        //selectedPath = getRealPathFromURI(selectedImageUri);
        imageCompany.setVisibility(View.VISIBLE);
        imageUri = Uri.fromFile(destination);
        imageUri1 = Uri.fromFile(destination);
        selectedPath = destination.getAbsolutePath();

        // Toast.makeText(getActivity(), destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        imageCompany.setImageBitmap(thumbnail);
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= 19;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }


            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void AddClientApi() {
        String urlData = AppConstants.BASEURL + "client/add";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.custom_progress_bar);


        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlData,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        String resultResponse = new String(response.data);
                        try {

                            JSONObject result = new JSONObject(resultResponse);
                            //int res = result.getInt("RESPONSECODE");
                            Log.e(TAG, "onResponse:=== " + response.toString());
                            System.out.println("JSON RETURN " + response.toString());
                            String message = String.valueOf(result.get("message"));
                            int status = result.getInt("status");
                            String response1 = String.valueOf(result.get("data"));


                            if (status == 0) {
                                simpleAutoCompleteTextView.setText("");
                                imageCompany.setImageResource(R.drawable.ic_menu_camera);
                                getCompanyListData();
                               /* idPhoto.setVisibility(View.VISIBLE);
                                JSONObject loginresponce = result.getJSONObject("data");
                                receiptHeading = loginresponce.getString("receiptHeading");
                                parkingAddress = loginresponce.getString("parkingAddress");
                                vehicleNo = loginresponce.getString("vehicleNo");
                                checkInDate = loginresponce.getString("checkInDate");
                                checkOutDate = loginresponce.getString("checkOutDate");
                                duration = loginresponce.getString("duration");
                                durationUnit = loginresponce.getString("durationUnit");
                                currencySymbol = loginresponce.getString("currencySymbol");
                                grandTotal = loginresponce.getString("grandTotal");
                                poweredBy = loginresponce.getString("poweredBy");
                                companyWebsite = loginresponce.getString("companyWebsite");
                                receiptNo = loginresponce.getString("receiptNo");
                                extraChargeText = loginresponce.getString("extraChargeText");
                                printReceipt = loginresponce.getString("printReceipt");
                           */     // cleareText();
                                // Picasso.with(AddClientActivity.this).load(R.mipmap.police_car512).into(idPhoto);
                                pDialog.dismiss();
                                // String respo = result.getString("RESPONSE");
                                Toast.makeText(AddClientActivity1.this, message, Toast.LENGTH_SHORT).show();


                            } else {
                                pDialog.dismiss();
                                // cleareText();
                                Toast.makeText(AddClientActivity1.this, message, Toast.LENGTH_SHORT).show();
                            }

                            //  Toast.makeText(getApplicationContext(), result.toString(), Toast.LENGTH_SHORT);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            //sendError(e.toString(), "parking/checkout_lost");

                            pDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                        // sendError(errorMessage, "parking/checkout_lost");

                        pDialog.dismiss();
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                        //sendError(errorMessage, "parking/checkout_lost");

                        pDialog.dismiss();
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                            //   sendError(errorMessage, "parking/checkout_lost");
                            pDialog.dismiss();
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message + " Please login again";
                            //  sendError(errorMessage, "parking/checkout_lost");
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message + " Check your inputs";
                            //  sendError(errorMessage, "parking/checkout_lost");
                            pDialog.dismiss();
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message + " Something is getting wrong";
                            //  sendError(errorMessage, "parking/checkout_lost");
                            pDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        pDialog.dismiss();
                        // sendError(e.toString(), "parking/checkout_lost");
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                pDialog.dismiss();
                error.printStackTrace();

            }
        }) {
            @Override
            protected java.util.Map<String, String> getParams() {

                //  String user_id = ConstantData.getString(getApplicationContext(),  USER_ID, "");
                //String vehicleNumber = etStateCode.getText().toString() + etCityCode.getText().toString() + etVehicleCode.getText().toString() + etVehicleNumber.getText().toString();

                java.util.Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("name", simpleAutoCompleteTextView.getText().toString().trim());
                params.put("latitude", stringLatitude);
                params.put("longitude", stringLongitude);
                params.put("company_id", company_id);

                Log.e("===============", "getParams:params " + params);
//                params.put("operatorId", agentId);
//                params.put("vendorId", vendorId);


                //.addFileToUpload(path, "file") //Adding file
                return params;
            }

            @Override
            protected java.util.Map<String, DataPart> getByteData() {

                java.util.Map<String, DataPart> params = new HashMap<>();
                String uploadId = UUID.randomUUID().toString();
                try {
                    InputStream iStream = getApplicationContext().getContentResolver().openInputStream(imageUri1);
                    byte[] inputData = getBytes(iStream);
                    params.put("image", new DataPart(uploadId + ".jpg", inputData, "image/*"));

                    Log.e("===============", "getParams:params " + params);
//
                } catch (IOException e) {
                    e.printStackTrace();
                    // sendError(e.toString(), "parking/checkout_lost");
                }
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(multipartRequest);


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Main2Activity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
