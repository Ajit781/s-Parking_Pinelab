package com.innovus.vyoma.s_parking_agentApollo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.eze.api.EzeAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pax.dal.entity.EFontTypeAscii;
import com.pax.dal.entity.EFontTypeExtCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.PassStoreListAdapter;
import adapter.VehicleTypeListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.SpclPassStoreBean;
import data_objects.bean.VehicleType;
import dmax.dialog.SpotsDialog;
import shared_pref.SharedStorage;
import utilities.ShowAlertDialog;
import utilities.async_tasks.AsyncResponse;
import utilities.async_tasks.RemoteAsync;
import utilities.constants.Constants;
import utilities.constants.Urls;
import utilities.eazytap.FloatView;
import utilities.eazytap.PrinterTester;
import utilities.printer_utils.Utils;

public class VehicleCheckInActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse, AdapterView.OnItemSelectedListener {
    private Spinner spinner_Vehicle_Type,sp_store;
    private EditText et_owner_ph_no,et_Vehicleno;
    private Button btn_checkin,default_number_btn,btn_ScanQR;
    private CheckBox check_pass;
    private TextView tv_vehicle_type;
    private LinearLayout ll_check_pass,ll_pass_main;
    private FloatView floatView;
    private TextView m_TxtVStartParkingMessage;
    private String blockCharacterSet = "~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹";
    RemoteAsync remoteAsync;
    private SpotsDialog progressDialog;
    String strVehicleType = "";
    String vehicle_number = "";
    String CheckinTime = "";
    Integer iMobileNumberLength = 0;
    Integer iVehicleType = 0;
    private String passapplied = "0";
    private String strStoreid = "0";
    private String bookingNumber = "";
    private String[] str_checking;
    private String[] str_qrdata;
    int isServerError =0;
    Bitmap bmp;
    private Uri imageUri;
    volatile boolean stopWorker;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    DataOutputStream mmOutputStream_forboom;
    BluetoothDevice mmDevice_forboom;
    BluetoothSocket mmSocket_forboom;
    BluetoothAdapter mBluetoothAdapter_forboom;
    private String m_strUserName;
    private Integer m_iUId;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";

    /******* for custom qr scan **************/
    private LinearLayout lay_scan;
    private RelativeLayout scanlay_main;
   // private ZXingScannerView mScannerView;
    private ImageButton close_btn;
    Animation bottomin,bottomout;
    ArrayList<VehicleType> vehicletypeArrayList = new ArrayList<VehicleType>();
    VehicleTypeListAdapter vehicleTypeListAdapter;
    PassStoreListAdapter passStoreListAdapter;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();

    private final int REQUEST_CODE_PRINT_BITMAP = 10029;// pax api print request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_check_in);

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+getResources().getString(R.string.app_name)+"</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if(getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        if(dataModel.vehicletypeArrayList.size()==0){
            VehicleTypeList();
        }
        if (savedInstanceState != null) {
            try {
                imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
                et_Vehicleno.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            m_strUserName = bundle.getString("myName");
            m_iUId = bundle.getInt("myID");
        }
        Log.e("location",SharedStorage.getValue(VehicleCheckInActivity.this,"parkinglocation"));

        initViews();


    }

    private void initViews() {

        spinner_Vehicle_Type= (Spinner) findViewById(R.id.spinner_Vehicle_Type);
        tv_vehicle_type = (TextView) findViewById(R.id.tv_vehicle_type);
        sp_store= (Spinner) findViewById(R.id.sp_store);
        et_owner_ph_no =(EditText)findViewById(R.id.et_owner_ph_no);
        et_Vehicleno =(EditText)findViewById(R.id.et_Vehicleno);
        btn_checkin =(Button)findViewById(R.id.btn_checkin);
        default_number_btn =(Button)findViewById(R.id.default_number_btn);
        btn_ScanQR =(Button)findViewById(R.id.btn_ScanQR);
        ll_pass_main = (LinearLayout) findViewById(R.id.ll_pass_main);
        ll_check_pass = (LinearLayout) findViewById(R.id.ll_check_pass);
        check_pass = (CheckBox) findViewById(R.id.check_pass);
        m_TxtVStartParkingMessage=(TextView)findViewById(R.id.TEXTVIEW_startparkingmessage);
        btn_checkin.setOnClickListener(this);
        default_number_btn.setOnClickListener(this);



/********* in that page scan ***************/
        lay_scan= (LinearLayout) findViewById(R.id.lay_scan);
        scanlay_main= (RelativeLayout) findViewById(R.id.scanlay_main);
        close_btn= (ImageButton) findViewById(R.id.close_btn);
        bottomin = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_in);
        bottomout = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.trans_bottom_out);

        et_Vehicleno.setFilters(new InputFilter[] { filter });



        if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
            et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
        } else {
            et_Vehicleno.setText(getResources().getString(R.string.statecode));

        }

        et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());

        spinner_Vehicle_Type.setOnItemSelectedListener(this);

