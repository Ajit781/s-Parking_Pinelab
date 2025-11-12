package com.innovus.vyoma.s_parking_agentApollo;

import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.isBound;
import static com.innovus.vyoma.s_parking_agentApollo.DashBoardActivity.mService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.crashlytics.android.Crashlytics;

import com.eze.api.EzeAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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
import java.util.Calendar;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adapter.VehicleTypeListAdapter;
import data_objects.SParkingAgentModel;
import data_objects.bean.GateOpenBoomBarrierBean;
import data_objects.bean.VehicleCheckInBean;
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
import utilities.others.CToast;
import utilities.printer_utils.Utils;


public class VehicleOwnerRegistrationActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse
        /*, SwipeListener */{

    /*private BarcodeDetector detector;*/
    private Context mContext;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    DataOutputStream mmOutputStream_forboom;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    private ImageView iv_printer;
    Thread workerThread;
    String bl_uuid;
    byte[] readBuffer;
    Bitmap bmp;
    int readBufferPosition;
    DatabaseHandler databaseHandler;
    volatile boolean stopWorker;
    private String m_strOTP;
    private EditText m_EditTxtName,EDITTEXT_Mobile_Number_new;
    private TextView m_EditTxtMobileNumber;
    private TextView m_EditTxtVehicleNumber;
    private TextView m_TextViewUserRegistrationInfo;
    SharedPreferences m_preferences;
    private String m_strUserName;
    private String m_Mobile_no,alt_mobile;
    private Integer m_iUId;
    private String m_strVehicleNumber;
    private Spinner m_SpinnerVehicleType;
    private Button bt_Register,default_number_btn;

    private String bookingNumber = "";
    String strName = "";
    String strStoreid = "";
    String passapplied = "";
    String strMobileNumber = "";
    String strVehicleNumber = "";
    String strVehicleType = "";
    Integer iMobileNumberLength = 0;
    Integer iVehicleType = 0;
    String vehicle_number = "";
    String CheckinTime ="";
    int isServerError =0;
    String payment_mode ="";
    private String blockCharacterSet = "@~#^|$%&*@)+=-_:;'<>?.,{}[]|/(!₹";
    RemoteAsync remoteAsync;
    private SpotsDialog progressDialog;
    VehicleTypeListAdapter vehicleTypeListAdapter;
    SParkingAgentModel dataModel = SParkingAgentModel.getInstance();
    private final int REQUEST_CODE_PRINT_BITMAP = 10029;// for APi print for pax

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
    private final static int MAX_LEFT_DISTANCE = 255;
    public static final int NOT_FOUND = -1;
    ProgressDialog dialog;

    private FloatView floatView;


    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 101:
                    Toast.makeText(VehicleOwnerRegistrationActivity.this,
                            "Printing now,pls wait for a moment", Toast.LENGTH_LONG)
                            .show();
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_owner_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = this;


        iv_printer = (ImageView) toolbar.findViewById(R.id.iv_printer);
        if(getWindow().getAttributes().softInputMode== WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        Bundle bundle = getIntent().getExtras();

        m_strUserName = bundle.getString("myName");
        m_iUId = Integer.valueOf(SharedStorage.getValue(getApplicationContext(), "UserId"));
        m_strVehicleNumber = bundle.getString("myVehicleNumber");
        //m_Mobile_no = bundle.getString("myMobileNumber");
        alt_mobile = bundle.getString("alt_mobile");
        strStoreid = bundle.getString("strStoreid");
        passapplied = bundle.getString("passapplied");
        isServerError = Integer.parseInt(bundle.getString("isServerError"));
        databaseHandler = new DatabaseHandler(getApplicationContext());


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

    @Override
    protected void onResume() {
        if(databaseHandler==null){
            databaseHandler = new DatabaseHandler(getApplicationContext());
        }
        super.onResume();
    }

    private void initviews(){
        m_EditTxtName = (EditText) findViewById(R.id.EDITTEXT_Name);
        EDITTEXT_Mobile_Number_new = (EditText) findViewById(R.id.EDITTEXT_Mobile_Number_new);
        m_EditTxtMobileNumber = (TextView) findViewById(R.id.EDITTEXT_Mobile_Number);
        m_EditTxtVehicleNumber = (TextView) findViewById(R.id.EDITTEXT_Vehicle_Number);
        bt_Register = (Button) findViewById(R.id.bt_Register);
        default_number_btn = (Button) findViewById(R.id.default_number_btn);

        m_TextViewUserRegistrationInfo = (TextView) findViewById(R.id.TEXTVIEW_ValidateRegistrationInfo);
        m_SpinnerVehicleType = (Spinner) findViewById(R.id.spinner_Vehicle_Type);

        bt_Register.setOnClickListener(this);
        default_number_btn.setOnClickListener(this);

        m_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        m_EditTxtVehicleNumber.setText(m_strVehicleNumber);
        m_EditTxtMobileNumber.setText(m_Mobile_no);

        m_SpinnerVehicleType.setSelection(0);
        m_EditTxtName.setFilters(new InputFilter[] { filter });
        EDITTEXT_Mobile_Number_new.setSelection(EDITTEXT_Mobile_Number_new.getText().length());


        strMobileNumber = m_EditTxtMobileNumber.getText().toString().trim();
        strMobileNumber = strMobileNumber.trim();
        strVehicleNumber = m_EditTxtVehicleNumber.getText().toString().trim();
        iMobileNumberLength = strMobileNumber.length();

        vehicleTypeListAdapter  = new VehicleTypeListAdapter(VehicleOwnerRegistrationActivity.this, dataModel.vehicletypeArrayList) {
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
        m_SpinnerVehicleType.setAdapter(vehicleTypeListAdapter);

        m_SpinnerVehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strVehicleType=m_SpinnerVehicleType.getSelectedItem().toString();
                iVehicleType = Integer.valueOf(dataModel.vehicletypeArrayList.get(position).getVehicleTypeId());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (!SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("")){

            floatView = FloatView.getInstance(VehicleOwnerRegistrationActivity.this);

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
        if (view.getId() == R.id.bt_Register) {
            strName = m_EditTxtName.getText().toString().trim();
            if (validate()) {
                if(EDITTEXT_Mobile_Number_new.getText().toString().contains("X")){

                    strMobileNumber = getResources().getString(R.string.defaultmobilenumber);

                    //  For Offline
                    if(isServerError == 1)

                   {
                        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                        btnOk.setText(getResources().getString(R.string.ok));

                        heading.setText(R.string.validation_name);

                        msg_txt.setText(getResources().getString(R.string.nonetavailable));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                        alertDialog.show();
                        alertDialog.setCancelable(false);
                    }

                    else {
                        if (dataModel.check_in == 0){

                            dataModel.isofflinecheckin = false;

                            if (strName.equals("")) {
                                getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),"NA", strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));

                            } else {
                                if (isValid(strName)){
                                    getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),strName, strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));

                                }else {
                                    ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_name));
                                }
                            }

                        }else {
                            if(passapplied.equals("0")){
                                startparkinng("0",alt_mobile);
                            }else {
                                startparkinng("4",alt_mobile);
                            }
                        }
                    }


                }else {
                    if (isValidMobile(EDITTEXT_Mobile_Number_new.getText().toString().trim())) {
                        strMobileNumber = EDITTEXT_Mobile_Number_new.getText().toString().trim();
                        if(isServerError == 1){
                            String dateTime[] = getDateTime();
                            String checkintime = dateTime[0]+" "+dateTime[2];
                            VehicleCheckInBean vehicleCheckInBean = new VehicleCheckInBean(strVehicleNumber,checkintime,
                                    String.valueOf(iVehicleType),strMobileNumber,passapplied,strStoreid,"0",0);
                            databaseHandler.addVehicleCheckIn(vehicleCheckInBean);

                            if (SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("")){

                                final AlertDialog alertDialogoffline = new AlertDialog.Builder(
                                        VehicleOwnerRegistrationActivity.this).create();

                                final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                                alertDialogoffline.setView(dialogView);
                                TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                                Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                                heading.setText(R.string.validation_name);

                                // Setting Dialog Message
                                msg_txt.setText(getResources().getString(R.string.check_in_message));

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialogoffline.dismiss();


                                        pae_change();


                                    }
                                });
                                //Animate alert dialog box
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.animator.fade_in,
                                        android.R.animator.fade_out);
                                // Showing Alert Message
                                alertDialogoffline.show();
                                alertDialogoffline.setCancelable(false);

                            }
                            else if(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("eazy_Tap")){

                                final AlertDialog alertDialogprint = new AlertDialog.Builder(
                                        VehicleOwnerRegistrationActivity.this).create();

                                final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                                View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                                alertDialogprint.setView(dialogView);
                                // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                                TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                                Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                                Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                                TextView title = (TextView) dialogView.findViewById(R.id.title);
                                title.setText(getResources().getString(R.string.app_name));

                                bt_cash.setText(getResources().getString(R.string.print));
                                msg_txt.setText(getResources().getString(R.string.check_in_message));

                                bt_cash.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.P)
                                    @Override
                                    public void onClick(View view) {
                                        alertDialogprint.dismiss();
                                        try {
                                           // printEazytapBill();
                                           // printEazytapBillNew(vehicle_number,CheckinTime);
                                            printEazytapBillUsingSDK(vehicle_number,CheckinTime);
                                        } catch (Exception e) {

                                            e.printStackTrace();

                                        }



                                    }
                                });
                                bt_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialogprint.dismiss();

                                        pae_change();

                                    }
                                });
                                //Animate alert dialog box
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.setCustomAnimations(android.R.animator.fade_in,
                                        android.R.animator.fade_out);
                                // Showing Alert Message
                                alertDialogprint.show();
                                alertDialogprint.setCancelable(false);

                            }


                        }else {
                            if (dataModel.check_in == 0){

                                if (strName.equals("")) {
                                    getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),"NA", strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));

                                } else {
                                    if (isValid(strName)){
                                        getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),strName, strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));

                                    }else {
                                        ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_name));
                                    }
                                }

                            }else {
                                if(passapplied.equals("0")){
                                    startparkinng("0",alt_mobile);
                                }else {
                                    startparkinng("4",alt_mobile);
                                }
                            }
                        }



                    }else {
                        Log.e("inelse","inelse part");
                        ShowAlertDialog.showAlertDialog(this, getResources().getString(R.string.vld_mbl_msg));
                    }
                }



            }
        }
        if(view.getId() == R.id.default_number_btn){
            StringBuilder defaultnumber = new StringBuilder();

            for(int i = 0;i<=getResources().getString(R.string.defaultmobilenumber).length();i++){
                if(i>=5){
                    defaultnumber.append("X");
                }else{
                    defaultnumber.append(getResources().getString(R.string.defaultmobilenumber).charAt(i));
                }
            }

            EDITTEXT_Mobile_Number_new.setText(defaultnumber);

            EDITTEXT_Mobile_Number_new.setSelection(EDITTEXT_Mobile_Number_new.getText().toString().length());

        }
    }

    private boolean validate() {
        boolean result = true;
         if (EDITTEXT_Mobile_Number_new.getText().toString().equals("") || EDITTEXT_Mobile_Number_new.getText().toString().length() != 10) {
            ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this, getResources().getString(R.string.vld_mbl_msg));
            result = false;
            return result;

        } else if (m_EditTxtVehicleNumber.getText().toString().equals("")) {
            ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this, getResources().getString(R.string.vehicle_num_vld));
            result = false;
            return result;
        }

        if(iVehicleType == 0){
            ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this, getResources().getString(R.string.vehicle_type_vld));
            result = false;
            return result;
        }
        return result;
    }

    void start_progress_dialog() {
        try{
            progressDialog = new SpotsDialog(VehicleOwnerRegistrationActivity.this, R.style.CustomWaitDialog);
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
    private boolean isValid(String name) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z ]*$";
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
    private void getRegisterAccess(String agentId, String name, String mobile, String vehicle, String iVehicleType) {
        start_progress_dialog();
        Urls Urls = new Urls();
        //String login_url = Urls.SetVehicleUserRegistration + "/" + name_replaced + "/" + mobile + "/" + vechile + "/" + iVehicleType + "/NA";
        String login_url = Urls.NewVehicleRegistration ;
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.NEWVEHICLEREGISTRATION;
        remoteAsync.delegate = this;
        String urlParams = "";
        try {

            urlParams = "AgentID=" + URLEncoder.encode(agentId, "UTF-8") +
                        "&VehicleOwnerMobileNumber=" + URLEncoder.encode(mobile, "UTF-8") +
                        "&VehicleNumber=" + URLEncoder.encode(vehicle, "UTF-8") +
                        "&VehicleTypeID=" + URLEncoder.encode(iVehicleType, "UTF-8") +
                        "&VehicleOwnerName=" + URLEncoder.encode(name, "UTF-8") +
                        "&RegistrationBy=" + URLEncoder.encode("1", "UTF-8");

            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        } catch (Exception e) {
            /*Log.e("ParamsException-->", e.getMessage());*/
            Crashlytics.log(Log.ERROR,"SParkingAgent_reg",e.getMessage());
        }
        Log.e(" UrlParams-->", urlParams);
        remoteAsync.execute(urlParams);

    }

    private void startparkinng(String PaymentMode,String alternate_phone) {
        start_progress_dialog();
        Urls Urls = new Urls();
        String userid = SharedStorage.getValue(getApplicationContext(),"UserId");
        /*String start_parking_url = Urls.VehicleCheckIN+"/"+veichele_number;*/
        String start_parking_url = Urls.VehicleCheckIN;

        payment_mode = PaymentMode;
        remoteAsync = new RemoteAsync(start_parking_url);
        remoteAsync.type = RemoteAsync.VEHICLECHECKIN;
        remoteAsync.delegate = this;

        String urlParams = "";
        try {
            urlParams = "AgentID=" + URLEncoder.encode(userid, "UTF-8") +
                    "&VehicleNumber=" + URLEncoder.encode(strVehicleNumber, "UTF-8") +
                    "&PaymentMode=" + URLEncoder.encode(PaymentMode, "UTF-8")+
                    "&IsSpecialPassApplied=" + URLEncoder.encode(passapplied, "UTF-8")+
                    "&SpecialPassID=" + URLEncoder.encode(strStoreid, "UTF-8")+
                    "&AlternateContactNo=" + URLEncoder.encode(alternate_phone, "UTF-8");
            //urlParams = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + selectedGoogleAddressBean.getPlace_id() + "&key=AIzaSyDzZucI3DFyg6-JxaIFqYCNREX8FT72JAM";
        }catch(Exception e){
            Log.e("ParamsException-->", e.getMessage());
            Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingreg",e.getMessage());
        }

        remoteAsync.execute(urlParams);
        Log.e("params>>",urlParams);
    }

    private void callBT_forboom(GateOpenBoomBarrierBean gateOpenBoomBarrierBean) {
        start_progress_dialog();
        String login_url = "http://"+SharedStorage.getValue(getApplicationContext(),"ip")+"/bbctrl";
        remoteAsync = new RemoteAsync(login_url);
        remoteAsync.type = RemoteAsync.BBCTRL;
        remoteAsync.delegate = this;

        Gson gson = new Gson();

        String urlParams = "";
        try {

            /*********convert bean class values from Json to Gson ******/
            urlParams = gson.toJson(gateOpenBoomBarrierBean);

        } catch (Exception e) {
            Log.e("ParamsException-->", e.getMessage());
        }

        Log.e("urlParams----->",urlParams);
        remoteAsync.execute(urlParams);
    }

    @Override
    public void processFinish(String type, String output) {
        if (type.equals(RemoteAsync.NEWVEHICLEREGISTRATION)) {
            stop_progress_dialog();
            try {
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());

                if (obj.getString("status").equals(Constants.SUCCESS)) {
                    /*isServerError = 0;*/
                    if(passapplied.equals("0")){
                        startparkinng("0",alt_mobile);
                    }else {
                        startparkinng("4",alt_mobile);
                    }
                    /*startparkinng("0",alt_mobile);*/
                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)) {
                    dataModel.check_in = 0;
                    dataModel.isofflinecheckin = true;
                    getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),strName, strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));


                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        getRegisterAccess(SharedStorage.getValue(getApplicationContext(),"UserId"),"NA", strMobileNumber, strVehicleNumber, String.valueOf(iVehicleType));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    //dataModel.check_in = 0;
                    dataModel.isofflinecheckin = true;
                    JSONObject msg = new JSONObject(output);
                    /*showMsg(msg.getString("message"));*/
                    ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this, msg.getString("message"));

                }

            } catch (Exception e) {
                e.printStackTrace();
                dataModel.isofflinecheckin = true;
               // Crashlytics.log(Log.ERROR,"SParkingAgent_reg_resp",e.getMessage());
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
                    ShowAlertDialog.showAlertDialogFailure(VehicleOwnerRegistrationActivity.this, msg.getString("message"));
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

                    //Redirecting to dashboard screen
                    ShowAlertDialog.showAlertDialog(this,obj.getString("message"));

                    Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                    finish();


                } else if(obj.getString("status").equals(Constants.NOT_SUCCESS)) {
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

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = this.getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();
                    alertDialog.setCancelable(false);

                } else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

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
                    ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this, msg.getString("message"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else  if (type.equals(RemoteAsync.VEHICLECHECKIN)) {
            stop_progress_dialog();

            try {
                Log.e("Response-->", output.toString());
                JSONObject obj = new JSONObject(output);
                Log.e("Response-->", obj.toString());
                dataModel.isofflinecheckin = true;
                if (obj.getString("status").equals(Constants.SUCCESS)) {

                    dataModel.details_shown="1";
                    dataModel.check_in = 0;
                    isServerError = 0;
                    bookingNumber =obj.getString("BookingNumber");

                    iVehicleType = Integer.valueOf(obj.getString("VehicleTypeID"));
                    vehicle_number = obj.getString("VehicleNumber");
                    CheckinTime =obj.getString("CheckinTime");

                    if (SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("")){

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                this).create();

                        final LayoutInflater inflater = this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                        alertDialog.setView(dialogView);
                        TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                        heading.setText(R.string.validation_name);

                        // Setting Dialog Message
                        msg_txt.setText(obj.getString("message"));

                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                finish();
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
                    else if(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("eazy_Tap")){

                        final AlertDialog alertDialogprint = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

                        final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialogprint.setView(dialogView);
                        // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));

                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.check_in_message));

                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();
                                try {
                                   // printEazytapBill();
                                   // printEazytapBillNew(vehicle_number,CheckinTime);
                                   // printEazytapBillUsingSDK(vehicle_number,CheckinTime);
                                    printBillUsingPineLab(CheckinTime,vehicle_number);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }



                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();

                                pae_change();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialogprint.show();
                        alertDialogprint.setCancelable(false);

                    }
                    else if(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("verifone")){

                        final AlertDialog alertDialogprint = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

                        final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialogprint.setView(dialogView);
                        // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));

                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.check_in_message));

                        bt_cash.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();
                                try {
                                   printEazytapBillNew(vehicle_number,CheckinTime);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();

                                pae_change();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialogprint.show();
                        alertDialogprint.setCancelable(false);

                    }



                }
                else if(obj.getString("status").equals(Constants.REG)){
                    dataModel.check_in = 0;
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            Intent intent = new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finish();


                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();


                }
                else if (obj.getString("status").equals(Constants.INSUF)){
                    dataModel.check_in = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                    alertDialog.setView(dialogView);
                    //TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                    Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);

                    //heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                    msg_txt.setText(obj.getString("message"));

                    bt_cash.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            if(passapplied.equals("0")){
                                startparkinng("1",alt_mobile);
                            }else {
                                startparkinng("4",alt_mobile);
                            }



                        }
                    });
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            final AlertDialog alertDialogcancel = new AlertDialog.Builder(VehicleOwnerRegistrationActivity.this).create();

                            final LayoutInflater inflatercancel = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                            View dialogView = inflatercancel.inflate(R.layout.customdialoglayout, null);
                            alertDialogcancel.setView(dialogView);
                            TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                            TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                            Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);
                            // Setting Dialog Title
                            //alertDialog.setTitle(title);
                            heading.setText(R.string.validation_name);

                            // Setting Dialog Message
                            msg_txt.setText(R.string.cancel_msg);
                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialogcancel.dismiss();
                                    Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                    finish();

                                }
                            });
                            //Animate alert dialog box
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            ft.setCustomAnimations(android.R.animator.fade_in,
                                    android.R.animator.fade_out);
                            // Showing Alert Message
                            alertDialogcancel.show();

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
                    dataModel.check_in = 0;

                    ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this,obj.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.FAILED)){
                    dataModel.check_in = 0;

                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            this).create();

                    final LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.customdialoglayout, null);
                    alertDialog.setView(dialogView);
                    TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                    TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                    Button btnOk = (Button) dialogView.findViewById(R.id.btnOk);

                    heading.setText(R.string.validation_name);

                    // Setting Dialog Message
                    msg_txt.setText(obj.getString("message"));

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            try {
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finish();

                        }
                    });
                    //Animate alert dialog box
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.animator.fade_in,
                            android.R.animator.fade_out);
                    // Showing Alert Message
                    alertDialog.show();

                    //ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this,obj.getString("message"));

                }
                else if (obj.getString("status").equals(Constants.NOT_SUCCESS)){
                    dataModel.check_in = 0;

                    String dateTime[] = getDateTime();
                    String checkintime = dateTime[0]+" "+dateTime[2];
                    VehicleCheckInBean vehicleCheckInBean = new VehicleCheckInBean(strVehicleNumber,checkintime,
                            String.valueOf(iVehicleType),strMobileNumber,passapplied,strStoreid,"0",0);
                    databaseHandler.addVehicleCheckIn(vehicleCheckInBean);
                    databaseHandler.addofflineVehicleCheckInNotSync(vehicleCheckInBean);

                    if (SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("")){

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
                                if(passapplied.equals("0")){
                                    startparkinng("0",alt_mobile);
                                }else {
                                    startparkinng("4",alt_mobile);
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
                    else if(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("eazy_Tap")){

                        final AlertDialog alertDialogprint = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

                        final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialogprint.setView(dialogView);
                        // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));

                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.check_in_message));

                        bt_cash.setOnClickListener(new View.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();
                                try {
                                   // printEazytapBill();
                                   // printEazytapBillNew(vehicle_number,CheckinTime);
                                    printEazytapBillUsingSDK(vehicle_number,CheckinTime);
                                } catch (Exception e) {

                                    e.printStackTrace();

                                }



                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();

                                pae_change();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialogprint.show();
                        alertDialogprint.setCancelable(false);

                    }
                    else if(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name").equals("verifone")){

                        final AlertDialog alertDialogprint = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

                        final LayoutInflater inflater = VehicleOwnerRegistrationActivity.this.getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.cash_dialog, null);
                        alertDialogprint.setView(dialogView);
                        // TextView heading = (TextView) dialogView.findViewById(R.id.heading);
                        TextView msg_txt = (TextView) dialogView.findViewById(R.id.msg_txt);
                        Button bt_cash = (Button) dialogView.findViewById(R.id.bt_cash);
                        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
                        TextView title = (TextView) dialogView.findViewById(R.id.title);
                        title.setText(getResources().getString(R.string.app_name));

                        bt_cash.setText(getResources().getString(R.string.print));
                        msg_txt.setText(getResources().getString(R.string.check_in_message));

                        bt_cash.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();
                                try {
                                    printEazytapBillNew(vehicle_number,CheckinTime);

                                } catch (Exception e) {

                                    e.printStackTrace();

                                }

                            }
                        });
                        bt_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialogprint.dismiss();

                                pae_change();

                            }
                        });
                        //Animate alert dialog box
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.setCustomAnimations(android.R.animator.fade_in,
                                android.R.animator.fade_out);
                        // Showing Alert Message
                        alertDialogprint.show();
                        alertDialogprint.setCancelable(false);

                    }

                }
                else if (obj.getString("status").equals(Constants.TOKEN_EXP)){

                    GenerateAuthToken();
                    try {
                        Thread.sleep(300);
                        if(passapplied.equals("0")){
                            startparkinng("0",alt_mobile);
                        }else {
                            startparkinng("4",alt_mobile);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    dataModel.check_in = 0;
                    isServerError = 0;
                    ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this,obj.getString("message"));
                }

            } catch (Exception e) {
                dataModel.check_in = 0;
                e.printStackTrace();
                Crashlytics.log(Log.ERROR,"SParkingAgent_startparkingreg_resp",e.getMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        dataModel.check_in =0;
        Intent intent = new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
        intent.putExtra("myID", m_iUId);
        intent.putExtra("myName", m_strUserName);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    // this will find a bluetooth printer device
    @SuppressLint("MissingPermission")
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
                /*myLabel.setText("No bluetooth adapter available");*/
            }

            if(!mBluetoothAdapter.isEnabled()) {
                if(dataModel.isbluetoothon == 0){
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
            }

            Boolean isnotconnected = true;
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"printer_name"))) {
                        Log.e("device",device.getName());
                        mmDevice = device;
                        isnotconnected = false;
                        //openBT();
                        break;
                    }
                }
            }


        }catch(Exception e){
            iv_printer.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

    public class clientSock extends Thread {
        public void run () {
            try {

                mmOutputStream_forboom.writeBytes(getResources().getString(R.string.commandtofire)); // anything you want
                mmOutputStream_forboom.flush();

                //closeBT_forBoom();
                pae_change();

            } catch (Exception e1) {
                e1.printStackTrace();
                return;
            }
        }
    }

    void pae_change(){

        Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
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
            e.printStackTrace();
            iv_printer.setVisibility(View.GONE);
        }
    }

    private String[] getDateTime() {
        final Calendar c = Calendar.getInstance();
        String dateTime [] = new String[3];
        int day =c.get(Calendar.DAY_OF_MONTH);
        String sday = "";
        if (day < 10) {
            sday = "0"+String.valueOf(day);
        } else {
            sday = String.valueOf(day);
        }
        dateTime[0] = c.get(Calendar.YEAR) +"-"+ String.valueOf(c.get(Calendar.MONTH)+1) +"-"+ sday;
        //dateTime[1] = c.get(Calendar.HOUR_OF_DAY) +":"+ c.get(Calendar.MINUTE);
        String curTimeSec = String.format("%02d:%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        String curTime = String.format("%02d:%02d",c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        dateTime[1] = curTime;
        dateTime[2] = curTimeSec;

        return dateTime;
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
    private void printEazytapBill() {

        new Thread(new Runnable() {
            public void run() {
                String dateTime[] = getDateTime();
                PrinterTester.getInstance().init();
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_16_32, EFontTypeExtCode.FONT_16_16);
                PrinterTester.getInstance().setGray(30);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("120"));
                StringBuilder print_bill = new StringBuilder();
                print_bill.append(paddingCenter("sParking", PAGE_WIDTH_TWO_INCH)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
                PrinterTester.getInstance().step(2);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("78"));
             //   PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_12_24,EFontTypeExtCode.FONT_16_32);
//                PrinterTester.getInstance().printStr("www.s-parking.com\n",null);
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter("www.s-parking.com", PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//                PrinterTester.getInstance().leftIndents(Short.parseShort("75"));
                print_bill = new StringBuilder();
                print_bill.append(paddingCenter(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation"), PAGE_WIDTH_TWO_INCH_SMALL)).append("\n");
                PrinterTester.getInstance().printStr(String.valueOf(print_bill),null);
//              PrinterTester.getInstance().printStr(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation")+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                PrinterTester.getInstance().fontSet(EFontTypeAscii.FONT_8_32,EFontTypeExtCode.FONT_16_32);
                PrinterTester.getInstance().printStr("Vehicle No    : "+m_EditTxtVehicleNumber.getText().toString()+"\n",null);
                PrinterTester.getInstance().printStr("CheckIn Time  : "+dateTime[0]+" "+dateTime[1]+"\n",null);
                PrinterTester.getInstance().printStr("Booking No    : "+bookingNumber+"\n",null);
                PrinterTester.getInstance().leftIndents(Short.parseShort("10"));
                try {
                    String strVehicleNo = m_EditTxtVehicleNumber.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+String.valueOf(iVehicleType);
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(strVehicleNo, BarcodeFormat.QR_CODE, 200, 200);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }

                        Bitmap bitmap = pad(bmp,100,0);

                        if(bitmap!=null){
                            byte[] command = Utils.decodeBitmap(bitmap);
                            PrinterTester.getInstance().printBitmap(bitmap);

//                            PrinterTester.getInstance().leftIndents(Short.parseShort("5"));
                            print_bill = new StringBuilder();
                            print_bill.append(paddingCenter("Download s-Parking App from Play Store", PAGE_WIDTH_TWO_INCH_SMALL_BOTTOM)).append("\n");
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
                m_EditTxtVehicleNumber.post(new Runnable() {
                    public void run() {
                        CToast.show(getApplicationContext(),status);
                        try {
                            Thread.sleep(300);
                            pae_change();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


    }

    public Bitmap pad(Bitmap Src, int padding_x, int padding_y) {
        Bitmap outputimage = Bitmap.createBitmap(Src.getWidth() + padding_x,Src.getHeight() + padding_y, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(outputimage);
        can.drawARGB(0xFF,0xFF,0xFF,0xFF); //This represents White color
        can.drawBitmap(Src, padding_x, padding_y, null);
        return outputimage;
    }

    /*******************  end of eazyTap printer printing code ****************/
    /*******************  end of eazyTap printer printing code ****************/
    // print using  verifone API
    private void printEazytapBillNew(String vehicle_number,String CheckinTime) {
        try
        {
            Bitmap bitmap = null;
            JSONObject jsonRequest = new JSONObject();
            JSONObject jsonImageObj = new JSONObject();

            String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this, "parkinglocation"), 35);
            if (arrLocName.length <= 1) {
                bitmap = Bitmap.createBitmap(400, 580, Bitmap.Config.ARGB_8888);
            } else {
                Integer bitmapHeight = 580 + ((arrLocName.length - 1) * 24);
                bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
            }


            bitmap.eraseColor(Color.WHITE);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(0,0, 0));
            Rect bounds = new Rect();

            // Set first line in Bitmap
            paint.setTextSize((int) (26));
            String strText = "INSLIP";
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 2;
            int y = 30;
            canvas.drawText(strText, x, y, paint);

            // Set third line in Bitmap
            paint.setTextSize((int) (22));
            //  strText = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation");
            //  String[] arrLocName = breakStringToLines(strText,35);
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
            String gstNo = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"AgencyGSTNo");

//            if(!gstNo.equals("")){
//
//                // Set fourth line in Bitmap
//                paint.setTextSize((int) (24));
//                strText = "GST - : " + gstNo;
//                paint.getTextBounds(strText, 0, strText.length(), bounds);
//                x = (bitmap.getWidth() - bounds.width()) / 2;
//                y += 24;
//                canvas.drawText(strText, x, y, paint);
//
//            }
            // Set fourth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Vehicle No       : "+vehicle_number;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 62;
            canvas.drawText(strText, x, y, paint);

            // Set fifth line in Bitmap
            String dateTime[] = getDateTime();
            CheckinTime = CheckinTime.substring(0, CheckinTime.length() - 3);

            paint.setTextSize((int) (22));
            //strText = "CheckIn Time : "+dateTime[0]+" "+dateTime[1];
            strText = "CheckIn Time  : "+CheckinTime;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set sixth line in Bitmap
            paint.setTextSize((int) (22));
            strText = "Booking No      : "+bookingNumber;
            paint.getTextBounds(strText, 0, strText.length(), bounds);
            x = (bitmap.getWidth() - bounds.width())/2;
            x = 25;
            y += 24;
            canvas.drawText(strText, x, y, paint);

            // Set qr line in Bitmap
            String strVehicleNo = vehicle_number+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+String.valueOf(iVehicleType);

            y += 20;
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

            // Set seventh line in Bitmap
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
            EzeAPI.printBitmap(VehicleOwnerRegistrationActivity.this, REQUEST_CODE_PRINT_BITMAP, jsonRequest);

//            m_EditTxtVehicleNumber.post(new Runnable() {
//                public void run() {
//              //      CToast.show(getApplicationContext(),status);
//                    try {
//                        Thread.sleep(300);
//                        pae_change();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
    private void printEazytapBillUsingSDK(String vehicle_number,String CheckinTime) {

        new Thread(new Runnable() {
            public void run() {
                try {

                    PrinterTester.getInstance().init();
                    Bitmap bitmap = null;
                    JSONObject jsonRequest = new JSONObject();
                    JSONObject jsonImageObj = new JSONObject();

                    String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this, "parkinglocation"), 35);
                    if (arrLocName.length <= 1) {
                        bitmap = Bitmap.createBitmap(400, 580, Bitmap.Config.ARGB_8888);
                    } else {
                        Integer bitmapHeight = 580 + ((arrLocName.length - 1) * 24);
                        bitmap = Bitmap.createBitmap(400, bitmapHeight, Bitmap.Config.ARGB_8888);
                    }

                    bitmap.eraseColor(Color.WHITE);

                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                   // paint.setTypeface(Typeface.DEFAULT_BOLD);
                    Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    Log.e("typeface", String.valueOf(typeface));
                    paint.setTypeface(typeface);
                    paint.setColor(Color.rgb(0, 0, 0));
                    Rect bounds = new Rect();

                    // Set first line in Bitmap
                    paint.setTextSize((int) (26));
                    String strText = "INSLIP";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    int x = (bitmap.getWidth() - bounds.width()) / 2;
                    int y = 30;
                    canvas.drawText(strText, x, y, paint);
// Set first line in Bitmap
//                    paint.setTextSize((int) (26));
//                    strText = "SmartPower";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width()) / 2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);
//                    paint.setTextSize((int) (22));
//                    strText = "www.s-parking.com";
//                    paint.getTextBounds(strText, 0, strText.length(), bounds);
//                    x = (bitmap.getWidth() - bounds.width()) / 2;
//                    y += 24;
//                    canvas.drawText(strText, x, y, paint);


                    // Set third line in Bitmap
                    paint.setTextSize((int) (22));
                    //  strText = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"parkinglocation");
                    //  String[] arrLocName = breakStringToLines(strText,35);
                    for (String strLocName : arrLocName) {
                        if (null != strLocName && !strLocName.trim().equals("")) {
                            paint.getTextBounds(strLocName, 0, strLocName.length(), bounds);
                            x = (bitmap.getWidth() - bounds.width()) / 2;
                            y += 24;
                            canvas.drawText(strLocName, x, y, paint);
                        }
                    }
                    String gstNo = SharedStorage.getValue(VehicleOwnerRegistrationActivity.this,"AgencyGSTNo");

//                    if(!gstNo.equals("")){
//
//                        // Set fourth line in Bitmap
//                        paint.setTextSize((int) (24));
//                        strText = "GST - : " + gstNo;
//                        paint.getTextBounds(strText, 0, strText.length(), bounds);
//                        x = (bitmap.getWidth() - bounds.width()) / 2;
//                        y += 24;
//                        canvas.drawText(strText, x, y, paint);
//
//                    }

                    // Set fourth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Vehicle No       : " + vehicle_number;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 62;
                    canvas.drawText(strText, x, y, paint);

                    // Set fifth line in Bitmap
                    String dateTime[] = getDateTime();
                    String checkInTime = CheckinTime.substring(0, CheckinTime.length() - 3);

                    paint.setTextSize((int) (24));
                    //strText = "CheckIn Time : "+dateTime[0]+" "+dateTime[1];
                    strText = "CheckIn Time : " + checkInTime;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    // Set sixth line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Booking No     : " + bookingNumber;
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    x = 20;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);


                    Typeface typefaceqr = Typeface.createFromAsset(getAssets(), "fonts/Outfit-Medium.ttf");
                    Log.e("typeface", String.valueOf(typefaceqr));
                    paint.setTypeface(typefaceqr);
                    paint.setColor(Color.rgb(0, 0, 0));
                    // Set qr line in Bitmap
                    //  String strVehicleNo = scanResults.getText().toString()+"##"+dateTime[0]+" "+dateTime[1]+"##"+bookingNumber+"##"+ivehicletype;
                    String strVehicleNo = vehicle_number + "##" + CheckinTime + "##" + bookingNumber + "##" + String.valueOf(iVehicleType);

                    y += 20;
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

                    Typeface typeface1 = Typeface.createFromAsset(getAssets(), "fonts/SignikaNegative-Medium.ttf");
                    Log.e("typeface", String.valueOf(typeface1));
                    paint.setTypeface(typeface1);
                    paint.setColor(Color.rgb(0, 0, 0));

                    // Set seventh line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Managed by Rohini Enterprise";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    y += 260;
                    canvas.drawText(strText, x, y, paint);

                    //  Set seventh line in Bitmap
                    paint.setTextSize((int) (24));
                    strText = "Parking at Owner's risk";
                    paint.getTextBounds(strText, 0, strText.length(), bounds);
                    x = (bitmap.getWidth() - bounds.width()) / 2;
                    y += 24;
                    canvas.drawText(strText, x, y, paint);

                    PrinterTester.getInstance().printBitmap(bitmap);

                    PrinterTester.getInstance().step(2);


                    final String status = PrinterTester.getInstance().start();
                    m_EditTxtVehicleNumber.post(new Runnable() {
                        public void run() {
                            // CToast.show(getApplicationContext(),status);
                            if (status.equals("Out of paper ")){
                                final AlertDialog alertDialog = new AlertDialog.Builder(
                                        VehicleOwnerRegistrationActivity.this).create();

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
                                            printEazytapBillUsingSDK(vehicle_number,CheckinTime);

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
                               // CToast.show(getApplicationContext(),status);
                                try {
                                    Thread.sleep(300);
                                    pae_change();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
//                finally {
//                    ShowAlertDialog.showAlertDialog(VehicleOwnerRegistrationActivity.this,"Unable to print receipt");
//
//                }

            }
        }).start();


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
                        try {
                            Thread.sleep(300);
                            pae_change();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Initialization of SDK is successful, proceed with your action
                    } else if (resultCode == RESULT_CANCELED) {
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

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
                                    printEazytapBillNew(vehicle_number,CheckinTime);
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


                    }else{
                        JSONObject response = new JSONObject(intent.getStringExtra("response"));
                        response = response.getJSONObject("error");
                        String errorCode = response.getString("code");
                        String errorMessage = response.getString("message");
                        Log.e("response", String.valueOf(response));

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                VehicleOwnerRegistrationActivity.this).create();

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
                                    printEazytapBillNew(vehicle_number,CheckinTime);
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

    private void page_change() {
        Intent intent=new Intent(VehicleOwnerRegistrationActivity.this, VehicleInfoScanActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        finish();
    }

    private String getEncoded64ImageStringFromBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = bmp;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedDate = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedDate;
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

    private void printBillUsingPineLab(String checkinTime, String vehicle_number) {

        if (!isBound || mService == null) {
            Log.e("PrintService", "Service is not bound or is null");
            return;
        }

        Message message = Message.obtain(null, 1001);  // PrintData method code
        Bundle data = new Bundle();

        JSONObject printRequest = new JSONObject();
        try {
            // Header
            JSONObject header = new JSONObject();
            header.put("ApplicationId", "8673ddad1b064f25aa3a25c00691fc8f"); // Your Application ID
//            header.put("ApplicationId", "8754f022bd7f475a9f29284a656d3401"); // Your Application ID
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

            // Row gap function (adds empty lines)

            // Title Line: INSLIP
            JSONObject printTitle = new JSONObject();
            printTitle.put("PrintDataType", 0);
            printTitle.put("PrinterWidth", 32);
            printTitle.put("IsCenterAligned", true);
            printTitle.put("DataToPrint", "INSLIP");
            printData.put(printTitle);

           // addEmptyLine.run(); // Row gap after title

            // Location Name lines
            String[] arrLocName = breakStringToLines(SharedStorage.getValue(VehicleOwnerRegistrationActivity.this, "parkinglocation"), 35);
            for (String strLocName : arrLocName) {
                if (strLocName != null && !strLocName.trim().isEmpty()) {
                    JSONObject locLine = new JSONObject();
                    locLine.put("PrintDataType", 0);
                    locLine.put("PrinterWidth", 32);
                    locLine.put("IsCenterAligned", true);
                    locLine.put("DataToPrint", strLocName);
                    printData.put(locLine);
                }
            }
          //  addEmptyLine.run(); // Add gap after location lines

            JSONObject newLine = new JSONObject();
            newLine.put("PrintDataType", 0);
            newLine.put("PrinterWidth", 32);
            newLine.put("IsCenterAligned", true);
            newLine.put("DataToPrint", " ");
            printData.put(newLine);

            // Vehicle No Line
            JSONObject printVehicle = new JSONObject();
            printVehicle.put("PrintDataType", 0);
            printVehicle.put("PrinterWidth", 45);
            printVehicle.put("IsCenterAligned", false);
            printVehicle.put("DataToPrint", "Vehicle No : " + vehicle_number);
            printData.put(printVehicle);



            // Check-In Time Line
            JSONObject printCheckin = new JSONObject();
            printCheckin.put("PrintDataType", 0);
            printCheckin.put("PrinterWidth", 45);
            printCheckin.put("IsCenterAligned", false);
            printCheckin.put("DataToPrint", "CheckIn Time : " + checkinTime);
            printData.put(printCheckin);



            // Booking No Line
            JSONObject booking = new JSONObject();
            booking.put("PrintDataType", 0);
            booking.put("PrinterWidth", 45);
            booking.put("IsCenterAligned", false);
            booking.put("DataToPrint", "Booking No. : " + bookingNumber);
            printData.put(booking);


            JSONObject newLine1 = new JSONObject();
            newLine1.put("PrintDataType", 0);
            newLine1.put("PrinterWidth", 32);
            newLine1.put("IsCenterAligned", true);
            newLine1.put("DataToPrint", " ");
            printData.put(newLine1);

            // QR Code line
            String strVehicleNo = vehicle_number + "##" + checkinTime;
            JSONObject printQR = new JSONObject();
            printQR.put("PrintDataType", 4);
            printQR.put("PrinterWidth", 45);
            printQR.put("IsCenterAligned", true);
            printQR.put("DataToPrint", strVehicleNo);
            printData.put(printQR);


                JSONObject newLine2 = new JSONObject();
                newLine2.put("PrintDataType", 0);
                newLine2.put("PrinterWidth", 32);
                newLine2.put("IsCenterAligned", true);
                newLine2.put("DataToPrint", " ");
                printData.put(newLine2);

            // Managed by line
            JSONObject companyName = new JSONObject();
            companyName.put("PrintDataType", 0);
            companyName.put("PrinterWidth", 32);
            companyName.put("IsCenterAligned", true);
            companyName.put("DataToPrint", "Managed by Rohini Enterprise");
            printData.put(companyName);

//            addEmptyLine.run();

            // Parking risk line
            JSONObject ownRisk = new JSONObject();
            ownRisk.put("PrintDataType", 0);
            ownRisk.put("PrinterWidth", 32);
            ownRisk.put("IsCenterAligned", true);
            ownRisk.put("DataToPrint", "Parking At Owner Risks");
            printData.put(ownRisk);

            JSONObject newLine3 = new JSONObject();
            newLine3.put("PrintDataType", 0);
            newLine3.put("PrinterWidth", 10);
            newLine3.put("IsCenterAligned", true);
            newLine3.put("DataToPrint", "\n\n");
            printData.put(newLine3);

            // Add array to detail
            detail.put("Data", printData);
            printRequest.put("Detail", detail);

            // Send request
            data.putString("MASTERAPPREQUEST", printRequest.toString());
            message.setData(data);
            message.replyTo = new Messenger(new VehicleOwnerRegistrationActivity.IncomingHandler()); // Handle response

            mService.send(message);

        } catch (JSONException | RemoteException e) {
            e.printStackTrace();
        }
    }




    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String response = bundle.getString("MASTERAPPRESPONSE");
            Log.i("PrintService", "Received response: " + response);

            try {
                // Handle the JSON response
                JSONObject responseObject = new JSONObject(response);
                JSONObject responseObj = responseObject.getJSONObject("Response");

                int responseCode = responseObj.getInt("ResponseCode");
                String responseMsg = responseObj.getString("ResponseMsg");

                if (responseCode==0) {
                   pae_change();
                    Log.i("PrintService", "Print successful: " + responseMsg);
                }
                else if (responseCode==1002){
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            VehicleOwnerRegistrationActivity.this).create();

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
                                printBillUsingPineLab(CheckinTime,vehicle_number);
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
                    final AlertDialog alertDialog = new AlertDialog.Builder(
                            VehicleOwnerRegistrationActivity.this).create();

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
