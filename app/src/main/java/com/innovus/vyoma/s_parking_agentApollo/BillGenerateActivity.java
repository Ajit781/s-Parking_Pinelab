package com.innovus.vyoma.s_parking_agentApollo;

import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.isBound;
import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.mService;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;

import android.os.Bundle;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aipl.android.fastaglib.FasTag;
import com.aipl.android.fastaglib.FasTagListener;
import com.bumptech.glide.Glide;
import com.eze.api.EzeAPI;
import com.google.gson.Gson;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import data_objects.SParkingAgentModel;
import db.DatabaseHandler;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;
import utilities.others.ConnectionStatus;


public class BillGenerateActivity extends AppCompatActivity implements AsyncResponse, View.OnClickListener, FasTagListener/*, SwproipeListener */{
    private Context mContext;
    private ImageView iv_list_car_type;
    private ImageView iv_printer;
    private LinearLayout ll_overtime_amount,ll_overtime_duration;
    private TextView txt_vechile_no,txt_vechileType,txt_owner_phone_no,txt_check_in_time,txt_check_out_time,txt_owner_fee,
            txt_fineamount, txt_offer_amount,txt_paymentMode,tv_info,txttotal_amount,txt_booking_no,txt_location,
            txt_overtimeamount,txt_overtimeduration;
    private Button btnOk,btnCancel,btnCard,btnFasttag;

    private String bookingno="",BookingID="",checkintime="",checkouttime="",ownerphoneno=""
            ,totalamount="",ParkingAreaName="",TotalDuration="",TotalParkingAmount="",TotalPaybleAmount="",OfferAmount=""
            ,FineAmount="",PaymentMode="",VehicleType="",bookingstatus="",vechile_no="",message="",AgencyName="",
            overtime_duration="",vehicle_type_icon="", overtime_amount="",bookingstatus_payment="",paymentModeId="",transactionId="";
    String proper_duration = "";

    static final String LOG_TAG = "BillGenerate";// TAG name
    /*private BarcodeDetector detector;*/
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    DataOutputStream mmOutputStream_forboom;
    InputStream mmInputStream;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    int  isPrint;
    Bitmap bmp;
    double fare= 0.00;
    double gst_fare= 0.00;
    double sgst_calc=0.00;
    double cgst_calc=0.00;
    int TotalAmt=0;
    int readBufferPosition;
    volatile boolean stopWorker;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    private SpotsDialog progressDialog;
    RemoteAsync remoteAsync;
    FasTag fastTag;
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    ProgressDialog dialog;
    private ExecutorService mSingleThreadExecutor;
    private FloatView floatView;
    public static String param_status="false";
    public static String sim_status="false";
    private String passapplied = "0";
    private String strStoreid = "0";
    private String choosepaymentMode = "";
    String package_name="";
//    EazyTap API KEY
//    8def92fb-4aac-4810-bbfc-091ac5f96fa6
    private final int REQUEST_CODE_INITIALIZE = 10001;
    public static final int NOT_FOUND = -1;
    //  REQUEST
    private final int REQUEST_CODE_PAY = 10015;
    private final int REQUEST_CODE_PRINT_BITMAP = 10029;// for APi print for pax

    @Override
    protected void onDestroy() {

        super.onDestroy();
        SharedStorage.setValue(getApplicationContext(),"billstatus", "");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_generate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // recieve data from DashboardActivity
//        if(SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("eazy_Tap")){
//            Log.i(BillGenerateActivity.LOG_TAG, "Started...");
////            CToast.show(getApplicationContext(),"Started...");
//            fastTag = new FasTag(115200, this);
//
//
//        }
        mContext = this;


        Bundle bundle = getIntent().getExtras();

        bookingno = bundle.getString("bookingno");
        BookingID = bundle.getString("BookingID");
        checkintime = bundle.getString("checkintime");
        checkouttime = bundle.getString("checkouttime");
        ownerphoneno = bundle.getString("ownerphoneno");
        ParkingAreaName = bundle.getString("ParkingAreaName");
        TotalDuration = bundle.getString("TotalDuration");
        TotalParkingAmount = bundle.getString("TotalParkingAmount");
        TotalPaybleAmount = bundle.getString("TotalPaybleAmount");
        OfferAmount = bundle.getString("OfferAmount");
        FineAmount = bundle.getString("FineAmount");
        PaymentMode = bundle.getString("PaymentMode");
        VehicleType = bundle.getString("VehicleType");
        vechile_no = bundle.getString("vechile_no");
        AgencyName = bundle.getString("AgencyName");
        message = bundle.getString("message");
        vehicle_type_icon = bundle.getString("vehicle_type_icon");
        overtime_duration = bundle.getString("overtime_duration");
        overtime_amount = bundle.getString("overtime_amount");
        // set supportActionBar title font color.
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+AgencyName+"</font>"));

        TotalAmt= Integer.parseInt(TotalPaybleAmount);
        fare = TotalAmt*100;
        gst_fare= Double.parseDouble(String.format("%.2f", fare/118));

        sgst_calc= Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));
        cgst_calc=  Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));
        Log.e("GST Calc",TotalAmt+"fare= "+gst_fare+"sgst_calc = "+sgst_calc+" cgst_calc="+cgst_calc);

        initviews();
    }

    private void initviews() {

        dataModel.about_advanced_dash = 1;

        btnOk = (Button) findViewById(R.id.btnOk);
        btnFasttag = (Button) findViewById(R.id.btnFasttag);
        btnCard = (Button) findViewById(R.id.btnCard);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        ll_overtime_amount = (LinearLayout)findViewById(R.id.ll_overtime_amount);
        ll_overtime_duration = (LinearLayout)findViewById(R.id.ll_overtime_duration);
        txt_overtimeamount = (TextView)findViewById(R.id.txt_overtimeamount);
        txt_overtimeduration = (TextView)findViewById(R.id.txt_overtimeduration);
        iv_list_car_type = (ImageView) findViewById(R.id.iv_list_car_type);
        txt_location = (TextView) findViewById(R.id.txt_location);
        txt_booking_no = (TextView) findViewById(R.id.txt_booking_no);
        tv_info = (TextView) findViewById(R.id.tv_info);
        txttotal_amount = (TextView) findViewById(R.id.txttotal_amount);
        txt_offer_amount = (TextView) findViewById(R.id.txt_offer_amount);
        txt_fineamount = (TextView) findViewById(R.id.txt_fineamount);
        txt_check_in_time = (TextView) findViewById(R.id.txt_check_in_time);
        txt_check_out_time = (TextView) findViewById(R.id.txt_check_out_time);
        //   TextView txt_owner_name = (TextView) findViewById(R.id.txt_owner_name);
        txt_owner_phone_no = (TextView) findViewById(R.id.txt_owner_phone_no);
        txt_owner_fee = (TextView) findViewById(R.id.txt_owner_fee);
        txt_vechileType = (TextView) findViewById(R.id.txt_vechileType);
        txt_paymentMode = (TextView) findViewById(R.id.txt_paymentMode);
        txt_vechile_no = (TextView) findViewById(R.id.txt_vechile_no);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);
        btnOk.setText(getResources().getString(R.string.recieved));
        btnCancel.setText(getResources().getString(R.string.NotRecieved));


        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCard.setOnClickListener(this);
        btnFasttag.setOnClickListener(this);
        //Eazy tap printer checking
        if(SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("eazy_Tap")||(SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("verifone"))){
            btnOk.setText(getResources().getString(R.string.cash));
            btnCard.setVisibility(View.VISIBLE);
            btnFasttag.setVisibility(View.VISIBLE);// for  UPI
            btnCancel.setVisibility(View.GONE);
//            fastTag = new FasTag(115200, this);
        }else {
            btnOk.setText(getResources().getString(R.string.recieved));
            btnCard.setVisibility(View.GONE);
            btnFasttag.setVisibility(View.GONE);
        }
        
        if (PaymentMode.equals("Wallet")){
            btnOk.setText(getResources().getString(R.string.recieved));
            btnCancel.setVisibility(View.GONE);
            btnCard.setVisibility(View.GONE);
            btnFasttag.setVisibility(View.GONE);
        }else {
            btnCancel.setVisibility(View.GONE);
        }