//        vehicletypeArrayList.add(new VehicleType("0", getResources().getString(R.string.selectvtype)));
//        vehicletypeArrayList.add(new VehicleType("1", "Two Wheeler"));
//        vehicletypeArrayList.add(new VehicleType("2", "Four Wheeler"));
//        vehicletypeArrayList.add(new VehicleType("3", "BUS And TRUCKS"));
//        vehicletypeArrayList.add(new VehicleType("4", "Cycle"));
//        vehicletypeArrayList.add(new VehicleType("5", "PREMIUM CAR"));
//        vehicletypeArrayList.add(new VehicleType("6", "Commercial Taxi/Auto"));
//        vehicletypeArrayList.add(new VehicleType("7", "AMBULANCE/ARMY/VVIP/AUTH"));
//        vehicletypeArrayList.add(new VehicleTprintEazytapBillype("8", "MVC STAFF"));
//        vehicletypeArrayList.add(new VehicleType("9", "SETTLEMENT PASS"));
//        vehicletypeArrayList.add(new VehicleType("10", "REGISTERED PVT TAXI OLA/UBER"));
//        vehicletypeArrayList.add(new VehicleType("11", "RAILWAY EMPLOYEES"));
//        vehicletypeArrayList.add(new VehicleType("12", "Helmet for 2 Wheelers"));
//        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
//        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);
        // Spinner value settings
       // spinner_Vehicle_Type.setSelection(0);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, dataModel.vehicletypeArrayList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        vehicleTypeListAdapter  = new VehicleTypeListAdapter(VehicleCheckInActivity.this, dataModel.vehicletypeArrayList) {
//            @Override
//            public boolean isEnabled(int position) {
//                if (position == 0) {
//                    return false;
//                } else {
//                    return true;
//                }
//            }
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//                if (position == 0) {
//                    // Set the hint text color gray
//                    tv.setTextColor(Color.GRAY);
//                } else {
//                    tv.setTextColor(Color.BLACK);
//                }
//                return view;
//            }
//        };
        spinner_Vehicle_Type.setAdapter(aa);
        aa.notifyDataSetChanged();

        if(iVehicleType!= 0){
            spinner_Vehicle_Type.setSelection(iVehicleType);

        }
        if (!SharedStorage.getValue(VehicleCheckInActivity.this,"printer_name").equals("")){

            floatView = FloatView.getInstance(VehicleCheckInActivity.this);

        }
    }
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

    private boolean isValidMobile(String phone) {
        String expression = "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[6789]\\d{9}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btn_checkin){
            if (validate()) {
                if (isValid(et_Vehicleno.getText().toString().trim())) {
                    dataModel.isofflinecheckin = false;
                        btn_checkin.setClickable(false);
                        btn_checkin.setEnabled(false);
                        if (passapplied.equals("0")) {

                            startparkinng(et_Vehicleno.getText().toString().trim(), "0", et_owner_ph_no.getText().toString().trim());

                        } else {
                            startparkinng(et_Vehicleno.getText().toString().trim(), "4", et_owner_ph_no.getText().toString().trim());
                        }


                }else {
                    ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_vehicle_msg));
                }
            }
        }
        if (view.getId()==R.id.default_number_btn){

            StringBuilder defaultnumber = new StringBuilder();

            for(int i = 0;i<=getResources().getString(R.string.defaultmobilenumber).length();i++){
                if(i>=5){
                    defaultnumber.append("X");
                }else{
                    defaultnumber.append(getResources().getString(R.string.defaultmobilenumber).charAt(i));
                }
            }

            et_owner_ph_no.setText(defaultnumber);

            et_owner_ph_no.setSelection(et_owner_ph_no.getText().toString().length());

        }
