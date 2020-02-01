package com.daffodil.officeproject.HomeModule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.daffodil.officeproject.R;
import com.daffodil.officeproject.base.AppConstants;
import com.daffodil.officeproject.base.VolleyMultipartRequest;
import com.daffodil.officeproject.base.VolleySingleton;
import com.daffodil.officeproject.others.DBHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;

public class PunchInActivityNew extends AppCompatActivity implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final String TAG = "PunchInActivity";
    private GoogleMap mMap;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private static final int LOCATION_REQUEST_CODE = 101;
    LinearLayout linearFindMe;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    TextView TvTimeIn, tvDate, tvAddress, TvTimeout;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE1 = 201;
    DBHelper mydb;
    SharedPreferences sharedpref;
    String mobile, user_id, otp, user_role, user_name, company_id, company_name;
    CheckBox checkLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch_in);
        mydb = new DBHelper(this);
        ArrayList array_list = mydb.getAllCotacts();
        initData();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        findViewByIdMethod();

        //

        // fetchLastLocation();
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
        try {
            Strclickcount1 = sharedpref.getInt("clickcount1", 0);
            Strclickcount = sharedpref.getInt("clickcount", 0);
            Log.e(TAG, "initData:clickcount1== " + Strclickcount1);
            Log.e(TAG, "initData:clickcount== " + Strclickcount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertData() {
        //String name, String phone, String place, String lati, String longi, String image
        boolean id = mydb.insertContact("name", "8109378332", tvAddress.getText().toString(), currentLocation.getLatitude(), currentLocation.getLongitude(), photo);

        // get the newly inserted note from db
        Cursor rs = mydb.getData(1);
        rs.moveToFirst();

        String nam = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
        String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
        String plac = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CITY));
        String LATI = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_LATI));
        String LONGI = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_LONGI));
        String IMAGE = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_IMAGE));
        String timestamp = rs.getString(rs.getColumnIndex(DBHelper.COLUMN_TIMESTAMP));
        Log.e(TAG, "insertData: =====" + nam);
        Log.e(TAG, "insertData: ==========" + phon);
        Log.e(TAG, "insertData: ======" + plac);
        Log.e(TAG, "insertData: ====" + LATI);
        Log.e(TAG, "insertData: ====" + LONGI);
        Log.e(TAG, "insertData: =====" + IMAGE);
        if (!rs.isClosed()) {
            rs.close();
        }
    }

    private void getDbData() {
        Cursor rs = mydb.getData(1);
        //id_To_Update = Value;
        rs.moveToFirst();

        String nam = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_NAME));
        String phon = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_PHONE));
        String plac = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_CITY));
        String LATI = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_LATI));
        String LONGI = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_LONGI));
        String IMAGE = rs.getString(rs.getColumnIndex(DBHelper.CONTACTS_COLUMN_IMAGE));
        String timestamp = rs.getString(rs.getColumnIndex(DBHelper.COLUMN_TIMESTAMP));
        Log.e(TAG, "insertData: =====" + nam);
        Log.e(TAG, "insertData: ==========" + phon);
        Log.e(TAG, "insertData: ======" + plac);
        Log.e(TAG, "insertData: ====" + LATI);
        Log.e(TAG, "insertData: ====" + LONGI);
        Log.e(TAG, "insertData: =====" + IMAGE);
        if (!rs.isClosed()) {
            rs.close();
        }
    }

    private void findViewByIdMethod() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Punch In");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkLocation = findViewById(R.id.checkLocation);
        tvDate = findViewById(R.id.tvDate);
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        String sDate = c.get(Calendar.DAY_OF_MONTH) + "-" + month + "-" + c.get(Calendar.YEAR);
// textView is the TextView view that should display it
        tvDate.setText("Date : " + sDate);
        // tvDate.setText("");
        tvAddress = findViewById(R.id.tvAddress);
        TvTimeIn = findViewById(R.id.TvTimeIn);
        TvTimeout = findViewById(R.id.TvTimeout);
        linearFindMe = findViewById(R.id.linearFindMe);

        TvTimeIn.setOnClickListener(this);
        TvTimeout.setOnClickListener(this);
        linearFindMe.setOnClickListener(this);

        if (Strclickcount == 1) {
            TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);

        }
        if (Strclickcount1 == 1) {
            TvTimeout.setBackgroundResource(R.drawable.bg_white_rounded_grreen);

        }
        if (ActivityCompat.checkSelfPermission(PunchInActivityNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PunchInActivityNew.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                } else {
                    Toast.makeText(PunchInActivityNew.this, "Location permission missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REQUEST_CODE:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
            case PERMISSION_REQUEST_CODE1:
                if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
            case 5:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 12);
                }
                break;
            case 6:
                Intent takePictureIntentt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntentt.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntentt, 12);
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(PunchInActivityNew.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    Bitmap photo;
    Uri imageUri;
    Uri imageUri1 = Uri.parse("");
    private String selectedPath = "";

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    //imageView.setImageBitmap(photo);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

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
                    // imageCompany.setVisibility(View.VISIBLE);
                    imageUri = Uri.fromFile(destination);
                    imageUri1 = Uri.fromFile(destination);
                    selectedPath = destination.getAbsolutePath();
                    type = "in";
                    if (imageUri1 == null) {

                        Toast.makeText(PunchInActivityNew.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                        // openCameraIntent();
                    } else {
                        TimeInAPi(type);
                    }
                    Log.e(TAG, "onCaptureImageResult: ----imageUri1---------" + imageUri1);
                    Log.e(TAG, "onCaptureImageResult: -------imageUri-------" + imageUri);
                    //Toast.makeText(AddClientActivity.this, destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    //  imageCompany.setImageBitmap(photo);
                    //  imageCompany.setVisibility(View.VISIBLE);

                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                    File destination = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");

                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Uri selectedImageUri = getImageUri(getActivity(), thumbnail);
                    //selectedPath = getRealPathFromURI(selectedImageUri);
                    // imageCompany.setVisibility(View.VISIBLE);
                    imageUri = Uri.fromFile(destination);
                    imageUri1 = Uri.fromFile(destination);
                    selectedPath = destination.getAbsolutePath();
                    Log.e(TAG, "onCaptureImageResult: ----imageUri1---------" + imageUri1);
                    Log.e(TAG, "onCaptureImageResult: -------imageUri-------" + imageUri);
                    type = "out";
                    if (imageUri1 == null) {

                        Toast.makeText(PunchInActivityNew.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                        // openCameraIntent();
                    } else {
                        TimeInAPi(type);
                    }

                }
                break;
        }
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (imageReturnedIntent != null) {
                    onCaptureImageResult(imageReturnedIntent);
                } else {
                    Toast.makeText(PunchInActivityNew.this, "Take ID Image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onCaptureImageResult(Intent imageReturnedIntent) {
        Bitmap thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");
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
        imageUri = Uri.fromFile(destination);
        imageUri1 = Uri.fromFile(destination);
        selectedPath = destination.getAbsolutePath();
        Log.e(TAG, "onCaptureImageResult: -------------" + imageUri1);
        Log.e(TAG, "onCaptureImageResult: --------------" + imageUri);
        //Toast.makeText(AddClientActivity.this, destination.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    updateAddress(currentLocation);
                    LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                   /* Log.e(TAG, "onLocationChanged: " + currentLocation.getLatitude());
                    Log.e(TAG, "onLocationChanged: " + currentLocation.getLongitude());
                   */
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Current Position");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    // mCurrLocationMarker = mMap.addMarker(markerOptions);
                    //move map camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                    //stop location updates

                    // Toast.makeText(PunchInActivity.this, currentLocation.getLatitude() + " " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
                    supportMapFragment.getMapAsync(PunchInActivityNew.this);
                } else {
                    Toast.makeText(PunchInActivityNew.this, "No Location recorded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Place current location marker

    }

    String AddressMain;

    private void updateAddress(Location currentLocation) {
        Log.e(TAG, "updateAddress: " + currentLocation.getLatitude());
        Log.e(TAG, "updateAddress: " + currentLocation.getLongitude());
        double Latitude = currentLocation.getLatitude();
        double Longitude = currentLocation.getLongitude();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            AddressMain = address + " , " + city + " , " + state + " , " + country + " , " + postalCode + " , " + knownName;
            tvAddress.setText("" + address + " , " + city + " , " + state + " , " + country + " , " + postalCode + " , " + knownName);
            insertData();
            getDbData();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.e(TAG, "onLocationChanged: " + location.getLatitude());
        Log.e(TAG, "onLocationChanged: " + location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    static final Integer CAMERA = 0x5;
    static final Integer CAMERA1 = 0x6;
    boolean current_location = false;
    String type;
    boolean click = false;
    int clickcount = 0;
    int clickcount1 = 0;
    int Strclickcount = 0;
    int Strclickcount1 = 0;
    SharedPreferences.Editor ed;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.TvTimeIn:


                clickcount = clickcount + 1;
                if (clickcount == 1) {
                    sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
                    ed = sharedpref.edit();
                    ed.putInt("clickcount", clickcount);
                    ed.apply();
                    ed.commit();
                    type = "in";
                    if (checkLocation.isChecked()) {
                        current_location = true;
                    } else {
                        current_location = false;

                    }
                    askForPermission(Manifest.permission.CAMERA, CAMERA);
                    if (checkPermission()) {
                        openCameraIntent();
                        Log.e(TAG, "onClick: imageUri1 in======" + imageUri1);
                        /*if (imageUri1 == null) {

                            Toast.makeText(PunchInActivity.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                            //openCameraIntent();
                        } else {
                            TimeInAPi(type);
                        }
*/
                        if (imageUri1 == null) {

                            Toast.makeText(PunchInActivityNew.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                            openCameraIntentOut();
                        } else {
                            TimeInAPi(type);
                        }
                    } else {
                        requestPermission();
                    }

                    //TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);

                } else {

                }


                break;
            case R.id.TvTimeout:
                clickcount1 = clickcount1 + 1;
                if (clickcount1 == 1) {
                    sharedpref = getSharedPreferences("opark", Context.MODE_PRIVATE);
                    ed = sharedpref.edit();
                    ed.putInt("clickcount1", clickcount1);
                    ed.apply();
                    ed.commit();
                    type = "out";
                    askForPermission(Manifest.permission.CAMERA, CAMERA1);
                    if (checkPermission()) {
                        openCameraIntentOut();
                        Log.e(TAG, "onClick: imageUri1     out======" + imageUri1);

                        if (imageUri1 == null) {

                            Toast.makeText(PunchInActivityNew.this, "Take client Photo", Toast.LENGTH_SHORT).show();
                            // openCameraIntent();
                        } else {
                            TimeInAPi(type);
                        }
                        // TvTimeout.setBackgroundResource(R.drawable.bg_white_rounded_grreen);

                    } else {
                        requestPermission();
                    }

                } else {

                }

                break;
            case R.id.linearFindMe:
                fetchLastLocation();
                break;
        }
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void TimeInAPi(final String type) {
        /*http://daffodillab.in/trackerpgpl/index.php/v1/user/attendence*/
        //user_id,address,image,latitude,longitude,company_id,type,current_location
        String urlData = AppConstants.BASEURL + "user/attendence";
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(true);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.show();
        pDialog.setCancelable(false);
        pDialog.setContentView(R.layout.custom_progress_bar);
        Log.e(TAG, "TimeInAPi: urlData======" + urlData);

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

                            Log.e(TAG, "message:=== " + message);

                            if (status == 0) {


                                pDialog.dismiss();
                                // String respo = result.getString("RESPONSE");
                                Toast.makeText(PunchInActivityNew.this, message, Toast.LENGTH_SHORT).show();
                                if (type.equals("in")) {
                                    TvTimeIn.setBackgroundResource(R.drawable.bg_white_rounded_grreen);

                                }
                                if (type.equals("out")) {
                                    TvTimeout.setBackgroundResource(R.drawable.bg_white_rounded_grreen);
                                }


                            } else {
                                pDialog.dismiss();
                                // cleareText();
                                Toast.makeText(PunchInActivityNew.this, message, Toast.LENGTH_SHORT).show();
                            }

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
            protected Map<String, String> getParams() {

                //  String user_id = ConstantData.getString(getApplicationContext(),  USER_ID, "");
                //String vehicleNumber = etStateCode.getText().toString() + etCityCode.getText().toString() + etVehicleCode.getText().toString() + etVehicleNumber.getText().toString();

                Map<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("address", AddressMain);
                params.put("latitude", String.valueOf(currentLocation.getLatitude()));
                params.put("longitude", String.valueOf(currentLocation.getLongitude()));
                params.put("company_id", company_id);
                params.put("type", type);
                params.put("current_location", String.valueOf(current_location));

                Log.e("===============", "getParams:params " + params);
//                params.put("operatorId", agentId);
//                params.put("vendorId", vendorId);


                //.addFileToUpload(path, "file") //Adding file
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {

                Map<String, DataPart> params = new HashMap<>();
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

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(PunchInActivityNew.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PunchInActivityNew.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PunchInActivityNew.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(PunchInActivityNew.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private static final int REQUEST_CAPTURE_IMAGE = 100;

    private void openCameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    private void openCameraIntentOut() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 1);
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }
}