//        if (vehicle_type_icon!=null){
//            Glide.with(this)
//                    .load(vehicle_type_icon)
//                    .into(iv_list_car_type);
//
//
//        }
        if(VehicleType.equals("Two Wheeler")){
            iv_list_car_type.setImageDrawable(getResources().getDrawable(R.drawable.bike));
        }else if (VehicleType.equals("Four Wheeler")){
            iv_list_car_type.setImageDrawable(getResources().getDrawable(R.drawable.car_compact));
        }else{
            iv_list_car_type.setImageDrawable(getResources().getDrawable(R.drawable.bus));
        }

        txt_check_in_time.setText(checkintime);
        txt_check_out_time.setText(checkouttime);
        txt_owner_phone_no.setText(ownerphoneno);
        txt_owner_fee.setText("₹ "+TotalParkingAmount);
        txt_vechileType.setText(VehicleType);
        txt_paymentMode.setText(PaymentMode);
        txt_vechile_no.setText(vechile_no);
        txt_fineamount.setText("₹ "+FineAmount);
        txt_offer_amount.setText("₹ "+OfferAmount);
        txttotal_amount.setText("₹ "+TotalPaybleAmount);
        txt_booking_no.setText("# "+bookingno);
        txt_location.setText(SharedStorage.getValue(this,"parkinglocation"));
        //used for adv booking
        if (overtime_duration.equals("0")){
            ll_overtime_amount.setVisibility(View.GONE);
            ll_overtime_duration.setVisibility(View.GONE);

        }
        else if (overtime_duration.equals("")){
            ll_overtime_amount.setVisibility(View.GONE);
            ll_overtime_duration.setVisibility(View.GONE);

        }else {
            String duration[] = overtime_duration.split(":");
            String hour = duration[0];
            String min = duration[1];
            proper_duration = "";

            if (hour.equals("0")){
                if (min.equals("1")){//checkin for 1min
                    proper_duration = min + " min";
                }else {
                    proper_duration = min + " mins";
                }
            }
           else {
                if (hour.equals("1")){
                    proper_duration = hour + " hour " + min + " mins";

                }else {
                    proper_duration = hour + " hours " + min + " mins";

                }
            }

            ll_overtime_amount.setVisibility(View.VISIBLE);
            ll_overtime_duration.setVisibility(View.VISIBLE);
           // txt_overtimeduration.setText(overtime_duration);
            txt_overtimeduration.setText(proper_duration);
            txt_overtimeamount.setText("₹ "+overtime_amount);

        }

        if(message.contains("Insufficient")){

            tv_info.setText(message);
        }else {
            tv_info.setVisibility(View.GONE);
        }


//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isPrint = 0;
//                paymentCollection(BookingID,"0");// here 0 denotes payment status  Not recieved
//
//            }
//        });
//
//        btnOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isPrint = 1;
//                paymentCollection(BookingID,"1");// here 1 denotes payment status is recieved
//            }
//        });

        // for EzeAPI initialization
        if (!SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("")){


            floatView = FloatView.getInstance(BillGenerateActivity.this);
//            floatView.createFloatView(20, 20);
//            floatView.release();
            //Live key
            JSONObject jsonRequest = new JSONObject();
            try {
//            jsonRequest.put("demoAppKey","25e95651-c5aa-4735-903d-c2133d661f7a");
//            jsonRequest.put("prodAppKey","25e95651-c5aa-4735-903d-c2133d661f7a");

                jsonRequest.put("demoAppKey","03ee6ba9-0e26-4cca-9c2d-95bb8358220e");
                jsonRequest.put("prodAppKey","03ee6ba9-0e26-4cca-9c2d-95bb8358220e");

                jsonRequest.put("merchantName","APOLLO_MULTISPECIALITY_HO");
                //jsonRequest.put("userName","1510202410");
                jsonRequest.put("userName","2119592700");
                jsonRequest.put("currencyCode","INR");
                jsonRequest.put("appMode","PROD");
                jsonRequest.put("captureSignature","false");
                jsonRequest.put("prepareDevice","false");

                Log.e("request_init",jsonRequest.toString());
                //          EzeApi initialization
                EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE, jsonRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

//
//            JSONObject jsonRequest = new JSONObject();
//            try {
//                 jsonRequest.put("demoAppKey","a8b2bcdd-0a39-497e-9a41-73d68a77ffa7");
////
//                //jsonRequest.put("prodAppKey","F6E5395E-E403-4925-A823-E63FF494DBDA");
//                jsonRequest.put("prodAppKey","a8b2bcdd-0a39-497e-9a41-73d68a77ffa7");
////                jsonRequest.put("prodAppKey","");
//                jsonRequest.put("merchantName","VYOMA_INNOVUS_GLOBEL_PVT");
//                //jsonRequest.put("userName",SharedStorage.getValue(getApplicationContext(),"Userame"));
//                jsonRequest.put("userName","9836700645");
//                jsonRequest.put("currencyCode","INR");
//                jsonRequest.put("appMode","PROD");
//                jsonRequest.put("captureSignature","false");
//                jsonRequest.put("prepareDevice","false");
//
//                Log.e("request_init",jsonRequest.toString());
//                //          EzeApi initialization
//                EzeAPI.initialize(this, REQUEST_CODE_INITIALIZE, jsonRequest);
//            }  catch (JSONException e) {
//                e.printStackTrace();
//            }

        }

    }

    // EzeAPI initialization request code  checking
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_INITIALIZE) {
            try {
                if (data != null && data.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response",response.toString());
                       // CToast.show(this," Initialization Sucessfull");
                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response",errorMessage);
                       // CToast.show(this," Initialization failed");
                        // Show the error to user as a pop-up informing the details
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                // Do your exception handling
            }
        }
        else if(requestCode == REQUEST_CODE_PAY) {
            try {
                if (data != null && data.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        response = response.getJSONObject("txn");
                        String strTxnId = response.getString("txnId");
                        Log.e("response",strTxnId);
                        Log.e("response",response.toString());
                        PaymentMode="Card";
                        isPrint = 1;
                        SetEzetapTransactionLog(BookingID,response.toString());
                        paymentCollection(BookingID,"1","6",strTxnId);// here 1 denotes payment status is recieved

                        // Payment is successful, you can record the transaction details like Ezetap transaction ID and complete the order
                    }
                    else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response",errorMessage);

                        ShowAlertDialog.showAlertDialog(this, errorMessage);
                        // Error Notification API Call;
                        //BookingID
                        String strEzetapRespose = data.getStringExtra("response");
                        try{
                            SetEzetapTransactionLog(BookingID,strEzetapRespose);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        // Show the error to user as a pop-up informing the details so that he can take action against it.
                    }
                    else{

                        String strEzetapRespose = "";
                        String errorMessage = "Payment Error";
                        try
                        {
                            JSONObject response = new JSONObject(data.getStringExtra("response"));
                            Log.e("Payment Error", String.valueOf(response));
                            response = response.getJSONObject("error");
                            String errorCode = response.getString("code");
                            errorMessage = response.getString("message");
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        finally
                        {
                            // Show the error to user as a pop-up informing the details
                            ShowAlertDialog.showAlertDialog(this, errorMessage);
                        }

                        try
                        {
                            strEzetapRespose = data.getStringExtra("response");
                            // Error Notification API Call;
                            //BookingID
                        }catch(Exception ex){
                            ex.printStackTrace();
                            strEzetapRespose = "Transaction failed :: Exception"+ex;
                        }finally {
                            try{
                                SetEzetapTransactionLog(BookingID,strEzetapRespose);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }

                }
                else{
                    // Show dialog : Text : "Data null from Ezetap : " + resultCode
                    ShowAlertDialog.showAlertDialog(this, "Data null from Ezetap : " + resultCode);

                }

            } catch (Exception e) {
                e.printStackTrace();
                // Do your exception handling
            }
        }
        else if (requestCode == REQUEST_CODE_PRINT_BITMAP) {
            try {
                if (data != null && data.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("result");
                        Log.e("response", String.valueOf(response));
                        // callBT_forboom api call on separate thread
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                try{
                                    callBT_forboom();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        try {
                            Thread.sleep(300);
                            pagechange();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // Initialization of SDK is successful, proceed with your action
                    }
                    else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                BillGenerateActivity.this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);
                        msg_txt.setText(errorMessage);
                        btnOk.setText(getResources().getString(R.string.reprint));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printEazytapBillNew();
                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
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
                    else{
                        JSONObject response = new JSONObject(data.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        // ShowAlertDialog.showAlertDialog(this, errorMessage);
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                BillGenerateActivity.this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);
                        msg_txt.setText(errorMessage);
                        btnOk.setText(getResources().getString(R.string.reprint));
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                try {
                                    printEazytapBillNew();
                                } catch (Exception e) {

                                    e.printStackTrace();

                                }
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
                else{
                    // Show dialog : Text : "Printer Error, Data null from Ezetap : " + resultCode
                    ShowAlertDialog.showAlertDialog(this, "Printer Error, Data null from Ezetap : " + resultCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //  ShowAlertDialog.showAlertDialog(this, e.getMessage());
                // Do your exception handling
            }
        }
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(BillGenerateActivity.this, R.style.CustomWaitDialog);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void stop_progress_dialog() {
        if(progressDialog!=null){

            try{
                progressDialog.dismiss();
                progressDialog=null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    // Request service for GenerateAuthToken checking
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
// this  service method calling for payment collection after bill generation
    private void paymentCollection(String bookingID, String bookingstatus, String PaymentModeID, String TransactionID) {
        start_progress_dialog();
        bookingstatus_payment = bookingstatus;
        bookingstatus_payment = bookingstatus;
        transactionId = TransactionID;
        paymentModeId = PaymentModeID;
        String userid = SharedStorage.getValue(getApplicationContext(), "UserId");

        Urls Urls = new Urls();
       // String start_parking_url = Urls.PaymentCollection;
        String start_parking_url = Urls.VehicleCheckOutV10;

        remoteAsync = new RemoteAsync(start_parking_url);
       // remoteAsync.type = RemoteAsync.PAYMENTCOLLECTION;
        remoteAsync.type = RemoteAsync.VEHICLECHECKOUTV10;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
//            urlParams = "BookingID=" + URLEncoder.encode(bookingID, "UTF-8") +
//                    "&PaymentStatus=" + URLEncoder.encode(bookingstatus, "UTF-8");

            urlParams = "VehicleNumber=" + URLEncoder.encode(vechile_no, "UTF-8")+
                    "&AgentID=" + URLEncoder.encode(SharedStorage.getValue(getApplicationContext(),"UserId"), "UTF-8")+
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&PaymentStatus=" + URLEncoder.encode(bookingstatus, "UTF-8")+
                    "&PaymentModeID=" + URLEncoder.encode(PaymentModeID, "UTF-8")+
                    "&TransactionID=" + URLEncoder.encode(TransactionID, "UTF-8")+
                    "&CheckoutTime=" + URLEncoder.encode(checkouttime, "UTF-8");


        } catch (Exception e) {

        }
        remoteAsync.execute(urlParams);
    }
    // this  service method calling for payment collection after bill generation
    private void SetEzetapTransactionLog(String bookingID, String TxnResponse) {
        start_progress_dialog();

        Urls Urls = new Urls();
        String start_parking_url = Urls.SetEzetapTransactionLog;
        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.SETEZETAPTRANSACTIONLOG;
        remoteAsync.delegate = this;
        String urlParams = "";
        try {
            urlParams = "BookingID=" + URLEncoder.encode(bookingID, "UTF-8") +
                    "&TxnResponse=" + URLEncoder.encode(TxnResponse, "UTF-8");



        } catch (Exception e) {

        }
        remoteAsync.execute(urlParams);
        Log.e("urlParams",urlParams);

    }

    @Override
    public void processFinish(String type, String output) {
       // if (type.equals(RemoteAsync.PAYMENTCOLLECTION)) {
        if (type.equals(RemoteAsync.VEHICLECHECKOUTV10)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {


                    SharedStorage.setValue(getApplicationContext(),"billstatus", "");
                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                   //after payment done, delete  generated bill from local database
                    databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                    SharedStorage.setValue(getApplicationContext(),"BookingID", "");
                    dataModel.bookingBillBean = null;

                    if (isPrint== 1) {
                        if (SharedStorage.getValue(BillGenerateActivity.this, "printer_name").equals("")) {
                            dataModel.about_advanced_dash = 1;
                            pagechange();// Activity Redirection

                        }
                        else if (SharedStorage.getValue(BillGenerateActivity.this, "printer_name").equals("eazy_Tap")) {
                            dataModel.about_advanced_dash = 1;

                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                    this).create();

                            final LayoutInflater inflater = this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                            alertDialog.setView(dialogView);

                            TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                            Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                            Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                            TextView title = (TextView) dialogView.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));
                            bt_cash.setText(getResources().getString(R.string.print));
                            msg_txt.setText(obj.getString("message"));

                            bt_cash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    try {
                                        //print Bill Using EazyTap
                                        //printEazytapBill();
                                       // printEazytapBillNew();
                                       // printEazytapBillUsingSDK();
                                        printBillUsingPineLab();

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }

                                }
                            });
                            bt_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    dataModel.about_advanced_dash = 1;
                                    pagechange();

                                }
                            });
                            //Animate alert dialog box
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.animator.fade_in,
                                    android.R.animator.fade_out);
                            // Showing Alert Message
                            alertDialog.show();
                            alertDialog.setCancelable(false);

                        }
                        else if (SharedStorage.getValue(BillGenerateActivity.this, "printer_name").equals("verifone")) {
                            dataModel.about_advanced_dash = 1;

                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                    this).create();

                            final LayoutInflater inflater = this.getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                            alertDialog.setView(dialogView);

                            TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                            Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                            Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                            TextView title = (TextView) dialogView.findViewById(R.id.title);
                            title.setText(getResources().getString(R.string.app_name));
                            bt_cash.setText(getResources().getString(R.string.print));
                            msg_txt.setText(obj.getString("message"));

                            bt_cash.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();
                                    try {
                                        //print Bill Using EazyTap
                                        printEazytapBillNew();

                                    } catch (Exception e) {

                                        e.printStackTrace();
                                    }

                                }
                            });
                            bt_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();

                                    dataModel.about_advanced_dash = 1;
                                    pagechange();

                                }
                            });
                            //Animate alert dialog box
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.animator.fade_in,
                                    android.R.animator.fade_out);
                            // Showing Alert Message
                            alertDialog.show();
                            alertDialog.setCancelable(false);

                        }


                        else {
//                            Intent intent = new Intent(BillGenerateActivity.this, VehicleInfoScanActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//                            finish();
                        }
                    }
                }
                else if (obj.getString("status").equals(Constants.FAILED)) {
                    btnOk.setClickable(true);
                    btnOk.setEnabled(true);
//
//                    SharedStorage.setValue(getApplicationContext(),"billstatus", "");
//                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
//                    databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
//                    SharedStorage.setValue(getApplicationContext(),"BookingID", "");
//                    dataModel.bookingBillBean = null;
//                    dataModel.about_advanced_dash = 1;
//                    Intent intent = new Intent(BillGenerateActivity.this,DashBoardActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                    finish();

                    try {
                        JSONObject msg = new JSONObject(output);

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                        Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                        btnyes.setText(getResources().getString(R.string.retry));
                        btnno.setText(getResources().getString(R.string.cancel));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        // msg_txt.setText(R.string.paymentreciept);
                        // msg_txt.setText(msg.getString("message"));
                        msg_txt.setText(getResources().getString(R.string.vehicleCheckoutfailed));
                        btnyes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();

                                    isPrint = 1;
                                    paymentCollection(BookingID,bookingstatus_payment,paymentModeId,transactionId);



                            }
                        });
                        btnno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