//        if (view.getId()==R.id.btn_ScanQR){
//            try {
//
//                /*m_qrScan.initiateScan();*/
//
//                hideKeyboard(this);
//                scanlay_main.setVisibility(View.VISIBLE);
//                scanlay_main.setAnimation(bottomin);
//                mScannerView = new ZXingScannerView(this);
//                lay_scan.addView(mScannerView);
//                mScannerView.setResultHandler(this);
//                mScannerView.startCamera();
//                mScannerView.setSoundEffectsEnabled(true);
//                mScannerView.setAutoFocus(true);
//
//                //turn on flash light after 5pm
//                String from = "6:00:00";
//                String to = "16:59:59";
//                String dateTime[] = getDateTime();
//                String n = dateTime[2];
//                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
//                Date date_from = formatter.parse(from);
//                Date date_to = formatter.parse(to);
//                Date dateNow = formatter.parse(n);
//                if (date_from.before(dateNow) && date_to.after(dateNow)) {
//                    mScannerView.setFlash(false);
//                }else {
//                    mScannerView.setFlash(true);
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//
//        }

    }

    private void hideKeyboard(Activity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean validate() {
        boolean result = true;
        if (et_owner_ph_no.getText().toString().equals("") || et_owner_ph_no.getText().toString().length() != 10) {
            ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, getResources().getString(R.string.vld_mbl_msg));
            result = false;
            return result;

        } else if (et_Vehicleno.getText().toString().equals("")|| et_Vehicleno.getText().toString().length()<6) {
            ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, getResources().getString(R.string.vehicle_num_vld));
            result = false;
            return result;
        }

        if(iVehicleType == 0){
            ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, getResources().getString(R.string.vehicle_type_vld));
            result = false;
            return result;
        }
        return result;
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(VehicleCheckInActivity.this, R.style.CustomWaitDialog);
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
//    @Override
//    public void handleResult(com.google.zxing.Result result) {
//        try{
//            if (result != null)
//            {
//                //if qrcode has nothing in it
//                if (result.getText() == null)
//                {
//                    m_TxtVStartParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//                }
//                else {
//                    Log.e("result", result.getText().toString().toUpperCase());
//
//                    if(SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
//                        String qr_result = result.getText().toString().toUpperCase();
//                        str_qrdata = qr_result.split("##");
//                        str_checking = str_qrdata[0].split(" ");
//
//                    }else {
//                        if (result.getText().contains("#")) {
//
//                            ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this,getResources().getString(R.string.proper_qr_read));
//
//                        } else {
//                            et_Vehicleno.setText(result.getText().toString().toUpperCase());
//                        }
//                    }
//
//                }
//            }
//            else
//            {
//                m_TxtVStartParkingMessage.setText(getResources().getString(R.string.unable_qr_read));
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        mScannerView.stopCameraPreview();
//        mScannerView.stopCamera();
//        scanlay_main.setVisibility(View.GONE);
//        scanlay_main.setAnimation(bottomout);
//        lay_scan.removeAllViews();
//    }

    private boolean isValid(String name) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z][a-z_A-Z0-9]*[0-9]$";
        CharSequence inputStr = name;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
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
    // checkin service api call
    private void startparkinng(String vehicleNo,String PaymentMode,String alternate_phone) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String userid = SharedStorage.getValue(getApplicationContext(),"UserId");
        /*String start_parking_url = Urls.VehicleCheckIN+"/"+veichele_number;*/
        String start_parking_url = Urls.VehicleCheckINV10;

        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.VEHICLECHECKINV10;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "VehicleNumber=" + URLEncoder.encode(vehicleNo, "UTF-8") +
                    "&PaymentMode=" + URLEncoder.encode("0", "UTF-8")+
                    "&AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&AlternateContactNo=" + URLEncoder.encode(alternate_phone, "UTF-8") +
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&VehicleTypeID=" + URLEncoder.encode(String.valueOf(iVehicleType), "UTF-8")+
                    "&ContactNumber=" + URLEncoder.encode(alternate_phone, "UTF-8");

            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
            Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingreg",e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }
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


    private void VehicleTypeList() {
        start_progress_dialog();
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
    private void callBT_forboom() {
        start_progress_dialog();
        String login_url = "http://"+SharedStorage.getValue(getApplicationContext(),"ip")+"/bbctrl";;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.BBCTRL;
        remoteAsync.delegate = this;

//        GateOpenBoomBarrierBean gateOpenBoomBarrierBean= new GateOpenBoomBarrierBean(Integer.valueOf(status));
//
//        Gson gson = new Gson();

        String urlParams = "";
        try {

            /*********convert bean class values from Json to Gson ******/
          //  urlParams = gson.toJson(gateOpenBoomBarrierBean);
          //  urlParams = "status=" + URLEncoder.encode(status, "UTF-8") ;

        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams----->",urlParams);
        remoteAsync.execute(urlParams);
    }


    @Override
    public void processFinish(String type, String output) {
       // if (type.equals(RemoteAsync.VEHICLECHECKIN)) {
        if (type.equals(RemoteAsync.VEHICLECHECKINV10)) {
            stop_progress_dialog();

            try {
                Log.e("Response-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());
                dataModel.isofflinecheckin = true;


                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    check_pass.setChecked(false);
                    et_owner_ph_no.setText("");
                    dataModel.details_shown="1";
                    isServerError = 0;

                    bookingNumber =obj.getString("BookingNumber");
                    iVehicleType = Integer.valueOf(obj.getString("VehicleTypeID"));
                    vehicle_number = obj.getString("VehicleNumber");
                    CheckinTime =obj.getString("CheckinTime");


                    if (SharedStorage.getValue(VehicleCheckInActivity.this,"printer_name").equals("")) {
                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));
                        reset();
                        // commented because of bluetooth function

                    }
                    else if (SharedStorage.getValue(VehicleCheckInActivity.this,"printer_name").equals("eazy_Tap")) {
//                        ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                        try {

                           // printEazytapBill(vehicle_number,CheckinTime);
                            printEazytapBillNew(vehicle_number,CheckinTime);

                        } catch (Exception e) {
                            ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this,"");
                            e.printStackTrace();

                            if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
                                et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
                            } else {
                                et_Vehicleno.setText(getResources().getString(R.string.statecode));
                            }
                            et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());
                        }
//
//                        final AlertDialog alertDialog = new AlertDialog.Builder(
//                                this).create();
//
//                        final LayoutInflater inflater = this.getLayoutInflater();
//                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
//                        alertDialog.setView(dialogView);
//
//                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
//                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
//                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
//                        TextView title = (TextView) dialogView.findViewById(R.id.title);
//                        title.setText(getResources().getString(R.string.app_name));
//                        bt_cash.setText(getResources().getString(R.string.print));
//                        msg_txt.setText(obj.getString("message"));
//
//                        bt_cash.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialog.dismiss();
//
//                                try {
//                                    printEazytapBill(vehicle_number);
//                                } catch (Exception e) {
//                                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this,"");
//                                    e.printStackTrace();
//
//                                    if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
//                                        et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
//                                    } else {
//                                        et_Vehicleno.setText(getResources().getString(R.string.statecode));
//                                    }
//                                    et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());
//                                }
//
//                            }
//                        });
//                        bt_cancel.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                alertDialog.dismiss();
//
//                                if (SharedStorage.getValue(getApplicationContext(), "FreeParkingFacility").equals("1")) {
//                                    et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
//                                } else {
//                                    et_Vehicleno.setText(getResources().getString(R.string.statecode));
//                                }
//                                et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());
//                                reset();
//                            }
//                        });
//                        //Animate alert dialog box
//                        FragmentTransaction ft = getFragmentManager().beginTransaction();
//                        ft.setCustomAnimations(android.R.animator.fade_in,
//                                android.R.animator.fade_out);
//                        // Showing Alert Message
//                        alertDialog.show();
//                        alertDialog.setCancelable(false);

                        // commented because of bluetooth function

                    }
                }
