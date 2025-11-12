package com.innovus.vyoma.s_parking_agentApollo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Calendar;

import data_objects.SParkingAgentModel;
import data_objects.bean.BookingBillBean;
import data_objects.bean.FreeParkingPriceType;
import data_objects.bean.ParkingPrice;
import data_objects.bean.UserBean;
import data_objects.bean.VehicleTypePrice;
import db.DatabaseHandler;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.SessionManager;
import utilities.constants.Urls;
import utilities.others.CToast;

public class LoginActivity extends Activity implements View.OnClickListener, AsyncResponse {

    private EditText m_EditTextUserName;
    private EditText m_EditTextPassword;
    private TextView terms_condition_txt;
    SharedPreferences m_preferences;
    private Button loginn_btn;
    private SessionManager session;
    RemoteAsync remoteAsync;
    private SpotsDialog progressDialog;
    boolean presstwice = false;
    private ConstraintLayout login_contennt;
    private SharedPreferences pref;
    private static String device_id;
    DatabaseHandler databaseHandler;
    int vehicle_type_count=0;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        session = new SessionManager(getApplicationContext());
        pref = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        Log.e("mono_share_home", "????" + pref.getString("tokenid", ""));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // local database initialization
        databaseHandler = new DatabaseHandler(getApplicationContext());
        if (session.isLoggedIn()){

            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        }
        initviews();

    }

    private void initviews() {

        m_EditTextUserName = (EditText) findViewById(R.id.EditText_UserName);
        m_EditTextPassword = (EditText) findViewById(R.id.EditText_Password);
        loginn_btn = (Button) findViewById(R.id.loginn_btn);
        login_contennt = (ConstraintLayout) findViewById(R.id.login_contennt);
        session = new SessionManager(getApplicationContext());
        loginn_btn.setOnClickListener(this);
        m_preferences = PreferenceManager.getDefaultSharedPreferences(this);

        terms_condition_txt =(TextView) findViewById(R.id.terms_condition_txt);

        SpannableString terms_privacyss = new SpannableString(getResources().getString(R.string.terms_condition_txt));

        ClickableSpan termsclickableSpan= new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dataModel.about_web_view=2;
                String web_view_string="http://www.s-parking.com/termscondition.html";
                String heading_about="Terms and Conditions";
                Intent intent=new Intent(LoginActivity.this,WebViewAboutUsActivity.class);
                intent.putExtra("web_view_string",web_view_string);
                intent.putExtra("heading_webview",heading_about);
                startActivity(intent);
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue_Lotus));
                ds.setUnderlineText(true);
            }
        };

        ClickableSpan privacyclickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                dataModel.about_web_view=2;
                String web_view_string="http://www.s-parking.com/privacypolicy.html";
                String heading_about="Privacy Policy";
                Intent intent=new Intent(LoginActivity.this,WebViewAboutUsActivity.class);
                intent.putExtra("web_view_string",web_view_string);
                intent.putExtra("heading_webview",heading_about);
                startActivity(intent);
                finish();
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.blue_Lotus));
                ds.setUnderlineText(true);
            }
        };

        terms_privacyss.setSpan(termsclickableSpan, 39, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        terms_privacyss.setSpan(privacyclickableSpan, 62, 76, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        terms_condition_txt.setText(terms_privacyss);
        terms_condition_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.loginn_btn) {
            if (validate()) {
                try {

                    device_id = Settings.Secure.getString(getContentResolver(),
                            Settings.Secure.ANDROID_ID);
                    Log.e("deviceId==>>", device_id);

                    /*TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    telephonyManager.getDeviceId();
                    device_id = telephonyManager.getDeviceId();*/
                    if (device_id == null) {
                        device_id = "1111111";

                    } else {
                        Log.e("deviceId==>>", device_id);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // login service call
                getLoginAccess(m_EditTextUserName.getText().toString().trim(),m_EditTextPassword.getText().toString().trim(),
                        device_id,pref.getString("tokenid", ""));
            }
        }
    }

    @Override
    public void onBackPressed() {
    }
    private void showMsg(String msg) {
        Snackbar snackbar = Snackbar
                .make(login_contennt, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
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
    // login service
    private void getLoginAccess(String userame, String password,String device_id,String pushId) {
        start_progress_dialog();
       // String login_url = Urls.GetAuthentication+"/5/"+userame+"/"+password+"/"+device_id;
        Urls Urls = new Urls();
        String login_url = Urls.GetLoginUser;
        //String login_url = Urls.GetLoginUserV10;


        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.GETLOGINUSER;
       // remoteAsync.type = RemoteAsync.GETLOGINUSERV10;
        remoteAsync.delegate = this;

        String urlParams =  "";
        try {

            urlParams = "UserTypeID=" + URLEncoder.encode("3", "UTF-8") +
                    "&LoginTypeID=" + URLEncoder.encode("", "UTF-8") +
                    "&UserName=" + URLEncoder.encode(userame, "UTF-8") +
                    "&Password=" + URLEncoder.encode(password, "UTF-8") +
                    "&DeviceID=" + URLEncoder.encode(device_id, "UTF-8") +
                    "&PushID=" + URLEncoder.encode(pushId, "UTF-8") +
                    "&FullName=" + URLEncoder.encode("", "UTF-8") +
                    "&EmailID=" + URLEncoder.encode("", "UTF-8") +
                    "&SocialID=" + URLEncoder.encode("", "UTF-8") +
                    "&ProfilePic=" + URLEncoder.encode("", "UTF-8");
        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams",urlParams);
        remoteAsync.execute(urlParams);

    }

    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.GETLOGINUSER)) {
     //   if (type.equals(RemoteAsync.GETLOGINUSERV10)) {
            //stop_progress_dialog();
            try {

                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    UserBean userBean=new UserBean();
                   // userBean.setFullname(obj.getString("full_name"));
                    userBean.setUid(obj.getString("u_id"));
                    userBean.setAgecyname(obj.getString("agent_name"));
                    userBean.setParkinngslot_number(obj.getString("area_code"));
                    userBean.setParkinglocation(obj.getString("location"));
                    SParkingAgentModel.getInstance().userBean = userBean;
                    SharedStorage.setValue(getApplicationContext(),"UserId", obj.getString("agent_id"));
                    SharedStorage.setValue(getApplicationContext(),"Userame", obj.getString("agent_name"));
                    SharedStorage.setValue(getApplicationContext(),"parkingslot", obj.getString("area_code"));
                    SharedStorage.setValue(getApplicationContext(),"parkinglocation", obj.getString("location"));
                    SharedStorage.setValue(getApplicationContext(),"parking_area_id", obj.getString("parking_area_id"));
                    SharedStorage.setValue(getApplicationContext(),"is_special_pass_available", obj.getString("is_special_pass_available"));
                    SharedStorage.setValue(getApplicationContext(),"FourWheelerRate", obj.getString("FourWheelerRate"));
                    SharedStorage.setValue(getApplicationContext(),"TwoWheelerRate", obj.getString("TwoWheelerRate"));
                    SharedStorage.setValue(getApplicationContext(),"agent_mode", "1");
                    SharedStorage.setValue(getApplicationContext(),"AgencyName", obj.getString("AgencyName"));
                    SharedStorage.setValue(getApplicationContext(),"AgencyGSTNo", obj.getString("AgencyGSTNo"));
                    SharedStorage.setValue(getApplicationContext(),"TwoWheelerSPRate", obj.getString("TwoWheelerSPRate"));
                    SharedStorage.setValue(getApplicationContext(),"FourWheelerSPRate", obj.getString("FourWheelerSPRate"));
                    SharedStorage.setValue(getApplicationContext(),"HeavyVehicleRate", obj.getString("HeavyVehicleRate"));
                    SharedStorage.setValue(getApplicationContext(),"FreeParkingFacility", obj.getString("FreeParkingFacility"));
                   // SharedStorage.setValue(getApplicationContext(),"ip","192.168.57.70");
                    SharedStorage.setValue(getApplicationContext(),"ip",obj.getString("AgentBoomControlHost"));

                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        JSONArray FreeParkingPriceList = obj.getJSONArray("FreeParkingPriceList");
                        if (FreeParkingPriceList.length() > 0) {
                            for (int i = 0; i < FreeParkingPriceList.length(); i++) {
                                JSONObject object = FreeParkingPriceList.getJSONObject(i);
                                FreeParkingPriceType freeParkingPriceType = new FreeParkingPriceType();
                                freeParkingPriceType.setStartTime(object.getString("StartTime"));
                                freeParkingPriceType.setEndTime(object.getString("EndTime"));
                                freeParkingPriceType.setPrice(object.getString("Price"));

                                databaseHandler.addFreeParkingPrice(freeParkingPriceType);

                            }

                        }

                        JSONArray TariffParkingAreaPriceList = obj.getJSONArray("TariffParkingAreaPriceList");
                        if (TariffParkingAreaPriceList.length() > 0) {
                            for (int i = 0; i < TariffParkingAreaPriceList.length(); i++) {
                                JSONObject object = TariffParkingAreaPriceList.getJSONObject(i);
                                VehicleTypePrice vehicleTypePrice = new VehicleTypePrice();
                                vehicleTypePrice.setVehicleTypeID(object.getString("VehicleTypeID"));
                                vehicleTypePrice.setVehicleType(object.getString("VehicleType"));
                                vehicleTypePrice.setFirstCharge(object.getString("FirstCharge"));
                                vehicleTypePrice.setHourlyCharge(object.getString("HourlyCharge"));
                                vehicleTypePrice.setMinDuration(object.getString("MinDuration"));
                                vehicleTypePrice.setRecursiveDuration(object.getString("RecursiveDuration"));

                                databaseHandler.addVehicleTypePrice(vehicleTypePrice);

                            }

                        }
                    }

                    session.setLogin(true);

                    startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                     //   databaseHandler.releaseDB();
//                    databaseHandler.deleteparkingPrice();
//                    PriceAsyncBean priceBean = new PriceAsyncBean();
//                    priceBean.setDatabaseHandler(databaseHandler);
//                    priceBean.setJsonArray(obj.getJSONArray("HourMinParkingPriceList"));
//
//                    PriceAsync priceAsync = new PriceAsync(this);
//                    priceAsync.execute(priceBean);




                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    stop_progress_dialog();
                    GenerateAuthToken();
                    Thread.sleep(300);
                    getLoginAccess(m_EditTextUserName.getText().toString().trim(),m_EditTextPassword.getText().toString().trim(),
                            device_id,pref.getString("tokenid", ""));

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    stop_progress_dialog();
                    GenerateAuthToken();

                }
                else {
                    stop_progress_dialog();
                    JSONObject msg = new JSONObject(output);
                   // showMsg(msg.getString("message"));
                    ShowAlertDialog.showAlertDialog(LoginActivity.this,msg.getString("message"));
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
                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialogFailure(LoginActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.PENDINGBILLGENERATE)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                CToast.show(getApplicationContext(),obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen

                    String bookingno = obj.getString("BookingNumber");
                    String BookingID = obj.getString("BookingID");
                    // String bookingstatus = obj.getString("bookingstatus");
                    String checkintime = obj.getString("CheckinTime");
                    String checkouttime = obj.getString("CheckoutTime");
                    // String ownername = obj.getString("ownername");
                    String ownerphoneno = obj.getString("VehicleOwnerContactNumber");
                    //String totalamount = obj.getString("totalamount");
                    String VehicleType = obj.getString("VehicleType");
                    String vechile_no = obj.getString("VehicleNumber");
                    String ParkingAreaName = obj.getString("ParkingAreaName");
                    String TotalDuration = obj.getString("TotalDuration");
                    String TotalParkingAmount = obj.getString("TotalParkingAmount");
                    String TotalPaybleAmount = obj.getString("TotalPaybleAmount");
                    String FineAmount = obj.getString("FineAmount");
                    String OfferAmount = obj.getString("OfferAmount");
                    String PaymentMode = obj.getString("PaymentMode");
                    String AgencyName = obj.getString("AgencyName");
                    String message = obj.getString("message");

                    SharedStorage.setValue(getApplicationContext(),"BookingID", obj.getString("BookingID"));

                    SharedStorage.setValue(getApplicationContext(),"billstatus", "1");

                    BookingBillBean bookingBillBean = new BookingBillBean(bookingno,BookingID,checkintime,checkouttime,ownerphoneno,VehicleType,
                            vechile_no,ParkingAreaName,TotalDuration,TotalParkingAmount,TotalPaybleAmount,FineAmount,OfferAmount,PaymentMode,
                            AgencyName,"0","0",message,"0");
                    DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                    databaseHandler.addbookingbill(bookingBillBean);



                    Intent intent = new Intent(LoginActivity.this,BillGenerateActivity.class);
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
                    intent.putExtra("message",message);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(LoginActivity.this, msg.getString("message"));
                    session.setLogin(false);
                }else {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(LoginActivity.this, getResources().getString(R.string.loginfailed)/*msg.getString("message")*/);

                }
            } catch (Exception e) {
                e.printStackTrace();
                session.setLogin(false);
            }
        }
    }


    public void pageChange(String status){
        Log.e("status===",status);
        if (status.equals("Success")){
            startActivity(new Intent(LoginActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
            finish();
        }
 //       else{
//            ShowAlertDialog.showAlertDialog(this,"Login Failed! Please try again");
//        }



    }

    private void addParkingPrice(int id, String parking_area, String policy, String shift_type, String date_time, String hour, String minutes,String vehicle_type,String parking_price) {
        ParkingPrice parkingPrice = new ParkingPrice();
        String dateTime[] = getDateTime();

        parkingPrice.setPp_id(id);
        parkingPrice.setPp_parking_area(parking_area);
        parkingPrice.setPp_policy(policy);
        parkingPrice.setPp_shift_type(shift_type);
        parkingPrice.setPp_lastupdateon(date_time);

        parkingPrice.setPp_hour(hour);
        parkingPrice.setPp_min(minutes);
        // set price of Two Wheeler
        parkingPrice.setPp_vehicle_type(vehicle_type);
        parkingPrice.setPp_price(parking_price);
        databaseHandler.addParkingPrice(parkingPrice);
    }


    // get current date and time
    private String[] getDateTime() {

        //calculation of hours using checkin and checkout time
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[3];
        int day =c.get(Calendar.DAY_OF_MONTH);
        String sday = "";
        if (day < 10) {
            sday = "0"+String.valueOf(day);
        } else {
            sday = String.valueOf(day);
        }
        //  dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DAY_OF_MONTH);
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ sday;
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;

        return dateTime;
    }

    private void endParking(String vehicle_number,String userid) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String start_parking_url = Urls.PendingBillGenerate;

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.PENDINGBILLGENERATE;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "VehicleNumber=" + URLEncoder.encode(vehicle_number, "UTF-8") +
                    "&AgentID=" + URLEncoder.encode(userid, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }


    void start_progress_dialog() {
        try {

            progressDialog = new SpotsDialog(LoginActivity.this, R.style.CustomWaitDialog);
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
    private boolean validate(){
        boolean result = true;

        if (m_EditTextUserName.getText().toString().equals("")){
            requestFocus(m_EditTextUserName);
            ShowAlertDialog.showAlertDialog(LoginActivity.this,getResources().getString(R.string.username_vld));
            result = false;
            return result;
        }
        if (m_EditTextPassword.getText().toString().equals("")){
            requestFocus(m_EditTextPassword);
            ShowAlertDialog.showAlertDialog(LoginActivity.this,getResources().getString(R.string.pass_vld));
            result = false;
            return result;
        }
        return result;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