//                                isPrint = 0;
//                                // redirect back to vehicle list-DashboardActivity
//                                SharedStorage.setValue(getApplicationContext(),"billstatus", "");
//                                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
//                                databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
//                                SharedStorage.setValue(getApplicationContext(),"BookingID", "");
//                                dataModel.bookingBillBean = null;
//                                dataModel.about_advanced_dash = 1;
//                                Intent intent = new Intent(BillGenerateActivity.this,DashBoardActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                finish();
                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        paymentCollection(BookingID,bookingstatus_payment,paymentModeId,transactionId);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    btnOk.setClickable(true);
                    btnOk.setEnabled(true);
                    try {
                        JSONObject msg = new JSONObject(output);

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                        Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                        btnyes.setText(getResources().getString(R.string.retry));
                        btnno.setText(getResources().getString(R.string.cancel));

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message

                        // msg_txt.setText(R.string.paymentreciept);
                         msg_txt.setText(msg.getString("message"));
                        btnyes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                   isPrint = 1;
                                    paymentCollection(BookingID,bookingstatus_payment,paymentModeId,transactionId);
                            }
                        });
                        btnno.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
//                                isPrint = 0;
//                                // redirect back to vehicle list-DashboardActivity
//                                SharedStorage.setValue(getApplicationContext(),"billstatus", "");
//                                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
//                                databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
//                                SharedStorage.setValue(getApplicationContext(),"BookingID", "");
//                                dataModel.bookingBillBean = null;
//                                dataModel.about_advanced_dash = 1;
//                                Intent intent = new Intent(BillGenerateActivity.this,DashBoardActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
//                                finish();
                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                } else if (obj.getString("status").equals(Constants.TOKEN_EXP)) {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialogFailure(BillGenerateActivity.this, msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                        pagechange();

                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {


                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        // SpecialPassList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {

                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(BillGenerateActivity.this, msg.getString("message"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.SETEZETAPTRANSACTIONLOG)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
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
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

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
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        // SpecialPassList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {

                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(BillGenerateActivity.this, msg.getString("message"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /******************* eazyTap printer printing code ****************/
    public static final int PAGE_WIDTH_TWO_INCH = 22;
    public static final int PAGE_WIDTH_TWO_INCH_SMALL = 30;
    public static final int PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM = 40;
    static String paddingLeft(String data, int n) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < n; i++)
            temp.append(" ");
        return (temp + data);
    }

    static String paddingRight(String data, int n) {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < n; i++)
            temp.append(" ");
        return (data + temp);

    }
    public static String paddingCenter(String data, int n) {
        String temp = "";
        String t1;
        String t2;
        String t3;

        if (data.length() > n) {
            t1 = String.format("%s\n", data.substring(0, n));
            t2 = data.substring(n, data.length());
            if (t2.length() > n) {
                t3 = paddingCenter(t2, n);
            } else {
                t3 = paddingLeft(t2, ((n / 2) - (t2.length() / 2)));
            }
            temp = t1 + t3;
        } else if (data.length() < n) {
            temp = paddingLeft(data, ((n / 2) - (data.length() / 2)));
        } else if (data.length() == n) {
            temp = data;
        }
        return temp;

    }

    private void printEazytapBillNew() {

        try
        {
            Bitmap bitmap = null;
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            Integer iBitmapBaseHeight = 570;
            String gstNo = SharedStorage.getValue(BillGenerateActivity.this,"AgencyGSTNo");
            if(null == gstNo || gstNo.trim().equals(""))
            {
                iBitmapBaseHeight -= 24;
            }

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation"),35);
            if(arrLocName.length <= 1)
            {
                bitmap = Bitmap.createBitmap(400, iBitmapBaseHeight, Bitmap.Config.ARGB_8888);
            }
            else
            {
                Integer bitmapHeight = iBitmapBaseHeight + ((arrLocName.length - 1) * 24);
                bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
            }
            bitmap.eraseColor(Color.WHITE);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 0));
            Rect bounds = new Rect();

            // Set first line in Bitmap
            paint.setTextSize((int) (26));
            String strText = "OUTSLIP";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = 30;
            canvas.drawText(strText, x, y, paint);

            paint.setTextSize((int) (22));
            strText = "Parking Maintance Charge";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set third line in Bitmap
            paint.setTextSize((int) (22));
            // strText = SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation");
            // String[] arrLocName = breakStringToLines(strText,35);
            for(String strLocName : arrLocName)
            {
                if(null != strLocName && !strLocName.trim().equals(""))
                {
                    paint.getTextBounds(strLocName, 0, strLocName.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strLocName, x, y, paint);
                }
            }



            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle No   : "+ vechile_no;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fifth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No  : "+ bookingno;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "In Time          : "+checkintime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set seventh line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Out Time       : "+checkouttime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);
            if (!overtime_duration.equals("0")){
                // Set fifth line in Bitmap
                paint.setTextSize((int) (22));
                strText = "Over Duration  : "+overtime_duration;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                x = 35;
                y += 24;
                canvas.drawText(strText, x, y, paint);

            }
            // Set eighth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Duration        : "+TotalDuration;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

//            // Set ninth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Amount         : "+ "Rs. "+TotalParkingAmount;
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set tenth line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Fine Amount : "+"Rs. "+FineAmount;
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//
//            // Set eleventh line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Discount       : "+"Rs. "+OfferAmount;
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = 35;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);

            // Set twelfth line in Bitmap
            paint.setTextSize((int) (20));
            y += 10;
            strText = "-------------------------------------------------------";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set thirteenth line in Bitmap
            paint.setTextSize((int) (26));
            strText = "Pay "+PaymentMode+"     : "+ "Rs. "+TotalPaybleAmount;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            paint.setTextSize((int) (20));
            strText = "-------------------------------------------------------";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = 35;
            y += 24;
            canvas.drawText(strText, x, y, paint);


            if(!(null == gstNo || gstNo.trim().equals("")))
            {
                paint.setTextSize((int) (22));
                strText = "Base value:- 16.94 per hour.";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                paint.setTextSize((int) (22));
                strText = "CGST @9%:- 1.52 per hour.";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                paint.setTextSize((int) (22));
                strText = "SGST @9%:- 1.52 per hour.";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                paint.setTextSize((int) (22));
                strText = "Total:- Rs. 20.00 Per hour.";
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

                // Set fifteenth line in Bitmap
                paint.setTextSize((int) (22));
                strText = "GSTIN : "+SharedStorage.getValue(BillGenerateActivity.this,"AgencyGSTNo");
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);

            }

            // Set sixteenth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Thank You. Please visit again!";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            // Building Image Object
            jsonImageObj.put("imageData", encodedImageData);
            jsonImageObj.put("imageType", "JPEG");
            jsonRequest.put("image", jsonImageObj); // Pass this attribute when you have a valid captured signature image
            EzeAPI.printBitmap(BillGenerateActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);
//            txt_owner_fee.post(new Runnable() {
//                public void run() {
////                        CToast.show(getApplicationContext(),status);
//                    try {
//                        Thread.sleep(300);
//                        pagechange();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

    }
    private void printEazytapBillUsingSDK() {
        new Thread(new Runnable() {
            public void run() {
                try
                {
                    PrinterTester.getInstance().init();
                    Bitmap bitmap = null;
                    JSONObject jsonRequest = new JSONObject();
                    JSONObject jsonImageObj = new JSONObject();

                    Integer iBitmapBaseHeight = 680;
                    String gstNo = SharedStorage.getValue(BillGenerateActivity.this,"AgencyGSTNo");
                    if(null == gstNo || gstNo.trim().equals(""))
                    {
                        iBitmapBaseHeight -= 24;
                    }

                    String[] arrLocName = breakStringToLines(SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation"),35);
                    if(arrLocName.length <= 1)
                    {
                        bitmap = Bitmap.createBitmap(400, iBitmapBaseHeight, Bitmap.Config.ARGB_8888);
                    }
                    else
                    {
                        Integer bitmapHeight = iBitmapBaseHeight + ((arrLocName.length - 1) * 24);
                        bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
                    }
                    bitmap.eraseColor(Color.WHITE);
                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    paint.setTypeface(typeface);
                    paint.setColor(Color.rgb(0, 0, 0));
                    Rect bounds = new Rect();

                    // Set first line in Bitmap
                    paint.setTextSize((int) (30));
                   // String strText = "Smartpower Receipt";
                    String strText = "OUTSLIP";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    int x = (bitmap.getWidth() - bounds.width())/2;
                    int y = 30;
                    canvas.drawText(strText, x, y, paint);

//                    // Set first line in Bitmap
//                    paint.setTextSize((int) (30));
//                    strText = "Smartpower";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                     x = (bitmap.getWidth() - bounds.width())/2;
//                     y += 24;
//                    canvas.drawText(strText, x, y, paint);

                    // Set second line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Parking Maintenance Charge";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set third line in Bitmap
                    paint.setTextSize((int) (22));
                    // strText = SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation");
                    // String[] arrLocName = breakStringToLines(strText,35);
                    for(String strLocName : arrLocName)
                    {
                        if(null != strLocName && !strLocName.trim().equals(""))
                        {
                            paint.getTextBounds(strLocName, 0, strLocName.length(), bounds);
                            x = (bitmap.getWidth() - bounds.width())/2;
                            y += 24;
                            canvas.drawText(strLocName, x, y, paint);
                        }
                    }



                    // Set fourth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Vehicle No    : "+ vechile_no;

                    //strText = "Booking No  : "+ bookingno;
                    //strText = "Over Duration : "+overtime_duration;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 62;
                    canvas.drawText(strText, x, y, paint);

                    // Set fifth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Booking No  : "+ bookingno;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set sixth line in Bitmap
                    paint.setTextSize((int) (24));
                   // strText = "In Time          : "+checkintime;
                    strText = "In Time          : "+checkintime;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set seventh line in Bitmap
                    paint.setTextSize((int) (24));
                   // strText = "Out Time       : "+checkouttime;
                    strText = "Out Time      : "+checkouttime;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
                    if (!overtime_duration.equals("0")){
                        // Set fifth line in Bitmap
                        paint.setTextSize((int) (24));
                        strText = "Over Duration     : "+overtime_duration;
                        paint.getTextBounds(strText, 0, strText.length(), bounds);
                        x = (bitmap.getWidth() - bounds.width())/2;
                        x = 25;
                        y += 24;
                        canvas.drawText(strText, x, y, paint);

                    }
                    // Set eighth line in Bitmap
                    paint.setTextSize((int) (24));
                    //strText = "Duration        : "+TotalDuration;
                    strText = "Duration      : "+TotalDuration;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

        //            // Set ninth line in Bitmap
        //            paint.setTextSize((int) (22));
        //            strText = "Amount         : "+ "Rs. "+TotalParkingAmount;
        //            paint.getTextBounds(strText, 0, strText.length(), bounds);
        //            x = 35;
        //            y += 24;
        //            canvas.drawText(strText, x, y, paint);
        //
        //            // Set tenth line in Bitmap
        //            paint.setTextSize((int) (22));
        //            strText = "Fine Amount : "+"Rs. "+FineAmount;
        //            paint.getTextBounds(strText, 0, strText.length(), bounds);
        //            x = 35;
        //            y += 24;
        //            canvas.drawText(strText, x, y, paint);
        //
        //            // Set eleventh line in Bitmap
        //            paint.setTextSize((int) (22));
        //            strText = "Discount       : "+"Rs. "+OfferAmount;
        //            paint.getTextBounds(strText, 0, strText.length(), bounds);
        //            x = 35;
        //            y += 24;
        //            canvas.drawText(strText, x, y, paint);
//                    double fare= 0.00;
//                    double gst_fare= 0.00;
//                    double sgst_calc=0.00;
//                    double cgst_calc=0.00;
//                    int TotalAmt= Integer.parseInt(TotalPaybleAmount);
//                    fare = TotalAmt*100;
//                    gst_fare= Double.parseDouble(String.format("%.2f", fare/118));
//
//                    sgst_calc= Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));
//                    cgst_calc=  Double.parseDouble(String.format("%.2f",(gst_fare*9)/100));

                    // Set twelfth line in Bitmap
                    paint.setTextSize((int) (24));
                    y += 24;
                    strText = "-------------------------------------------------------";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Total Amt." + "   : "+ "Rs. "+gst_fare;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);


                    paint.setTextSize((int) (26));
                    strText = "CGST @9% : "+"Rs. "+cgst_calc;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint); paint.setTextSize((int) (26));
                    strText = "SGST @9% : "+"Rs. "+sgst_calc;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
// Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "Pay "+PaymentMode+"    : "+ "Rs. "+TotalPaybleAmount;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);


                    // Set twelfth line in Bitmap
                    paint.setTextSize((int) (24));
                    //y += 24;
                    strText = "-------------------------------------------------------";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set thirteenth line in Bitmap
                    paint.setTextSize((int) (26));
                    strText = "GST No.   "+gstNo;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = 25;
                    y += 24;
                    canvas.drawText(strText, x, y, paint); paint.setTextSize((int) (26));


//                    if(!(null == gstNo || gstNo.trim().equals("")))
//                    {
//                        // Set fourteenth line in Bitmap
//                        paint.setTextSize((int) (22));
//                        strText = "Base value:- 16.94 per hour.";
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width())/2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                        paint.setTextSize((int) (22));
//                        strText = "CGST @9%:- 1.52 per hour.";
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width())/2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                        paint.setTextSize((int) (22));
//                        strText = "SGST @9%:- 1.52 per hour.";
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width())/2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                        paint.setTextSize((int) (22));
//                        strText = "Total:- Rs. 20.00 Per hour.";
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width())/2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//
////                        // Set fourteenth line in Bitmap
////                        paint.setTextSize((int) (22));
////                        strText = "Smartpower";
////                        paint.getTextBounds(strText, 0, strText.length(), bounds);
////                        x = (bitmap.getWidth() - bounds.width())/2;
////                        y += 24;
////                        canvas.drawText(strText, x, y, paint);
//
//                        // Set fifteenth line in Bitmap
//                        paint.setTextSize((int) (22));
//                        strText = "GST : "+gstNo;
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width())/2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                        paint.setTextSize((int) (24));
//                        strText = "-------------------------------------------------------";
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = 25;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//                    }

                    // Set sixteenth line in Bitmap
                    paint.setTextSize((int) (24));
                    y +=24;
                    strText = "\n" +
                            "Managed by Rohini Enterprise";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);
                    // Set sixteenth line in Bitmap