//                else if(obj.getString("status").equals(Constants.NORMAL_CHECKIN)){
//
//                    final AlertDialog alertDialog = new AlertDialog.Builder(
//                            this).create();
//
//                    final LayoutInflater inflater = this.getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.customdialog_advance_normal_parking, null);
//                    alertDialog.setView(dialogView);
//                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
//                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
//                    Button btnOk = (Button) dialogView.findViewById(R.id.btnyes);
//                    Button btncancel = (Button) dialogView.findViewById(R.id.btncancel);
//                    // Setting Dialog Title
//                    //alertDialog.setTitle(title);
//                    heading.setText(R.string.validation_name);
//
//                    // Setting Dialog Message
//                    msg_txt.setText(obj.getString("message"));
//                    btncancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            alertDialog.dismiss();
//                        }
//                    });
//                    btnOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//                            dataModel.isofflinecheckin = false;
//
//                            if (validate()) {
//                                if (isValid(et_Vehicleno.getText().toString().trim())) {
//                                    dataModel.isofflinecheckin = false;
//                                    if(passapplied.equals("0")){
//                                        startparkinng(et_Vehicleno.getText().toString().trim(), "0", et_owner_ph_no.getText().toString().trim());
//                                    }else {
//                                        startparkinng(et_Vehicleno.getText().toString().trim(), "4", et_owner_ph_no.getText().toString().trim());
//                                    }
//
//
//                                }else {
//                                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, getResources().getString(R.string.vld_vehicle_msg));
//                                }
//                            }
//
//                        }
//                    });
//                    //Animate alert dialog box
//                    FragmentTransaction ft = getFragmentManager().beginTransaction();
//                    ft.setCustomAnimations(android.R.animator.fade_in,
//                            android.R.animator.fade_out);
//                    // Showing Alert Message
//                    alertDialog.show();
//
//                }

                else if (obj.getString("status").equals(Constants.INSUF)){
                    isServerError = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                    alertDialog.setView(dialogView);
                    // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                    Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);

                    msg_txt.setText(obj.getString("message"));

                    bt_cash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            dataModel.isofflinecheckin = false;
                            if(passapplied.equals("0")){
                                startparkinng(et_Vehicleno.getText().toString().trim(),"1",et_owner_ph_no.getText().toString().trim());
                            }else {
                                startparkinng(et_Vehicleno.getText().toString().trim(), "4", et_owner_ph_no.getText().toString().trim());
                            }

                        }
                    });
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
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


                }
                else if (obj.getString("status").equals(Constants.PASS)){
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this,msg.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    btn_checkin.setClickable(true);
                    btn_checkin.setEnabled(true);

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

                        }
                    });
                    btnyes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                            if (passapplied.equals("0")) {

                                startparkinng(et_Vehicleno.getText().toString().trim(), "0", et_owner_ph_no.getText().toString().trim());

                            } else {
                                startparkinng(et_Vehicleno.getText().toString().trim(), "4", et_owner_ph_no.getText().toString().trim());
                            }
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
                        if(passapplied.equals("0")){
                            startparkinng(et_Vehicleno.getText().toString().trim(), "0", et_owner_ph_no.getText().toString().trim());
                        }else {
                            startparkinng(et_Vehicleno.getText().toString().trim(), "4", et_owner_ph_no.getText().toString().trim());
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else{
                    JSONObject msg = new JSONObject(output);
                    isServerError =0;
                    btn_checkin.setClickable(true);
                    btn_checkin.setEnabled(true);
                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this,msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingresp",e.getMessage());
            }
        }
        else if (type.equals(RemoteAsync.BBCTRL)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    try {
                        Thread.sleep(300);
                        reset();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //Redirecting to dashboard screen
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));
                    if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
                    }else {
                        et_Vehicleno.setText(getResources().getString(R.string.statecode));
                    }
                    et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());


                }
                else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
