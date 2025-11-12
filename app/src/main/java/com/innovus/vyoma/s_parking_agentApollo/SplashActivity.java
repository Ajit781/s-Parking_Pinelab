package com.innovus.vyoma.s_parking_agentApollo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;


import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import data_objects.SParkingAgentModel;
import db.DatabaseHandler;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.SessionManager;
import utilities.constants.Urls;
import utilities.others.ConnectionStatus;

public class SplashActivity extends AppCompatActivity implements AsyncResponse {

    private static final boolean AUTO_HIDE = true;
    SharedPreferences sharedpreferences;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    private SessionManager session;
    RemoteAsync remoteAsync;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private String version_name = "";

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {

            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            /*hide();*/
        }
    };


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initGoogleAPIClient();//Init Google API Client

        initViews();

    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        mGoogleApiClient = new GoogleApiClient.Builder(SplashActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();

        }

    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(SplashActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (mGoogleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {
                    Toast.makeText(SplashActivity.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }


    private void initViews() {

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Integer uid = sharedpreferences.getInt("UID", 0);
        final String strFullName = sharedpreferences.getString("UserName", "");

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.VISIBLE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    protected void onResume() {

        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
        session = new SessionManager(getApplicationContext());
        /*prepareObjectAnimator(anticipateOvershootInterpolator);*/
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            Log.e("packageInfo", String.valueOf(packageInfo));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version_code = packageInfo.versionCode;
        version_name = packageInfo.versionName;
        Log.i("updated version code", String.valueOf(version_code) + "  " + version_name);
        SharedStorage.setValue(getApplicationContext(),"versionname",version_name);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(!SharedStorage.getValue(getApplicationContext(),"baseUrl").equals("")){
            dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");
        }
        else{
            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())){

                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

            }else{
                ShowAlertDialog.showAlertDialog(SplashActivity.this,getResources().getString(R.string.NoInternetConnection));

            }

        }

        if(!SharedStorage.getValue(getApplicationContext(),"licence_renewdate").equals("")){

            try {
                Date estimated_date = dateFormat.parse(SharedStorage.getValue(getApplicationContext(),"licence_renewdate"));
                Date current_date = new Date();

                System.out.println("Current Date " + dateFormat.format(current_date));
                System.out.println("Current Date " + dateFormat.format(estimated_date));
                long diff = 0;

                Log.e("splash", "onResume: "+String.valueOf(diff));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    diff = betweenDates(current_date,estimated_date);
                }else {
                    diff = betweenDateslowversion(current_date,estimated_date);
                }


                if(diff > 7){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if (session.isLoggedIn()) {

                                if(SharedStorage.getValue(getApplicationContext(),"billstatus").equals("1")){
                                    dataModel.details_shown="1";

                                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                                    databaseHandler.getbill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                                    String bookingno="",BookingID="",checkintime="",checkouttime="",ownerphoneno=""
                                            ,totalamount="",ParkingAreaName="",TotalDuration="",TotalParkingAmount="",TotalPaybleAmount="",OfferAmount=""
                                            ,FineAmount="",PaymentMode="",AgencyName="",VehicleType="",bookingstatus="",vechile_no="",
                                            overtime_duration="",overtime_amount="",message="";

                                    if(dataModel.bookingBillBean != null){
                                        bookingno = dataModel.bookingBillBean.getBookingno();
                                        BookingID = dataModel.bookingBillBean.getBookingID();
                                        checkintime = dataModel.bookingBillBean.getCheckintime();
                                        checkouttime =dataModel.bookingBillBean.getCheckouttime();
                                        ownerphoneno = dataModel.bookingBillBean.getOwnerphoneno();
                                        VehicleType = dataModel.bookingBillBean.getVehicleType();
                                        vechile_no = dataModel.bookingBillBean.getVechile_no();
                                        ParkingAreaName = dataModel.bookingBillBean.getParkingAreaName();
                                        TotalDuration = dataModel.bookingBillBean.getTotalDuration();
                                        TotalParkingAmount = dataModel.bookingBillBean.getTotalParkingAmount();
                                        TotalPaybleAmount = dataModel.bookingBillBean.getTotalPaybleAmount();
                                        FineAmount = dataModel.bookingBillBean.getFineAmount();
                                        OfferAmount = dataModel.bookingBillBean.getOfferAmount();
                                        PaymentMode = dataModel.bookingBillBean.getPaymentMode();
                                        AgencyName = dataModel.bookingBillBean.getAgencyName();
                                        overtime_duration = dataModel.bookingBillBean.getOverTimeDuration();
                                        overtime_amount = dataModel.bookingBillBean.getOverTimeAmount();
                                        message = dataModel.bookingBillBean.getMessage();
                                        Log.e("PaymentMode",PaymentMode);



                                    }

                                    Intent intent = new Intent(SplashActivity.this,BillGenerateActivity.class);
                                    intent.putExtra("bookingno",bookingno);
                                    intent.putExtra("BookingID",BookingID);
                                    intent.putExtra("checkintime",checkintime);
                                    intent.putExtra("checkouttime",checkouttime);
                                    intent.putExtra("ownerphoneno",ownerphoneno);
                                    //intent.putExtra("totalamount",totalamount);
                                    intent.putExtra("ParkingAreaName",ParkingAreaName);
                                    intent.putExtra("TotalDuration",TotalDuration);
                                    intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                                    intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                                    intent.putExtra("OfferAmount",OfferAmount);
                                    intent.putExtra("FineAmount",FineAmount);
                                    intent.putExtra("PaymentMode",PaymentMode);
                                    intent.putExtra("VehicleType",VehicleType);
                                    //intent.putExtra("bookingstatus",bookingstatus);
                                    intent.putExtra("vechile_no",vechile_no);
                                    intent.putExtra("AgencyName",AgencyName);
                                    intent.putExtra("overtime_duration",overtime_duration);
                                    intent.putExtra("overtime_amount",overtime_amount);
                                    intent.putExtra("message",message);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                }else {
                                    dataModel.details_shown="1";
                                    startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                }

                            }else{
                                dataModel.details_shown="";
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            }

                        }
                    }, 3000);
                }else{
                    if(diff>0){
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.ok));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.licencence_warning_msg));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // User is already logged in. Take him to main activity

                                        if (session.isLoggedIn()) {
                                            // User is already logged in. Take him to main activity

                                            if(SharedStorage.getValue(getApplicationContext(),"billstatus").equals("1")){
                                                dataModel.details_shown="1";

                                                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                                                databaseHandler.getbill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                                                String bookingno="",BookingID="",checkintime="",checkouttime="",ownerphoneno=""
                                                        ,totalamount="",ParkingAreaName="",TotalDuration="",TotalParkingAmount="",TotalPaybleAmount="",OfferAmount=""
                                                        ,FineAmount="",PaymentMode="",AgencyName="",VehicleType="",bookingstatus="",vechile_no="",
                                                        overtime_duration="",overtime_amount="",message="";

                                                if(dataModel.bookingBillBean != null){
                                                    bookingno = dataModel.bookingBillBean.getBookingno();
                                                    BookingID = dataModel.bookingBillBean.getBookingID();
                                                    checkintime = dataModel.bookingBillBean.getCheckintime();
                                                    checkouttime =dataModel.bookingBillBean.getCheckouttime();
                                                    ownerphoneno = dataModel.bookingBillBean.getOwnerphoneno();
                                                    VehicleType = dataModel.bookingBillBean.getVehicleType();
                                                    vechile_no = dataModel.bookingBillBean.getVechile_no();
                                                    ParkingAreaName = dataModel.bookingBillBean.getParkingAreaName();
                                                    TotalDuration = dataModel.bookingBillBean.getTotalDuration();
                                                    TotalParkingAmount = dataModel.bookingBillBean.getTotalParkingAmount();
                                                    TotalPaybleAmount = dataModel.bookingBillBean.getTotalPaybleAmount();
                                                    FineAmount = dataModel.bookingBillBean.getFineAmount();
                                                    OfferAmount = dataModel.bookingBillBean.getOfferAmount();
                                                    PaymentMode = dataModel.bookingBillBean.getPaymentMode();
                                                    AgencyName = dataModel.bookingBillBean.getAgencyName();
                                                    overtime_duration = dataModel.bookingBillBean.getOverTimeDuration();
                                                    overtime_amount = dataModel.bookingBillBean.getOverTimeAmount();
                                                    message = dataModel.bookingBillBean.getMessage();
                                                    Log.e("PaymentMode",PaymentMode);
                                                }

                                                Intent intent = new Intent(SplashActivity.this,BillGenerateActivity.class);
                                                intent.putExtra("bookingno",bookingno);
                                                intent.putExtra("BookingID",BookingID);
                                                intent.putExtra("checkintime",checkintime);
                                                intent.putExtra("checkouttime",checkouttime);
                                                intent.putExtra("ownerphoneno",ownerphoneno);
                                                //intent.putExtra("totalamount",totalamount);
                                                intent.putExtra("ParkingAreaName",ParkingAreaName);
                                                intent.putExtra("TotalDuration",TotalDuration);
                                                intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                                                intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                                                intent.putExtra("OfferAmount",OfferAmount);
                                                intent.putExtra("FineAmount",FineAmount);
                                                intent.putExtra("PaymentMode",PaymentMode);
                                                intent.putExtra("VehicleType",VehicleType);
                                                //intent.putExtra("bookingstatus",bookingstatus);
                                                intent.putExtra("vechile_no",vechile_no);
                                                intent.putExtra("AgencyName",AgencyName);
                                                intent.putExtra("overtime_duration",overtime_duration);
                                                intent.putExtra("overtime_amount",overtime_amount);
                                                intent.putExtra("message",message);
                                                startActivity(intent);
                                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                finish();
                                            }else {
                                                dataModel.details_shown="1";
                                                startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                                finish();
                                            }

                                        }else{
                                            dataModel.details_shown="";
                                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                            finish();
                                        }

                                    }
                                }, 3000);

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }else {
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.retry));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.licencence_msg));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            // Internet connection checking
            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())){

                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

            }
            else {


                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                alertDialog.setView(dialogView);
                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                btnOk.setText(getResources().getString(R.string.retry));

                heading.setText(R.string.validation_name);

                // Setting Dialog Message

                msg_txt.setText(getResources().getString(R.string.no_internet));

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                    }
                });
                //Animate alert dialog box
                FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                // Showing Alert Message
                alertDialog.show();
                alertDialog.setCancelable(false);

            }
        }

        super.onResume();
    }
    // Request service for App version checking
    private void GenerateAuthToken() {
        Urls Urls = new Urls();
        String login_url = Urls.GenerateAuthToken;

        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GENERATEAUTHTOKEN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "userid=" + URLEncoder.encode("AndroidApp", "UTF-8") +
                    "&password=" + URLEncoder.encode("vyoma@123", "UTF-8");
        }catch(Exception e){
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    private void CheckAppsVersion(String packagename,String versionname) {
        //String login_url = Urls.CheckAppsVersion+"/1/"+packagename+"/"+versionname;
        String login_url = Urls.CheckAppVersion;


        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.CHECKAPPVERSION;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {

            urlParams = "Package=" + URLEncoder.encode(packagename, "UTF-8") +
                    "&Version=" + URLEncoder.encode(versionname, "UTF-8");
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.CHECKAPPVERSION)) {
            try {
                JSONObject obj = new JSONObject(output);

                Log.e("Response-->", obj.toString());
                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    String versioncode= SharedStorage.getValue(getApplicationContext(),"versionname");
                    //GenerateAuthToken();
                    if(versioncode.equals(obj.getString("ServerVersion"))){
                        SharedStorage.setValue(getApplicationContext(),"baseUrl",obj.getString("base_url"));
                      // SharedStorage.setValue(getApplicationContext(),"baseUrl","http://ec2-13-233-106-250.ap-south-1.compute.amazonaws.com/api/");

                        dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // User is already logged in. Take him to main activity

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = new Date();
                                System.out.println("Current Date " + dateFormat.format(date));

                                // Convert Date to Calendar
                                Calendar c = Calendar.getInstance();
                                c.setTime(date);

                                // Perform addition/subtraction
                                c.add(Calendar.DATE, 45);

                                // Convert calendar back to Date
//                                Date currentDatePlusOne = c.getTime();
//
//                                System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));
                                Date currentDatePlusOne = null;
                                try {
                                    currentDatePlusOne = dateFormat.parse(obj.getString("valid_upto"));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));

                                SharedStorage.setValue(getApplicationContext(),"licence_renewdate",dateFormat.format(currentDatePlusOne));

                                if (session.isLoggedIn()) {
                                    // User is already logged in. Take him to main activity

                                    if(SharedStorage.getValue(getApplicationContext(),"billstatus").equals("1")){
                                        dataModel.details_shown="1";

                                        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                                        databaseHandler.getbill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                                        String bookingno="",BookingID="",checkintime="",checkouttime="",ownerphoneno=""
                                                ,totalamount="",ParkingAreaName="",TotalDuration="",TotalParkingAmount="",TotalPaybleAmount="",OfferAmount=""
                                                ,FineAmount="",PaymentMode="",AgencyName="",VehicleType="",bookingstatus="",vechile_no="",
                                                overtime_duration="",overtime_amount="",message="";

                                        if(dataModel.bookingBillBean != null){
                                            bookingno = dataModel.bookingBillBean.getBookingno();
                                            BookingID = dataModel.bookingBillBean.getBookingID();
                                            checkintime = dataModel.bookingBillBean.getCheckintime();
                                            checkouttime =dataModel.bookingBillBean.getCheckouttime();
                                            ownerphoneno = dataModel.bookingBillBean.getOwnerphoneno();
                                            VehicleType = dataModel.bookingBillBean.getVehicleType();
                                            vechile_no = dataModel.bookingBillBean.getVechile_no();
                                            ParkingAreaName = dataModel.bookingBillBean.getParkingAreaName();
                                            TotalDuration = dataModel.bookingBillBean.getTotalDuration();
                                            TotalParkingAmount = dataModel.bookingBillBean.getTotalParkingAmount();
                                            TotalPaybleAmount = dataModel.bookingBillBean.getTotalPaybleAmount();
                                            FineAmount = dataModel.bookingBillBean.getFineAmount();
                                            OfferAmount = dataModel.bookingBillBean.getOfferAmount();
                                            PaymentMode = dataModel.bookingBillBean.getPaymentMode();
                                            AgencyName = dataModel.bookingBillBean.getAgencyName();
                                            overtime_duration = dataModel.bookingBillBean.getOverTimeDuration();
                                            overtime_amount = dataModel.bookingBillBean.getOverTimeAmount();
                                            message = dataModel.bookingBillBean.getMessage();
                                            Log.e("PaymentMode",PaymentMode);
                                        }

                                        Intent intent = new Intent(SplashActivity.this,BillGenerateActivity.class);
                                        intent.putExtra("bookingno",bookingno);
                                        intent.putExtra("BookingID",BookingID);
                                        intent.putExtra("checkintime",checkintime);
                                        intent.putExtra("checkouttime",checkouttime);
                                        intent.putExtra("ownerphoneno",ownerphoneno);
                                        //intent.putExtra("totalamount",totalamount);
                                        intent.putExtra("ParkingAreaName",ParkingAreaName);
                                        intent.putExtra("TotalDuration",TotalDuration);
                                        intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                                        intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                                        intent.putExtra("OfferAmount",OfferAmount);
                                        intent.putExtra("FineAmount",FineAmount);
                                        intent.putExtra("PaymentMode",PaymentMode);
                                        intent.putExtra("VehicleType",VehicleType);
                                        //intent.putExtra("bookingstatus",bookingstatus);
                                        intent.putExtra("vechile_no",vechile_no);
                                        intent.putExtra("AgencyName",AgencyName);
                                        intent.putExtra("overtime_duration",overtime_duration);
                                        intent.putExtra("overtime_amount",overtime_amount);
                                        intent.putExtra("message",message);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        finish();
                                    }else {
                                        dataModel.details_shown="1";
                                        startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                        finish();
                                    }

                                }else{
                                    dataModel.details_shown="";
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                }

                            }
                        }, 3000);
                    }else{
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.retry));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        msg_txt.setText(getResources().getString(R.string.version_notmatching));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }

                }
                else if(obj.getString("status").equals(Constants.FAILED)){


                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.ok));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.licencence_msg));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);
                }
                else if(obj.getString("status").equals(Constants.PASS)){
                    SharedStorage.setValue(getApplicationContext(),"baseUrl",obj.getString("base_url"));
                     // SharedStorage.setValue(getApplicationContext(),"baseUrl","http://sparkingservice.mine.nu/api/");
                    dataModel.url= SharedStorage.getValue(getApplicationContext(),"baseUrl");

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            System.out.println("Current Date " + dateFormat.format(date));

                            // Convert Date to Calendar
                            Calendar c = Calendar.getInstance();
                            c.setTime(date);

                            // Perform addition/subtraction
                            c.add(Calendar.DATE, 45);

                            // Convert calendar back to Date
//                            Date currentDatePlusOne = c.getTime();
//
//                            System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));
                            Date currentDatePlusOne = null;
                            try {
                                currentDatePlusOne = dateFormat.parse(obj.getString("valid_upto"));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("Updated Date " + dateFormat.format(currentDatePlusOne));


                            SharedStorage.setValue(getApplicationContext(),"licence_renewdate",dateFormat.format(currentDatePlusOne));

                            if (session.isLoggedIn()) {
                                // User is already logged in. Take him to main activity

                                if(SharedStorage.getValue(getApplicationContext(),"billstatus").equals("1")){
                                    dataModel.details_shown="1";

                                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                                    databaseHandler.getbill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                                    String bookingno="",BookingID="",checkintime="",checkouttime="",ownerphoneno=""
                                            ,totalamount="",ParkingAreaName="",TotalDuration="",TotalParkingAmount="",TotalPaybleAmount="",OfferAmount=""
                                            ,FineAmount="",PaymentMode="",AgencyName="",VehicleType="",bookingstatus="",vechile_no="",
                                            overtime_duration="",overtime_amount="",message="";

                                    if(dataModel.bookingBillBean != null){
                                        bookingno = dataModel.bookingBillBean.getBookingno();
                                        BookingID = dataModel.bookingBillBean.getBookingID();
                                        checkintime = dataModel.bookingBillBean.getCheckintime();
                                        checkouttime =dataModel.bookingBillBean.getCheckouttime();
                                        ownerphoneno = dataModel.bookingBillBean.getOwnerphoneno();
                                        VehicleType = dataModel.bookingBillBean.getVehicleType();
                                        vechile_no = dataModel.bookingBillBean.getVechile_no();
                                        ParkingAreaName = dataModel.bookingBillBean.getParkingAreaName();
                                        TotalDuration = dataModel.bookingBillBean.getTotalDuration();
                                        TotalParkingAmount = dataModel.bookingBillBean.getTotalParkingAmount();
                                        TotalPaybleAmount = dataModel.bookingBillBean.getTotalPaybleAmount();
                                        FineAmount = dataModel.bookingBillBean.getFineAmount();
                                        OfferAmount = dataModel.bookingBillBean.getOfferAmount();
                                        PaymentMode = dataModel.bookingBillBean.getPaymentMode();
                                        AgencyName = dataModel.bookingBillBean.getAgencyName();
                                        message = dataModel.bookingBillBean.getMessage();
                                        overtime_duration = dataModel.bookingBillBean.getOverTimeDuration();
                                        overtime_amount = dataModel.bookingBillBean.getOverTimeAmount();
                                        Log.e("PaymentMode",PaymentMode);
                                    }

                                    Intent intent = new Intent(SplashActivity.this,BillGenerateActivity.class);
                                    intent.putExtra("bookingno",bookingno);
                                    intent.putExtra("BookingID",BookingID);
                                    intent.putExtra("checkintime",checkintime);
                                    intent.putExtra("checkouttime",checkouttime);
                                    intent.putExtra("ownerphoneno",ownerphoneno);
                                    //intent.putExtra("totalamount",totalamount);
                                    intent.putExtra("ParkingAreaName",ParkingAreaName);
                                    intent.putExtra("TotalDuration",TotalDuration);
                                    intent.putExtra("TotalParkingAmount",TotalParkingAmount);
                                    intent.putExtra("TotalPaybleAmount",TotalPaybleAmount);
                                    intent.putExtra("OfferAmount",OfferAmount);
                                    intent.putExtra("FineAmount",FineAmount);
                                    intent.putExtra("PaymentMode",PaymentMode);
                                    intent.putExtra("VehicleType",VehicleType);
                                    //intent.putExtra("bookingstatus",bookingstatus);
                                    intent.putExtra("vechile_no",vechile_no);
                                    intent.putExtra("AgencyName",AgencyName);
                                    intent.putExtra("overtime_duration",overtime_duration);
                                    intent.putExtra("overtime_amount",overtime_amount);
                                    intent.putExtra("message",message);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                }else {
                                    dataModel.details_shown="1";
                                    startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                    finish();
                                }

                            }else{
                                dataModel.details_shown="";
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            }
                        }
                    }, 3000);
                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                }
                else {
                    JSONObject msg = new JSONObject(output);

                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message

                    msg_txt.setText(getResources().getString(R.string.licencence_msg_new));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            CheckAppsVersion(getApplicationContext().getPackageName(),version_name);

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.GENERATEAUTHTOKEN)) {
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.base_token = obj.getString("access_token");
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {

                            if (session.isLoggedIn()) {
                                startActivity(new Intent(SplashActivity.this, DashBoardActivity.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            }else{
                                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                                finish();
                            }
                        }
                    }, 3000); //delayed by 3 second

                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                   // ShowAlertDialog.showAlertDialogFailure(SplashActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    public class UpdateMeeDialog {

        ActivityManager am;
        TextView rootName;
        Context context;
        Dialog dialog;
        String key1,schoolId;
        public void showDialogAddRoute(Activity activity, final String packageName){
            context=activity;
            dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_update);
            am = (ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);

            Button cancelDialogue=(Button)dialog.findViewById(R.id.buttonUpdate);
            Log.i("package name",packageName);
            cancelDialogue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.innovus.vyoma.s_parking_agent"));
                    context.startActivity(intent);
                }
            });
            dialog.show();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long betweenDates(Date firstDate, Date secondDate) throws IOException
    {
        return ChronoUnit.DAYS.between(firstDate.toInstant(), secondDate.toInstant());
    }


    public static long betweenDateslowversion(Date firstDate, Date secondDate) throws IOException
    {

        long msDiff = secondDate.getTime() - firstDate.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        return daysDiff;
    }
}