//                    paint.setTextSize((int) (24));
//                    y +=24;
//                    strText = "VISIT AGAIN!";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width())/2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);

//                    // Set sixteenth line in Bitmap
                    paint.setTextSize((int) (24));
                    y +=24;
                    strText = "www.s-parking.com";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width())/2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    PrinterTester.getInstance().printBitmap(bitmap);

                    PrinterTester.getInstance().step(2);

                    final String status = PrinterTester.getInstance().start();
                    // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());

                    txt_owner_fee.post(new Runnable() {
                        public void run() {
        //                        CToast.show(getApplicationContext(),status);
                            if (status.equals("Out of paper ")){
                                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                        BillGenerateActivity.this).create();

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                                alertDialog.setView(dialogView);
                                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                                Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                                heading.setText(R.string.validation_name);
                                msg_txt.setText(status);
                                btnOk.setText(getResources().getString(R.string.reprint));
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        try {
                                            printEazytapBillUsingSDK();
//                                            printBillUsingPineLab();

                                        } catch (Exception e) {

                                            e.printStackTrace();

                                        }
                                    }
                                });
                                //Animate alert dialog box
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.animator.fade_in,
                                        android.R.animator.fade_out);
                                // Showing Alert Message
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                            }else{
                                btnOk.setClickable(true);
                                btnOk.setEnabled(true);
                                // callBT_forboom api call on separate thread
//                                new Thread(new Runnable() {
//
//                                    @Override
//                                    public void run() {
//                                        try{
//                                            callBT_forboom();
//                                        }catch(Exception e){
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }).start();
                                try {
                                    Thread.sleep(300);
                                    pagechange();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });

                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        }).start();


    }
    private String getEncoded64ImageStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = bmp;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedDate = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedDate;
    }

   // eazyTap printer printing start
    private void  printEazytapBill() {

        new Thread(new Runnable() {
            public void run() {
                try{
                    String dateTime[] = getDateTime();
                    PrinterTester.getInstance().init();
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().setGray(30);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
//                PrinterTester.getInstance().printStr("Tax Invoice\n",null);
                    StringBuilder print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("sParking Receipt", PAGE_WIDTH_TWO_INCH)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                    PrinterTester.getInstance().step(2);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("30"));
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_16);
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Parking Maintenance Charge", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);

                    String str = SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation");
                    String[] arrOfStr = str.split("-");

                    for (String str_location : arrOfStr){
                        print_bill = new StringBuilder();
                        print_bill.append(paddingCenter(str_location, PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                        PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                    }

//                PrinterTester.getInstance().printStr(SharedStorage.getValue(BillGenerateActivity.this,"parkinglocation")+"\n",null);
                    PrinterTester.getInstance().printStr("\n",null);
                    PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("Vehicle No   : " + vechile_no+"\n",null);
                    PrinterTester.getInstance().printStr("Vehicle Type : " + VehicleType+"\n",null);

                    PrinterTester.getInstance().printStr("Booking No   : " + bookingno+"\n",null);
                    PrinterTester.getInstance().printStr("In Time      : " + checkintime+"\n",null);
                    PrinterTester.getInstance().printStr("Out Time     : " + checkouttime+"\n",null);

                    if (!overtime_duration.equals("0")){
                        PrinterTester.getInstance().printStr("Over Duration   : " + overtime_duration+"\n",null);
                    }
                    PrinterTester.getInstance().printStr("Duration     : " + TotalDuration+"\n",null);
//                PrinterTester.getInstance().printStr("Amount       : " + "Rs. "+TotalParkingAmount+"\n",null);
//                if (!overtime_duration.equals("0")){
//                    PrinterTester.getInstance().printStr("Overtime Amt : " + "Rs. "+FineAmount,null);
//                }
//
//                PrinterTester.getInstance().printStr("Fine Amount  : " + "Rs. "+FineAmount+"\n",null);
//                PrinterTester.getInstance().printStr("Discount     : " + "Rs. "+OfferAmount+"\n",null);
                    PrinterTester.getInstance().printStr("--------------------------------"+"\n",null);
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("Pay "+PaymentMode+"    " + "Rs. "+TotalPaybleAmount+"\n",null);

                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Inclusive of GST @ 18%", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("Maa Vaishno Company", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("GSTIN:"+SharedStorage.getValue(BillGenerateActivity.this,"AgencyGSTNo"), PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("Managed by Rohini Enterprise", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    print_bill.append(paddingCenter("www.smartpower.co.in", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                  PrinterTester.getInstance().printStr("Inclusive of GST @ 18%"+"\n",null);
//                  PrinterTester.getInstance().printStr("GSTIN:"+"19AABCB0977F1ZP"+"\n",null);
//                  PrinterTester.getInstance().printStr("Download s-Parking from Play Store"+"\n",null);
                    PrinterTester.getInstance().printStr("\n",null);
                    PrinterTester.getInstance().printStr("\n",null);
                    PrinterTester.getInstance().printStr("\n",null);

                    PrinterTester.getInstance().step(2);


                    final String status = PrinterTester.getInstance().start();
                    // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());

                    txt_owner_fee.post(new Runnable() {
                        public void run() {
//                        CToast.show(getApplicationContext(),status);
                            if (status.equals("Out of paper ")){
                                final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                                        BillGenerateActivity.this).create();

                                LayoutInflater inflater = getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                                alertDialog.setView(dialogView);
                                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                                Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                                heading.setText(R.string.validation_name);
                                msg_txt.setText(status);
                                btnOk.setText(getResources().getString(R.string.reprint));
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        try {
                                            printEazytapBill();

                                        } catch (Exception e) {

                                            e.printStackTrace();

                                        }
                                    }
                                });
                                //Animate alert dialog box
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.animator.fade_in,
                                        android.R.animator.fade_out);
                                // Showing Alert Message
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                            }else{
                                btnOk.setClickable(true);
                                btnOk.setEnabled(true);
                                // callBT_forboom api call on separate thread
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try{
                                            callBT_forboom();
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                try {
                                    Thread.sleep(300);
                                    pagechange();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }).start();


    }

    /*******************  end of eazyTap printer printing code ****************/
    private void callBT_forboom() {
        start_progress_dialog();
        String login_url = "http://"+SharedStorage.getValue(getApplicationContext(),"ip")+"/bbctrl";
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.BBCTRL;
        remoteAsync.delegate = this;
      //  GateOpenBoomBarrierBean gateOpenBoomBarrierBean= new GateOpenBoomBarrierBean(Integer.valueOf(status));

        Gson gson = new Gson();

        String urlParams = "";
        try {

            /*********convert bean class values from Json to Gson ******/
           // urlParams = gson.toJson(gateOpenBoomBarrierBean);
           // urlParams = "status=" + URLEncoder.encode(status, "UTF-8") ;

        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams----->",urlParams);
        remoteAsync.execute(urlParams);
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        dataModel.about_advanced_dash = 0;
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // show a dailog for payment recieved or not on back pressing

        final AlertDialog alertDialog = new AlertDialog.Builder(
                this).create();

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
        alertDialog.setView(dialogView);
        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
        Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
        Button btnno = (Button) dialogView.findViewById(R.id.btnno);

        heading.setText(R.string.validation_name);

        // Setting Dialog Message

       // msg_txt.setText(R.string.paymentreciept);
        msg_txt.setText(R.string.do_you_wantto);
        btnyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                if (dataModel.cameFrom.equals("Dashboard")){
                    isPrint = 0;

                    // redirect back to vehicle list-DashboardActivity
                    SharedStorage.setValue(getApplicationContext(),"billstatus", "");
                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                    databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                    SharedStorage.setValue(getApplicationContext(),"BookingID", "");

                    dataModel.bookingBillBean = null;
                    dataModel.about_advanced_dash = 1;
                    Intent intent = new Intent(BillGenerateActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                }else if(dataModel.cameFrom.equals("EndParking")){
                    dataModel.about_advanced_dash = 1;
                    dataModel.bookingBillBean = null;
                    Intent intent = new Intent(BillGenerateActivity.this, EndParkingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                }
                else{
                    isPrint = 0;

                    // redirect back to vehicle list-DashboardActivity
                    SharedStorage.setValue(getApplicationContext(),"billstatus", "");
                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                    databaseHandler.deletebill(SharedStorage.getValue(getApplicationContext(),"BookingID"));
                    SharedStorage.setValue(getApplicationContext(),"BookingID", "");

                    dataModel.bookingBillBean = null;
                    dataModel.about_advanced_dash = 1;
                    Intent intent = new Intent(BillGenerateActivity.this, DashBoardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();
                }



            }
        });
        btnno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                isPrint = 0;
                //payment collection request
               // paymentCollection(BookingID,"0");//here 0 denotes payment status  Not recieved

            }
        });
        //Animate alert dialog box
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        // Showing Alert Message
        alertDialog.show();

    }


    public void pagechange(){
        if (dataModel.cameFrom.equals("Dashboard")){
            dataModel.about_advanced_dash = 1;
            Intent intent = new Intent(BillGenerateActivity.this, DashBoardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }else{
            dataModel.about_advanced_dash = 1;
            Intent intent = new Intent(BillGenerateActivity.this, EndParkingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }

    }


    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Log.e("data",data);
                                                // tv_device_name.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                            iv_printer.setVisibility(View.GONE);
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            iv_printer.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    // print date and Time
    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[2];
        dateTime[0] = c.get(Calendar.DAY_OF_MONTH) +"/"+ String.valueOf(c.get(Calendar.MONTH)+1) +"/"+ c.get(Calendar.YEAR);
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+c.get(Calendar.MINUTE);
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        return dateTime;
    }

    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;

            if(mmOutputStream != null){
                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }

            /*myLabel.setText("Bluetooth Closed");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param encodedString
     * @return bitmap (from given string)
     */
    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);

            /*Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);*/
            Bitmap bitmap = Bitmap.createBitmap(200, 50, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            float scale = getResources().getDisplayMetrics().density;
            //paint.setTextSize((int) (25 * scale));
            paint.setTextSize(16);

            canvas.drawText(encodedString, 200, 50, paint);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.btnCancel){
            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
                isPrint = 0;
                paymentCollection(BookingID, "0", "1", "0");// here 0 denotes payment status  Not recieved
            }else{
                btnOk.setClickable(true);
                btnOk.setEnabled(true);
                ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
            }
        }
        if(view.getId()==R.id.btnOk){

            btnOk.setClickable(false);
            btnOk.setEnabled(false);

            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
            isPrint = 1;
            paymentCollection(BookingID,"1","1","0");// here 1 denotes payment status is recieved
            }else{
                btnOk.setClickable(true);
                btnOk.setEnabled(true);
                ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
            }
        }
        if(view.getId()==R.id.btnFasttag){
//            isPrint = 1;
//
//            Log.i(BillGenerateActivity.LOG_TAG, "Reading...");
////            CToast.show(getApplicationContext(),"Reading...");
//            fastTag.startRead();
            isPrint = 1;
            choosepaymentMode= "UPI";
            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {

                Intent intent = new Intent();
                intent.setAction("com.pinelabs.masterapp.SERVER");
                intent.setPackage("com.pinelabs.masterapp");
                bindService(intent, connection, BIND_AUTO_CREATE);

            }else{
                btnOk.setClickable(true);
                btnOk.setEnabled(true);
                ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
            }

        }