//                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//                    LayoutInflater inflater = this.getLayoutInflater();
//                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
//                    alertDialog.setView(dialogView);
//                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
//                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
//                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
//                    btnOk.setText(getResources().getString(R.string.retry));
//
//                    heading.setText(R.string.validation_name);
//
//                    // Setting Dialog Message
//                    /*alertDialog.setMessage(message);*/
//                    msg_txt.setText(obj.getString("message"));
//
//                    btnOk.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            alertDialog.dismiss();
//
//                        }
//                    });
//                    //Animate alert dialog box
//                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
//                    ft.setCustomAnimations(android.R.animator.fade_in,
//                            android.R.animator.fade_out);
//                    // Showing Alert Message
//                    alertDialog.show();
//                    alertDialog.setCancelable(false);
                    if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
                    }else {
                        et_Vehicleno.setText(getResources().getString(R.string.statecode));
                    }
                    et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
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


                }
                else {

                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, msg.getString("message"));

                    if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
                        et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
                    }else {
                        et_Vehicleno.setText(getResources().getString(R.string.statecode));
                    }
                    et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());
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
                        passStoreListAdapter  = new PassStoreListAdapter(VehicleCheckInActivity.this, dataModel.spclPassStoreBeanArrayList) {
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
                        sp_store.setAdapter(passStoreListAdapter);

                        sp_store.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                strStoreid=dataModel.spclPassStoreBeanArrayList.get(position).getPassStoreId();


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
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

                    // Setting Dialog Message
                    /*alertDialog.setMessage(message);*/
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            SpecialPassList();
                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                    /*JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleInfoScanActivity.this, msg.getString("message"));*/

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        SpecialPassList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, msg.getString("message"));

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
                    ShowAlertDialog.showAlertDialogFailure(VehicleCheckInActivity.this,msg.getString("message"));
                    GenerateAuthToken();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else  if (type.equals(RemoteAsync.VEHICLETYPELIST)) {
            stop_progress_dialog();
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
                            if(!(object.getString("vehicle_type_id").equals("3") || (object.getString("vehicle_type_id").equals("5"))))
                            {
                                vehicleType.setVehicleTypeId(object.getString("vehicle_type_id"));
                                vehicleType.setVehicleTypeName(object.getString("vehicle_type_name"));

                                vehicletypeArrayList.add(vehicleType);
                            }
//                            vehicleType.setVehicleTypeId(object.getString("vehicle_type_id"));
//                            vehicleType.setVehicleTypeName(object.getString("vehicle_type_name"));
//
//                            vehicletypeArrayList.add(vehicleType);

                        }
                        Log.e("vehicleTypeList",vehicletypeArrayList.toString());
                        dataModel.vehicletypeArrayList.removeAll(dataModel.vehicletypeArrayList);
                        dataModel.vehicletypeArrayList.addAll(vehicletypeArrayList);

                    }

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialog_failurelayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.title);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                    btnOk.setText(getResources().getString(R.string.ok));

                    heading.setText(R.string.validation_name);
                    msg_txt.setText(getResources().getString(R.string.nonetavailable));
                    //msg_txt.setText(getResources().getString(R.string.no_internet));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();

                        }
                    });
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        VehicleTypeList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialog(VehicleCheckInActivity.this, msg.getString("message"));

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;

            if (mmOutputStream != null){

                mmOutputStream.close();
                mmInputStream.close();
                mmSocket.close();
            }

            /*myLabel.setText("Bluetooth Closed");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Text field reset.
    private void reset(){

        if (SharedStorage.getValue(getApplicationContext(),"FreeParkingFacility").equals("1")){
            et_Vehicleno.setText(getResources().getString(R.string.statecode_ts));
        }else {
            et_Vehicleno.setText(getResources().getString(R.string.statecode));
        }
        et_Vehicleno.setSelection(et_Vehicleno.getText().toString().length());

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
    private void printEazytapBillNew(String vehicle_number ,String CheckinTime) {
        Log.e("vehicle_number",vehicle_number);
        try
        {
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            Bitmap bitmap = Bitmap.createBitmap(400, 530, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 0));
            Rect bounds = new Rect();

            // Set first line in Bitmap
            paint.setTextSize((int) (26));
            String strText = "INSLIP";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/2;
            int y = 30;
            canvas.drawText(strText, x, y, paint);

//            // Set second line in Bitmap
//            paint.setTextSize((int) (22));
//            strText = "Welcome";
//            paint.getTextBounds(strText, 0, strText.length(), bounds);
//            x = (bitmap.getWidth() - bounds.width())/2;
//            y += 24;
//            canvas.drawText(strText, x, y, paint);
//            Log.e("after welcome",vehicle_number);

            String str = SharedStorage.getValue(VehicleCheckInActivity.this,"parkinglocation");
            String[] arrOfStr = str.split("-");

            for (String str_location : arrOfStr){
                // Set third line in Bitmap
                paint.setTextSize((int) (22));
                strText =str_location;
                paint.getTextBounds(strText, 0, strText.length(), bounds);
                x = (bitmap.getWidth() - bounds.width())/2;
                y += 24;
                canvas.drawText(strText, x, y, paint);
            }
            Log.e("after location",vehicle_number);


            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle No      : "+vehicle_number;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle Type   : "+strVehicleType;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set fifth line in Bitmap
            String dateTime[] = getDateTime();
            CheckinTime = CheckinTime.substring(0, CheckinTime.length() - 3);

            paint.setTextSize((int) (22));
            //strText = "CheckIn Time : "+dateTime[0]+" "+dateTime[1];
            strText = "CheckIn Time : "+CheckinTime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No     : "+bookingNumber;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 1;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set qr line in Bitmap
            //  String strVehicleNo = scanResults.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;
            String strVehicleNo = vehicle_number+"##"+CheckinTime+"##"+bookingNumber+"##"+iVehicleType;

            y += 10;
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 250, 250);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            //bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int xCount = 0; xCount < width; xCount++) {
                for (int yCount = 0; yCount < height; yCount++) {
                    bitmap.setPixel(xCount + 80, yCount + y, bitMatrix.get(xCount, yCount) ? Color.BLACK : Color.WHITE);
                }
            }
            paint.setTextSize((int) (22));
            strText = "Download s-Parking from Play Store";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            y += 260;
            canvas.drawText(strText, x, y, paint);


            String encodedImageData = getEncoded64ImageStringFromBitmap(bitmap);
            // Building Image Object
            jsonImageObj.put("imageData", encodedImageData);
            jsonImageObj.put("imageType", "JPEG");
            jsonRequest.put("image", jsonImageObj); // Pass this attribute when you have a valid captured signature image
            EzeAPI.printBitmap(VehicleCheckInActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);

            et_Vehicleno.post(new Runnable() {
                public void run() {
                    //      CToast.show(getApplicationContext(),status);
                    try {
                        Thread.sleep(300);
                        reset();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private String getEncoded64ImageStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = bmp;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedDate = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedDate;
    }
    private void printEazytapBill(String vehicle_number,String CheckinTime) {

        new Thread(new Runnable() {
            public void run() {
                try{
                    String dateTime[] = getDateTime();
                    PrinterTester.getInstance().init();
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().setGray(30);
                    StringBuilder print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("sParking", PAGE_WIDTH_TWO_INCH)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                    PrinterTester.getInstance().step(2);
             //       PrinterTester.getInstance().leftIndents(Short.parseShort("70"));
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_16);
                    print_bill = new StringBuilder();
                    print_bill.append(paddingCenter("Welcome", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                    PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("60"));

                    String str = SharedStorage.getValue(VehicleCheckInActivity.this,"parkinglocation");
                    String[] arrOfStr = str.split("-");

                    for (String str_location : arrOfStr){
                        print_bill = new StringBuilder();
                        print_bill.append(paddingCenter(str_location, PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                        PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                    }

//                PrinterTester.getInstance().printStr(SharedStorage.getValue(VehicleInfoScanActivity.this,"parkinglocation")+"\n",null);
                    //PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                    PrinterTester.getInstance().printStr("\n",null);
                    PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                    PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_16);
                    PrinterTester.getInstance().printStr("Vehicle No  : "+vehicle_number+"\n",null);
                    PrinterTester.getInstance().printStr("Vehicle Type: "+strVehicleType+"\n",null);
                    PrinterTester.getInstance().printStr("CheckIn Time: "+CheckinTime+"\n",null);
                    PrinterTester.getInstance().printStr("Booking No  : "+bookingNumber+"\n",null);
                    try {

                        String strVehicleNo = vehicle_number+"##"+CheckinTime+"##"+bookingNumber+"##"+iVehicleType;

                        QRCodeWriter writer = new QRCodeWriter();
                        try {
                            BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 250, 250);
                            int width = bitMatrix.getWidth();
                            int height = bitMatrix.getHeight();
                            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                            for (int x = 0; x < width; x++) {
                                for (int y = 0; y < height; y++) {
                                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                                }
                            }

                            Bitmap bitmap = pad(bmp,50,0);

                            if(bitmap!=null){
                                byte[] command = Utils.decodeBitmap(bitmap);
                                PrinterTester.getInstance().printBitmap(bitmap);

                            PrinterTester.getInstance().leftIndents(Short.parseShort("5"));

                                print_bill = new StringBuilder();
                                print_bill.append(paddingCenter("www.smartpower.co.in", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
                                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                            PrinterTester.getInstance().printStr("Download s-Parking App from Play Store\n",null);
                                PrinterTester.getInstance().printStr("\n",null);
                                PrinterTester.getInstance().printStr("\n",null);
                                PrinterTester.getInstance().step(60);
                            }else{
                                Log.e("Print Photo error", "the file isn't exists");
                            }
                        } catch (WriterException e) {
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    PrinterTester.getInstance().step(2);


                    final String status = PrinterTester.getInstance().start();
                    // int status_code = Integer.parseInt(PrinterTester.getInstance().getStatus());
                    et_Vehicleno.post(new Runnable() {
                        public void run() {
                            // CToast.show(getApplicationContext(),status);
                            if (status.equals("Out of paper ")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(
                                        VehicleCheckInActivity.this).create();

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
                                            printEazytapBill(vehicle_number,CheckinTime);

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
                                reset();
                                btn_checkin.setClickable(true);
                                btn_checkin.setEnabled(true);
                            }

                        }
                    });
                }catch(Exception e){
                   e.printStackTrace();
                }

            }
        }).start();

    }
    public static String getWhiteSpace(int size) {
        StringBuilder builder = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            builder.append(' ');
        }
        return builder.toString();
    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }

    /*******************  end of eazyTap printer printing code ****************/

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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        strVehicleType=spinner_Vehicle_Type.getSelectedItem().toString();
        iVehicleType = Integer.valueOf(dataModel.vehicletypeArrayList.get(position).getVehicleTypeId());
        tv_vehicle_type.setText(dataModel.vehicletypeArrayList.get(position).getVehicleTypeName());

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    @Override
    protected void onResume() {
        super.onResume();
//        if(mScannerView!= null){
//            // mScannerView.setResultHandler(this);
//            mScannerView.startCamera();
//        }

    }

    @Override
    public void onPause() {
        super.onPause();
//        if(mScannerView!= null){
//            mScannerView.stopCamera();
//        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        dataModel.details_shown="1";
        dataModel.about_advanced_dash = 0;

        if(scanlay_main.getVisibility() == View.VISIBLE){
            try {

              //  mScannerView.stopCameraPreview();
              //  mScannerView.stopCamera();
                scanlay_main.setVisibility(View.GONE);
                scanlay_main.setAnimation(bottomout);
                lay_scan.removeAllViews();

            }catch (Exception e){
                e.printStackTrace();
            }


        }else {
            try {
                closeBT();
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(VehicleCheckInActivity.this, DashBoardActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            finish();
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_PRINT_BITMAP) {
            try {
                if (intent != null && intent.hasExtra("response")) {
                    if (resultCode == RESULT_OK) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
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
                        reset();
                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleCheckInActivity.this).create();

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
                                    printEazytapBillNew(CheckinTime, vehicle_number);
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

            } catch (Exception e) {
                e.printStackTrace();
                //  ShowAlertDialog.showAlertDialog(this, e.getMessage());
                // Do your exception handling
            }
        }
    }

}