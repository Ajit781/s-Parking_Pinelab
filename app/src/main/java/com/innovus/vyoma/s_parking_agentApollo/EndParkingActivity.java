package com.innovus.vyoma.s_parking_agentApollo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.renderscript.Script;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.PassStoreListAdapter;
import adapter.VechileListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.BookingBillBean;
import data_objects.bean.DataObject;
import data_objects.bean.SpclPassStoreBean;
import data_objects.bean.VehicleType;
import db.DatabaseHandler;
import dmax.dialog.SpotsDialog;

import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.others.ConnectionStatus;

import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.hideKeyboard;

//unused
public class EndParkingActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse, AdapterView.OnItemSelectedListener {


    private EditText et_bookingid, et_checkinDate, m_EditText_VehicleNumber, et_checkinTime, et_acess_control;
    private Button btn_EndParkingScanQR = null;
    private Button btn_endParking;
    private RelativeLayout rl_scan;
    private TextView tv_vehicle_type;
    private Spinner spinner_Vehicle_Type;
    private ImageView iv_datepicker, iv_timepicker;
    private RadioGroup radioGroup_pass, radioGroup_vehicletype;
    private FrameLayout fl_asess_amount;
    private RadioButton radioButton_specialpass, radioButton_monthlypass, radioButton_twowheel, radioButton_fourwheel, radioButton_none;
    private ConstraintLayout endlay;
    private String blockCharacterSet = "~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹";
    private String m_strUserName;
    private Integer m_iUId;
    Integer iID;
    private static final int REQUEST_CODE_SCAN = 1;
    private String pending_vehicleNumber = "";
    int isServerError =0;
    String strVehicleType = "";
    String vehicle_number="";
    boolean isBackCamera = true;
    private CheckBox check_pass;
    //private IntentIntegrator m_qrScan = null;
    private TextView m_TxtVEndParkingMessage = null;
    private SpotsDialog progressDialog;
    private Integer paymode = 1;
    Integer iVehicleType = 0;
    private int year1;
    private int day;
    private int month;

    private String version_name = "";
    private int access_control_amount = 0;
    Animation animbounce, trans_left_in,trans_right_in,bottom_in,bottom_out;
    String advbookingid = "";

    /******* for custom qr scan **************/
//    private DecoratedBarcodeView barcodeScannerView;
    //private CaptureManager capture;
    private LinearLayout lay_scan;
    private View lay_scan_view;
   // private ZXingScannerView mScannerView;
    private int timeOut = 120;
    private Handler resultHandler;
    private TranslateAnimation translateAnimation;


    Calendar myCalendar = Calendar.getInstance();
    static final int DATE_PICKER_ID = 1111;
    static final int DATE_PICKER_ID_CASE = 1112;
    private LinearLayout ll_pass_main;

    DatePickerDialog.OnDateSetListener arivalfrag, departurefrag;
    DatePickerDialog datePickerDialog1, datePickerDialog;
    TimePickerDialog.OnTimeSetListener timefrag;
    TimePickerDialog timePickerDialog;

    Integer parkingrate = 0;
    String parkingareaid = "";
    private String checkintime = "";
    private String checkouttime = "";
    private String ownermobilenumber = "";
    private String parkingfee = "";
    private String[] str_checking;
    private String[] str_qrdata;
    DatabaseHandler databaseHandler;
    BookingBillBean bookingBillBean;
    private String passapplied = "0";
    private String strStoreid = "0";
    private  int deletedIndex=0;
    private DataObject deletedItem= new DataObject();

    private ImageView iv_printer,iv_close;
    int readBufferPosition;
    volatile boolean stopWorker;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    VechileListAdapter vechileListAdapter;
    PassStoreListAdapter passStoreListAdapter;

    Calendar mcurrentTime = Calendar.getInstance();
    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    int minute = mcurrentTime.get(Calendar.MINUTE);

    DataOutputStream mmOutputStream_forboom;