//        if(view.getId()==R.id.bt_cash){
////            isPrint = 1;
////
////            Log.i(BillGenerateActivity.LOG_TAG, "Reading...");
//////            CToast.show(getApplicationContext(),"Reading...");
////            fastTag.startRead();
//            isPrint = 1;
//            choosepaymentMode= "CASH";
//            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
//
//                Intent intent = new Intent();
//                intent.setAction("com.pinelabs.masterapp.SERVER");
//                intent.setPackage("com.pinelabs.masterapp");
//                bindService(intent, connection, BIND_AUTO_CREATE);
//
//            }else{
//                btnOk.setClickable(true);
//                btnOk.setEnabled(true);
//                ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
//            }
//
//        }
//        if(view.getId()==R.id.btnCard){
//            isPrint = 1;
//            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {
//
//            //create  Json to do payment by card
//            JSONObject jsonRequest = new JSONObject();
//            JSONObject jsonOptionalParams = new JSONObject();
//            JSONObject jsonReferences = new JSONObject();
//            JSONObject jsonCustomer = new JSONObject();
//
//            try {
//                //Building References Object
//                jsonReferences.put("reference1", bookingno);
//                jsonReferences.put("reference2", vechile_no);
//                jsonReferences.put("reference3", checkintime);
//
        if(view.getId()==R.id.btnCard){
            isPrint = 1;
            choosepaymentMode= "CARD";
            if(ConnectionStatus.checkConnectionStatus(getApplicationContext())) {

                Intent intent = new Intent();
                intent.setAction("com.pinelabs.masterapp.SERVER");
                intent.setPackage("com.pinelabs.masterapp");
                bindService(intent, connection, BIND_AUTO_CREATE);


            }else{
                btnOk.setClickable(true);
                btnOk.setEnabled(true);
                ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
            }

        }
////Passing Additional References
//                JSONArray array = new JSONArray();
//                array.put(SharedStorage.getValue(getApplicationContext(),"AgencyName"));
//               // array.put(SharedStorage.getValue(getApplicationContext(),"parkinglocation"));
//                array.put(SharedStorage.getValue(getApplicationContext(),"parkingslot"));
//                jsonReferences.put("additionalReferences",array);
//
////Building Customer Object
//               // jsonCustomer.put("name", SharedStorage.getValue(getApplicationContext(),"Userame"));
//                jsonCustomer.put("mobileNo", ownerphoneno);
//               /// jsonCustomer.put("email", "sanhik.chatterjee@vyomainnovusglobal.com");
//
////Building Optional params Object
//                jsonOptionalParams.put("references",jsonReferences);
//                jsonOptionalParams.put("customer",jsonCustomer);
//
//
//                //Building final request object
//                jsonRequest.put("amount", TotalPaybleAmount);
//                jsonRequest.put("options", jsonOptionalParams);
//                Log.e("request", jsonRequest.toString());
//
//                String strRequest = "Payment Initiated : "+jsonRequest.toString();
//                try{
//                    SetEzetapTransactionLog(BookingID,strRequest);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                doPay(jsonRequest);// pay using EzeAPI
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else{
//                btnOk.setClickable(true);
//                btnOk.setEnabled(true);
//             ShowAlertDialog.showAlertDialogFailure(this, getResources().getString(R.string.nonetavailable));
//        }
//
//        }

    }

    // request to EzeAPI  for payment
    private void doPay(JSONObject jsonRequest) {
        EzeAPI.pay(this, REQUEST_CODE_PAY, jsonRequest);
    }

    //fasttag data read representation
    @Override
    public void onFasTagRead(String s) {
       // CToast.show(BillGenerateActivity.this,s);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("eazy_Tap")){
//            if(fastTag != null){
//                fastTag.resume(this);
//            }
//
//
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if(SharedStorage.getValue(BillGenerateActivity.this,"printer_name").equals("eazy_Tap")){
//            if(fastTag != null){
//                fastTag.pause();
//            }
//
//        }


    }
    // for location line break
    public static String[] breakStringToLines(String str, int maxLength)
    {
        StringBuilder result = new StringBuilder();
        while (str.length() > maxLength)
        {
            // Attempt to break on whitespace first,
            int breakingIndex = lastIndexOfRegex(str, "\\s", maxLength);

            // Then on other non-alphanumeric characters,
            if (breakingIndex == NOT_FOUND) breakingIndex = lastIndexOfRegex(str, "[^a-zA-Z0-9]", maxLength);

            // And if all else fails, break in the middle of the word
            if (breakingIndex == NOT_FOUND) breakingIndex = maxLength;

            // Append each prepared line to the builder
            result.append(str.substring(0, breakingIndex + 1));
            result.append("");

            // And start the next line
            str = str.substring(breakingIndex + 1);
        }

        // Check if there are any residual characters left
        if (str.length() > 0)
        {
            result.append(str);
        }

        Log.e("check", Arrays.toString(result.toString().split("\n")));

        // Return the resulting string
        return result.toString().split("\n");
    }
    // lastIndexOfRegex
    public static int lastIndexOfRegex(String str, String toFind, int fromIndex)
    {

        // Limit the search by searching on a suitable substring
        return lastIndexOfRegex(str.substring(0, fromIndex), toFind);
    }

    //lastIndexOfRegex
    public static int lastIndexOfRegex(String str, String toFind)
    {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = NOT_FOUND;

        // Search for the given pattern
        while (matcher.find())
        {
            lastIndex = matcher.start();
        }
        return lastIndex;
    }

    private ServiceConnection connection = new ServiceConnection() {
        Messenger mServerMessenger;
        boolean isBound;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mServerMessenger = new Messenger(service);
            isBound = true;
            Message message = Message.obtain(null, 1001);
            Bundle data = new Bundle();

            Gson gson=new Gson();
            HashMap<String,String> map=new HashMap<>();
            map.put("ApplicationId","8673ddad1b064f25aa3a25c00691fc8f");
            map.put("UserId","user1234");
            map.put("MethodId","1001");
            map.put("VersionNo","1.0");

            HashMap<String,String> map1=new HashMap<>();
            if (choosepaymentMode.equals("CARD")){
                map1.put("TransactionType","4001");
            }else if (choosepaymentMode.equals("UPI")){
                map1.put("TransactionType","5123");
            }

            map1.put("BillingRefNo","TXN12345678");
            map1.put("PaymentAmount",TotalPaybleAmount + "00");


            HashMap<String,HashMap<String,String>> h1=new HashMap<>();
            h1.put("Header",map);
            h1.put("Detail",map1);


            Log.e("tag",gson.toJson(h1));


            data.putString("MASTERAPPREQUEST", gson.toJson(h1));

            message.setData(data);

            try {

                message.replyTo = new Messenger(new IncomingHandler());

                mServerMessenger.send(message);


            } catch (RemoteException e) {

                e.printStackTrace();

            }

        }


        @Override
        public void onServiceDisconnected(ComponentName name)
        {

            mServerMessenger = null;

            isBound = false;

        }

    };