    private Context mContext;

    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog pDialog;
    RemoteAsync remoteAsync;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private ExecutorService mSingleThreadExecutor;
    ArrayList<VehicleType>vehicletypeArrayList = new ArrayList<VehicleType>();
    private FloatView floatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_parking);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        iID = Integer.valueOf(SharedStorage.getValue(getApplicationContext(),"UserId"));
        bottom_in = AnimationUtils.loadAnimation(EndParkingActivity.this.getApplicationContext(),
                R.anim.trans_bottom_in);
        bottom_out = AnimationUtils.loadAnimation(EndParkingActivity.this.getApplicationContext(),
                R.anim.trans_bottom_out);


        databaseHandler = new DatabaseHandler(getApplicationContext());
        try{
            if(ConnectionStatus.checkConnectionStatus(EndParkingActivity.this)) {
                VehicleTypeList();

            }else{
                final AlertDialog alertDialog = new AlertDialog.Builder(EndParkingActivity.this).create();
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                alertDialog.setView(dialogView);
                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                btnyes.setText(getResources().getString(R.string.retry));
                btnno.setText(getResources().getString(R.string.cancel));

                heading.setText(R.string.app_name);

                msg_txt.setText(getResources().getString(R.string.nonetavailable));
                //msg_txt.setText(getResources().getString(R.string.no_internet));

                btnno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                    }
                });
                btnyes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        VehicleTypeList();// for vehicle type

                    }
                });
                //Animate alert dialog box
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in,
                        android.R.animator.fade_out);
                alertDialog.show();
                alertDialog.setCancelable(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        initviews();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initviews(){

        dataModel.about_advanced_dash = 1;

        spinner_Vehicle_Type = (Spinner) findViewById(R.id.spinner_Vehicle_Type);
        et_bookingid = (EditText) findViewById(R.id.et_bookingid);
        et_checkinDate = (EditText) findViewById(R.id.et_checkinDate);
        et_checkinTime = (EditText) findViewById(R.id.et_checkinTime);
        et_acess_control = (EditText) findViewById(R.id.et_acess_control);
        m_EditText_VehicleNumber = (EditText) findViewById(R.id.et_VehiclenoEndParking);
        m_TxtVEndParkingMessage = (TextView) findViewById(R.id.TEXTVIEW_endparkingmessage);
        tv_vehicle_type = (TextView) findViewById(R.id.tv_vehicle_type);
        endlay = (ConstraintLayout) findViewById(R.id.endlay);
        check_pass = (CheckBox) findViewById(R.id.check_pass);
        btn_EndParkingScanQR = (Button) findViewById(R.id.btn_EndParkingScanQR);
        btn_endParking = (Button) findViewById(R.id.btn_endParking);
        radioGroup_pass = (RadioGroup) findViewById(R.id.radioGroup_pass);
        radioGroup_vehicletype = (RadioGroup) findViewById(R.id.radioGroup_vehicletype);
        radioButton_specialpass = (RadioButton) findViewById(R.id.radioButton_specialpass);
        radioButton_monthlypass = (RadioButton) findViewById(R.id.radioButton_monthlypass);
        radioButton_twowheel = (RadioButton) findViewById(R.id.radioButton_twowheel);
        radioButton_fourwheel = (RadioButton) findViewById(R.id.radioButton_fourwheel);
        radioButton_none = (RadioButton) findViewById(R.id.radioButton_none);
        iv_datepicker = (ImageView) findViewById(R.id.iv_datepicker);
        iv_printer = (ImageView) findViewById(R.id.iv_printer);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_timepicker = (ImageView) findViewById(R.id.iv_timepicker);
        ll_pass_main = (LinearLayout) findViewById(R.id.ll_pass_main);
        rl_scan = (RelativeLayout) findViewById(R.id.rl_scan);
        fl_asess_amount = (FrameLayout) findViewById(R.id.fl_asess_amount);

        // set clickable false for all inpur fields
        // set clickable false for all inpur fields

        spinner_Vehicle_Type.setEnabled(false);
        spinner_Vehicle_Type.setFocusable(false);
        tv_vehicle_type.setEnabled(false);
        tv_vehicle_type.setFocusable(false);
        et_bookingid.setEnabled(false);
        et_bookingid.setFocusable(false);
        et_checkinDate.setEnabled(false);
        et_checkinDate.setFocusable(false);
        et_checkinTime.setEnabled(false);
        et_checkinTime.setFocusable(false);
        m_EditText_VehicleNumber.setEnabled(false);
        m_EditText_VehicleNumber.setFocusable(false);

        /********* in that page scan ***************/
        //mScannerView = new ZXingScannerView(this);
        lay_scan= (LinearLayout) findViewById(R.id.lay_scan);

        m_EditText_VehicleNumber.setFilters(new InputFilter[] { filter });
       // m_qrScan = new IntentIntegrator(this);

        btn_endParking.setOnClickListener(this);
        btn_EndParkingScanQR.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        spinner_Vehicle_Type.setOnItemSelectedListener(this);

//        vehicletypeArrayList.add(new VehicleType("0", getResources().getString(R.string.selectvtype)));
//        vehicletypeArrayList.add(new VehicleType("1", "Two Wheeler"));
//        vehicletypeArrayList.add(new VehicleType("2", "Four Wheeler"));
//        vehicletypeArrayList.add(new VehicleType("3", "BUS And TRUCKS"));
//        vehicletypeArrayList.add(new VehicleType("4", "Cycle"));
//        vehicletypeArrayList.add(new VehicleType("5", "PREMIUM CAR"));
//        vehicletypeArrayList.add(new VehicleType("6", "COMMERCIAL AUTO TAXIS"));
//        vehicletypeArrayList.add(new VehicleType("7", "AMB/ARMY/UPP/VIP"));
//        vehicletypeArrayList.add(new VehicleType("8", "MVC STAFF"));
//        vehicletypeArrayList.add(new VehicleType("9", "SETTLEMENT PASS"));
//        vehicletypeArrayList.add(new VehicleType("10", "REGISTERED AUTO TAXIS"));
//        vehicletypeArrayList.add(new VehicleType("11", "RAILWAY EMPLOYEES"));
//        vehicletypeArrayList.add(new VehicleType("12", "Helmet for 2 Wheelers"));
//        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
//        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);
        // Spinner value settings
        // spinner_Vehicle_Type.setSelection(0);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, dataModel.vehicletypeArrayList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_Vehicle_Type.setAdapter(aa);
        aa.notifyDataSetChanged();

        if(iVehicleType!= 0){
            spinner_Vehicle_Type.setSelection(iVehicleType);

        }
        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            ll_pass_main.setVisibility(View.GONE);// visibility gone for testing for boom gate opening
        }else {
            ll_pass_main.setVisibility(View.GONE);
        }
        check_pass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    passapplied = "1";// means yes pass is Applied
                   // sp_store.setVisibility(View.VISIBLE);
                    SpecialPassList();

                }else {
                    passapplied= "0";// pass not applied
                    strStoreid = "0";
                   // sp_store.setVisibility(View.GONE);
                }
            }
        });
    }
    //
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        strVehicleType=spinner_Vehicle_Type.getSelectedItem().toString();
        iVehicleType = Integer.valueOf(dataModel.vehicletypeArrayList.get(position).getVehicleTypeId());
        tv_vehicle_type.setText(dataModel.vehicletypeArrayList.get(position).getVehicleTypeName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    // for vehicle type list
    private void VehicleTypeList() {
        //  start_progress_dialog();
        Urls Urls = new Urls();
        String login_url = Urls.VehicleTypeList;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.VEHICLETYPELIST;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "";
        } catch (Exception e) {
            /*Log.e("ParamsException-->", e.getMessage());*/
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>", urlParams);
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
    // special Pass list Request method
    private void SpecialPassList() {
        start_progress_dialog();

        Urls Urls = new Urls();
        String start_parking_url = Urls.SpecialPassList;
        // String start_parking_url = Urls.SetEndParking + "/" + bookingid;


        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.SPECIALPASSLIST;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "ParkingAreaID=" + URLEncoder.encode(SharedStorage.getValue(getApplicationContext(),"parking_area_id"), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    // vehicle number text  filter
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    // System.out.println("Input consist of only characters from 'a' to 'z'");
                    return "";

                }


//                if (source != null && blockCharacterSet.contains(("" + source.charAt(i)))) {
//                    return source.toString().substring(start,source.toString().length()-1);
//                }
            }
            return null;
        }
    };


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_endParking) {
            if (validate()) {
                if(isValid(m_EditText_VehicleNumber.getText().toString().trim())) {
                    boolean is_match = true;
                    String vehiclennum = m_EditText_VehicleNumber.getText().toString().trim();
                    vehicle_number = vehiclennum.replace(" ", "");
                    dataModel.vehicle_no=vehicle_number;
                    if(passapplied.equals("0")) {
                        // vehicle checked out without any pass
                        endParking(vehicle_number, String.valueOf(iID));

                    }else {
                        // vehicle checked out using pass
                        endParking(vehicle_number, String.valueOf(iID));

                    }

                }else {
                    ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_vehicle_msg));
                }

            }
        }
        else if(view.getId()==R.id.btn_EndParkingScanQR){
            // qr SCAN initialization
            //m_qrScan.initiateScan();
            btn_endParking.setVisibility(View.GONE);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 5);
            } else {
                // qr SCAN initialization
                //m_qrScan.initiateScan();
                try{
                    // Start the scan activity
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("Place a QR code inside the rectangle");
                    options.setBeepEnabled(true);
                    options.setBarcodeImageEnabled(true);

                    barcodeLauncher.launch(options);
                }catch(Exception e){
                    Log.e("Exception",e.toString());
                }
//                try{
//                    hideKeyboard(this);
//                    rl_scan.setVisibility(View.VISIBLE);
//                    rl_scan.setAnimation(bottom_in);
//
//                    mScannerView = new ZXingScannerView(this);
//                    lay_scan.addView(mScannerView);
//                    mScannerView.setResultHandler(this);
//                    mScannerView.setSoundEffectsEnabled(true);
//                    mScannerView.setAutoFocus(true);
//                    mScannerView.startCamera();
//
//                    //turn on flash light after 5pm
//                    String from = "6:00:00";
//                    String to = "16:59:59";
//                    String dateTime[] = getDateTime();
//                    String n = dateTime[2];
//                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                    Date date_from = formatter.parse(from);
//                    Date date_to = formatter.parse(to);
//                    Date dateNow = formatter.parse(n);
//                    if (date_from.before(dateNow) && date_to.after(dateNow)) {
//                        Log.e("Yes time between","Yes time between");
//                        mScannerView.setFlash(false);
//                    }else {
//                        mScannerView.setFlash(true);
//                        Log.e("Yes time else between","Yes time else between");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

        }
        else if (view.getId() == R.id.iv_close) {
           // mScannerView.stopCameraPreview();
           // mScannerView.stopCamera();
            btn_endParking.setVisibility(View.VISIBLE);
            rl_scan.setVisibility(View.GONE);
            rl_scan.setAnimation(bottom_out);
            lay_scan.removeAllViews();
        }
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime[] = new String[3];
        int day =c.get(Calendar.DAY_OF_MONTH);
        String sday ="";
        if (day < 10) {
            sday = "0"+String.valueOf(day);
        } else {
            sday = String.valueOf(day);
        }
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ sday;
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;
        return dateTime;
    }


    // expression for Validating Correct Vehicle Number Format
    public static boolean isValid(String str)
    {
        boolean isValid = false;
        String expression = "^[a-z_A-Z][a-z_A-Z0-9]*[0-9]$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }
    private boolean validate() {

        boolean result = true;
        if (et_bookingid.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.booking_vid));
            result = false;
            return result;

        } else if (et_checkinDate.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.checking_vdate));
            result = false;
            return result;

        } else if (et_checkinTime.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.checking_vtime));
            result = false;
            et_checkinTime.requestFocus();
            return result;

        } else if (et_bookingid.getText().toString().trim().length() < 15){
            ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.booking_validid));
            result = false;
            et_bookingid.requestFocus();
            return result;

        }else if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            if (et_acess_control.getText().toString().equals("")){
                ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.acess_vamount));
                result = false;
                et_acess_control.requestFocus();
            }
        }
        else if(iVehicleType == 0){
            ShowAlertDialog.showAlertDialog(EndParkingActivity.this, getResources().getString(R.string.vehicle_type_vld));
            result = false;
            return result;
        }
        return result;
    }

    // validating end parking if special pass is avialable
    private boolean validateendparking(){
        boolean result = true;

        if(SharedStorage.getValue(getApplicationContext(),"is_special_pass_available").equals("1")){
            if(check_pass.isChecked()){
                if(strStoreid.equals("0")){
                    ShowAlertDialog.showAlertDialog(EndParkingActivity.this,getResources().getString(R.string.validate_storepass));
                    result = false;
                    return result;
                }
            }
        }

        return result;
    }

    // this method is used for vehicle check out
    private void endParking(String vehicle_number,String userid) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String start_parking_url = Urls.GetCheckoutDetailsV20;
        // String start_parking_url = Urls.SetEndParking + "/" + bookingid;

        if(dataModel.dataObjectArrayList.size()>0){
            for (int i=0;i<dataModel.dataObjectArrayList.size();i++){
                if(dataModel.dataObjectArrayList.get(i).getM_strVehicleNo().equals(vehicle_number)){
                    dataModel.dataObjectArrayList.remove(i);
                }
            }
        }

        dataModel.about_advanced_dash = 1;
        pending_vehicleNumber = vehicle_number;

        remoteAsync = new RemoteAsync(start_parking_url);
        // remoteAsync.type = RemoteAsync.VEHICLECHECKOUT;
        remoteAsync.type = RemoteAsync.VEHICLECHECKOUTDETAILSV20;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "VehicleNumber=" + URLEncoder.encode(vehicle_number, "UTF-8") +
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&AgentID=" + URLEncoder.encode(userid, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(EndParkingActivity.this, R.style.CustomWaitDialog);
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

    @Override
    public void processFinish(String type, String output) {
        // else if (type.equals(RemoteAsync.VEHICLECHECKOUT)) {
        if (type.equals(RemoteAsync.VEHICLECHECKOUTDETAILSV20)) {
            stop_progress_dialog();
            try {
                Log.e("output-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    strStoreid = "0";
                    passapplied = "0";
                    dataModel.vehicle_no="";
                    dataModel.cameFrom="EndParking";
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
//                    String overtime_duration = obj.getString("OverTimeDuration");
//                    String overtime_amount = obj.getString("OverTimeAmount");
                    String overtime_duration = "0";
                    String overtime_amount = "0";

                    SharedStorage.setValue(getApplicationContext(),"BookingID", obj.getString("BookingID"));

                    SharedStorage.setValue(getApplicationContext(),"billstatus", "1");

                    BookingBillBean bookingBillBean = new BookingBillBean(bookingno,BookingID,checkintime,checkouttime,ownerphoneno,VehicleType,
                            vechile_no,ParkingAreaName,TotalDuration,TotalParkingAmount,TotalPaybleAmount,FineAmount,OfferAmount,PaymentMode,
                            AgencyName,overtime_duration,overtime_amount,message,"0");
                    databaseHandler.addbookingbill(bookingBillBean);
                    dataModel.cameFrom="EndParkingActivity";
                    Intent intent = new Intent(EndParkingActivity.this,BillGenerateActivity.class);
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
                    intent.putExtra("overtime_duration",overtime_duration);
                    intent.putExtra("overtime_amount",overtime_amount);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    finish();

                }
                else if (obj.getString("status").equals(Constants.PASS)){
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    strStoreid = "0";
                    passapplied = "0";
                    dataModel.about_advanced_dash = 0;

                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){

                    dataModel.about_advanced_dash = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
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


                    heading.setText(R.string.validation_name);
                    msg_txt.setText(getResources().getString(R.string.nonetavailable));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            isServerError = 1;
                            vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                            dataModel.vehicleCheckInBean = deletedItem;
                            //PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

                            dataModel.about_advanced_dash = 1;
                            dataModel.check_in_remove = 1;

                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            endParking(dataModel.vehicle_no, String.valueOf(iID));
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){
                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                        //get all parking details by parking id
                        // endParking(pending_vehicleNumber, String.valueOf(iID));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    dataModel.about_advanced_dash = 0;
                    isServerError = 0;
                    strStoreid = "0";
                    passapplied = "0";
                    pending_vehicleNumber = "";
                  //  vechileListAdapter.restoreItem(deletedItem, deletedIndex);
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                    /*alertDialog.setMessage(message);*/
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.PENDINGBILLGENERATE)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    //Redirecting to dashboard screen
                    isServerError = 0;
                    pending_vehicleNumber = "";

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


                    dataModel.cameFrom="EndParkingActivity";
                    Intent intent = new Intent(EndParkingActivity.this,BillGenerateActivity.class);
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
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                     /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));

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
                        //get all parking details by parking id
                        PendingBillGenerate(pending_vehicleNumber,SharedStorage.getValue(getApplicationContext(),"UserId"));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    isServerError = 0;
                    pending_vehicleNumber = "";
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(EndParkingActivity.this, msg.getString("message"));

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
                    ShowAlertDialog.showAlertDialogFailure(EndParkingActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (type.equals(RemoteAsync.SPECIALPASSLIST)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    JSONArray SpecialPassList = obj.getJSONArray("SpecialPassList");
                    ArrayList<SpclPassStoreBean> spclPassStoreBeanArrayList = new ArrayList<SpclPassStoreBean>();
                    spclPassStoreBeanArrayList.add(new SpclPassStoreBean("0", getResources().getString(R.string.selectpasstype),"","",""));
                    if (SpecialPassList.length() > 0) {
                        for (int i = 0; i < SpecialPassList.length(); i++) {
                            JSONObject object = SpecialPassList.getJSONObject(i);
                            SpclPassStoreBean spclPassStoreBean = new SpclPassStoreBean();


                            spclPassStoreBean.setPassStoreId(object.getString("SpecialPassID"));
                            spclPassStoreBean.setPassStoreName(object.getString("SpecialPassName"));
                            spclPassStoreBean.setSpecialPassAddress(object.getString("SpecialPassAddress"));
                            spclPassStoreBean.setAvgRequiredSpecialPass(object.getString("AvgRequiredSpecialPass"));
                            spclPassStoreBean.setStartingFrom(object.getString("StartingFrom"));


                            spclPassStoreBeanArrayList.add(spclPassStoreBean);

                        }
                        dataModel.spclPassStoreBeanArrayList.removeAll(dataModel.spclPassStoreBeanArrayList);
                        dataModel.spclPassStoreBeanArrayList.addAll(spclPassStoreBeanArrayList);
                        passStoreListAdapter  = new PassStoreListAdapter(EndParkingActivity.this, dataModel.spclPassStoreBeanArrayList) {
                            @Override
                            public boolean isEnabled(int position) {
                                if (position == 0) {
                                    return false;
                                } else {
                                    return true;
                                }
                            }
                            @Override
                            public View getDropDownView(int position, View convertView,
                                                        ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (position == 0) {
                                    // Set the hint text color gray
                                    tv.setTextColor(Color.GRAY);
                                } else {
                                    tv.setTextColor(Color.BLACK);
                                }
                                return view;
                            }
                        };
                     //   sp_store.setAdapter(passStoreListAdapter);

//                        sp_store.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                strStoreid=dataModel.spclPassStoreBeanArrayList.get(position).getPassStoreId();
//
//
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//
//                            }
//                        });
                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.retry));

                    heading.setText(R.string.validation_name);

                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            SpecialPassList();// service calling for Special pass list
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
                else {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(EndParkingActivity.this, msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else  if (type.equals(RemoteAsync.VEHICLETYPELIST)) {
            //stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    JSONArray vehicletypelistArray = obj.getJSONArray("VehicleTypeList");
                    ArrayList<VehicleType> vehicletypeArrayList = new ArrayList<VehicleType>();
                    vehicletypeArrayList.add(new VehicleType("0", getResources().getString(R.string.selectvtype)));
                    if (vehicletypelistArray.length() > 0) {
                        for (int i = 0; i < vehicletypelistArray.length(); i++) {
                            JSONObject object = vehicletypelistArray.getJSONObject(i);
                            VehicleType vehicleType = new VehicleType();
                            vehicleType.setVehicleTypeId(object.getString("vehicle_type_id"));
                            vehicleType.setVehicleTypeName(object.getString("vehicle_type_name"));

                            vehicletypeArrayList.add(vehicleType);

                        }
                        Log.e("vehicleTypeList",vehicletypeArrayList.toString());
                        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
                        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);

                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(EndParkingActivity.this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_end_parking, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnyes = (Button) dialogView.findViewById(R.id.btnyes);
                    Button btnno = (Button) dialogView.findViewById(R.id.btnno);
                    btnyes.setText(getResources().getString(R.string.retry));
                    btnno.setText(getResources().getString(R.string.cancel));

                    heading.setText(R.string.app_name);

                    msg_txt.setText(getResources().getString(R.string.nonetavailable));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnno.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            VehicleTypeList();// for vehicle type
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft =getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)) {
                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        VehicleTypeList();
                        //get all parking details by parking id
                        // endParking(pending_vehicleNumber, String.valueOf(iID));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialog(EndParkingActivity.this, msg.getString("message"));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // for pending bill generation
    private void PendingBillGenerate(String vehicle_number,String userid) {
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

    @Override
    public void onBackPressed() {
        dataModel.about_advanced_dash = 0;
        if(dataModel.check_in_remove!=2){

            dataModel.vehicleCheckInBean = null;

        }
        if(rl_scan.getVisibility() == View.VISIBLE){
            try {
                rl_scan.setVisibility(View.GONE);
                rl_scan.setAnimation(bottom_out);
                btn_endParking.setVisibility(View.VISIBLE);
                lay_scan.removeAllViews();

            }catch (Exception e){
                e.printStackTrace();
            }

        }else {
            startActivity(new Intent(EndParkingActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }

    }

    // Qr Scan result handler
   // @Override
//    public void handleResult(Result result) {
//
//        try{
//            if (result != null) {
//                String rawresult = result.getText();
//
//                Log.e("result", rawresult);
//                if (result.getText() == null) {
//                    m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                    btn_endParking.setVisibility(View.VISIBLE);
//                } else {
//
//                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
//                        Log.e("result--->", result.getText().toString().toUpperCase());
//                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
//                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
//                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
//                    str_checking=checkinobj.getString("checkintime").split(" ");
//                    et_checkinDate.setText(str_checking[0]);
//                    et_checkinTime.setText(str_checking[1]);
//                    et_bookingid.setText(checkinobj.getString("BookingID"));
//                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/
//
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        m_EditText_VehicleNumber.setText(str_qrdata[0]);
//                        str_checking = str_qrdata[1].split(" ");
//                        et_checkinDate.setText(str_checking[0]);
//                        et_checkinTime.setText(str_checking[1]);
//                        et_bookingid.setText(str_qrdata[2]);
//                        iVehicleType = Integer.valueOf(str_qrdata[3]);
//
//                        //ownermobilenumber = checkinobj.getString("mobilenum");
//                        spinner_Vehicle_Type.setSelection(iVehicleType);
//                        et_acess_control.setText(str_qrdata[4]);
//
//                    }else {
//                        Log.e("result--->", result.getText().toString().toUpperCase());
//                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
//                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
//                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
//                    str_checking=checkinobj.getString("checkintime").split(" ");
//                    et_checkinDate.setText(str_checking[0]);
//                    et_checkinTime.setText(str_checking[1]);
//                    et_bookingid.setText(checkinobj.getString("BookingID"));
//                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/
//
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        m_EditText_VehicleNumber.setText(str_qrdata[0]);
//                        str_checking = str_qrdata[1].split(" ");
//                        et_checkinDate.setText(str_checking[0]);
//                        et_checkinTime.setText(str_checking[1]);
//                        et_bookingid.setText(str_qrdata[2]);
//                        iVehicleType = Integer.valueOf(str_qrdata[3]);
//                        spinner_Vehicle_Type.setSelection(iVehicleType);
//                        //ownermobilenumber = checkinobj.getString("mobilenum");
//
//
//                    }
//
//                }
//            }else {
//                m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                btn_endParking.setVisibility(View.VISIBLE);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//       /* mScannerView.stopCameraPreview();
//
//        mScannerView.resumeCameraPreview(this);*/
//        //mScannerView.stopCameraPreview();
//       // mScannerView.stopCamera();
//        rl_scan.setVisibility(View.GONE);
//        btn_endParking.setVisibility(View.VISIBLE);
//        rl_scan.setAnimation(bottom_out);
//        lay_scan.removeAllViews();
//
//    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
            result -> {
                if(result.getContents() == null) {
                    Intent originalIntent = result.getOriginalIntent();
                    if (originalIntent == null) {
                        Log.d("MainActivity", "Cancelled scan");
                        Toast.makeText(EndParkingActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else if(originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                        Log.d("MainActivity", "Cancelled scan due to missing camera permission");
                        Toast.makeText(EndParkingActivity.this, "Cancelled due to missing camera permission", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("MainActivity", "Scanned");
                    Toast.makeText(EndParkingActivity.this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                    try{
                        if (result != null) {
                            String rawresult =  result.getContents();

                            Log.e("result", rawresult);
                            if (result.getContents() == null) {
                                m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
                                btn_endParking.setVisibility(View.VISIBLE);
                            } else {

                                if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                                    Log.e("result--->", result.getContents().toString().toUpperCase());
                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
                    str_checking=checkinobj.getString("checkintime").split(" ");
                    et_checkinDate.setText(str_checking[0]);
                    et_checkinTime.setText(str_checking[1]);
                    et_bookingid.setText(checkinobj.getString("BookingID"));
                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/

                                    String qr_result = result.getContents().toString().toUpperCase();
                                    str_qrdata = qr_result.split("##");
                                    m_EditText_VehicleNumber.setText(str_qrdata[0]);
                                    str_checking = str_qrdata[1].split(" ");
                                    et_checkinDate.setText(str_checking[0]);
                                    et_checkinTime.setText(str_checking[1]);
                                    et_bookingid.setText(str_qrdata[2]);
                                    iVehicleType = Integer.valueOf(str_qrdata[3]);

                                    //ownermobilenumber = checkinobj.getString("mobilenum");
                                    spinner_Vehicle_Type.setSelection(iVehicleType);
                                    et_acess_control.setText(str_qrdata[4]);

                                }else {
                                    Log.e("result--->", result.getContents().toString().toUpperCase());
                    /*JSONObject jsonObject = new JSONObject(result.getContents().toString());
                    JSONObject checkinobj = jsonObject.getJSONObject("Checkin");
                    m_EditText_VehicleNumber.setText(checkinobj.getString("vehicle_number"));
                    str_checking=checkinobj.getString("checkintime").split(" ");
                    et_checkinDate.setText(str_checking[0]);
                    et_checkinTime.setText(str_checking[1]);
                    et_bookingid.setText(checkinobj.getString("BookingID"));
                    iVehicleType = Integer.valueOf(checkinobj.getString("vehicletype"));*/

                                    String qr_result = result.getContents().toString().toUpperCase();
                                    str_qrdata = qr_result.split("##");
                                    m_EditText_VehicleNumber.setText(str_qrdata[0]);
                                    str_checking = str_qrdata[1].split(" ");
                                    et_checkinDate.setText(str_checking[0]);
                                    et_checkinTime.setText(str_checking[1]);
                                    et_bookingid.setText(str_qrdata[2]);
                                    iVehicleType = Integer.valueOf(str_qrdata[3]);
                                    spinner_Vehicle_Type.setSelection(iVehicleType);
                                    //ownermobilenumber = checkinobj.getString("mobilenum");


                                }

                            }
                        }else {
                            m_TxtVEndParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
                            btn_endParking.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
;
                    rl_scan.setVisibility(View.GONE);
                    btn_endParking.setVisibility(View.VISIBLE);


                }
            });


}