//get the responce form the payment app->


//for handler the responce of pinlab--

    private class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            try {
            Bundle bundle = msg.getData();

            String value = bundle.getString("MASTERAPPRESPONSE"); // process the response Json as required.

            Log.e("Tagresponse",value);

            JSONObject jsonObject = new JSONObject(value);

            JSONObject response = jsonObject.getJSONObject("Response");
            int responsecode = response.getInt("ResponseCode");
            String ResponseMsg = response.getString("ResponseMsg");

            Log.e("responsecode", String.valueOf(responsecode));

            System.out.println("ResponseMsg: " + ResponseMsg);

                if (responsecode==0) {
                    if (choosepaymentMode.equals("CARD")){
                    PaymentMode="Card";
                    isPrint = 1;
                    SetEzetapTransactionLog(BookingID,response.toString());
                    paymentCollection(BookingID,"1","6","strTxnId");// here 1 denotes payment status is recieved

                    }else{
                        PaymentMode="UPI";
                        isPrint = 1;
                        SetEzetapTransactionLog(BookingID,response.toString());
                        paymentCollection(BookingID,"1","6","strTxnId");// here 1 denotes payment status is recieved

                    }
                    Log.i("PaymentService", "Payment successful: " + ResponseMsg);



                } else if (responsecode==7) {


//                    when user cancel the payment
                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                            BillGenerateActivity.this).create();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);
                    msg_txt.setText(ResponseMsg);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            unbindService(connection);// unbind the service connection
                             SetEzetapTransactionLog(BookingID,response.toString());

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else {
                    unbindService(connection);// unbind the service connection
                    SetEzetapTransactionLog(BookingID,response.toString());
                    Log.e("PaymentService", "Payment failed: " + ResponseMsg);
                    btnCard.setClickable(true);
                    btnCard.setEnabled(true);
                }
           // Toast.makeText(BillGenerateActivity.this,value, Toast.LENGTH_LONG).show();

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private void printBillUsingPineLab() {

        if (!isBound || mService == null) {
            Log.e("PrintService", "Service is not bound or is null");
            return;
        }

        Message message = Message.obtain(null, 1001);  // PrintData method code
        Bundle data = new Bundle();

        JSONObject printRequest = new JSONObject();

        try {

            String gstNo = SharedStorage.getValue(BillGenerateActivity.this,"AgencyGSTNo");

            // Header
            JSONObject header = new JSONObject();
            header.put("ApplicationId", "8673ddad1b064f25aa3a25c00691fc8f");
           // Your Application ID
//            header.put("ApplicationId", "8754f022bd7f475a9f29284a656d3401");
            header.put("UserId", "user1234");
            header.put("MethodId", "1002");          // PrintData method ID
            header.put("VersionNo", "1.0");
            printRequest.put("Header", header);

            // Detail
            JSONObject detail = new JSONObject();
            detail.put("PrintRefNo", "123456");     // Unique reference number
            detail.put("SavePrintData", true);      // Save print data on device

            // Print Data Array
            JSONArray printData = new JSONArray();

            // Title Line: OUTSLIP
            JSONObject printTitle = new JSONObject();
            printTitle.put("PrintDataType", 0);        // Text
            printTitle.put("PrinterWidth", 32);
            printTitle.put("IsCenterAligned", true);
            printTitle.put("DataToPrint", "OUTSLIP");
            printData.put(printTitle);

            JSONObject parking = new JSONObject();
            parking.put("PrintDataType", 0);        // Text
            parking.put("PrinterWidth", 32);
            parking.put("IsCenterAligned", true);
            parking.put("DataToPrint", "Parking Maintenance Charge");
            printData.put(parking);

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(BillGenerateActivity.this, "parkinglocation"), 35);

            for (String strLocName : arrLocName) {
                if (null != strLocName && !strLocName.trim().equals("")) {

                    JSONObject bookingNO = new JSONObject();
                    bookingNO.put("PrintDataType", 0);        // Text
                    bookingNO.put("PrinterWidth", 32);
                    bookingNO.put("IsCenterAligned", true);
                    bookingNO.put("DataToPrint", strLocName);
                    printData.put(bookingNO);



                }
            }

            JSONObject newLine = new JSONObject();
            newLine.put("PrintDataType", 0);
            newLine.put("PrinterWidth", 32);
            newLine.put("IsCenterAligned", true);
            newLine.put("DataToPrint", " ");
            printData.put(newLine);


            // Vehicle No Line
            JSONObject printVehicle = new JSONObject();
            printVehicle.put("PrintDataType", 0);
            printVehicle.put("PrinterWidth", 32);
            printVehicle.put("IsCenterAligned", false);
            printVehicle.put("DataToPrint", "Vehicle No: " + vechile_no);
            printData.put(printVehicle);



            // Booking No Line
            JSONObject booking = new JSONObject();
            booking.put("PrintDataType", 0);
            booking.put("PrinterWidth", 45);
            booking.put("IsCenterAligned", false);
            booking.put("DataToPrint", "Booking No. : " + bookingno);
            printData.put(booking);

            // Check-In Time Line
            JSONObject printCheckin = new JSONObject();
            printCheckin.put("PrintDataType", 0);
            printCheckin.put("PrinterWidth", 32);
            printCheckin.put("IsCenterAligned", false);
            printCheckin.put("DataToPrint", "CheckIn Time : " + checkintime);
            printData.put(printCheckin);


            // Check-In Time Line
            JSONObject printCheckout = new JSONObject();
            printCheckout.put("PrintDataType", 0);
            printCheckout.put("PrinterWidth", 32);
            printCheckout.put("IsCenterAligned", false);
            printCheckout.put("DataToPrint", "CheckIn Time : " + checkouttime);
            printData.put(printCheckout);


            if (!overtime_duration.equals("0")){
                // Set fifth line in Bitmap


                JSONObject duration = new JSONObject();
                duration.put("PrintDataType", 0);
                duration.put("PrinterWidth", 32);
                duration.put("IsCenterAligned", false);
                duration.put("DataToPrint", "Over Duration : " + overtime_duration);
                printData.put(duration);

            }

            JSONObject Totalduration = new JSONObject();
            Totalduration.put("PrintDataType", 0);
            Totalduration.put("PrinterWidth", 32);
            Totalduration.put("IsCenterAligned", false);
            Totalduration.put("DataToPrint", "Duration : " + TotalDuration);
            printData.put(Totalduration);

            JSONObject newLine5 = new JSONObject();
            newLine5.put("PrintDataType", 0);
            newLine5.put("PrinterWidth", 32);
            newLine5.put("IsCenterAligned", true);
            newLine5.put("DataToPrint", " ");
            printData.put(newLine5);


            JSONObject line = new JSONObject();
            line.put("PrintDataType", 0);
            line.put("PrinterWidth", 32);
            line.put("IsCenterAligned", true);
            line.put("DataToPrint", "-------------------------------");
            printData.put(line);

            JSONObject newLine6 = new JSONObject();
            newLine6.put("PrintDataType", 0);
            newLine6.put("PrinterWidth", 32);
            newLine6.put("IsCenterAligned", true);
            newLine6.put("DataToPrint", " ");
            printData.put(newLine6);

            JSONObject GST = new JSONObject();
            GST.put("PrintDataType", 0);
            GST.put("PrinterWidth", 32);
            GST.put("IsCenterAligned", false);
            GST.put("DataToPrint", "Total Amt. : " + "Rs. "+gst_fare);
            printData.put(GST);





            JSONObject CGST = new JSONObject();
            CGST.put("PrintDataType", 0);
            CGST.put("PrinterWidth", 32);
            CGST.put("IsCenterAligned", false);
            CGST.put("DataToPrint", "CGST @9% : " + "Rs. "+cgst_calc);
            printData.put(CGST);


            JSONObject SGST = new JSONObject();
            SGST.put("PrintDataType", 0);
            SGST.put("PrinterWidth", 32);
            SGST.put("IsCenterAligned", false);
            SGST.put("DataToPrint", "SGST @9% : " + "Rs. "+sgst_calc);
            printData.put(SGST);

            JSONObject paymentMode = new JSONObject();
            paymentMode.put("PrintDataType", 0);
            paymentMode.put("PrinterWidth", 32);
            paymentMode.put("IsCenterAligned", false);
            paymentMode.put("DataToPrint", "Pay "+PaymentMode+" : "+ "Rs. "+TotalPaybleAmount);
            printData.put(paymentMode);

            JSONObject newLine3 = new JSONObject();
            newLine3.put("PrintDataType", 0);
            newLine3.put("PrinterWidth", 32);
            newLine3.put("IsCenterAligned", true);
            newLine3.put("DataToPrint", " ");
            printData.put(newLine3);

            JSONObject line2 = new JSONObject();
            line2.put("PrintDataType", 0);
            line2.put("PrinterWidth", 32);
            line2.put("IsCenterAligned", true);
            line2.put("DataToPrint", "-------------------------------");
            printData.put(line2);

            JSONObject newLine4 = new JSONObject();
            newLine4.put("PrintDataType", 0);
            newLine4.put("PrinterWidth", 32);
            newLine4.put("IsCenterAligned", true);
            newLine4.put("DataToPrint", " ");
            printData.put(newLine4);


            JSONObject GSTNo = new JSONObject();
            GSTNo.put("PrintDataType", 0);
            GSTNo.put("PrinterWidth", 32);
            GSTNo.put("IsCenterAligned", false);
            GSTNo.put("DataToPrint", "GST No. " + gstNo);
            printData.put(GSTNo);



            // Example: Print a QR Code

//            String strVehicleNo = vehicle_number + "##" + checkinTime + "##" + bookingNumber + "##" + ivehicletype;
//
//            JSONObject printQR = new JSONObject();
//            printQR.put("PrintDataType", 4);          // 4 = QR code
//            printQR.put("PrinterWidth", 32);
//            printQR.put("IsCenterAligned", true);
//            printQR.put("DataToPrint", strVehicleNo);
//            printData.put(printQR);

            JSONObject newLine2 = new JSONObject();
            newLine2.put("PrintDataType", 0);
            newLine2.put("PrinterWidth", 32);
            newLine2.put("IsCenterAligned", true);
            newLine2.put("DataToPrint", " ");
            printData.put(newLine2);

            JSONObject companyName = new JSONObject();
            companyName.put("PrintDataType", 0);
            companyName.put("PrinterWidth", 32);
            companyName.put("IsCenterAligned", true);
            companyName.put("DataToPrint", "Managed by Rohini Enterprises");
            printData.put(companyName);

            JSONObject ownRisk = new JSONObject();
            ownRisk.put("PrintDataType", 0);
            ownRisk.put("PrinterWidth", 32);
            ownRisk.put("IsCenterAligned", true);
            ownRisk.put("DataToPrint", "www.s-parking.com");
            printData.put(ownRisk);

            JSONObject newLine7 = new JSONObject();
            newLine7.put("PrintDataType", 0);
            newLine7.put("PrinterWidth", 24);
            newLine7.put("IsCenterAligned", true);
            newLine7.put("DataToPrint", "\n\n");
            printData.put(newLine7);




            // Add array to detail
            detail.put("Data", printData);
            printRequest.put("Detail", detail);

            // Send request
            data.putString("MASTERAPPREQUEST", printRequest.toString());
            message.setData(data);
            message.replyTo = new Messenger(new BillGenerateActivity.IncomingHandlerPrint()); // Handle response

            mService.send(message);

        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
    }


    private class IncomingHandlerPrint extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("MASTERAPPRESPONSE");
            Log.i("PrintService", "Received response: " + response);

            try {
                // Handle the JSON response
                JSONObject responseObject = new JSONObject(response);
                JSONObject responseHeader = responseObject.getJSONObject("Header");
                JSONObject responseObj = responseObject.getJSONObject("Response");
                int responseCode = responseObj.getInt("ResponseCode");
                String responseMsg = responseObj.getString("ResponseMsg");

                if (responseCode==0) {

                    pagechange();

                    Log.i("PrintService", "Print successful: " + responseMsg);
                }
                else if (responseCode==1002){
                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                            BillGenerateActivity.this).create();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);
                    msg_txt.setText(responseMsg);
                    btnOk.setText(getResources().getString(R.string.reprint));
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            try {
                                printBillUsingPineLab();

                            } catch (Exception e) {

                                e.printStackTrace();

                            }
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                    Log.e("PrintService", "Print failed: " + responseMsg);
                }
                else{
                    final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(
                            BillGenerateActivity.this).create();

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);
                    msg_txt.setText(responseMsg);
                    btnOk.setText(getResources().getString(R.string.reprint));
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                    Log.e("PrintService", "Print failed: " + responseMsg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}







